package com.example.ninja.Controllers.loginscreen;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ninja.R;
import com.example.ninja.Domain.httpRequests.AsodoRequester;
import com.example.ninja.Domain.httpRequests.CustomListener;
import com.example.ninja.Domain.util.ActivityUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RegActivity extends AppCompatActivity {

    private boolean awaitingResponse;
    EditText mTextUsername;
    EditText mTextPassword;
    Button mButtonRegistreer;
    TextView mTextViewRegister;
    EditText mTextKenteken;
    EditText mTextCnfPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        mTextUsername = (EditText) findViewById(R.id.edittext_username);
        mTextPassword = (EditText) findViewById(R.id.edittext_password);
        mTextKenteken = (EditText) findViewById(R.id.edittext_Kenteken);
        mTextCnfPassword = (EditText) findViewById(R.id.edittext_cnf_password);
        mButtonRegistreer = (Button) findViewById(R.id.button_login);

        Activity self = this;
        mTextViewRegister = (TextView) findViewById(R.id.textview_register);
        mTextViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtils.changeActivity(self, RegActivity.this, LogActivity.class);
            }
        });

        mButtonRegistreer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerButtonHandler(v);
            }
        });
    }

    /**
     * Handler which handles interaction with the register button.
     *
     * @param v
     */
    public void registerButtonHandler(View v) {
        if (!awaitingResponse) {
            // Get variables
            String user = mTextUsername.getText().toString().trim();
            String password = mTextPassword.getText().toString().trim();
            String cnf_pwd = mTextCnfPassword.getText().toString().trim();
            String licensePlate = mTextKenteken.getText().toString().trim();

            // Only proceed if values are filled in
            if (user.isEmpty() || password.isEmpty() || cnf_pwd.isEmpty() || licensePlate.isEmpty()) {
                return;
            }

            // Check if two passwords match
            if (!password.equals(cnf_pwd)) {
                registerFailed("Wachtwoorden komen niet overeen!");
                return;
            }

            // Update status
            awaitingResponse = true;

            // Make request
            String jsonString = "{\"username\":\"" + user + "\",\"password\":\"" + password +
                    "\",\"licensePlate\":\"" + licensePlate + "\"}";
            JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();

            AsodoRequester.newRequest("register", json, RegActivity.this, new CustomListener() {
                @Override
                public void onResponse(JsonObject jsonResponse) {
                    registerResponseHandler(jsonResponse);
                }
            });
        }
    }

    /**
     * Called when the application receives a response from the API.
     *
     * @param response The response from the API.
     */
    public void registerResponseHandler(JsonObject response) {
        awaitingResponse = false;
        System.out.println(response);
        if (response.get("error") != null) {
            registerFailed("Gebruikersnaam al in gebruik!");
            return;
        }

        registerSuccess();
    }

    /**
     * Called whenever a registration succeeds.
     */
    public void registerSuccess() {
        // Show toast
        Toast.makeText(RegActivity.this, "Succesvol geregistreerd!", Toast.LENGTH_SHORT).show();

        // Move to login
        ActivityUtils.changeActivity(this, RegActivity.this, LogActivity.class);
    }

    /**
     * Called whenever a registration fails.
     *
     * @param reason The reason the  registration has failed.
     */
    public void registerFailed(String reason) {
        // Show toast
        Toast.makeText(RegActivity.this, reason, Toast.LENGTH_SHORT).show();
    }
}
