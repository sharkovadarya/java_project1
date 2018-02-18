package ru.spbau.group202.notdeadbydeadline.ui.utilities;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListViewUtility {

    // set listView2 height based on listView1 height
    private static void setListViewHeightBasedOnChildren(ListView listView1,
                                                  ListView listView2) {
        ListAdapter listAdapter = listView1.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView1);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView2.getLayoutParams();
        params.height = totalHeight + (listView2.getDividerHeight() * (listAdapter.getCount() - 1));
        listView2.setLayoutParams(params);
    }

    public static void setTwoListViewsHeight(ListView listView1, ListView listView2) {
        if (listView1.getAdapter().getCount() > listView2.getAdapter().getCount()) {
            setListViewHeightBasedOnChildren(listView1, listView2);
            setListViewHeightBasedOnChildren(listView1, listView1);
        } else {
            setListViewHeightBasedOnChildren(listView2, listView2);
            setListViewHeightBasedOnChildren(listView2, listView1);
        }
    }
}
