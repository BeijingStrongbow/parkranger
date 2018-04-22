package com.github.beijingstrongbow.parkranger;

import android.app.ApplicationErrorReport;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateJoin extends AppCompatActivity {

    EditText nametxt;
    EditText groupidtxt;

    private LocationHandler locationHandler;
    private FirebaseHandler firebaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationHandler = LocationHandler.getInstance(this, (LocationManager) this.getSystemService(Context.LOCATION_SERVICE));
        firebaseHandler = FirebaseHandler.getInstance();
        setContentView(R.layout.activity_create_join);

        nametxt = (EditText) findViewById(R.id.nametxt);
        groupidtxt = (EditText) findViewById(R.id.groupidtxt);
        Button joinbtn = (Button) findViewById(R.id.joinbtn);
        Button createbtn = (Button) findViewById(R.id.createbtn);

        joinbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String name = nametxt.getText().toString();
                if (name.length()==0)
                    Toast.makeText(CreateJoin.this, "Invalid name", Toast.LENGTH_SHORT).show();
                else {
                    Integer groupid;
                    if (groupidtxt.getText().toString().length() == 4) {
                        groupid = Integer.parseInt(groupidtxt.getText().toString());
                        if (FirebaseHandler.addUserToGroup(groupid,LocationHandler.getLatitude(),LocationHandler.getLongitude(),name)) {
                            Toast.makeText(CreateJoin.this, "Joined Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CreateJoin.this, MapsActivity.class);
                            startActivity(intent);
                        }
                        else Toast.makeText(CreateJoin.this, "Group ID does not exist", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(CreateJoin.this, "Invalid group ID", Toast.LENGTH_SHORT).show();
                }
            }
        });

        createbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String name = nametxt.getText().toString();
                if (name.length()==0)
                    Toast.makeText(CreateJoin.this, "Invalid name", Toast.LENGTH_SHORT).show();
                else {
                    int groupid;
                    if (groupidtxt.getText().toString().length() == 4) {
                        groupid = Integer.parseInt(groupidtxt.getText().toString());
                        if (FirebaseHandler.addGroup(groupid,LocationHandler.getLatitude(),LocationHandler.getLongitude(),name)) {
                            Toast.makeText(CreateJoin.this, "Created Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CreateJoin.this, MapsActivity.class);
                            startActivity(intent);
                        }
                        else Toast.makeText(CreateJoin.this, "Group ID already exist", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(CreateJoin.this, "Invalid group ID", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


