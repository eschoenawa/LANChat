package de.eschoenawa.lanchat.config

import de.eschoenawa.lanchat.config.mock.MockConfigPersister
import de.eschoenawa.lanchat.config.mock.MockSettingsDefinition
import org.junit.Test

class ConfigTest {

    private val settingsList = arrayListOf(
        Config.Setting("name", "Alice", "Username to show", Config.SettingType.RAW, true, false),
        Config.Setting("active", "false", "Are you active?", Config.SettingType.BOOLEAN, true, false),
        Config.Setting("log_path", "./log.log", "", Config.SettingType.RAW, false, false)
    )
    private val alternativeSettingName = Config.Setting("name", "Bob", "Username to show", Config.SettingType.RAW, true, false)
    private val alternativeSettingLogPath = Config.Setting("log_path", "./fakelog.log", "", Config.SettingType.RAW, true, false)

    @Test
    fun shouldInitializeFromDefinition() {
        val testSettingsList = arrayListOf(settingsList[0], settingsList[1], settingsList[2])
        val settingsDefinition = MockSettingsDefinition(testSettingsList)
        val persister = MockConfigPersister()
        val config = ConfigImpl(settingsDefinition, persister)
        assert(config.requireString("name").equals("Alice"))
        assert(!config.requireBoolean("active"))
        assert(config.requireString("log_path").equals("./log.log"))
        assert(settingsDefinition.validated)
        assert(persister.fileCreated)
        assert(persister.settings["name"]!!.value.equals("Alice"))
        assert(persister.settings["active"]!!.value.equals("false"))
        assert(persister.settings["log_path"] == null)
    }

    @Test
    fun shouldPrioritizeFileOverDefinitionForModifiableSettings() {
        val testSettingsList = arrayListOf(settingsList[0], settingsList[1], settingsList[2])
        val settingsDefinition = MockSettingsDefinition(testSettingsList)
        val persistedSettings = hashMapOf(Pair("name", alternativeSettingName))
        val persister = MockConfigPersister(persistedSettings)
        val config = ConfigImpl(settingsDefinition, persister)
        assert(config.requireString("name").equals("Bob"))
    }

    @Test
    fun shouldNotLoadNonModifiableFromFile() {
        val testSettingsList = arrayListOf(settingsList[0], settingsList[1], settingsList[2])
        val settingsDefinition = MockSettingsDefinition(testSettingsList)
        val persistedSettings = hashMapOf(Pair("log_path", alternativeSettingLogPath))
        val persister = MockConfigPersister(persistedSettings)
        val config = ConfigImpl(settingsDefinition, persister)
        assert(config.requireString("log_path").equals("./log.log"))
    }

    @Test
    fun shouldHandleTransactionCommitCorrectly() {
        val testSettingsList = arrayListOf(settingsList[0], settingsList[1], settingsList[2])
        val settingsDefinition = MockSettingsDefinition(testSettingsList)
        val persister = MockConfigPersister()
        val config = ConfigImpl(settingsDefinition, persister)
        config.beginTransaction()
        assert(config.isTransactionActive)
        config.setBoolean("active", true)
        assert(config.requireBoolean("active"))
        assert(persister.settings["active"]!!.value.equals("false"))
        persister.fileCreated = false
        settingsDefinition.validated = false
        config.commitTransaction()
        assert(!config.isTransactionActive)
        assert(config.requireBoolean("active"))
        assert(persister.settings["active"]!!.value.equals("true"))
        assert(persister.fileCreated)
        assert(settingsDefinition.validated)
    }

    @Test
    fun shouldHandleTransactionAbortCorrectly() {
        val testSettingsList = arrayListOf(settingsList[0], settingsList[1], settingsList[2])
        val settingsDefinition = MockSettingsDefinition(testSettingsList)
        val persister = MockConfigPersister()
        val config = ConfigImpl(settingsDefinition, persister)
        config.beginTransaction()
        assert(config.isTransactionActive)
        config.setBoolean("active", true)
        assert(config.requireBoolean("active"))
        assert(persister.settings["active"]!!.value.equals("false"))
        persister.fileCreated = false
        settingsDefinition.validated = false
        config.abortTransaction()
        assert(!config.isTransactionActive)
        assert(!config.requireBoolean("active"))
        assert(persister.settings["active"]!!.value.equals("false"))
        assert(!persister.fileCreated)
        assert(!settingsDefinition.validated)
    }

    @Test
    fun shouldUseDefaultsOnLoadFail() {
        val testSettingsList = arrayListOf(settingsList[0], settingsList[1], settingsList[2])
        val settingsDefinition = MockSettingsDefinition(testSettingsList)
        val persistedSettings = hashMapOf(Pair("name", alternativeSettingName))
        val persister = MockConfigPersister(persistedSettings)
        persister.failLoading = true
        val config = ConfigImpl(settingsDefinition, persister)
        assert(config.requireString("name").equals("Alice"))
        assert(!config.requireBoolean("active"))
        assert(config.requireString("log_path").equals("./log.log"))
        assert(settingsDefinition.validated)
    }
}