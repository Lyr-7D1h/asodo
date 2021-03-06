package com.example.ninja.Domain.httpRequests;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ninja.Domain.util.AlertUtils;
import com.example.ninja.R;
import com.google.gson.JsonObject;

import java.io.File;

/**
 * Class used to make requests to our own Asodo API
 */
public class AsodoRequester {
    private RequestQueue requestQueue;

    /**
     * Constructor for Asodo Class
     * Instantiates an Volley RequestQueue
     */
    private AsodoRequester(Context ctx) {
        // Instantiate the RequestQueue with the cache and network.
        requestQueue = Volley.newRequestQueue(ctx);

        // Start the queue
        requestQueue.start();
    }

    /**
     * Add a StringRequest to the RequestQueue object
     *
     * @param request the StringRequest to add
     */
    private void addRequestToQueue(StringRequest request) {
        this.requestQueue.add(request);
    }

    /**
     * Creates a StringRequest based on the parameters
     *
     * @param view Indicates the view which is requested from the server
     * @param json Contains the parameters that are required for the view
     * @param responseListener A listener which is fired whenever we get a response from the api
     * @ Returns a StringRequest object according to the given parameters
     */
    public static StringRequest createStringRequest(String view, JsonObject json, Activity context, Response.Listener<String> responseListener) {
        String apiEndpoint = "http://api.asodo.nl/";
        return new CustomStringRequest(Request.Method.POST, apiEndpoint,
                responseListener, error -> {
            // Show alert
            AlertUtils.showAlert(context.getString(R.string.retry), context.getString(R.string.cancel),
                    context.getString(R.string.no_internet), context, (dialog, which) -> {
                // New request
                AsodoRequester.newRequest(view, json, context, responseListener);
            });
        }, view, json);
    }

    /**
     * Creates a StringRequest based on the parameters
     *
     * @param view Indicates the view which is requested from the server
     * @param json Contains the parameters that are required for the view
     * @param responseListener A listener which is fired whenever we get a response from the api
     * @ Returns a StringRequest object according to the given parameters
     */
    public static StringRequest createStringRequest(String view, JsonObject json, Response.Listener<String> responseListener) {
        String apiEndpoint = "http://api.asodo.nl/";
        return new CustomStringRequest(Request.Method.POST, apiEndpoint,
                responseListener, error -> {
            // Do nothing
        }, view, json);
    }

    /**
     * Static method used to create an api call which is fired at the Asodo API
     *
     * @param view Indicates the view which is requested from the server
     * @param json Contains the parameters that are required for the view
     * @param responseListener A listener which is fired whenever we get a response from the api
     */
    public static void newRequest(String view, JsonObject json, Activity context, Response.Listener<String> responseListener) {
        // Init RequestQueue
        AsodoRequester asodoRequester = new AsodoRequester(context);

        // Formulate the request and handle the response.
        StringRequest stringRequest = createStringRequest(view, json, context, responseListener);

        // Add the request to the RequestQueue.
        asodoRequester.addRequestToQueue(stringRequest);
    }


    /**
     * Static method used to create an api call which is fired at the Asodo API,
     * without connectivity check
     *
     * @param view Indicates the view which is requested from the server
     * @param json Contains the parameters that are required for the view
     * @param responseListener A listener which is fired whenever we get a response from the api
     */
    public static void newRequest(String view, JsonObject json, Context context, Response.Listener<String> responseListener) {
        // Init RequestQueue
        AsodoRequester asodoRequester = new AsodoRequester(context);

        // Formulate the request and handle the response.
        StringRequest stringRequest = createStringRequest(view, json, responseListener);

        // Add the request to the RequestQueue.
        asodoRequester.addRequestToQueue(stringRequest);
    }
}
