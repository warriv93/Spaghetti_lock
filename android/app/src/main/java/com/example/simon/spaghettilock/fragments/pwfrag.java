package com.example.simon.spaghettilock.fragments;


import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.simon.spaghettilock.R;
import com.example.simon.spaghettilock.resources.ConnectedThread;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
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
    final ProgressDialog showProgress = ProgressDialog.show(getActivity(), "", "Please wait, Loading Page...", true);
    private EditText pwet;
    private UserLoginTask mAuthTask = null;
    private ConnectedThread ct;

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
                attemptLogin();
            }
        });

    }

    /**
     * attempting the unlock by sending the password to connected server in salted hash
     */
    private void attemptLogin() {
        // Reset errors.
        pwet.setError(null);

        // Store values at the time of the inputed password
        String password = pwet.getText().toString();
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
            showProgress.show();
            mAuthTask = new UserLoginTask(password);

            //            mAuthTask.execute((Void) null);
        }
    }

    /**
     * check if the entered password is 4 chars or longer
     * @param password
     * @return
     */
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 4;
    }

    /**
     * set ConnectedThread obj
     * @param ct
     */
    public void setCt(ConnectedThread ct) {
        this.ct = ct;
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

                try {

                    int iterationCount = 1000;
                    int keyLength = 256;
                    int saltLength = keyLength / 8; // same size as key output

                    SecureRandom random = new SecureRandom();
                    //byte[] salt = new byte[saltLength];

                    String str = "salt";
                    byte[] salt = str.getBytes();
                    //random.nextBytes(salt);
                    KeySpec keySpec = new PBEKeySpec(mPassword.toCharArray(), salt,
                            iterationCount, keyLength);
                    SecretKeyFactory keyFactory = SecretKeyFactory
                            .getInstance("PBKDF2WithHmacSHA1");

                    byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
                    SecretKey key = new SecretKeySpec(keyBytes, "AES");

                    Cipher cipher = null;
                    cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

                    byte[] iv = new byte[cipher.getBlockSize()];
                    random.nextBytes(iv);
                    IvParameterSpec ivParams = new IvParameterSpec(iv);
                    cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
                    Log.d("TEST","before cipher:  "+mPassword);
                    byte[] ciphertext = cipher.doFinal(mPassword.getBytes("UTF-8"));

                    //send final hashed pw to pc
                    ct.write(ciphertext);
                    Log.d("TEST: " , "after cipher:  "+ciphertext.toString());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }




                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            //write password to pc
           // ct.write(mPassword.getBytes());

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
          showProgress.dismiss();

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
            showProgress.dismiss();
        }
    }


}
