package com.example.ninja.Domain.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.MalformedJsonException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CacheUtils {
    public static void cacheJsonObject(Context ctx, int writeMode, JsonObject jsonObject, String name) {
        FileOutputStream outputStream;

        try {
            outputStream = ctx.openFileOutput(name, writeMode);
            outputStream.write(jsonObject.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            File directory = ctx.getFilesDir();
            new File(directory, name);
        }
    }

    public static void cacheJsonObject(Context ctx, JsonObject jsonObject, String name) {
        cacheJsonObject(ctx, Context.MODE_APPEND, jsonObject, name);
    }

    public static JsonObject readCache(Context ctx, String name) throws MalformedJsonException {
        JsonObject res = null;

        try {
            // Init
            FileInputStream in = ctx.openFileInput(name);
            BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(in)));

            // Create JsonObject
//            System.out.println(br.readLine());
            res = new JsonParser().parse(br.readLine()).getAsJsonObject();

            // Clean up
            br.close();
            in.close();
        } catch (FileNotFoundException e) {
            // Do nothing
        } catch (IOException e) {
            // Do nothing
        }

        // Return
        return res;
    }

    public static void deleteCache(Context ctx, String name) {
        ctx.deleteFile(name);
    }
}
