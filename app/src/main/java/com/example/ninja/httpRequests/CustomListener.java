package com.example.ninja.httpRequests;

import com.android.volley.Response;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Custom Listener<String> class which makes sure that
 * the incoming response is returned in JsonObject format
 */
public abstract class CustomListener implements Response.Listener<String> {

    /**
     * Called when a response is received and converts response to a JsonObject.
     *
     * @param response
     */
    @Override
    public void onResponse(String response) {
        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
        this.onResponse(jsonObject);
    }

    /**
     * Called when a response is received.
     *
     * @param jsonResponse
     */
    abstract public void onResponse(JsonObject jsonResponse);
}
