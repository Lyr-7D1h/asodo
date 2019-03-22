package com.example.ninja.httpRequests;

import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class CustomStringRequest extends StringRequest {
    private String view;
    private JsonObject json;

    public CustomStringRequest(String view, JsonObject json, int method, String url, Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);

        this.view = view;
        this.json = json;
    }

    public CustomStringRequest(String view, JsonObject json, String url, Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener) {
        super(url, listener, errorListener);

        this.view = view;
        this.json = json;
        this.getErrorListener();
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();

        params.put("view", view);
        params.put("json", json.toString());

        return params;
    }
}
