package com.trabajoTerminal.violenceAlarm.ExtraClasses;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trabajoTerminal.violenceAlarm.R;
import com.trabajoTerminal.violenceAlarm.databinding.FragmentAboutBinding;

public class AboutFragment extends Fragment {
    private FragmentAboutBinding binding;
    //Strings
    String userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentAboutBinding.bind(view);
        if (getArguments() != null) {
            userID = getArguments().getString("userID");
        }
        //Bundle
        final Bundle args = new Bundle();
        args.putString("userID", userID);
        binding.imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AboutFragment.this).navigate(R.id.action_aboutFragment_to_mainScreen,args);
            }
        });
    }
}