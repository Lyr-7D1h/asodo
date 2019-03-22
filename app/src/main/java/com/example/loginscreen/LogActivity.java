package com.example.loginscreen;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LogActivity extends AppCompatActivity {

    EditText mTextUsername;
    EditText mTextPassword;
    Button mButtonLogin;
    TextView mTextViewRegister;
    DatabaseHelp db;
   // ViewGroup progressView;
    protected boolean isProgressShowing = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        db = new DatabaseHelp(this);
        mTextUsername = (EditText)findViewById(R.id.edittext_username);
        mTextPassword = (EditText)findViewById(R.id.edittext_password);
        mButtonLogin = (Button)findViewById(R.id.button_login);
        mTextViewRegister = (TextView) findViewById(R.id.textview_register);
        mTextViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LogActivity.this, RegActivity.class);
                startActivity(registerIntent);
            }


        });
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = mTextUsername.getText().toString().trim();
                String pass = mTextPassword.getText().toString().trim();
                Boolean res =  db.checkUser(user, pass);
                if(res == true){

                    Intent moveToHome = new Intent(LogActivity.this,HomeActivity.class);
                    startActivity(moveToHome);
                }
                else{
                    Toast.makeText(LogActivity.this, "Error bij Login!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
