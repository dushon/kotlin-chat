package com.example.demo.frontend.external

import kotlin.js.Promise

external interface LocalForage {
    fun setItem(key: String, value: Any): Promise<Unit>
    fun getItem(key: String): Promise<Any>
}

@JsModule("localforage")
@JsNonModule
external val localForage: LocalForage
