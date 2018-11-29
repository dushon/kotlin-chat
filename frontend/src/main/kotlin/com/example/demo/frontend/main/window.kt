package com.example.demo.frontend.main

import com.example.demo.frontend.extension.publish
import com.example.demo.frontend.external.LocalForage
import com.example.demo.frontend.external.jquery.invoke
import com.example.demo.frontend.external.jquery.jQuery
import com.example.demo.frontend.external.localForage
import com.example.demo.frontend.external.stompjs.Client
import com.example.demo.shared.MyLibrary
import com.example.demo.shared.message.HelloBroadcastMessage
import com.example.demo.shared.message.HelloMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLScriptElement
import org.w3c.notifications.*
import org.w3c.workers.RegistrationOptions
import kotlin.browser.document
import kotlin.browser.window

private class LocalForageQueue<T>(private val storage: LocalForage, private val storageKey: String) {
    suspend fun pop(): T? {
        val items = getQueuedItems()
        if (items.isEmpty()) {
            return null
        }
        setQueuedItems(items.sliceArray(1..(items.size - 1)))
        return items[0]
    }

    suspend fun push(item: T) {
        setQueuedItems(getQueuedItems() + item)
    }

    suspend fun unpop(item: T) {
        setQueuedItems(arrayOf(item) + getQueuedItems())
    }

    private suspend fun getQueuedItems(): Array<T> {
        return storage.getItem(storageKey).await().unsafeCast<Array<T>?>() ?: emptyArray()
    }

    private suspend fun setQueuedItems(messages: Array<T>) {
        storage.setItem(storageKey, messages).await()
    }
}

private val messageQueue = LocalForageQueue<HelloMessage>(localForage, "messageQueue")

private lateinit var stompClient: Client // initialized in windowMain

private fun setConnected(connected: Boolean) {
    jQuery("#connect").prop("disabled", connected)
    jQuery("#disconnect").prop("disabled", !connected)
}

private fun connect() {
    if (!stompClient.connected) {
        stompClient.activate()
    }
}

private fun disconnect() {
    if (stompClient.connected) {
        stompClient.deactivate()
    }
}

private suspend fun flushMessageQueue() {
    if (!stompClient.connected) {
        return
    }

    while (true) {
        val message = messageQueue.pop() ?: break
        try {
            stompClient.publish("/app/hello", JSON.stringify(message))
        } catch (e: Throwable) {
            messageQueue.unpop(message)
            throw e
        }
    }
}

private suspend fun sendName() {
    val messageInput = jQuery("#message")
    val messageBody = messageInput.`val`() as String
    messageQueue.push(HelloMessage(messageBody))
    flushMessageQueue()
    messageInput.`val`("")
}

private fun showGreeting(message: HelloBroadcastMessage) {
    jQuery("#messages").append("<tr><td>${message.author}</td><td>${message.body}</td></tr>")

    fun showGreetingNotification() {
        println("Showing notification")
        Notification("New message from ${message.author}", NotificationOptions(body = message.body))
    }

    when (Notification.permission) {
        NotificationPermission.GRANTED -> showGreetingNotification()
        NotificationPermission.DEFAULT -> GlobalScope.launch {
            println("Asking for permission to notify")
            val permission = Notification.requestPermission().await()
            if (permission == NotificationPermission.GRANTED) {
                showGreetingNotification()
            }
        }
    }
}

private fun onInit() {
    jQuery("form").on("submit") {
        it.preventDefault()
    }
    jQuery("#connect").on("click") {
        println("Connecting")
        connect()
    }
    jQuery("#disconnect").on("click") {
        println("Disconnecting")
        disconnect()
    }
    jQuery("#send").on("click") {
        GlobalScope.launch {
            println("Sending")
            sendName()
        }
    }
}

private suspend fun registerServiceWorker() {
    val currentScript = document.currentScript
    if (currentScript is HTMLScriptElement) {
        val currentScriptPath = currentScript.src
        window.navigator.serviceWorker.register(currentScriptPath).await()
        println("Service worker $currentScriptPath registered")
    }
}

suspend fun windowMain() {
    val wsRoot = window.location.protocol.replace("http", "ws") + "//" + window.location.host
    stompClient = com.example.demo.frontend.extension.stompClient {
        config {
            brokerURL = "$wsRoot/gs-guide-websocket"
        }
        onConnect = { client ->
            GlobalScope.launch {
                client.subscribe("/topic/greetings") { message ->
                    message.body?.let {
                        showGreeting(JSON.parse(it))
                    }
                }

                setConnected(true)
                println("Connected")

                println("Sending queued messages")
                flushMessageQueue()
            }
        }
        onDisconnect = {
            setConnected(false)
            println("Disconnected")
        }
        onStompError = {
            setConnected(false)
            println("Disconnected due to error")
        }
    }

    println(MyLibrary().kotlinLanguage().name)
    jQuery { onInit() }
    registerServiceWorker()
}
