package com.loopj.android.http;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created with IntelliJ IDEA.
 * User: nimast
 * Date: 10/9/12
 * Time: 1:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class WithHeadersJsonHttpResponseHandler extends WithHeadersAsyncHttpResponseHandler {

    /**
     * Fired when a request returns successfully and contains a json object
     * at the base of the response string. Override to handle in your
     * own code.
     * @param response the parsed json object found in the server response (if any)
     * @param headers the response headers of the HTTP response from the server
     */
    public void onSuccess(JSONObject response, Header[] headers) {
    }

    /**
     * Fired when a request returns successfully and contains a json array
     * at the base of the response string. Override to handle in your
     * own code.
     * @param response the parsed json array found in the server response (if any)
     * @param headers the response headers of the HTTP response from the server
     */
    public void onSuccess(JSONArray response, Header[] headers) {
    }

    /**
     * Fired when a request results in an error that is returned as a json object.
     * @param e the underlying cause of the failure
     * @param errorResponse the response body, if any, parsed as a json object.
     * @param headers the response headers of the HTTP response from the server
     */
    public void onFailure(Throwable e, JSONObject errorResponse, Header[] headers) {
    }

    /**
     * Fired when a request results in an error that is returned as a json array.
     * @param e the underlying cause of the failure
     * @param errorResponse the response body, if any, parsed as a json array.
     * @param headers the response headers of the HTTP response from the server
     */
    public void onFailure(Throwable e, JSONArray errorResponse, Header[] headers) {
    }

    @Override
    protected void handleSuccessWithHeadersMessage(String responseBody, Header[] headers) {
        super.handleSuccessMessage(responseBody);

        try {
            Object jsonResponse = parseResponse(responseBody);
            if (jsonResponse instanceof JSONObject) {
                onSuccess((JSONObject) jsonResponse, headers);
            } else if (jsonResponse instanceof JSONArray) {
                onSuccess((JSONArray) jsonResponse, headers);
            } else {
                throw new JSONException("Unexpected type " + jsonResponse.getClass().getName());
            }
        } catch (JSONException e) {
            onFailure(e, responseBody, headers);
        }
    }

    @Override
    protected void handleFailureWithHeadersMessage(Throwable e, String responseBody, Header[] headers) {
        if (responseBody != null) try {
            Object jsonResponse = parseResponse(responseBody);
            if (jsonResponse instanceof JSONObject) {
                onFailure(e, (JSONObject) jsonResponse, headers);
            } else if (jsonResponse instanceof JSONArray) {
                onFailure(e, (JSONArray) jsonResponse, headers);
            } else {
                onFailure(e, responseBody, headers);
            }
        } catch (JSONException ex) {
            onFailure(e, responseBody, headers);
        }
        else {
            onFailure(e, "", headers);
        }
    }

    protected Object parseResponse(String responseBody) throws JSONException {
        return new JSONTokener(responseBody).nextValue();
    }
}
