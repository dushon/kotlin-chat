package com.example.demo.frontend.main

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.promise
import org.w3c.dom.WorkerGlobalScope
import org.w3c.fetch.Response
import org.w3c.workers.ExtendableEvent
import org.w3c.workers.FetchEvent

@JsName("self")
private external val workerSelf: WorkerGlobalScope

fun serviceWorkerMain() {
    val cacheName = "v1"
    val urlsToCache = arrayOf(
            "/",
            "/chat/login",
            "/frontend.bundle.js",
            "/webjars/bootstrap/4.1.3/css/bootstrap.min.css"
    )

    workerSelf.addEventListener("install", { event ->
        if (event is ExtendableEvent) {
            val cachePromise = GlobalScope.promise {
                val cache = workerSelf.caches.open(cacheName).await()
                cache.addAll(urlsToCache).await()
            }
            event.waitUntil(cachePromise)
        }
    })

    workerSelf.addEventListener("fetch", { event ->
        if (event is FetchEvent && event.request.method == "GET") {
            val responsePromise = GlobalScope.promise {
                val cache = workerSelf.caches.open(cacheName).await()
                val cachedResponse = cache.match(event.request).await() as Response?
                if (cachedResponse != null) {
                     cachedResponse
                } else {
                    val response = workerSelf.fetch(event.request).await()
                    cache.put(event.request, response.clone())
                    response
                }
            }
            event.respondWith(responsePromise)
        }
    })
}
