package de.eschoenawa.lanchat.launcher;

import de.eschoenawa.lanchat.config.Config;
import de.eschoenawa.lanchat.controller.LanChatController;
import de.eschoenawa.lanchat.definition.LanChatSettingsDefinition;
import de.eschoenawa.lanchat.helper.ServiceLocator;
import de.eschoenawa.lanchat.util.ErrorHandler;
import de.eschoenawa.lanchat.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Launcher {
    public static final String JAR_LOCATION = "./LANChat.jar";
    public static final String NEW_JAR_LOCATION = "./LANChat_new.jar";
    public static final String UPDATE_URL_PROVIDER_FILE = "./UPDATE";
    public static final String NO_UPDATE_FILE_LOCATION = "./NOUPDATE";
    public static final String COPY_STEP_FILE_LOCATION = "./UPDATECOPY";
    public static final String DELETE_STEP_FILE_LOCATION = "./UPDATEDELETE";
    private static final String TAG = "Launcher";

    public static void main(String[] args) {
        try {
            Launcher launcher = new Launcher();
            if (args != null && args.length > 0) {
                launcher.processArgs(args);
            }
            launcher.launch();
        } catch (Exception e) {
            ErrorHandler.fatalCrash(e);
        }
    }

    public void launch() {
        Log.i(TAG, "--[ Launching LANChat ]--");
        String updateUrl = runUpdateRoutine();
        Log.d(TAG, "Loading configuration...");
        Config config = ServiceLocator.getConfig();
        Log.d(TAG, "Configuration loaded, launching LANChat.");
        setLogLevel(config);
        LanChatController controller = new LanChatController(config, updateUrl);
        controller.launch();
    }

    private void processArgs(String[] args) {
        for (String arg : args) {
            if ("noExToFile".equals(arg)) {
                ErrorHandler.allowWriteToFile = false;
                Log.w("ArgProcessor", "Disabled exception logging to file!");
            }
        }
    }

    private String runUpdateRoutine() {
        File noUpdateFile = new File(NO_UPDATE_FILE_LOCATION);
        if (noUpdateFile.exists()) {
            // no updating when in dev environment (or another environment that prohibits update actions)
            Log.d(TAG, "NOUPDATE file detected, update flow is being skipped.");
            return null;
        }
        Log.d(TAG, "Running update flow...");
        File updateCopyStepFile = new File(COPY_STEP_FILE_LOCATION);
        File updateDeleteStepFile = new File(DELETE_STEP_FILE_LOCATION);
        if (updateCopyStepFile.exists()) {
            Log.d(TAG, "Update step 2: Replace LANChat.jar with LANChat_new.jar.");
            waitForPreviousJvmToShutDown();
            File defaultLanChatJar = new File(JAR_LOCATION);
            File newLanChatJar = new File(NEW_JAR_LOCATION);
            try {
                Log.d(TAG, "Copying...");
                Files.copy(newLanChatJar.toPath(), defaultLanChatJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Log.d(TAG, "Done.");
            } catch (IOException e) {
                Log.e(TAG, "CRITICAL ERROR! FAILED TO COPY NEW LANCHAT JAR TO DEFAULT LOCATION. MANUAL REPAIR REQUIRED!");
                ErrorHandler.fatalCrash(e);
            }
            Log.d(TAG, "Deleting UPDATECOPY file...");
            if (!updateCopyStepFile.delete()) {
                Log.e(TAG, "CRITICAL ERROR! FAILED TO REMOVE UPDATECOPY FILE. MANUAL REPAIR REQUIRED!");
                ErrorHandler.fatalCrash(new Exception("Critical Error!"));
            }
            Log.d(TAG, "Done.");
            try {
                if (!updateDeleteStepFile.createNewFile()) {
                    Log.e(TAG, "CRITICAL ERROR! FAILED TO CREATE UPDATEDELETE FILE. MANUAL REPAIR REQUIRED!");
                    ErrorHandler.fatalCrash(new Exception("Critical Error!"));
                }
            } catch (IOException e) {
                Log.e(TAG, "CRITICAL ERROR! FAILED TO CREATE UPDATEDELETE FILE. MANUAL REPAIR REQUIRED!");
                ErrorHandler.fatalCrash(e);
            }
            Log.d(TAG, "Step 2 successful. Exiting and re-launching with LANChat.jar");
            try {
                Runtime.getRuntime().exec("java -jar " + JAR_LOCATION);
            } catch (IOException e) {
                Log.e(TAG, "CRITICAL ERROR! FAILED TO LAUNCH LANChat.jar. MANUAL REPAIR REQUIRED!");
                ErrorHandler.fatalCrash(e);
            }
            System.exit(0);
        } else if (updateDeleteStepFile.exists()) {
            Log.d(TAG, "Update step 3: Delete LANChat_new.jar.");
            waitForPreviousJvmToShutDown();
            Log.d(TAG, "Deleting LANChat_new.jar...");
            File tempJar = new File(NEW_JAR_LOCATION);
            if (!tempJar.delete()) {
                Log.e(TAG, "Failed to remove temporary .jar! Do you have write access?");
            } else {
                Log.d(TAG, "Done.");
            }
            Log.d(TAG, "Deleting UPDATEDELETE file...");
            if (!updateDeleteStepFile.delete()) {
                Log.e(TAG, "Failed to remove UPDATEDELETE file! Do you have write access?");
            } else {
                Log.d(TAG, "Done.");
            }
            Log.d(TAG, "Update successful. Proceeding with normal launch.");
        } else {
            Log.d(TAG, "No update steps queued, proceeding with normal launch.");
        }
        File updateFile = new File(UPDATE_URL_PROVIDER_FILE);
        if (updateFile.exists()) {
            Log.d(TAG, "This instance is an update provider. Reading URL from file...");
            try {
                byte[] encoded = Files.readAllBytes(updateFile.toPath());
                return new String(encoded, StandardCharsets.UTF_8);
            } catch (IOException e) {
                ErrorHandler.reportError(e, true, "Unable to read URL from update file. Disabling update provider feature.");
            }
        }
        return null;
    }

    private void waitForPreviousJvmToShutDown() {
        Log.d(TAG, "Waiting to allow previous JVM to shut down...");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Wait aborted!");
        }
        Log.d(TAG, "Continuing.");
    }

    private void setLogLevel(Config config) {
        String logLevelString = config.getString(LanChatSettingsDefinition.SettingKeys.LOG_LEVEL, "t");
        Log.setLogLevel(Log.getLogLevelFromString(logLevelString));
    }
}
