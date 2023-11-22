package com.rpl9.ratemykos;


import static com.rpl9.ratemykos.HomeActivity.kosView;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.rpl9.ratemykos.model.Facility;

public class KosDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Listkos", "Masuk" + kosView.name);
        setContentView(R.layout.activity_kos_detail);
        TextView name = findViewById(R.id.detailKosTitle);
        TextView location = findViewById(R.id.detailLocationText);
//        TextView size = findViewById(R.id.detailSizeText);
//        TextView price = findViewById(R.id.detailPriceText);
        CheckBox WiFi = findViewById(R.id.Wifi);
        CheckBox AC = findViewById(R.id.AC);
        CheckBox Bathroom = findViewById(R.id.Bathroom);
        CheckBox Refrigerator = findViewById(R.id.Refrigerator);
        CheckBox Kitchen = findViewById(R.id.Kitchen);
        name.setText(kosView.name);
        location.setText(kosView.location);

        if (kosView.facilities != null) {
            if (kosView.facilities.contains(Facility.WiFi)) {
                WiFi.setChecked(true);
            }
            if (kosView.facilities.contains(Facility.AC)) {
                AC.setChecked(true);
            }
            if (kosView.facilities.contains(Facility.Bathroom)) {
                Bathroom.setChecked(true);
            }
            if (kosView.facilities.contains(Facility.AC)) {
                AC.setChecked(true);
            }
            if (kosView.facilities.contains(Facility.Kitchen)) {
                Kitchen.setChecked(true);
            }
            if (kosView.facilities.contains(Facility.Refrigerator)) {
                Refrigerator.setChecked(true);
            }
        }
        Button payment = findViewById(R.id.bookButton);
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent move = new Intent(KosDetailActivity.this, BookActivity.class);
                startActivity(move);
            }
        });
    }
}