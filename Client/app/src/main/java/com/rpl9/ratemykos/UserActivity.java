package com.rpl9.ratemykos;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        ImageView profilePicture = findViewById(R.id.profile_picture);
        TextView username = findViewById(R.id.username);
        Button editProfile = findViewById(R.id.edit_profile);
        EditText name = findViewById(R.id.name);
        EditText email = findViewById(R.id.email);
        EditText phone = findViewById(R.id.phone);
        TextView type = findViewById(R.id.Type);
        TextView chooseType = findViewById(R.id.Choose_type);
        Button saveChanges = findViewById(R.id.save_changes);

        // When the Edit Profile button is clicked, the fields become editable
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setEnabled(true);
                email.setEnabled(true);
                phone.setEnabled(true);
            }
        });

        // When the Save Changes button is clicked, a toast message is displayed
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
