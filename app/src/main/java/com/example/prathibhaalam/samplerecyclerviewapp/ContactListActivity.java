package com.example.prathibhaalam.samplerecyclerviewapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.example.prathibhaalam.samplerecyclerviewapp.AddANewCatActivity.CAT_NAME;
import static com.example.prathibhaalam.samplerecyclerviewapp.AddANewCatActivity.IMAGE_URI;

public class ContactListActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 1;
    private static final int ADD_A_NEW_CAT_REQUEST = 2;

    ContactsAdapter adapter;
    RecyclerView contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        } else {
            loadAndDisplayData();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST && grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
            loadAndDisplayData();
        } else {
            Toast.makeText(this, "Need permission to read external storage", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_A_NEW_CAT_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = Uri.parse(data.getStringExtra(IMAGE_URI));
            adapter.addNewContact(new Contact(uri, data.getStringExtra(CAT_NAME), "1 day ago", "offline"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_cat:
                Intent intent = new Intent(this, AddANewCatActivity.class);
                startActivityForResult(intent, ADD_A_NEW_CAT_REQUEST);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadAndDisplayData() {
        contactList = findViewById(R.id.contact_list);
        ArrayList<Contact> contacts = createMyCatFriendsList(100);
        adapter = new ContactsAdapter(this, contacts);
        contactList.setAdapter(adapter);
        contactList.setLayoutManager(new LinearLayoutManager(this));
    }

    private ArrayList<Contact> createMyCatFriendsList(int numCats) {
        ArrayList<Contact> contacts = new ArrayList<>(numCats);
        for (int i=0; i < numCats; i++) {
            int randomInt = new Random().nextInt(20);
            contacts.add(new Contact(Uri.fromFile(new File(Environment.getExternalStorageDirectory()+"/Pictures/cats/download"+randomInt+".jpeg")), "Cat "+i, randomInt+" minutes ago", "offline"));
        }
        return contacts;
    }

    static class Contact {
        Uri imageUri;
        String name;
        String seen;
        String status;

        Contact(Uri imageUri, String name, String seen, String status) {
            this.imageUri = imageUri;
            this.name = name;
            this.seen = seen;
            this.status = status;
        }

    }

    static class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

        Context context;
        ArrayList<Contact> contacts;


        ContactsAdapter(Context context, ArrayList<Contact> contacts) {
            this.context = context;
            this.contacts = contacts;
        }

        void addNewContact(Contact contact) {
            contacts.add(0,contact);
            notifyItemInserted(0);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Contact contact = contacts.get(position);
            Picasso.with(context).load(contact.imageUri).placeholder(R.drawable.cat).into(holder.contactPhoto);
            holder.contactName.setText(contact.name);
            holder.contactSeen.setText(contact.seen);
            holder.contactStatus.setText(contact.status);
        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView contactPhoto;
            TextView contactName;
            TextView contactSeen;
            TextView contactStatus;

            ViewHolder(View itemView) {
                super(itemView);
                contactPhoto = itemView.findViewById(R.id.cat_photo);
                contactName = itemView.findViewById(R.id.cat_name);
                contactSeen = itemView.findViewById(R.id.contact_seen);
                contactStatus = itemView.findViewById(R.id.contact_status);
            }
        }
    }
}
