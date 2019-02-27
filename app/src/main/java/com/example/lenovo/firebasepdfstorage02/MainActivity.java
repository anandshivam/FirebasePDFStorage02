package com.example.lenovo.firebasepdfstorage02;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button selectFile, upload, fetch;
    EditText notification, fileTitle;
    Uri pdfUri; //LOCAL_STORAGE URL's
    FirebaseStorage storage;
    FirebaseDatabase database;
    ArrayList<String> urls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mapping resources
        selectFile = findViewById(R.id.selectFile);
        upload = findViewById(R.id.upload);
        notification = findViewById(R.id.notification);
        fetch= findViewById(R.id.fetchFiles);
        fileTitle=findViewById(R.id.filetitle);

        //getting object of FirebaseStorage
        storage= FirebaseStorage.getInstance();
        //getting object of FirebaseDatabse
        database= FirebaseDatabase.getInstance();

        //setting onClicklisteners
        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MyRecyclerViewActivity.class));
            }
        });

        selectFile.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
                {
                    selectPdf();
                }
                else
                {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 8);
                }
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pdfUri!=null){
                    uploadFile(pdfUri);
                }
            }
        });
    }

    private void uploadFile(Uri pdfUri) {
        //PROGRESS DIALOG
        final ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("UPLOADING FILE");
        progressDialog.setProgress(0);
        progressDialog.show();
        //setting string variables to be used at different places
        final String pdfname = fileTitle.getText().toString()+".pdf";
        final String pdfname1 = fileTitle.getText().toString()+System.currentTimeMillis()+"";
        //getting root path in FIREBASE
        final StorageReference reference= storage.getReference();
        //putting file in ROOT -> UPLOADs -> a folder with name="pdfname"
        reference.child("UPLOADS").child(pdfname).putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String url = reference.getDownloadUrl().toString();
                        //store url in realtime database
                        //returns path to the root of REALTIME_DATABASE
                        DatabaseReference reference1= database.getReference();
                        reference1.child(pdfname1).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(), "UPLOADED SUCCESSFULLY...", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "UPLOAD UNSUCCESSFUL...", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //displaying progress bar for tracking progress
                        //getting PROGRESS in realtime
                        int currentProgress = (int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        //setting progress in PROGRESS_DIALOG
                        progressDialog.setProgress(currentProgress);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==9 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
            selectPdf();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "PLEASE ENABLE PERMISSIONS, ignore: if done", Toast.LENGTH_LONG).show();
        }
    }

    private void selectPdf() {
        //user will be choosing the file through FILE_MANAGER using implicit Intent
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 78);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==78 && resultCode==RESULT_OK && data!=null){
            //we will be fetching URI for our selected file
            pdfUri= data.getData();//-> returns uri of selected file
            notification.setText(new StringBuilder().append("CHOSEN FILE : ").append(data.getData().getLastPathSegment()).toString());
        }
        else{
            Toast.makeText(getApplicationContext(), "KINDLY SELECT A FILE", Toast.LENGTH_LONG).show();
        }
    }
}
