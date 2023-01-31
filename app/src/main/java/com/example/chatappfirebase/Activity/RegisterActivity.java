package com.example.chatappfirebase.Activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.chatappfirebase.Models.Users;
import com.example.chatappfirebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    EditText regName, regEmail, regPassword, regConPassword;
    Button regSubmit;
    TextView alreadyLogin;
    ProgressDialog progressDialog;
    CircleImageView profileImage;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri imageUri;
    String imageUrl;
    String regexPassword = ".{8,8}";

//    String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`" +
//            "!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        alreadyLogin = findViewById(R.id.already_login);
        regEmail = findViewById(R.id.reg_email);
        regName = findViewById(R.id.reg_name);
        regPassword = findViewById(R.id.reg_password);
        regConPassword = findViewById(R.id.reg_confirm_password);
        regSubmit = findViewById(R.id.reg_submit);
        profileImage = (CircleImageView) findViewById(R.id.profile_image);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);

        // Initializing Firebase instance
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        // using underline texts
        SpannableString already = new SpannableString("Already have an account? Login!");
        already.setSpan(new UnderlineSpan(), 25, already.length(), 0);
        alreadyLogin.setText(already);

        // using validation
        AwesomeValidation mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        mAwesomeValidation.addValidation(this, R.id.reg_email, Patterns.EMAIL_ADDRESS, R.string.err_email);
        mAwesomeValidation.addValidation(this, R.id.reg_name, RegexTemplate.NOT_EMPTY, R.string.err_name);

        mAwesomeValidation.addValidation(this, R.id.reg_password, regexPassword,
                R.string.err_password);
        mAwesomeValidation.addValidation(this, R.id.reg_confirm_password, R.id.reg_password,
                R.string.err_password_mismatch);

        mAwesomeValidation.addValidation(this, R.id.reg_password, RegexTemplate.NOT_EMPTY, R.string.error);
        mAwesomeValidation.addValidation(this, R.id.reg_confirm_password, RegexTemplate.NOT_EMPTY, R.string.error);
        mAwesomeValidation.addValidation(this, R.id.reg_email, RegexTemplate.NOT_EMPTY, R.string.error);

        alreadyLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login_Activity = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(login_Activity);
            }
        });
        regSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAwesomeValidation.validate()) {
                    //   if (regConPassword.equals(regPassword)) {
                    progressDialog.show();
                    String name = regName.getText().toString();
                    String email = regEmail.getText().toString();
                    String password = regPassword.getText().toString();
                    String confirmPassword = regConPassword.getText().toString();
                    String status = "Hey, there I'm using this Application!";

                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                DatabaseReference reference = database.getReference().child("user")
                                        .child(Objects.requireNonNull(auth.getUid()));
                                StorageReference storageReference = storage.getReference().child("upload")
                                        .child(auth.getUid());
                                if (imageUri != null) {
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageUrl = uri.toString();
                                                        Users user = new Users(auth.getUid(), name, email,
                                                                imageUrl, status);
                                                        reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(RegisterActivity.this, "Error in Creating User", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                } else {
                                    String status = "Hey, there I'm using this Application!";
                                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/chat-app-firebase-e531b.appspot.com/o/default.jpg?alt=media&token=b165b443-0524-4b07-808e-727c5170daf5";
                                    Users user = new Users(auth.getUid(), name, email,
                                            imageUrl, status);
                                    reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "Error in Creating User", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }


                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                selectImageLauncher.launch("image/*");

            }
        });

    }

    private final ActivityResultLauncher<String> selectImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        imageUri = uri;
                        profileImage.setImageURI(uri);
                    }
                }
            });
}