package dev.mateux.adapters

import dev.mateux.ports.WebSocketProvider
import io.quarkus.test.junit.QuarkusTest
import io.vertx.core.json.JsonObject
import org.junit.jupiter.api.*
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Notification Consumer Test")
class NotificationsConsumerTest {
    private lateinit var websocketProvider: WebSocketProvider
    private lateinit var notificationsConsumer: NotificationsConsumer

    @BeforeAll
    fun setUp() {
        websocketProvider = mock(WebSocketProvider::class.java)
        notificationsConsumer = NotificationsConsumer(websocketProvider)
    }

    @AfterEach
    fun tearDown() {
        reset(websocketProvider)
    }

    @Test
    fun `should acquire if message is sent through websocket`() {
        // Arrange
        val notification = mock<JsonObject>()
        `when`(notification.getString("targetUser")).thenReturn("user")
        `when`(notification.getString("message")).thenReturn("message")
        `when`(websocketProvider.sendMessage(anyOrNull(), anyOrNull())).thenReturn(true)

        // Act
        notificationsConsumer.consumeNotification(notification)

        // Assert
        verify(websocketProvider).sendMessage("user", "message")
    }

    @Test
    fun `should acquire if message is not sent through websocket`() {
        // Arrange
        val notification = mock<JsonObject>()
        `when`(notification.getString("targetUser")).thenReturn("user")
        `when`(notification.getString("message")).thenReturn("message")
        `when`(websocketProvider.sendMessage(anyOrNull(), anyOrNull())).thenReturn(false)

        // Act
        notificationsConsumer.consumeNotification(notification)

        // Assert
        verify(websocketProvider).sendMessage("user", "message")
    }
}