package com.example.storageapp.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SpeechRecognitionHelper(private val context: Context) {
    
    private var speechRecognizer: SpeechRecognizer? = null
    private val _recognitionState = MutableStateFlow<RecognitionState>(RecognitionState.Idle)
    val recognitionState: StateFlow<RecognitionState> = _recognitionState
    
    sealed class RecognitionState {
        object Idle : RecognitionState()
        object Listening : RecognitionState()
        data class Success(val text: String) : RecognitionState()
        data class Error(val message: String) : RecognitionState()
    }
    
    fun startListening(onResult: (String) -> Unit) {
        // 检查权限
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            _recognitionState.value = RecognitionState.Error("需要录音权限")
            return
        }
        
        // 检查设备是否支持语音识别
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _recognitionState.value = RecognitionState.Error("设备不支持语音识别")
            Toast.makeText(context, "设备不支持语音识别", Toast.LENGTH_SHORT).show()
            return
        }
        
        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh-CN")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }
            
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    _recognitionState.value = RecognitionState.Listening
                }
                
                override fun onBeginningOfSpeech() {}
                
                override fun onRmsChanged(rmsdB: Float) {}
                
                override fun onBufferReceived(buffer: ByteArray?) {}
                
                override fun onEndOfSpeech() {}
                
                override fun onError(error: Int) {
                    val errorMessage = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "音频错误"
                        SpeechRecognizer.ERROR_CLIENT -> "客户端错误"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "权限不足"
                        SpeechRecognizer.ERROR_NETWORK -> "网络错误"
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "网络超时"
                        SpeechRecognizer.ERROR_NO_MATCH -> "无法识别"
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "识别器忙"
                        SpeechRecognizer.ERROR_SERVER -> "服务器错误"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "未检测到语音"
                        else -> "未知错误"
                    }
                    _recognitionState.value = RecognitionState.Error(errorMessage)
                    speechRecognizer?.destroy()
                    speechRecognizer = null
                }
                
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val text = matches[0]
                        _recognitionState.value = RecognitionState.Success(text)
                        onResult(text)
                    }
                    speechRecognizer?.destroy()
                    speechRecognizer = null
                }
                
                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        // 可以在这里处理部分结果
                    }
                }
                
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            
            _recognitionState.value = RecognitionState.Listening
            speechRecognizer?.startListening(intent)
            
        } catch (e: Exception) {
            _recognitionState.value = RecognitionState.Error("启动语音识别失败: ${e.message}")
            speechRecognizer?.destroy()
            speechRecognizer = null
        }
    }
    
    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            _recognitionState.value = RecognitionState.Idle
        } catch (e: Exception) {
            // 忽略错误
        }
    }
    
    fun cancelListening() {
        try {
            speechRecognizer?.cancel()
            _recognitionState.value = RecognitionState.Idle
        } catch (e: Exception) {
            // 忽略错误
        }
    }
    
    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
    
    companion object {
        const val REQUEST_RECORD_AUDIO_PERMISSION = 1001
        
        fun requestPermission(activity: Activity) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        }
        
        fun hasPermission(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}
