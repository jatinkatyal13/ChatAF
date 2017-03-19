package com.gmail.prototype1.chataf;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by Akshit on 3/10/2017.
**/

public class chat_main extends AppCompatActivity {
    private Toolbar toolbar;
    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("PLACEHOLDER");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


}
