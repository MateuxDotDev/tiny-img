package dev.mateux.adapters

import dev.mateux.application.dto.QueuePayload
import dev.mateux.ports.MessageQueue
import jakarta.enterprise.context.ApplicationScoped
import kotlinx.coroutines.future.await
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
class MessageQueueImpl(
    @Channel("optimize") private var emitter: Emitter<QueuePayload>
) : MessageQueue {
    override fun sendImage(payload: QueuePayload): Boolean {
        val returnValue = emitter.send(payload).toCompletableFuture().join()
        return true
    }
}