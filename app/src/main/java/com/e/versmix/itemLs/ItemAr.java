package com.e.versmix.itemLs;

import static com.e.versmix.MainActivity.LOG_TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.e.versmix.MainActivity;
import com.e.versmix.R;
import com.e.versmix.activ.AyLetters;
import com.e.versmix.utils.Globus;
//import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.Random;


public class ItemAr extends LvAdapt<celli> {

    public int movecnt, usermoves;
    private final Context mContext;
    private final GridView mGridView;
    private int selidx=-1, letterCount=-1, idxRand;
    private final Random random = new Random();
    private final AyLetters mActivity;
    private String MischLetters = "AH";
    private final Globus gc = (Globus) Globus.getAppContext();

    public ItemAr(Context context, GridView gridView, AyLetters activity)
    {
        super(context, android.R.layout.simple_list_item_2);
        //mLetters = letters;
        this.mContext = context;
        mGridView = gridView;
        mActivity = activity;
    }

    public void clearme() {
        celli item=getItem(0);
        while (item!=null) {
            remove(item);
            item=getItem(0);
        }
    }
    public void loadLetters(char[] mLetters) {
       // ListAdapter ma = mGridView.getAdapter();
        //mGridView.setAdapter(null);

        celli item;  char c;
        int wordCount=0;
        for (int i = 0; i < mLetters.length; i++) {
            item = new celli();
            c=mLetters[i];
            item.CharOk=c;           item.CharVisi=c;
            item.idxOk=i;           item.idxVisi=i;
            item.position=i;
            item.mischMoved=false;
            if (c==' ') wordCount++; else {
                item.wordID = wordCount;
                letterCount++;
            }
            add(item);
        }
       // mGridView.setAdapter(ma);
    }

    public void mischen() {
        if (letterCount < 10) return;
        idxRand=0; int chrashCnt=0,  cix=letterCount/8, loopcnt=999;
        int moves=cix+random.nextInt(1+(letterCount/22));
        movecnt=0;  usermoves=0;
        //if (moves < 10) moves=11;
        MischLetters="";
        celli vonCell, nachCell;
        try {
            while (moves > 0 && loopcnt>0) { chrashCnt++;
                if (loopcnt < 222) cix=-3; else cix=-1;
                cix = getRandomCellIdx(139, cix);
                vonCell = getItem(cix);//getRandomCell(9, -1);
                assert vonCell != null;
                cix = getRandomCellIdx(146, vonCell.position);
                //Log.d(LOG_TAG, "got .. Random idx: " + cix  );
                nachCell = getItem(cix);//getRandomCell(19, vonCell.position);
                chrashCnt++;
                //Log.d(LOG_TAG, " vonCell null: " + (vonCell==null) + " nachCell null: " + (nachCell==null) );
                if (nachCell != null) {// && vonCell.wordID!=nachCell.wordID
                    if (vonCell.CharOk!=nachCell.CharOk ) {
                        doSwitch(vonCell, nachCell);
                        if (vonCell.wordID==nachCell.wordID)
                            nachCell.mischMoved=false;
                        movecnt++;
                        moves--;
                        }

                    //gc.Logl("moved: " + vonCell.position + " nach: " + nachCell.position, true);
                    //MischLetters = MischLetters + nachCell.CharOk + vonCell.CharOk;
                   // Log.d(LOG_TAG, "moved: " + vonCell.position + " nach: " + nachCell.position);
                }
                loopcnt--;
            }
            //Toast.makeText(getContext(), "moved: "+movecnt, Toast.LENGTH_LONG).show();
            //Log.d(LOG_TAG, "moved: " + movecnt);
        }
        catch (Exception e) {
                //e.printStackTrace();
            gc.Logl("Crash: "+e.getMessage()  + chrashCnt , true);
               // Toast.makeText(getContext(), "Crash: "+e.getMessage()  + chrashCnt, Toast.LENGTH_LONG).show();
        }
    }

    private int getRandomCellIdx(int tries, int ignore) {
        int max=getCount(), Idx, retidx=-1;
       // Log.d(LOG_TAG, "Item Count: " + max); MischLetters
        if (max < 11) return -1;
        if (ignore==-3 && max>22) max=5;
        celli cel;
        while (tries > 0 && retidx == -1) {
            Idx= random.nextInt( max-1);
            //Log.d(LOG_TAG, "Random idx: " + Idx  );
            if (ignore>-1 && ignore==Idx) break;
            cel = getItem(Idx);
            //Log.d(LOG_TAG, "idx: " + Idx + "  null: "+ (cel==null) );
            if (canMove(cel)) retidx = Idx;
            tries --;
        }
        if (retidx == -1)
            for (int i = idxRand; i < max-1; i++) {
                cel = getItem(i);
                if (canMove(cel)) {
                    idxRand = i;
                    retidx = i; break;
                }
            }
       // Log.d(LOG_TAG, "idxRand: " + idxRand);
        cel = getItem(retidx);
        if (retidx !=-1 && cel!=null) MischLetters = MischLetters + cel.CharOk;
        return retidx;
    }
    private boolean canMove(celli item) {
        if (item==null || item.CharOk==' ' || item.mischMoved) return false;
        if (MischLetters.length() < 4) return true;
        int lCnt=0;
        for (int i = 0; i < MischLetters.length(); i++) {
            char c = MischLetters.charAt(i);
            if (c==item.CharOk) lCnt++;
        }
        return lCnt < 4;
    }
    @SuppressLint("ViewHolder")
    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent)
    {
        celli citem = getItem(position);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        if (citem==null)
            citem = new celli();

        // if (convertView == null) { not for GridView !!
            //assert inflater != null;
        //if (inflater==null) return null;
        gridView = inflater.inflate(R.layout.cell, parent, false); // pull views set value into textview
        citem.letterView = gridView.findViewById(R.id.grid_itemletter); // set values into views
        citem.letterView.setTag(citem.position);
        citem.cellView = mGridView.getChildAt(citem.position);
            //citem.tv2 = gridView.findViewById(R.id.grid_itemlabel);

            gridView.setTag(position);


            //citem.position = position;
            //citem.idxVisi = position; ist immer 0

            if (citem.CharOk==' ') citem.letterView.setVisibility(View.INVISIBLE); else
                citem.letterView.setOnClickListener(view1 -> {
                TextView tv = (TextView) view1;
                int idx = (Integer) tv.getTag(); //tv.setText("?");wordCount
                celli item = getItem(idx);
                if (item == null) return;
                if (item.position==item.idxVisi) return;
                if (item.selected) selidx=-1;
                item.selected=!item.selected;
                // Log.d(LOG_TAG, "onClick.. selected: " + item.selected + " CharOk: " + item.CharOk);

                doItemState(item);
                if ( allDone() ) this.mActivity.LettersDone();
            });


        //String ah = position + "/"+citem.position + " C: " + citem.CharOk;
       // citem.tv2.setText(ah);

        setVisiText(citem);  setBackground(citem);

        return gridView;
    }

    @Override
    public celli getItem(int position) {
        if (position < 0 || position >= getCount()) return null;
        return super.getItem(position);
    }

    private boolean allDone() {
        for (int i = 0; i < getCount(); i++) {
            celli item = getItem(i);
            if (item.idxVisi!=item.position) return false;
        }
        return true;
    }
    private void doItemState(@NotNull celli item) {
        if (item.selected) {

            if (selidx>-1) {
                celli selItem = getItem(selidx);
                if (selItem==null) return;
                selItem.selected = false;           item.selected = false;
                Log.d(LOG_TAG, "doItemState -- doSwitch " );
                doSwitch(item, selItem);
                setVisiText(item);              setVisiText(selItem);
                setBackground(item);            setBackground(selItem);
                usermoves ++;
                mActivity.updateClickLabel();
                Log.d(LOG_TAG, "doItemState -- setBackground x2" );
                selidx=-1;
            } else {
                selidx = item.position;
                Log.d(LOG_TAG, "doItemState -- setBackground " );
                setBackground(item);
            }
        } else
            setBackground(item);
    }
    private void setVisiText(@NotNull celli item) {
        if (item.letterView == null) return;
        if (item.idxVisi==item.position)
            item.letterView.setText(String.valueOf(item.CharOk));
        else {
            celli v = getItem(item.idxVisi);
            if (v != null)
                item.letterView.setText(String.valueOf(v.CharOk));
        }
    }
    private void setBackground(@NotNull celli item) {
        if (item.letterView == null) return;
        if (item.idxVisi==item.position)
            item.letterView.setBackgroundResource(R.drawable.richtigplaz);
        else {
            celli visi = getItem(item.idxVisi);
            if (visi != null) {
                if (item.selected) {
                    if (item.wordID==visi.wordID)
                        item.letterView.setBackgroundResource(R.drawable.richtigsel); else
                            item.letterView.setBackgroundResource(R.drawable.selected);
                } else {
                    if (item.wordID==visi.wordID)
                        item.letterView.setBackgroundResource(R.drawable.richtig); else
                            item.letterView.setBackgroundResource(R.drawable.rounded_corner);
                }

            }
        }
    }
    private void doSwitch(@NotNull celli von, @NotNull celli nach) {
        int m=nach.idxVisi;
        nach.idxVisi = von.idxVisi;        von.idxVisi = m;
        nach.mischMoved = true;             von.mischMoved = true;
    }
}

