package com.androidzhi.app.data.model

/**
 * 应用常量定义
 */
object Constants {
    
    // 音频配置
    object AudioConfig {
        const val INPUT_SAMPLE_RATE = 16000
        const val OUTPUT_SAMPLE_RATE = 24000
        const val CHANNELS = 1
        const val FRAME_DURATION = 20
        const val INPUT_FRAME_SIZE = INPUT_SAMPLE_RATE * FRAME_DURATION / 1000
        const val OUTPUT_FRAME_SIZE = OUTPUT_SAMPLE_RATE * FRAME_DURATION / 1000
    }
    
    // 设备状态
    object DeviceState {
        const val IDLE = "idle"
        const val CONNECTING = "connecting"
        const val LISTENING = "listening"
        const val SPEAKING = "speaking"
    }
    
    // 监听模式
    object ListeningMode {
        const val REALTIME = "realtime"
        const val AUTO_STOP = "auto_stop"
        const val MANUAL = "manual"
    }
    
    // 消息类型
    object MessageType {
        const val HELLO = "hello"
        const val GOODBYE = "goodbye"
        const val TTS = "tts"
        const val STT = "stt"
        const val ERROR = "error"
        const val MCP = "mcp"
    }
    
    // 默认配置
    object Defaults {
        const val WEBSOCKET_URL = "wss://api.tenclass.net/xiaozhi/v1/"
        const val OTA_VERSION_URL = "https://api.tenclass.net/xiaozhi/ota/"
    }
}