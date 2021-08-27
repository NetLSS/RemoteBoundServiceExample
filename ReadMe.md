# Remote Bound Service

- 앱과 다른 프로세스에서 실행되는 서비스와의 통신

![img1](img/img1.png)


## Service

```kotlin
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
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.lilcode.example.remoteboundservice">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.RemoteBoundService">
        <service
            android:name=".RemoteService"
            android:enabled="true"
            android:exported="true"
            android:process=":my_process" />

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

## Main Activity

```kotlin
package com.lilcode.example.remoteboundservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.View

class MainActivity : AppCompatActivity() {

    var myService: Messenger? = null
    var isBound: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(applicationContext, RemoteService::class.java)
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE)
    }

    private val myConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            myService = Messenger(service)
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            myService = null
            isBound = false
        }

    }

    fun sendMessage(view: View) {
        if (!isBound) return

        val msg = Message.obtain()
        val bundle = Bundle()

        bundle.putString("MyString", "Message Received!")

        msg.data = bundle

        try {
            myService?.send(msg)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }
}
```