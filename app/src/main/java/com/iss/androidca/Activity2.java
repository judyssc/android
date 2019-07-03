package com.iss.androidca;

import android.os.Bundle;
import android.widget.Chronometer;

import androidx.appcompat.app.AppCompatActivity;

public class Activity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2);

        Chronometer simpleChronometer = (Chronometer) findViewById(R.id.simpleChronometer);
        simpleChronometer.start();

    }
}

