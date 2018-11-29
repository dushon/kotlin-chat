package com.example.demo.backend.listener

import com.example.demo.backend.extension.get
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor

@Component
class HttpHandshakeInterceptor : HandshakeInterceptor {
    override fun afterHandshake(request: ServerHttpRequest, response: ServerHttpResponse, wsHandler: WebSocketHandler, exception: Exception?) {
    }

    override fun beforeHandshake(request: ServerHttpRequest, response: ServerHttpResponse, wsHandler: WebSocketHandler, attributes: MutableMap<String, Any>): Boolean {
        if (request is ServletServerHttpRequest) {
            val username = request.servletRequest.session["username"] as String?
            username?.let { attributes["username"] = it }
        }
        return true
    }
}