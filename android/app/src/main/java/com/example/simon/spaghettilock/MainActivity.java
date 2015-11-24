package com.example.simon.spaghettilock;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.simon.spaghettilock.fragments.startscreen;
import com.example.simon.spaghettilock.fragments.welcome;
import com.example.simon.spaghettilock.resources.bluetoothConnect;

import java.util.List;
import java.util.Set;

public class MainActivity extends Activity {

    public static BluetoothAdapter mBluetoothAdapter;
    private final static int REQUEST_ENABLE_BT = 1;
    public ArrayAdapter<String> BTArrayAdapter;
    public Set<BluetoothDevice> pairedDevices;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initVariables();
        CheckBluetoothState();
    }

    private void initVariables() {
//        // get fragment manager
        FragmentManager fm = getFragmentManager();

        // add fragment to startscreen holder
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragContainer, new startscreen());
//         // trx.add(R.id.your_placeholder, new YourFragment(), "detail");
        ft.commit();

        BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

    }

    public void listDevices(){
        // Listing paired devices
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        // show in list
        BTArrayAdapter.notifyDataSetChanged();
        for (BluetoothDevice device : pairedDevices) {
            //
            BTArrayAdapter.add(device.getName() + " " +device.getAddress());
//                    Toast.makeText(this, "\n  Device: " + device.getName() + ", " + device, Toast.LENGTH_SHORT).show();
//                    deviceMacAdress = mBluetoothAdapter.getRemoteDevice(device.getAddress());
        }

    }

        final BroadcastReceiver bReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action))
                // Get the BluetoothDevice object from the Intent
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // add the name and the MAC address of the object to the arrayAdapter

//                BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

                BTArrayAdapter.notifyDataSetChanged();
            }
    };


    public void startBluetoothThread(String adress) {
        Log.d("TEST", adress);
        BluetoothDevice deviceAdress = mBluetoothAdapter.getRemoteDevice(adress);
        bluetoothConnect bt = new bluetoothConnect(deviceAdress);
        bt.start();
        Log.d("TEST", "THREAD STARTED");

    }

    public void CheckBluetoothState() {
        //inti adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (mBluetoothAdapter.isEnabled()) {

                Toast.makeText(this, "\n" +
                        "Bluetooth is enabled.", Toast.LENGTH_SHORT).show();
            }
        }
        //prompt user to turn on Bluetooth
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else{
            Toast.makeText(getApplicationContext(),"Bluetooth is already on",
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
}
