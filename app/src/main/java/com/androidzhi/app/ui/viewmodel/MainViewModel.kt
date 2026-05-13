package com.androidzhi.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidzhi.app.data.model.ConnectionState
import com.androidzhi.app.data.model.DeviceState
import com.androidzhi.app.service.VoiceService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 主屏幕 ViewModel
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val voiceService: VoiceService
) : ViewModel() {
    
    val deviceState: StateFlow<DeviceState> = voiceService.deviceState
    val connectionState: StateFlow<ConnectionState> = voiceService.connectionState
    
    init {
        viewModelScope.launch {
            voiceService.connect()
        }
    }
    
    fun startListening() {
        voiceService.startManualListening()
    }
    
    fun stopListening() {
        voiceService.stopManualListening()
    }
    
    override fun onCleared() {
        super.onCleared()
        voiceService.disconnect()
    }
}