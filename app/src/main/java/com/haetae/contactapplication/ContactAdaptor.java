package com.haetae.contactapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ContactAdaptor extends ArrayAdapter<Contact> {
    public ContactAdaptor(@NonNull Context context, int resource, @NonNull List<Contact> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.contact_item, parent, false);
        Contact contact = getItem(position);
        TextView text = view.findViewById(R.id.contact_info);
        text.setText(contact.toString());
        return view;
    }
}
