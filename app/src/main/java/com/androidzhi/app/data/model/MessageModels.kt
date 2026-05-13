package com.androidzhi.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * Hello消息 - 客户端发送给服务器
 */
data class HelloMessage(
    val type: String = "hello",
    val version: Int = 1,
    val features: Features = Features(),
    val transport: String = "websocket",
    val audioParams: AudioParams = AudioParams()
) {
    data class Features(
        val mcp: Boolean = true
    )
    
    data class AudioParams(
        val format: String = "opus",
        val sampleRate: Int = Constants.AudioConfig.INPUT_SAMPLE_RATE,
        val channels: Int = Constants.AudioConfig.CHANNELS,
        val frameDuration: Int = Constants.AudioConfig.FRAME_DURATION
    )
}

/**
 * TTS消息
 */
data class TtsMessage(
    val type: String = "tts",
    val state: String,
    @SerializedName("session_id")
    val sessionId: String? = null
)

/**
 * 监听控制消息
 */
data class ListenMessage(
    val type: String = "listen",
    val mode: String,
    val state: String? = null
)

/**
 * 打断消息
 */
data class AbortMessage(
    val type: String = "abort",
    @SerializedName("session_id")
    val sessionId: String? = null
)