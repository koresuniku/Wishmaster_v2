package com.koresuniku.wishmaster.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.koresuniku.wishmaster.R;
import com.koresuniku.wishmaster.activities.ThreadsActivity;
import com.koresuniku.wishmaster.http.threads_api.models.Files;

import java.util.List;

public class StringUtils {
    public static String getCorrectPostsAndFilesString(String posts, String files) {
        String result = "Пропущено ";

        Integer postsLastNumeral =
                Integer.parseInt(posts.substring(posts.length() - 1, posts.length()));
        Integer filesLastNumeral =
                Integer.parseInt(files.substring(files.length() - 1, files.length()));

        switch (postsLastNumeral) {
            case 0: {
                result += posts + " постов, ";
                break;
            }
            case 1: {
                result += posts + " пост, ";
                break;
            }
            case 2: {
                result += posts + " поста, ";
                break;
            }
            case 3: {
                result += posts + " поста, ";
                break;
            }
            case 4: {
                result += posts + " поста, ";
                break;
            }
            case 5: {
                result += posts + " постов, ";
                break;
            }
            case 6: {
                result += posts + " постов, ";
                break;
            }
            case 7: {
                result += posts + " постов, ";
                break;
            }
            case 8: {
                result += posts + " постов, ";
                break;
            }
            case 9: {
                result += posts + " постов, ";
                break;
            }
        }

        switch (filesLastNumeral) {
            case 0: {
                result += files + " файлов";
                break;
            }
            case 1: {
                result += files + " файл";
                break;
            }
            case 2: {
                result += files + " файла";
                break;
            }
            case 3: {
                result += files + " файла";
                break;
            }
            case 4: {
                result += files + " файла";
                break;
            }
            case 5: {
                result += files + " файлов";
                break;
            }
            case 6: {
                result += files + " файлов";
                break;
            }
            case 7: {
                result += files + " файлов";
                break;
            }
            case 8: {
                result += files + " файлов";
                break;
            }
            case 9: {
                result += files + " файлов";
                break;
            }
        }
        return result;
    }

    public static String getCorrectNotifyNewPostsString(int count) {
        if (count == 0) return "Новых постов нет";

        int lastNumber;
        int signsCount = -1;
        if (count < 10) {
            lastNumber = count;
        } else {
            signsCount = String.valueOf(count).length();
            lastNumber = Integer.parseInt(String.valueOf(count).substring(signsCount -1 , signsCount));
        }
        if (count >= 10 && count <= 20) {
            return " новых постов";
        }
        if (lastNumber == 1) {
            if (count >= 10 && String.valueOf(count).substring(signsCount - 1, signsCount).equals("11")) {
                return count + " новых постов";
            }
            return count + " новый пост";
        }
        if (lastNumber == 2 || lastNumber == 3 || lastNumber == 4) {
            return count + " новых поста";
        }
        return count + " новых постов";
    }

    @NonNull
    public static String getNumberAndTimeString(String number, String name, String trip, String time) {
        StringBuilder builder = new StringBuilder();

        builder.append("№");
        builder.append(number);
        builder.append(name.equals("") ? "" : " " + name);
        builder.append(" ");
        builder.append(trip.equals("") ? "" : " " + trip);
        builder.append(" ");
        builder.append(time);

        return builder.toString();
    }

    @NonNull
    public static String getSummaryString(Context context, String size, String width, String height) {
        StringBuilder builder = new StringBuilder();

        builder.append(size);
        builder.append(context.getString(R.string.kilobytes_shortened));
        builder.append(", ");
        builder.append(width);
        builder.append("x");
        builder.append(height);

        return builder.toString();
    }

    public static String getShortInfoForToolbarString(
            TextView mediaToolbarTitleTextView, int thumbnailPosition, List<Files> files) {
        StringBuilder builder = new StringBuilder();

        builder.append("(");
        builder.append(thumbnailPosition + 1);
        builder.append("/");
        builder.append(files.size());
        builder.append("), ");
        builder.append(files.get(thumbnailPosition).getWidth());
        builder.append("x");
        builder.append(files.get(thumbnailPosition).getHeight());
        builder.append(", ");
        builder.append(files.get(thumbnailPosition).getSize());
        builder.append(" ");
        builder.append(mediaToolbarTitleTextView.getContext().getString(R.string.kilobytes_shortened));

        return builder.toString();
    }
}
