package com.e.versmix.activ;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.e.versmix.R;
import com.e.versmix.utils.BaseSwipeActivity;
import com.e.versmix.utils.Globus;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class AyClickW extends BaseSwipeActivity {

    private final Random random = new Random();
    private int wordidx, clickCnt;
    private FlowLayout layout;
    private TextView tvTextvs, tvVers;
    private String curText;
    private final List<String> wordlist = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ay_clickwo);

        tvVers = findViewById(R.id.acheaderTextView);
        tvVers.setOnClickListener(view1 -> {
            // Toast.makeText(this, " clicked", Toast.LENGTH_LONG).show();
            gc.csvList().getRandomText();
            loadText();
        });
        tvTextvs = findViewById(R.id.actvText);
        tvTextvs.setOnClickListener(view1 -> {
            // Toast.makeText(this, " clicked", Toast.LENGTH_LONG).show(); acheaderTextView
            gc.csvList().getRandomText();
            loadText();
        });
        layout = this.findViewById(R.id.acflowLayout);
        loadText();

    }
    @Override
    public void onBackPressed() {
        gc.doBackPressed();
        super.onBackPressed();
    }

    public void doAgainClick(View view) {
        loadText();
    }
    public void doPopUpTxtClick(View view) {
        gc.showPopupWin(this.tvTextvs, gc.LernItem.Text, this::loadText);
    }
    public void tvSpeackClick(View v) {
        gc.ttSgl().speak(gc.LernItem.Text);
    }
    public void ycbtttsSettingsClick(View v) {
        gc.ttSgl().andoSetttings();
    }
    public void ycbtdoSpeakClick(View v) {
        String tvTxt=tvTextvs.getText().toString(), spTxt=gc.LernItem.Text;
        if (wordidx>wordlist.size()) return;
        String aWord = wordlist.get(wordidx-1);//toUpperCase(Locale.GERMANY).
        int wLen = aWord.length();
        int pos = spTxt.toUpperCase(Locale.GERMANY).indexOf(aWord, tvTxt.length()-wLen-2);
        //gc.Logl(tvTxt.length()-wLen-2 + " "+aWord+ "|"+pos + " "+spTxt.toUpperCase(Locale.GERMANY), true);
        if (pos < 5) return;
        tvTxt = spTxt.substring(0, pos+wLen);
        //gc.Logl(tvTxt, true);
        gc.ttSgl().speak(tvTxt);
    }
    private void loadText() {
        if (gc.LernItem==null) return;
        String text = gc.LernItem.Text;
        tvTextvs.setText("");
        wordlist.clear();   clickCnt=0;
        curText = gc.LernItem.Text;     wordidx=-1;
        tvVers.setText(gc.LernItem.Vers);
        text = gc.formatTextUpper(text);
        StringBuilder wort= new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c==' ') {
                wordlist.add(wort.toString());
                //addWort(" ");
                wort = new StringBuilder();
            }
            else wort.append(c);

        }
        if (wort.length()>0) {
            wordlist.add(wort.toString()); //addWort(" ");
        }
        //gc.Logl("do gues now", true);
        doGuessWords();
    }
    private void checkAdd(int idi) {
        String aWord = wordlist.get(idi);
        if (!isInGuesList(aWord)) {
            addWort(aWord);
            //if (!woadi && idx==wordidx) wo
        }
    }
    private void doGuessWords() {
        layout.removeAllViews();
        wordidx++;
        //boolean woadi=false;
        //addWort(wordlist.get(wordidx));
        int cnt=99;
        while (layout.getChildCount() < 3 + random.nextInt(5) && cnt >0) {
            checkAdd( random.nextInt(wordlist.size()-1) );
            cnt--;
        }
        checkAdd(wordidx);

    }
    private boolean isInGuesList(String aWord) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            TextView v = (TextView) layout.getChildAt(i);
            if (aWord.equals(v.getText().toString())) return true;
        }
        return false;
    }
    private void addWort(String aWord) {
        TextView textView = (TextView) LayoutInflater.from(layout.getContext())
                .inflate(R.layout.cliword, layout, false);
        textView.setText(aWord);
        textView.setGravity(Gravity.CENTER);
        // textView.setTag(idx);
        textView.setBackgroundResource(R.drawable.rounded_corner);
        textView.setOnClickListener(this::wordClick);
        int id = 0, cnt=layout.getChildCount();
        if (cnt>1) id=random.nextInt(cnt-1);
        layout.addView(textView,id);
    }
    private void wordClick(View view1) {
        TextView tv = (TextView) view1;
        String guwo = wordlist.get(wordidx);
        clickCnt++;
        if (guwo.equals(tv.getText().toString())) {
            String ct = tvTextvs.getText().toString();
            ct = ct+guwo+" ";
            tvTextvs.setText(ct);
            if (wordidx < wordlist.size()-1) doGuessWords();
                else {
                    layout.removeAllViews();
                    tvTextvs.setText(curText);
                doPopUpTxtClick(null);
            }
        } else {
            tv.setBackgroundResource(R.drawable.wrong);
        }
        guwo = clickCnt + " - " + wordlist.size()+ "   " + gc.LernItem.Vers;
        tvVers.setText(guwo);
    }

    public void ac_menuClick(View view) {
        gc.PopsMenu(view, R.menu.clickw_menu);
    }

    public void onSwipeLeft() {
        finish();
        gc.activityStart(this, AyWords.class);
    }
    public void onSwipeRight() {
        finish();
        //gc.activityStart(this, AyEntries.class);
    }

}