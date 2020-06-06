package de.eschoenawa.lanchat.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Log {
    private static final String TIMESTAMP_FORMAT_STRING = "yyyy/MM/dd HH:mm:ss:SSS";
    private static final int MAX_TAG_LENGTH = 20;

    private static LogLevel logLevel = LogLevel.TRACE;
    private static String[] buffer = new String[1024];
    private static int head = 0;

    /**
     * Sets the log buffer size and copies the existing buffer to the new one.
     *
     * @param size The new log buffer size, cannot be 0 or smaller.
     */
    public synchronized static void setBufferSize(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Log buffer size cannot be 0!");
        }
        String[] oldBuffer = buffer;
        int oldHead = head;
        buffer = new String[size];
        head = 0;
        if (oldBuffer[oldHead] != null) {
            writeToBuffer(oldBuffer[oldHead]);
        }
        for (int i = (oldHead + 1) % oldBuffer.length; i != oldHead; i = (i + 1) % oldBuffer.length) {
            if (oldBuffer[i] != null) {
                writeToBuffer(oldBuffer[i]);
            }
        }
    }

    /**
     * Returns the current buffer size.
     *
     * @return The current buffer size
     */
    public static int getBufferSize() {
        return buffer.length;
    }

    /**
     * Sets the Loglevel of this Log.
     *
     * @param newLogLevel The new Loglevel
     */
    public static void setLogLevel(LogLevel newLogLevel) {
        logLevel = newLogLevel;
    }

    /**
     * Dumps the contents of the buffer into a string array, ordered by the order the logs were performed. Note that
     * the returned string array might have null entries, if there were less logs than the buffer size or the buffer
     * size was changed.
     *
     * @return A string array containing the buffered logs
     */
    public synchronized static String[] dumpBuffer() {
        String[] result = new String[buffer.length];
        int virtualHead = head;
        for (int i = 0; i < result.length; i++) {
            if (buffer[virtualHead] != null) {
                result[i] = buffer[virtualHead];
            }
            virtualHead = (virtualHead + 1) % buffer.length;
        }
        return result;
    }

    private static void writeToBuffer(String s) {
        buffer[head] = s;
        head = (head + 1) % buffer.length;
    }

    //region Actual logging
    private static void logWithLoglevel(LogLevel msgLogLevel, String tag, String message, Throwable throwable) {
        if (msgLogLevel.ordinal() >= logLevel.ordinal()) {
            DateFormat timestampFormat = new SimpleDateFormat(TIMESTAMP_FORMAT_STRING);
            Calendar currentTime = Calendar.getInstance();
            String timestamp = timestampFormat.format(currentTime.getTime());
            if (tag == null || "".equals(tag)) {
                tag = msgLogLevel.toString();
            } else if (tag.length() > MAX_TAG_LENGTH) {
                tag = tag.substring(0, MAX_TAG_LENGTH);
            }
            String logtext = timestamp + "/" + getLogLevelString(msgLogLevel) + " [" + tag + "]: " + message;
            if (throwable != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);
                String stackTraceString = sw.toString();
                logtext = logtext + "\n" + stackTraceString;
                log(logtext, true);
            } else {
                log(logtext, msgLogLevel == LogLevel.ERROR);
            }
        }
    }

    private static void log(String msg, boolean error) {
        if (error) {
            System.err.println(msg);
        } else {
            System.out.println(msg);
        }
        writeToBuffer(msg);
    }

    /**
     * Write a line to the log with the loglevel "TRACE".
     * @param tag The tag to add to the log message
     * @param message The actual log message
     */
    public synchronized static void t(String tag, String message) {
        logWithLoglevel(LogLevel.TRACE, tag, message, null);
    }

    /**
     * Write a line to the log with the loglevel "DEBUG".
     * @param tag The tag to add to the log message
     * @param message The actual log message
     */
    public synchronized static void d(String tag, String message) {
        logWithLoglevel(LogLevel.DEBUG, tag, message, null);
    }

    /**
     * Write a line to the log with the loglevel "INFO".
     * @param tag The tag to add to the log message
     * @param message The actual log message
     */
    public synchronized static void i(String tag, String message) {
        logWithLoglevel(LogLevel.INFO, tag, message, null);
    }

    /**
     * Write a line to the log with the loglevel "WARN".
     * @param tag The tag to add to the log message
     * @param message The actual log message
     */
    public synchronized static void w(String tag, String message) {
        logWithLoglevel(LogLevel.WARN, tag, message, null);
    }

    /**
     * Write a line to the log with the loglevel "ERROR".
     * @param tag The tag to add to the log message
     * @param message The actual log message
     * @param throwable The stacktrace of this throwable will be appended to the log
     */
    public synchronized static void e(String tag, String message, Throwable throwable) {
        logWithLoglevel(LogLevel.ERROR, tag, message, throwable);
    }

    /**
     * Write a line to the log with the loglevel "ERROR".
     * @param tag The tag to add to the log message
     * @param message The actual log message
     */
    public synchronized static void e(String tag, String message) {
        e(tag, message, null);
    }
    //endregion

    private static String getLogLevelString(LogLevel logLevel) {
        switch (logLevel) {
            case TRACE:
                return "t";
            case DEBUG:
                return "d";
            case INFO:
                return "i";
            case WARN:
                return "w";
            case ERROR:
                return "e";
            default:
                return "!";
        }
    }

    public static LogLevel getLogLevelFromString(String logLevel) {
        switch (logLevel) {
            case "t":
            default:
                return LogLevel.TRACE;
            case "d":
                return LogLevel.DEBUG;
            case "i":
                return LogLevel.INFO;
            case "w":
                return LogLevel.WARN;
            case "e":
                return LogLevel.ERROR;
        }
    }

    /**
     * This enum describes all possible loglevels.
     */
    public enum LogLevel {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    }
}
