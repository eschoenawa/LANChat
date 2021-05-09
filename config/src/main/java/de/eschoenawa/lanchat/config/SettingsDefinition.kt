package de.eschoenawa.lanchat.config

interface SettingsDefinition {
    val allSettings: List<Config.Setting>
    val configPathKey: String
    fun validateSettings()
}