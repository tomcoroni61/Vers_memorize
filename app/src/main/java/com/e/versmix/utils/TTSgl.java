package com.e.versmix.utils;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class TTSgl {
    public TextToSpeech ttobj;
    private final Context mContext;
    private final Globus gc = (Globus) Globus.getAppContext();
    private boolean initOk=false, doreinit=false;

    public TTSgl(Context context) {
        mContext = context;
        startTTS();
    }
    //if new engine is selected
    public void restart() {
        if (ttobj != null) {
            ttobj.stop();
            ttobj.shutdown();
            ttobj = null;
        }
        startTTS();
        gc.toast("TTS restart done");
        doreinit=false;
    }
    private void startTTS() {
        if (!doreinit) initOk=false;
        ttobj =new TextToSpeech(mContext, status -> {
            if(status == TextToSpeech.SUCCESS) {
                //ttobj.setLanguage(Locale.GERMANY);
                initOk=true; // gc.Logl("Press again, Speech ok", true);
            }// else gc.Logl("speech failed: "+status, true);
        });

    }
    public void andoSetttings() {
        Intent intent = new Intent();
        intent.setAction("com.android.settings.TTS_SETTINGS");
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        gc.mainActivity.startActivityForResult(intent, 55);
        doreinit=true;
    }

    public void speak(String txt) {
        if (doreinit) restart();
        if (txt.isEmpty()) return;
        if (!initOk || ttobj==null) {
            gc.toast("Init TTS failed!!");
            return;
        }
        if (ttobj.isSpeaking()) ttobj.stop(); else
            ttobj.speak(txt,
                    TextToSpeech.QUEUE_FLUSH, null, null);
    }
}

/*
private void speak(String s, String text) {
        try{
            float pitch = (float) 0.62;
            float speed = (float) 0.86;
            textToSpeech.setSpeechRate(speed);
            textToSpeech.setPitch(pitch);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Bundle bundle = new Bundle();
                bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
                textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, bundle, null);
                textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, bundle, null);
            } else {
                HashMap<String, String> param = new HashMap<>();
                param.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));
                textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, param);
                textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, param);
            }
        }catch (Exception ae){
            ae.printStackTrace();
        }
 */