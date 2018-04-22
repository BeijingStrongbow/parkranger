package com.github.beijingstrongbow.parkranger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateJoin extends AppCompatActivity {

    EditText nametxt;
    EditText groupidtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_join);

        nametxt = (EditText) findViewById(R.id.nametxt);
        groupidtxt = (EditText) findViewById(R.id.groupidtxt);
        Button joinbtn = (Button) findViewById(R.id.joinbtn);
        Button createbtn = (Button) findViewById(R.id.createbtn);

        joinbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Double groupid = Double.parseDouble(groupidtxt.getText().toString());

            }
        });

        createbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String name = nametxt.getText().toString();
                Double groupid = Double.parseDouble(groupidtxt.getText().toString());

            }
        });
    }
}
