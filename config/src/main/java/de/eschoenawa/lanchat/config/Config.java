package de.eschoenawa.lanchat.config;

import java.util.List;

public interface Config {
    void beginTransaction();

    void abortTransaction();

    void commitTransaction();

    boolean isTransactionActive();

    boolean doesTransactionRequireRestart();

    List<Setting> getModifiableSettings();

    String getString(String key, String def);

    int getInt(String key, int def);

    boolean getBoolean(String key, boolean def);

    double getDouble(String key, double def);

    float getFloat(String key, float def);

    long getLong(String key, long def);

    char getChar(String key, char def);

    void setString(String key, String value);

    void setInt(String key, int value);

    void setBoolean(String key, boolean value);

    void setDouble(String key, double value);

    void setFloat(String key, float value);

    void setLong(String key, long value);

    void setChar(String key, char value);

    class Setting {
        private String key;
        private String value;
        private String displayText;
        private SettingType type;
        private boolean modifiable;
        private boolean restartRequired;

        public Setting(String key, String value, String displayText, SettingType type, boolean modifiable, boolean restartRequired) {
            this.key = key;
            this.value = value;
            this.displayText = displayText;
            this.type = type;
            this.modifiable = modifiable;
            this.restartRequired = restartRequired;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getDisplayText() {
            return displayText;
        }

        public SettingType getType() {
            return type;
        }

        public boolean isModifiable() {
            return modifiable;
        }

        public boolean isRestartRequired() {
            return restartRequired;
        }
    }

    enum SettingType {
        RAW,
        BOOLEAN
    }
}
