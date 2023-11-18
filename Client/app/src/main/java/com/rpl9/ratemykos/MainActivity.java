package com.rpl9.ratemykos;

import static android.content.ContentValues.TAG;

import static com.rpl9.ratemykos.utils.Constants.ERROR_DIALOG_REQUEST;
import static com.rpl9.ratemykos.utils.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.rpl9.ratemykos.utils.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.rpl9.ratemykos.model.Account;
import com.rpl9.ratemykos.request.BaseApiService;
import com.rpl9.ratemykos.request.UtilsApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


    public class MainActivity extends AppCompatActivity {
        BaseApiService mApiService;
        EditText identifierText, passwordText;
        Button login, map, listbutton;
        TextView signUpText;
        Context mContext;
        private boolean mLocationPermissionGranted = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            mApiService = UtilsApi.getApiService();
            mContext = this;

            if(isServicesOK()){
                if(isMapsEnabled()){
                    mapinit();
                }
            }
            login = findViewById(R.id.loginMain);
            listbutton = findViewById(R.id.List);
            signUpText = findViewById(R.id.signupText);
            signUpText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Handle the click on the "Sign Up Now" text
                    moveToRegisterActivity();
                }
            });

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent move = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(move);
                }
            });
            listbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent move = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(move);
                }
            });
        }


        private void mapinit(){
            Toast.makeText(this, "Maps Enabled", Toast.LENGTH_SHORT).show();
            map = findViewById(R.id.mapButton);
            map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent move = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(move);
                }
            });
        }
        private boolean checkMapServices(){
            if(isServicesOK()){
                if(isMapsEnabled()){
                    return true;
                }
            }
            return false;
        }

        private void buildAlertMessageNoGps() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }

        public boolean isMapsEnabled(){
            final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                buildAlertMessageNoGps();
                return false;
            }
            return true;
        }

        private void getLocationPermission() {
            /*
             * Request location permission, so that we can get the location of the
             * device. The result of the permission request is handled by a callback,
             * onRequestPermissionsResult.
             */
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
//                getChatrooms();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }

        public boolean isServicesOK(){
            Log.d(TAG, "isServicesOK: checking google services version");

            int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

            if(available == ConnectionResult.SUCCESS){
                //everything is fine and the user can make map requests
                Log.d(TAG, "isServicesOK: Google Play Services is working");
                return true;
            }
            else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
                //an error occured but we can resolve it
                Log.d(TAG, "isServicesOK: an error occured but we can fix it");
                Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
                dialog.show();
            }else{
                Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
            }
            return false;
        }


        private void moveToRegisterActivity() {
            Intent move = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(move);
        }
    }

