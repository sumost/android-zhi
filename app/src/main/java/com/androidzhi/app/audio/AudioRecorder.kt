package com.androidzhi.app.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import com.androidzhi.app.data.model.Constants
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 音频录制器
 */
@Singleton
class AudioRecorder @Inject constructor() {
    
    companion object {
        private const val TAG = "AudioRecorder"
    }
    
    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private var recordingJob: Job? = null
    
    private val _audioDataFlow = MutableSharedFlow<ByteArray>(extraBufferCapacity = 10)
    val audioDataFlow: SharedFlow<ByteArray> = _audioDataFlow
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    @SuppressLint("MissingPermission")
    fun startRecording(): Boolean {
        if (isRecording) return true
        
        return try {
            val sampleRate = Constants.AudioConfig.INPUT_SAMPLE_RATE
            val channelConfig = AudioFormat.CHANNEL_IN_MONO
            val audioFormat = AudioFormat.ENCODING_PCM_16BIT
            
            val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
            
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                minBufferSize * 2
            )
            
            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                Log.e(TAG, "AudioRecord 初始化失败")
                return false
            }
            
            audioRecord?.startRecording()
            isRecording = true
            
            recordingJob = scope.launch {
                val buffer = ShortArray(Constants.AudioConfig.INPUT_FRAME_SIZE)
                
                while (isRecording && isActive) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {
                        val pcmData = shortArrayToByteArray(buffer.copyOf(read))
                        _audioDataFlow.tryEmit(pcmData)
                    }
                }
            }
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "开始录制失败", e)
            stopRecording()
            false
        }
    }
    
    fun stopRecording() {
        isRecording = false
        recordingJob?.cancel()
        
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            Log.e(TAG, "停止录制失败", e)
        }
        
        audioRecord = null
    }
    
    fun isRecording(): Boolean = isRecording
    
    private fun shortArrayToByteArray(shortArray: ShortArray): ByteArray {
        val byteArray = ByteArray(shortArray.size * 2)
        for (i in shortArray.indices) {
            byteArray[i * 2] = (shortArray[i].toInt() and 0xFF).toByte()
            byteArray[i * 2 + 1] = ((shortArray[i].toInt() shr 8) and 0xFF).toByte()
        }
        return byteArray
    }
}