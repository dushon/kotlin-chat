package com.example.demo.shared.message

interface Message

data class HelloMessage(val body: String) : Message
data class HelloBroadcastMessage(val author: String, val body: String) : Message
