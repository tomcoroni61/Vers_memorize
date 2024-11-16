package com.e.versmix.itemLs;

import android.content.Context;
import android.widget.ArrayAdapter;

public class LvAdapt<T> extends ArrayAdapter<T> {

    public LvAdapt(Context context, int textViewResourceId) {
        super(context, textViewResourceId);    }

        public void set(int index, T item) {
        remove(item); insert(item, index);
        }
}

