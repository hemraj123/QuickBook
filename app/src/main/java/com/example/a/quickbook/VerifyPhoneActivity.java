package com.example.a.quickbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {

    private String mVerificationId;
    private EditText editTextCode;
    private FirebaseAuth mAuth;

    private EditText editText1, editText2, editText3, editText4,editText5,editText6;
    private char[] editTexts;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);


        mAuth = FirebaseAuth.getInstance();
       // editTextCode = findViewById(R.id.editTextCode);

        editText1 = (EditText) findViewById(R.id.editTextCode1);
        editText2 = (EditText) findViewById(R.id.editTextCode2);
        editText3 = (EditText) findViewById(R.id.editTextCode3);
        editText4 = (EditText) findViewById(R.id.editTextCode4);
        editText5 = (EditText) findViewById(R.id.editTextCode5);
        editText6 = (EditText) findViewById(R.id.editTextCode6);


        Intent intent = getIntent();                               //getting mobile number from the previous activity
        String mobileno = intent.getStringExtra("mobileno");

        sendVerificationCode(mobileno);


        findViewById(R.id.buttonSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = editTextCode.getText().toString().trim();
                String strArray[] = code. split(" ");

                if (code.isEmpty() || code.length() < 6) {
                    editTextCode.setError("Enter valid code");
                    editTextCode.requestFocus();
                    return;
                }
                verifyVerificationCode(code);               //verifying the code entered manually
            }
        });

    }

    private void sendVerificationCode(String mobileno) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobileno, 60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {


            String code = phoneAuthCredential.getSmsCode();

           char[] strArray=code.toCharArray();

           if (code != null)
            {


                                                      //sometime the code is not detected automatically (code fill karna)
                editText1.setText(String.valueOf(strArray[0]));
                editText2.setText(String.valueOf(strArray[1]));
                editText3.setText(String.valueOf(strArray[2]));
                editText4.setText(String.valueOf(strArray[3]));
                editText5.setText(String.valueOf(strArray[4]));
                editText6.setText(String.valueOf(strArray[5]));

                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);


            mVerificationId = s;
        }
    };


    private void verifyVerificationCode(String code)
    {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyPhoneActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Intent intent = new Intent(VerifyPhoneActivity.this, RegisterActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }
                        else

                            {
                                String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }



                        }
                    }
                });
    }

}