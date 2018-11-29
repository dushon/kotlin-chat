package com.example.demo.frontend

import com.example.demo.frontend.main.serviceWorkerMain
import com.example.demo.frontend.main.windowMain

private suspend fun main() {
    if (isWindow()) {
        windowMain()
    } else {
        serviceWorkerMain()
    }
}

private fun isWindow(): Boolean = js("self.window === self") as Boolean
