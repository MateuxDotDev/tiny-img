package dev.mateux.adapters

import dev.mateux.ports.WebSocketProvider
import io.smallrye.common.annotation.RunOnVirtualThread
import io.vertx.core.json.JsonObject
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.jboss.logging.Logger

@ApplicationScoped
class NotificationsConsumer(
    @Inject private var webSocket: WebSocketProvider
) {
    @Incoming("notifications")
    @RunOnVirtualThread
    fun consumeNotification(notification: JsonObject) {
        if (!webSocket.sendMessage(notification.getString("targetUser"), notification.getString("message"))) {
            Logger.getLogger(NotificationsConsumer::class.java).warn("Failed to send notification")
        }
    }
}
