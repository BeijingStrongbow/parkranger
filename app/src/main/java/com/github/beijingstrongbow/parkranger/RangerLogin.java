package com.github.beijingstrongbow.parkranger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RangerLogin extends AppCompatActivity {

    EditText usernametxt;
    EditText passwordtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranger_login);
        usernametxt = (EditText) findViewById(R.id.usernametxt);
        passwordtxt = (EditText) findViewById(R.id.passwordtxt);
        Button loginbtn = (Button) findViewById(R.id.loginbtn);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernametxt.getText().toString();
                String password = passwordtxt.getText().toString();
                if(FirebaseHandler.getInstance().checkLogin(username, password)) {
                    Toast.makeText(RangerLogin.this, "Login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RangerLogin.this, MapsActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(RangerLogin.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
