package de.eschoenawa.lanchat.config.persist

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import de.eschoenawa.lanchat.config.Config
import de.eschoenawa.lanchat.config.persist.ConfigPersister.ConfigPersisterLoadCallback
import java.io.*

class SimpleFileConfigPersister(private val path: String) : ConfigPersister {
    override fun save(settings: Map<String, Config.Setting>) {
        val gson = GsonBuilder().create()
        try {
            PrintWriter(path).use { writer ->
                val json = gson.toJson(generateStringSettingsMap(settings))
                writer.print(json)
            }
        } catch (e: FileNotFoundException) {
            throw IllegalStateException("Invalid config path: '$path'!", e)
        }
    }

    override fun load(callback: ConfigPersisterLoadCallback) {
        val gson = Gson()
        val f = File(path)
        if (!f.exists()) {
            callback.onPersistentSettingsNotYetCreated()
            return
        }
        try {
            BufferedReader(FileReader(path)).use { br ->
                val json = br.readLine()
                br.close()
                val type = object : TypeToken<Map<String?, String?>?>() {}.type
                val settingsFromFile = gson.fromJson<Map<String, String>>(json, type)
                for (key in settingsFromFile.keys) {
                    callback.onSettingLoaded(key, settingsFromFile[key]!!)
                }
            }
        } catch (e: IOException) {
            callback.onLoadFailed(e.message ?: "null")
        }
        callback.onLoadDone()
    }

    private fun generateStringSettingsMap(settings: Map<String, Config.Setting>): Map<String, String> {
        val result: MutableMap<String, String> = HashMap()
        for (key in settings.keys) {
            val setting = settings[key]
            setting?.let { result[key] = setting.value } ?: throw IllegalStateException("Setting '$key' is null!")
        }
        return result
    }
}