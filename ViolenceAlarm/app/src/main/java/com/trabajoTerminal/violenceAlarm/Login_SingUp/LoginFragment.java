package com.trabajoTerminal.violenceAlarm.Login_SingUp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trabajoTerminal.violenceAlarm.R;
import com.trabajoTerminal.violenceAlarm.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {
    SharedPreferences userPreferences;
    private FragmentLoginBinding binding;
    //Variables
    TextInputLayout email;
    TextInputLayout password;
    CheckBox rememberme;
    //Strings
    String _email;
    String _password;
    String userID;
    //Firebase Instance
    Query checkUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userPreferences = getActivity().getSharedPreferences("userData", Context.MODE_PRIVATE);
        binding = FragmentLoginBinding.bind(view);
        //
        email = binding.loginEmail;
        password = binding.loginPassword;
        rememberme = binding.checkboxRememberme;
        binding.btnSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_loginFragment_to_signUpFragment);
            }
        });
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateEmail() | !validatePassword())
                    return;
                checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("email").equalTo(_email);
                checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                    email.setError(null);
                    email.setErrorEnabled(false);
                            //Verifying password
                            String sysPassword = snapshot.child(userID).child("password").getValue(String.class);
                            if(!sysPassword.equals(_password)){
                                password.setError("Contraseña incorrecta");
                                password.setErrorEnabled(true);
                            }
                        }else {
                            email.setError("Usuario no registrado");
                            email.setErrorEnabled(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getView().getContext(),"Error: " + error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
                Bundle args = new Bundle();
                args.putString("userID", userID);
                NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_loginFragment_to_mainScreen,args);
            }
        });
        rememberme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){}
            }
        });
    }

    //Validate Functions
    private boolean validateEmail() {
        _email = email.getEditText().getText().toString().trim();
        String checkFormat = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";

        if (_email.isEmpty()) {
            email.setError("Campo vacio");
            return false;
        } else if (!_email.matches(checkFormat)) {
            email.setError("Email invalido");
            return false;
        } else {
            userID = encodeEmail(_email);
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        _password = password.getEditText().getText().toString().trim();
        String checkFormat = "^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";

        if (_password.isEmpty()) {
            password.setError("Campo vacio");
            return false;
        } else if (!_password.matches(checkFormat)) {
            password.setError("La contraseña debe tener al menos 4 caracteres, un número y sin espacios");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    //Encode Functions
    public String encodeEmail(String email) {
        // Convert ASCII string to char array
        char[] ch = email.toCharArray();

        StringBuilder builder = new StringBuilder();
        for (char c : ch) {
            String hexCode=String.format("%H", c);
            builder.append(hexCode);
        }
        return builder.toString();
    }
}