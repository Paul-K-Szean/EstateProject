package estateco.estate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import controllers.FavouriteCtrl;
import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.User;
import handler.SQLiteHandler;
import handler.SessionHandler;
import handler.Utility;

public class LoginUI extends Activity {
    private static final String TAG = LoginUI.class.getSimpleName();

    private SessionHandler session;
    private SQLiteHandler db;
    private UserCtrl userCtrl;
    private PropertyCtrl propertyCtrl;
    private FavouriteCtrl favouriteCtrl;
    private User user;


    private EditText etLoginEmail, etLoginPassword;
    private Button btnLogin, btnLoginRandom;
    private TextView tvErrorMsgLogin, tvRegisterLink;
    private String valLoginEmail, valLoginPassword;


    public LoginUI() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // setup ctrl objects
        session = new SessionHandler(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());
        userCtrl = new UserCtrl(getApplicationContext());
        propertyCtrl = new PropertyCtrl(getApplicationContext());
        favouriteCtrl = new FavouriteCtrl(getApplicationContext());
        user = userCtrl.getUserDetails();

        // check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            startActivity(new Intent(LoginUI.this, MainUI.class));
            finish();
        } else {
            // remove any existing data in local db.
            userCtrl.deleteUserTable();
            propertyCtrl.deletePropertyTable();
            favouriteCtrl.deleteFavouritePropertyTable();
            session.setLogin(false);
        }
        etLoginEmail = (EditText) findViewById(R.id.ETLoginEmail);
        etLoginPassword = (EditText) findViewById(R.id.ETLoginPassword);
        btnLogin = (Button) findViewById(R.id.BTNLogin);
        tvErrorMsgLogin = (TextView) findViewById(R.id.TVErrorMsgLogin);
        tvRegisterLink = (TextView) findViewById(R.id.TVRegisterLink);

        etLoginEmail.setText("user01@gmail.com");
        etLoginPassword.setText("user01");


        // login button click
        btnLogin.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Log.i(TAG, "LoginClicked");
                                            valLoginEmail = etLoginEmail.getText().toString();
                                            valLoginPassword = etLoginPassword.getText().toString();

                                            // Check for empty data in the form
                                            if (!valLoginEmail.isEmpty() && !valLoginPassword.isEmpty()) {
                                                // login user from server
                                                userCtrl.serverUserLogin(LoginUI.this, session, valLoginEmail, valLoginPassword);

                                            } else {
                                                // prompt user to enter credentials
                                                if (valLoginEmail.isEmpty())
                                                    etLoginEmail.setError("Cannot be empty!");
                                                if (valLoginPassword.isEmpty())
                                                    etLoginPassword.setError("Cannot be empty!");
                                            }
                                        }
                                    }
        );

        btnLoginRandom = (Button) findViewById(R.id.BTNLoginRandom);
        btnLoginRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = "user" + Utility.generateNumberAsString(10, 50);
                etLoginEmail.setText(login + "@gmail.com");
                etLoginPassword.setText(login);
            }
        });


        // link to RegisterUI
        tvRegisterLink.setOnClickListener(new View.OnClickListener()

                                          {
                                              @Override
                                              public void onClick(View v) {
                                                  startActivity(new Intent(LoginUI.this, RegisterUI.class));
                                              }
                                          }

        );
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
