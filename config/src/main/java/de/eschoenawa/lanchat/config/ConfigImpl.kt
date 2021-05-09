package de.eschoenawa.lanchat.config

import com.google.gson.GsonBuilder
import de.eschoenawa.lanchat.config.persist.ConfigPersister
import de.eschoenawa.lanchat.config.persist.ConfigPersister.ConfigPersisterLoadCallback
import de.eschoenawa.lanchat.config.persist.SimpleFileConfigPersister
import de.eschoenawa.lanchat.util.Log

class ConfigImpl : Config {
    companion object {
        private const val TAG = "Config"
    }

    private val persister: ConfigPersister
    private val settingsDefinition: SettingsDefinition
    private val settings: MutableMap<String, Config.Setting> = HashMap()
    private var currentTransaction: MutableMap<String, String?>? = null

    constructor(settingsDefinition: SettingsDefinition) {
        this.settingsDefinition = settingsDefinition
        loadSettingsFromDefinition()
        persister = SimpleFileConfigPersister(getString(settingsDefinition.configPathKey, null)!!)
        loadSettingsFromFile()
        this.settingsDefinition.validateSettings()
    }

    constructor(settingsDefinition: SettingsDefinition, persister: ConfigPersister) {
        this.settingsDefinition = settingsDefinition
        loadSettingsFromDefinition()
        this.persister = persister
        loadSettingsFromFile()
        this.settingsDefinition.validateSettings()
    }

    private fun loadSettingsFromDefinition() {
        for (setting in settingsDefinition.allSettings) {
            settings[setting.key] = setting
        }
    }

    private fun loadSettingsFromFile() {
        persister.load(object : ConfigPersisterLoadCallback {
            private var overwriteRequired = false
            override fun onSettingLoaded(key: String, value: String) {
                val currentSetting = settings[key]
                if (currentSetting == null || !currentSetting.isModifiable) {
                    Log.w(
                        TAG,
                        "Setting '$key' is either not defined or not modifiable and therefore won't be loaded from file."
                    )
                    overwriteRequired = true
                } else {
                    currentSetting.value = value
                    settings[key] = currentSetting
                }
            }

            override fun onPersistentSettingsNotYetCreated() {
                Log.d(TAG, "Config file doesn't exist yet. Creating new one now...")
                saveSettings()
                Log.d(TAG, "Config file created!")
            }

            override fun onLoadFailed(reason: String) {
                Log.e(TAG, "Unable to read from config file ($reason)! Using defaults.")
            }

            override fun onLoadDone() {
                if (overwriteRequired) {
                    Log.w(TAG, "Config file contains invalid or deprecated settings. Re-creating config file...")
                    saveSettings()
                    Log.d(TAG, "Config file re-created.")
                }
            }
        })
    }

    private fun saveSettings() {
        Log.d(TAG, "Saving config file...")
        persister.save(generateModifiableSettingsMap())
        Log.d(TAG, "Config file saved!")
    }

    private fun generateStringSettingsMap(): Map<String, String> {
        val result: MutableMap<String, String> = HashMap()
        for (key in settings.keys) {
            val setting = settings[key]
            if (setting != null && setting.isModifiable) {
                result[key] = setting.value
            }
        }
        return result
    }

    @Synchronized
    override fun beginTransaction() {
        currentTransaction = HashMap()
    }

    @Synchronized
    override fun abortTransaction() {
        currentTransaction = null
    }

    @Synchronized
    override fun commitTransaction() {
        currentTransaction?.let {transaction ->
            for (key in transaction.keys) {
                val setting = settings[key]
                if (setting != null && setting.isModifiable) {
                    val newValue = transaction[key]
                    Log.d(TAG, "Changing setting '$key' to '$newValue'.")
                    setting.value = newValue!!
                    settings[key] = setting
                } else {
                    Log.w(
                        TAG,
                        "Ignoring changes to setting '$key' (value set to '${transaction[key]}')! Not registered: ${setting == null}"
                    )
                }
            }
            currentTransaction = null
            saveSettings()
            settingsDefinition.validateSettings()
        } ?: throw IllegalStateException("No transaction currently active!")
    }

    @get:Synchronized
    override val isTransactionActive: Boolean
        get() = currentTransaction != null

    @Synchronized
    override fun doesTransactionRequireRestart(): Boolean {
        currentTransaction?.let { transaction ->
            for (key in transaction.keys) {
                val setting = settings[key]
                if (setting != null && setting.isModifiable && setting.isRestartRequired) {
                    return true
                }
            }
            return false
        } ?: throw IllegalStateException("No transaction currently active!")
    }

    override fun getModifiableSettings(): List<Config.Setting> {
        val result: MutableList<Config.Setting> = ArrayList()
        for (setting in settings.values) {
            if (setting.isModifiable) {
                result.add(setting)
            }
        }
        return result
    }

    override fun getString(key: String, def: String?): String? {
        var result: String? = null
        currentTransaction?.let { transaction ->
            result = transaction[key]
        } ?: run {
            val found = settings[key]
            result = found?.value ?: def
        }
        return result
    }

    override fun getInt(key: String, def: Int): Int {
        return getString(key, def.toString())!!.toInt()
    }

    override fun getBoolean(key: String, def: Boolean): Boolean {
        return getString(key, def.toString()).toBoolean()
    }

    override fun getDouble(key: String, def: Double): Double {
        return getString(key, def.toString())!!.toDouble()
    }

    override fun getFloat(key: String, def: Float): Float {
        return getString(key, def.toString())!!.toFloat()
    }

    override fun getLong(key: String, def: Long): Long {
        return getString(key, def.toString())!!.toLong()
    }

    override fun getChar(key: String, def: Char): Char {
        return getString(key, def.toString())!![0]
    }

    override fun requireString(key: String): String {
        return getString(key, null)
            ?: throw IllegalStateException("Value for key '$key' is required but not stored in config!")
    }

    override fun requireInt(key: String): Int {
        return requireString(key).toInt()
    }

    override fun requireBoolean(key: String): Boolean {
        return requireString(key).toBoolean()
    }

    override fun requireDouble(key: String): Double {
        return requireString(key).toDouble()
    }

    override fun requireFloat(key: String): Float {
        return requireString(key).toFloat()
    }

    override fun requireLong(key: String): Long {
        return requireString(key).toLong()
    }

    override fun requireChar(key: String): Char {
        return requireString(key)[0]
    }

    @Synchronized
    override fun setString(key: String, value: String?) {
        currentTransaction?.let { transaction ->
            transaction[key] = value
        } ?: throw IllegalStateException("No transaction currently active!")
    }

    @Synchronized
    override fun setInt(key: String, value: Int) {
        setString(key, value.toString())
    }

    @Synchronized
    override fun setBoolean(key: String, value: Boolean) {
        setString(key, value.toString())
    }

    @Synchronized
    override fun setDouble(key: String, value: Double) {
        setString(key, value.toString())
    }

    @Synchronized
    override fun setFloat(key: String, value: Float) {
        setString(key, value.toString())
    }

    @Synchronized
    override fun setLong(key: String, value: Long) {
        setString(key, value.toString())
    }

    @Synchronized
    override fun setChar(key: String, value: Char) {
        setString(key, value.toString())
    }

    override fun toString(): String {
        val gson = GsonBuilder().create()
        return gson.toJson(generateStringSettingsMap())
    }

    private fun generateModifiableSettingsMap(): Map<String, Config.Setting> {
        val result = HashMap<String, Config.Setting>()
        for (key in settings.keys) {
            val setting = settings[key]
            if (setting!!.isModifiable) {
                result[key] = setting
            }
        }
        return result
    }
}