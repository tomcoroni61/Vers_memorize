package com.e.versmix.utils;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.TaskInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.e.versmix.MainActivity;
import com.e.versmix.R;
import com.e.versmix.activ.AyClickW;
import com.e.versmix.activ.AyLetters;
import com.e.versmix.activ.AySpeech;
import com.e.versmix.activ.AyWords;
import com.e.versmix.activ.AyEntries;
import com.e.versmix.itemLs.CsvData;
import com.e.versmix.itemLs.CsvList;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Locale;

public class Globus extends Application {

    public Dateien dateien() {
        if (mDats==null) mDats = new Dateien();
        return mDats;
    }
    public CsvList csvList() {
        if (mCsvList==null) {
            mCsvList = new CsvList(this);
            String fname = SpruchFileName();
            if (!mCsvList.readFromPrivate(fname, '#')) {
                if (appVals().valueReadBool("welcome", false))
                    Logl(fname + " not found, read Org now", true);
                mCsvList.readFromAssets(getString(R.string.spruch_csv), "#");
                mCsvList.saveToPrivate(fname, '#');
            }
        }
        return mCsvList;
    }
    public TTSgl ttSgl() {
        if (mTTSgl==null) mTTSgl = new TTSgl(mainActi);
        return mTTSgl;
    }
    public StT speechToText() {
        if (mStT==null) mStT = new StT();
        return mStT;
    }
    public AppVals appVals() {
        if (mAppVals ==null) mAppVals = new AppVals();
        return mAppVals;
    }
    public Context mainActi;
    public MainActivity mainActivity=null;
    public ErrorReporter errorReporter=null;
    public long pressedTime, lastSwipe;
    public int LernData_Idx;
    public CsvData LernItem;
    public String sharedText;

    public static Context getAppContext() {
        return appContext;
    }
    public void Logl(String msg, boolean mitToast) {
        Log.d(LOG_TAG, msg);
        if (mitToast) Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
    public interface AskDlgOkEve {
        void onOkClick();
    }
/*
gc.askDlg("Crashreport found, Okay to send email?", () ->  {
                });
    gc.showPopupWin(this.tvTextvs, gc.LernItem.Text, this::loadText);
    gc.showPopupWin(gridView, csvData.Text, () -> newText(true));
 */
    public void askDlg(String ask, AskDlgOkEve askDlgOkEve) {
        askDlg(getString(R.string.app_name), ask, askDlgOkEve);
    }

    public void askDlg(String title, String ask, AskDlgOkEve askDlgOkEve) {
        TextView tv = new TextView(mainActi);
        ask="\n"+ask;
        tv.setText(ask);
        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setGravity(Gravity.CENTER);
        new AlertDialog.Builder(mainActi)
                .setTitle(title)
                .setView(tv)
                .setPositiveButton("Ok", (dialog, id) -> {
                     //finish();
                     if (askDlgOkEve!=null) askDlgOkEve.onOkClick();
                 })
                .setNegativeButton("No", null).show();
        //d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        //alertDialog.show();
    }
    public int getPopUpWidth() {
        WindowManager w = mainActivity.getWindowManager();
        Point size = new Point();
        w.getDefaultDisplay().getSize(size);
        int wid = size.x;
        if (wid>size.y) wid=size.y;
        wid = wid - (wid/6);
        return wid;
    }

/*
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.wpopup, null);

        TextView textView = popupView.findViewById(R.id.tv_popup);
        if (textView !=null)
            textView.setText(gc.csvList().RandomItem.Text);
        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        Button bt = popupView.findViewById(R.id.pwwordmix);
        bt.setVisibility(View.INVISIBLE);
        popupWindow.showAtLocation(this.layout, Gravity.CENTER, 0, 0);

 */
    private byte bpcnt=0;
    public void doBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis())
            bpcnt++; else bpcnt=0;
        if (bpcnt > 2)
            askDlg(getString(R.string.exit_app_now), () ->  {
                currentActivity.finishAffinity();
            });
        pressedTime = System.currentTimeMillis();
    }
    private String getCurActivity() {
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        //List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        //List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();

        List<ActivityManager.AppTask> tasks = am.getAppTasks();
        String ret="ne";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            for (ActivityManager.AppTask task : tasks) {
                TaskInfo taskInfo = task.getTaskInfo(); //.topActivity.getClassName()
                 {
                    if (taskInfo.isRunning) {
                        if (taskInfo.topActivity==null) return "";
                        ret = taskInfo.topActivity.getClassName();
                        //Logl("CURRENT Activity ::" + ret, true);

                    }
                }
                // Log.d(TAG, "stackId: " + task.getTaskInfo().stackId);
            }
        return ret;
        //ComponentName componentInfo = taskInfo.get(0).topActivity;
        //componentInfo.getPackageName();
    }
    public void showPopupWin(View v, String txt) {
        showPopupWin(v, txt, null);
    }
    public void showPopupWin(View v, String txt, AskDlgOkEve askDlgOkEve) {
        int wid = getPopUpWidth();
        String curAyName= getCurActivity();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.wpopup, null);
        popupView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        final PopupWindow pw = new PopupWindow(popupView, wid,//MATCH_PARENT geht
                RelativeLayout.LayoutParams.WRAP_CONTENT, true);
//final PopupWindow pup = new PopupWindow(pupLayout, android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
// android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView textView = popupView.findViewById(R.id.tv_popup);
        if (textView==null) return;
        textView.setText(txt);

        //textView.setOnClickListener(view1 -> { keine wirkung pw.setHeight

        Button bt = popupView.findViewById(R.id.pwwordmix);
        if (curAyName.indexOf("AyWords")>0)
            bt.setVisibility(View.INVISIBLE);
        bt.setOnClickListener(view1 -> {
            // Toast.makeText(this, " clicked", Toast.LENGTH_LONG).show();
            pw.dismiss();
            activityStart(mainActi, AyWords.class);
        });

        bt = popupView.findViewById(R.id.pwwordclck);
        if (curAyName.indexOf("AyClickW")>0)
            bt.setVisibility(View.INVISIBLE);
        bt.setOnClickListener(view1 -> {
            // Toast.makeText(this, " clicked", Toast.LENGTH_LONG).show();
            pw.dismiss();
            activityStart(mainActi, AyClickW.class);
        });

        bt = popupView.findViewById(R.id.pwMain);
        if (curAyName.indexOf("MainActivity")>0)
            bt.setVisibility(View.INVISIBLE);
        bt.setOnClickListener(view1 -> {
            // Toast.makeText(this, " clicked", Toast.LENGTH_LONG).show();
            pw.dismiss();
            activityStart(mainActi, MainActivity.class);
        });

        bt = popupView.findViewById(R.id.pwspeakclck);
        bt.setOnClickListener(view1 -> {
            ttSgl().speak(txt);
        });

        bt = popupView.findViewById(R.id.pwworkclck);
        if (curAyName.indexOf("AyEntries")>0)
            bt.setVisibility(View.INVISIBLE);
        bt.setOnClickListener(view1 -> {
            pw.dismiss();
            activityStart(mainActi, AyEntries.class);
        });

        bt = popupView.findViewById(R.id.pwsaylck);
        if (curAyName.indexOf("AySpeech")>0)
            bt.setVisibility(View.INVISIBLE);
        bt.setOnClickListener(view1 -> {
            pw.dismiss();
            activityStart(mainActi, AySpeech.class);
        });

        bt = popupView.findViewById(R.id.pwletters);
        if (curAyName.indexOf("AyLetters")>0)
            bt.setVisibility(View.INVISIBLE);
        bt.setOnClickListener(view1 -> {
            pw.dismiss();
            activityStart(mainActi, AyLetters.class);
        });

        bt = popupView.findViewById(R.id.pwretry);
        if (askDlgOkEve==null) bt.setVisibility(View.INVISIBLE); else
            bt.setOnClickListener(view1 -> {
                pw.dismiss();
                askDlgOkEve.onOkClick();
            });

        pw.setAnimationStyle(android.R.anim.fade_in);
        RelativeLayout relativeLayout  =  popupView.findViewById(R.id.rlpop);
        int hei = getHeight(relativeLayout, wid)+20;
        pw.setHeight(hei);//+ (pw.getWidth()/4)
        pw.showAtLocation(v, Gravity.CENTER, 0, 0);
    }
/*
        int lenTxt=txt.length();  double div = 0.000;
        //if (lenTxt < 30)
        int hei = getHeight(textView, wid/2)+70;
        //pw.setHeight(textView.getHeight());
        //pw.setHeight( (bt.getMeasuredHeight()*2) + textView.getMeasuredHeight() );
        //pw.setHeight( getHeight(textView, pw.getWidth()) + getHeight(textView, pw.getWidth()) );
        popupView.measure(View.MeasureSpec.makeMeasureSpec(wid, // findViewById(R.id.main).getWidth(),
                View.MeasureSpec.AT_MOST), View.MeasureSpec.UNSPECIFIED);
        // pw.setWidth(popupView.getMeasuredWidth());
        textView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

 */
    public static int getHeight(View t, int w) {
        //t.setWidth(w);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        t.measure(widthMeasureSpec, heightMeasureSpec);
        return t.getMeasuredHeight();
    }

    public void activityStart(Context context, Class<?> cls) {
        try {
        Context  ctx=context;
        if (ctx==null)
            ctx=getApplicationContext();
        Intent intent = new Intent(ctx, cls);
        if (context==null) //immer neu damit back to main .. hilft nicht
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
        }  catch (  Exception e) {
           Logl("Start Bug " + e.getMessage(), true);
           if (errorReporter!=null) {
               //errorReporter.
               errorReporter.doException(e);
           }

        }
    }
    public boolean checkPermissions(boolean askUser, String... permissionsId) {
        boolean permissions = true;
        for (String p : permissionsId) { //permissions &&
            permissions = ContextCompat.checkSelfPermission(mainActi, p) == PERMISSION_GRANTED;

            int callbackId=0; //42 falls user ablehnt, hier egal requestPermissions
            if (askUser && !permissions) {
                //if (permissions)                Toast.makeText(mainActi, Arrays.toString(permissionsId) +" ok, change in Settings", Toast.LENGTH_LONG).show();
                requestPermissions(mainActivity, permissionsId, callbackId);
                //permissions = false;
            }
        }

        return permissions;
    }

    private static Context appContext;
    private static final String LOG_TAG = "merried";
    private Dateien mDats;
    private CsvList mCsvList;
    private TTSgl   mTTSgl;
    private AppVals mAppVals;
    private StT     mStT;

    public String formatTextUpper(String str) {
        return formatText(str).toUpperCase(Locale.getDefault());
    }
    public String formatText(String strIn) {
        String str=strIn.trim();
        StringBuilder sz = new StringBuilder();
        //str=str.toUpperCase(Locale.getDefault());

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') ){
                sz.append(c);
            } else
                switch (c) {
                    case 'ö': case 'Ö': case 'ä': case 'Ä':
                    case 'ü': case 'Ü': case 'ß':  sz.append(c); break;
                    case ' ': if (sz.length()>1 && sz.charAt(sz.length()-1) != ' ') sz.append(c); break;
                    default: sz.append(' '); break;
                }

        }
        str=sz.toString().replaceAll(" {2}", " ");
        return str;
    }
    public void PopsMenu(View v, int menuResi) {
        TextView viewx = (TextView) v;
        PopupMenu popupMenu = new PopupMenu(this.mainActivity, viewx);
        popupMenu.getMenuInflater().inflate(menuResi, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            doMenu(menuItem);
            return true;
        });
        popupMenu.show();
    }
    public String SpruchFileName() {
        return appVals().valueReadString("eCurDataFile", getString(R.string.spruch_csv));
    }
    private void doMenu(MenuItem item) {
        int id = item.getItemId(); //

        switch (id) {
            case R.id.mEntries:
                activityStart(currentActivity,  AyEntries.class);
                break;
            case R.id.mClickWord:
                activityStart(currentActivity,  AyClickW.class);
                break;
            case R.id.mSpeech:  activityStart(currentActivity, AySpeech.class); break;
            case R.id.mWordMix: activityStart(currentActivity,  AyWords.class);  break;
            case R.id.mLetters: activityStart(currentActivity,  AyLetters.class); break;
            case R.id.mMain:    activityStart(currentActivity,  MainActivity.class);  break;
            case R.id.mExit:    //if (currentActivity!=null) currentActivity.finish();= just return
                // activityStart(this,  MainActivity.class); //nicer? then System.exit(1);
                // mainActivity.finish();
                //ne: System.exit(0);

                if (currentActivity!=null) currentActivity.finishAffinity();
                mainActivity.finishAffinity();
                //int pid = android.os.Process.myPid();                android.os.Process.killProcess(pid);
                // System.exit(0);
            break;
        }

    }
    //@SuppressLint("SuspiciousIndentation")
    public void playaTone(double freqHz, int durationMs, double volume, boolean doSleep) {
        if (volume > 1 || volume < 0){
            volume = 1; //will make sure it isn't too loud
        }
        int count = (int)(44100.0 * 2.0 * (durationMs / 1000.0)) & ~1;
        short[] samples = new short[count];
        for(int i = 0; i < count; i += 2){
            short sample = (short)(volume * Math.sin(2 * Math.PI * i / (44100.0 / freqHz)) * 0x7FFF);
            samples[i] = sample;
            samples[i + 1] = sample;
        }
        AudioTrack track = null;
        try {
            track = new AudioTrack(AudioManager.STREAM_ALARM, 44100,
                    AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                    count * (Short.SIZE / 8), AudioTrack.MODE_STREAM);

            track.write(samples, 0, count); //track.
            track.play();
            if (doSleep) Thread.sleep(durationMs);
        }
        catch (Exception ignored){ }
        if (track != null) {
            if (doSleep) track.release(); else
                new Handler(Looper.getMainLooper()).postDelayed(track::release, durationMs + 55);

        }

    }

    private Thread.UncaughtExceptionHandler oldHandler;
    private void globalExceptionHandler() {
        //if (!BuildConfig.DEBUG)            return;

        oldHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            try {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                Intent intent = new Intent(this, Reportus.class);
                intent.putExtra("ErrMsg", sw.toString());//or..Intent.EXTRA_TEXT
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            } catch(Exception ex) {
               // ex.printStackTrace();
            } finally {
                if (oldHandler != null)
                    oldHandler.uncaughtException(t, e);
                else
                    System.exit(1);
                //System.exit();
            }
        });
    }
    private int startCount=0;
    @Override
    public void onCreate() {
        super.onCreate();  //Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        appContext = getApplicationContext();
        startCount++;
        globalExceptionHandler();
        //new Reportus("Nice to meet you");
        if (startCount>1) Logl("app starts: "+startCount, true);
        doLifeCycle();
    }
    private Activity currentActivity;
    private void  doLifeCycle() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
                currentActivity = activity;
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                currentActivity = activity;
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                currentActivity = activity;
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                currentActivity = null;
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                // don't clear current activity because activity may get stopped after
                // the new activity is resumed
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                // don't clear current activity because activity may get destroyed after
                // the new activity is resumed
            }
        });
    }
}

/*

    }
    private final Runnable r=new Runnable() {
        public void run() {
            //what ever you do here will be done after 3 seconds delay.
            if (alertDialog.isShowing()) handler.postDelayed(r, 1000); else {
                if (askDlgRet) Logl("oki", true); else
                    Logl("NOT oki", true);
            }
        }
    };
    private AlertDialog alertDialog;
    private final Handler handler=new Handler();
    public boolean askDlgRet;

Snackbar snackbar = Snackbar
        .make(ActivityLayout, "Your Message", Snackbar.LENGTH_LONG);
snackbar.show();
text = text.replaceAll("daß", "das");
        text = text.replaceAll("dass", "das");
        text = text.replaceAll(" {2}", " ");

        spk = spk.replaceAll("daß", "das");
        spk = spk.replaceAll("dass", "das");
        spk = spk.replaceAll(" {2}", " ");


<speak version="1.0" xml:lang="zh-CN"><voice name="zh-CN-XiaoxiaoNeural"><prosody rate="${(rate-100)?c}%" pitch="${(pitch-100)?c}%">${text}</prosody></voice></speak>
<speak version="1.0" xml:lang="de-DE"><voice name="de-DE-BerndNeural"><prosody rate="${(rate-100)?c}%" pitch="${(pitch-100)?c}%">${text}</prosody></voice></speak>
 */