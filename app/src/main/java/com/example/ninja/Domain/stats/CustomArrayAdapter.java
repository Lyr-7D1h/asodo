package com.example.ninja.Domain.stats;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomArrayAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<String[]> items;

    /**
     * Constructor
     *
     * @param context            The current context.
     * @param resource           The resource ID for a layout file containing a layout to use when
     *                           instantiating views.
     * @param textViewResourceId The id of the TextView within the layout resource to be populated
     */
    public CustomArrayAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        this.context = context;
        this.items = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void add(String[] item) {
        if(item.length != 2) {
            throw new IllegalArgumentException("Item should be of length 2");
        }

        items.add(item);
    }

    public void clear() {
        items.clear();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
        TextView text2 = (TextView) view.findViewById(android.R.id.text2);

        text1.setText(items.get(position)[0]);
        text2.setText(items.get(position)[1]);
        return view;
    }
}
