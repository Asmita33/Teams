package com.example.teamsclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OTPActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private String mAuthCredentials;

    private Button signIn;
    private EditText code;
    private TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);

        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();

        signIn=findViewById(R.id.sign_in);
        code=findViewById(R.id.code);
        error=findViewById(R.id.error_msg);

         mAuthCredentials=getIntent().getStringExtra("Authcredentials");

         signIn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String mCode=code.getText().toString();
                 if(mCode.isEmpty())
                 {
                     error.setText("Enter code");
                     error.setVisibility(View.VISIBLE);
                 }
                 else if(mCode.length()<6||mCode.length()>6)
                 {

                     error.setText("Please enter the 6-digit code.The code only contains numbers");
                     error.setVisibility(View.VISIBLE);
                 }
                 else
                 {
                     error.setVisibility(View.INVISIBLE);
                     signIn.setEnabled(false);
                     PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mAuthCredentials, mCode);
                     signInWithPhoneAuthCredential(credential);
                 }
             }
         });

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(OTPActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendUserToHome();
                        } else {
                           signIn.setEnabled(false);
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                error.setText("That code didn't work. Check the code and try again.");
                                error.setVisibility(View.VISIBLE);
                            }
                        }
                        error.setVisibility(View.INVISIBLE);
                        signIn.setEnabled(true);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser!=null)
        {
            sendUserToHome();
        }
    }
    public void sendUserToHome()
    {
        Intent i=new Intent(OTPActivity.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//clear top
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);//clear task
        startActivity(i);
        finish();
    }
}