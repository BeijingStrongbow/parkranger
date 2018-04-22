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
        System.out.println("test2");
        super.onCreate(savedInstanceState);
        locationHandler = LocationHandler.getInstance(this, (LocationManager) this.getSystemService(Context.LOCATION_SERVICE));
        firebaseHandler = FirebaseHandler.getInstance();
        firebaseHandler.getGroups();
        setContentView(R.layout.activity_create_join);

        nametxt = (EditText) findViewById(R.id.nametxt);
        groupidtxt = (EditText) findViewById(R.id.groupidtxt);
        Button joinbtn = (Button) findViewById(R.id.joinbtn);
        Button createbtn = (Button) findViewById(R.id.createbtn);
        System.out.println("test3");
        joinbtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                System.out.println("test4");
                String name = nametxt.getText().toString();
                if (name.length()==0)
                    Toast.makeText(CreateJoin.this, "Invalid name", Toast.LENGTH_SHORT).show();
                else {
                    Integer groupid;
                    if (groupidtxt.getText().toString().length() == 4) {
                        groupid = Integer.parseInt(groupidtxt.getText().toString());
                        if(firebaseHandler.addUserToGroup(groupid, locationHandler.getLatitude(), locationHandler.getLongitude(),name)) {
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
                System.out.println("test5");
                String name = nametxt.getText().toString();
                if (name.length()==0)
                    Toast.makeText(CreateJoin.this, "Invalid name", Toast.LENGTH_SHORT).show();
                else {
                    int groupid;
                    if (groupidtxt.getText().toString().length() == 4) {
                        groupid = Integer.parseInt(groupidtxt.getText().toString());
                        System.out.println("test1");
                        System.out.println(locationHandler.getLatitude() + " " + locationHandler.getLongitude());

                        if (firebaseHandler.addGroup(groupid, locationHandler.getLatitude(), locationHandler.getLongitude(), name)) {
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


