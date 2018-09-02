package com.example.hp_pc.lakbayclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    ListView lvpmethods;
    List wew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvpmethods = findViewById(R.id.lvpmethods);

        String[] txtStrings = {"Cash"};
//        int[] icons = {R.drawable.};
//        wew.add("Cash");


//        lvpmethods.setAdapter(adapter);
    }

}
