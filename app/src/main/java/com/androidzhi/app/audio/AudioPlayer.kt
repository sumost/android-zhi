package com.androidzhi.app.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import com.androidzhi.app.data.model.Constants
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 音频播放器
 */
@Singleton
class AudioPlayer @Inject constructor() {
    
    companion object {
        private const val TAG = "AudioPlayer"
    }
    
    private var audioTrack: AudioTrack? = null
    private var isPlaying = false
    private var playJob: Job? = null
    
    private val audioQueue = ConcurrentLinkedQueue<ByteArray>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    fun startPlaying(): Boolean {
        if (isPlaying) return true
        
        return try {
            val sampleRate = Constants.AudioConfig.OUTPUT_SAMPLE_RATE
            val channelConfig = AudioFormat.CHANNEL_OUT_MONO
            val audioFormat = AudioFormat.ENCODING_PCM_16BIT
            
            val minBufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat)
            
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(sampleRate)
                        .setEncoding(audioFormat)
                        .setChannelMask(channelConfig)
                        .build()
                )
                .setBufferSizeInBytes(minBufferSize * 2)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()
            
            audioTrack?.play()
            isPlaying = true
            
            playJob = scope.launch {
                while (isPlaying && isActive) {
                    val data = audioQueue.poll()
                    if (data != null) {
                        audioTrack?.write(data, 0, data.size)
                    } else {
                        delay(5)
                    }
                }
            }
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "开始播放失败", e)
            stopPlaying()
            false
        }
    }
    
    fun stopPlaying() {
        isPlaying = false
        playJob?.cancel()
        
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            Log.e(TAG, "停止播放失败", e)
        }
        
        audioTrack = null
        audioQueue.clear()
    }
    
    fun playAudio(pcmData: ByteArray) {
        if (isPlaying) {
            audioQueue.offer(pcmData)
        }
    }
    
    fun isPlaying(): Boolean = isPlaying
    
    fun clearQueue() {
        audioQueue.clear()
    }
}