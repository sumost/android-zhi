package com.androidzhi.app.data.model

/**
 * 设备状态数据类
 */
sealed class DeviceState(val name: String, val displayName: String) {
    object Idle : DeviceState("idle", "待命")
    object Connecting : DeviceState("connecting", "连接中")
    object Listening : DeviceState("listening", "聆听中")
    object Speaking : DeviceState("speaking", "播放中")
    
    companion object {
        fun fromString(state: String): DeviceState {
            return when (state) {
                "idle" -> Idle
                "connecting" -> Connecting
                "listening" -> Listening
                "speaking" -> Speaking
                else -> Idle
            }
        }
    }
}

/**
 * 监听模式
 */
enum class ListeningMode {
    REALTIME,
    AUTO_STOP,
    MANUAL
}

/**
 * 连接状态
 */
sealed class ConnectionState {
    object Disconnected : ConnectionState()
    object Connecting : ConnectionState()
    data class Connected(val info: String = "") : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}