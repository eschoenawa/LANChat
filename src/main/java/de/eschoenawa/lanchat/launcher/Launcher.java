package de.eschoenawa.lanchat.launcher;

import de.eschoenawa.lanchat.config.Config;
import de.eschoenawa.lanchat.controller.LanChatController;
import de.eschoenawa.lanchat.definition.LanChatSettingsDefinition;
import de.eschoenawa.lanchat.helper.ServiceLocator;
import de.eschoenawa.lanchat.util.ErrorHandler;
import de.eschoenawa.lanchat.util.Log;

public class Launcher {
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
        Log.d(TAG, "Loading configuration...");
        Config config = ServiceLocator.getConfig();
        Log.d(TAG, "Configuration loaded, launching LANChat.");
        setLogLevel(config);
        //TODO pass config report to error handler to include configuration in logs
        LanChatController controller = new LanChatController(config);
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

    private void setLogLevel(Config config) {
        String logLevelString = config.getString(LanChatSettingsDefinition.SettingKeys.LOG_LEVEL, "t");
        Log.setLogLevel(Log.getLogLevelFromString(logLevelString));
    }
}
