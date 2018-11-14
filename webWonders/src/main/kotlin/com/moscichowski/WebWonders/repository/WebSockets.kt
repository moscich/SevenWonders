package com.moscichowski.WebWonders.repository

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.HashMap


@Configuration
@EnableWebSocket
internal class WebsocketConfig : WebSocketConfigurer {

    @Bean
    fun myMessageHandler(): MyMessageHandler {
        return MyMessageHandler()
    }

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(myMessageHandler(), "/my-websocket-endpoint/*").setAllowedOrigins("*")
    }

}

class MyMessageHandler : TextWebSocketHandler() {

    private val map = HashMap<String, MutableList<WebSocketSession>>()

    @Synchronized
    fun notify(gameId: String) {
        map[gameId]?.forEach {
            it.sendMessage(TextMessage("Reload"))
        }
    }

    @Synchronized
    @Throws(Exception::class)
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val gameNo = session.uri?.path?.split("/")?.last()
        map[gameNo]?.removeIf { !it.isOpen }
    }

    @Synchronized
    @Throws(Exception::class)
    override fun afterConnectionEstablished(session: WebSocketSession) {
        // The WebSocket has been opened
        // I might save this session object so that I can send messages to it outside of this method

        val gameNo = session.uri?.path?.split("/")?.last()
        if(gameNo != null && map[gameNo] == null) {
            map[gameNo] = mutableListOf()
        }
        map[gameNo]?.add(session)

        // Let's send the first message
        println("aoweij")
        session.sendMessage(TextMessage("You are now connected to the server. This is the first message."))
    }

    @Throws(Exception::class)
    override fun handleTextMessage(session: WebSocketSession, textMessage: TextMessage) {
        // A message has been received
        System.out.println("Message received: " + textMessage.payload)
    }


}