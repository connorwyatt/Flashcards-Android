package io.connorwyatt.flashcards.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.connorwyatt.flashcards.data.entities.Category;

public class CategoryNameArrayAdapter extends ArrayAdapter<Category> {
    public CategoryNameArrayAdapter(Context context, List<Category> categories) {
        super(context, 0, categories);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Category category = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater
                    .from(getContext())
                    .inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        TextView text = (TextView) convertView.findViewById(android.R.id.text1);
        text.setText(category.getName());

        return convertView;
    }

    @Override public View getDropDownView(int position, View convertView, ViewGroup parent) {
        Category category = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater
                    .from(getContext())
                    .inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TextView text = (TextView) convertView.findViewById(android.R.id.text1);
        text.setText(category.getName());

        return convertView;
    }
}
