@file:JsModule("@stomp/stompjs/esm6/index.js")
@file:JsNonModule

package com.example.demo.frontend.external.stompjs

external interface IPublishParams {
    var destination: String
    var body: String?
}

external interface Message {
    var body: String?
}

external interface StompConfig {
    var brokerURL: String?
    var reconnectDelay: Int?
    var heartbeatIncoming: Int?
    var heartbeatOutgoing: Int?
}

external class Client(config: StompConfig) {
    var onConnect: (() -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onDisconnect: (() -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onStompError: (() -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var connected: Boolean
        get() = definedExternally

    fun activate()
    fun deactivate()
    fun publish(params: IPublishParams)
    fun subscribe(destination: String, callback: (Message) -> Unit)
}
