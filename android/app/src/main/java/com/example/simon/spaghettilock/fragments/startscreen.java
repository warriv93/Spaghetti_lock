package com.example.simon.spaghettilock.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.simon.spaghettilock.MainActivity;
import com.example.simon.spaghettilock.R;

/**
 * This Fragment is the first fragment that is showen to the user.
 */
public class startscreen extends Fragment {
    private ListView btlv;
    private MainActivity ma;

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

    /**
     * Init all the needed components for this fragment
     * @param view
     */
    private void init(View view) {
        ma = (MainActivity) getActivity();
        //set background background
        RelativeLayout startLayout = (RelativeLayout) view.findViewById(R.id.startLayout);
        startLayout.setBackgroundColor(Color.WHITE);

        if (view != null) {
            InputMethodManager imm = (InputMethodManager)ma.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        btlv = (ListView) view.findViewById(R.id.lv);
        btlv.setAdapter(ma.BTArrayAdapter);
        btlv.setOnItemClickListener(new btlvListener());

        Button btConnect = (Button) view.findViewById(R.id.btConnect);
        //on button click create list and list all paired devices
        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ma.listDevices();
            }
        });
    }

    /**
     * on list item click send device address and start bluetooth listening thread
     */
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
            ma.startBluetoothThread(newinfo);
        }
    }
}
