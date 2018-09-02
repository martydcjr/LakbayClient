package com.example.hp_pc.lakbayclient;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AccountSettingsActivity extends AppCompatActivity {

    MaterialEditText etfirstName, etlastName, etnumber;

    public FirebaseAuth nAuth;
    public DatabaseReference userdata;

    private String userID;

    private ImageView mProfileImage;

    private String firstName, lastName, number, birthDate, profileImageUrl;
    private String m;

    TextView etbirthDate;
    Calendar date;
    int day, month, year;

    private Uri resultUri;

    static final int REQUEST_TAKE_PHOTO  = 1;
    int SELECT_FILE = 0;
    final int REQUEST_CODE_GALLERY = 999;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProfileImage = findViewById(R.id.profileImage);

        etfirstName = findViewById(R.id.fname);
        etlastName = findViewById(R.id.lname);
        etnumber = findViewById(R.id.pnumber);
        etbirthDate = findViewById(R.id.bdate);
        date = Calendar.getInstance();
        day = date.get(Calendar.DAY_OF_MONTH);
        month = date.get(Calendar.MONTH);
        year = date.get(Calendar.YEAR);
        month = month+1;



        nAuth = FirebaseAuth.getInstance();
        userID = nAuth.getCurrentUser().getUid();
        userdata = FirebaseDatabase.getInstance().getReference().child("clients").child(userID);

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("Image/*");
//                startActivityForResult(intent, 1);

                SelectImage();


            }
        });

        getUserInfo();

        etbirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AccountSettingsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        if (monthOfYear == 1){
                            m = "January";
                        }
                        if (monthOfYear == 2){
                            m = "February";
                        }
                        if (monthOfYear == 3){
                            m = "March";
                        }
                        if (monthOfYear == 4){
                            m = "April";
                        }
                        if (monthOfYear == 5){
                            m = "May";
                        }
                        if (monthOfYear == 6){
                            m = "June";
                        }
                        if (monthOfYear == 7){
                            m = "July";
                        }
                        if (monthOfYear == 8){
                            m = "August";
                        }
                        if (monthOfYear == 9){
                            m = "September";
                        }
                        if (monthOfYear == 10){
                            m = "October";
                        }
                        if (monthOfYear == 11){
                            m = "November";
                        }
                        if (monthOfYear == 12){
                            m = "December";
                        }
                        etbirthDate.setText( m + " " + dayOfMonth + ", " +year);
                    }
                },year, month, day);
                datePickerDialog.show();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Account Information Saved", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                saveUserInformation();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void SelectImage() {
        final CharSequence[] items = {"Camera","Gallery","Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("SELECT IMAGE");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(items[i].equals("Camera")) {
                    dispatchTakePictureIntent();
                }
                else if(items[i].equals("Gallery")) {
                    RequestSelectImage();
                }
                else if(items[i].equals("Cancel")) {
                    dialogInterface.cancel();
                }
            }
        });
        builder.show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.hp_pc.lakbayclient",
                        photoFile);
                resultUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void RequestSelectImage() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            selectFromGallery();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    FancyToast.makeText(this, "PERMISSION REQUIRED!", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
            }
        }
    }

    private void selectFromGallery() {
        Intent select = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        select.setType("image/*");
        startActivityForResult(select.createChooser(select, "SELECT FILE"), SELECT_FILE);
    }
//    user_firstname
//    user_lastname
//    user_mobile
//    user_birthdate
//    firstName, lastName, number, birthDate

    private void getUserInfo(){
        userdata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("user_firstname") != null){
                        firstName = map.get("user_firstname").toString();
                        etfirstName.setText(firstName);
                    }
                    if (map.get("user_lastname") != null){
                        lastName = map.get("user_lastname").toString();
                        etlastName.setText(lastName);
                    }
                    if (map.get("user_mobile") != null){
                        number = map.get("user_mobile").toString();
                        etnumber.setText(number);
                    }
                    if (map.get("user_birthdate") != null){
                        birthDate = map.get("user_birthdate").toString();
                        etbirthDate.setText(birthDate);
                    }
                    if (map.get("profile_image_url") != null){
                        profileImageUrl = map.get("profile_image_url").toString();
                        Glide.with(getApplication()).load(profileImageUrl).into(mProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveUserInformation() {

        firstName = etfirstName.getText().toString();
        lastName = etlastName.getText().toString();
        number = etnumber.getText().toString();
        birthDate = etbirthDate.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("user_firstname", firstName);
        userInfo.put("user_lastname", lastName);
        userInfo.put("user_mobile", number);
        userInfo.put("user_birthdate", birthDate);
        userdata.updateChildren(userInfo);

        if (resultUri != null){
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20,baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);


            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                    finish();
//                    return;
                }
            });

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Map newImage = new HashMap();
                    newImage.put("profile_image_url", downloadUrl.toString());
                    userdata.updateChildren(newImage);

//                    finish();
//                    return;
                }
            });
        } else {
//            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int requestResult, Intent data) {
        super.onActivityResult(requestCode, requestResult, data);
//        if (requestCode == 1 && requestCode == Activity.RESULT_OK){
//            final Uri imageUri = data.getData();
//            resultUri = imageUri;
//            mProfileImage.setImageURI(resultUri);
//        }

        if (requestResult == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                mProfileImage.setImageURI(resultUri);
            }
            if (requestCode == SELECT_FILE) {
                final Uri imageUri = data.getData();
                resultUri = imageUri;
                mProfileImage.setImageURI(resultUri);
            }
        }
    }

}
