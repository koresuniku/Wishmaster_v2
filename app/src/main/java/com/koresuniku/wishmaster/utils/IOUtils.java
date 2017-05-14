package com.koresuniku.wishmaster.utils;

import android.graphics.drawable.Drawable;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class IOUtils {
    public static long getDirSize(File dir, long initialSize) {
        if (dir == null || !dir.exists()) {
            return 0;
        }

        long size = initialSize;
        File[] files = dir.listFiles();
        if (files == null) {
            return 0;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                size += getDirSize(file, size);
            }
            if (file.isFile()) {
                size += file.length();
            }
        }

        return size;
    }

    public static double convertBytesToMb(long bytes) {
        return bytes / 1024d / 1024d;
    }

    public static long convertMbToBytes(double mb) {
        return (long) (mb * 1024 * 1024);
    }
}
