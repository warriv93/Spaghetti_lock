package com.example.simon.spaghettilock.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.simon.spaghettilock.MainActivity;
import com.example.simon.spaghettilock.R;
import com.example.simon.spaghettilock.resources.ConnectedThread;


/**
 * After successfully pairing pc and phone this fragment will show the user 2 choices
 * Unlock pc with password or fingerprint
 * A simple {@link Fragment} subclass.
 */
public class welcome extends Fragment {
    private ConnectedThread ct;
    private MainActivity ma;

    public welcome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
       //init all variables and button onclick
        init(view);
        return view;
    }

    /**
     * change to either password fragment or fingerprint fragment depending on what button is pressed
     * @param view
     */
    private void init(View view) {
        ma = (MainActivity) getActivity();
        //set background background
        RelativeLayout welcomeLayout = (RelativeLayout) view.findViewById(R.id.welcomeLayout);
        welcomeLayout.setBackgroundColor(Color.WHITE);
        //setup buttons
        Button pwbt = (Button) view.findViewById(R.id.startPw);
        pwbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //  change fragment
                ma.createpwFrag(ct);
            }
        });
        Button fingerbt = (Button) view.findViewById(R.id.startFinger);
        fingerbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragContainer, new fingerfrag());
                ft.commit();*/
                Toast.makeText(ma, "You need to use Android M to use this feature!", Toast.LENGTH_SHORT).show();
                //on click!
            }
        });
    }

    /**
     *set ConnectedThread obj
     * @param ct
     */
    public void setCt(ConnectedThread ct) {
        this.ct = ct;
    }

    /**
     * set MainActivity obj
     * @param ma

    public void setMa(MainActivity ma) {
        this.ma = ma;
    }  */
}
