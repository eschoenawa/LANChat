package de.eschoenawa.lanchat.launcher;

import de.eschoenawa.lanchat.config.Config;
import de.eschoenawa.lanchat.controller.LanChatController;
import de.eschoenawa.lanchat.helper.SL;
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
        Config config = SL.getConfig();
        Log.d(TAG, "Configuration loaded, launching LANChat.");
        //TODO pass config report to error handler to include configuration in logs
        LanChatController controller = SL.getLanChatController(config);
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
}
