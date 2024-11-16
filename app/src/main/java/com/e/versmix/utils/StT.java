package com.e.versmix.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import com.e.versmix.activ.AySpeech;

import java.util.Locale;

// extends AndroidNonvisibleComponent  impl , OnDestroyListener, OnInitializeListener
public class StT  implements RecognitionListener {

    public AySpeech aySpeech;

    private final Globus gc = (Globus) Globus.getAppContext();
    private AudioManager audioManager = null;
    private SpeechRecognizer speech = null;
    private final Intent recognizerIntent;
    boolean havePermission;
    boolean soundEnabled = true;
    boolean isRunning = false;
    String language = Locale.getDefault().getLanguage();

    public StT () {
        gc.Logl("recognizer on Create", false);
        audioManager = (AudioManager) gc.getSystemService(Context.AUDIO_SERVICE);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        havePermission = gc.checkPermissions(false, Manifest.permission.RECORD_AUDIO);
    }

    public String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            case SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED:
                message = "Language Not supported";
                break;
            case SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE:
                message = "Language Unavailable";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    public boolean IsRecognitionAvailable() {
        return SpeechRecognizer.isRecognitionAvailable(gc);
    }

    public void SoundEnabled(boolean value) {
        soundEnabled = value;
        // Android 8 xiaomi
        audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, !value);
    }

    public String Language() {
        return language;
    }

    public void Language(String language) {
        this.language = language;
        if (isRunning)
            Stop();
        if (language.trim() == "") {
            recognizerIntent.removeExtra(RecognizerIntent.EXTRA_LANGUAGE);
        } else {
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        }
        Start();
    }

    private long recognizeStart;
    private byte startTries;
    @Override
    public void onReadyForSpeech(Bundle params) {
        aySpeech.onReadyForSpeech();
    }

    @Override
    public void onBeginningOfSpeech() {
        aySpeech.onBeginningOfSpeech();
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        aySpeech.onRmsChanged(rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        aySpeech.onEndOfSpeech();

        //speech.stopListening();
    }

    @Override
    public void onError(int errorNumber) {
        gc.Logl("onErr run:"+isRunning,false);
        if (!isRunning) {
            aySpeech.startRecording();
            return;
        }
        if (startTries < 4 && System.currentTimeMillis()-recognizeStart < 4444) {
            startTries++;
            Start(true);
            return;
        }
        // if (errorNumber == 8) return;
        aySpeech.onError(errorNumber);
        // if (isRunning)            speech.startSpeechToText(recognizerIntent);

    }

    @Override
    public void onResults(Bundle results) {
        aySpeech.onResults(results);
        // speech.stopListening();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    public void Start() {
        // if (speech!=null) speech.stopListening();
        Start(false);
    }
    public void Start(boolean retreing) {
        gc.Logl("reco Start   retr: "+retreing, false);
        if (!retreing) {
            recognizeStart = System.currentTimeMillis();
            startTries = 0;
        }

        if (!havePermission) {
            if (!gc.checkPermissions(true, Manifest.permission.RECORD_AUDIO)) return;
            havePermission = true;
            new Handler(Looper.getMainLooper()).postDelayed(this::Start, 41000);
        }
//        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(gc));
         // else            gc.ErrorOccurred(this, "Start", 38107, "Speech recognition is not available");
        if (SpeechRecognizer.isRecognitionAvailable(gc) && speech==null) {
            speech = SpeechRecognizer.createSpeechRecognizer(gc);
            speech.setRecognitionListener(this);
        }

        if (speech==null) return;
        // speech.stopListening();
        isRunning = true;
        speech.startListening(recognizerIntent);
    }
    public void Stop() {
        isRunning = false;
        if (speech!=null) speech.stopListening();
        //SoundEnabled(true);
    }
}
