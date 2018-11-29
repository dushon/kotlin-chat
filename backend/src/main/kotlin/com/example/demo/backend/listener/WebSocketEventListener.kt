package com.example.demo.backend.listener

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent

@Component
class WebSocketEventListener {

    @EventListener
    fun handleWebSocketConnection(event: SessionConnectedEvent) {
        println("New websocket connection")
    }

    @EventListener
    fun handleWebSocketDisconnection(event: SessionDisconnectEvent) {
        println("Websocket connection terminated")
    }

}