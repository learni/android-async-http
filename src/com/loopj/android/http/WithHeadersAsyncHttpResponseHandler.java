package com.loopj.android.http;

import android.os.Message;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.logging.Handler;

/**
 * Created with IntelliJ IDEA.
 * User: nimast
 * Date: 10/9/12
 * Time: 1:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class WithHeadersAsyncHttpResponseHandler extends AsyncHttpResponseHandler {

    protected static final int SUCCESS_WITH_HEADERS_MESSAGE = 4;
    protected static final int FAILURE_WITH_HEADERS_MESSAGE = 5;

    /**
     * Fired when a request returns successfully, override to handle in your own code
     *
     * @param content the body of the HTTP response from the server
     * @param headers the response headers of the HTTP response from the server
     */
    public void onSuccess(String content, Header[] headers) {
    }

    /**
     * Fired when a request fails to complete, override to handle in your own code
     *
     * @param error   the underlying cause of the failure
     * @param content the response body, if any
     * @param headers the response headers of the HTTP response from the server
     */
    public void onFailure(Throwable error, String content, Header[] headers) {
    }

    @Override
    protected void handleMessage(Message message) {
        Object[] response;
        switch (message.what) {
            case SUCCESS_WITH_HEADERS_MESSAGE:
                response = (Object[]) message.obj;
                handleSuccessWithHeadersMessage((String) response[0], (Header[]) response[1]);
                break;
            case FAILURE_WITH_HEADERS_MESSAGE:
                response = (Object[]) message.obj;
                handleFailureWithHeadersMessage((Throwable) response[0], (String) response[1], (Header[]) response[2]);
                break;
            default:
                super.handleMessage(message);
        }
    }

    protected void handleSuccessWithHeadersMessage(String responseBody, Header[] headers) {
        onSuccess(responseBody, headers);
    }

    protected void handleFailureWithHeadersMessage(Throwable throwable, String responseBody, Header[] headers) {
        onFailure(throwable, responseBody, headers);
    }

    @Override
    protected void sendResponseMessage(HttpResponse response) {
        StatusLine status = response.getStatusLine();
        Header[] headers = null;
        String responseBody = null;

        try {
            headers = response.getAllHeaders();
            HttpEntity entity = null;
            HttpEntity temp = response.getEntity();
            if (temp != null) {
                entity = new BufferedHttpEntity(temp);
                responseBody = EntityUtils.toString(entity, "UTF-8");

            }
        } catch (IOException e) {
            sendFailureMessage(e, (String) null, headers);
        }

        if (status.getStatusCode() >= 300) {
            sendFailureMessage(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()), responseBody, headers);
        } else {
            sendSuccessMessage(responseBody, headers);
        }
    }

    protected void sendSuccessMessage(String responseBody, Header[] headers) {
        sendMessage(obtainMessage(SUCCESS_WITH_HEADERS_MESSAGE, new Object[]{responseBody, headers}));
    }

    protected void sendFailureMessage(Throwable e, String responseBody, Header[] headers) {
        sendMessage(obtainMessage(FAILURE_WITH_HEADERS_MESSAGE, new Object[]{e, responseBody, headers}));
    }
}
