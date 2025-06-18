package com.akuntansimandiri.app.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

class VoiceManager(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit
) {
    private var speechRecognizer: SpeechRecognizer? = null

    fun startListening() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onError(error: Int) {
                    val msg = when (error) {
                        SpeechRecognizer.ERROR_NO_MATCH -> "Tidak cocok, coba lagi."
                        SpeechRecognizer.ERROR_AUDIO -> "Masalah audio."
                        SpeechRecognizer.ERROR_CLIENT -> "Masalah client."
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Izin tidak cukup."
                        SpeechRecognizer.ERROR_NETWORK -> "Masalah jaringan."
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Perekam sibuk."
                        SpeechRecognizer.ERROR_SERVER -> "Masalah server."
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Tidak ada suara."
                        else -> "Error tidak diketahui: $error"
                    }
                    onError(msg)
                }

                override fun onResults(results: Bundle?) {
                    val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    data?.firstOrNull()?.let { onResult(it) }
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID")
        }

        speechRecognizer?.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}