package com.example.ninja.httpRequests;

import com.android.volley.Response;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public abstract class CustomListener implements Response.Listener<String> {
    @Override
    public void onResponse(String response) {
        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
        this.onResponse(jsonObject);
    }

    abstract public void onResponse(JsonObject jsonResponse);
}
