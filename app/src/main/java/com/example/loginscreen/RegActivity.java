package com.example.loginscreen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegActivity extends AppCompatActivity {
    EditText mTextUsername;
    EditText mTextPassword;
    Button mButtonRegistreer;
    TextView mTextViewRegister;
    EditText mTextKenteken;
    EditText mTextCnfPassword;
    DatabaseHelp db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        db = new DatabaseHelp(this);
        mTextUsername = (EditText)findViewById(R.id.edittext_username);
        mTextPassword = (EditText)findViewById(R.id.edittext_password);
        mTextKenteken = (EditText) findViewById(R.id.edittext_Kenteken);
        mTextCnfPassword = (EditText) findViewById(R.id.edittext_cnf_password);
        mButtonRegistreer = (Button)findViewById(R.id.button_login);
        mTextViewRegister = (TextView) findViewById(R.id.textview_register);
        mTextViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(RegActivity.this, LogActivity.class);
                startActivity(registerIntent);
            }
        });
        mButtonRegistreer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user = mTextUsername.getText().toString().trim();oi
                String password = mTextPassword.getText().toString().trim();
                String cnf_pwd = mTextCnfPassword.getText().toString().trim();
                if(password.equals(cnf_pwd)){
                    long val = db.addUser(user,password);
                    if(val > 0){
                        Toast.makeText(RegActivity.this,"Je bent Geregistreerd",Toast.LENGTH_SHORT).show();
                        Intent moveToLogin = new Intent(RegActivity.this,LogActivity.class);
                        startActivity(moveToLogin);
                    }
                    else{
                        Toast.makeText(RegActivity.this,"Registreer error",Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    Toast.makeText(RegActivity.this,"Passwords komen niet overeen",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
