package de.eschoenawa.lanchat.config.mock

import de.eschoenawa.lanchat.config.Config
import de.eschoenawa.lanchat.config.SettingsDefinition

class MockSettingsDefinition(private val settings: MutableList<Config.Setting>): SettingsDefinition {
    var validated = false
    override fun getAllSettings(): MutableList<Config.Setting> {
        return settings
    }

    override fun getConfigPathKey(): String {
        return "NO_PATH"
    }

    override fun validateSettings() {
        validated = true
    }
}