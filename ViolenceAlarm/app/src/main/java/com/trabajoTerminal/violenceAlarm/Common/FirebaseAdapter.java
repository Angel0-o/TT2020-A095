package com.trabajoTerminal.violenceAlarm.Common;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.trabajoTerminal.violenceAlarm.DataModel.Contact;
import com.trabajoTerminal.violenceAlarm.R;

public class FirebaseAdapter extends FirebaseRecyclerAdapter<Contact,FirebaseAdapter.viewHolder> {

    public FirebaseAdapter(@NonNull FirebaseRecyclerOptions<Contact> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final viewHolder holder, final int position, @NonNull final Contact model) {
        //Do
        holder.contactName.setText(model.getContactName());
        holder.contactPhone.setText(model.getPhoneNumber());
        //Firebase
        final String ID = getRef(position).getKey();
        final String parentID = getRef(position).getParent().getParent().getKey();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(holder.itemView.getContext(),"ID: " + model.getPhoneNumber(),Toast.LENGTH_LONG).show();
            }
        });
        holder.removeContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getItemCount() == 1)
                    Toast.makeText(holder.itemView.getContext(),"Necesitas almenos un contacto ",Toast.LENGTH_LONG).show();
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setMessage("El contacto se eliminara definitivamente").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(holder.itemView.getContext(),"Contacto eliminado",Toast.LENGTH_LONG).show();
                            getRef(position).removeValue();
                        }
                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            }
        });
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item,parent,false);
        return new viewHolder(view);
    }

    class viewHolder extends RecyclerView.ViewHolder {
        //View elements
        ImageView removeContact;
        TextView contactName;
        TextView contactPhone;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contact_name_item);
            contactPhone = itemView.findViewById(R.id.contact_phone_item);
            removeContact = itemView.findViewById(R.id.btn_remove_contact);
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

}
