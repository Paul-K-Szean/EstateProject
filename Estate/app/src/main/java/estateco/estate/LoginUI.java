package estateco.estate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import controllers.EstateConfig;
import controllers.EstateCtrl;
import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.User;
import handler.AsyncTaskResponse;
import handler.AsyncTaskHandler;
import handler.ErrorHandler;
import handler.SQLiteHandler;
import handler.SessionHandler;

public class LoginUI extends Activity {
    private static final String TAG = LoginUI.class.getSimpleName();

    private SessionHandler session;
    private SQLiteHandler db;
    private UserCtrl userCtrl;
    private PropertyCtrl propertyCtrl;
    private User user;


    private EditText etLoginEmail, etLoginPassword;
    private Button btnLogin;
    private TextView tvErrorMsgLogin, tvRegisterLink;
    private String valLoginEmail, valLoginPassword;

    private Map<String, String> paramValues;

    public LoginUI() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.w(TAG, "onCreate");
        etLoginEmail = (EditText) findViewById(R.id.ETLoginEmail);
        etLoginPassword = (EditText) findViewById(R.id.ETLoginPassword);
        btnLogin = (Button) findViewById(R.id.BTNLogin);
        tvErrorMsgLogin = (TextView) findViewById(R.id.TVErrorMsgLogin);
        tvRegisterLink = (TextView) findViewById(R.id.TVRegisterLink);

        etLoginEmail.setText("user01@gmail.com");
        etLoginPassword.setText("user01");


        // setup ctrl objects
        db = new SQLiteHandler(getApplicationContext());
        userCtrl = new UserCtrl(getApplicationContext());
        propertyCtrl = new PropertyCtrl(getApplicationContext());
        session = new SessionHandler(getApplicationContext());

        // check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            startActivity(new Intent(LoginUI.this, MainUI.class));
            finish();
        } else {
            // remove any existing data in local db.
            userCtrl.deleteUserDetails();
            propertyCtrl.deletePropertyDetails();
            session.setLogin(false);
        }

        // login button click
        btnLogin.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Log.i(TAG, "LoginClicked");
                                            valLoginEmail = etLoginEmail.getText().toString();
                                            valLoginPassword = etLoginPassword.getText().toString();

                                            // Check for empty data in the form
                                            if (!valLoginEmail.isEmpty() && !valLoginPassword.isEmpty()) {
                                                // get user data from server
                                                paramValues = new HashMap<>();
                                                paramValues.put(UserCtrl.KEY_EMAIL, valLoginEmail);
                                                paramValues.put(UserCtrl.KEY_PASSWORD, valLoginPassword);
                                                new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_LOGIN, paramValues, LoginUI.this, new AsyncTaskResponse() {
                                                    @Override
                                                    public void onAsyncTaskResponse(String response) {
                                                        try {
                                                            Log.i(TAG, response);
                                                            JSONObject jObj = new JSONObject(response);
                                                            boolean error = jObj.getBoolean("error");
                                                            // check for error in json
                                                            if (error) {
                                                                String errorMsg = jObj.getString("error_msg");
                                                                ErrorHandler.errorHandler(LoginUI.this, errorMsg);
                                                            } else {
                                                                session.setLogin(true);
                                                                JSONObject userObj = jObj.getJSONObject("user");
                                                                user = new User(
                                                                        userObj.getString(UserCtrl.KEY_USERID),
                                                                        userObj.getString(UserCtrl.KEY_NAME),
                                                                        userObj.getString(UserCtrl.KEY_EMAIL),
                                                                        userObj.getString(UserCtrl.KEY_PASSWORD),
                                                                        userObj.getString(UserCtrl.KEY_CONTACT));
                                                                Log.i(TAG, "user: " + user.getUserID());
                                                                EstateCtrl.syncToLocalDB(LoginUI.this, user);
                                                                startActivity(new Intent(LoginUI.this, MainUI.class));
                                                                finish(); // close this activity
                                                            }
                                                        } catch (JSONException error) {
                                                            // JSON error
                                                            ErrorHandler.errorHandler(LoginUI.this, error);
                                                        }
                                                    }
                                                }).execute();


                                            } else {
                                                // prompt user to enter credentials
                                                if (valLoginEmail.isEmpty())
                                                    etLoginEmail.setError("Cannot be empty!");
                                                if (valLoginPassword.isEmpty())
                                                    etLoginPassword.setError("Cannot be empty!");
                                            }
                                            Log.i(TAG, "End of login clicked");
                                        }
                                    }
        );

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
