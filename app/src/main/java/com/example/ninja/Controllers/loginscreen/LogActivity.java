package com.example.ninja.Controllers.loginscreen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ninja.Controllers.AhNiffo;
import com.example.ninja.Controllers.MainActivity;
import com.example.ninja.Domain.Global;
import com.example.ninja.Domain.util.CacheUtils;
import com.example.ninja.Domain.util.LocaleUtils;
import com.example.ninja.R;
import com.example.ninja.Domain.httpRequests.AsodoRequester;
import com.example.ninja.Domain.httpRequests.CustomListener;
import com.example.ninja.Domain.util.ActivityUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.MalformedJsonException;

public class LogActivity extends AppCompatActivity {

    private boolean awaitingResponse;
    private final Context context = this;
    EditText mTextUsername;
    EditText mTextPassword;
    Button mButtonLogin;
    TextView mTextViewRegister;

    // ViewGroup progressView;
    protected boolean isProgressShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        checkLanguageSet();

        // Check if user is logged in
        try {
            if(CacheUtils.readCache(context, "user.cache") != null) {
                if(((Global) this.getApplication()).isActiveTrip()) {
                    Intent intent = new Intent(LogActivity.this, MainActivity.class);
                    intent.putExtra("redirect", 1);
                    startActivity(intent);
                } else {
                    ActivityUtils.changeActivity(this, LogActivity.this, MainActivity.class);
                }

                finish();
            }
        } catch (MalformedJsonException e) {
            CacheUtils.deleteCache(context, "user.cache");
        }

        awaitingResponse = false;

        mTextUsername = (EditText) findViewById(R.id.edittext_username);
        mTextPassword = (EditText) findViewById(R.id.edittext_password);
        mButtonLogin = (Button) findViewById(R.id.button_login);

        Activity self = this;
        mTextViewRegister = (TextView) findViewById(R.id.textview_register);
        mTextViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtils.changeActivity(self, LogActivity.this, RegActivity.class);
                finish();
                overridePendingTransition(0,0);
            }
        });

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButtonHandler(v);
            }
        });
    }

    /**
     * Handler which handles interaction with the register button.
     *
     * @param v
     */
    public void loginButtonHandler(View v) {
        if (!awaitingResponse) {
            // Get variables
            String user = mTextUsername.getText().toString().trim();
            String password = mTextPassword.getText().toString().trim();
            if(!(user.matches("ahniffo") && password.matches("huts"))) {
                // Only proceed if values are filled in
                if (user.isEmpty() || password.isEmpty()) {
                    return;
                }

                // Update status
                awaitingResponse = true;

                // Make request
                String jsonString = "{\"username\":\"" + user + "\",\"password\":\"" + password + "\"}";
                JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();

                AsodoRequester.newRequest("authenticate", json, LogActivity.this, new CustomListener() {
                    @Override
                    public void onResponse(JsonObject jsonResponse) {
                        loginResponseHandler(jsonResponse);
                    }
                });
            } else {
                ActivityUtils.changeActivity(this, LogActivity.this, AhNiffo.class);
            }

        }
    }

    /**
     * Called when the application receives a response from the API.
     *
     * @param response The response from the API.
     */
    public void loginResponseHandler(JsonObject response) {
        awaitingResponse = false;
        if (response.get("error") != null) {
            loginFailed();
            return;
        }

        loginSuccess(response);
    }

    /**
     * Called whenever a login attempt succeeds.
     *
     * @param response The userData to save to file
     */
    public void loginSuccess(JsonObject response) {
        // Show toast
        Toast.makeText(LogActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();

        // Store response to file
        CacheUtils.cacheJsonObject(context, 0, response, "user.cache");

        // Sync routes
        ((Global) this.getApplication()).sync();

        // Move to home
        ActivityUtils.changeActivity(this, LogActivity.this, MainActivity.class);
        finish();
    }

    /**
     * Called whenever a login attempt fails.
     */
    public void loginFailed() {
        // Show toast
        Toast.makeText(LogActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
    }

    public void checkLanguageSet() {
        if(!((Global) getApplication()).isLanguageSet()) {
            // Set locale
            LocaleUtils.setLocale(this);
            ((Global) getApplication()).setLanguageSet(true);

            //It is required to recreate the activity to reflect the change in UI.
            ActivityUtils.changeActivity(this, LogActivity.this, LogActivity.class);
            finish();
            overridePendingTransition(0,0);
        }
    }
}
