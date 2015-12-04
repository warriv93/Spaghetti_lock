package com.example.simon.spaghettilock.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.example.simon.spaghettilock.MainActivity;
import com.example.simon.spaghettilock.R;
import com.example.simon.spaghettilock.resources.ConnectedThread;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * This is the fragment that holds the Password input. and here is also a inner AsyncTask that handles the password hashing attempt.
 * And then sends it to the ConnectedThread (Bluetooth connection) to send it to the connected PC.
 * A simple {@link Fragment} subclass.
 */
public class pwfrag extends Fragment {
    private MainActivity ma;
   // private final ProgressDialog showProgress = ProgressDialog.show(ma, "", "Please wait, Loading Page...", true);
    private EditText pwet;
    private UserLoginTask mAuthTask = null;
    private ConnectedThread ct;
    final protected static char[] hexArray = "0123456789abcdef".toCharArray();


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
    /**
     * Init all the needed components for this fragment
     * @param view
     */
    private void inti(View view) {
        ma = (MainActivity) getActivity();
        //set background background
        RelativeLayout pwLayout = (RelativeLayout) view.findViewById(R.id.pwLayout);
        pwLayout.setBackgroundColor(Color.WHITE);
        pwet = (EditText) view.findViewById(R.id.password);
        pwet.setText("passwordpassword");

        //while something is inputed into the edittext.
//        pwet.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == R.id.login) {
//                    attemptLogin();
//                    return true;
//                }
//                return false;
//            }
//        });

        Button btUnlock = (Button) view.findViewById(R.id.btUnlockpw);
        //on unlock button click attempt the unlock
        btUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(view);
            }
        });

    }

    /**
     * attempting the unlock by sending the password to connected server in salted hash
     */
    private void attemptLogin(View view) {
        // Reset errors.
        pwet.setError(null);

        // Store values at the time of the inputed password
        String password = pwet.getText().toString();
        pwet.setText("");
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password)) {
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
            //showProgress.show();

            mAuthTask = new UserLoginTask(password);
            mAuthTask.execute((Void) null);
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)ma.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
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
            // attempt authentication against server.
            Looper.prepare();
            try {
                    char[] chars = mPassword.toCharArray();
                    byte[] salt = "momsspaghetti".getBytes();
                    //getSalt();

                //char array of password, byte array of salt, 10000 iterations, 512 bits (64bytes)
                    PBEKeySpec spec = new PBEKeySpec(chars, salt, 10000,
                           256);
                    SecretKeyFactory skf = SecretKeyFactory
                            .getInstance("PBKDF2WithHmacSHA1");
                    byte[] hash = skf.generateSecret(spec).getEncoded();
                    String res1 = toHex(hash);
                    //String res1 = new String(hash, "UTF-8");
                    Log.d("test", "before cipher:  : " + res1);

                    //merges 2 byte[] together
                    byte[] one = "#magic#".getBytes();
                    byte[] two = res1.getBytes();
                    String str = new String(one, "UTF-8");
                    Log.d("test", "magic?: " + one);
                    byte[] combined = new byte[one.length + two.length];
                    for (int i = 0; i < combined.length; ++i){
                        combined[i] = i < one.length ? one[i] : two[i - one.length];
                    }
                     //send final hashed pw to pc

                    String res2 = toHex(res1.getBytes());
                    Log.d("test", "AFTER combination:  : " + res2);
                    ct.write(combined);
                } catch (Exception e) {
                    System.out.println("Exception: Error in generating password: "
                            + e.toString());
                }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
          //showProgress.dismiss();
            Toast.makeText(ma, "Password successfully sent to PC!", Toast.LENGTH_SHORT).show();

            if (success) {
                //finish();
            } else {
                pwet.setError(getString(R.string.error_incorrect_password));
                pwet.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
           // showProgress.dismiss();
        }
    }

/**
 * translate input byte array to hex
 */
    public static String toHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * check if the entered password is 4 chars or longer
     * @param password
     * @return
     */
    private boolean isPasswordValid(String password) {
        return password.length() >= 4;
    }

    /**
     * set ConnectedThread obj
     * @param ct
     */
    public void setCt(ConnectedThread ct) {
        this.ct = ct;
    }
}