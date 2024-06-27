package dev.mateux.adapters

import dev.mateux.application.dto.QueuePayload
import dev.mateux.ports.MessageQueue
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
class MessageQueueImpl(
    @Channel("quote-requests") private var emitter: Emitter<QueuePayload>
) : MessageQueue {
    override fun sendImage(payload: QueuePayload): Boolean {
        emitter.send(payload)
        return true
    }
}