package de.eschoenawa.lanchat.config.mock

import de.eschoenawa.lanchat.config.Config
import de.eschoenawa.lanchat.config.SettingsDefinition

class MockSettingsDefinition(private val settings: MutableList<Config.Setting>) : SettingsDefinition {
    var validated = false
    override val allSettings: List<Config.Setting>
        get() = settings

    override val configPathKey: String
        get() = "NO_PATH"

    override fun validateSettings() {
        validated = true
    }
}