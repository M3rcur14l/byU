package com.como.laps.byu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Antonello on 11/07/15.
 */
public class ProductAdapter extends ArrayAdapter<Product> {

    private LayoutInflater inflater;
    private ViewHolder holder = new ViewHolder();
    private List<Product> products;


    public ProductAdapter(Context context, int resource, List<Product> products) {
        super(context, resource, products);
        this.products = products;
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            v = inflater.inflate(R.layout.rowlayout, null);

            // cache view fields into the holder
            holder = new ViewHolder();
            holder.productName = (TextView) v.findViewById(R.id.product_name);
            holder.productImage = (ImageView) v.findViewById(R.id.product_image);
            holder.productPrice = (TextView) v.findViewById(R.id.product_price);
            holder.derivable = (ImageView) v.findViewById(R.id.deliverable_image);

            // associate the holder with the view for later lookup
            v.setTag(holder);
        }
        else {
            // view already exists, get the holder instance from the view
            holder = (ViewHolder)v.getTag();
        }
        return v;
    }

    static class ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        ImageView derivable;
    }
}


