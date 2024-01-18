package es.upm.etsiinf.pui.pui_newsmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import es.upm.etsiinf.pui.pui_newsmanager.exceptions.AuthenticationError;
import es.upm.etsiinf.pui.pui_newsmanager.model.ModelManager;

public class LoginActivity extends AppCompatActivity {

    public static final String PREFERENCES_AUTH_TOKEN_KEY = "AuthTokenHeader";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnGoToArticles = findViewById(R.id.goToArticles);

        btnGoToArticles.setOnClickListener(view -> {
            this.finish();
        });

        btnLogin.setOnClickListener(view -> {
            EditText txtUsername = findViewById(R.id.txtUsername);
            EditText txtPassword = findViewById(R.id.txtPassword);

            //Call the thread to login
            LoginThread loginThread = new LoginThread(this, txtUsername.getText().toString(), txtPassword.getText().toString());
            (new Thread(loginThread)).start();
        });
    }

    public void userLogged(String username, String password) {
        CheckBox ckbRememberMe = findViewById(R.id.ckbRememberMe);
        if (ckbRememberMe.isChecked()) {
            SharedPreferences preferences = getSharedPreferences(MainActivity.PREFERENCES_KEY, MODE_PRIVATE);
            //Saving the APIKEY in the preferences
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PREFERENCES_AUTH_TOKEN_KEY, MainActivity.modelManager.getApikey());
            editor.putString(ModelManager.ATTR_LOGIN_USER, username);
            editor.putString(ModelManager.ATTR_LOGIN_PASS, password);
            editor.apply();
        }
        finish();
    }

    public void showIncorrectCredentialsToast(){
        Toast toast = Toast.makeText(this , "Invalid credentials", Toast.LENGTH_LONG);
        toast.show();
    }
}