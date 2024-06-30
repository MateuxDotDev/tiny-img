package dev.mateux.adapters

import dev.mateux.domain.Notification
import dev.mateux.ports.WebSocketProvider
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.eclipse.microprofile.reactive.messaging.Message
import java.util.concurrent.CompletionStage

@ApplicationScoped
class NotificationsConsumer(
    @Inject private var webSocket: WebSocketProvider
) {
    @Incoming("notifications")
    fun consumeNotification(notification: Message<Notification>): CompletionStage<Void> {
        return if (webSocket.sendMessage(notification.payload.targetUser, notification.payload.message)) {
            notification.ack()
        } else {
            notification.nack(RuntimeException("Failed to send notification"))
        }
    }
}
