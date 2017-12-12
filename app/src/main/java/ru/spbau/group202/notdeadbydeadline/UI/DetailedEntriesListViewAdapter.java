package ru.spbau.group202.notdeadbydeadline.UI;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.spbau.group202.notdeadbydeadline.R;

import static android.graphics.Typeface.BOLD;

public class DetailedEntriesListViewAdapter extends BaseAdapter {
    private Context ctx;
    LayoutInflater lInflater;
    ArrayList<ArrayList<String>> detailedEntries;

    DetailedEntriesListViewAdapter(Context context,
                                   ArrayList<ArrayList<String>> detailedEntries) {
        ctx = context;
        this.detailedEntries = detailedEntries;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return detailedEntries.size();
    }

    @Override
    public Object getItem(int position) {
        return detailedEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    @SuppressWarnings("unchecked")
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.custom_listview_item, parent, false);
        }

        ArrayList<String> detailedEntry = (ArrayList<String>) getItem(position);

        String deadlinesField = "\nDeadline: ";
        String descriptionFiled = "\nDescription: ";
        String submitField = "\nSubmit: ";
        String expectedScoreField = "\nExpected Score: ";

        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(descriptionFiled);
        stringBuilder.setSpan(new StyleSpan(BOLD),
                0, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        stringBuilder.append(detailedEntry.get(0));
        stringBuilder.append(deadlinesField);
        stringBuilder.setSpan(new StyleSpan(BOLD),
                stringBuilder.length() - deadlinesField.length(),
                stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        stringBuilder.append(detailedEntry.get(1));
        stringBuilder.append(submitField);
        stringBuilder.setSpan(new StyleSpan(BOLD),
                stringBuilder.length() - submitField.length(),
                stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        stringBuilder.append(detailedEntry.get(2));
        stringBuilder.append(expectedScoreField);
        stringBuilder.setSpan(new StyleSpan(BOLD),
                stringBuilder.length() - expectedScoreField.length(),
                stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        stringBuilder.append(detailedEntry.get(3));


        ((TextView) view.findViewById(R.id.list_item)).setText(stringBuilder);

        return view;
    }


}