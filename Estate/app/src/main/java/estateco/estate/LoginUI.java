package estateco.estate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import controllers.EstateConfig;
import controllers.EstateCtrl;
import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.User;
import helper.SQLiteHandler;
import helper.SessionManager;

public class LoginUI extends Activity {
    private static final String TAG = LoginUI.class.getSimpleName();
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private UserCtrl userCtrl;
    private User user;
    private PropertyCtrl propertyCtrl;


    private EditText etLoginEmail, etLoginPassword;
    private Button btnLogin;
    private TextView tvErrorMsgLogin, tvRegisterLink;
    private String valLoginEmail, valLoginPassword;


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

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // setup ctrl objects
        db = new SQLiteHandler(getApplicationContext());
        userCtrl = new UserCtrl(getApplicationContext());
        propertyCtrl = new PropertyCtrl(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        // check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            startActivity(new Intent(LoginUI.this, MainUI.class));
            finish();
        } else {
            db.deleteUsers();
            session.setLogin(false);
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("btnLogin", "LoginClicked");
                valLoginEmail = etLoginEmail.getText().toString();
                valLoginPassword = etLoginPassword.getText().toString();

                // Check for empty data in the form
                if (!valLoginEmail.isEmpty() && !valLoginPassword.isEmpty()) {

                    new AsyncTask_Login().execute(valLoginEmail, valLoginPassword);
                } else {
                    // prompt user to enter credentials
                    if (valLoginEmail.isEmpty())
                        etLoginEmail.setError("Cannot be empty!");
                    if (valLoginPassword.isEmpty())
                        etLoginPassword.setError("Cannot be empty!");
                }

            }
        });

        // Link to RegisterUI
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginUI.this, RegisterUI.class));
                finish();
            }
        });
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

    // Async Task - Login
    private class AsyncTask_Login extends AsyncTask<String, Void, Void> {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        Boolean IsInternetConnected = false;

        @Override
        protected void onPreExecute() {
            Log.w(TAG, "onPreExecute()");
            IsInternetConnected = EstateCtrl.CheckInternetConnection(getApplicationContext());
            // login user
            pDialog.setIndeterminate(true);
            pDialog.setMessage("Logging in ...");
            showDialog();
        }

        @Override
        protected Void doInBackground(final String... params) {
            Log.w(TAG, "doInBackground()");
            if (IsInternetConnected) {

                // Connect to server
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        EstateConfig.URL_LOGIN, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Login Response: " + response.toString());
                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (error) {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                                hideDialog();
                            } else {
                                // user successfully logged in, create login session
                                session.setLogin(true);

                                // store the user in SQLite
                                JSONObject jObjUser = jObj.getJSONObject("user");
                                user = new User(
                                        jObjUser.getString("userID"),
                                        jObjUser.getString("name"),
                                        jObjUser.getString("email"),
                                        jObjUser.getString("password"),
                                        jObjUser.getString("contact"));

                                // remove existing user data
                                userCtrl.deleteUserDetails();
                                // remove any existing property data.
                                propertyCtrl.deletePropertyDetails();
                                // inserting row in users table
                                userCtrl.addUserDetails(user);

                                // Launch main activity
                                Intent intent = new Intent(LoginUI.this,
                                        MainUI.class);
                                startActivity(intent);
                                hideDialog();
                                finish();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        hideDialog();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Login Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),
                                "Server is down...", Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> paramsLogin = new HashMap<>();
                        paramsLogin.put("email", params[0]);    // Email
                        paramsLogin.put("password", params[1]); // Password
                        return paramsLogin;
                    }

                };

                // Adding request to request queue
                EstateCtrl.getInstance().addToRequestQueue(strReq, tag_string_req);


            } else {
                Toast.makeText(getApplicationContext(), "Network not detected!", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void response) {
            Log.w(TAG, "onPostExecute()");
            // hide soft key board
            EstateCtrl.getInstance().hideSoftKeyboard(LoginUI.this);
        }

    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
