package ru.spbau.group202.notdeadbydeadline.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.spbau.group202.notdeadbydeadline.R;

public class ScheduleListViewAdapter extends BaseAdapter {

    private LayoutInflater lInflater;
    private List<List<String>> detailedEntries;

    ScheduleListViewAdapter(Context context,
                            List<List<String>> detailedEntries) {
        this.detailedEntries = detailedEntries;
        lInflater = (LayoutInflater) context
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
            //view = lInflater.inflate(R.layout.custom_schedule_listview_item, parent, false);
            view = lInflater.inflate(R.layout.custom_deadline_listview_entry, parent, false);
        }

        ArrayList<String> detailedEntry = (ArrayList<String>) getItem(position);

        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(detailedEntry.get(1));
        stringBuilder.append("  ");
        int pos = stringBuilder.length();
        stringBuilder.append(detailedEntry.get(0));
        stringBuilder.setSpan(new StyleSpan(Typeface.BOLD),
                pos, stringBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (!detailedEntry.get(2).isEmpty() || !detailedEntry.get(3).isEmpty()) {
            stringBuilder.append(", \n");
            if (!detailedEntry.get(2).isEmpty()) {
                stringBuilder.append(detailedEntry.get(2));
                stringBuilder.append(", ");
            }
            stringBuilder.append(detailedEntry.get(3));
        }


        ((TextView) view.findViewById(android.R.id.text2)).setText(stringBuilder);

        return view;
    }
}
