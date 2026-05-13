package com.androidzhi.app.protocol

import com.androidzhi.app.data.model.ConnectionState
import kotlinx.coroutines.flow.StateFlow

/**
 * 通信协议抽象接口
 */
interface Protocol {
    
    val connectionState: StateFlow<ConnectionState>
    val isAudioChannelOpened: Boolean
    val sessionId: String?
    
    suspend fun connect(): Boolean
    suspend fun disconnect()
    suspend fun openAudioChannel(): Boolean
    suspend fun closeAudioChannel()
    suspend fun sendText(message: String): Boolean
    suspend fun sendAudio(data: ByteArray): Boolean
    suspend fun sendStartListening(mode: String): Boolean
    suspend fun sendStopListening(): Boolean
    suspend fun sendAbortSpeaking(reason: String? = null): Boolean
    
    fun enableAutoReconnect(enabled: Boolean, maxAttempts: Int = 5)
    fun onConnectionStateChanged(callback: (Boolean, String) -> Unit)
    fun onNetworkError(callback: (String) -> Unit)
    fun onAudioChannelOpened(callback: suspend () -> Unit)
    fun onAudioChannelClosed(callback: suspend () -> Unit)
    fun onIncomingJson(callback: (Map<String, Any>) -> Unit)
    fun onIncomingAudio(callback: (ByteArray) -> Unit)
}