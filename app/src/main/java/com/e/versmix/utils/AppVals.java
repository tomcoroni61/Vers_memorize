package com.e.versmix.utils;

import android.content.Context;

import java.util.ArrayList;
// more in saveload_helper..
public class AppVals extends Dateien {

    private final String aFileName = "appVals.txt";
    private String curValueName;

    public boolean valueReadBool( String valueName, boolean defValue) {
        if (!openInputStream(aFileName)) return defValue;
        while (readLine() ) {
            if (rLine.equals(valueName)) {
                curValueName=valueName;
                return readBool();
            } }
        closeInputStream();
        return defValue;
    }
    public void valueWriteBool(String valueName, boolean value) {
        ArrayList<String> list = new ArrayList<>();
        int cnt = 0;
        int fIdx = -1;

        if (openInputStream(aFileName)) {
            while (readLine()) {
                if (!list.add(rLine)) break;
                cnt++;
                if (!rLine.isEmpty() && rLine.equals(valueName)) fIdx = cnt;
            }
            closeInputStream();
        }

        if (fIdx==-1) {list.add(valueName); list.add(Boolean.toString(value)); } else
            list.set(fIdx, Boolean.toString(value));
        //list.add(0," ");  list.add(0,Line1);   list.add(0,Line2);
        if (!openOutputStream(aFileName, Context.MODE_PRIVATE)) return;
        for (String aStrLine : list) {
            if (!writeLine(aStrLine)) return;
        }
        closeOutputStream();
    }

    public void valueWriteString(String valueName, String value) {
        ArrayList<String> list = new ArrayList<String>();
        int cnt = 0;
        int fIdx = -1;

        if (openInputStream(aFileName)) {
            while (readLine()) {
                if (!list.add(rLine)) break;
                cnt++;
                if (!rLine.isEmpty() && rLine.equals(valueName)) fIdx = cnt;
            }
            closeInputStream();
        }

        if (fIdx==-1 || fIdx==list.size()) {list.add(valueName); list.add(value); } else
            list.set(fIdx,value);
        //list.add(0," ");  list.add(0,Line1);   list.add(0,Line2);
        if (!openOutputStream(aFileName, Context.MODE_PRIVATE)) return;
        for (String aStrLine : list) {
            if (!writeLine(aStrLine)) return;
        }
        closeOutputStream();
    }
    public String valueReadString( String valueName, String defValue) {
        if (!openInputStream(aFileName)) return defValue;
        while (readLine() ) { if (rLine.equals(valueName)) return readString(); }
        closeInputStream();
        return defValue;
    }

    public boolean valueWriteInteger( String valueName, int value) {
        ArrayList<String> list = new ArrayList<String>();
        int cnt = 0;
        int fIdx = -1;

        if (openInputStream(aFileName)) {
            while (readLine()) {
                if (!list.add(rLine)) break;
                cnt++;
                if (!rLine.isEmpty() && rLine.equals(valueName)) fIdx = cnt;
            }
            closeInputStream();
        }

        if (fIdx==-1) {list.add(valueName); list.add(Integer.toString(value)); } else
            list.set(fIdx, Integer.toString(value));
        //list.add(0," ");  list.add(0,Line1);   list.add(0,Line2);
        if (!openOutputStream(aFileName, Context.MODE_PRIVATE)) return false;
        for (String aStrLine : list) {
            if (!writeLine(aStrLine)) return false;
        }
        closeOutputStream();  return true;
    }

    public int valueReadInt( String valueName, int defValue) {
        if (!openInputStream(aFileName)) return defValue;
        while (readLine() ) {
            if (rLine.equals(valueName)) {
                curValueName=valueName;
                return readInt();
            } }
        closeInputStream();
        return defValue;
    }


    private boolean readBool() { try { rLine = reader.readLine();
    } catch (Exception e) {
        if (Crash.length()< 3) Crash = "readBool " + curValueName;
        //e.printStackTrace();   Toast.makeText(aContext, "failed to read ", Toast.LENGTH_LONG).show();
        return false;
    }
        return Boolean.parseBoolean(rLine);
    }
    private String readString() {
        try { rLine = reader.readLine();
        } catch (Exception e) {
            if (Crash.length()< 3) Crash = "readString " + curValueName;
            //e.printStackTrace();   Toast.makeText(aContext, "failed to read ", Toast.LENGTH_LONG).show();
            return "";
        }
        return rLine ;
    }
    private int readInt() {
        try {
            rLine = reader.readLine();
            return Integer.parseInt(rLine);
        } catch (Exception e) {
            if (Crash.length()< 3) Crash = "readInt " + curValueName;
            //e.printStackTrace();   Toast.makeText(aContext, "failed to read int", Toast.LENGTH_LONG).show();
            return 0;
        }
    }
}
