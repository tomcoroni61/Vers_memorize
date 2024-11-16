package com.e.versmix.activ;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.e.versmix.R;
import com.e.versmix.utils.Globus;

public class AyLog extends AppCompatActivity {

    private final Globus gc = (Globus) Globus.getAppContext();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ay_log);
    }


}
