package de.eschoenawa.lanchat.ui.settings;

import de.eschoenawa.lanchat.config.Config;
import de.eschoenawa.lanchat.helper.Texts;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class SettingsTableModel extends AbstractTableModel {
    private final Config config;
    private final List<Config.Setting> modifiableSettings;

    public SettingsTableModel(Config config) {
        this.config = config;
        this.modifiableSettings = config.getModifiableSettings();
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return Texts.Settings.SETTINGS_HEADER;
            case 1:
                return Texts.Settings.VALUES_HEADER;
            default:
                throw new IllegalArgumentException("Invalid Column!");
        }
    }

    @Override
    public int getRowCount() {
        return this.modifiableSettings.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return this.modifiableSettings.get(rowIndex).getDisplayText();
        } else {
            Config.Setting currentSetting = this.modifiableSettings.get(rowIndex);
            switch (currentSetting.getType()) {
                case RAW:
                default:
                    return config.getString(currentSetting.getKey(), currentSetting.getValue());
                case BOOLEAN:
                    return Boolean.parseBoolean(config.getString(currentSetting.getKey(), currentSetting.getValue()));
            }
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1;
    }

    @Override
    public void setValueAt(Object newValue, int rowIndex, int columnIndex) {
        if (columnIndex != 1) {
            throw new IllegalArgumentException("Trying to set value of a non-modifiable field!");
        }
        if (!config.isTransactionActive()) {
            config.beginTransaction();
        }
        Config.Setting currentSetting = this.modifiableSettings.get(rowIndex);
        switch (currentSetting.getType()) {
            case RAW:
            default:
                config.setString(currentSetting.getKey(), (String) newValue);
                break;
            case BOOLEAN:
                if (newValue instanceof Boolean) {
                    Boolean newBooleanValue = (Boolean) newValue;
                    config.setBoolean(currentSetting.getKey(), newBooleanValue);
                } else {
                    throw new IllegalStateException("Setting type is boolean but new value isn't one!");
                }
        }
    }

    public boolean applyChanges() {
        boolean result = false;
        if (config.isTransactionActive()) {
            result = config.doesTransactionRequireRestart();
            config.commitTransaction();
        }
        return result;
    }

    public void cancelChanges() {
        if (config.isTransactionActive()) {
            config.abortTransaction();
        }
    }
}
