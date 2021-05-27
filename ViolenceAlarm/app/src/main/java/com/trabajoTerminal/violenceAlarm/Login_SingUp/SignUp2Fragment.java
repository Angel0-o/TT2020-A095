package com.trabajoTerminal.violenceAlarm.Login_SingUp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trabajoTerminal.violenceAlarm.DataModel.Contact;
import com.trabajoTerminal.violenceAlarm.DataModel.User;
import com.trabajoTerminal.violenceAlarm.R;
import com.trabajoTerminal.violenceAlarm.databinding.FragmentSignUp2Binding;

import java.util.Calendar;

public class SignUp2Fragment extends Fragment {
    private FragmentSignUp2Binding binding;
    DatePicker datePicker;
    TextInputLayout phoneNumber;
    TextInputLayout contactName;
    //Strings
    String _email;
    String _password;
    String _name;
    String _lastname;
    String _gender;
    String _date;
    String _phonenumber;
    String _contactname;
    String userID;
    //Firebase Instance
    boolean response;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentSignUp2Binding.bind(view);
        datePicker = binding.datePicker;
        phoneNumber = binding.phoneNumber;
        contactName = binding.contactName;
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
            if(_date != null){
                String[] fullDate = _date.split("/");
                int day = Integer.parseInt(fullDate[0]);
                int month = Integer.parseInt(fullDate[1]);
                month = month == 1 ? month : month - 1;
                int year = Integer.parseInt(fullDate[2]);
                datePicker.updateDate(year,month,day);
            }if(_contactname != null){
                contactName.getEditText().setText(_contactname);
            }if (_phonenumber != null){
                phoneNumber.getEditText().setText(_phonenumber);}
        }
        final Bundle args = new Bundle();
        args.putString("userID", userID);
        args.putString("email", _email);
        args.putString("password", _password);
        args.putString("name", _name);
        args.putString("lastname", _lastname);
        args.putString("gender", _gender);
        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateContactName() | !validateAge() | !validatePhoneNumber())
                    return;
                storeNewUserData(userID);
                Toast.makeText(getContext(),"Registro exitoso",Toast.LENGTH_LONG).show();
                NavHostFragment.findNavController(SignUp2Fragment.this).navigate(R.id.action_signUp2_to_loginFragment);
            }
        });
        binding.previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();
                _date = day + "/" + month + "/" + year;
                _contactname = contactName.getEditText().getText().toString().trim();
                _phonenumber = phoneNumber.getEditText().getText().toString().trim();
                args.putString("birthdate", _date);
                args.putString("contactname", _contactname);
                args.putString("phonenumber", _phonenumber);
                NavHostFragment.findNavController(SignUp2Fragment.this).navigate(R.id.action_signUp2_to_signUpFragment,args);
            }
        });
        binding.signUp2Backview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();
                _date = day + "/" + month + "/" + year;
                _contactname = contactName.getEditText().getText().toString().trim();
                _phonenumber = phoneNumber.getEditText().getText().toString().trim();
                args.putString("birthdate", _date);
                args.putString("contactname", _contactname);
                args.putString("phonenumber", _phonenumber);
                NavHostFragment.findNavController(SignUp2Fragment.this).navigate(R.id.action_signUp2_to_signUpFragment,args);
            }
        });
    }

    //ValidateForms Functions

    private boolean validateContactName() {
        _contactname = contactName.getEditText().getText().toString().trim();

        if (_contactname.isEmpty()) {
            contactName.setError("Campo vacio");
            return false;
        } else {
            contactName.setError(null);
            contactName.setErrorEnabled(false);
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

        _date = day + "/" + month + "/" + year;

        if (isAgeValid < 12) {
            Toast.makeText(getContext(), "La edad minima es 13 años", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

    private boolean validatePhoneNumber() {
        _phonenumber = phoneNumber.getEditText().getText().toString().trim();
        String checkFormat = ".{10}";
        if (_phonenumber.isEmpty()) {
            phoneNumber.setError("Campo vacio");
            return false;
        } else if (!_phonenumber.matches(checkFormat)) {
            phoneNumber.setError("Número invalido");
            return false;
        } else {
            phoneNumber.setError(null);
            phoneNumber.setErrorEnabled(false);
            return true;
        }
    }

    //Firebase Store Functions
    private void storeNewUserData(String emailID) {
        FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
        DatabaseReference reference = dataBase.getReference("Users");
        //Object User
        User user = new User(_email,_password,_name,_lastname,_gender,_date);
        reference.child(userID).setValue(user);
        Contact contact = new Contact(_contactname,_phonenumber);
        reference.child(userID).child("contacts").child(_phonenumber).setValue(contact);
    }
}