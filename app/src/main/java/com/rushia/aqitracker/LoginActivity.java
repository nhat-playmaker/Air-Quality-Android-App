package com.rushia.aqitracker;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {

    final int REQUEST_CODE = 123;
    final String decodeHeaderStart = "013cef";
    final String decodeHeaderEnd = "2ce37f";
    final String decodeTransaction = "eeffeeff";
    String deviceID, devicePassword;

    EditText editTextDeviceID, editTextDevicePassword;
    CircularProgressButton buttonLogin;
    FrameLayout buttonScanQR;
    ArrayList<DeviceInfo> arrayDevice;
    CheckBox checkBoxRemember;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Initialize();

        String url_device = "https://aqitracker-abcteam.000webhostapp.com/";
        getDeviceInformation(url_device);

        buttonScanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ScanActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.stay);

            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String deviceID = editTextDeviceID.getText().toString();
                boolean loginOK = false;

                for (int i = 0; i < arrayDevice.size(); i++) {
                    if (deviceID.equals(String.valueOf(arrayDevice.get(i).getDeviceID()))) {
                        String devicePassword = editTextDevicePassword.getText().toString();
                        if (devicePassword.equals(arrayDevice.get(i).getDevicePassword())) {
                            loginOK = true;
                            Bundle bundle = new Bundle();
                            bundle.putString("device-id", deviceID);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("id-from-login-activity", bundle);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
                        }
                    }
                }

                if (!loginOK) {
                    Toast.makeText(LoginActivity.this, "Device ID or password is incorrect! Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void Initialize() {
        editTextDeviceID = findViewById(R.id.editTextDeviceID);
        editTextDevicePassword = findViewById(R.id.editTextPassword);
        buttonScanQR = findViewById(R.id.buttonQRScan);
        buttonLogin = findViewById(R.id.cirLoginButton);
        checkBoxRemember = findViewById(R.id.checkboxRemember);

        arrayDevice = new ArrayList<>();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK one more time to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String dataScannerReceive = data.getStringExtra("data-receive");

            String[] component = dataScannerReceive.split("-", 5);

            if (component[0].equals(decodeHeaderStart) && component[4].equals(decodeHeaderEnd) && component[2].equals(decodeTransaction)) {
                Toast.makeText(this, "Scan QR code successfully", Toast.LENGTH_SHORT).show();

                deviceID = component[1];
                devicePassword = component[3];

                editTextDeviceID.setText(deviceID);
                editTextDevicePassword.setText(devicePassword);
            }
            else {
                Toast.makeText(this, "This QR code has wrong format", Toast.LENGTH_SHORT).show();
                editTextDeviceID.setText("");
                editTextDevicePassword.setText("");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getDeviceInformation(String URL) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                arrayDevice.add(new DeviceInfo(
                                        object.getInt("id"),
                                        object.getInt("device_id"),
                                        object.getString("password")
                                ));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonArrayRequest);
    }
}