package com.e.versmix;

import static com.e.versmix.utils.Fixi.Globis.merkVers;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;

import com.e.versmix.activ.AyClickW;
import com.e.versmix.activ.AyEntries;
import com.e.versmix.activ.AyLetters;
import com.e.versmix.activ.AySpeech;
import com.e.versmix.activ.AyWelcome;
import com.e.versmix.activ.AyWords;
import com.e.versmix.utils.BaseSwipeActivity;
import com.e.versmix.utils.FileDlg;
import com.e.versmix.utils.SimpleFileDialog;

import java.io.File;

public class MainActivity extends BaseSwipeActivity {
    public static final String LOG_TAG = "merried"; //MainActivity.class.getSimpleName();
    //private final Globus gc = (Globus) Globus.getAppContext();
    private TextView tvHeader, tvVersText, tvMerkVers, tvTopVers;
    private boolean bshowText=true;
    private EditText eSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int crashcnt=0;
        /* wifi debug  Hotspot Poco,  debug device SM-S911B
        SM-S911B öffnen und Entwickler --> Debugging über wlan

        cd C:\LwD\Programs\Android\SDK\platform-tools
        adb connect 192.168.122.104:39665   ..  connected to 192.168.122.104:39665
        //kleine grüne Punkt meint verbunden..
adb pair 192.168.122.104:35359

         */
        //
        //
        //adb pair 192.168.122.104:34889
        try {
            Log.d(LOG_TAG, "MainActivity On Create ");
            gc.mainActi = this;   gc.mainActivity=this;
            setContentView(R.layout.main_activity);  crashcnt=11;
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            crashcnt=6;
            tvVersText = findViewById(R.id.matvVersText);
            tvTopVers = findViewById(R.id.matvVersTop);

            crashcnt=61;//: to delete :
            handleReceiveShare();

            crashcnt = 2;
            tvHeader = findViewById(R.id.headerTextView); crashcnt=9;
            eSearch = findViewById(R.id.sedSeek);

            PopsMenu();
            crashcnt=22;
            mabtMischDataClick(tvHeader);
            crashcnt=11;
            gc.ttSgl().speak(""); crashcnt=91;
            tvMerkVers = findViewById(R.id.matvMerkVers);
            readMerkvers();
            if (!gc.appVals().valueReadBool("welcome", false)) {
                gc.appVals().valueWriteBool("welcome", true);
                gc.activityStart(this, AyWelcome.class);
            }

        }  catch (  Exception e) {
            gc.Logl("MA_Crash Nr: " + crashcnt + " Msg " + e.getMessage(), true);

        }

    }

    private void handleReceiveShare() {
        Intent intent = getIntent();
        String action = intent.getAction();// https://developer.android.com/training/sharing/receive?hl=de
        /* if (Intent.ACTION_SEND_MULTIPLE.equals(action)
        for multiple files ..in Manifest add:
        <intent-filter>
                <action android:name="android.intent.action.SEND_MULTIPLE"/>
                <data android:mimeType="* __/*"/>
                <category android:name="android.intent.category.DEFAULT"/>
            </intent-filter>
        single: <action android:name="android.intent.action.SEND" />

         */
        if (!Intent.ACTION_SEND.equals(action)) return;
//  MIME-Typen:  text/*  alle wie text/plain .. rtf,html .. text/json
//        String type = intent.getType(); // image/*
//        if ("text/plain".equals(type)) {

        gc.sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (gc.sharedText != null) {
            gc.askDlg(getString(R.string.add_to_your_bibleverses), gc.sharedText,
                    () -> gc.activityStart(this, AyEntries.class));
        }

        Uri uri = intent.getData();
        if (uri !=null) gc.askDlg("not supported","uri found: "+ uri, null);

        imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            displayName = gc.dateien().getDisplayName(imageUri);
            gc.askDlg(getString(R.string.copy_to_app_file_dir)+ "\n" + displayName, () ->  {
                if (gc.dateien().hasPrivateFile(displayName))
                    gc.askDlg(getString(R.string.overwrite)+displayName,
                            () -> gc.dateien().copyFileToPrivate(imageUri, displayName)); else
                    gc.dateien().copyFileToPrivate(imageUri, displayName);
            });
        }
    }
    private String displayName;
    private Uri imageUri;

    private void doCurDataIdx(boolean Minus) {
        gc.csvList().doLearnDataIdx(Minus);
        setFields();
    }
    private void setFields() {
        if (gc.LernItem==null) return;
        if (bshowText)   tvVersText.setText(gc.LernItem.Text);
            else tvVersText.setText("");

        tvHeader.setText(gc.LernItem.Vers);
        tvTopVers.setText(gc.LernItem.Vers);
    }

    private void PopsMenu() {
        TextView viewx = findViewById(R.id.mabottomRight);

        // Setting onClick behavior to the button
        viewx.setOnClickListener(view -> {
            // Initializing the popup menu and giving the reference as current context
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, viewx);

            // Inflating popup menu from popup_menu.xml file popup_menu
            popupMenu.getMenuInflater().inflate(R.menu.main_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                //gc.Logl("You Clicked " + menuItem.getTitle(), true);
                //Toast.makeText(MainActivity.this, "You Clicked " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                doMenu(menuItem);
                return true;
            });
            // Showing the popup menu
            popupMenu.show();
        });

    }

    private void doMenu(MenuItem item) {
        int id = item.getItemId(); //

        switch (id) {
            //case R.id.massetcop:
              //  gc.Logl( "Filecopy: " + gc.dateien().assetFileToPrivate(Spruch), true); break;
            //case R.id.mShare: gc.dateien().shareFile(Spruch, this); break;
            // case R.id.mShare2: gc.share_image(Spruch); break;
            case R.id.mEntries:
                if (!gc.dateien().hasPrivateFile(gc.SpruchFileName()))
                    gc.Logl(gc.SpruchFileName() + " File not found" , true); else
                        gc.activityStart(this,  AyEntries.class);
                break;
            case R.id.mClickWord:
                if (!gc.dateien().hasPrivateFile(gc.SpruchFileName()))
                    gc.Logl(gc.SpruchFileName() + " File not found" , true); else
                    try {
                        Intent intent = new Intent(this, AyClickW.class);  // **********     !! New activity also in AndroidManifest !! zipAlign true
                        startActivity(intent);
                    }  catch (  Exception e) {
                        gc.Logl("Entri Bug " + e.getMessage(), true);

                    }
                break;
            case R.id.mtfeili: filechooser(); break;
            case R.id.mfeilch2: filechooser2(); break;
            case R.id.mSpeech:  gc.activityStart(this, AySpeech.class); break;
            case R.id.mLayout:                showWordmix();                break;
            case R.id.mtxtvers:
                gc.showPopupWin(tvHeader, gc.LernItem.Text); break;
            case R.id.mdlg: filechooser3();

                //gc.askDlg("test", () -> gc.Logl("All OKay for it", true));

                break;
        }

    }
    
    // https://github.com/iPaulPro/aFileChooser
//https://stackoverflow.com/questions/38170660/how-to-create-a-file-chooser-dialog-with-android-studio
    private final int PICKFILE_RESULT_CODE = 123;
    private void filechooser() {
        if (!gc.checkPermissions(true, Manifest.permission.READ_EXTERNAL_STORAGE)) return;
        Intent chooseFile;
        Intent intent;
//https://developer.android.com/training/data-storage/shared/documents-files?hl=de
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        //chooseFile.setType("text/plain");
        chooseFile.setType("*/*");
        intent = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(intent, PICKFILE_RESULT_CODE);
        //startActivity(intent);
    }

/*
Storage Access Framework (ACTION_OPEN_DOCUMENT, ACTION_OPEN_DOCUMENT_TREE
 */
    private void filechooser2() {
        SimpleFileDialog fileOpenDialog =  new SimpleFileDialog(
                MainActivity.this,
                "FileOpen", //or FileSave or FileSave..  ..= chosenDir with dir FileOpen
                chosenDir -> {
                    // The code in this function will be executed when the dialog OK button is pushed
                    //editFile.setText(chosenDir);
                    gc.Logl(chosenDir, true);
                }
        );
        fileOpenDialog.chooseFile_or_Dir(getFilesDir().getAbsolutePath());
    }
    private void filechooser3() {
        try {
        FileDlg fileDlg = new FileDlg(this ,
                "FileOpen", //or FileSave or FileSave..  ..= chosenDir with dir FileOpen
                ".csv",
                chosenDir -> {
                    // The code in this function will be executed when the dialog OK button is pushed
                    //editFile.setText(chosenDir);
                    gc.Logl(chosenDir, true);
                }
        );
        //fileDlg.create(); //!
        fileDlg.chooseFile_or_Dir(getFilesDir().getAbsolutePath());
        }  catch (  Exception e) {
            gc.Logl("MA_Crash: " + e.getMessage(), true);

        }
    }
    private void showWordmix() {
        gc.activityStart(this, AyWords.class);

    }


    public void matxtClick(View view) {
        gc.showPopupWin(this.tvVersText, gc.LernItem.Text);
    }

    public void mattsSettingsClick(View view) {
        gc.ttSgl().andoSetttings();
    }
    public void mabtPrevDataClick(View view) {
        doCurDataIdx(true);
    }
    public void mabtNextDataClick(View view) {
        doCurDataIdx(false);
    }

    public void maShowTextClick(View view) {
        CheckBox cb = (CheckBox) view;
        bshowText = cb.isChecked();
        setFields();
    }


    public void maspeackClick(View view) {
        gc.ttSgl().speak(gc.LernItem.Text);
    }

    public void maSpeakRecClick(View view) {
        gc.activityStart(this, AySpeech.class);
    }

    public void maEditClick(View view) {
        gc.activityStart(this, AyEntries.class);
    }

    public void maWordClick(View view) {
        gc.activityStart(this, AyClickW.class);
    }

    public void maWordMixClick(View view) {
        gc.activityStart(this, AyWords.class);
    }

    public void mabtMischDataClick(View view) {
        gc.csvList().getRandomText();
        setFields();
    }

    public void maLettersClick(View view) {
        gc.activityStart(this, AyLetters.class);
    }

    public void maMenuClick(View view) {
        gc.PopsMenu(view, R.menu.maintop_menu);
    }

    public void mabtSearchClick(View view) {
        int  idx=gc.csvList().findText(eSearch.getText().toString(), gc.LernData_Idx+1);
        if (idx < 0) return;
        gc.csvList().getLernData(idx);
        setFields();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        switch(requestCode){
            case 55: gc.ttSgl().restart(); break;
            case PICKFILE_RESULT_CODE:
                Uri uri = data.getData();
                assert uri != null;
                String uriString = uri.toString();
                File myFile = new File(uriString);
                String displayName = null;

                if (uriString.startsWith("content://")) {
                    Cursor cursor;
                    cursor = getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int cidx=cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (cidx>-1)
                            displayName = cursor.getString(cidx);
                        cursor.close();
                    }

                } else if (uriString.startsWith("file://")) {
                    displayName = myFile.getName();
                }
                //Uri uri = data.getData();
                //String filePath = uri.getPath(); resultCode == Activity.RESULT_OK
                //gc.Logl(filePath, true);
                gc.dateien().copyFileToPrivate(uri, displayName);
                break;
            case 23: break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void readMerkvers() {
        String vers=gc.appVals().valueReadString(merkVers, "joh 3:16");
        tvMerkVers.setText(vers);
    }
    public void gSetLernVersClick(View view) {
        if (gc.LernItem==null) return;
        gc.appVals().valueWriteString(merkVers, gc.LernItem.Vers);
        readMerkvers();
        //
    }

    public void gMerk_to_LernVersClick(View view) {
        String vers = tvMerkVers.getText().toString();
        int idx = gc.csvList().hasBibleVers(vers);
        if (idx<0) return;
        gc.LernItem = gc.csvList().getLernData(idx);
        setFields();
    }

    public void onSwipeLeft() {
        gc.activityStart(this, AyClickW.class);
    }
    public void onSwipeRight() {
        gc.activityStart(this, AyEntries.class);
    }

    public void mabtWelcomeClick(View view) {
        gc.activityStart(this, AyWelcome.class);
    }
}

/*
try {
            Context  context =gc.getApplicationContext();
            Intent intent = new Intent(context, AyWords.class);  // **********     !! New activity also in AndroidManifest !! zipAlign true
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }  catch (  Exception e) {
            gc.Logl("Entri Bug " + e.getMessage(), true);

        }
    public void shareFile(String filePath) {

        File privateRootDir = getFilesDir();
        File file = new File(privateRootDir, filePath);

        try {
            Intent share = new Intent(Intent.ACTION_SEND);

            if (file.exists()) {
                share.setType("text/*");
                gc.Logl(" getUriForFile " + file.getAbsolutePath(), true);

                //Oks
                Uri uri =FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),  BuildConfig.APPLICATION_ID + ".provider", file);
                //Uri uri =FileProvider.getUriForFile(this,  BuildConfig.APPLICATION_ID + ".provider", file);


                //Uri uri = FileProvider.getUriForFile(gc.getApplicationContext(),"com.e.versmix.fileprovider",file);
                share.putExtra(Intent.EXTRA_STREAM,uri);
                gc.Logl(" shareFile startActivity ", true);
                startActivity(Intent.createChooser(share,"Share table"));

            } else
                gc.Logl(filePath + " not found", true);
        }  catch (  Exception e) {
            gc.Logl("ShareFile_Crash: " + e.getMessage(), true);

        }

    }

popupView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
 */