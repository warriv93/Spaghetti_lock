package com.example.simon.spaghettilock.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.simon.spaghettilock.MainActivity;
import com.example.simon.spaghettilock.R;
import com.example.simon.spaghettilock.resources.ConnectedThread;
import com.example.simon.spaghettilock.resources.bluetoothConnect;


public class startscreen extends Fragment {
    private ListView btlv;
    ConnectedThread ct;
    public startscreen() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_startscreen2, container, false);

        init(view);
        return view;
    }

    private void init(View view) {
        btlv = (ListView) view.findViewById(R.id.lv);
        btlv.setAdapter(((MainActivity) getActivity()).BTArrayAdapter);
        btlv.setOnItemClickListener(new btlvListener());


        Button btConnect = (Button) view.findViewById(R.id.btConnect);
        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).listDevices();

//                    // move to next fragment

            }
        });
    }

    private class btlvListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            // ListView Clicked item index
            int itemPosition = position;

          String info =   ((MainActivity) getActivity()).BTArrayAdapter.
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
            ((MainActivity) getActivity()).startBluetoothThread(newinfo);

        }
    }
}
