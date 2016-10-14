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
import controllers.UserCtrl;
import entities.User;
import helper.SQLiteHandler;
import helper.SessionManager;
import helper.Utility;

public class RegisterUI extends Activity {
    private static final String TAG = RegisterUI.class.getSimpleName();
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private UserCtrl userCtrl;
    private User user;

    private EditText etRegName, etRegEmail, etRegPassword01, etRegPassword02;
    private Button btnRegister;
    private TextView tvErrorMsgRegister, tvLoginLink;
    private String valRegName, valRegEmail, valRegPassword01, valRegPassword02, valRegContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etRegName = (EditText) findViewById(R.id.ETRegName);
        etRegEmail = (EditText) findViewById(R.id.ETRegEmail);
        etRegPassword01 = (EditText) findViewById(R.id.ETRegPassword01);
        etRegPassword02 = (EditText) findViewById(R.id.ETRegPassword02);
        btnRegister = (Button) findViewById(R.id.BTNRegister);
        tvErrorMsgRegister = (TextView) findViewById(R.id.TVErrorMsgRegister);
        tvLoginLink = (TextView) findViewById(R.id.TVLoginLink);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // setup ctrl objects
        db = new SQLiteHandler(getApplicationContext());
        userCtrl = new UserCtrl(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            startActivity(new Intent(RegisterUI.this, MainUI.class));
            finish();
        } else {
            db.deleteUsers();
            session.setLogin(false);
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                valRegName = etRegName.getText().toString();
                valRegEmail = etRegEmail.getText().toString();
                valRegPassword01 = etRegPassword01.getText().toString();
                valRegPassword02 = etRegPassword02.getText().toString();
                valRegContact = "96693115";

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
                        // Create user
                        pDialog.setMessage("Registering ...");
                        showDialog();
                        new AsyncTask_Register().execute(valRegName, valRegEmail, valRegPassword02, valRegContact);
                    } else {
                        // Error messages
                        if (!isEmailValid) etRegEmail.setError("Email format not valid.");
                        if (!isPasswordSame)
                            etRegPassword02.setError("Confirm password does not match.");
                    }
                } else {
                    // Empty fields check
                    if (isNameEmpty) etRegName.setError("Required field!");
                    if (isEmailEmpty) etRegEmail.setError("Required field!");
                    if (isPassword01Empty) etRegPassword01.setError("Required field!");
                    if (isPassword02Empty) etRegPassword02.setError("Required field!");
                }
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

    // Async Task - Register
    private class AsyncTask_Register extends AsyncTask<String, Void, Void> {
        // Tag used to cancel the request
        String tag_string_req = "req_register";
        Boolean IsInternetConnected = false;

        @Override
        protected void onPreExecute() {
            Log.w(TAG, "onPreExecute()");
            IsInternetConnected = EstateCtrl.CheckInternetConnection(getApplicationContext());
        }

        @Override
        protected Void doInBackground(final String... params) {
            Log.w(TAG, "doInBackground()");
            if (IsInternetConnected) {
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        EstateConfig.URL_REGISTER, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Register Response: " + response.toString());
                        hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            if (!error) {
                                // User successfully stored in MySQL
                                // Now store the user in sqlite
                                JSONObject jObjUser = jObj.getJSONObject("user");
                                user = new User(
                                        jObjUser.getString("userID"),
                                        jObjUser.getString("name"),
                                        jObjUser.getString("email"),
                                        jObjUser.getString("password"),
                                        jObjUser.getString("contact"));

                                // inserting row in users table
                                userCtrl.addUserDetails(user);

                                Toast.makeText(getApplicationContext(), "Account successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                                // launch login activity
                                startActivity(new Intent(RegisterUI.this, LoginUI.class));
                                finish();
                            } else {

                                // Error occurred in registration. Get the error
                                // message
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Registration Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),
                                "Server is down...", Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting params to register url
                        Map<String, String> paramsRegister = new HashMap<>();
                        paramsRegister.put("name", params[0]);          // Name
                        paramsRegister.put("email", params[1]);         // Email
                        paramsRegister.put("password", params[2]);      // Password
                        paramsRegister.put("contact", params[3]);      // Contact
                        return paramsRegister;
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
            EstateCtrl.getInstance().hideSoftKeyboard(RegisterUI.this);
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
