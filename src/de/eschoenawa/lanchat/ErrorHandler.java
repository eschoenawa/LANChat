package de.eschoenawa.lanchat;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Calendar;

import static de.eschoenawa.lanchat.Config.*;

public class ErrorHandler {
    public static void reportError(Exception e) {
        reportError(e, true);
    }

    public static void reportError(Exception e, boolean dialog) {
        e.printStackTrace();
        if (dialog) {
            Object[] options = {"Yes",
                    "No"};
            int userChoice = JOptionPane.showOptionDialog(null,
                    "An error has occurred:\n" + e.getClass().getName() + ": " + e.getMessage() + "\n\nShould the stacktrace be written to a logfile?",
                    e.getClass().getName(),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (userChoice == 0) {
                logException(e, EXCEPTION_PATH_PREFIX);
            }
        } else {
            logException(e, EXCEPTION_PATH_PREFIX);
        }
    }

    public static void fatalCrash(Exception e) {
        e.printStackTrace();
        logException(e, CRASH_PATH_PREFIX);
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

    private static void logException(Exception e, String prefix) {
        File file = new File(prefix + Calendar.getInstance().getTimeInMillis() + CRASHLOG_PATH_POSTFIX);
        try (PrintStream ps = new PrintStream(file)) {
            e.printStackTrace(ps);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.err.println("Unable to write exception to file!");
        }
    }
}
