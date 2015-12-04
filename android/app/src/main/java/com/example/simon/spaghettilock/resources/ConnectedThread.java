package com.example.simon.spaghettilock.resources;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import com.example.simon.spaghettilock.MainActivity;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


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
        Looper.prepare();
        byte[] buffer = new byte[1024];
        int begin = 0;
        int bytes = 0;

        //write "Connected" to connected server
        String msg = "Connected";
        byte[] one = "#msg#".getBytes();
        byte[] two = msg.getBytes();
        byte[] combined = new byte[one.length + two.length];
        for (int i = 0; i < combined.length; ++i){
            combined[i] = i < one.length ? one[i] : two[i - one.length];
        }
        write(combined);

        //infinit loop until connection fails
        while (true) {
            try {
                //read stream from server, save answer
                bytes = mmInStream.read(buffer, 0, buffer.length);
                String str = new String(buffer, 0, bytes, "UTF-8");
                Log.d("test", "Receiving:  "+bytes+"       "+str);
                Toast.makeText(ma, "Server: "+str, Toast.LENGTH_SHORT).show();
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
    public void write(byte[] msg) {
        try {
            //save byte encoded with UTF-8 to a string to be able to print
            String str = new String(msg, "UTF-8");
            Log.d("test", "before encrypt, SENDING: " + str);

            //generate current time in secounds
            Calendar c = Calendar.getInstance();
            Long seconds = System.currentTimeMillis()/1000;
            String pwtime = Long.toString(seconds);
            while(pwtime.length()<16){
                pwtime+="0";
            }
            Log.d("test", "encryption key: " + pwtime);
            //run the encryption algorithms
            while(str.length()%32!=0){
                str+="#";
            }
            Log.d("test", "encryption key: " + str);

            byte[] encryptedMsg = encrypt(pwtime.getBytes(), str.getBytes());

            String str2 = new String(encryptedMsg, "UTF-8");
            Log.d("test", "After encrypt, SENDING: " + str2);
            //send bytes to connected host
            mmOutStream.write(encryptedMsg);
        } catch (IOException e) {
            e.printStackTrace();
            } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Performing and encryption with AES, CBC and NoPadding
     * @param raw
     * @param msg
     * @return
     * @throws Exception
     */
    private static byte[] encrypt(byte[] raw, byte[] msg) throws Exception {
       //Init IV vector
        String IV = new String(raw, "UTF-8");

        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES/CBC/NoPadding");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(IV.getBytes()));
        byte[] encrypted = cipher.doFinal(msg);
        return encrypted;
    }

    /**
     * if connection fails close socket and go back to first fragment
     * */
    public void cancel() {
        try {
            mmSocket.close();
            ma.createHomeFrag();
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
