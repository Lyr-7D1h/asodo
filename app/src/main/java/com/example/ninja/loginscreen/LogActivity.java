package com.example.ninja.loginscreen;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ninja.MainActivity;
import com.example.ninja.R;
import com.example.ninja.httpRequests.AsodoRequester;
import com.example.ninja.httpRequests.CustomListener;
import com.example.ninja.util.ActivityUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LogActivity extends AppCompatActivity {

    private boolean awaitingResponse;
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
            }
        });

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButtonHandler(v);
            }
        });
    }

    public void loginButtonHandler(View v) {
        if (!awaitingResponse) {
            // Get variables
            String user = mTextUsername.getText().toString().trim();
            String password = mTextPassword.getText().toString().trim();

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
        }
    }

    public void loginResponseHandler(JsonObject response) {
        awaitingResponse = false;
        if (response.get("error") != null) {
            loginFailed();
            return;
        }

        loginSuccess(response);
    }

    public void loginSuccess(JsonObject response) {
        // Show toast
        Toast.makeText(LogActivity.this, "Login succesvol!", Toast.LENGTH_SHORT).show();

        // Store response to file
        //TODO

        // Move to home
        ActivityUtils.changeActivity(this, LogActivity.this, MainActivity.class);
    }

    public void loginFailed() {
        // Show toast
        Toast.makeText(LogActivity.this, "Ongeldige gebruikersnaam/wachtwoord!", Toast.LENGTH_SHORT).show();
    }

    public void showProgressingView() {
        if (!isProgressShowing) {
            View view = findViewById(R.id.progressBar1);
            view.bringToFront();
        }
    }
}

//  public void hideProgressingView() {
//   View v = this.findViewById(android.R.id.content).getRootView();
// ViewGroup viewGroup = (ViewGroup) v;
//  viewGroup.removeView(progressView);
//   isProgressShowing = false;
