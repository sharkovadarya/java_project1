package ru.spbau.group202.notdeadbydeadline.ui.utilities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class CustomDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private ArrayList<String> filepaths = new ArrayList<>();

    public void setFilepaths(ArrayList<String> filepaths) {
        this.filepaths = filepaths;
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private String parseFilename(String filepath) {
        Uri uri = Uri.parse(filepath);
        // TODO figure out how permissions for this work
        //return getFileName(uri);
        return "Name parsing isn't working for now, sorry!";
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String[] paths = new String[filepaths.size()];
        paths = filepaths.toArray(paths);
        for (int i = 0; i < paths.length; i++) {
            paths[i] = parseFilename(paths[i]);
        }

        builder.setTitle("Choose file to open")
                .setItems(paths, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(),
                                "Chosen file: " + parseFilename(filepaths.get(which)),
                                Toast.LENGTH_SHORT).show();
                    }
                });

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}
