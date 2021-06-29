package com.example.teamsclone.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.teamsclone.R;


public class Meet extends Fragment {

    public Meet() {
        // Required empty public constructor
    }

    Button testing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_meet, container, false);

        //In fragments we use view along with findViewById
        testing=view.findViewById(R.id.test);
        testing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //While working with we give getContext() instead of activity name wherever required
                Toast.makeText(getContext(),"Done",Toast.LENGTH_LONG).show();
            }
        });

        return  view;
    }

}