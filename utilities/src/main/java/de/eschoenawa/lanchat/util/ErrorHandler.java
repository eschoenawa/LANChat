package de.eschoenawa.lanchat.util;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Calendar;


public class ErrorHandler {

    private static final String TAG = "ErrorHandler";
    private static final String DEFAULT_EXCEPTION_PATH_PREFIX = "./exception_";
    private static final String DEFAULT_CRASH_PATH_PREFIX = "./crash_";
    private static final String DEFAULT_LOG_PATH_PREFIX = "./log_";
    private static final String DEFAULT_PATH_POSTFIX = ".log";

    public static boolean allowWriteToFile = true;
    public static String exceptionPathPrefix = DEFAULT_EXCEPTION_PATH_PREFIX;
    public static String crashPathPrefix = DEFAULT_CRASH_PATH_PREFIX;
    public static String logPathPrefix = DEFAULT_LOG_PATH_PREFIX;
    public static String pathPostfix = DEFAULT_PATH_POSTFIX;

    public static void showErrorDialog(String message) {
        showErrorDialog("Error", message);
    }

    public static void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void reportError(Exception e) {
        reportError(e, true);
    }

    public static void reportError(Exception e, boolean dialog) {
        reportError(e, dialog, "An error has occurred:\n");
    }

    public static void reportError(Exception e, boolean dialog, String errorPrefix) {
        Log.e(TAG, "Error reported! dialog=" + dialog, e);
        if (dialog) {
            Object[] options = {"Yes",
                    "No"};
            int userChoice = JOptionPane.showOptionDialog(null,
                    errorPrefix + e.getClass().getName() + ": " + e.getMessage() + "\n\nShould the stacktrace be written to a logfile?",
                    e.getClass().getName(),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (userChoice == 0) {
                logException(e, exceptionPathPrefix);
            }
        } else {
            logException(e, exceptionPathPrefix);
        }
    }

    public static void fatalCrash(Exception e) {
        Log.e(TAG, "Fatal crash!", e);
        logException(e, crashPathPrefix);
        try {
            JOptionPane.showMessageDialog(null,
                    "A fatal error has occurred:\n" + e.getClass().getName() + ": " + e.getMessage(),
                    e.getClass().getName(),
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.exit(5555);
    }

    public static void createLogfile() {
        logException(null, logPathPrefix);
    }

    private static void logException(Exception e, String prefix) {
        if (allowWriteToFile) {
            File file = new File(prefix + Calendar.getInstance().getTimeInMillis() + pathPostfix);
            try (PrintStream ps = new PrintStream(file)) {
                String[] logs = Log.dumpBuffer();
                for (String entry : logs) {
                    if (entry != null) {
                        ps.println(entry);
                    }
                }
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                System.err.println("Unable to write exception to file!");
            }
        } else {
            if (e != null) {
                System.err.println("Log of '" + e + ": " + e.getMessage() + "' not written to file (disabled)!");
            } else {
                System.err.println("Logfile not written to file (disabled)!");
            }
        }
    }
}
