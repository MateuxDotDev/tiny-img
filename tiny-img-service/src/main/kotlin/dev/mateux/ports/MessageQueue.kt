package dev.mateux.ports

import dev.mateux.application.dto.QueuePayload

interface MessageQueue {
    fun sendImage(payload: QueuePayload): Boolean
}