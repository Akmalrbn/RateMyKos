package com.rpl9.ratemykos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rpl9.ratemykos.model.Account;
import com.rpl9.ratemykos.request.BaseApiService;
import com.rpl9.ratemykos.request.UtilsApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


    public class MainActivity extends AppCompatActivity {

        BaseApiService mApiService;
        EditText identifierText, passwordText;
        Button login;
        TextView signUpText;
        Context mContext;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            mApiService = UtilsApi.getApiService();
            mContext = this;

            login = findViewById(R.id.loginButton);
            identifierText = findViewById(R.id.username);
            passwordText = findViewById(R.id.password);

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
                    String identifier = identifierText.getText().toString();
                    String password = passwordText.getText().toString();
                    // Call the login method with the EditText values
                    login(identifier, password);
                }
            });
        }

        private void login(String identifier, String password) {
            Call<Account> call = mApiService.login(identifier, password);
            call.enqueue(new Callback<Account>() {
                @Override
                public void onResponse(Call<Account> call, Response<Account> response) {
                    if (response.isSuccessful()) {
                        Account account = response.body();
                        String email = account.getEmail();
                        String username = account.getUsername();
                        // Handle a successful login response, e.g., save the token and user information
                        moveToNextActivity();
                    } else {
                        // Handle the error response, e.g., display an error message
                        handleErrorResponse(response);
                    }
                }

                @Override
                public void onFailure(Call<Account> call, Throwable t) {
                    // Handle network or request failure
                    showToast("Network or request failure. Please try again.");
                }
            });
        }

        private void handleErrorResponse(Response<Account> response) {
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

        private void moveToNextActivity() {
            Intent move = new Intent(MainActivity.this, NextActivity.class); // Change NextActivity to your desired next activity
            startActivity(move);
        }

        private void moveToRegisterActivity() {
            Intent move = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(move);
        }
    }

