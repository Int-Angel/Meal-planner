package com.example.mealplanner.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.mealplanner.LoginActivity;
import com.example.mealplanner.R;
import com.parse.ParseUser;


public class ProfileFragment extends Fragment {

    private final static String TAG = "ProfileFragment";

    private TextView tvUsername;
    private TextView tvName;
    private TextView tvLastname;
    private Switch swIsPublic;
    private Button btnLogout;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvUsername = view.findViewById(R.id.tvUsername);
        tvName = view.findViewById(R.id.tvName);
        tvLastname = view.findViewById(R.id.tvLastname);
        swIsPublic = view.findViewById(R.id.swIsPublic);
        btnLogout = view.findViewById(R.id.btnLogout);

        bind();
        setUpListeners();
    }

    private void bind(){
        swIsPublic.setChecked(ParseUser.getCurrentUser().getBoolean("isPublic"));
    }

    private void setUpListeners(){
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        swIsPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }

    private void logout(){
        ParseUser.logOut();

        Intent intent = new Intent(getContext(), LoginActivity.class);
        getContext().startActivity(intent);
        getActivity().finish();
    }
}