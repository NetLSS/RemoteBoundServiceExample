package com.lilcode.example.remoteboundservice

import android.app.Service
import android.content.Intent
import android.os.*
import android.widget.Toast

class RemoteService : Service() {

    private var count = 0

    private  val myMessenger = Messenger(IncomingHandler()) // 핸들러 인스턴스 생성 및 이것을 전달하여 메신저 객체 생성

    // 핸들러 구성하기
    inner class IncomingHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val data = msg.data
            val dataString = data.getString("MyString")
            Toast.makeText(applicationContext, "$dataString ${++count}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return myMessenger.binder // 메신저의 IBinder 객체를 반환
    }
}