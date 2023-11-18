package com.rpl9.ratemykos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import androidx.appcompat.widget.SearchView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rpl9.ratemykos.model.Account;
import com.rpl9.ratemykos.model.Kos;
import com.rpl9.ratemykos.request.BaseApiService;
import com.rpl9.ratemykos.request.UtilsApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    MenuItem balance;
    BaseApiService mApiService;
    Context mContext;
    public static ArrayList<Kos> arrayOfKos = new ArrayList<Kos>();
    public static ArrayList<Kos> KosList = new ArrayList<Kos>();
    KosAdapter adapter;
    int pageSize = 0;
    public static Account accLoggedIn;
    public static Kos kosView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mApiService = UtilsApi.getApiService();
        mContext = this;
        Button next = findViewById(R.id.nextButton);
        Button prev = findViewById(R.id.prevButton);
        Button go = findViewById(R.id.goButton);
        EditText page = findViewById(R.id.page);
        TextView filter = findViewById(R.id.filterTitle);
        TextView setCity = findViewById(R.id.setCityFilter);
        TextView setBed = findViewById(R.id.setBedTypeFilter);

        Group groupFilter = findViewById(R.id.groupFilter);
        Spinner citySpin = (Spinner) findViewById(R.id.CityFilterSpinner);
        //citySpin.setAdapter((new ArrayAdapter<City>(this, android.R.layout.simple_spinner_dropdown_item, City.values())));
        Spinner bedSpin = (Spinner) findViewById(R.id.BedTypeFilterSpinner);
        //bedSpin.setAdapter((new ArrayAdapter<BedType>(this, android.R.layout.simple_spinner_dropdown_item, BedType.values())));
        requestRoom(pageSize);
        getAll();
        adapter = new KosAdapter(this, arrayOfKos);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                kosView = (Kos) (listView.getItemAtPosition(position));
                System.out.println(listView.getItemAtPosition(position));
                Intent move = new Intent(HomeActivity.this, KosDetailActivity.class);
                startActivity(move);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageSize++;
                page.setText(String.valueOf(pageSize+1));
                requestRoom(pageSize);
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pageSize > 0){
                    pageSize--;
                }

                page.setText(String.valueOf(pageSize+1));
                requestRoom(pageSize);
            }
        });
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageSize = Integer.parseInt(page.getText().toString())-1;
                requestRoom(pageSize);
            }
        });
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupFilter.getVisibility() == View.GONE){
                    groupFilter.setVisibility(View.VISIBLE);
                }
                else if (groupFilter.getVisibility() == View.VISIBLE){
                    groupFilter.setVisibility(View.GONE);
                    requestRoom(pageSize);
                }
            }
        });
//        setCity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                City cityFil = City.valueOf(citySpin.getSelectedItem().toString());
//                arrayOfRoom.clear();
//                for (Room filter : KosList) {
//                    if (filter.city == cityFil) {
//                        arrayOfRoom.add(filter);
//                    }
//                }
//                adapter.notifyDataSetChanged();
//            }
//        });
//        setBed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                BedType bedFil = BedType.valueOf(bedSpin.getSelectedItem().toString());
//                arrayOfRoom.clear();
//                for (Room filter : KosList) {
//                    if (filter.bedType == bedFil) {
//                        arrayOfRoom.add(filter);
//                    }
//                }
//                adapter.notifyDataSetChanged();
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        balance = menu.findItem(R.id.balance_menu);
        balance.setTitle("Rp. " );
        SearchView search = (SearchView) menu.findItem(R.id.search_button).getActionView();
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println(newText);
                arrayOfKos.clear();
                if (newText.length() == 0 || newText == null) {
                    arrayOfKos.addAll(KosList);
                } else {
                    for (Kos filter : KosList) {
                        if (filter.name.toLowerCase().contains(newText.toLowerCase())) {
                            arrayOfKos.add(filter);
                            System.out.println(newText.toLowerCase());
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.person){
            Intent move = new Intent(HomeActivity.this, UserActivity.class);
            startActivity(move);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Mendapatkan 8 room sesuai parameter page yang akan ditampilkan pada listview
     * @param page
     * @return
     */
    protected List<Kos> requestRoom(int page){
        mApiService.getall().enqueue(new Callback<List<Kos>>() {

            @Override
            public void onResponse(Call<List<Kos>> call, Response<List<Kos>> response) {
                arrayOfKos.clear();
                arrayOfKos.addAll(response.body());
                System.out.println(arrayOfKos.toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Kos>> call, Throwable t) {
                Toast.makeText(mContext, "no Kos", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
        return null;
    }

    /**
     * Mendapatkan semua room yang ada dan disimpan dalam ArrayList<Room> KosList
     * @return null
     */
    protected List<Kos> getAll(){
        mApiService.getall().enqueue(new Callback<List<Kos>>() {
            @Override
            public void onResponse(Call<List<Kos>> call, Response<List<Kos>> response) {
                if(response.isSuccessful()) {
                    KosList.clear();
                    KosList.addAll(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<Kos>> call, Throwable t) {
                Toast.makeText(mContext, "salah", Toast.LENGTH_SHORT).show();
            }
        });
        return null;
    }
}