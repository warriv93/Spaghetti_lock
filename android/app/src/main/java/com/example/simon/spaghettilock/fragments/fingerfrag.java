package com.example.simon.spaghettilock.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.example.simon.spaghettilock.MainActivity;
import com.example.simon.spaghettilock.R;


/**
 * This fragment is a holder for the fingerprint functionality
 * A simple {@link Fragment} subclass.
 */
public class fingerfrag extends Fragment {
    private MainActivity ma;

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
    /**
     * Init all the needed components for this fragment
     * @param view
     */
    private void inti(View view) {
        ma = (MainActivity) getActivity();
        //set background background
        RelativeLayout fingerLayout = (RelativeLayout) view.findViewById(R.id.fingerLayout);
        fingerLayout.setBackgroundColor(Color.WHITE);
        ImageView ib = (ImageView) view.findViewById(R.id.imageViewFinger);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ma, "Place your finger on the scanner!", Toast.LENGTH_SHORT).show();
            }

        });
    }
}
