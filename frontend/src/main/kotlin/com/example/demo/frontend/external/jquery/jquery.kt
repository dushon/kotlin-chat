package com.example.demo.frontend.external.jquery

import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.events.Event

external interface JQuery<TElement : Node> {
    fun append(vararg contents: String): JQuery<TElement>
    fun hide(): JQuery<TElement>
    fun html(html: String)
    fun on(eventName: String, handler: (Event) -> Unit): JQuery<TElement>
    fun prop(propertyName: String, value: Any)
    fun show(): JQuery<TElement>
    fun `val`(): dynamic /* String | Number | Array<String> */
    fun `val`(value: String): JQuery<TElement>
}

@Suppress("UnsafeCastFromDynamic")
inline operator fun <TElement: Node> JQuery<TElement>.invoke(noinline callback: (JQuery<TElement>) -> Unit): JQuery<TElement> = asDynamic()(callback)
@Suppress("UnsafeCastFromDynamic")
inline operator fun <TElement: Node> JQuery<TElement>.invoke(selector: String): JQuery<TElement> = asDynamic()(selector)

@JsModule("jquery")
@JsNonModule
external val jQuery: JQuery<HTMLElement>
