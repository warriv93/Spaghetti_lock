package com.example.simon.spaghettilock.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simon.spaghettilock.MainActivity;
import com.example.simon.spaghettilock.R;
import com.example.simon.spaghettilock.resources.ConnectedThread;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * A simple {@link Fragment} subclass.
 */
public class pwfrag extends Fragment {
    private MainActivity ma;
   // private final ProgressDialog showProgress = ProgressDialog.show(ma, "", "Please wait, Loading Page...", true);
    private EditText pwet;
    private UserLoginTask mAuthTask = null;
    private ConnectedThread ct;
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();


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




         /*   try {

                int iterationCount = 10000;
                int keyLength = 256;
                int saltLength = keyLength / 8; // same size as key output

                SecureRandom random = new SecureRandom();
                //byte[] salt = new byte[saltLength];

                String str = "salt";
                byte[] salt = str.getBytes();
                //random.nextBytes(salt);
                KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
                        iterationCount, keyLength);
                SecretKeyFactory keyFactory = SecretKeyFactory
                        .getInstance("PBKDF2WithHmacSHA1");

                byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
                SecretKey key = new SecretKeySpec(keyBytes, "AES");

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                byte[] iv = new byte[cipher.getBlockSize()];
                random.nextBytes(iv);
                IvParameterSpec ivParams = new IvParameterSpec(iv);
                cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
                //Log.d("TEST","before cipher:  "+password);
                byte[] ciphertext = cipher.doFinal(plaintext.getBytes("UTF-8"));
*/


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
                try {
                    char[] chars = mPassword.toCharArray();
                    byte[] salt = "salt".getBytes();
                    //getSalt();

                    PBEKeySpec spec = new PBEKeySpec(chars, salt, 1000,
                            20 * Byte.SIZE);
                    SecretKeyFactory skf = SecretKeyFactory
                            .getInstance("PBKDF2WithHmacSHA1");
                    byte[] hash = skf.generateSecret(spec).getEncoded();

                    MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                    hash = sha256.digest();

                    //return toHex(salt) + ":" + toHex(hash);

                    String res = toHex(hash);
                    Log.d("test", "after cipher:  : " + res);

                    //send final hashed pw to pc
                    ct.write(hash);
                    Toast.makeText(ma, "Password successfully sent to PC!", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    System.out.println("Exception: Error in generating password"
                            + e.toString());
                }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
          //showProgress.dismiss();

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

    public void setMa(MainActivity ma) {
        this.ma = ma;
    }
}