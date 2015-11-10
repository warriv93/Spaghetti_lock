package com.example.simon.spaghettilock.fragments;


import android.app.FragmentManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.simon.spaghettilock.MainActivity;
import com.example.simon.spaghettilock.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class fingerfrag extends Fragment {


    public fingerfrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fingerfrag, container, false);
        inti(view);
        return view;
    }

    private void inti(View view) {
        ImageView ib = (ImageView) view.findViewById(R.id.imageViewFinger);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(((MainActivity) getActivity()), "Place your finger on the scanner!", Toast.LENGTH_SHORT).show();
            }

        });
    }

}
