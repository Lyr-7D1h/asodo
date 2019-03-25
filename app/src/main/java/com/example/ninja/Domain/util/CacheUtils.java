package com.example.ninja.Domain.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class CacheUtils {
    public static void cacheObject(Context ctx, Object object, String name){
        final GsonBuilder gbuilder = new GsonBuilder();
        final Gson gson = gbuilder.create();

        String json = gson.toJson(object);
        System.out.println(json); // TODO remove

        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        cacheJsonObject(ctx, jsonObject, name);
    }



    public static void cacheJsonObject(Context ctx, JsonObject jsonObject, String name) {
        FileOutputStream outputStream;

        try {
            outputStream = ctx.openFileOutput(name, Context.MODE_APPEND);
            outputStream.write(jsonObject.toString().getBytes());

            FileInputStream in = ctx.openFileInput("trips.list");
            BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(in)));
            System.out.println(br.readLine());

        } catch (Exception e) {
            File directory = ctx.getFilesDir();
            new File(directory, name);
        }
    }

    public static void deleteCache(Context ctx) {
        ctx.deleteFile("tips.list");
    }
}
