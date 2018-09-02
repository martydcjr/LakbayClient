package com.example.hp_pc.lakbayclient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    public EditText cEmail, cPassword, cPassword2;
    public String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public TextView register, error;

    public FirebaseAuth nAuth;
    public FirebaseAuth.AuthStateListener firebaseAuthListener;

    private ProgressDialog progressDialog;
    private CheckBox sPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        progressDialog = new ProgressDialog(this);

        nAuth = FirebaseAuth.getInstance();

        cEmail =findViewById(R.id.clientEmail);
        cPassword =findViewById(R.id.clientPass);
        cPassword2 =findViewById(R.id.clientPass2);
        register = findViewById(R.id.btn_register);
        error = findViewById(R.id.passError);

        sPass = findViewById(R.id.showPass);


        sPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    // show password
                    cPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    cPassword2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    cPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    cPassword2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
////                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
////                    startActivity(intent);
////                    finish();
//                    return;
                }
            }
        };

        register.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                final String email = cEmail.getText().toString();
                final String password = cPassword.getText().toString();
                final String password2 = cPassword2.getText().toString();

                if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
                    FancyToast.makeText(getApplicationContext(),"Enter Email and Password",FancyToast.LENGTH_SHORT,FancyToast.WARNING,false).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    FancyToast.makeText(getApplicationContext(),"Enter Email Address",FancyToast.LENGTH_SHORT,FancyToast.WARNING,false).show();
                    return;
                }

                if (!email.matches(emailPattern) && email.length() > 0) {
                    FancyToast.makeText(getApplicationContext(),"Invalid Email Address",FancyToast.LENGTH_SHORT,FancyToast.WARNING,false).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    FancyToast.makeText(getApplicationContext(),"Enter Password",FancyToast.LENGTH_SHORT,FancyToast.WARNING,false).show();
                    return;
                }

                if (password.length() < 8) {
                    FancyToast.makeText(getApplicationContext(),"Password must be 8 characters",FancyToast.LENGTH_SHORT,FancyToast.WARNING,false).show();
                    return;
                }

                if (!password.equals(password2) && password.length() > 7){
                    FancyToast.makeText(getApplicationContext(),"Password Mismatch!",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                    return;
                }

                if (!isValidPassword(password.trim())) {
                    error.setVisibility(View.VISIBLE);
                    error.setText("Invalid Password!" + "\n"
                            + "• At least 8 characters" + "\n"
                            + "• At least 1 digit" + "\n"
                            + "• At least 1 Uppercase" + "\n"
                            + "• At least 1 Lowercase");
                }




                else {
                    nAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                FancyToast.makeText(getApplicationContext(),"Sign Up Error!",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                            progressDialog.setMessage("Registration Error!");
                            progressDialog.show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                }
                            }, 2000);

                            }else{
                                //sendverification email
                                sendVerificationEmail();

                                progressDialog.setMessage("Registering Your Account");
                                progressDialog.show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                    }
                                }, 4000);

                                String user_id = nAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("clients").child(user_id);
                                current_user_db.setValue(true);
                                Intent intent = new Intent(SignUpActivity.this, RegistrationActivity.class);
                                startActivity(intent);

                            }
                        }
                    });
                }
            }
        });
    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }


    public void sendVerificationEmail(){
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    if (user!=null){
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                        }else {
                            FancyToast.makeText(getApplicationContext(),"Couldn't Send Email Verification",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                        }
                    }
                });
        }
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
