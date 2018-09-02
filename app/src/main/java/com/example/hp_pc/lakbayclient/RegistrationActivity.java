package com.example.hp_pc.lakbayclient;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    public Button submit;
    public EditText fname, lname, number;
    TextView bdate;
    Calendar date;
    int day, month, year;


    public FirebaseAuth nAuth;
    public DatabaseReference userdata;
    public FirebaseAuth.AuthStateListener firebaseAuthListener;

    private ProgressDialog progressDialog, dialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        progressDialog = new ProgressDialog(this);
        dialog2 = new ProgressDialog(this);

        submit = findViewById(R.id.btn_submit);
        fname = findViewById(R.id.etfname);
        lname = findViewById(R.id.etlname);
        number = findViewById(R.id.mnumber);

        bdate = findViewById(R.id.bdate);
        date = Calendar.getInstance();

        day = date.get(Calendar.DAY_OF_MONTH);
        month = date.get(Calendar.MONTH);
        year = date.get(Calendar.YEAR);

        month = month+1;

        userdata = FirebaseDatabase.getInstance().getReference();

        nAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
//                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
//                    startActivity(intent);
//                    finish();
//                    return;
                }
            }
        };


//        bdate.setText(day+"/"+month+"/"+year);

        bdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegistrationActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        bdate.setText( monthOfYear + "-" + dayOfMonth + "-" +  year);
                    }
                },year, month, day);
                datePickerDialog.show();
            }
        });



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Map<String, String> userinfo = new HashMap<String, String>();

                final String firstname = fname.getText().toString();
                final String lastname = lname.getText().toString();
                final String mobilenumber = number.getText().toString();
                final String birthdate = bdate.getText().toString();

                if(TextUtils.isEmpty(firstname) && TextUtils.isEmpty(lastname) && TextUtils.isEmpty(mobilenumber)){
                        FancyToast.makeText(getApplicationContext(),"Enter Your Information",FancyToast.LENGTH_SHORT,FancyToast.WARNING,false).show();
                    return;
                }

                if (TextUtils.isEmpty(firstname)) {
                    FancyToast.makeText(getApplicationContext(),"Enter Your First Name.",FancyToast.LENGTH_SHORT,FancyToast.WARNING,false).show();
                    return;
                }

                if (TextUtils.isEmpty(lastname)) {
                    FancyToast.makeText(getApplicationContext(),"Enter Your Last Name.",FancyToast.LENGTH_SHORT,FancyToast.WARNING,false).show();
                    return;
                }

                if (TextUtils.isEmpty(mobilenumber)) {
                    FancyToast.makeText(getApplicationContext(),"Enter Your Mobile Number.",FancyToast.LENGTH_SHORT,FancyToast.WARNING,false).show();
                    return;
                }
                if (TextUtils.isEmpty(birthdate)) {
                    FancyToast.makeText(getApplicationContext(),"Enter Your Birthdate",FancyToast.LENGTH_SHORT,FancyToast.WARNING,false).show();
                    return;
                }
                else {
                    progressDialog.setMessage("Saving your account details");
                    progressDialog.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            userinfo.put("is_user", "true");
                            userinfo.put("user_firstname", firstname);
                            userinfo.put("user_lastname", lastname);
                            userinfo.put("user_mobile", mobilenumber);
                            userinfo.put("user_birthdate", birthdate);

                            String user_id = nAuth.getCurrentUser().getUid();
                            userdata.child("clients").child(user_id).setValue(userinfo);
                            progressDialog.dismiss();

                            verify();
                        }
                    }, 3000);

                }
            }
        });


    }

    private void verify() {
        dialog2.setMessage("Please verify your email");
        dialog2.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog2.dismiss();
                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }, 3000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        nAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        nAuth.removeAuthStateListener(firebaseAuthListener);
    }
}
