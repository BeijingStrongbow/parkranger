package com.github.beijingstrongbow.parkranger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SosMessage extends AppCompatActivity {

    EditText messagetxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_message);

        messagetxt = (EditText) findViewById(R.id.messagetxt);

        Button backbtn = (Button) findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SosMessage.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        Button sendbtn = (Button) findViewById(R.id.sendbtn);
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messagetxt.getText().toString();
                FirebaseHandler.getInstance().putSOS(LocationHandler.getInstance().getLatitude(), LocationHandler.getInstance().getLongitude(), message);
                Toast.makeText(SosMessage.this, "Send message successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SosMessage.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }
}
