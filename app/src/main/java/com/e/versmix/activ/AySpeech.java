package com.e.versmix.activ;

import static com.e.versmix.utils.Fixi.Items.itemChooseColor;
import static com.e.versmix.utils.Fixi.Items.itemNormalColor;

import androidx.annotation.NonNull;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.e.versmix.R;
import com.e.versmix.databinding.AySpeechBinding;
import com.e.versmix.itemLs.LvAdapt;
import com.e.versmix.itemLs.SuEr_Item;
import com.e.versmix.utils.BaseSwipeActivity;
import com.e.versmix.utils.ShButton;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.Locale;

public class AySpeech extends BaseSwipeActivity {

    private EditText  eWordsInc, eReplaceS;
    private boolean bAutoHideText=true;
    private Button btUp, btDown;
    private Suer_Adapter suer_adapter;
    private int ItemClickidx = -1;
    private AySpeechBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AySpeechBinding.inflate(getLayoutInflater());

        setContentView(binding.root);
        saw_RecPause = gc.appVals().valueReadBool("extraPause", false);
        binding.extraPause.setChecked(saw_RecPause);

        eWordsInc = findViewById(R.id.sedwordcnt);
        eWordsInc.setText(gc.appVals().valueReadString("eWordsInc", "3"));
        eWordsInc.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {  }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count==0 || s==null || s.length()==0) return;
                gc.appVals().valueWriteString("eWordsInc", s.toString());
            }
        });

        //  suche ersetzenliste ...
        ListView lv = findViewById(R.id.lvserepl);
        suer_adapter = new Suer_Adapter(this);//sbtSpeakAndWordInc  binding.ed_SearchR
        lv.setAdapter(suer_adapter);
        handleItemClick();
        lv.setOnItemClickListener((parent, view, position, id) -> {
            ItemClickidx = position;
            handleItemClick();
        });
        suer_adapter.loadFromFile();
        int i=0;
        String[] suer ={"dass", "das", "daß", "das", " {2}", " "};
        if (suer_adapter.getCount()==0) {
            while (i < 6) {
                SuEr_Item item = new SuEr_Item();
                item.suche = suer[i];
                i++;
                item.ersetze = suer[i];
                i++;
                suer_adapter.add(item);
            }
            suer_adapter.savetofile();
            suer_adapter.notifyDataSetChanged();
        }
        eReplaceS = findViewById(R.id.ed_Replace);
        btDown = findViewById(R.id.btsrDown);
        btUp = findViewById(R.id.btsrUp);

        setFields();

    }
    @Override
    public void onBackPressed() {
        gc.doBackPressed();
        super.onBackPressed();
    }

    public void btDownClick(View view) {
        SuEr_Item Clickitem = suer_adapter.getItem(ItemClickidx);
        SuEr_Item aitem =suer_adapter.getItem(ItemClickidx + 1);
        suer_adapter.set(ItemClickidx, aitem);
        suer_adapter.set(ItemClickidx + 1, Clickitem);
        ItemClickidx++;
        ArrowButs();
        suer_adapter.savetofile();

    }

    public void btUpClick(View view) {
        SuEr_Item Clickitem = suer_adapter.getItem(ItemClickidx);
        SuEr_Item aitem = suer_adapter.getItem(ItemClickidx - 1);
        suer_adapter.set(ItemClickidx, aitem);
        suer_adapter.set(ItemClickidx - 1, Clickitem);
        ItemClickidx--;
        ArrowButs();
        suer_adapter.savetofile();
    }

    public void btAddClick(View view) {
        SuEr_Item item = new SuEr_Item();
        item.suche = binding.edSearchR.getText().toString();
        item.ersetze = eReplaceS.getText().toString();
        suer_adapter.add(item);  suer_adapter.savetofile();
    }

    public void btDeleteClick(View view) {
        if (ItemClickidx<0 || ItemClickidx > suer_adapter.getCount()-1) return;
        SuEr_Item item = suer_adapter.getItem(ItemClickidx);
        ItemClickidx=-1;
        suer_adapter.remove(item);
        suer_adapter.notifyDataSetChanged();
        ArrowButs();
    }

    private void handleItemClick() {
        for (int i = 0; i < suer_adapter.getCount(); i++) {
            SuEr_Item item = suer_adapter.getItem(i);
            if (item==null) continue;
            item.ItemBakColor = itemNormalColor;
            if (i==ItemClickidx) {
                item.ItemBakColor = itemChooseColor;
                binding.edSearchR.setText(item.suche);
                eReplaceS.setText(item.ersetze);
            }
            //if (!item.ItemBakColor.equals(itemChooseColor))
        }

        suer_adapter.notifyDataSetChanged();
        ArrowButs();
    }

    private void ArrowButs(){  if (ItemClickidx <0) return; SuEr_Item item =suer_adapter.getItem(ItemClickidx);
        if (item==null) return;
        if (!item.ItemBakColor.equals(itemChooseColor))
            {  btUp.setVisibility(View.INVISIBLE); btDown.setVisibility(View.INVISIBLE); return; }
        int count = suer_adapter.getCount();
        if (ItemClickidx < count-1 && ItemClickidx>-1 ) btDown.setVisibility(View.VISIBLE); else  btDown.setVisibility(View.INVISIBLE);
        if (ItemClickidx > 0)  btUp.setVisibility(View.VISIBLE); else  btUp.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

        private void doCurDataIdx(boolean Minus) {
        gc.csvList().doLearnDataIdx(Minus);

        setFields();
    }
    private void setFields() {
        if (gc.LernItem==null) return;
        binding.asfLText.removeAllViews();
        String txt = gc.LernItem.Text, wort="", now="";

        for (int i = 0; i < txt.length(); i++) {
            char c = txt.charAt(i);
            if ( (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') ){ //(c >= '0' && c <= '9') ||
                if (!now.isEmpty()) {
                    addTxt(wort + now);
                    now = ""; wort="";
                }
                wort += c;

            } else
                switch (c) {
                    case 'ö': case 'Ö': case 'ä': case 'Ä':
                    case 'ü': case 'Ü': case 'ß':
                        if (!now.isEmpty()) {
                            addTxt(wort + now);
                            now = ""; wort="";
                        }
                        wort += c;
                    break;
                    default: now += c;

                    break;
                }

        }
        addTxt(wort + now);
        binding.headerTextView.setText(gc.LernItem.Vers);
    }
    private void addTxt(String tx) {
        if (tx.isEmpty()) return;
        TextView textView = (TextView) LayoutInflater.from(binding.asfLText.getContext())
                .inflate(R.layout.tv_speech, binding.asfLText, false);
        textView.setText(tx);
        //textView.setBackgroundResource(R.drawable.rounded_corner);
        textView.setOnClickListener(this::wordClick);
        binding.asfLText.addView(textView);
    } // extends SpeechRecognizer

    private void wordClick(View view) {
        TextView textView = (TextView) view;
        if (textView.getBackground()==null) {
            int cnt=0;
            for (int i = 0; i < binding.asfLText.getChildCount(); i++) {
                View v = binding.asfLText.getChildAt(i);
                if (v.getBackground() != null) {
                    cnt++;
                    if (cnt>1) v.setBackground(null);
                }
            }
            textView.setBackgroundResource(R.drawable.speech_sel);
        } else
                textView.setBackground(null);
    }

    public void asttsSettingsClick(View v) {
        gc.ttSgl().andoSetttings();
    }

    private void endRecoBeep() {
        if (!RecSignal) return;
        gc.playaTone(500, 255, 0.2, true);
        gc.playaTone(700, 255, 0.2, true);
    }

    private boolean recording = false;
    private String curTTS_SpeakText, lastRecoTxt, textSpoken; //binding.recognizedText
    private long saw_lastMicNoice, afterRecStart;
    private boolean  RecSignal=true;
    private void getSpeakText() {
        int cnt=0, wIdx=binding.asfLText.getChildCount();   curTTS_SpeakText ="";
        if (saw_cnt>4) wIdx=saw_cnt+1;
        if (wIdx>binding.asfLText.getChildCount()) wIdx=binding.asfLText.getChildCount();
        //gc.Logl("widx "+wIdx + " cnt: "+flText.getChildCount(), false);
        //gc.Logl("saw_cnt "+saw_cnt , false);

        for (int i = 0; i < wIdx; i++) {
            TextView v = (TextView) binding.asfLText.getChildAt(i);

            if (saw_cnt < 4) {
                if (v.getBackground() != null) {
                    cnt++;

                }
                if (cnt == 1) curTTS_SpeakText += v.getText().toString();
                if (cnt == 2) {
                    curTTS_SpeakText += v.getText().toString();
                    cnt = 3;
                }
            }  else curTTS_SpeakText += v.getText().toString();
        }
        if (curTTS_SpeakText.isEmpty()) curTTS_SpeakText = gc.LernItem.Text;

    }
    private int waitSec, timeRecOut=1400;
    private final Handler Timhandl = new Handler(Looper.getMainLooper());
    private final Runnable checkSpeackStart = new Runnable() {  //geht nur wenn handy eingeschalten ist.
        @Override
        public void run() {
            waitSec++;
            if (waitSec>30) return;
            if (!gc.ttSgl().ttobj.isSpeaking()) Timhandl.postDelayed(checkSpeackStart, 1111); else
                Timhandl.postDelayed(checkSpeakEnd, 1111);
        }
    };
    private final Runnable checkUserSpeaking = new Runnable() {
        @Override
        public void run() {
            long cmi = System.currentTimeMillis();

            if (cmi - afterRecStart > timeRecOut* 1000L) {//timeRecOut geht nur bis ~400 "Looper gestoppt"?
                //stt macht looper kaputt? in app context ok.
                binding.recognizedText.setText(lastRecoTxt);
                lastRecoTxt = "";
                gc.Logl("Aufnahme timeOut", true);
                //speechRecognizer.stopListening();
                stopRecording();
                doneRecording();
                return;
            }
            if (cmi - saw_lastMicNoice < 4000) {//weiter stt
                //binding.recognizedText.setText(lastRecoTxt);
                textSpoken = lastRecoTxt;
                gc.Logl("Aufnahme geht weiter", false);
                lastRecoTxt = "";
                return;
            }
            Timhandl.postDelayed(checkUserSpeaking, 131);
        }
    }; //

    private final Runnable RunstartReco = () -> {
        if (RecSignal) gc.playaTone(500, 455, 0.2, true);
        startSpeechToText();
        afterRecStart = System.currentTimeMillis();
        Timhandl.postDelayed(checkUserSpeaking, 111);
    }; // Timhandl.postDelayed(startReco, 1511);
    private final Runnable startReco = () -> {
        if (RecSignal) gc.playaTone(500, 455, 0.2, true);
        startRecording();
        //ysbtRecognizeClick(null);
    }; // Timhandl.postDelayed(checkUserSpeaking, 1511);
    private final Runnable sayOki = () -> gc.ttSgl().speak(getString(R.string.okay));
    private final Runnable checkSpeakEnd = new Runnable() {
        @Override
        public void run() {
            if (gc.ttSgl().ttobj.isSpeaking()) Timhandl.postDelayed(checkSpeakEnd, 1111); else {
                Timhandl.postDelayed(startReco, 1511);

            }
        }
    };
    private boolean startSpeak() {
        getSpeakText();
        if (curTTS_SpeakText.isEmpty()) {
            //Note that Snackbars are preferred for brief messages while the app is in the foreground.
            Toast.makeText(this, "Please enter some text", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (ss == Saw_states.saw_wait || binding.speakText.isChecked())
                gc.ttSgl().speak(curTTS_SpeakText);
        }
        return true;
    }
    public void speakClick(View view) {
        startSpeak();
    }
    public void speakAndClick(View view) {
        if (!startSpeak()) return;
        waitSec=0;  saw_cnt=-1;  ss = Saw_states.saw_wait;
        Timhandl.postDelayed(checkSpeackStart, 1111);
    }//
    public void sckRecPauseClick(View view) {
        CheckBox cb = (CheckBox) view;//binding.extraPause.setChecked(gc.appVals().valueReadBool("extraPause", false));
        gc.appVals().valueWriteBool("extraPause", cb.isChecked() );
        saw_RecPause = cb.isChecked();
    }
    public void sckRecBeepClick(View view) {
        CheckBox cb = (CheckBox) view;
        RecSignal = cb.isChecked();
    }

    private int saw_cnt=-1, saw_inc=3, saw_WordsOk_cnt, saw_allSayedCnt, saw_MinRecLevel=2;
    private boolean saw_RecPause = false, saw_RecPauseContinue=false;

    public void keepScreenOnClick(View view) {
        CheckBox cb = (CheckBox) view;
        if (!cb.isChecked()) getWindow(). clearFlags(WindowManager. LayoutParams. FLAG_KEEP_SCREEN_ON); else
            getWindow().addFlags(WindowManager. LayoutParams. FLAG_KEEP_SCREEN_ON);
    }

    private enum Saw_states { saw_wait, saw_started, saw_userStop, saw_3xOk }
    private Saw_states ss = Saw_states.saw_wait;

    public void speakAndWordsClick(View view) {

        if (ss == Saw_states.saw_wait) {
            ss = Saw_states.saw_started;
            saw_cnt = 5;  saw_allSayedCnt=0;
            binding.sbtSpeakAndWordInc.setText(R.string.stop);
            binding.recognizedText.setText("");  binding.ystvRecoToDo.setText("");
            textSpoken="";
            try {
                saw_inc = Integer.parseInt(eWordsInc.getText().toString());
            } catch(NumberFormatException ignored) {  }
            waitSec=0;
            if (!startSpeak()) return;
            Timhandl.postDelayed(checkSpeackStart, 1111);
        } else
        if (view!=null || ss==Saw_states.saw_userStop) {
            gc.ttSgl().ttobj.stop();
            binding.sbtSpeakAndWordInc.setText(R.string.words);
            ss = Saw_states.saw_wait;
            binding.ystvRecoToDo.setText(R.string.stop_by_user);
        } else
        //if (ss==Saw_states.saw_3xOk)
            {
                binding.sbtSpeakAndWordInc.setText(R.string.words);
                ss = Saw_states.saw_wait;

            }

    }
    private void afterRecordCheck() {
        if (textSpoken.length() < 11) { //saw_cnt < 4 || saw_WordsOk_cnt<3
            if (ss != Saw_states.saw_wait) {
                gc.Logl("afterRecordCheck if (ss != Saw_states.saw_wait) ", false);
                ss = Saw_states.saw_userStop;
                speakAndWordsClick(null);
            }
            return;
        }
        if (saw_WordsOk_cnt < saw_cnt) saw_cnt = saw_WordsOk_cnt; else
            saw_cnt += saw_inc;
        //Snackbar.make(this.binding.recognizedText, "Reco saw_cnt "+saw_cnt, Snackbar.LENGTH_LONG).show();
        if (saw_WordsOk_cnt > saw_cnt) saw_cnt=saw_WordsOk_cnt;
        if (saw_WordsOk_cnt > binding.asfLText.getChildCount()-2) {
            saw_allSayedCnt++;
            if (saw_allSayedCnt>2) {
                ss = Saw_states.saw_3xOk;
                Timhandl.postDelayed(sayOki, 2111);
                speakAndWordsClick(null);
                String eReco=getString(R.string._3x_okay) + "\n"+ binding.recognizedText.getText().toString();
                binding.recognizedText.setText(eReco); //textSpoken="";
                return;
            }
            binding.recognizedText.setText("");  textSpoken="";
            gc.ttSgl().speak(getString(R.string.times_okay_say_it_again));
            Timhandl.postDelayed(startReco, 5511);
            return;
        }

        waitSec=0;  binding.recognizedText.setText("");  textSpoken="";
        if (!startSpeak()) return;
        Timhandl.postDelayed(checkSpeackStart, 1111);
    }
    private String suerByList(String text) {
        for (int i = 0; i < suer_adapter.getCount(); i++) {
            SuEr_Item item = suer_adapter.getItem(i);
            if (item==null) continue;
            text = text.replaceAll(item.suche, item.ersetze);
        }
        return text;
    }
    private void checkRecorded() {
        if (curTTS_SpeakText==null) return;
        String spk = textSpoken; // binding.recognizedText.getText().toString();
        String text= curTTS_SpeakText.toLowerCase(Locale.getDefault());

        if (spk.length()>text.length()+6) {
            int mi=saw_cnt;  saw_cnt=0;
            getSpeakText();
            text= curTTS_SpeakText.toLowerCase(Locale.getDefault());
            saw_cnt=mi;
        }
        spk = gc.formatText(spk);   saw_WordsOk_cnt=0;
        spk= spk.toLowerCase(Locale.getDefault());//

        text = gc.formatText(text);
        if (text.isEmpty() || spk.isEmpty()) return;

        text = suerByList(text);
        spk = suerByList(spk);

        int  cnt=0, idxText=0, idxSpk=0;
        char charText, charSpk;

        while (idxText < text.length() && idxSpk < spk.length()) {

            charSpk = spk.charAt(idxSpk);   charText =text.charAt(idxText);

            if (charSpk==' ' && charText != ' ') {
                idxSpk++; cnt++;
                continue;
            }
            if (charText==' ' && charSpk != ' ') {
                idxText++;  continue;
            }

            if (charSpk == charText) {
                cnt ++;
                if (charSpk==' ') saw_WordsOk_cnt++;
            } else break;

            idxText++; idxSpk++;
        }


        SpannableString spannableString = new SpannableString(spk);
        //BackgroundColorSpan[] backgroundSpans = spannableString.getSpans(0, spannableString.length(), BackgroundColorSpan.class);
        spannableString.setSpan(new BackgroundColorSpan(Color.parseColor("#306533")), 0, cnt, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.recognizedText.setText(spannableString);
        afterRecordCheck();
    }


    public void sbtSearchClick(View v) {
        int  idx=gc.csvList().findText(binding.sedSeek.getText().toString(), gc.LernData_Idx+1);
        if (idx < 0) return;
        gc.LernItem=gc.csvList().getLernData(idx);
        setFields();
    }
    public void sbtPrevDataClick(View v) {
        doCurDataIdx(false);
    }
    public void sbtNextDataClick(View v) {
        doCurDataIdx(true);
    }

    public void startRecording() {
        if (bAutoHideText) binding.asfLText.setVisibility(View.INVISIBLE);
        binding.recognizedText.setHint("");  binding.recognizedText.setText("");
        textSpoken="";
        startSpeechToText();
    }
    private void stopRecording() {
        gc.speechToText().Stop();
        binding.asfLText.setVisibility(View.VISIBLE);
        binding.progressBar1.setVisibility(View.GONE);
        binding.sbtReci.setText(getString(R.string.record));
    }
    //2. onError if record btn
    public void ysbtRecognizeClick(View v) {
        if (!recording) {
            waitSec=0;  saw_cnt=-1;  ss = Saw_states.saw_wait;
            getSpeakText();
            timeRecOut = 3700; //Integer.parseInt(eReplaceS.getText().toString());
            binding.recognizedText.setText("");
            binding.sbtReci.setText(getString(R.string.stop));
            recording = true;
            // gc.speechToText().Stop();
            startRecording();
        } else {
            recording=false;
            stopRecording();
        }
    }
//https://medium.com/@andraz.pajtler/android-speech-to-text-the-missing-guide-part-1-824e2636c45a
    protected void startSpeechToText() {
        gc.speechToText().aySpeech = this;


        gc.speechToText().Start();
        binding.progressBar1.setVisibility(View.VISIBLE);
    }




    public void onError(int i) {//nichts mehr zu sagen
        String err = gc.speechToText().getErrorText(i);
        binding.ystvRecoToDo.setText(err);
        //gc.Logl("onErr ");
        if ( saw_RecPause && saw_RecPauseContinue) {
            String txt=textSpoken+" "+lastRecoTxt; //binding.recognizedText.getText().toString()
            binding.recognizedText.setText(txt);
            lastRecoTxt="";
            saw_RecPauseContinue=false;
            doneRecording();
            return;
        }
        if (recording) { //nur user stt
            ysbtRecognizeClick(null);
            return;
        }

        if (ss!=Saw_states.saw_wait) {
            ss = Saw_states.saw_userStop;
            gc.Logl("onErr saw_userStop?",false);
            speakAndWordsClick(null);
        }
        // resetSpeechRecognizer();
        binding.progressBar1.setVisibility(View.GONE);
//see: https://medium.com/@andraz.pajtler/android-speech-to-text-the-missing-guide-part-1-824e2636c45a
    } //onError

    public void onResults(Bundle bundle) { //=endof recognize
        //micButton.setImageResource(R.drawable.ic_mic_black_off);
        ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        assert data != null;
        String rectxt = textSpoken + " " + data.get(0); //binding.recognizedText.getText().toString()
        endRecoBeep(); //Hoppla einfach mit Aufnahme aufgehört?
        binding.recognizedText.setText(rectxt);
        gc.Logl("onResults Textlen: "+data.get(0).length(),false);

        if (saw_RecPause) {
            String vText = gc.formatText(gc.LernItem.Text);
            if (rectxt.length() > vText.length()-4) {
                binding.recognizedText.setText(rectxt);
                textSpoken = rectxt;
                doneRecording();
                return;
            }
            lastRecoTxt = rectxt;
            Timhandl.postDelayed(RunstartReco, 2511);
            saw_RecPauseContinue = true;
            return;
        } else {
            textSpoken = data.get(0);
        }
        doneRecording();
    }


    public void onReadyForSpeech() {
        binding.ystvRecoToDo.setText(R.string.speak_now);
        //if (!saw_RecPause) binding.recognizedText.setText(R.string.ready_to_hear);
    }

    public void onBeginningOfSpeech() {
        // binding.ystvRecoToDo.setText(R.string.speech_start); user starts speaking..
        binding.progressBar1.setIndeterminate(false);
        binding.progressBar1.setMax(100);
    }

    public void onRmsChanged(float rmsdB) {
        int rms = Math.round(rmsdB*10);
        binding.progressBar1.setProgress( rms );
        if (rms >= saw_MinRecLevel)
            saw_lastMicNoice=System.currentTimeMillis();
    }
    public void onEndOfSpeech() {
        //binding.recognizedText.setHint(R.string.recording_done);
        gc.Logl("onEndOfSpeech",false);
        binding.ystvRecoToDo.setText(R.string.recording_done);
        binding.progressBar1.setIndeterminate(true);
        binding.sbtReci.setText(getString(R.string.record));
    }

    private void doneRecording() {
        //recording=false;
        binding.asfLText.setVisibility(View.VISIBLE);
        binding.progressBar1.setVisibility(View.GONE);
        binding.sbtReci.setText(getString(R.string.record));
        checkRecorded();
    }
    public void as_menuClick(View view) {
        gc.PopsMenu(view, R.menu.speech_menu);
    }

    public void maautoHideClick(View view) {
        CheckBox cb = (CheckBox) view;
        bAutoHideText = cb.isChecked();
    }


    private static class ViewHolder { TextView tvSuch, tvErsetz; }

        private class Suer_Adapter extends LvAdapt<SuEr_Item> {
        private final LayoutInflater mInflater;

        public Suer_Adapter(Context context) {
            super(context, android.R.layout.simple_list_item_2);
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View view;
            ViewHolder holder;
            SuEr_Item item = getItem(position);


            if (convertView == null) {
                view = mInflater.inflate(R.layout.suerrow, parent, false);
                holder = new ViewHolder();

                holder.tvSuch = view.findViewById(R.id.tvSuch);
                holder.tvErsetz = view.findViewById(R.id.tvErsetze);
                view.setTag(holder);
            } else {  view = convertView;  holder = (ViewHolder) view.getTag(); }
            if (item!=null) {
                holder.tvSuch.setText(item.suche);
                holder.tvSuch.setBackgroundColor(Color.parseColor(item.ItemBakColor));
                holder.tvErsetz.setText(item.ersetze);
                holder.tvErsetz.setBackgroundColor(Color.parseColor(item.ItemBakColor));
            }
            return view;
        }

        private void loadFromFile() {
            if (!gc.dateien().openInputStream("SeekReplace.txt")) return;
            this.clear();
            while (gc.dateien().readLine()) {
                SuEr_Item item = new SuEr_Item();
                item.suche=gc.dateien().rLine;
                if (!gc.dateien().readLine()) break;
                item.ersetze=gc.dateien().rLine;
                this.add(item);
            }
            gc.dateien().closeInputStream();
            this.notifyDataSetChanged();
        }
        private void savetofile() {
            if (!gc.dateien().openOutputStream("SeekReplace.txt", Context.MODE_PRIVATE)) return;
            for (int i = 0; i < this.getCount(); i++) {
                SuEr_Item item = getItem(i);
                if (item==null) continue;
                gc.dateien().writeLine(item.suche);
                gc.dateien().writeLine(item.ersetze);
            }
            gc.dateien().closeOutputStream();
        }
    }

    public void onSwipeLeft() {
        finish();
        gc.activityStart(this, AyEntries.class);
    }
    public void onSwipeRight() {
        finish();
        gc.activityStart(this, AyLetters.class);
    }
    public void openKaldiTestClick(View view) {
        Uri uri = Uri.parse("https://huggingface.co/spaces/k2-fsa/text-to-speech"); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

}