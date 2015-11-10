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
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        byte[] buffer;

        // Unique UUID for this application, you may use different
        private static final UUID MY_UUID = UUID
                .fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

        public bluetoothConnect(BluetoothDevice device) {

            Log.d("TEST", device.getAddress());

            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.d("CONNECTTHREAD","Could not create RFCOMM socket: " + e.toString());
                e.printStackTrace();
            }
            mmSocket = tmp;

            //now make the socket connection in separate thread to avoid FC
            Thread connectionThread  = new Thread(new Runnable() {

                @Override
                public void run() {
                    // Always cancel discovery because it will slow down a connection
                    MainActivity.mBluetoothAdapter.cancelDiscovery();

                    // Make a connection to the BluetoothSocket
                    try {
                        // This is a blocking call and will only return on a
                        // successful connection or an exception
                        mmSocket.connect();
                        Log.d("TEST", "SUCCESSFULLY CONNECTED!" );
                    } catch (IOException e) {
                        //connection to device failed so close the socket
                        try {
                            mmSocket.close();
                        } catch (IOException e2) {
                            Log.d("CONNECT THREAD", "Could not close connection: " + e.toString());
                            e2.printStackTrace();
                        }
                    }
                }
            });

            connectionThread.start();

            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
                buffer = new byte[1024];
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }



        public void run() {

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    int bytes;
                    //read the data from socket stream
                    bytes = mmInStream.read(buffer);


//                    byte[] byteArray = new byte[](bytes);
                     String response = Arrays.toString(buffer);

                    Log.d("TEST", response);

                    // Send the obtained bytes to the UI Activity
                } catch (IOException e) {
                    //an exception here marks connection loss
                    //send message to UI Activity
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                //write the data to socket stream
                mmOutStream.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

