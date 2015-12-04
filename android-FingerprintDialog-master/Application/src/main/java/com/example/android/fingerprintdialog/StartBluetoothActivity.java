package com.example.android.fingerprintdialog;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.fingerprintdialog.resources.bluetoothConnect;

import java.util.Set;

public class StartBluetoothActivity extends Activity {

    public static BluetoothAdapter mBluetoothAdapter;
    private final static int REQUEST_ENABLE_BT = 1;
    public ArrayAdapter<String> BTArrayAdapter;
    public Set<BluetoothDevice> pairedDevices;
    private ListView btlv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_bluetooth);

     //   CheckBluetoothState();
        initVariables();
    }


    private void initVariables() {
        BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        btlv = (ListView) findViewById(R.id.lv);
        btlv.setAdapter(BTArrayAdapter);
        btlv.setOnItemClickListener(new btlvListener());

        Button btConnect = (Button) findViewById(R.id.btConnect);
        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // listDevices();
                // CHANGE ACTIVITY
                Intent myIntent = new Intent(StartBluetoothActivity.this, MainActivity.class);
                //to be able to parse data over to the next activity
                // myIntent.putExtra("key", value); //Optional parameters
                StartBluetoothActivity.this.startActivity(myIntent);

            }
        });
    }

            private class btlvListener implements AdapterView.OnItemClickListener {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // ListView Clicked item index
                    int itemPosition = position;

                    String info =   BTArrayAdapter.
                            getItem(itemPosition);
                    int numPoints = 0, i;
                    for(i=info.length()-1;i>0;i--){
                        if(info.charAt(i)==':'){
                            numPoints++;
                            if (numPoints == 5) {
                                i -= 3;
                                break;
                            }
                        }
                    }
                    String newinfo = info.substring(i).replaceAll(" ", "");
                    Log.d("TEST", newinfo);
                    startBluetoothThread(newinfo);

                   // CHANGE ACTIVITY
                    Intent myIntent = new Intent(StartBluetoothActivity.this, MainActivity.class);
                   //to be able to parse data over to the next activity
                   // myIntent.putExtra("key", value); //Optional parameters
                    StartBluetoothActivity.this.startActivity(myIntent);

                   Log.d("TEST", newinfo);
                }
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
