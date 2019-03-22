package com.example.ninja.httpRequests;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;

import java.io.File;

public class Requester {
    private RequestQueue requestQueue;

    public Requester() {
        // Instantiate the cache
        File f = new File("cache");
        Cache cache = new DiskBasedCache(f, 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();
    }

    private void addRequestToQueue(StringRequest request) {
        this.requestQueue.add(request);
    }

    private static StringRequest createStringRequest(String view, JsonObject json, Response.Listener<String> responseListener) {
        String apiEndpoint = "http://api.asodo.nl/";
        return new CustomStringRequest(view, json, Request.Method.POST, apiEndpoint,
                responseListener, error -> System.err.println("F"));
    }

    public static void newRequest(String view, JsonObject json, Response.Listener<String> responseListener) {
        // Init RequestQueue
        Requester requester = new Requester();
        System.out.println(requester);

        // Formulate the request and handle the response.
        StringRequest stringRequest = createStringRequest(view, json, responseListener);
        System.out.println();

        // Add the request to the RequestQueue.
        requester.addRequestToQueue(stringRequest);
    }
}
