package com.example.demo.frontend.extension

import com.example.demo.frontend.external.stompjs.Client
import com.example.demo.frontend.external.stompjs.IPublishParams
import com.example.demo.frontend.external.stompjs.StompConfig

private data class PublishParams(override var destination: String, override var body: String?) : IPublishParams

fun Client.publish(destination: String, body: String) {
    this.publish(PublishParams(destination, body))
}

/* STOMP Client builder DSL */

@DslMarker
annotation class ClientDslTagMarker

@ClientDslTagMarker
class ClientDsl {
    private lateinit var stompConfig: StompConfig
    var onConnect: ((Client) -> Unit)? = null
    var onDisconnect: (() -> Unit)? = null
    var onStompError: (() -> Unit)? = null

    fun config(init: StompConfig.() -> Unit) {
        stompConfig = js("{}").unsafeCast<StompConfig>().apply(init)
    }

    fun build(init: ClientDsl.() -> Unit): Client {
        init()

        val client = Client(stompConfig)
        onConnect?.let { client.onConnect = { it(client) } }
        onDisconnect?.let { client.onDisconnect = it }
        onStompError?.let { client.onStompError = it }
        return client
    }
}

fun stompClient(init: ClientDsl.() -> Unit) = ClientDsl().build(init)
