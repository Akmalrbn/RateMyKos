package com.rpl9.ratemykos;

import static com.rpl9.ratemykos.LoginActivity.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rpl9.ratemykos.model.Account;
import com.rpl9.ratemykos.model.Facility;
import com.rpl9.ratemykos.model.Kos;
import com.rpl9.ratemykos.model.Kos_type;
import com.rpl9.ratemykos.request.BaseApiService;
import com.rpl9.ratemykos.request.UtilsApi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {
    MenuItem addKos;
    BaseApiService mApiService;
    Context mContext;
    public static ArrayList<Kos> arrayOfKos = new ArrayList<Kos>();
    public static ArrayList<Kos> KosList = new ArrayList<Kos>();
    public static List<String> suggestions = new ArrayList<String>();

    Spinner typeSpin, facilitySpin;
    KosAdapter adapter;
    int pageSize = 0;
    public static Kos kosView;
    private GoogleMap mMap;
    private MapFragment mapFragment;
    private boolean mapVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mApiService = UtilsApi.getApiService();
        mContext = this;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapHome);
        mapFragment.getMapAsync(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        typeSpin = (Spinner) findViewById(R.id.KosSortSpinner);
        typeSpin.setAdapter((new ArrayAdapter<Kos_type>(this, android.R.layout.simple_spinner_dropdown_item, Kos_type.values())));
        facilitySpin = (Spinner) findViewById(R.id.KosFacilitySpinner);
        facilitySpin.setAdapter((new ArrayAdapter<Facility>(this, android.R.layout.simple_spinner_dropdown_item, Facility.values())));
        ArrayAdapter<String> adapterSearch = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suggestions);
        AutoCompleteTextView searchBar = findViewById(R.id.searchHome);
        searchBar.setAdapter(adapterSearch);
        requestKos();
        getAll();
        adapter = new KosAdapter(this, arrayOfKos);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                kosView = (Kos) (listView.getItemAtPosition(position));
                Log.d("Listkos", "Tes Kos" + listView.getItemAtPosition(position));
                System.out.println(listView.getItemAtPosition(position));
                Intent move = new Intent(HomeActivity.this, KosDetailActivity.class);
                startActivity(move);
            }
        });
// Set up listeners
        typeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                filterKosList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                getAll();
            }
        });

        facilitySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                filterKosList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                getAll();
            }
        });
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // This method is called to notify you that characters within `charSequence` are about to be replaced with new text
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // This method is called to notify you that characters within `charSequence` have been replaced with new text
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // This method is called to notify you that somewhere within `editable` text has been changed
                // You can perform actions here based on the current text in the AutoCompleteTextView
                String newText = editable.toString();
                System.out.println(newText);
                // Clear the existing data in arrayOfKos
                arrayOfKos.clear();

                // If the text is empty, add all items from KosList
                if (newText.length() == 0 || newText == null) {
                    arrayOfKos.addAll(KosList);
                } else {
                    // Filter items based on the current text
                    for (Kos filter : KosList) {
                        if (filter.name.toLowerCase().contains(newText.toLowerCase())) {
                            arrayOfKos.add(filter);
                            System.out.println(newText.toLowerCase());
                        }
                    }
                }

                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged();
                resetMap();
            }
        });

    }
    private void filterKosList() {
        Kos_type selectedType = Kos_type.valueOf(typeSpin.getSelectedItem().toString());
        Facility selectedFacility = Facility.valueOf(facilitySpin.getSelectedItem().toString());

        if(Kos_type.valueOf(typeSpin.getSelectedItem().toString()) == Kos_type.All
                && Facility.valueOf(facilitySpin.getSelectedItem().toString()) == Facility.All){
            getAll();
            requestKos();
        } else if (Kos_type.valueOf(typeSpin.getSelectedItem().toString()) == Kos_type.All
                && Facility.valueOf(facilitySpin.getSelectedItem().toString()) != Facility.All) {
            Facility facilityFil = Facility.valueOf(facilitySpin.getSelectedItem().toString());
            arrayOfKos.clear();
            for (Kos filter : KosList) {
                if (filter.facilities.contains(facilityFil)) {
                    arrayOfKos.add(filter);
                }
            }
            adapter.notifyDataSetChanged();
            resetMap();
        } else if (Kos_type.valueOf(typeSpin.getSelectedItem().toString()) != Kos_type.All
                && Facility.valueOf(facilitySpin.getSelectedItem().toString()) == Facility.All) {
            Kos_type typeFil = Kos_type.valueOf(typeSpin.getSelectedItem().toString());
            arrayOfKos.clear();
            for (Kos filter : KosList) {
                if (filter.kos_type == typeFil) {
                    arrayOfKos.add(filter);
                }
            }
            adapter.notifyDataSetChanged();
            resetMap();
        } else {
            Kos_type typeFil = Kos_type.valueOf(typeSpin.getSelectedItem().toString());
            Facility facilityFil = Facility.valueOf(facilitySpin.getSelectedItem().toString());
            arrayOfKos.clear();
            for (Kos filter : KosList) {
                if (filter.kos_type == typeFil && filter.facilities.contains(facilityFil)) {
                    arrayOfKos.add(filter);
                }
            }
            adapter.notifyDataSetChanged();
            resetMap();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        addKos = menu.findItem(R.id.add_box);
//        SearchView search = (SearchView) menu.findItem(R.id.search_button).getActionView();
//        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                System.out.println(newText);
//                arrayOfKos.clear();
//                if (newText.length() == 0 || newText == null) {
//                    arrayOfKos.addAll(KosList);
//                } else {
//                    for (Kos filter : KosList) {
//                        if (filter.name.toLowerCase().contains(newText.toLowerCase())) {
//                            arrayOfKos.add(filter);
//                            System.out.println(newText.toLowerCase());
//                        }
//                    }
//                }
//                adapter.notifyDataSetChanged();
//                return false;
//            }
//        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.person){
            if(account == null){
                Intent move = new Intent(HomeActivity.this, LoginActivity.class);
                Toast.makeText(mContext, "Login dulu bang", Toast.LENGTH_SHORT).show();
                startActivity(move);
            }
            else {
                Intent move = new Intent(HomeActivity.this, UserActivity.class);
                startActivity(move);
            }

        }
        if(item.getItemId() == R.id.add_box){
            Intent move = new Intent(HomeActivity.this, CreateKosActivity.class);
            startActivity(move);
        }
        return super.onOptionsItemSelected(item);
    }

    protected List<Kos> requestKos(){
        mApiService.getall().enqueue(new Callback<List<Kos>>() {

            @Override
            public void onResponse(Call<List<Kos>> call, Response<List<Kos>> response) {
                arrayOfKos.clear();
                arrayOfKos.addAll(response.body());
                resetMap();
                Log.d("HASIL", response.body().toString());
                System.out.println(arrayOfKos.toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Kos>> call, Throwable t) {
//                Toast.makeText(mContext, "no Kos", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
        return null;
    }

    protected List<Kos> getAll(){
        mApiService.getall().enqueue(new Callback<List<Kos>>() {
            @Override
            public void onResponse(Call<List<Kos>> call, Response<List<Kos>> response) {
                if(response.isSuccessful()) {
                    KosList.clear();
                    KosList.addAll(response.body());
                    Set<String> uniqueNamesSet = new HashSet<>(); // Use a Set to store unique names
                    for (Kos kos : KosList) {
                        String name = kos.name;
                        if (!uniqueNamesSet.contains(name)) {
                            suggestions.add(name);
                            uniqueNamesSet.add(name); // Add the name to the set to mark it as encountered
                        }
                    }
                    resetMap();
                    Log.d("HASIL2", response.body().toString());

                }
            }
            @Override
            public void onFailure(Call<List<Kos>> call, Throwable t) {
                Toast.makeText(mContext, "salah", Toast.LENGTH_SHORT).show();
            }
        });
        return null;
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        getAll();
        requestKos();
        LatLng UI = new LatLng(-6.3606, 106.8272);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UI, 15));
        mMap.addMarker(new MarkerOptions().position(UI).title("Universitas Indonesia"));
        if (!arrayOfKos.isEmpty()) {
            // Move the map-related code inside onResponse to ensure data is available
            // Iterate through the list of Kos and add a marker for each one
            for (Kos kos : arrayOfKos) {
                LatLng kosLocation = new LatLng(kos.latitude, kos.longitude);
                mMap.addMarker(new MarkerOptions().position(kosLocation).title(kos.name));
            }
        } else {
            // Handle the case where arrayOfKos is empty (no data available yet)
//            Toast.makeText(mContext, "No data available for map markers", Toast.LENGTH_SHORT).show();
        }
        // Iterate through the list of Kos and add a marker for each one
//        LatLng testLocation = new LatLng(arrayOfKos.get(0).latitute, arrayOfKos.get(0).latitute);
//        mMap.addMarker(new MarkerOptions().position(testLocation).title("Test Kos"));
    }
    private void resetMap(){
        if (mMap != null){
            mMap.clear();
        }
        LatLng UP = new LatLng(-6.3606, 106.8272);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UP, 15));
        mMap.addMarker(new MarkerOptions().position(UP).title("Universitas Indonesia"));
        if (!arrayOfKos.isEmpty()) {
            // Move the map-related code inside onResponse to ensure data is available
            // Iterate through the list of Kos and add a marker for each one
            for (Kos kos : arrayOfKos) {
                LatLng kosLocation = new LatLng(kos.latitude, kos.longitude);
                mMap.addMarker(new MarkerOptions().position(kosLocation).title(kos.name));
            }
        } else {
            // Handle the case where arrayOfKos is empty (no data available yet)
            Toast.makeText(mContext, "No data available for map markers", Toast.LENGTH_SHORT).show();
        }

    }

}