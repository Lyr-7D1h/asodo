package com.example.ninja.Domain.util;

import android.content.Context;

import com.google.gson.JsonObject;

public class UserUtils {
    private static JsonObject getUserData(Context ctx) {
        return CacheUtils.readCache(ctx, "user.cache");
    }

    public static String getUserID(Context ctx) {
        JsonObject userData = UserUtils.getUserData(ctx);

        return userData.get("userID").toString();
    }
}
