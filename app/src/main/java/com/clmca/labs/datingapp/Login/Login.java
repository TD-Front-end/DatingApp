package com.clmca.labs.datingapp.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.clmca.labs.datingapp.service.api.ApiService;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.clmca.labs.datingapp.Main.MainActivity;
import com.clmca.labs.datingapp.R;
import com.google.android.gms.common.api.Scope;
import com.clmca.labs.datingapp.service.sharedprefs.SharedPrefs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    SignInButton signInButton;
    private GoogleApiClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 1;


    public static Context contextOfApplication;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        contextOfApplication = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Yêu cầu cung cáp thông tin email....
        String idTokenRequest = "1096280217531-liqq0djthhopsvgaom7cg6am19gk4gch.apps.googleusercontent.com";

        Scope scope = new Scope("https://www.googleapis.com/auth/plus.login");
        Scope scope2 = new Scope("https://www.googleapis.com/auth/user.birthday.read");


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(idTokenRequest)
                .requestEmail()
                .requestProfile()
                .requestScopes(scope, scope2)
                .build();



        // kết nối với google API client
        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //ánh xạ
        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);

        signInButton.setOnClickListener(view -> {
            Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
            startActivityForResult(intent, RC_SIGN_IN);
        });

        signInButton.setSize(SignInButton.SIZE_STANDARD);



    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //kết nối thất bại
        Log.d("Failed", connectionResult + "");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            handleSigninResult(result);

        }
    }

    private void handleSigninResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();

            String idToken = account.getIdToken();


            GoogleAuthenticationRequest request = GoogleAuthenticationRequest.builder()
                    .idToken(idToken)
                    .build();


            ApiService.apiService.getAuthenticationToken(request).enqueue(new Callback<AuthenticationResponse>() {
                @Override
                public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                    AuthenticationResponse body = response.body();

                    SharedPreferences instance = SharedPrefs.getInstance();
                    SharedPreferences.Editor editor = instance.edit();

                    editor.putString("fullname", body.getFullName());
                    editor.putString("jwt", body.getJwt());
                    editor.putString("username", body.getUsername());
                    editor.putString("avatar", body.getAvatar());
                    editor.putString("locale", body.getLocale());
                    editor.apply();


                    startActivity(new Intent(Login.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                    Toast.makeText(Login.this, "Login false:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        } else {
            //Toast.makeText(Login.this, "Login false", Toast.LENGTH_SHORT).show();// login vô được mà chạy vào đây
        }
    }

    public static Context getContextOfApplication(){
        return contextOfApplication;
    }


}
