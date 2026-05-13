package com.androidzhi.app.protocol

import com.androidzhi.app.data.model.*
import com.androidzhi.app.data.local.PreferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WebSocket 协议实现
 */
@Singleton
class WebSocketProtocol @Inject constructor(
    private val preferencesDataStore: PreferencesDataStore
) : Protocol {
    
    private val gson = Gson()
    private var webSocket: WebSocket? = null
    private var client: OkHttpClient? = null
    
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState> = _connectionState
    
    override var isAudioChannelOpened: Boolean = false
        private set
    override var sessionId: String? = null
        private set
    
    private var onConnectionStateChangedCallback: ((Boolean, String) -> Unit)? = null
    private var onNetworkErrorCallback: ((String) -> Unit)? = null
    private var onAudioChannelOpenedCallback: (suspend () -> Unit)? = null
    private var onAudioChannelClosedCallback: (suspend () -> Unit)? = null
    private var onIncomingJsonCallback: ((Map<String, Any>) -> Unit)? = null
    private var onIncomingAudioCallback: ((ByteArray) -> Unit)? = null
    
    private var autoReconnectEnabled = false
    private var maxReconnectAttempts = 5
    private var reconnectAttempts = 0
    private var isClosing = false
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override suspend fun connect(): Boolean {
        if (isClosing) return false
        
        return try {
            _connectionState.value = ConnectionState.Connecting
            
            val websocketUrl = preferencesDataStore.websocketUrl.first()
            val accessToken = preferencesDataStore.accessToken.first() ?: ""
            val deviceId = preferencesDataStore.deviceId.first()
            val clientId = preferencesDataStore.clientId.first()
            
            client = OkHttpClient.Builder()
                .pingInterval(20, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
            
            val request = Request.Builder()
                .url(websocketUrl)
                .header("Authorization", "Bearer $accessToken")
                .header("Protocol-Version", "1")
                .header("Device-Id", deviceId)
                .header("Client-Id", clientId)
                .build()
            
            webSocket = client?.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(ws: WebSocket, response: Response) {
                    val helloMessage = HelloMessage()
                    ws.send(gson.toJson(helloMessage))
                }
                
                override fun onMessage(ws: WebSocket, text: String) {
                    handleTextMessage(text)
                }
                
                override fun onMessage(ws: WebSocket, bytes: ByteString) {
                    onIncomingAudioCallback?.invoke(bytes.toByteArray())
                }
                
                override fun onClosing(ws: WebSocket, code: Int, reason: String) {
                    isAudioChannelOpened = false
                    _connectionState.value = ConnectionState.Disconnected
                }
                
                override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                    isAudioChannelOpened = false
                    _connectionState.value = ConnectionState.Disconnected
                }
                
                override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                    _connectionState.value = ConnectionState.Error(t.message ?: "连接失败")
                    onNetworkErrorCallback?.invoke("连接失败: ${t.message}")
                }
            })
            
            withTimeout(10000) {
                var attempts = 0
                while (_connectionState.value !is ConnectionState.Connected && attempts < 50) {
                    delay(200)
                    attempts++
                }
                _connectionState.value is ConnectionState.Connected
            }
        } catch (e: Exception) {
            _connectionState.value = ConnectionState.Error(e.message ?: "连接异常")
            false
        }
    }
    
    override suspend fun disconnect() {
        isClosing = true
        webSocket?.close(1000, "正常关闭")
        client?.dispatcher?.executorService?.shutdown()
        isAudioChannelOpened = false
        _connectionState.value = ConnectionState.Disconnected
        isClosing = false
    }
    
    override suspend fun openAudioChannel(): Boolean {
        return if (_connectionState.value is ConnectionState.Connected) {
            isAudioChannelOpened = true
            onAudioChannelOpenedCallback?.invoke()
            true
        } else {
            false
        }
    }
    
    override suspend fun closeAudioChannel() {
        isAudioChannelOpened = false
        onAudioChannelClosedCallback?.invoke()
    }
    
    override suspend fun sendText(message: String): Boolean {
        return webSocket?.send(message) ?: false
    }
    
    override suspend fun sendAudio(data: ByteArray): Boolean {
        return if (isAudioChannelOpened) {
            webSocket?.send(data.toByteString()) ?: false
        } else {
            false
        }
    }
    
    override suspend fun sendStartListening(mode: String): Boolean {
        val message = ListenMessage(mode = mode, state = "start")
        return sendText(gson.toJson(message))
    }
    
    override suspend fun sendStopListening(): Boolean {
        val message = ListenMessage(mode = "manual", state = "stop")
        return sendText(gson.toJson(message))
    }
    
    override suspend fun sendAbortSpeaking(reason: String?): Boolean {
        val message = AbortMessage(sessionId = sessionId)
        return sendText(gson.toJson(message))
    }
    
    override fun enableAutoReconnect(enabled: Boolean, maxAttempts: Int) {
        autoReconnectEnabled = enabled
        maxReconnectAttempts = maxAttempts
    }
    
    override fun onConnectionStateChanged(callback: (Boolean, String) -> Unit) {
        onConnectionStateChangedCallback = callback
    }
    
    override fun onNetworkError(callback: (String) -> Unit) {
        onNetworkErrorCallback = callback
    }
    
    override fun onAudioChannelOpened(callback: suspend () -> Unit) {
        onAudioChannelOpenedCallback = callback
    }
    
    override fun onAudioChannelClosed(callback: suspend () -> Unit) {
        onAudioChannelClosedCallback = callback
    }
    
    override fun onIncomingJson(callback: (Map<String, Any>) -> Unit) {
        onIncomingJsonCallback = callback
    }
    
    override fun onIncomingAudio(callback: (ByteArray) -> Unit) {
        onIncomingAudioCallback = callback
    }
    
    private fun handleTextMessage(text: String) {
        try {
            val json = gson.fromJson(text, Map::class.java) as Map<String, Any>
            val type = json["type"] as? String
            
            when (type) {
                "hello" -> {
                    sessionId = json["session_id"] as? String
                    _connectionState.value = ConnectionState.Connected("连接成功")
                    onConnectionStateChangedCallback?.invoke(true, "连接成功")
                }
            }
            
            onIncomingJsonCallback?.invoke(json)
        } catch (e: Exception) {
            // 解析错误
        }
    }
}