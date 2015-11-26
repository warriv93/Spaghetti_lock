package com.example.simon.spaghettilock.fragments;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.simon.spaghettilock.LoginActivity;
import com.example.simon.spaghettilock.MainActivity;
import com.example.simon.spaghettilock.R;


/**
 * After successfully pairing pc and phone this fragment will show the user 2 choices
 * Unlock pc with password or fingerprint
 * A simple {@link Fragment} subclass.
 */
public class welcome extends Fragment {
    MainActivity ma;

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

    private void init(View view) {
        Button pwbt = (Button) view.findViewById(R.id.startPw);
        pwbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //what to do onClick?
//                //change to ACTIVITY
//                Intent myIntent = new Intent(((MainActivity) getActivity()), LoginActivity.class);
////              send something to other activity
////               myIntent.putExtra("key", value); //Optional parameters
//                ((MainActivity) getActivity()).startActivity(myIntent);
//                change fragment
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragContainer, new pwfrag());
                ft.commit();
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


                //on cick!



            }
        });
    }


}
