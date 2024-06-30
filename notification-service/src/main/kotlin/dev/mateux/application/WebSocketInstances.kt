package dev.mateux.application

import jakarta.websocket.Session

object WebSocketInstances {
    val sessions = mutableMapOf<String, Session>()
}