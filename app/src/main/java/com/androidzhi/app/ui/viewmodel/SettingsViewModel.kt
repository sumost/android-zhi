package com.androidzhi.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidzhi.app.data.local.PreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 设置 ViewModel
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesDataStore: PreferencesDataStore
) : ViewModel() {
    
    data class Settings(
        val aecEnabled: Boolean = true,
        val wakeWordEnabled: Boolean = false,
        val protocolType: String = "websocket",
        val websocketUrl: String = "",
        val version: String = "1.0.0"
    )
    
    private val _settings = MutableStateFlow(Settings())
    val settings: StateFlow<Settings> = _settings.asStateFlow()
    
    init {
        viewModelScope.launch {
            combine(
                preferencesDataStore.aecEnabled,
                preferencesDataStore.wakeWordEnabled,
                preferencesDataStore.protocolType,
                preferencesDataStore.websocketUrl
            ) { aec, wakeWord, protocol, wsUrl ->
                Settings(
                    aecEnabled = aec,
                    wakeWordEnabled = wakeWord,
                    protocolType = protocol,
                    websocketUrl = wsUrl,
                    version = "1.0.0"
                )
            }.collect { _settings.value = it }
        }
    }
    
    fun setAecEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setAecEnabled(enabled)
        }
    }
    
    fun setWakeWordEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setWakeWordEnabled(enabled)
        }
    }
}