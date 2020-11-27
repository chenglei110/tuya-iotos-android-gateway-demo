package com.tuya.smart.android.demo.speech

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.tuya.smart.iotgateway.gateway.TuyaIotGateway
import com.tuya.smart.iotgateway.speech.OnSpeechCallback
import com.tuya.smart.iotgateway.speech.SpeechHelper
import com.tuya.smartpad.demo.R
import kotlinx.android.synthetic.main.activity_speach_test.*

class SpeechTestActivity : Activity() {
    var helper: SpeechHelper? = null

    companion object {
        val TAG: String = "SpeechTestActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speach_test)

        init()
    }

    private fun init() {
        try {
            helper = SpeechHelper(this, "/sdcard/tuya_speech_config/", object : OnSpeechCallback {
                override fun onDeInitComplete() {
                    TODO("SpeechHelper closed successfully.")
                }

                override fun onInitComplete() {
                    TODO("SpeechHelper initialized successfully")
                }

                override fun onDeInitError(errMsg: String) {
                    TODO("SpeechHelper close error：errMsg")
                }

                override fun onCommand(command: String, data: String) {
                    TODO("Received offline command")
                }

                override fun getCommands(): Array<String> {
                    TODO("Offline commands regist")
                }

                override fun onInitError(errMsg: String) {
                    TODO("SpeechHelper initialization failed：errMsg")
                }

                override fun onASRError(errMsg: String) {
                    TODO("ASRError：errMsg")
                }

                override fun onPermissionDenied() {
                    TODO("Permission Denied")
                }

                override fun onStartListening() {
                    TODO("Listening")
                }

                override fun onWakeup(): Boolean {
                    TODO("Wake up. Return true to intercept start listening; return false to not intercept")
                }

                override fun onSpeechBeginning(errCode: Int) {
                    TODO("Not yet implemented")
                }

                override fun onSpeechEnd(errCode: Int) {
                    TODO("Not yet implemented")
                }

                override fun onResponse(success: Boolean, isDialog: Boolean, audioPath: ArrayList<String>?) {
                    TODO("Received cloud response")
                }
            }, 10000, "onlyonekey")
        } catch (e: Exception) {
            Log.e(TAG, e.message)
            return
        }

        helper?.start()

        TuyaIotGateway.getInstance().setSpeechHandler(helper?.getHandler())
    }

    override fun onDestroy() {
        super.onDestroy()

        helper?.stop()
    }

    private fun setText(text: String) {
        runOnUiThread {
            text_view.text = text
        }
    }
}
