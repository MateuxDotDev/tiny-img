package dev.mateux.ports

interface WebSocketProvider {
    fun sendMessage(target: String, message: String): Boolean
}