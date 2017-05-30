package com.koresuniku.wishmaster.utils;

import android.util.Log;

import com.koresuniku.wishmaster.activities.SingleThreadActivity;
import com.koresuniku.wishmaster.http.single_thread_api.models.Post;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlUtils {
    private static final String LOG_TAG = HtmlUtils.class.getSimpleName();

    public static Map<String, List<String>> getAnswersForPost(SingleThreadActivity activity) {
        long startMillis = System.currentTimeMillis();
        List<String> postAnswers;
        Map<String, List<String>> answers = new HashMap<>();
        for (Post post : activity.mPosts) {
            Document doc = Jsoup.parse(post.getComment());
            Elements links = doc.select("a[href]");
            String answer, rawAnswer;
            Element link;
            for (int i = 0; i < links.size(); i++) {
                link = links.get(i);
                rawAnswer = link.attr("href");
                if (!rawAnswer.contains("http") && !rawAnswer.contains("ftp")) {
                    answer = link.attr("data-num");
                    if (answers.get(answer) == null) {
                        postAnswers = new ArrayList<>();
                        postAnswers.add(post.getNum());
                        answers.put(answer, postAnswers);
                    } else {
                        postAnswers = answers.get(answer);
                        postAnswers.add(post.getNum());
                        answers.put(answer, postAnswers);
                    }
                }
            }
        }
        Log.d(LOG_TAG, "jsoup: " + (System.currentTimeMillis() - startMillis));
        return answers;
    }
}
