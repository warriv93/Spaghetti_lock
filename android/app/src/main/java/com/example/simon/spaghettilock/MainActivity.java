package com.example.simon.spaghettilock;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.example.simon.spaghettilock.fragments.pwfrag;
import com.example.simon.spaghettilock.fragments.startscreen;
import com.example.simon.spaghettilock.fragments.welcome;
import com.example.simon.spaghettilock.resources.ConnectedThread;
import com.example.simon.spaghettilock.resources.bluetoothConnect;
import java.util.Set;

public class MainActivity extends Activity {
    public static BluetoothAdapter mBluetoothAdapter;
    private final static int REQUEST_ENABLE_BT = 1;
    public ArrayAdapter<String> BTArrayAdapter;
    public Set<BluetoothDevice> pairedDevices;
    private bluetoothConnect bt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initVariables();
        CheckBluetoothState();
    }

    /**
     * add fragment startscreen
     * init arrayadapter for list
     */
    private void initVariables() {
        goBackToHome();
        //init array adapter for list
        BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

    }

    /**
     * get all devices paired to device to be able to connect to pc
     */
    public void listDevices(){
        // Listing paired devices
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        // update list
        BTArrayAdapter.notifyDataSetChanged();
        // loop though lsit and add each name and address to array
        for (BluetoothDevice device : pairedDevices) {
            BTArrayAdapter.add(device.getName() + " " +device.getAddress());
        }
    }

    /**
     * start the bluetooth thread that will listen for connection for the socket
     * @param adress
     */
    public void startBluetoothThread(String adress) {
        //Log.d("TEST", adress);
        BluetoothDevice deviceAdress = mBluetoothAdapter.getRemoteDevice(adress);
        bt = new bluetoothConnect(deviceAdress, this);
        bt.start();
        Log.d("TEST", "THREAD STARTED");
    }
    /**
     * change fragment to welcome fragment (choose either fingerprint or password unlock)
     */
    public void createWelcomefrag(ConnectedThread ct) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        welcome w = new welcome();
        w.setCt(ct);
        w.setMa(this);
        ft.replace(R.id.fragContainer, w);
        ft.commit();
    }
    /**
     * change fragment to password fragment
     */
    public void createpwFrag(ConnectedThread ct) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        pwfrag pwFrag = new pwfrag();
        pwFrag.setCt(ct);
        ft.replace(R.id.fragContainer, pwFrag);
        ft.commit();
    }

    /**
     * Check if phone has a bluetooth adapter
     * if it's turned of: turn it on
     */
    public void CheckBluetoothState() {
        //inti adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(this, "This device does not support Bluetooth.", Toast.LENGTH_SHORT).show();
        } else {
            if (mBluetoothAdapter.isEnabled()) {

                Toast.makeText(this, "\n" +
                        "Bluetooth is enabled.", Toast.LENGTH_SHORT).show();
            }
        }
        //force start bluetooth adapter
       if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Toast.makeText(getApplicationContext(),"Bluetooth is now enabled.",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is enabled.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * change fragment to startscreen
     */
    public void goBackToHome() {
        // get fragment manager
        FragmentManager fm = getFragmentManager();
        // add fragment to startscreen holder
        FragmentTransaction ft = fm.beginTransaction();
        startscreen sc = new startscreen();
        sc.setMa(this);
        ft.add(R.id.fragContainer, new startscreen());
        ft.commit();
    }

    /**
     * on Application (pause) minimized cancel bluetooth listener thread
     * bt.cancel() = close socket and cancel all connected threads
     */
    @Override
    protected void onPause() {
        super.onPause();
        bt.cancel();
    }

    /**
     * when application is resumed from minimization go back to startscreen
     */
    @Override
    protected void onResume() {
        super.onResume();
        goBackToHome();
    }
}
