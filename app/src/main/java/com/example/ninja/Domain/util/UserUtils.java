package com.example.ninja.Domain.util;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.MalformedJsonException;

public class UserUtils {
    private static JsonObject getUserData(Context ctx) {
        try {
            return CacheUtils.readCache(ctx, "user.cache");
        } catch (MalformedJsonException e) {
            CacheUtils.deleteCache(ctx, "user.cache");
            return null;
        }
    }

    public static String getUserID(Context ctx) {
        JsonObject userData = UserUtils.getUserData(ctx);

        return userData.get("userID").getAsString();
    }

    public static String getFirstCarID(Context ctx) {
        JsonObject userData = UserUtils.getUserData(ctx);

        JsonArray ownedCars = userData.getAsJsonArray("ownedCars");
        if(ownedCars.size() == 0) {
            return "";
        }

        return ownedCars.get(0).getAsJsonObject().get("id").getAsString();
    }
}
