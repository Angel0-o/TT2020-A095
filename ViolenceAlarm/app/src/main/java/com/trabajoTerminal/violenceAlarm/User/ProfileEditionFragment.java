package com.trabajoTerminal.violenceAlarm.User;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trabajoTerminal.violenceAlarm.R;
import com.trabajoTerminal.violenceAlarm.databinding.FragmentProfileEditionBinding;

import java.util.Calendar;

public class ProfileEditionFragment extends Fragment {
    private FragmentProfileEditionBinding binding;
    //Variables
    TextInputLayout email;
    TextInputLayout password;
    TextInputLayout passwordconfirm;
    TextInputLayout name;
    TextInputLayout lastname;
    int radioID;
    RadioButton male;
    RadioButton female;
    RadioButton other;
    DatePicker datePicker;
    //Strings
    String userID;
    String _email;
    String _password;
    String _passwordconfirm;
    String _name;
    String _lastname;
    String _gender;
    String _birthdate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_edition, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentProfileEditionBinding.bind(view);
        //Recover userID
        if (getArguments() != null) {
            userID = getArguments().getString("userID");
        }
        //Building Bundle
        final Bundle args = new Bundle();
        args.putString("userID", userID);
        //Fields
        email = binding.profileEmail;
        password = binding.profilePassword;
        passwordconfirm = binding.profilePasswordConfirm;
        name = binding.profileName;
        lastname = binding.profileLastname;
        male = binding.rdbtnMale;
        female = binding.rdbtnFemale;
        other = binding.rdbtnOther;
        datePicker = binding.profileDatePicker;
        //Call functions
        updateUI();
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validatePassword() | !validateName() | !validateLastName() | !validateAge() | !validateGender())
                    return;
                updateUser();
                NavHostFragment.findNavController(ProfileEditionFragment.this).navigate(R.id.action_profileEditionFragment_to_mainScreen,args);
            }
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(ProfileEditionFragment.this).navigate(R.id.action_profileEditionFragment_to_mainScreen,args);
            }
        });
        binding.imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(ProfileEditionFragment.this).navigate(R.id.action_profileEditionFragment_to_mainScreen,args);
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

    private boolean validatePassword() {
        _password = password.getEditText().getText().toString().trim();
        _passwordconfirm = passwordconfirm.getEditText().getText().toString().trim();
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
        }else if (_passwordconfirm.isEmpty()) {
            passwordconfirm.setError("Campo vacio");
            return false;
        } else if (!_passwordconfirm.matches(checkFormat)) {
            passwordconfirm.setError("La contraseña debe tener al menos 4 caracteres, un número y sin espacios");
            return false;
        } else if(!_password.equals(_passwordconfirm)){
            passwordconfirm.setError("Las contraseñas no coinciden");
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
            Toast.makeText(getContext(), "Please Select Gender", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateAge() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int userAge = datePicker.getYear();
        int isAgeValid = currentYear - userAge;
        //Date
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        month = month == 12 ? month : month + 1;
        _birthdate = day + "/" + month + "/" + year;

        if (isAgeValid < 12) {
            Toast.makeText(getContext(), "La edad minima es 13 años", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

    //Firebase functions
    private void updateUser(){
        //Database
        FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
        DatabaseReference reference = dataBase.getReference("Users");
        //Object User
        reference.child(userID).child("password").setValue(_password);
        reference.child(userID).child("name").setValue(_name);
        reference.child(userID).child("lastName").setValue(_lastname);
        reference.child(userID).child("gender").setValue(_gender);
        reference.child(userID).child("birthDate").setValue(_birthdate);
        Toast.makeText(getContext(),"Datos guardados ",Toast.LENGTH_LONG).show();
    }

    private void updateUI(){
        //Database
        Query userData = FirebaseDatabase.getInstance().getReference("Users").orderByKey().equalTo(userID);

        userData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    _email = dataSnapshot.child(userID).child("email").getValue(String.class);
                    _password = dataSnapshot.child(userID).child("password").getValue(String.class);
                    _name = dataSnapshot.child(userID).child("name").getValue(String.class);
                    _lastname = dataSnapshot.child(userID).child("lastName").getValue(String.class);
                    _gender = dataSnapshot.child(userID).child("gender").getValue(String.class);
                    _birthdate = dataSnapshot.child(userID).child("birthDate").getValue(String.class);
                    //Setting data
                    email.getEditText().setText(_email);
                    password.getEditText().setText(_password);
                    passwordconfirm.getEditText().setText(_password);
                    name.getEditText().setText(_name);
                    lastname.getEditText().setText(_lastname);
                    if("Masculino".equals(_gender))
                        male.setChecked(true);
                    else if("Femenino".equals(_gender))
                        female.setChecked(true);
                    else if("Otro".equals(_gender))
                        other.setChecked(true);
                    String[] fullDate = _birthdate.split("/");
                    int day = Integer.parseInt(fullDate[0]);
                    int month = Integer.parseInt(fullDate[1]);
                    month = month == 1 ? month : month - 1;
                    int year = Integer.parseInt(fullDate[2]);
                    datePicker.updateDate(year,month,day);
                }else {
                    Toast.makeText(getContext(),"Error critico",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}