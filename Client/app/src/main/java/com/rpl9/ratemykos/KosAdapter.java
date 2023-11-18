package com.rpl9.ratemykos;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.rpl9.ratemykos.model.Kos;
import java.util.ArrayList;

public class KosAdapter extends ArrayAdapter<Kos> {
    public KosAdapter(Context context, ArrayList<Kos> kos) {
        super(context, 0, kos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Kos kos = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.itemlist, parent, false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.itemList);
        TextView price = convertView.findViewById(R.id.listPrice);
        name.setText(kos.name);
        price.setText("Rp. ");
        return convertView;
    }
}

