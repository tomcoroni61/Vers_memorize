package com.e.versmix.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.e.versmix.BuildConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Dateien {

    public String rLine, Crash=" ";

    public Dateien() {
        aContext = gc.getApplicationContext();
    }
    public boolean assetFileToPrivate(String filename) {

        if (!openAssetInputStream(filename)) return false;
        if (!openOutputStream(filename, Context.MODE_PRIVATE) ) return false;

        while (readLine()) {
            if (!writeLine(rLine) ) break;
        }
        closeOutputStream();
        closeInputStream();
        return true;
    }
    public void copyFileToPrivate(Uri uri, String saveName ) {
        /*File yourFile = ...; ContentResolver contentResolver  String filePath = uri.getPath();
FileOutputStream outputStream = (FileOutputStream) contentResolver.openOutputStream(destination);
        uri.getLastPathSegment();
        if (openInputStream(uri.getPath())) {
            if (!openOutputStream(uri.getLastPathSegment(), Context.MODE_PRIVATE)) return;

        }

         */
        try {
            if (saveName==null) saveName="unknown.txt";
            ContentResolver contentResolver = aContext.getContentResolver();
            FileInputStream inputStream = (FileInputStream) contentResolver.openInputStream(uri);//(FileInputStream) new FileInputStream(uri.getPath());
            FileOutputStream outputStream = aContext.openFileOutput(saveName, Context.MODE_PRIVATE);
            if (outputStream != null && inputStream !=null) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.close();
                inputStream.close();
            }
            gc.Logl("copy file done", true);
        } catch (IOException e) {
            gc.Logl("failed: "+e.getMessage(), true);
        }
    }

    public boolean hasPrivateFile(String aFileName) {
        File privateRootDir = aContext.getFilesDir();
        File file = new File(privateRootDir, aFileName);
        return file.exists();
    }
    public boolean readLine() {
        try {
            rLine = reader.readLine();
        } catch (Exception e) {
            //e.printStackTrace();   Toast.makeText(aContext, "failed to read ", Toast.LENGTH_LONG).show();
            if (Crash.length()< 3) Crash = "readLine";
            return false;
        }
        return (rLine != null);
    }


    private final Globus gc = (Globus) Globus.getAppContext();
    private FileOutputStream outputStream=null;
    private FileInputStream inputStream = null;
    public BufferedReader reader;  //mit openInputstream...
    private final Context aContext;


    private  boolean openAssetInputStream(String FileName) {
        try {  //try .. ando will das
            AssetManager assetManager = aContext.getAssets();
            InputStream is = assetManager.open(FileName);
            if (is!=null) {
                InputStreamReader tmp=new InputStreamReader(is, StandardCharsets.UTF_8);
                if (reader!=null) reader.close();
                reader=new BufferedReader(tmp);
            } else return false;
//BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        }
        catch (java.io.FileNotFoundException e) {
            return false;
            // that's OK, we probably haven't created it yet
        }

        catch (Exception e) {
            //e.printStackTrace();
            Toast.makeText(aContext, "failed to open for load ", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    public   boolean openInputStream(String FileName) {
        try {  //try .. ando will das
            if (inputStream!=null) inputStream.close();
            inputStream = aContext.openFileInput(FileName);
            if (inputStream!=null) {
                InputStreamReader tmp=new InputStreamReader(inputStream);
                if (reader!=null) reader.close();
                reader=new BufferedReader(tmp);
            } else return false;

        }
        catch (java.io.FileNotFoundException e) { inputStream=null; return false;
            // that's OK, we probably haven't created it yet
        }

        catch (Exception e) {
            //e.printStackTrace();
            Toast.makeText(aContext, "failed to open for load ", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    public void closeOutputStream() {
        try { if (outputStream!=null) outputStream.close(); outputStream=null; } catch (Exception e) {
        //e.printStackTrace();
        gc.Logl( "failed to close ", false);
    } }
    public boolean openOutputStream(String FileName, int mode) {
        try { if (outputStream!=null) closeOutputStream();
            outputStream = aContext.openFileOutput(FileName, mode);  //outputStream.flush();
            //  gc.iLog("DataDir: "+aContext.getFilesDir());
        } catch (Exception e) {
            //e.printStackTrace();
            Toast.makeText(aContext, "failed to open for write "+ FileName, Toast.LENGTH_LONG).show();
//            gc.iLog("failed to open for write "+ FileName);
            return false;
        }
        return true;
    }
    public void closeInputStream() {
        try { if (inputStream!=null) inputStream.close(); inputStream=null;
            if (reader!=null) {reader.close(); reader=null; }
        }catch (Throwable t) {
            Toast.makeText(aContext, "Exception: " + t, Toast.LENGTH_LONG).show();
        }
    }
    public boolean writeLine(String aLine) {
        try {
            outputStream.write((aLine + "\n").getBytes());
        } catch (Exception e) { //gc.iLog("failed to  write ");
            //e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getDisplayName(Uri uri) {
        String displayName = "?";
        if (uri==null) return displayName;
        String uriString = uri.toString();
        File myFile = new File(uriString);


        if (uriString.startsWith("content://")) {
            Cursor cursor;
            cursor = gc.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int cidx=cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (cidx>-1)
                    displayName = cursor.getString(cidx);
                cursor.close();
            }

        } else if (uriString.startsWith("file://")) {
            displayName = myFile.getName();
        }
        return displayName;
    }
    public void shareFile(String filePath, Context context) {

        File privateRootDir = context.getFilesDir();
        File file = new File(privateRootDir, filePath);

        try {
            Intent share = new Intent(Intent.ACTION_SEND);
            //share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (file.exists()) {
                share.setType("text/plain"); //.. text/*
                //gc.Logl(" getUriForFile " + file.getAbsolutePath(), true);

                //Oks
                Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(context),
                        BuildConfig.APPLICATION_ID + ".provider", file);
                //Uri uri =FileProvider.getUriForFile(this,  BuildConfig.APPLICATION_ID + ".provider", file);


                //Uri uri = FileProvider.getUriForFile(gc.getApplicationContext(),"com.e.versmix.fileprovider",file);
                share.putExtra(Intent.EXTRA_STREAM,uri);
                //gc.Logl(" shareFile startActivity ", true);
                context.startActivity(Intent.createChooser(share,"Share table"));
            } else
                gc.Logl(filePath + " not found", true);
        }  catch (  Exception e) {
            gc.Logl("ShareFile_Crash: " + e.getMessage(), true);

        }

    }


  /*
    public void toDownloadFolder(String url) {
        DownloadManager downloadManager = (DownloadManager) aContext.getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url)) // 5.
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // 6.
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "filename_to_save"); // 7.
        downloadManager.enqueue(request); // 8.
        Toast.makeText(aContext, "Download started", Toast.LENGTH_SHORT).show(); // 9.

    }

    private static class downloadPDFTask extends BaseTask<Long> {

        final Context context;
        final String urlString

        public downloadPDFTask(Context context, String urlString) {
            this.context = context;
            this.urlString = urlString;
        }

        @Override
        public Long doInBackground() {
            DownloadManager downloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(urlString);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle("Java_Programming.pdf");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Java_Programming.pdf");
            return downloadmanager.enqueue(request);
        }

        @Override
        public void onPostExecute(Long result) {
            context.receiveDownloadId(result);
            Toast.makeText(context, "Dowload completed!", Toast.LENGTH_SHORT).show();
        }
    }
*/

}

