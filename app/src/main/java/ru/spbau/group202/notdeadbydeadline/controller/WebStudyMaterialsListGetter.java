package ru.spbau.group202.notdeadbydeadline.controller;


import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.spbau.group202.notdeadbydeadline.model.utilities.AsyncTaskResult;
import ru.spbau.group202.notdeadbydeadline.model.utilities.StudyMaterialsSourceAccessException;


public class WebStudyMaterialsListGetter
        extends AsyncTask<Void, Void, AsyncTaskResult<List<String>[], StudyMaterialsSourceAccessException>> {
    private static final String STUDY_MATERIAL_SOURCE = "https://cdkrot.me/";

    @Override
    protected AsyncTaskResult<List<String>[], StudyMaterialsSourceAccessException> doInBackground(Void... voids) {
        @SuppressWarnings("unchecked")
        ArrayList<String>[] materialsByTerms = new ArrayList[7];
        for (int term = 0; term < materialsByTerms.length; term++) {
            materialsByTerms[term] = new ArrayList<>();
        }

        try {
            Document html = Jsoup.connect(STUDY_MATERIAL_SOURCE).get();
            Elements materialsUrl = html.select("a[href$=.pdf]");

            Pattern urlPattern = Pattern.compile(".+/(.+/.*?term(\\d).+)/\\d+\\.pdf");
            Pattern noTermPattern = Pattern.compile(STUDY_MATERIAL_SOURCE + "(.+)/\\d+\\.pdf");
            for (Element materialUrl : materialsUrl) {
                String absUrl = materialUrl.attr("abs:href");
                Matcher urlMatcher = urlPattern.matcher(absUrl);
                if (urlMatcher.find()) {
                    materialsByTerms[Integer.valueOf(urlMatcher.group(2))].add(urlMatcher.group(1));
                } else {
                    Matcher noTermMatcher = noTermPattern.matcher(absUrl);
                    if (noTermMatcher.find()) {
                        materialsByTerms[0].add(noTermMatcher.group(1));
                    }

                }
            }
        } catch (IOException exception) {
            return new AsyncTaskResult<>(new StudyMaterialsSourceAccessException());
        }

        return new AsyncTaskResult<>(materialsByTerms);
    }
}
