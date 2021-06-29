package com.example.teamsclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class LoginActivity extends AppCompatActivity {
    private Button signIn;
    private  Button signUp;
    private EditText phoneNumber;
    private TextView errorMsg;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signIn=findViewById(R.id.sign_in);
        signUp=findViewById(R.id.sign_up);
        phoneNumber=findViewById(R.id.phone_number);
        errorMsg=findViewById(R.id.error_msg);

        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(LoginActivity.this,SignUpActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//clear top
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);//clear task
                startActivity(i);
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phone=phoneNumber.getText().toString();

                if(phone.isEmpty())
                {
                    errorMsg.setText("Enter phone number to continue");
                    errorMsg.setVisibility(View.VISIBLE);

                }
                else
                {   errorMsg.setVisibility(View.INVISIBLE);
                    signIn.setEnabled(false);

                    db.collection("Users").document(
                            phone).get().
                            addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult().exists())
                            {
                                String st;
                                st="+91"+phone;
                                PhoneAuthOptions options =
                                        PhoneAuthOptions.newBuilder(auth)
                                                .setPhoneNumber(st)       // Phone number to verify
                                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                                .setActivity(LoginActivity.this)                 // Activity (for callback binding)
                                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                                .build();
                                PhoneAuthProvider.verifyPhoneNumber(options);
                            }
                            else
                            {
                                errorMsg.setText("Account does not exist!");
                                errorMsg.setVisibility(View.VISIBLE);
                                signIn.setEnabled(true);
                            }
                        }
                    });
                }
            }
        });

            mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
             signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
            errorMsg.setText("Verification Failed, please try again");
            errorMsg.setVisibility(View.VISIBLE);
            signIn.setEnabled(true);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                Intent otpIntent=new Intent(LoginActivity.this,OTPActivity.class);
                                otpIntent.putExtra("Authcredentials",s);
                                startActivity(otpIntent);
                            }
                        },1000);



            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser!=null)
        {
            sendUserToHome();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendUserToHome();
                        } else {
                            signIn.setEnabled(false);
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                errorMsg.setText("Error in verifying OTP");
                                errorMsg.setVisibility(View.VISIBLE);
                            }
                        }
                        errorMsg.setVisibility(View.INVISIBLE);
                        signIn.setEnabled(true);
                    }
                });
    }

    public void sendUserToHome()
    {
        Intent i=new Intent(LoginActivity.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//clear top
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);//clear task
        startActivity(i);
        finish();
    }
}