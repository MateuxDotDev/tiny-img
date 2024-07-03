package dev.mateux.adapters

import dev.mateux.application.WebSocketInstances
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.jwt.auth.principal.JWTParser
import jakarta.websocket.Session
import org.junit.jupiter.api.*
import org.mockito.Mockito.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("WebSocket Provider Impl Test")
class WebSocketProviderImplTest {
    private lateinit var jwtParser: JWTParser
    private lateinit var session: Session
    private lateinit var webSocketProvider: WebSocketProviderImpl

    @BeforeAll
    fun setUp() {
        jwtParser = mock(JWTParser::class.java)
        session = mock(Session::class.java)
        webSocketProvider = WebSocketProviderImpl(jwtParser)
    }

    @AfterEach
    fun tearDown() {
        reset(jwtParser, session)
    }

    @Test
    fun `should send message to target user and return true`() {
        // Arrange
        WebSocketInstances.sessions["user"] = session
        `when`(session.asyncRemote).thenReturn(mock())
        `when`(jwtParser.parse("token")).thenReturn(mock())
        `when`(jwtParser.parse("token").subject).thenReturn("user")

        // Act
        val result = webSocketProvider.sendMessage("user", "message")

        // Assert
        verify(session.asyncRemote, times(1)).sendText("message")
        assertTrue(result)
    }

    @Test
    fun `should not send message to target when session is missing and return false`() {
        // Arrange & Act
        val result = webSocketProvider.sendMessage("missing-user", "message")

        // Assert
        assertFalse(result)
    }

    @Test
    fun `should validate token on open`() {
        // Arrange
        `when`(jwtParser.parse("token")).thenReturn(mock())
        `when`(jwtParser.parse("token").subject).thenReturn("user")
        `when`(session.asyncRemote).thenReturn(mock())

        // Act
        webSocketProvider.onOpen(session, "token")

        // Assert
        verify(session.asyncRemote, times(1)).sendText("Connected")
    }

    @Test
    fun `should remove session on close`() {
        // Arrange
        WebSocketInstances.sessions["user"] = session
        `when`(jwtParser.parse("token")).thenReturn(mock())
        `when`(jwtParser.parse("token").subject).thenReturn("user")

        // Act
        webSocketProvider.onClose(session, "token")

        // Assert
        assertFalse(WebSocketInstances.sessions.containsKey("user"))
    }

    @Test
    fun `should remove session on error`() {
        // Arrange
        WebSocketInstances.sessions["user"] = session
        `when`(jwtParser.parse("token")).thenReturn(mock())
        `when`(jwtParser.parse("token").subject).thenReturn("user")

        // Act
        webSocketProvider.onError(session, "token", RuntimeException())

        // Assert
        assertFalse(WebSocketInstances.sessions.containsKey("user"))
    }

    @Test
    fun `should remove session when token is not valid`() {
        // Arrange
        WebSocketInstances.sessions[""] = session
        `when`(jwtParser.parse("token")).thenThrow(RuntimeException())
        `when`(session.asyncRemote).thenReturn(mock())

        // Act
        webSocketProvider.onOpen(session, "token")

        // Assert
        assertFalse(WebSocketInstances.sessions.containsKey(""))
    }
}