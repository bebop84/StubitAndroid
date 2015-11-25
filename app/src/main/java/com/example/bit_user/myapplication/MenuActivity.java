package com.example.bit_user.myapplication;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;


public class MenuActivity extends Activity implements  View.OnClickListener {
    ImageButton menu01;
    ImageButton menu02;
    ImageButton menu03;
    ImageButton menu04;
    ImageButton menu05;
    ImageButton menu06;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menu);
        menu01 =(ImageButton)findViewById(R.id.menu01);
        menu02 = (ImageButton)findViewById(R.id.menu02);
        menu03=(ImageButton)findViewById(R.id.menu03);
        menu04=(ImageButton)findViewById(R.id.menu04);
        menu05=(ImageButton)findViewById(R.id.menu05);
        menu06=(ImageButton)findViewById(R.id.menu06);

        menu01.setOnClickListener(this);
        menu02.setOnClickListener(this);
        menu03.setOnClickListener(this);
        menu04.setOnClickListener(this);
        menu05.setOnClickListener(this);
        menu06.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.menu01:
                Intent intent = new Intent(this,BoardActivity.class);
                startActivity(intent);
                break;
            case R.id.menu02:
                Intent intent2 = new Intent(this,checkActivity.class);
                startActivity(intent2);
                break;
            case R.id.menu03:
                Intent intent3 = new Intent(this,DataInesertActivity.class);
                startActivity(intent3);
                break;
            case R.id.menu04:
                Intent intent4 = new Intent(this,DataListActivity.class);
                startActivity(intent4);
                break;
            case R.id.menu05:
                Intent intent5 = new Intent(this,GCMPush.class);
                startActivity(intent5);
                break;
        }


    }


}