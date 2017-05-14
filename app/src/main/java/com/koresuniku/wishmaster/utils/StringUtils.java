package com.koresuniku.wishmaster.utils;

public class StringUtils {
    public static String correctPostsAndFilesString(String posts, String files) {
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

    public static String correctNotifyNewPostsString(int count) {
        if (count == 0) return "Новых постов нет";

        int lastNumber;
        int signsCount = -1;
        if (count < 10) {
            lastNumber = count;
        } else {
            signsCount = String.valueOf(count).length();
            lastNumber = Integer.parseInt(String.valueOf(count).substring(signsCount -1 , signsCount));
        }
        if (lastNumber == 1) {
            if (count >= 10 && String.valueOf(count).substring(signsCount -1 , signsCount).equals("11")) {
                return count + " новых постов";
            }
            return count + " новый пост";
        }
        if (lastNumber == 2 || lastNumber == 3 || lastNumber == 4) {
            return count + " новых поста";
        }
        return count + " новых постов";
    }
}
