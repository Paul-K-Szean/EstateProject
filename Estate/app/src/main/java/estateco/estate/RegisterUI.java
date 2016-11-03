package estateco.estate;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.User;
import handler.SQLiteHandler;
import handler.SessionHandler;
import handler.Utility;

import static android.view.View.GONE;
import static android.widget.Toast.LENGTH_LONG;

public class RegisterUI extends Activity {
    private static final String TAG = RegisterUI.class.getSimpleName();

    private SessionHandler session;
    private SQLiteHandler db;
    private UserCtrl userCtrl;
    private PropertyCtrl propertyCtrl;
    private User user;

    private EditText etRegName, etRegEmail, etRegPassword01, etRegPassword02;
    private Button btnRegister, btnRegRandom;
    private TextView tvLoginLink;
    private String valRegName, valRegEmail, valRegPassword01, valRegPassword02, valRegContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // setup ctrl objects
        session = new SessionHandler(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());
        userCtrl = new UserCtrl(getApplicationContext(), session);
        propertyCtrl = new PropertyCtrl(getApplicationContext());
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            startActivity(new Intent(RegisterUI.this, MainUI.class));
            finish();
        } else {
            userCtrl.deleteUserTable();
            propertyCtrl.deletePropertyTable();
            session.setLogin(false);
        }

        etRegName = (EditText) findViewById(R.id.ETRegName);
        etRegEmail = (EditText) findViewById(R.id.ETRegEmail);
        etRegPassword01 = (EditText) findViewById(R.id.ETRegPassword01);
        etRegPassword02 = (EditText) findViewById(R.id.ETRegPassword02);
        btnRegister = (Button) findViewById(R.id.BTNRegister);
        btnRegRandom = (Button) findViewById(R.id.BTNRegRandom);

        tvLoginLink = (TextView) findViewById(R.id.TVLoginLink);


        SensorManager manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (manager.getSensorList(Sensor.TYPE_ALL).isEmpty()) {
            // running on an emulator
            valRegContact = "96693115";
        } else {
            // running on a device
        }

        btnRegRandom.setVisibility(GONE);
        btnRegRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = "user" + String.valueOf(Utility.generateNumberAsString(01, 20));
                etRegName.setText(value);
                etRegEmail.setText(value + "@gmail.com");
                etRegPassword01.setText(value);
                etRegPassword02.setText(value);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                createUser();
            }
        });

        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterUI.this, LoginUI.class));
                finish();
            }
        });


    }

    public void createUser() {
        Log.i(TAG, "createUser");
        // getting phone number from simcard
        TelephonyManager tMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String simCardNumber = tMgr.getLine1Number();
        if (simCardNumber.isEmpty()) {
            valRegContact = "96693115";
        } else {
            valRegContact = simCardNumber;
        }

        valRegName = etRegName.getText().toString();
        valRegEmail = etRegEmail.getText().toString();
        valRegPassword01 = etRegPassword01.getText().toString();
        valRegPassword02 = etRegPassword02.getText().toString();

        boolean isNameEmpty = valRegName.isEmpty();
        boolean isEmailEmpty = valRegEmail.isEmpty();
        boolean isPassword01Empty = valRegPassword01.isEmpty();
        boolean isPassword02Empty = valRegPassword02.isEmpty();

        // No empty fields
        if (!isNameEmpty &&
                !isEmailEmpty &&
                !isPassword01Empty &&
                !isPassword02Empty) {

            boolean isEmailValid = Utility.isEmailValid(valRegEmail);
            boolean isPasswordSame = valRegPassword02.equals((valRegPassword01));

            if (isEmailValid && isPasswordSame) {
                // register user to server
                userCtrl.serverUserRegister(RegisterUI.this, valRegName, valRegEmail, valRegPassword02, valRegContact);
            } else {
                // error messages
                if (!isEmailValid) etRegEmail.setError("Email format not valid.");
                if (!isPasswordSame)
                    etRegPassword02.setError("Confirm password does not match.");
            }
        } else {
            // empty fields check
            if (isNameEmpty) etRegName.setError("Required field!");
            if (isEmailEmpty) etRegEmail.setError("Required field!");
            if (isPassword01Empty) etRegPassword01.setError("Required field!");
            if (isPassword02Empty) etRegPassword02.setError("Required field!");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + " was " + grantResults[0]);
            //resume tasks needing this permission
            createUser();
        } else {
            Toast.makeText(RegisterUI.this, "This application need to use your mobile phone number.", LENGTH_LONG).show();
            return;
        }
    }


    @Override
    public void onStart() {
        Log.w(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.w(TAG, "onResume");
        super.onResume();
        // Fetching user details from sqlite
        user = userCtrl.getUserDetails();
    }

    @Override
    public void onPause() {
        Log.w(TAG, "onPause");
        super.onPause();
        if (user != null) {
            userCtrl.updateUserDetails(user);
        } else
            Log.e(TAG, "No user to retain");
    }

    @Override
    public void onStop() {
        Log.w(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.w(TAG, "onDestroy");
        super.onDestroy();
    }

}
