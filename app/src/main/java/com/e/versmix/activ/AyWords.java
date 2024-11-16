package com.e.versmix.activ;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.e.versmix.R;
import com.e.versmix.utils.BaseSwipeActivity;
import com.e.versmix.utils.Globus;

import org.apmem.tools.layouts.FlowLayout;

import java.util.Random;

public class AyWords extends BaseSwipeActivity {
// https://github.com/ApmeM/android-flowlayout/blob/master/app/src/main/java/org/apmem/tools/example/FlowLayoutActivity.java

    private final Random random = new Random();
    private FlowLayout layout;
    private TextView tvVers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ay_words);
        tvVers = findViewById(R.id.ayheaderTextView);
        tvVers.setOnClickListener(view1 -> {
            // Toast.makeText(this, " clicked", Toast.LENGTH_LONG).show();
            gc.csvList().getRandomText();
            loadText();
        });
        layout = this.findViewById(R.id.flowLayout);
        loadText();

    }
    @Override
    public void onBackPressed() {
        gc.doBackPressed();
        super.onBackPressed();
    }

    public void loadText() {
        if (gc.LernItem ==null) return;
        String text = gc.LernItem.Text;
        layout.removeAllViews();
        text = gc.formatTextUpper(text);
        gc.Logl(text, false);
        StringBuilder wort= new StringBuilder();
        int cnt=0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c==' ') {
                addWort(wort.toString(), cnt);
                //addWort(" ");
                wort = new StringBuilder();
                cnt++;
            }
            else wort.append(c);

        }
        if (wort.length()>0) {
            addWort(wort.toString(), cnt); //addWort(" ");
        }
        mischViews();
        if (gc.LernItem !=null)
            tvVers.setText(gc.LernItem.Vers);
    }
    public void txtClick(View v) {
        gc.showPopupWin(this.layout, gc.LernItem.Text, this::loadText);
    }
    public void tvSpeackClick(View v) {
        gc.ttSgl().speak(gc.LernItem.Text);
    }
    private void mischViews() {
        int cnt=layout.getChildCount();
        if (cnt<5) return;
        int mc=5 + random.nextInt(cnt), idx, idx2;
        while (mc>0) {
            idx = random.nextInt(cnt-1);
            for (int i = 0; i < 22; i++) {
                idx2 = random.nextInt(cnt-1);
                if (idx!=idx2 && idx2<cnt-2) {
                    View view=layout.getChildAt(idx);
                    layout.removeView(view);
                    layout.addView(view, idx2);
                    break;
                }
            }
            mc--;
        }
        removeView=null;
    }
    private TextView removeView=null;
    private void checkNewPlatz(int idx, View view) {
        int viewIdx = (int) view.getTag();
        boolean riPl = idx == viewIdx;
        if (!riPl) {
            String vText = ((TextView) view).getText().toString();
            for (int i = idx+1; i < layout.getChildCount(); i++) {
                TextView tv = (TextView) layout.getChildAt(i);
                String sText = tv.getText().toString();
                int xdi = (int) tv.getTag();
                if (vText.equals(sText) && xdi==idx) {
                    riPl=true;
                    view.setTag(idx);  tv.setTag(viewIdx);
                }
            }
        }
        if (riPl)
            view.setBackgroundResource(R.drawable.richtigplaz); else
                view.setBackgroundResource(R.drawable.rounded_corner);
    }
    private void wordClick(View view1) {
        TextView tv = (TextView) view1;
        int idi = getViewIdx(tv);
        if (removeView != null) {
            if (removeView==tv) {
                removeView.setBackgroundResource(R.drawable.rounded_corner);
                removeView = null;
            } else {

                if (idi > -1) {
                    layout.removeView(removeView);
                    layout.addView(removeView, idi);
                    checkNewPlatz(idi, removeView);
                    OkCheck();
                }

                removeView = null;
            }
        } else {
            if (idi == (int) tv.getTag())
                tv.setBackgroundResource(R.drawable.richtigplaz);
            else {
                int rid=isNext(view1);
                if (rid >-1) {
                    layout.removeView(view1);
                    layout.addView(view1, rid);
                    tv.setBackgroundResource(R.drawable.richtigplaz);
                    OkCheck();
                } else {
                    removeView = tv;
                    removeView.setBackgroundResource(R.drawable.selected);
                }
            }

        }

    }
    private int isNext(View view){
        int oc = (int) view.getTag(), Cnt = layout.getChildCount(), okcnt=-1;
        for (int i = 0; i < Cnt; i++) {
            View v = layout.getChildAt(i);
            int vTag = (int) v.getTag();
            if (vTag==i) okcnt++; else {
                if (i==0) {
                    if (oc==1) return 0; else return -1;
                } else {
                    if (okcnt==oc-1) return oc;
                    return -1;
                }
            }
        }
        return -1;
    }
    private void OkCheck() {
        int okcnt=0, Cnt = layout.getChildCount();
        for (int i = 0; i < Cnt; i++) {
            View v = layout.getChildAt(i);
            if ((int) v.getTag()==i) okcnt++; else
                v.setBackgroundResource(R.drawable.rounded_corner);
        }
        if (okcnt!=Cnt) {

            return;
        }
        //gc.Logl("Oki: "+okcnt+ " / "+Cnt, true);
        for (int i = 0; i < Cnt; i++) {
            View v = layout.getChildAt(i);
            v.setBackgroundResource(R.drawable.richtigplaz);
        }
        gc.showPopupWin(this.layout, gc.LernItem.Text, this::loadText);
    }
    private int getViewIdx(View view) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View v = layout.getChildAt(i);
            if (v==view) return i;
        }
        return -1;
    }
    private void addWort(String wd, int idx) {
        TextView textView = (TextView) LayoutInflater.from(layout.getContext())
                .inflate(R.layout.tv_word, layout, false);
        textView.setText(wd);
        textView.setTag(idx);
        textView.setBackgroundResource(R.drawable.rounded_corner);
        textView.setOnClickListener(this::wordClick);
        layout.addView(textView);

    }

    public void aw_menuClick(View view) {
        gc.PopsMenu(view, R.menu.words_menu);
    }

    public void onSwipeLeft() {
        finish();
        gc.activityStart(this, AyLetters.class);
    }
    public void onSwipeRight() {
        finish();
        gc.activityStart(this, AyClickW.class);
    }

}


