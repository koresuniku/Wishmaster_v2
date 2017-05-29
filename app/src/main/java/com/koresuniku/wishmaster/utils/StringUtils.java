package com.koresuniku.wishmaster.utils;

import android.content.Context;

import com.koresuniku.wishmaster.R;

public class StringUtils {
    public static String getCorrectPostsAndFilesString(String posts, String files) {
        String result = "Пропущено ";

        Integer postsLastNumeral =
                Integer.parseInt(posts.substring(posts.length() - 1, posts.length()));
        Integer filesLastNUmeral =
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

        switch (filesLastNUmeral) {
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
}
