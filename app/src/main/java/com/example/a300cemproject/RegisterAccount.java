package com.example.a300cemproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.*;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterAccount extends AppCompatActivity implements View.OnClickListener {

    //Setting private variables for the registerAccount page
    private TextView banner, registerAccount;
    private EditText editTextName, editTextAge, editTextEmailAddress, editTextPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);

        //Setting up firebase for the user accounts
        mAuth = FirebaseAuth.getInstance();

        //Setting the banner to a clickable textView
        banner = (TextView) findViewById(R.id.banner);
        banner.setOnClickListener(this);
        //initialising variables with the onCreate method
        registerAccount = (Button) findViewById(R.id.register);
        registerAccount.setOnClickListener(this);
        editTextName = (EditText) findViewById(R.id.name);
        editTextAge = (EditText) findViewById(R.id.age);
        editTextEmailAddress = (EditText) findViewById(R.id.emailAddress);
        editTextPassword = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }


    //Setting a clickable button to go back home if the app banner is clicked
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.banner:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.register:
                registerAccount();
                break;
        }
    }
    //Setting user input validation for required fields
    private void registerAccount() {
        String name = editTextName.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();
        String emailAddress = editTextEmailAddress.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(name.isEmpty()) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return;
        }
        if(age.isEmpty()) {
            editTextAge.setError("age is a required");
            editTextAge.requestFocus();
            return;
        }
        if(emailAddress.isEmpty()) {
            editTextEmailAddress.setError("email is required");
            editTextEmailAddress.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            editTextEmailAddress.setError("Not a valid Email!");
            editTextEmailAddress.requestFocus();
            return;
        }
        if(password.isEmpty()) {
            editTextPassword.setError("password is required");
            editTextPassword.requestFocus();
            return;
        }
        if(password.length() < 5) {
            editTextPassword.setError("Min Password length is 5 characters!");
            editTextPassword.requestFocus();
            return;
        }
        //Making the progress bar valid if a user has been registered successfully
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    //Allowing a user to added using the firebase API
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Account account = new Account(name, age, emailAddress);
                            Toast.makeText(RegisterAccount.this, "New account has been added!", Toast.LENGTH_LONG).show();
                            FirebaseDatabase.getInstance().getReference("Account")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(account).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                //If the account is added, then the progress bar will be gone
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(RegisterAccount.this, "New account has been added!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility((View.GONE));
                                    } else {
                                        Toast.makeText(RegisterAccount.this, "Account creation failed, please try again...", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterAccount.this, "Account creation failed, please try again...", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
