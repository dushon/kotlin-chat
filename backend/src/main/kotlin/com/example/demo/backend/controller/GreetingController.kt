package com.example.demo.backend.controller

import com.example.demo.backend.extension.get
import com.example.demo.backend.extension.set
import com.example.demo.shared.message.HelloBroadcastMessage
import com.example.demo.shared.message.HelloMessage
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.util.HtmlUtils
import javax.servlet.http.HttpServletRequest
import kotlin.contracts.contract

@Controller
class WelcomeController {

    @GetMapping("/")
    fun index(request: HttpServletRequest, model: Model): String {
        val username = request.session["username"] as String?


        if (!isValidUsername(username)) {
            return "redirect:/chat/login"
        }

        model["username"] = username


        return "chat"
    }

    @GetMapping("/chat/login")
    fun login(): String {
        return "login"
    }

    @PostMapping("/chat/login")
    fun doLogin(request: HttpServletRequest, @RequestParam(name = "username", defaultValue = "") originalUsername: String): String {
        val username = originalUsername.trim()

        if (!isValidUsername(username)) {
            return "login"
        }

        request.session["username"] = username

        return "redirect:/"
    }

    @RequestMapping("/logout")
    fun logout(request: HttpServletRequest): String {
        request.session.invalidate()
        return "redirect:/chat/login"
    }

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    fun sendMessage(@Payload message: HelloMessage, headerAccessor: SimpMessageHeaderAccessor): HelloBroadcastMessage {
        val author = headerAccessor.sessionAttributes?.get("username") as String? ?: throw Exception("Unknown username")
        val body = HtmlUtils.htmlEscape(message.body)
        return HelloBroadcastMessage(author, body)
    }

}

private fun isValidUsername(username: String?): Boolean {
    contract {
        returns(true) implies (username != null)
    }
    return !username.isNullOrBlank()
}
