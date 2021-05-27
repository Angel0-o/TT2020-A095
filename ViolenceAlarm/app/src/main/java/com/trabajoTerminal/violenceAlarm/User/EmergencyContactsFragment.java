package com.trabajoTerminal.violenceAlarm.User;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.trabajoTerminal.violenceAlarm.Common.FirebaseAdapter;
import com.trabajoTerminal.violenceAlarm.DataModel.Contact;
import com.trabajoTerminal.violenceAlarm.R;
import com.trabajoTerminal.violenceAlarm.databinding.FragmentEmergencyContactsBinding;

public class EmergencyContactsFragment extends Fragment {
    private FragmentEmergencyContactsBinding binding;
    private RecyclerView recyclerView;
    private FirebaseAdapter adapter;
    TextInputLayout phoneNumber;
    TextInputLayout contactName;
    //Strings
    String userID;
    String _contactname;
    String _phonenumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_emergency_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentEmergencyContactsBinding.bind(view);
        if (getArguments() != null) {
            userID = getArguments().getString("userID");
        }
        //Bundle
        final Bundle args = new Bundle();
        args.putString("userID", userID);
        //Recycler style
        recyclerView = binding.recyclerViewContact;
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        //Firebase
        Query userData = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("contacts");
        FirebaseRecyclerOptions<Contact> options =
                new FirebaseRecyclerOptions.Builder<Contact>()
                        .setQuery(userData, Contact.class)
                        .build();
        //Adapter
        adapter = new FirebaseAdapter(options);
        recyclerView.setAdapter(adapter);
        binding.imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(EmergencyContactsFragment.this).navigate(R.id.action_emergencyContactsFragment_to_mainScreen,args);
            }
        });
        binding.addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewContactDialog();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void addNewContactDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View contactPopupView = LayoutInflater.from(getContext()).inflate(R.layout.popup_contact,null);
        phoneNumber = contactPopupView.findViewById(R.id.popup_phone_number);
        contactName = contactPopupView.findViewById(R.id.popup_contact_name);
        Button popupNewContact = contactPopupView.findViewById(R.id.popup_save);
        Button popupCancel = contactPopupView.findViewById(R.id.popup_cancel);
        builder.setView(contactPopupView);
        final AlertDialog dialog = builder.create();
        dialog.show();
        //Listeners
        popupNewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateContactName() | !validatePhoneNumber())
                    return;
                FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
                DatabaseReference reference = dataBase.getReference("Users");
                //Object User
                Contact contact = new Contact(_contactname,_phonenumber);
                reference.child(userID).child("contacts").child(_phonenumber).setValue(contact);
                dialog.dismiss();
                Toast.makeText(getContext(),"Contacto añadido",Toast.LENGTH_LONG).show();
            }
        });
        popupCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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
}