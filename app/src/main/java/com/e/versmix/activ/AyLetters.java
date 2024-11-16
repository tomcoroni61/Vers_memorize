package com.e.versmix.activ;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.e.versmix.R;
import com.e.versmix.itemLs.CsvData;
import com.e.versmix.itemLs.ItemAr;
import com.e.versmix.utils.BaseSwipeActivity;
import com.e.versmix.utils.Globus;

public class AyLetters extends BaseSwipeActivity {
    public static final String LOG_TAG = "merried"; //MainActivity.class.getSimpleName();
    public ItemAr adapter;
    //private sbSce dataSource;
    //private final Globus gc = (Globus) Globus.getAppContext();
    private GridView gridView;
    private TextView tvHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int crashcnt=0;
        try {
            Log.d(LOG_TAG, "MainActivity On Create ");
            // gc.mainActi = this;   gc.mainActivity=this;
            setContentView(R.layout.ay_letters);  crashcnt=11;
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            gridView = findViewById(R.id.basiGridView); crashcnt=12;

            // MenuItem menuItem = entry menu disable..
            crashcnt = 2;
            tvHeader = findViewById(R.id.headerTextView); crashcnt=9;
            crashcnt=4;
            newText(true); crashcnt=5;

            //PopsMenu();
            tvHeader.setOnClickListener(view1 -> {
                // Toast.makeText(this, " clicked", Toast.LENGTH_LONG).show();
                newText(false);

            });
            crashcnt=11;
            gc.ttSgl().speak("");
        }  catch (  Exception e) {
            gc.Logl("MA_Crash Nr: " + crashcnt + " Msg: " + e.getMessage(), true);

        }

    } //Create ...
    @Override
    public void onBackPressed() {
        gc.doBackPressed();
        super.onBackPressed();
    }
    public void speackClick(View v) {
        gc.ttSgl().speak(gc.LernItem.Text);
    }

    public void updateClickLabel() {
        //csvList.RandomItem = csvList.dataList.get(csvList.RandomText_Idx);
        if (gc.LernItem == null) return;
        String ht= gc.LernItem.Vers + "    " +  adapter.usermoves + " | " + adapter.movecnt;
        tvHeader.setText(ht);
    }
    public void LettersDone() {
        CsvData csvData = gc.LernItem;
        if (csvData==null) return;
        String ht= csvData.Vers + " Done " + adapter.usermoves + " <> " + adapter.movecnt ;
        tvHeader.setText(ht);
        gc.showPopupWin(gridView, csvData.Text, () -> newText(true));
    }
    public void newText(boolean nurMisch) {
        int crashcnt=0;
        try {
            if (nurMisch && gc.LernItem.Text.length() < 22) nurMisch=false;
            String str; crashcnt=1;
            if (nurMisch) str = gc.LernItem.Text; else
                str = gc.csvList().getRandomText(); // "Es ist aber der Glaube eine feste Zuversicht dessen, was man hofft, und ein Nichtzweifeln an dem, was man nicht sieht.";
            str = gc.formatTextUpper(str);
            gridView.setAdapter(null);
            if (adapter!=null) {
                adapter.clearme();
                adapter.clear();
                adapter=null;
            }
            //gridView.get
            adapter = new ItemAr(this, gridView, this);
            gridView.setAdapter(adapter);  crashcnt=2;
            char[] letters = str.toCharArray();
            adapter.loadLetters(letters);
            adapter.mischen();   crashcnt=9;
            if (adapter.getCount() != letters.length)
                gc.Logl("Error load TextLen: " + letters.length + " ListLen: " + adapter.getCount(), true);
            updateClickLabel();
        }  catch (  Exception e) {
            gc.Logl("MA_Crash Nr: " + crashcnt + " Msg: " + e.getMessage(), true);

        }
    }


    public void letxtClick(View view) {
        gc.showPopupWin(this.gridView, gc.LernItem.Text, () -> newText(true));
    }

    public void al_menuClick(View view) {
        gc.PopsMenu(view, R.menu.letters_menu);
    }

    public void alRetryClick(View view) {
        newText(true);
    }

    public void onSwipeLeft() {
        finish();
        gc.activityStart(this, AySpeech.class);
    }
    public void onSwipeRight() {
        finish();
        gc.activityStart(this, AyWords.class);
    }

}

