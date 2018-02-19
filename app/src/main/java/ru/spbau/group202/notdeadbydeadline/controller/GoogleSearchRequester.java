package ru.spbau.group202.notdeadbydeadline.controller;


import android.os.AsyncTask;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ru.spbau.group202.notdeadbydeadline.model.utilities.AsyncTaskResult;
import ru.spbau.group202.notdeadbydeadline.model.utilities.UrlDownloadingException;

public class GoogleSearchRequester
        extends AsyncTask<String, Void, AsyncTaskResult<List<File>, UrlDownloadingException>> {
    private static final String GOOGLE_SEARCH_URL = "https://www.google.com/search";
    private static final int NUMBER_OF_SEARCH_TRIES = 5;

    @Override
    protected AsyncTaskResult<List<File>, UrlDownloadingException> doInBackground(String... strings) {
        ArrayList<File> results = new ArrayList<>();
        String query = strings[0];
        String appDirectory = strings[1];
        String searchURL = GOOGLE_SEARCH_URL + "?q=" + "filetype:pdf " + "+"
                + query.replace(" ", "+") + "&num="
                + Integer.toString(NUMBER_OF_SEARCH_TRIES);

        try {
            Document html = Jsoup.connect(searchURL).userAgent("Mozilla").timeout(5000).get();
            Elements links = html.select("h3.r > a");
            for (Element link : links) {
                String stringUrl = link.attr("href").replace("/url?q=", "").replaceAll("\\.pdf.*", ".pdf");
                URL url = new URL(stringUrl);
                File searchResultsFolder = new File(appDirectory + File.separator + "search");
                //noinspection ResultOfMethodCallIgnored
                searchResultsFolder.mkdir();
                File foundMaterial = new File(searchResultsFolder.getAbsolutePath(), link.text());
                FileUtils.copyURLToFile(url, foundMaterial);
                results.add(foundMaterial);
            }
        } catch (IOException exception) {
            return new AsyncTaskResult<>(new UrlDownloadingException());
        }

        return new AsyncTaskResult<>(results);
    }
}
