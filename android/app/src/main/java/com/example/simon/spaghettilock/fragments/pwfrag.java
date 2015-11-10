package com.example.simon.spaghettilock.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.simon.spaghettilock.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class pwfrag extends Fragment {

    private EditText pwet;
    private UserLoginTask mAuthTask = null;

    public pwfrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_pwfrag, container, false);

        inti(view);
        return view;
    }

    private void inti(View view) {
        pwet = (EditText) view.findViewById(R.id.password);
        pwet.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button btUnlock = (Button) view.findViewById(R.id.btUnlockpw);
        btUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }

    private void attemptLogin() {
//        if (mAuthTask != null) {
//
//            //TODO
//
//            return;
//        }

//        // Reset errors.
        pwet.setError(null);

        // Store values at the time of the login attempt.
        String password = pwet.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            pwet.setError(getString(R.string.error_invalid_password));
            focusView = pwet;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            showProgress(true);
                mAuthTask = new UserLoginTask(password);
//       7     mAuthTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 4;
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        //        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String password) {
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

//            for (String credential : DUMMY_CREDENTIALS) {
//                String[] pieces = credential.split(":");
//                if (pieces[0].equals(mEmail)) {
//                    // Account exists, return true if the password matches.
//                    return pieces[1].equals(mPassword);
//                }
            //}

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
//            showProgress(false);

            if (success) {
//                finish();
            } else {
                pwet.setError(getString(R.string.error_incorrect_password));
                pwet.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
//            showProgress(false);
        }
    }


}
