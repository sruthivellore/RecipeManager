package com.example.anantharam.recipemanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Register extends AppCompatActivity {
    DatabaseHelper db;
    EditText e1,e2;
    Button bt2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);
        bt2 = (Button) findViewById(R.id.bt2);
        e1 = findViewById(R.id.editText);
        e2 = findViewById(R.id.editText2);

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s1 = e1.getText().toString();
                String s2 = e2.getText().toString();
                if(s1.equals("")||s2.equals("")){
                    Toast.makeText(getApplicationContext(),"Fields are empty",Toast.LENGTH_SHORT).show();
                }else{
                    Boolean chkemail = db.chkemail(s1);
                    if (chkemail==true){
                        Boolean insert = db.insert(s1,s2);
                        if(insert == true){
                            Intent i1 = new Intent(Register.this, choose.class);
                            startActivity(i1);
                        }
                    }

                }

            }
        });
    }

}
