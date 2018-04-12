package com.example.user.assignmentmap;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class LoginPage extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    EditText emailid,passowrdTv;
    TextView signup;
    Button loginBtn;
    DatabaseReference guestReference= FirebaseDatabase.getInstance().getReference("users").child("Guest");
    DatabaseReference ownerReference=FirebaseDatabase.getInstance().getReference("users").child("Owner");
    ArrayList<UserDetails> userList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        firebaseAuth=FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        emailid=findViewById(R.id.emailId);
        passowrdTv=findViewById(R.id.password);
        signup=findViewById(R.id.signup);
        loginBtn=findViewById(R.id.loginBtn);

        if(isInternetConnected())
        {
            guestReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                        UserDetails userDetails = new UserDetails();
                        userDetails.setEmail(snapshot.child("EmailID").getValue().toString());
                        userDetails.setSpinnerType(snapshot.child("Usertype").getValue().toString());
                        userDetails.setPassword(snapshot.getKey());

                        userList.add(userDetails);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        if(isInternetConnected())
        {
            ownerReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                        UserDetails userDetails = new UserDetails();
                        userDetails.setEmail(snapshot.child("EmailID").getValue().toString());
                        userDetails.setSpinnerType(snapshot.child("Usertype").getValue().toString());
                        userDetails.setPassword(snapshot.getKey());

                        userList.add(userDetails);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginPage.this,SignUp.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInternetConnected())
                {
                    userLogin();
                }
                else {
                    Toast.makeText(LoginPage.this, "Internet error!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    private void userLogin() {
        final String email1=emailid.getText().toString().trim();
        String pass=passowrdTv.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(emailid.getText()).matches())
        {
            emailid.setError("Incorrect email");
            return;

        }
        if(TextUtils.isEmpty(email1))
        {
            emailid.setError("Empty email");
            return;
        }
        if(TextUtils.isEmpty(pass))
        {
            passowrdTv.setError("password field is empty");
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email1,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    int flag=0;
                    FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                    if(firebaseUser.isEmailVerified())
                    {
                        for(int i=0;i<userList.size();i++)
                        {
                            if(userList.get(i).getEmail().equals(email1))
                            {
                                Intent intent=new Intent(LoginPage.this,BaseActivity.class);
                                intent.putExtra("Key",userList.get(i).getPassword());
                                intent.putExtra("Usertype",userList.get(i).getSpinnerType());
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                flag=1;
                                startActivity(intent);
                                break;
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(LoginPage.this, "Email Not Verified", Toast.LENGTH_SHORT).show();
                    }

                    if(flag==0)
                    {
                        Toast.makeText(LoginPage.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(LoginPage.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }


            }



        });

    }

    private boolean isInternetConnected() {
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
