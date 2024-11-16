package com.e.versmix.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.e.versmix.R;

import org.apmem.tools.layouts.FlowLayout;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FileDlg extends Dialog implements View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, no;

    private String m_dir = "";
    private List<String> m_subdirs = null;
    private boolean m_goToUpper = false;
    public String ChoosenDir;
    public String default_file_name = "BackUp.csv";
    public String selected_file_name = default_file_name;

    private static final int FileOpen     = 0;
    private static final int FileSave     = 1;
    // private static final int FolderChoose = 2;
    private final Context m_context;
    private String m_sdcardDirectory;
    private final String m_fileFilter;
    private final int Select_type;
    private FlowLayout fileList;
    private final SimpleFileDialogListener m_SimpleFileDialogListener;
    private final Globus gc = (Globus) Globus.getAppContext();
    private EditText edFile;
    private TextView tvDir;

    public FileDlg(Activity a, String file_select_type, String file_filter,
                   SimpleFileDialogListener SimpleFileDialogListener) {
        super(a);
        // TODO Auto-generated constructor stub tvTitel
        this.c = a;
        m_context = this.getContext();
        if (file_filter != null)
            m_fileFilter = file_filter.toLowerCase(Locale.getDefault()); else
                m_fileFilter="";
        switch (file_select_type) {
            case "FileSave":
                Select_type = FileSave;
                break;
            case "FileOpen..":
                Select_type = FileOpen;
                m_goToUpper = true;
                break;
            case "FileSave..":
                Select_type = FileSave;
                m_goToUpper = true;
                break;
            default:
                Select_type = FileOpen;
                break;
        }
        m_SimpleFileDialogListener = SimpleFileDialogListener;
        m_sdcardDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        try
        {  m_sdcardDirectory = new File(m_sdcardDirectory).getCanonicalPath();     }
        catch (IOException ignored)  {       }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.filedlg);
        yes = findViewById(R.id.btn_yes);
        no = findViewById(R.id.btn_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        fileList = findViewById(R.id.flFileList);
        edFile = findViewById(R.id.edFileName);
        tvDir = findViewById(R.id.tvDirName);
        findViewById(R.id.btn_delete).setOnClickListener(this);

        TextView m_titleView1= findViewById(R.id.tvTitel);
        if (Select_type == FileOpen    ) m_titleView1.setText(gc.getString(R.string.open));
        if (Select_type == FileSave    ) m_titleView1.setText(gc.getString(R.string.save_as));
        //if (Select_type == FolderChoose) m_titleView1.setText("Folder Select:");

        //fileList.removeAllViews();btn_delete
        // ((Button) findViewById(R.id.btn_delete)).setOnClickListener(this);
        //Ne: TextView wi = findViewById(R.id.tvBottom);        wi.setWidth(gc.getPopUpWidth()*2);
        Objects.requireNonNull(getWindow()).setLayout(gc.getPopUpWidth(), RelativeLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                if (m_SimpleFileDialogListener != null){
                    {
                        if (Select_type == FileOpen || Select_type == FileSave)
                        {
                            selected_file_name= edFile.getText() +"";//m_dir + "/" +
                            ChoosenDir = m_dir;
                            m_SimpleFileDialogListener.onChosenDir( selected_file_name);}
                        else
                        {
                            m_SimpleFileDialogListener.onChosenDir(m_dir);
                        }
                    }
                }
                //c.finish();
                break;
            //case R.id.btn_no:    break;
            case R.id.btn_delete: askDelete(); return;
            default:
                break;
        }
        dismiss();
    }

    
// ***********  from SimpleFileDialog  *********************

    private List<String> getDirectories(String dir)
    {
        List<String> dirs = new ArrayList<>();
        try
        {
            File dirFile = new File(dir);

            // if directory is not the base sd card directory add ".." for going up one directory
            if ((m_goToUpper || ! m_dir.equals(m_sdcardDirectory) )
                    && !"/".equals(m_dir)
            ) {
                dirs.add("..");
            }
            Log.d("~~~~","m_dir="+m_dir);
            if (! dirFile.exists() || ! dirFile.isDirectory())
            {
                return dirs;
            }

  /* OK:         File[] files = dirFile.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return (pathname.getName().endsWith(".img"));
                    or ?
                    return (file.getPath().endsWith(".jpg")||file.getPath().endsWith(".jpeg"));
                }
            });
 */

//org
            for (File file : Objects.requireNonNull(dirFile.listFiles()))
            {
                if ( file.isDirectory())
                {
                    // Add "/" to directory names to identify them in the list
                    dirs.add( file.getName() + "/" );
                }
                else if (Select_type == FileSave || Select_type == FileOpen)
                {
                    // Add file names to the list if we are doing a file save or file open operation
                    String fname = file.getName();
                    if ( m_fileFilter.isEmpty() || fname.toLowerCase(Locale.getDefault()).endsWith(m_fileFilter) )
                            dirs.add( fname );

                }
            }
        }
        catch (Exception ignored)	{}

        dirs.sort(Comparator.naturalOrder());
        return dirs;
    }

    public void chooseFile_or_Dir(String dir) {
        File dirFile = new File(dir);
        while (!dirFile.exists() || !dirFile.isDirectory()) {
            dir = dirFile.getParent();
            assert dir != null;
            dirFile = new File(dir);
            //Log.d("~~~~~","dir="+dir);
        }
        // Log.d("~~~~~","dir="+dir);
        //m_sdcardDirectory
        try {
            dir = new File(dir).getCanonicalPath();
        } catch (IOException ioe) {
            return;
        }
        //fileList = dlg.findViewById(R.id.flFileList);
        m_dir = dir;
        m_subdirs = getDirectories(dir);
        create();

        fillFileList();
        show();
    }

    private void onTextViewClick(View view)
    {
        String m_dir_old = m_dir;
        TextView textView = (TextView) view;
        String sel = textView.getText().toString(); // ((AlertDialog) dialog).getListView().getAdapter().getItem(item);
        if (sel.charAt(sel.length()-1) == '/')	sel = sel.substring(0, sel.length()-1);

        // Navigate into the sub-directory
        if (sel.equals(".."))
        {
            m_dir = m_dir.substring(0, m_dir.lastIndexOf("/"));
            if(m_dir.isEmpty()) {
                m_dir = "/";
            }
        }
        else
        {
            m_dir += "/" + sel;
        }
        selected_file_name = default_file_name;

        if ((new File(m_dir).isFile())) // If the selection is a regular file
        {
            m_dir = m_dir_old;
            selected_file_name = sel;
        }

        updateDirectory();
    }
    private boolean deleteFile(String df) {
        File deleFile = new File(df);
        if   (! deleFile.exists() ) return false;
        return deleFile.delete();
    }
    private boolean createSubDir(String newDir)
    {
        File newDirFile = new File(newDir);
        if   (! newDirFile.exists() ) return newDirFile.mkdir();
        else return false;
    }
    private void updateDirectory()
    {
        m_subdirs.clear();
        m_subdirs.addAll( getDirectories(m_dir) );
        // todo m_titleView.setText(m_dir);
        //m_listAdapter.notifyDataSetChanged();
        //#scorch
        if (Select_type == FileSave || Select_type == FileOpen)
        {
           edFile.setText(selected_file_name);
        }
        fillFileList();
    }
    private boolean isDir(String aNme) {
        return (aNme.contains("..") || aNme.indexOf('/')>-1);
    }
    private void fillFileList() {
        if (fileList==null) return;
        fileList.removeAllViews();
        if (m_subdirs==null) return;
        tvDir.setText(m_dir);
        for (String s: m_subdirs) {
            if (isDir(s)) addFile(s);
        }
        for (String s: m_subdirs) {
            if (!isDir(s)) addFile(s);
        }

    }
    private void addFile(String aWord) {
        if (aWord == null) return;
        TextView textView;
        //if (fileList==null) textView = new TextView(m_context); else
        if (!isDir(aWord))
            textView = (TextView) LayoutInflater.from(fileList.getContext())
                .inflate(R.layout.dlgfile, fileList, false); else
            textView = (TextView) LayoutInflater.from(fileList.getContext()).inflate(R.layout.dlgfolder, fileList, false);
        textView.setText(aWord);
        //textView.setTextSize(12);
        textView.setGravity(Gravity.CENTER);
        // textView.setTag(idx);
        //textView.setBackgroundResource(R.drawable.rounded_corner);
        textView.setOnClickListener(this::onTextViewClick);
        fileList.addView(textView);
    }
    private void askDelete() {
        final EditText input = new EditText(m_context);
        input.setText(selected_file_name);
        new AlertDialog.Builder(m_context).
                setTitle("File to delete:").
                setView(input).setPositiveButton("OK", (dialog, whichButton) -> {
                    Editable newDir = input.getText();
                    String newDirName = newDir.toString();
                    // Create new directory
                    if ( deleteFile(m_dir + "/" + newDirName) )
                    {
                        updateDirectory();
                    }
                }).setNegativeButton("Cancel", null).show();
    }


    public interface SimpleFileDialogListener
    {
        void onChosenDir(String chosenDir);
    }
    
} //end class
