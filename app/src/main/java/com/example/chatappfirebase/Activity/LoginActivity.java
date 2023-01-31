package com.example.chatappfirebase.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.PasswordTransformationMethod;
import android.text.style.UnderlineSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.chatappfirebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    TextView forgotPassword;
    TextView signUp, sign_report;
    EditText email, password;
    Button login;
    FirebaseAuth auth;
    ProgressDialog progressDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        signUp = (TextView) findViewById(R.id.sign_up);
        sign_report = (TextView) findViewById(R.id.signin_report);
        email = (EditText) findViewById(R.id.enter_email);
        login = findViewById(R.id.button_login);
        password = (EditText) findViewById(R.id.enter_password);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();

        Drawable drawable = password.getCompoundDrawables()[2];

        AwesomeValidation mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        mAwesomeValidation.addValidation(this, R.id.enter_password, RegexTemplate.NOT_EMPTY, R.string.error);
        mAwesomeValidation.addValidation(this, R.id.enter_email, RegexTemplate.NOT_EMPTY, R.string.error);

        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= (password.getRight() - password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (password.getTransformationMethod() instanceof PasswordTransformationMethod) {
                            password.setTransformationMethod(null);
                            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                            drawable.setAlpha(255);
                        } else {
                            password.setTransformationMethod(new PasswordTransformationMethod());
                            drawable.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
                            drawable.setAlpha(255);
                        }
                        // password.performClick();
                        return true;
                    }
                }
                return false;
            }
        });
        SpannableString forget = new SpannableString("Forgot Password ?");
        forget.setSpan(new UnderlineSpan(), 0, forget.length(), 0);
        forgotPassword.setText(forget);

        SpannableString sign_up = new SpannableString("Don't have an account, Sign Up!");
        sign_up.setSpan(new UnderlineSpan(), 23, sign_up.length(), 0);
        signUp.setText(sign_up);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAwesomeValidation.validate()) {
                    progressDialog.show();
                    String StEmail = email.getText().toString();
                    String StPassword = password.getText().toString();

                    auth.signInWithEmailAndPassword(StEmail, StPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                finish();
                            } else {
                                sign_report.setText("Error in Login");
                                sign_report.setTextColor(getResources().getColor(R.color.red));
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });

    }
}