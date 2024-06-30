package dev.mateux.adapters

import dev.mateux.application.WebSocketInstances
import dev.mateux.ports.WebSocketProvider
import io.smallrye.jwt.auth.principal.JWTParser
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.websocket.OnClose
import jakarta.websocket.OnError
import jakarta.websocket.OnMessage
import jakarta.websocket.OnOpen
import jakarta.websocket.Session
import jakarta.websocket.server.PathParam
import jakarta.websocket.server.ServerEndpoint

@ApplicationScoped
@ServerEndpoint("/notifications/{token}")
class WebSocketProviderImpl(
    @Inject private var jwtParser: JWTParser
): WebSocketProvider {
    override fun sendMessage(target: String, message: String): Boolean {
        return WebSocketInstances.sessions[target]?.let {
            it.asyncRemote.sendText(message)
            true
        } ?: false
    }

    fun validateToken(session: Session, token: String): Boolean {
        try {
            WebSocketInstances.sessions[jwtParser.parse(token).subject] = session
            return true
        } catch (e: Exception) {
            session.asyncRemote.sendText("Invalid token")
            session.close()
        }

        return false
    }

    @OnOpen
    fun onOpen(session: Session, @PathParam("token") token: String) {
        if (validateToken(session, token)) {
            session.asyncRemote.sendText("Connected")
        }
    }

    @OnClose
    fun onClose(session: Session, @PathParam("token") token: String) {
        WebSocketInstances.sessions.remove(jwtParser.parse(token).subject)
    }

    @OnError
    fun onError(session: Session, @PathParam("token") token: String, throwable: Throwable) {
        WebSocketInstances.sessions.remove(jwtParser.parse(token).subject)
    }

    @OnMessage
    fun onMessage(session: Session, @PathParam("token") token: String) {
        if (validateToken(session, token)) {
            session.asyncRemote.sendText("Token validated")
        }
    }
}