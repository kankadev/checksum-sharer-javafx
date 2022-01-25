package dev.kanka.checksumsharer.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Formats different raw values to human readable values
 */
public class FormatUtil {

    /**
     *
     * @param value Timestamp as Long
     * @return formatted Date
     */
    public static String getTimestamp(Long value) {
        Date date = new Date(value);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    /**
     *
     * @param value FileSize as Number
     * @return formatted file size
     */
    public static String getFileSize(Number value) {
        return FileUtil.humanReadableByteCountBin((Long) value) + " (" + value + " Bytes)";
    }
}
