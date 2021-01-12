package com.jetbrains.kmm_app.androidApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.jetbrains.kmm_app.shared.Greeting
import android.widget.TextView
import com.jetbrains.kmm_app.shared.AppSocket
import com.jetbrains.kmm_app.shared.AppSocket.State

class MainActivity : AppCompatActivity() {
    private val socket = AppSocket("wss://echo.websocket.org")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv: TextView = findViewById(R.id.text_view)

        val connectButton: Button = findViewById(R.id.connect_button)
        connectButton.setOnClickListener {
            if (socket.currentState == State.CONNECTED) socket.send(greet()) else socket.connect()
        }
        val disconnectButton: Button = findViewById(R.id.disconnect_button)
        disconnectButton.setOnClickListener {
            socket.disconnect()
        }

        socket.messageListener = {
            runOnUiThread {
                tv.text = "Echo: $it"
            }
        }

        socket.stateListener = {
            runOnUiThread {
                tv.text = it.toString()
                when (it) {
                    State.CONNECTING -> {
                        connectButton.isEnabled = false
                        disconnectButton.isEnabled =  true
                    }
                    State.CONNECTED -> {
                        connectButton.isEnabled = true
                        connectButton.text = "Send message"
                        disconnectButton.isEnabled = true
                    }
                    State.CLOSING -> {
                        connectButton.isEnabled = false
                        disconnectButton.isEnabled = false
                    }
                    State.CLOSED -> {
                        socket.socketError?.let { tv.text = it.message }
                        connectButton.text = "Connect"
                        connectButton.isEnabled = true
                        disconnectButton.isEnabled = false
                    }
                }
            }
        }
    }

    private fun greet(): String {
        return Greeting().greeting()
    }
}
