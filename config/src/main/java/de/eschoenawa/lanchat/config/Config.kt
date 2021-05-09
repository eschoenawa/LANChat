package de.eschoenawa.lanchat.config

interface Config {
    fun beginTransaction()
    fun abortTransaction()
    fun commitTransaction()
    val isTransactionActive: Boolean
    fun doesTransactionRequireRestart(): Boolean
    fun getModifiableSettings(): List<Setting>
    fun getString(key: String, def: String?): String?
    fun getInt(key: String, def: Int): Int
    fun getBoolean(key: String, def: Boolean): Boolean
    fun getDouble(key: String, def: Double): Double
    fun getFloat(key: String, def: Float): Float
    fun getLong(key: String, def: Long): Long
    fun getChar(key: String, def: Char): Char
    fun requireString(key: String): String
    fun requireInt(key: String): Int
    fun requireBoolean(key: String): Boolean
    fun requireDouble(key: String): Double
    fun requireFloat(key: String): Float
    fun requireLong(key: String): Long
    fun requireChar(key: String): Char
    fun setString(key: String, value: String?)
    fun setInt(key: String, value: Int)
    fun setBoolean(key: String, value: Boolean)
    fun setDouble(key: String, value: Double)
    fun setFloat(key: String, value: Float)
    fun setLong(key: String, value: Long)
    fun setChar(key: String, value: Char)
    override fun toString(): String
    class Setting(
        val key: String,
        var value: String,
        val displayText: String,
        val type: SettingType,
        val isModifiable: Boolean,
        val isRestartRequired: Boolean
    )

    enum class SettingType {
        RAW, BOOLEAN
    }
}