package unrn.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import unrn.DTOs.NuevoRetweet;
import unrn.DTOs.NuevoTweet;
import unrn.DTOs.TweetDto;
import unrn.DTOs.TweetsUsuarioResponseDto;
import unrn.model.Tweet;
import unrn.service.TwitterService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TweetControllerTest {

    @Mock
    private TwitterService twitterService;

    @InjectMocks
    private TweetController tweetController;

    @Test
    void crearTweet_ValidRequest_ReturnsCreated() {
        // Arrange
        NuevoTweet request = new NuevoTweet(1L, "Test tweet");
        doNothing().when(twitterService).crearTweet(anyLong(), anyString());

        // Act
        ResponseEntity<?> response = tweetController.crearTweet(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(twitterService, times(1)).crearTweet(1L, "Test tweet");
    }

    @Test
    void crearTweet_ServiceThrowsException_ReturnsBadRequest() {
        // Arrange
        NuevoTweet request = new NuevoTweet(1L, "");
        String errorMessage = "Texto inválido";
        doThrow(new RuntimeException(errorMessage))
                .when(twitterService).crearTweet(anyLong(), anyString());

        // Act & Assert
        var ex = assertThrows(RuntimeException.class, () -> {
            tweetController.crearTweet(request);
        });
        assertEquals(errorMessage, ex.getMessage());
    }

    @Test
    void crearRetweet_ValidRequest_ReturnsCreated() {
        // Arrange
        NuevoRetweet request = new NuevoRetweet(1L, 2L);
        doNothing().when(twitterService).crearRetweet(anyLong(), anyLong());

        // Act
        ResponseEntity<?> response = tweetController.crearRetweet(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(twitterService, times(1)).crearRetweet(1L, 2L);
    }

    @Test
    void listarTweetsDeUsuario_ValidUserId_ReturnsTweets() {
        // Arrange
        Long userId = 1L;
        Tweet tweet1 = mock(Tweet.class);
        Tweet tweet2 = mock(Tweet.class);
        Tweet tweetOriginal = mock(Tweet.class);
        unrn.model.Usuario author = mock(unrn.model.Usuario.class);
        unrn.model.Usuario autorOriginal = mock(unrn.model.Usuario.class);

        when(author.obtenerUserName()).thenReturn("testuser");
        when(autorOriginal.obtenerUserName()).thenReturn("originaluser");

        when(twitterService.listarTweetsDeUsuarioConLimit(userId, 15, 0))
                .thenReturn(List.of(tweet1, tweet2));
        when(twitterService.contarTweetsDeUsuario(userId))
                .thenReturn(2);

        // Tweet normal
        when(tweet1.getId()).thenReturn(101L);
        when(tweet1.texto()).thenReturn("First tweet");
        when(tweet1.autor()).thenReturn(author);
        when(tweet1.getFechaCreacion()).thenReturn(java.time.LocalDateTime.now());
        when(tweet1.origen()).thenReturn(null);

        // Retweet
        when(tweet2.getId()).thenReturn(102L);
        when(tweet2.texto()).thenReturn(null);
        when(tweet2.autor()).thenReturn(author);
        when(tweet2.getFechaCreacion()).thenReturn(java.time.LocalDateTime.now());
        when(tweet2.origen()).thenReturn(tweetOriginal);
        when(tweetOriginal.getId()).thenReturn(101L);
        when(tweetOriginal.texto()).thenReturn("Original text");
        when(tweetOriginal.autor()).thenReturn(autorOriginal);

        // Act
        ResponseEntity<?> response = tweetController.listarTweetsDeUsuario(userId, 15, 0);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof TweetsUsuarioResponseDto);

        TweetsUsuarioResponseDto responseDto = (TweetsUsuarioResponseDto) response.getBody();
        assertEquals(2, responseDto.tweets().size());
        assertEquals(101L, responseDto.tweets().get(0).id());
        assertEquals("First tweet", responseDto.tweets().get(0).texto());
        assertEquals("testuser", responseDto.tweets().get(0).autorUsername());
        assertNull(responseDto.tweets().get(0).origenId());
        assertFalse(responseDto.tweets().get(0).esRetweet());

        assertEquals(102L, responseDto.tweets().get(1).id());
        assertNotNull(responseDto.tweets().get(1).origenId());
        assertTrue(responseDto.tweets().get(1).esRetweet());
        assertEquals("Original text", responseDto.tweets().get(1).tweetOriginalTexto());
        assertEquals("originaluser", responseDto.tweets().get(1).usuarioOriginal());
        assertEquals("testuser", responseDto.tweets().get(1).usuarioRetweet());
    }

    @Test
    void listarTodosLosTweets_ReturnsAllTweets() {
        // Arrange
        Tweet tweet1 = mock(Tweet.class);
        Tweet tweet2 = mock(Tweet.class);
        unrn.model.Usuario author = mock(unrn.model.Usuario.class);
        when(author.obtenerUserName()).thenReturn("testuser");
        when(twitterService.listarTodosLosTweets())
                .thenReturn(List.of(tweet1, tweet2));
        when(tweet1.getId()).thenReturn(101L);
        when(tweet1.texto()).thenReturn("First tweet");
        when(tweet1.autor()).thenReturn(author);
        when(tweet1.getFechaCreacion()).thenReturn(java.time.LocalDateTime.now());
        when(tweet1.origen()).thenReturn(null);
        when(tweet2.getId()).thenReturn(102L);
        when(tweet2.texto()).thenReturn("Second tweet");
        when(tweet2.autor()).thenReturn(author);
        when(tweet2.getFechaCreacion()).thenReturn(java.time.LocalDateTime.now());
        when(tweet2.origen()).thenReturn(null);

        // Act
        ResponseEntity<?> response = tweetController.listarTodosLosTweets();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);

        @SuppressWarnings("unchecked")
        List<TweetDto> tweets = (List<TweetDto>) response.getBody();
        assertEquals(2, tweets.size());
        assertEquals(101L, tweets.get(0).id());
        assertEquals("First tweet", tweets.get(0).texto());
        assertEquals("testuser", tweets.get(0).autorUsername());
        assertNull(tweets.get(0).origenId());
        assertFalse(tweets.get(0).esRetweet());
        assertEquals(102L, tweets.get(1).id());
        assertEquals("Second tweet", tweets.get(1).texto());
        assertEquals("testuser", tweets.get(1).autorUsername());
        assertNull(tweets.get(1).origenId());
        assertFalse(tweets.get(1).esRetweet());
    }

    @Test
    void listarTweetsDeUsuario_ServiceThrowsException_ReturnsBadRequest() {
        // Arrange
        Long userId = 1L;
        String errorMessage = "Usuario no encontrado";
        when(twitterService.listarTweetsDeUsuarioConLimit(userId, 15, 0))
                .thenThrow(new RuntimeException(errorMessage));

        // Act & Assert
        var ex = assertThrows(RuntimeException.class, () -> {
            tweetController.listarTweetsDeUsuario(userId, 15, 0);
        });
        assertEquals(errorMessage, ex.getMessage());
    }
}
