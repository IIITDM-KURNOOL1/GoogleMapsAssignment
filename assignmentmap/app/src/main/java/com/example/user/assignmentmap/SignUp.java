package com.example.user.assignmentmap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.*;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity  {

    private EditText email, password, confirmPassword;
    Spinner spinner;
    Button signUpBtn;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    public static final String spinner1="Guest";
    public static final String spinner2="Owner";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        email = findViewById(R.id.signupEmail);
        password = findViewById(R.id.signupPassword);
        confirmPassword = findViewById(R.id.signupPasswordConfirm);
        spinner = findViewById(R.id.spinner);
        signUpBtn = findViewById(R.id.signupBtn);
        firebaseAuth = FirebaseAuth.getInstance();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInternetConneted())
                    registerUser();
                else
                    Toast.makeText(SignUp.this, "No internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerUser() {

        final String email1=email.getText().toString().trim();
        final String pass=password.getText().toString().trim();
        String confirmPass=confirmPassword.getText().toString();

        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches())
        {
            email.setError("Wrong Email");
            email.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(pass)) {
            password.setError("Password is empty");
            return;
        }

        if(TextUtils.isEmpty(confirmPass))
        {
            confirmPassword.setError("Confirm Password is empty");
        }
        if(!TextUtils.equals(pass,confirmPass))
        {
            Toast.makeText(this, "Password and confirm password is not matching", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email1,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    databaseReference = FirebaseDatabase.getInstance().getReference("users");

                    if(spinner.getSelectedItem().toString().equals(spinner1))
                    {
                        String id = databaseReference.push().getKey();
                        databaseReference.child("Guest").child(id).child("Password").setValue(pass);
                        databaseReference.child("Guest").child(id).child("EmailID").setValue(email1);
                        databaseReference.child("Guest").child(id).child("Usertype").setValue(spinner1);
                    }
                    else if(spinner.getSelectedItem().toString().equals(spinner2))
                    {
                        String id = databaseReference.push().getKey();
                        databaseReference.child("Owner").child(id).child("Password").setValue(pass);
                        databaseReference.child("Owner").child(id).child("EmailID").setValue(email1);
                        databaseReference.child("Owner").child(id).child("Usertype").setValue(spinner2);
                        databaseReference.child("Owner").child(id).child("RestaurantDetails").child("Name").setValue("");
                        databaseReference.child("Owner").child(id).child("RestaurantDetails").child("Address").setValue("");
                        databaseReference.child("Owner").child(id).child("RestaurantDetails").child("Lattitude").setValue("");
                        databaseReference.child("Owner").child(id).child("RestaurantDetails").child("Longitude").setValue("");
                        databaseReference.child("Owner").child(id).child("RestaurantDetails").child("Menu").setValue("");
                        databaseReference.child("Owner").child(id).child("RestaurantDetails").child("Timings").setValue("");
                        databaseReference.child("Owner").child(id).child("RestaurantDetails").child("ContactInfo").setValue("");

                    }

                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(SignUp.this, "please verify your email from gmail Account", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Intent intent =new Intent(SignUp.this,LoginPage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else         {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException)
                    {
                        Toast.makeText(getApplicationContext(),"Already registered with this email",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

    }

    private boolean isInternetConneted() {

        boolean isConnected = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            isConnected = true;
        }

        //returning connection status
        return isConnected;


    }



}
