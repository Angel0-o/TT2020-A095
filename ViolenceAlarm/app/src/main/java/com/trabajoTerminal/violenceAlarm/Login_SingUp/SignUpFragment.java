package com.trabajoTerminal.violenceAlarm.Login_SingUp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trabajoTerminal.violenceAlarm.R;
import com.trabajoTerminal.violenceAlarm.databinding.FragmentSignUpBinding;

public class SignUpFragment extends Fragment {
    private FragmentSignUpBinding binding;
    TextInputLayout email;
    TextInputLayout password;
    TextInputLayout name;
    TextInputLayout lastname;
    RadioButton selectGender;
    RadioButton male;
    RadioButton female;
    RadioButton other;
    int radioID;
    //Strings
    String _email;
    String _password;
    String _name;
    String _lastname;
    String _gender;
    String _date;
    String _contactname;
    String _phonenumber;
    String userID;
    //Firebase Instance
    Query checkUser;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("userID", userID);
        outState.putString("email", _email);
        outState.putString("password", _password);
        outState.putString("name", _name);
        outState.putString("lastname", _lastname);
        outState.putString("gender", _gender);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentSignUpBinding.bind(view);
        email = binding.signupEmail;
        password = binding.signupPassword;
        name = binding.signupName;
        lastname = binding.signupLastname;
        male = binding.rdbtnMale;
        female = binding.rdbtnFemale;
        other = binding.rdbtnOther;
        selectGender = new RadioButton(getContext());
        //Recovering Data
        if (getArguments() != null) {
            userID = getArguments().getString("userID");
            _email = getArguments().getString("email");
            _password = getArguments().getString("password");
            _name = getArguments().getString("name");
            _lastname = getArguments().getString("lastname");
            _gender = getArguments().getString("gender");
            _date = getArguments().getString("birthdate");
            _contactname = getArguments().getString("contactname");
            _phonenumber = getArguments().getString("phonenumber");
            email.getEditText().setText(_email);
            password.getEditText().setText(_password);
            name.getEditText().setText(_name);
            lastname.getEditText().setText(_lastname);
            if("Masculino".equals(_gender))
                male.setChecked(true);
            else if("Femenino".equals(_gender))
                female.setChecked(true);
            else if("Otro".equals(_gender))
                other.setChecked(true);
        }
        //Buttons
        binding.signupBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateName() | !validateLastName() | !validateEmail() | !validatePassword() | !validateGender())
                    return;
                if(male.isChecked())
                    _gender = male.getText().toString();
                else if(female.isChecked())
                    _gender = female.getText().toString();
                else if(other.isChecked())
                    _gender = other.getText().toString();
                userID = encodeEmail(_email);
                //Database
                checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("email").equalTo(_email);

                checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            binding.signupEmail.setError("Usuario ya registrado");
                            binding.signupEmail.setErrorEnabled(true);
                        }else {
                            binding.signupEmail.setError(null);
                            binding.signupEmail.setErrorEnabled(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
                Bundle args = new Bundle();
                args.putString("userID", userID);
                args.putString("email", _email);
                args.putString("password", _password);
                args.putString("name", _name);
                args.putString("lastname", _lastname);
                args.putString("gender", _gender);
                args.putString("birthdate", _date);
                args.putString("contactname", _contactname);
                args.putString("phonenumber", _phonenumber);
                NavHostFragment.findNavController(SignUpFragment.this).navigate(R.id.action_signUpFragment_to_signUp2,args);
            }
        });
        binding.signupBtnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(SignUpFragment.this).navigate(R.id.action_signUpFragment_to_loginFragment);
            }
        });
        binding.signupImageviewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(SignUpFragment.this).navigate(R.id.action_signUpFragment_to_loginFragment);
            }
        });
    }


    //ValidateForms Functions
    private boolean validateName() {
        _name = name.getEditText().getText().toString().trim();
        if (_name.isEmpty()) {
            name.setError("Campo vacio");
            return false;
        } else {
            name.setError(null);
            name.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateLastName() {
        _lastname = lastname.getEditText().getText().toString().trim();

        if (_lastname.isEmpty()) {
            lastname.setError("Campo vacio");
            return false;
        } else {
            lastname.setError(null);
            lastname.setErrorEnabled(false);
            return true;
        }
    }

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

    private boolean validateGender() {
        radioID = binding.rdGroup.getCheckedRadioButtonId();
        if (radioID == -1) {
            Toast.makeText(getContext(), "Porfavor selecciona un genero", Toast.LENGTH_SHORT).show();
            return false;
        } else {
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