package com.como.laps.byu;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Antonello on 11/07/15.
 */
public class ProductAdapter extends ArrayAdapter<Product> {

    public ProductAdapter(Context context, int resource, List<Product> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return super.getView(position, convertView, parent);
    }
}
