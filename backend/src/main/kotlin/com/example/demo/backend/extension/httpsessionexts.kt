package com.example.demo.backend.extension

import javax.servlet.http.HttpSession

operator fun HttpSession.set(name: String, value: Any) {
    return this.setAttribute(name, value)
}

operator fun HttpSession.get(name: String): Any? {
    return this.getAttribute(name)
}