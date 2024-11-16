package com.e.versmix.activ;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.e.versmix.MainActivity;
import com.e.versmix.R;
import com.e.versmix.itemLs.CsvData;
import com.e.versmix.utils.BaseSwipeActivity;
import com.e.versmix.utils.FileDlg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AyEntries extends BaseSwipeActivity implements AdapterView.OnItemClickListener {

    //private final Globus gc = (Globus) Globus.getAppContext();
    private CsvData curDataItem;
    private int     curDataidx, waitSec=10;
    private EditText eRecognizer, eIdx, eBibleVers, eBereich, eVersText, eTranslation,
            eSearch;
    private TextView tvCurDataFile;
    private ListPopupWindow listPopupWindow=null;
    private final List<String> bereichNames = new ArrayList<>();
    private final Handler Timhandl = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ay_entries);
        eRecognizer = findViewById(R.id.yedrecus);
        eIdx= findViewById(R.id.yedDataIdx);
        eBibleVers = findViewById(R.id.yedBibelstelle);
        eBereich = findViewById(R.id.yedBereich);
        eTranslation = findViewById(R.id.yedTranslation);
        eVersText = findViewById(R.id.sedVersTxt);
        eSearch = findViewById(R.id.yedSearchTxt);
        tvCurDataFile = findViewById(R.id.ytvcurSpruchFile);

        tvCurDataFile.setText(gc.SpruchFileName());


    /*      relativeLayout.setOnTouchListener(new OnSwipeTouchListener(this.getBaseContext()) {
            public void onSwipeTop() {
                // Toast.makeText(this, "top", Toast.LENGTH_SHORT).show();
                gc.toast("onSwipeTop");
            }
            public void onSwipeRight() {
                //   startActivity(new Intent(MyActivity.this,NewActivity.class));
                // finish();
                gc.toast("onSwipeRight");
            }
            public void onSwipeLeft() {
                // Toast.makeText(MyActivity.this, "left", Toast.LENGTH_SHORT).show();
                gc.toast("onSwipeLeft");
            }
            public void onSwipeBottom() {
                // Toast.makeText(MyActivity.this, "bottom", Toast.LENGTH_SHORT).show();
                gc.toast("onSwipeBottom");
            }

        });

      eCurDataFile.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {  }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count==0 || s==null || s.length()==0) return;
                if (waitSec==10) Timhandl.postDelayed(saveNewName, 1111);
                waitSec+=6;

            }
        });  */

        doListPopup();
        loadData();
        doSharedText();
    }
    private void doSharedText() {
        if (gc.sharedText==null || gc.sharedText.length() < 5) return;
        ybtclearFieldsClick(null);
        eVersText.setText(gc.sharedText);
        ybtAddDataClick(null);
        gc.sharedText = null;
    }
    @Override
    public void onBackPressed() {
        gc.doBackPressed();
        super.onBackPressed();
        finish();
    }
    private final Runnable saveNewName = new Runnable() {  //geht nur wenn handy eingeschalten ist.
        @Override
        public void run() {
            waitSec--;
            if (waitSec>0) {
                Timhandl.postDelayed(saveNewName, 1111);
                return;
            }
            String fn= tvCurDataFile.getText().toString();
            gc.appVals().valueWriteString("eCurDataFile", fn);
            gc.csvList().saveToPrivate(fn,'#');
            gc.toast("Saved to: "+fn);
        }
    };
    private final Runnable runPopupList = new Runnable() {  //geht nur wenn handy eingeschalten ist.
        @Override
        public void run() {
            if (eBereich.getSelectionEnd()-eBereich.getSelectionStart() > 0) return;
            String txt=eBereich.getText().toString();
            if (txt.length() < 3 || bereichNames.size()>1)
                listPopupWindow.show();
        }
    };
    private boolean hasBereich(String txt) {
        for (String s: bereichNames) {
            if (s.contains(txt)) return true;
        }
        return false;
    }
    private void doListPopup() {
        if (eBereich==null) return;
        bereichNames.clear();
        int cnt = gc.csvList().dataList.size();
        if (cnt>2)
            for (int i = 1; i < cnt; i++) {
                CsvData csvData = gc.csvList().dataList.get(i);
                if (!hasBereich(csvData.Bereich))
                    bereichNames.add(csvData.Bereich);
            }
        Collections.sort(bereichNames);

        if (listPopupWindow!=null) return;

        listPopupWindow = new ListPopupWindow(
                this);
        listPopupWindow.setAdapter(new ArrayAdapter<>( this,
                R.layout.lpw_item, bereichNames));
        listPopupWindow.setAnchorView(eBereich);
        listPopupWindow.setWidth(300);
        listPopupWindow.setHeight(400);

        listPopupWindow.setModal(false);
        listPopupWindow.setOnItemClickListener(this);//setOnClickListener


        eBereich.setOnFocusChangeListener((view, b) -> {
            if (eBereich==null) return;
            if (view.isFocused()) {
                Timhandl.postDelayed(runPopupList, 1111);

            }
        });
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view,  int position, long id) {
        String txt=eBereich.getText().toString() + " " + bereichNames.get(position);
        txt = txt.replace("  ", " ");
        eBereich.setText(txt);
        listPopupWindow.dismiss();
    }

    public void ybtclearFieldsClick(View v) {
//eVersText eBibleVers, eBereich, eTranslation;
        eVersText.setText("");
        eBibleVers.setText(""); eBereich.setText(""); eTranslation.setText("");

    }
    public void ybtPrevDataClick(View view) {
        doCurDataIdx(true);
    }
    public void ybtNextDataClick(View view) {
        doCurDataIdx(false);
        //ttobj.setLanguage(Locale.GERMAN);
        //gc.Logl(curDataItem.Text, true);startActivity(new Intent(android.provider.Settings.ACTION_VOICE_INPUT_SETTINGS), 0);

        //startActivity(Intent("com.android.settings.TTS_SETTINGS"))
    }
    public void ttsSettingsClick(View v) {
        gc.ttSgl().andoSetttings();
    }
    public void ybtdoSpeakClick(View view) {
        gc.ttSgl().speak(eVersText.getText().toString());
    }
    public void ybtDeleDataClick(View view) {
        gc.csvList().dataList.remove(curDataidx);
        doCurDataIdx(true);
        gc.csvList().saveToPrivate(gc.SpruchFileName(),'#');
    }
    private void adl(CsvData csvData) {
        curDataItem = csvData;
        if (curDataItem==null) return;
        gc.csvList().dataList.add(curDataidx, csvData);
        setFields();
        doListPopup();
        gc.toast("Data added");
    }
    public void ybtAddDataClick(View view) {
        CsvData csvData = new CsvData();
        FieldsToItem(csvData); //

        if (gc.csvList().findText(csvData.Text, 0)>-1 ) {
            gc.askDlg("Text doppelt, dennoch hinzufügen?", () -> adl(csvData));
            return;
        }
        adl(csvData);
    }
    public void ybtChangeDataClick(View view) {
        FieldsToItem(curDataItem);
        gc.csvList().saveToPrivate(gc.SpruchFileName(),'#');
        doListPopup();
    }

    public void ybtSearchClick(View view) {
        int  idx=gc.csvList().findText(eSearch.getText().toString(), curDataidx+1);
        if (idx < 0) return;
        curDataItem=gc.csvList().getLernData(idx);
        curDataidx=idx;
        setFields();
    }
//  START         **************  extra save / load / restore / share ....   START

    public void ybtloadOrgDataClick(View view) {
        String fn=getString(R.string.spruch_csv);
        if (!gc.dateien().assetFileToPrivate(fn)) {
            gc.Logl("failed get org", true);
            return;
        }
        gc.csvList().readFromPrivate(fn, '#');

        tvCurDataFile.setText(fn);
        gc.appVals().valueWriteString("eCurDataFile", fn);

    }
    public void ybtshareDataClick(View view) {
        gc.dateien().shareFile(gc.SpruchFileName(), this);
    }

    public void ybtSaveAsClick(View view) {
        FileDlg fileDlg = new FileDlg(this ,
                "FileSave", //or FileSave or FileSave..  ..= chosenDir with dir | or FileOpen
                ".csv",
                chosenDir -> {
                    tvCurDataFile.setText(chosenDir);
                    gc.csvList().saveToPrivate(chosenDir,'#');
                    gc.appVals().valueWriteString("eCurDataFile", chosenDir);
                }
        );
        //fileDlg.create(); //!
        fileDlg.chooseFile_or_Dir(getFilesDir().getAbsolutePath());

    }
    public void ybtOpenClick(View view) {
        FileDlg fileDlg = new FileDlg(this ,
                "FileOpen", //or FileSave or FileSave..  ..= chosenDir with dir | or FileOpen
                ".csv",
                chosenDir -> {
                    tvCurDataFile.setText(chosenDir);
                    gc.csvList().readFromPrivate(chosenDir,'#');
                    gc.appVals().valueWriteString("eCurDataFile", chosenDir);
                    loadData();
                }
        );
        //fileDlg.create(); //!
        fileDlg.chooseFile_or_Dir(getFilesDir().getAbsolutePath());
    }
    private final int PICKFILE_RESULT_CODE = 123;
    public void ybtChooserClick(View view) {
        if (!gc.checkPermissions(true, Manifest.permission.READ_EXTERNAL_STORAGE)) return;
        Intent chooseFile;
        Intent intent;
//https://developer.android.com/training/data-storage/shared/documents-files?hl=de
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("text/plain");
        //ne chooseFile.setType("*/*"); = alle or text/*  = all text like rtf, html jons
        intent = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(intent, PICKFILE_RESULT_CODE);
    }

    //private String displayName = null;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode){
            case PICKFILE_RESULT_CODE:
                if(resultCode== Activity.RESULT_OK){
                    Uri uri = data.getData();
                    String displayName = gc.dateien().getDisplayName(uri);

                    if (gc.dateien().hasPrivateFile(displayName))
                        gc.askDlg(getString(R.string.overwrite)+displayName, () ->  {
                            gc.dateien().copyFileToPrivate(uri, displayName);
                        }); else
                            gc.dateien().copyFileToPrivate(uri, displayName);
                }
                break;
            case 23: break; //dummy
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // **************  extra save / load / restore / share ....   END


    private void doCurDataIdx(boolean Minus) {
        curDataidx = gc.csvList().doDataIdx(Minus, Integer.parseInt(eIdx.getText().toString()));
        curDataItem = gc.csvList().dataList.get(curDataidx);

        if (curDataItem==null) return;
        setFields();
    }
    private void setFields() {
        eVersText.setText(curDataItem.Text);
        eBereich.setText(curDataItem.Bereich);
        eBibleVers.setText(curDataItem.Vers);
        eTranslation.setText(curDataItem.Translation);
        eIdx.setText( String.format(Locale.getDefault(),"%d", curDataidx) );
        ((TextView)  findViewById(R.id.ytvCount) ).setText( String.format(Locale.getDefault(),"%d", gc.csvList().dataList.size()-1) );
    }
    private String RemoveLineEndings(String value)   {
        if(value==null || value.isEmpty())
        {
            return value;
        }
        String lineSeparator = String.valueOf(((char) 0x2028));
        String paragraphSeparator = String.valueOf(((char) 0x2029));

        return value.replace("\r\n", "")
                .replace("\n", "")
                .replace("\r", "")
                .replace(lineSeparator, "")
                .replace(paragraphSeparator, "");
    }
    private void FieldsToItem(CsvData item) {
        item.Text = RemoveLineEndings(eVersText.getText().toString());
        item.Bereich = eBereich.getText().toString();
        item.Vers = eBibleVers.getText().toString();
        item.Translation = eTranslation.getText().toString();
        //((EditText)  findViewById(R.id.yedDataIdx) ).setText( String.format(Locale.GERMAN,"%d", curDataidx) );

    }
    private void loadData() {
        if (gc.csvList().dataList.size()>3) {
            curDataidx=gc.LernData_Idx;
            curDataItem=gc.csvList().getLernData(curDataidx);
            setFields();
        }
    }
    private SpeechRecognizer speechRecognizer=null;
    private Intent speechRecognizerIntent;
    private boolean recording = false;
    public void ybtRecognizeClick(View v) {

        if (!gc.checkPermissions(false, Manifest.permission.RECORD_AUDIO)
            && !gc.checkPermissions(true, Manifest.permission.RECORD_AUDIO)) return;

        if (speechRecognizer==null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            Long googleVoiceTimeout = 15000L;

            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, googleVoiceTimeout);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, googleVoiceTimeout);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,googleVoiceTimeout);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true);


            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle bundle) {
                    eRecognizer.setText(R.string.ready);
                }

                @Override
                public void onBeginningOfSpeech() {
                    eRecognizer.setText("");
                    eRecognizer.setHint("Speak now...");
                }

                @Override
                public void onRmsChanged(float v) {

                }

                @Override
                public void onBufferReceived(byte[] bytes) {

                }

                @Override
                public void onEndOfSpeech() {
                    recording=false;
                    eRecognizer.setHint("Recording done");
                }

                @Override
                public void onError(int i) {
                    String err = "Error: "+i;
                    eRecognizer.setText(err);
//see: https://medium.com/@andraz.pajtler/android-speech-to-text-the-missing-guide-part-1-824e2636c45a
                }

                @Override
                public void onResults(Bundle bundle) {
                    //micButton.setImageResource(R.drawable.ic_mic_black_off);
                    ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    assert data != null;
                    eRecognizer.setText(data.get(0));
                    recording=false;
                }

                @Override
                public void onPartialResults(Bundle bundle) {
                    ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    assert data != null;
                    eRecognizer.setText(data.get(0));
                }

                @Override
                public void onEvent(int i, Bundle bundle) {

                }
            });

        }

        if (!recording) {
            speechRecognizer.startListening(speechRecognizerIntent);
            recording = true;
        } else {
            recording=false;
            speechRecognizer.stopListening();
        }

        //
    }
    public void ae_menuClick(View view) {
        gc.PopsMenu(view, R.menu.entries_menu);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer!=null) {
            speechRecognizer.destroy();  speechRecognizer=null;
        }
    }

    public void ae_doPopUpClick(View view) {
        listPopupWindow.show();
    }

    public void onSwipeLeft() {
        finish();
        gc.activityStart(this, MainActivity.class);
    }
    public void onSwipeRight() {
        finish();
        gc.activityStart(this, AySpeech.class);
    }

}