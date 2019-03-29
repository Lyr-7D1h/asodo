package com.example.ninja.Domain.httpRequests;

import com.google.gson.JsonObject;

public abstract class AsodoRequesterCallback {
    abstract public void callback(JsonObject jsonResponse);
}
