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

public class RegisterActivity extends AppCompatActivity {

    BaseApiService mApiService;
    EditText emailText, usernameText, passwordText;
    TextView reg;
    Button regButton;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mApiService = UtilsApi.getApiService();
        mContext = this;
        emailText = findViewById(R.id.registerEmail);
        usernameText = findViewById(R.id.registerUsername);
        passwordText = findViewById(R.id.registerPassword);
        regButton = findViewById(R.id.registerButton);
        reg = findViewById(R.id.signupTextLogin);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailText.getText().toString();
                String username = usernameText.getText().toString();
                String password = passwordText.getText().toString();
                register(email, username, password);
            }
        });
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent move = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(move);
            }
        });
    }
    private void register(String emailReg, String usernameReg, String passwordReg) {
        Call<Account> call = mApiService.register(emailReg, usernameReg, passwordReg);
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if (response.isSuccessful()) {
                    Account account = response.body();
                    String email = account.getEmail();
                    String username = account.getUsername();
                    // Handle a successful login response, e.g., save the token and user information
                    Intent move = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(move);
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
}