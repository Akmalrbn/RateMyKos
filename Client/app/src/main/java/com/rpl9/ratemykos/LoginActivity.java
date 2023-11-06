package com.rpl9.ratemykos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rpl9.ratemykos.model.Account;
import com.rpl9.ratemykos.request.BaseApiService;
import com.rpl9.ratemykos.request.UtilsApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    BaseApiService mApiService;
    EditText identifierText, passwordText;
    Button login;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mApiService = UtilsApi.getApiService();
        mContext = this;

        login = findViewById(R.id.loginButton);
        identifierText = findViewById(R.id.identifierLogin);
        passwordText = findViewById(R.id.passwordLogin);

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
                    Intent move = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(move);
                    // Handle a successful login response, e.g., save the token and user information
                } else {
                    // Handle the error response, e.g., display an error message
                }
            }
            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                // Handle network or request failure
            }
        });
    }


}