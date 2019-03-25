package com.example.ninja.Domain.util;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class UserUtils {
    private static JsonObject getUserData(Context ctx) {
        return CacheUtils.readCache(ctx, "user.cache");
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
