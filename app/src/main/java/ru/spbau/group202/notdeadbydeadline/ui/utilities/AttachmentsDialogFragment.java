package ru.spbau.group202.notdeadbydeadline.ui.utilities;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static org.apache.commons.io.FileUtils.getFile;

public class AttachmentsDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private ArrayList<String> filepaths = new ArrayList<>();

    public void setFilepaths(ArrayList<String> filepaths) {
        this.filepaths = filepaths;
    }

    private String parseFilename(String filename) {
        File f = new File(filename);
        return f.getName();
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
                        openFile(filepaths.get(which));
                        Toast.makeText(getActivity(),
                                "Chosen file: " + filepaths.get(which),
                                Toast.LENGTH_SHORT).show();
                    }
                });

        return builder.create();
    }

    private void openFile(String filepath) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        File file = new File(filepath);

        String extension = FilenameUtils.getExtension(filepath);
        switch (extension) {
            case "jpg":
            case "png":
            case "gif":
                intent.setDataAndType(Uri.fromFile(file), "image/*");
                break;
            case "pdf":
                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                break;
            case "txt":
                intent.setDataAndType(Uri.fromFile(file), "text/plain");
                break;
            case "doc": // TODO does this even work
                intent.setDataAndType(Uri.fromFile(file), "application/msword");
                break;
            case "docx": // TODO does this even work
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                break;
            case "html":
                intent.setDataAndType(Uri.fromFile(file), "text/html");
                break;
        }
        startActivity(intent);
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}
