package com.example.demo.frontend.external.indexeddb

import org.w3c.dom.Window
import kotlin.js.*

external interface IDBRequestEvent

external interface IDBRequestSuccessEventTarget<TResult> {
    val result: TResult
}

external interface IDBRequestSuccessEvent<TResult> : IDBRequestEvent {
    val target: IDBRequestSuccessEventTarget<TResult>
}

external interface IDBRequestErrorEvent : IDBRequestEvent

external interface IDBVersionChangeEvent : IDBRequestEvent, IDBRequestSuccessEvent<IDBDatabase>

external interface IDBRequest<TResult> {
    val result: TResult?

    var onerror: ((event: IDBRequestErrorEvent) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally

    var onsuccess: ((event: IDBRequestSuccessEvent<TResult>) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}

external interface IDBOpenDBRequest : IDBRequest<IDBDatabase> {
    var onupgradeneeded: ((event: IDBVersionChangeEvent) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}

external interface IDBCursor {
    fun `continue`()
    fun delete()
}

external interface IDBIndexParameters {
    var unique: Boolean?
}

external interface IDBIndex {
    fun openCursor(): IDBRequest<IDBCursorWithValue<Any>?>
}

external interface IDBCursorWithValue<TValue> : IDBCursor {
    val value: TValue
}

external interface IDBObjectStore {
    fun add(value: Any)
    fun add(value: Any, key: Any)
    fun clear()
    fun createIndex(indexName: String, keyPath: Array<String>)
    fun createIndex(indexName: String, keyPath: Array<String>, objectParameters: IDBIndexParameters)
    fun delete(key: Any)
    fun index(name: String): IDBIndex
    fun openCursor(): IDBRequest<IDBCursorWithValue<Any>?>
}

external interface IDBTransaction {
    fun objectStore(name: String): IDBObjectStore
}

external interface IDBDatabase {
    fun createObjectStore(name: String, options: Json): IDBObjectStore
    fun transaction(objectStore: String, mode: String = definedExternally): IDBTransaction
    fun transaction(objectStores: Array<String>, mode: String = definedExternally): IDBTransaction
}

external interface IDBFactory {
    fun open(name: String, version: Long = definedExternally): IDBOpenDBRequest
}

val Window.indexedDB: IDBFactory
    inline get() = this.window.asDynamic().indexedDB.unsafeCast<IDBFactory>()

fun createIDBIndexParameters() = js("{}").unsafeCast<IDBIndexParameters>()
