package com.e.versmix.itemLs;

import static com.e.versmix.MainActivity.LOG_TAG;

import android.content.Context;
//import android.content.res.AssetManager;
import android.content.res.AssetManager;
import android.util.Log;

import com.e.versmix.utils.Globus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.StringTokenizer;

public class CsvList {

    public  ArrayList<CsvData > dataList = new ArrayList<>();
    public String filename;

    private final Context mContext;
    private final Random random = new Random();
    private final Globus gc = (Globus) Globus.getAppContext();
    private boolean brandi;

    public CsvList (Context context) {
        mContext = context;
    }

    public boolean readFromPrivate(String csvFile, char delim) {
       if  (!gc.dateien().openInputStream(csvFile) )
           return false;

       buildList(delim);

       gc.dateien().closeInputStream();
       return dataList.size() > 5;
    }
    public void saveToPrivate(String csvFile, char delim){
        if (!gc.dateien().openOutputStream(csvFile, Context.MODE_PRIVATE)) return;
        String line;//Thema # Vers # Translation # Res1 # Res2 # Text
        for (CsvData item : dataList) {
            line = item.Bereich + delim + item.Vers + delim+item.Translation+delim+
                    item.Res1+delim+item.Res2+delim+item.Text;
            if (!gc.dateien().writeLine(line)) break;
        }
        gc.dateien().closeOutputStream();
    }

    private void buildList( char delim) {
        //BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        //gc.Logl("alive readAndInsert: ", true);
        dataList.clear();
        String line, block;
        int tblnum, crashcnt=0;
        //no gc.dateien().readLine(); //ignore Header for save needed

        try {
        while (gc.dateien().readLine()) {
            line = gc.dateien().rLine;
            block=""; tblnum=1; crashcnt=6;
            if (line != null && line.length()>5) {
                CsvData obj= new CsvData ();   crashcnt=1;
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);  crashcnt=8;
                    if (c==delim) {
                        switch (tblnum) {
                            case 1: obj.Bereich = block; break;
                            case 2: obj.Vers = block;
                               // gc.Logl("Block " + block, true);
                            break;
                            case 3: obj.Translation = block; break;
                            case 4: obj.Res1 = block; break;
                            case 5: obj.Res2 = block; break;
                            case 6: obj.Text = block; break;
                        }
                        tblnum++;   block="";    crashcnt=4;
                    } else block = block + c;
                } //for
                crashcnt = 11;
                if (obj.Text==null) obj.Text = block;
                dataList.add(obj);
            }
        }
        }  catch (  Exception e) {
            gc.Logl("BL nr " + crashcnt + " Msg: " + e.getMessage(), true);

        }
    }


    public void readFromAssets(String csvFile, String delim) {


        AssetManager assetManager = mContext.getAssets();
        InputStream is;
        if (assetManager==null) {
            gc.Logl(" assetManager = Null!!", true);
            return;
        }
        try {
            String[] files = assetManager.list("");
            Log.d(LOG_TAG, "Files: " + Arrays.toString(files));
            //files = assetManager.getLocales();
            // Log.d(LOG_TAG, "Files: " + Arrays.toString(files));
            //AssetFileDescriptor afd = assetManager.open(csvFile);

            is = assetManager.open(csvFile);

        } catch (IOException e) {
            // TODO Auto-generated catch block
           // e.printStackTrace();
            gc.Logl( "openCrash File " + csvFile + "  Err: " + e.getMessage(), true);
            return;
        }
        filename = csvFile;
        buildList2(is, delim);

/*
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        //gc.Logl("alive readAndInsert: ", true);
        String line;
        StringTokenizer st;
        dataList.clear();
        //gc.Logl("alive 22222  readAndInsert: ", true);
        try {
// Bereich # Vers # Translation # Res1 #  Res2 # Text
            while ((line = reader.readLine()) != null) {
                st = new StringTokenizer(line, delim);
                //st.countTokens();
                CsvData obj= new CsvData ();
                obj.Bereich = st.nextToken();
                obj.Vers = st.nextToken();
                obj.Translation = st.nextToken();
                if (st.hasMoreTokens()) obj.Res1 = st.nextToken();
                if (st.hasMoreTokens()) obj.Res2 = st.nextToken(); //2x Res
                if (st.hasMoreTokens()) obj.Text    = st.nextToken(); else
                    gc.Logl("No Text Zeile: "+dataList.size() + " Vers "+obj.Vers, true);
                gc.Logl( "Inhalt Text: " + obj.Text , false);
                dataList.add(obj);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            gc.Logl("readline err Zeile: "+dataList.size()+" err "+e.getMessage(), true);
        }

        gc.Logl("Ok Zeilen: "+dataList.size(), true);
*/
    }

    public void doLearnDataIdx(boolean Minus) {
        //gc.LernData_Idx = LeIdx;
        gc.LernData_Idx = doDataIdx(Minus, gc.LernData_Idx);

        getLernData(gc.LernData_Idx);
    }
    public CsvData getLernData(int idx) {
        gc.LernItem = dataList.get(idx);
        gc.LernData_Idx = idx;
        return gc.LernItem;
    }
    public int doDataIdx(boolean Minus, int aIdx) {
        if (Minus) {
            aIdx--;
            if (aIdx < 1) aIdx = dataList.size() - 1;
        } else {
            aIdx++;
            if (aIdx>dataList.size()-1) aIdx=1;
        }

        return aIdx;
    }
    public String getRandomText() {
        String str = "Es ist aber der Glaube eine feste Zuversicht dessen, was man hofft, und ein Nichtzweifeln an dem, was man nicht sieht.";
        int count = dataList.size(), idx;
        if (count > 2) {
            int mIdx = gc.appVals().valueReadInt("randi_Idx", 0);
            brandi = !brandi;
            if (brandi)     idx = 1 + random.nextInt(count-2); else
                {
                    mIdx++;
                    if (mIdx>count-2) mIdx=0;
                    idx=mIdx;
                    gc.appVals().valueWriteInteger("randi_Idx", mIdx);
                }
            gc.LernItem = dataList.get(idx);
            gc.LernData_Idx = idx;
            str= gc.LernItem.Text;
        }
        return str;
    }

    private void buildList2(InputStream inputStream, String delim) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        //gc.Logl("alive readAndInsert: ", true);
        String line;
        StringTokenizer st;
        dataList.clear();
        //gc.Logl("alive 22222  readAndInsert: ", true);
        try {  reader.readLine(); //first Header...
// Bereich # Vers # Translation # Res1 #  Res2 # Text
            while ((line = reader.readLine()) != null) {
                st = new StringTokenizer(line, delim);
                //st.countTokens();
                CsvData obj= new CsvData ();
                obj.Bereich = st.nextToken();
                obj.Vers = st.nextToken();
                obj.Translation = st.nextToken();
                if (st.hasMoreTokens()) obj.Res1 = st.nextToken();
                if (st.hasMoreTokens()) obj.Res2 = st.nextToken(); //2x Res
                if (st.hasMoreTokens()) obj.Text    = st.nextToken(); else
                    gc.Logl("No Text Zeile: "+dataList.size() + " Vers "+obj.Vers, true);
                gc.Logl( "Inhalt Text: " + obj.Text , false);
                dataList.add(obj);
            }
            reader.close();
            inputStream.close();
        } catch (Exception e) {
            //e.printStackTrace();
            gc.Logl("readline err Zeile: "+dataList.size()+" err "+e.getMessage(), true);
        }


       // gc.Logl("Ok Zeilen: "+dataList.size(), true);
    }
    private boolean isInData(String sw, int Dataidx) {
        CsvData data = dataList.get(Dataidx);
        if (data.Bereich.toLowerCase().contains(sw)) return true;
        if (data.Vers.toLowerCase().contains(sw)) return true;
        return data.Text.toLowerCase().contains(sw);
    }
    public int findText(String aTxt, int startIdx) {
        //sz=gc.csvList().dataList.size(), cnt=sz+2;
        int sz=dataList.size(), cnt=sz+2, idx=startIdx;
        String sw = aTxt.toLowerCase();
        while (cnt > 0) {
            cnt--;
            if (idx>sz-1) idx=0;
            if (isInData(sw, idx)) {
                return idx;
            }
            idx++;
        }
        return -1;
    }
    public int hasBibleVers(String aVers) {
       aVers = aVers.toLowerCase(); int idx=-1;
       for (CsvData item : dataList) { idx++;
           if (item.Vers.toLowerCase().equals(aVers)) return idx;
       }
       return idx;
    }
}
