package com.example.simon.spaghettilock.resources;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.example.simon.spaghettilock.MainActivity;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by simon on 15. 11. 23.
 * This class represents a Connected (Thread) Bluetooth client
 */
public class ConnectedThread extends Thread{
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private MainActivity ma;

    public ConnectedThread(BluetoothSocket socket) {
        //get connected socket
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        //try to set up streams to socket so we can send and receive
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    /**
     * run = while thread is running
     */
    public void run() {
        Log.d("TEST", "ConnectedThread CREATED");

        byte[] buffer = new byte[1024];
        int begin = 0;
        int bytes = 0;

        //write "Connected" to connected server
        String msg = "Connected";
        write(msg.getBytes());

        //infinit loop until connection fails
        while (true) {
            try {
                //read stream from server, save answer
                bytes = mmInStream.read(buffer, 0, buffer.length);
                String str = new String(buffer, 0, bytes, "UTF-8");
                Log.d("test", "Receiving:  "+bytes+"       "+str);
//                for(int i = begin; i < bytes; i++) {
//                    if(buffer[i] == "#".getBytes()[0]) {
//                        mHandler.obtainMessage(1, begin, i, buffer).sendToTarget();
//                        begin = i + 1;
//                        if(i == bytes - 1) {
//                            bytes = 0;
//                            begin = 0;
//                        }
//                    }
//                }
            } catch (IOException e) {
                //if connection fails close socket and go back to first fragment
                Log.d("test", "TREAD DESTROYED");
                cancel();

                break;
            }
        }
    }

    /**
     *  Write to server
     */
    public void write(byte[] bytes) {
        try {
            //save byte encoded with UTF-8 to a string to be able to print
            String str = new String(bytes, "UTF-8");
            Log.d("test", "SENDING: " + str);
            //send bytes to connected host
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

    /**
     * if connection fails close socket and go back to first fragment
     * */
    public void cancel() {
        try {
            mmSocket.close();
            ma.goBackToHome();
        } catch (IOException e) { }
    }

    /**
     * best practice to print msg from server to UI
     */
    Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            byte[] writeBuf = (byte[]) msg.obj;
            int begin = (int)msg.arg1;
            int end = (int)msg.arg2;

            switch(msg.what) {
                case 1:
                    String writeMessage = new String(writeBuf);
                    writeMessage = writeMessage.substring(begin, end);
                    Log.d("test", "Receiving: "+writeMessage);
                    break;
            }
        }
    };

    /**
     * set MainActivity obj
     * @param ma
     */
    public void setMa(MainActivity ma) {
        this.ma = ma;
    }
}
