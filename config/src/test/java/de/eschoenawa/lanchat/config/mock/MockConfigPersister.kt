package de.eschoenawa.lanchat.config.mock

import de.eschoenawa.lanchat.config.Config
import de.eschoenawa.lanchat.config.persist.ConfigPersister

class MockConfigPersister() : ConfigPersister {

    var settings : MutableMap<String, Config.Setting>
    var fileCreated : Boolean
    var failLoading : Boolean

    init {
        this.fileCreated = false
        this.failLoading = false
        this.settings = HashMap()
    }

    constructor(settings: MutableMap<String, Config.Setting>) : this() {
        this.fileCreated = true
        this.settings = settings
    }

    override fun save(settings: Map<String, Config.Setting>) {
        this.settings = settings.toMutableMap()
        this.fileCreated = true
    }

    override fun load(callback: ConfigPersister.ConfigPersisterLoadCallback) {
        if (!fileCreated) {
            callback.onPersistentSettingsNotYetCreated()
            return
        } else if (failLoading) {
            callback.onLoadFailed("Mocking load failure.")
            return
        }
        for (key in settings.keys) {
            callback.onSettingLoaded(key, settings[key]!!.value)
        }
        callback.onLoadDone()
    }
}