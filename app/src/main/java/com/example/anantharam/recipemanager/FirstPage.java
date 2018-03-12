package com.example.anantharam.recipemanager;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FirstPage extends AppCompatActivity {
    DatabaseHelper db;
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);
        db = new DatabaseHelper(this);

        new Handler(). postDelayed(new Runnable(){
            @Override
            public void run(){
                Boolean tablecheck = db.tablecheck();
                if (tablecheck==true){
                    Intent homeIntent = new Intent(FirstPage.this,choose.class);
                    startActivity(homeIntent);
                }
                else{
                    Intent homeIntent = new Intent(FirstPage.this,Register.class);
                    startActivity(homeIntent);
                }
                finish();
            }

        },SPLASH_TIME_OUT);

    }
}
