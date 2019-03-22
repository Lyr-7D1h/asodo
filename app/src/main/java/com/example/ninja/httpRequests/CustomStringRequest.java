package com.example.ninja.httpRequests;

import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom StringRequest class which makes sure
 * the right Parameters are loaded for the request
 */
public class CustomStringRequest extends StringRequest {
    private String view;
    private JsonObject json;

    /**
     * Creates a new request with the given method.
     *
     * @param method        the request {@link Method} to use
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     * @param view          View indicating which view is requested from the API
     * @param json          JsonObject {@link JsonObject} containing the parameters for the request
     */
    public CustomStringRequest(int method, String url, Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener, String view, JsonObject json) {
        super(method, url, listener, errorListener);
        this.view = view;
        this.json = json;
    }

    /**
     * Returns a Map of parameters to be used for a POST or PUT request.
     *
     * @return Returns a Map of parameters to be used for a POST or PUT request.
     */
    @Override
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();

        params.put("view", view);
        params.put("json", json.toString());

        return params;
    }
}
