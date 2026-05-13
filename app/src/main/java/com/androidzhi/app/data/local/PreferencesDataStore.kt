package com.androidzhi.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "android_zhi_prefs")

/**
 * 偏好设置数据存储
 */
@Singleton
class PreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    val deviceId: Flow<String> = dataStore.data
        .map { it[DEVICE_ID] ?: generateDeviceId() }
    
    val clientId: Flow<String> = dataStore.data
        .map { it[CLIENT_ID] ?: generateClientId() }
    
    val accessToken: Flow<String?> = dataStore.data
        .map { it[ACCESS_TOKEN] }
    
    val isActivated: Flow<Boolean> = dataStore.data
        .map { it[IS_ACTIVATED] ?: false }
    
    val websocketUrl: Flow<String> = dataStore.data
        .map { it[WEBSOCKET_URL] ?: "wss://api.tenclass.net/xiaozhi/v1/" }
    
    val aecEnabled: Flow<Boolean> = dataStore.data
        .map { it[AEC_ENABLED] ?: true }
    
    val wakeWordEnabled: Flow<Boolean> = dataStore.data
        .map { it[WAKE_WORD_ENABLED] ?: false }
    
    val protocolType: Flow<String> = dataStore.data
        .map { it[PROTOCOL_TYPE] ?: "websocket" }

    suspend fun setDeviceId(id: String) {
        dataStore.edit { it[DEVICE_ID] = id }
    }
    
    suspend fun setClientId(id: String) {
        dataStore.edit { it[CLIENT_ID] = id }
    }
    
    suspend fun setAccessToken(token: String?) {
        dataStore.edit { 
            if (token != null) {
                it[ACCESS_TOKEN] = token
            } else {
                it.remove(ACCESS_TOKEN)
            }
        }
    }
    
    suspend fun setActivated(activated: Boolean) {
        dataStore.edit { it[IS_ACTIVATED] = activated }
    }
    
    suspend fun setWebsocketUrl(url: String) {
        dataStore.edit { it[WEBSOCKET_URL] = url }
    }
    
    suspend fun setAecEnabled(enabled: Boolean) {
        dataStore.edit { it[AEC_ENABLED] = enabled }
    }
    
    suspend fun setWakeWordEnabled(enabled: Boolean) {
        dataStore.edit { it[WAKE_WORD_ENABLED] = enabled }
    }
    
    suspend fun setProtocolType(type: String) {
        dataStore.edit { it[PROTOCOL_TYPE] = type }
    }

    private fun generateDeviceId(): String {
        return "android_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
    
    private fun generateClientId(): String {
        return "android_client_${System.currentTimeMillis()}"
    }

    companion object {
        private val DEVICE_ID = stringPreferencesKey("device_id")
        private val CLIENT_ID = stringPreferencesKey("client_id")
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val IS_ACTIVATED = booleanPreferencesKey("is_activated")
        private val WEBSOCKET_URL = stringPreferencesKey("websocket_url")
        private val AEC_ENABLED = booleanPreferencesKey("aec_enabled")
        private val WAKE_WORD_ENABLED = stringPreferencesKey("wake_word_enabled")
        private val PROTOCOL_TYPE = stringPreferencesKey("protocol_type")
    }
}