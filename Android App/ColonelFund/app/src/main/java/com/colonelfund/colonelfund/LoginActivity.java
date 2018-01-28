package com.colonelfund.colonelfund;

<<<<<<< Updated upstream
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
=======
>>>>>>> Stashed changes
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.internal.Utility;
import com.facebook.login.LoginManager;
import com.facebook.login.Login;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via userName/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private EditText mEmailView, mUserName;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
<<<<<<< Updated upstream

    @Override
    protected void onCreate(Bundle savedInstanceState) {
=======
    private Button btnLogin, btnRegister;
    AppSingleton appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appContext = new AppSingleton(this.getApplicationContext());
>>>>>>> Stashed changes
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_login);

        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        //logger.logPurchase(BigDecimal.valueOf(4.32), Currency.getInstance("USD"));

        // Set up the login form.
        mUserName = (EditText) findViewById(R.id.txtUserName);
        mPasswordView = (EditText) findViewById(R.id.txtPassword);

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        //Button fbLogin = (Button) findViewById(R.id.login_button);


<<<<<<< Updated upstream

        /**
         * A dummy authentication store containing known user names and passwords.
         * TODO: remove after connecting to a real authentication system.
         */
        final MemberCollection mcf = new MemberCollection(getApplicationContext());
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = mUserName.getText().toString();
                String pwd = mPasswordView.getText().toString();
                if(userName.equalsIgnoreCase("admin") && pwd.equals("admin")){
                    Intent MainIntent = new Intent (LoginActivity.this,MainActivity.class);
                    startActivity(MainIntent);
                    Toast.makeText(LoginActivity.this, "Signed in successfully",Toast.LENGTH_LONG).show();
                } else if (mcf.checkLogin(userName,pwd)) {
                    Intent MainIntent = new Intent (LoginActivity.this,MainActivity.class);
                    startActivity(MainIntent);
                    Toast.makeText(LoginActivity.this, "Signed in successfully",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Wrong username or password",Toast.LENGTH_LONG).show();
                }

            }
        });

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
=======
        // TODO: 1/15/2018 Remove before final submission 

        // registrationEnable
//        btnRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onRegister(view);
//            }
//        });

        btnFacebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
>>>>>>> Stashed changes
            @Override
            public void onSuccess(LoginResult loginResult) {

                AccessToken accessToken = loginResult.getAccessToken();
                GraphRequest req = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try{
                            Intent MainIntent = new Intent (LoginActivity.this,MainActivity.class);
                            startActivity(MainIntent);
                            Toast.makeText(LoginActivity.this, object.getString("name") + " Signed in successfully",Toast.LENGTH_LONG).show();

                        }catch(JSONException ex) {
                            ex.printStackTrace();
                        }

                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                req.setParameters(parameters);
                req.executeAsync();
                //Intent MainIntent = new Intent (LoginActivity.this,MainActivity.class);
                //startActivity(MainIntent);
                //Log.d("login", accessToken. );

                //Toast.makeText(LoginActivity.this, accessToken.getUserId() + "Signed in successfully",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                 Toast.makeText(LoginActivity.this, "Cancelled",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(LoginActivity.this, "Wrong username or password",Toast.LENGTH_LONG).show();
            }
        });

<<<<<<< Updated upstream
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView =      findViewById(R.id.login_progress);

    }
=======
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.

        appContext.cancelAll();
    }

    private void loginUser(final String emailAddress, final String password) {
        // Tag used to cancel the request
        String cancel_req_tag = "login";
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_FOR_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        String user = jObj.getJSONObject("user").getString("firstName");
                        // Launch User activity
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("username", user);
                        startActivity(intent);
//                        Toast.makeText(getApplicationContext(), "Signed in successfully", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else {

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Wrong username or password", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("emailAddress", emailAddress);
                params.put("password", password);
                return params;
            }

        };
        // Adding request to request queue
        strReq.setTag(getApplicationContext());

        appContext.addToRequestQueue(strReq, cancel_req_tag);
    }

    /**
     * Launches "Create new Account" page.
     *
     * @param
     */
    // TODO: 1/15/2018 remove before final submission
    // registrationEnable
/*
    public void onRegister(View view) {
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }
*/

>>>>>>> Stashed changes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

      }
}

