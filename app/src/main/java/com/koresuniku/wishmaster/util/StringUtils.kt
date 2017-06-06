package com.koresuniku.wishmaster.util

import android.content.Context
import android.text.Html
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.TextView

import com.koresuniku.wishmaster.R
import com.koresuniku.wishmaster.http.threads_api.models.Files

object StringUtils {
    fun getPostsAndFilesString(posts: String, files: String): String {
        var result = "Пропущено "

        val postsLastNumeral = Integer.parseInt(posts.substring(posts.length - 1, posts.length))
        val filesLastNumeral = Integer.parseInt(files.substring(files.length - 1, files.length))

        when (postsLastNumeral) {
            0 -> {
                result += posts + " постов, "
            }
            1 -> {
                result += posts + " пост, "
            }
            2 -> {
                result += posts + " поста, "
            }
            3 -> {
                result += posts + " поста, "
            }
            4 -> {
                result += posts + " поста, "
            }
            5 -> {
                result += posts + " постов, "
            }
            6 -> {
                result += posts + " постов, "
            }
            7 -> {
                result += posts + " постов, "
            }
            8 -> {
                result += posts + " постов, "
            }
            9 -> {
                result += posts + " постов, "
            }
        }

        when (filesLastNumeral) {
            0 -> {
                result += files + " файлов"
            }
            1 -> {
                result += files + " файл"
            }
            2 -> {
                result += files + " файла"
            }
            3 -> {
                result += files + " файла"
            }
            4 -> {
                result += files + " файла"
            }
            5 -> {
                result += files + " файлов"
            }
            6 -> {
                result += files + " файлов"
            }
            7 -> {
                result += files + " файлов"
            }
            8 -> {
                result += files + " файлов"
            }
            9 -> {
                result += files + " файлов"
            }
        }
        return result
    }

    fun getNotifyNewPostsString(count: Int): String {
        if (count == 0) return "Новых постов нет"

        val lastNumber: Int
        var signsCount = -1
        if (count < 10) {
            lastNumber = count
        } else {
            signsCount = count.toString().length
            lastNumber = Integer.parseInt(count.toString().substring(signsCount - 1, signsCount))
        }
        if (count in 10..20) {
            return count.toString() + " новых постов"
        }
        if (lastNumber == 1) {
            if (count >= 10 && count.toString().substring(signsCount - 1, signsCount) == "11") {
                return count.toString() + " новых постов"
            }
            return count.toString() + " новый пост"
        }
        if (lastNumber == 2 || lastNumber == 3 || lastNumber == 4) {
            return count.toString() + " новых поста"
        }
        return count.toString() + " новых постов"
    }

    fun getAnswersString(count: Int): String {
        if (count == 0) return ""

        val lastNumber: Int
        var signsCount = -1
        if (count < 10) {
            lastNumber = count
        } else {
            signsCount = count.toString().length
            lastNumber = Integer.parseInt(count.toString().substring(signsCount - 1, signsCount))
        }
        if (count in 10..20) {
            return count.toString() + " ответов"
        }
        if (lastNumber == 1) {
            if (count >= 10 && count.toString().substring(signsCount - 1, signsCount) == "11") {
                return count.toString() + " ответов"
            }
            return count.toString() + " ответ"
        }
        if (lastNumber == 2 || lastNumber == 3 || lastNumber == 4) {
            return count.toString() + " ответа"
        }
        return count.toString() + " ответов"
    }

    fun getNumberAndTimeString(context: Context, position: Int, number: String,
                               op: String, name: String, trip: String, time: String): SpannableString {
        val builder = SpannableStringBuilder(
                "#" + (position + 1) + " ")
        builder.append(if (op == "0") "" else "OP ")
        builder.setSpan(ForegroundColorSpan(
                context.resources.getColor(R.color.post_number_color)),
                0, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val stringBuilder = StringBuilder()
        stringBuilder.append("№")
        stringBuilder.append(number)
        stringBuilder.append(" ")
        stringBuilder.append(Html.fromHtml(if (name == "") "" else name + " "))
        stringBuilder.append(Html.fromHtml(if (trip == "") "" else trip + ""))
        stringBuilder.append(time.replace("[^\\d^\\s/|:]".toRegex(), "").replace("  ", " "))
        builder.append(stringBuilder.toString())

        return SpannableString(builder)
    }

    fun getNumberAndTimeString(number: String, name: String, trip: String, time: String): SpannableString {
        val builder = SpannableStringBuilder()

        builder.append("№")
        builder.append(number)
        builder.append(" ")
        builder.append(Html.fromHtml(if (name == "") "" else name + " "))
        builder.append(Html.fromHtml(if (trip == "") "" else trip + " "))
        builder.append(time.replace("[^\\d^\\s/|:]".toRegex(), "").replace("  ", " "))

        return SpannableString(builder)
    }

    fun getSummaryString(context: Context, size: String, width: String, height: String): String {
        val builder = StringBuilder()

        builder.append(size)
        builder.append(context.getString(R.string.kilobytes_shortened))
        builder.append(", ")
        builder.append(width)
        builder.append("x")
        builder.append(height)

        return builder.toString()
    }

    fun getShortInfoForToolbarString(
            mediaToolbarTitleTextView: TextView, thumbnailPosition: Int, files: List<Files>): String {
        val builder = StringBuilder()

        builder.append("(")
        builder.append(thumbnailPosition + 1)
        builder.append("/")
        builder.append(files.size)
        builder.append("), ")
        builder.append(files[thumbnailPosition].width)
        builder.append("x")
        builder.append(files[thumbnailPosition].height)
        builder.append(", ")
        builder.append(files[thumbnailPosition].size)
        builder.append(" ")
        builder.append(mediaToolbarTitleTextView.context.getString(R.string.kilobytes_shortened))

        return builder.toString()
    }

    fun getFilenameString(name: String, counter: Int): String {
        for (i in name.length - 1 downTo 0) {
            if (name.substring(i, i + 1) == ".") {
                return name.substring(0, i) + "(" + counter + ")." + name.substring(i + 1, name.length)
            } else
                Log.d("String utils", "char: " + name.substring(i, i + 1))
        }
        return "$name($counter)"
    }
}
