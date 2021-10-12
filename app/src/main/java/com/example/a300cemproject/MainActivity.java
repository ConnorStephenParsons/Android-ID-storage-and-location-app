package com.example.a300cemproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {
    //Variables for account login in system on this page
    private TextView register;
    private EditText editTextEmailAddress, editTextPassword;
    private Button login;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    //Variables for the third sensor
    private TextView textView;
    private SensorManager sensorManager;
    private Sensor sensor;




    //Allowing the following to be clickable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);

        editTextEmailAddress = (EditText) findViewById(R.id.emailAddress);
        editTextPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar)  findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

        //Initialising the sensor text views to the layout file
        textView = findViewById(R.id.text_accelerometer);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MainActivity.this, sensor, sensorManager.SENSOR_DELAY_NORMAL);




    }
    //cases on how the user can interact with the system
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.register:
                startActivity(new Intent(this, RegisterAccount.class));
                break;

            case R.id.login:
                accountLogin();
                break;
        }

    }
    //Logic for logging in a user
    private void accountLogin() {
        String emailAddress = editTextEmailAddress.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //validation for logging in.
        if(emailAddress.isEmpty()) {
            editTextEmailAddress.setError(("No Email inputted "));
            editTextEmailAddress.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            editTextEmailAddress.setError("You must use a valid Email");
            editTextEmailAddress.requestFocus();
            return;
        }
        if(password.isEmpty()) {
            editTextPassword.setError("You must enter your password!");
            editTextPassword.requestFocus();
            return;
        }
        if(password.length() < 5) {
            editTextPassword.setError("Password length is a minimum of 5 characters");
            editTextPassword.requestFocus();
            return;
        }
        progressBar.setVisibility((View.VISIBLE));
        mAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    //User is therefore redirected to the Identity page
                    startActivity(new Intent(MainActivity.this, IdentityActivity.class));
                }else{
                    Toast.makeText(MainActivity.this, "Failed to login to account check credentials and try again...", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //the event values provides tje data for the accelerometer (3 different axis, x, y, z
        textView.setText(event.values[0]+"\n"+event.values[1]+"\n"+event.values[2]);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
