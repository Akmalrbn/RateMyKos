package com.rpl9.ratemykos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.rpl9.ratemykos.model.Account;
import com.rpl9.ratemykos.model.Facility;
import com.rpl9.ratemykos.model.Kos;
import com.rpl9.ratemykos.model.Kos_type;
import com.rpl9.ratemykos.request.BaseApiService;
import com.rpl9.ratemykos.request.UtilsApi;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateKosActivity extends AppCompatActivity {

    private EditText KosName;
    private EditText KosLocation;
    private EditText KosDescription;
    private TextView KosLatitude;
    private TextView KosLongitude;
    private Button MapsButton;
    private Button CreateButton;
    private String name, location, description, kos_type;
    private double lati, longi;
    BaseApiService mApiService;
    Context mContext;
    ArrayList<Facility> facilities = new ArrayList<Facility>();

    private final ActivityResultLauncher<Intent> mapLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::onMapActivityResult
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_kos);
        mApiService = UtilsApi.getApiService();
        mContext = this;
        KosName = findViewById(R.id.createKosName);
        KosLocation = findViewById(R.id.createKosAddress);
        KosLatitude = findViewById(R.id.createLat);
        KosLongitude = findViewById(R.id.createLong);
        KosDescription = findViewById(R.id.createKosDescription);
        MapsButton = findViewById(R.id.setLatLong);
        CreateButton = findViewById(R.id.createKosButton);
        CheckBox WiFi = findViewById(R.id.createWifi);
        CheckBox AC = findViewById(R.id.createAC);
        CheckBox Bathroom = findViewById(R.id.createBathroom);
        CheckBox Refrigerator = findViewById(R.id.createRefrigerator);
        CheckBox Kitchen = findViewById(R.id.createKitchen);
        Spinner KosType = (Spinner) findViewById(R.id.createType);
        KosType.setAdapter((new ArrayAdapter<Kos_type>(this, android.R.layout.simple_spinner_dropdown_item, Kos_type.values())));
        MapsButton.setOnClickListener(view -> onGoToMapButtonClick());
        CreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (WiFi.isChecked()){
                    facilities.add(Facility.WiFi);
                }
                if (AC.isChecked()){
                    facilities.add(Facility.AC);
                }
                if (Bathroom.isChecked()){
                    facilities.add(Facility.Bathroom);
                }
                if (Refrigerator.isChecked()){
                    facilities.add(Facility.Refrigerator);
                }
                if (Kitchen.isChecked()){
                    facilities.add(Facility.Kitchen);
                }
                name = KosName.getText().toString();
                location = KosLocation.getText().toString();
                description = KosDescription.getText().toString();
                kos_type = KosType.getSelectedItem().toString();
                createKos();
            }
        });
    }

    private void onGoToMapButtonClick() {
        Intent intent = new Intent(this, MapsActivity.class);
        mapLauncher.launch(intent);
    }

    private void onSubmitButtonClick() {
        // Your submission logic here
    }

    private void onMapActivityResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                lati = data.getDoubleExtra("SELECTED_LATITUDE", 0.0);
                longi = data.getDoubleExtra("SELECTED_LONGITUDE", 0.0);

                // Set the values in the TextView fields
                KosLatitude.setText(String.valueOf(lati));
                KosLongitude.setText(String.valueOf(longi));
            }
        }
    }
//    private void createKos() {
//        Call<Kos> call = mApiService.addkosnofacilities(name, location, lati, longi, description);
//        call.enqueue(new Callback<Kos>() {
//            @Override
//            public void onResponse(Call<Kos> call, Response<Kos> response) {
//                if (response.isSuccessful()) {
//                    Intent move = new Intent(CreateKosActivity.this, HomeActivity.class);
//                    startActivity(move);
//                } else {
//                    // Handle the error response, e.g., display an error message
//                    handleErrorResponse(response);
//                }
//            }
//            @Override
//            public void onFailure(Call<Kos> call, Throwable t) {
//                // Handle network or request failure
//                Log.e("CreateKos", "Request failed", t);
//                showToast("Network or request failure. Please try again.");
//                t.printStackTrace();
//
//            }
//        });
//    }

    private void createKos() {
        Call<Kos> call = mApiService.addkos(name, location, lati, longi, description, facilities, kos_type);
        call.enqueue(new Callback<Kos>() {
            @Override
            public void onResponse(Call<Kos> call, Response<Kos> response) {
                if (response.isSuccessful()) {
                    Intent move = new Intent(CreateKosActivity.this, HomeActivity.class);
                    startActivity(move);
                } else {
                    // Handle the error response, e.g., display an error message
                    handleErrorResponse(response);
                }
            }
            @Override
            public void onFailure(Call<Kos> call, Throwable t) {
                // Handle network or request failure
                Log.e("CreateKos", "Request failed", t);
                showToast("Network or request failure. Please try again.");
                t.printStackTrace();
            }
        });
    }

    private void handleErrorResponse(Response<Kos> response) {
        if (response.code() == 401) {
            // Unauthorized, show error message
            showToast("Invalid credentials");
        } else {
            // Handle other error codes or display a generic error message
            showToast("Error: " + response.message());
        }
    }
    private void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
}
