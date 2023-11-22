package com.rpl9.ratemykos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class LoginActivity extends AppCompatActivity {

    BaseApiService mApiService;
    EditText identifierText, passwordText;
    TextView reg;
    Button loginButton;
    Context mContext;
    public static Account account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mApiService = UtilsApi.getApiService();
        mContext = this;
        identifierText = findViewById(R.id.username);
        passwordText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        reg = findViewById(R.id.signupTextLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String identifier = identifierText.getText().toString();
                String password = passwordText.getText().toString();
                login(identifier, password);
            }
        });
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent move = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(move);
            }
        });


    }
    private void login(String identifier, String password) {
        Call<Account> call = mApiService.login(identifier, password);
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if (response.isSuccessful()) {
                    account = response.body();
                    // Handle a successful login response, e.g., save the token and user information
                    Intent move = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(move);
                } else {
                    // Handle the error response, e.g., display an error message
                    handleErrorResponse(response);
                }
            }
            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                // Handle network or request failure
                Log.e("Retrofit", "Request failed", t);
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
}