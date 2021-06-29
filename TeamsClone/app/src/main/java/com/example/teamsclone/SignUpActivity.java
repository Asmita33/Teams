package com.example.teamsclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teamsclone.ui.DatePickerFragment;
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

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    Button add,back,nameFragNext,bdayFragNext,verificationFragNext,chooseDateBtn;
    EditText phone,firstName,lastName,verificationCode;
    FirebaseFirestore database;
    TextView phonenumber,phonenumber1,phonenumber2,chooseDate,errorMsgPhoneFrag,errorMsgVerificationFrag;
    String currentDate;
    Users user=new Users();

    private String mAuthCredentials;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();


        database=FirebaseFirestore.getInstance();
        add=findViewById(R.id.button);
        phone=findViewById(R.id.editTextPhone);
        back=findViewById(R.id.back_button);
        verificationCode=findViewById(R.id.verification_phone);
        chooseDate=findViewById(R.id.textView_date);
        chooseDateBtn=findViewById(R.id.choose_bdate);
        errorMsgPhoneFrag=findViewById(R.id.text_error);
        errorMsgVerificationFrag=findViewById(R.id.text_error_verification);
        phonenumber1=findViewById(R.id.textViewPhone1);
        phonenumber2=findViewById(R.id.textViewPhone2);
        phonenumber=findViewById(R.id.textViewPhone);
        firstName=findViewById(R.id.editTextFirstName);
        lastName=findViewById(R.id.editTextLastName);
        nameFragNext=findViewById(R.id.name_frag_next);
        bdayFragNext=findViewById(R.id.bday_frag_next);
        verificationFragNext=findViewById(R.id.verification_frag_next);


        FragmentManager manager=this.getSupportFragmentManager();
        manager.beginTransaction().show(manager.findFragmentById(R.id.phone_frag))
                .hide(manager.findFragmentById(R.id.name_fragment))
                .hide(manager.findFragmentById(R.id.birthday_fragment))
                .hide(manager.findFragmentById(R.id.verification_fragment))
                .commit();

        //On clicking next of name_fragment
        nameFragNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=firstName.getText().toString()+' '+lastName.getText().toString();
                user.setName(name);
                manager.beginTransaction().
                        show(manager.findFragmentById(R.id.birthday_fragment))
                        .hide(manager.findFragmentById(R.id.phone_frag))
                        .hide(manager.findFragmentById(R.id.name_fragment))
                        .hide(manager.findFragmentById(R.id.verification_fragment))
                        .commit();

               phonenumber1.setText("+91"+phone.getText().toString());
            }
        });

        //On clicking next of birthday_fragment
        bdayFragNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setBirthdate(currentDate);
                manager.beginTransaction().
                        show(manager.findFragmentById(R.id.verification_fragment))
                        .hide(manager.findFragmentById(R.id.phone_frag))
                        .hide(manager.findFragmentById(R.id.name_fragment))
                        .hide(manager.findFragmentById(R.id.birthday_fragment))
                        .commit();
                phonenumber2.setText("+91"+phone.getText().toString());

                String st;
                st="+91"+phone.getText().toString();
                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(auth)
                                .setPhoneNumber(st)       // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(SignUpActivity.this)                 // Activity (for callback binding)
                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });

        //On clicking choose date drop down button
        chooseDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker=new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(),"date picker");
            }
        });

        //On clicking next of verification_fragment
        verificationFragNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.collection("Users").document(user.getPhone()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                    }
                });
                String mCode=verificationCode.getText().toString();
                if(mCode.isEmpty())
                {

                }
                else if(mCode.length()<6||mCode.length()>6)
                {

                }
                else
                {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mAuthCredentials, mCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                finish();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=phone.getText().toString();

                if(str.length()!=10)
                {
                 errorMsgPhoneFrag.setText("Invalid number");
                 errorMsgPhoneFrag.setVisibility(View.VISIBLE);
                }
                else
                {
                    errorMsgPhoneFrag.setVisibility(View.INVISIBLE);
                    phonenumber.setText("+91"+phone.getText().toString());
                    user.setPhone(str);

                    db.collection("Users").document(
                            str).get().
                            addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.getResult().exists())
                                    {
                                        errorMsgPhoneFrag.setVisibility(View.VISIBLE);
                                        errorMsgPhoneFrag.setText("Account already exists for this number");

                                    }
                                    else
                                    {
                                        errorMsgPhoneFrag.setVisibility(View.INVISIBLE);
                                        manager.beginTransaction().
                                                show(manager.findFragmentById(R.id.name_fragment))
                                                .hide(manager.findFragmentById(R.id.phone_frag))
                                                .hide(manager.findFragmentById(R.id.birthday_fragment))
                                                .hide(manager.findFragmentById(R.id.verification_fragment))
                                                .commit();
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
                errorMsgVerificationFrag.setVisibility(View.VISIBLE);
                errorMsgVerificationFrag.setText("Verification failed");
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                errorMsgVerificationFrag.setVisibility(View.INVISIBLE);
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                mAuthCredentials=s;
                            }
                        },1000);



            }
        };

    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c= Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);

        currentDate= DateFormat.getDateInstance(DateFormat.MONTH_FIELD).format(c.getTime());
        chooseDate.setText(currentDate);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendUserToHome();
                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            }
                        }

                    }
                });
    }


    public void sendUserToHome()
    {
        Intent i=new Intent(SignUpActivity.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//clear top
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);//clear task
        startActivity(i);
        finish();
    }
}