package com.rushia.aqitracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendMailActivity extends AppCompatActivity {

    Toolbar toolbarEmail;

    EditText editTextSubject, editTextProblemDetails;
    Button buttonSendMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mail);

        toolbarEmail = findViewById(R.id.toolbarEmail);
        editTextSubject = findViewById(R.id.editTextSubject);
        editTextProblemDetails = findViewById(R.id.editTextProblemDetail);
        buttonSendMail = findViewById(R.id.buttonSendMail);

        toolbarEmail.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMailActivity.this.finish();
            }
        });

        buttonSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextSubject.getText().toString().isEmpty() && !editTextProblemDetails.getText().toString().isEmpty()) {
                    String email = "abc_team.bki2021@gmail.com";
                    Intent intent = new Intent(Intent.ACTION_SEND);

                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                    intent.putExtra(Intent.EXTRA_SUBJECT, editTextSubject.getText().toString());
                    intent.putExtra(Intent.EXTRA_TEXT, editTextProblemDetails.getText().toString());

                    intent.setType("message/rfc822");
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(SendMailActivity.this, "There is no application that support this action!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(SendMailActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}