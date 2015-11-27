package com.example.simon.spaghettilock.resources;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.simon.spaghettilock.LoginActivity;
import com.example.simon.spaghettilock.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by simon on 15. 11. 7.
 */
public class bluetoothConnect extends Thread {
        private final BluetoothSocket mmSocket;
        private ConnectedThread mConnectedThread;
    private MainActivity ma;
        // Unique UUID for this application, you may use different
        private static final UUID MY_UUID = UUID
                .fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

        public bluetoothConnect(BluetoothDevice device, MainActivity ma) {
            this.ma = ma;
            Log.d("TEST", device.getAddress());

            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.d("CONNECTTHREAD", "Could not create RFCOMM socket: " + e.toString());
                e.printStackTrace();
            }
            mmSocket = tmp;
        }
            //now make the socket connection in separate thread to avoid FC
            public void run() {
                    // Always cancel discovery because it will slow down a connection
                    MainActivity.mBluetoothAdapter.cancelDiscovery();

                    // Make a connection to the BluetoothSocket
                    try {
                        Log.i("TEST", "Listening for a connection...");

                        // This is a blocking call and will only return on a
                        // successful connection or an exception
                        mmSocket.connect();

                        Log.d("TEST", "Connected to " + mmSocket.getRemoteDevice().getName());

                        Log.d("TEST", "SUCCESSFULLY CONNECTED!");
                    } catch (IOException e) {
                        //connection to device failed so close the socket
                        Log.d("TEST", e.getMessage());
                        Log.d("TEST", "EPIC FAIL!");

                        try {
                            mmSocket.close();
                            Log.d("TEST", "SOCKET CLOSED!");

                        } catch (IOException e2) {
                            Log.d("CONNECT THREAD", "Could not close connection: " + e.toString());
                            e2.printStackTrace();
                        }
                    }
                if (mmSocket.isConnected()) {
                    mConnectedThread = new ConnectedThread(mmSocket);
                    mConnectedThread.setMa(ma);
                    mConnectedThread.start();
                    ma.createWelcomefrag(mConnectedThread);
                }else{
                    try {
                        sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

    public void cancel() {
        try {
            mmSocket.close();
            mConnectedThread.cancel();
        } catch (IOException e) { }
    }

    public boolean isConnected() {
       return mmSocket.isConnected();
    }

}