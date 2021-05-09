package de.eschoenawa.lanchat.config.persist

import de.eschoenawa.lanchat.config.Config

interface ConfigPersister {
    fun save(settings: Map<String, Config.Setting>)
    fun load(callback: ConfigPersisterLoadCallback)
    interface ConfigPersisterLoadCallback {
        fun onSettingLoaded(key: String, value: String)
        fun onPersistentSettingsNotYetCreated()
        fun onLoadFailed(reason: String)
        fun onLoadDone()
    }
}