package com.e.versmix.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.e.versmix.R;

public class Reportus extends AppCompatActivity {

    private final Globus gc = (Globus) Globus.getAppContext();
    private String repl="Error";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //comment this line if you need to show Title.
        setContentView(R.layout.acty_dlg);
        Intent intent = getIntent();
        if (intent.hasExtra("ErrMsg"))
            repl=intent.getStringExtra("ErrMsg");
        EditText et = findViewById(R.id.edMessage);
        et.setText(repl);
    }


    public void adSendMailClick(View view) {
        SendErrorMail(this.getBaseContext(), repl);
    }
    private void SendErrorMail(Context _context, String ErrorContent) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        String subject = _context.getResources().getString(R.string.CrashReport_MailSubject)
                + _context.getResources().getString(R.string.app_name);
        String body = _context.getResources().getString(R.string.CrashReport_MailBody) +
                "\n\n" +
                ErrorContent +
                "\n\n";
        sendIntent.putExtra(Intent.EXTRA_EMAIL,
                new String[]{_context.getString(R.string.CrashReportEmailTo)});
        sendIntent.putExtra(Intent.EXTRA_TEXT, body);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.setType("message/rfc822");
        //startActivity(Intent.createChooser(sendIntent, "Title:"));

        try {
            startActivity(Intent.createChooser(sendIntent,
                    "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            gc.Logl( "No email clients installed.",true );
        }

    }

    public void adSendExitClick(View view) {
        finish();
    }
}
