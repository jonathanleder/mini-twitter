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
        java.time.LocalDateTime fechaOriginal = java.time.LocalDateTime.now().minusHours(2);
        TweetDto tweet1 = new TweetDto(101L, "First tweet", "testuser",
                java.time.LocalDateTime.now(), null, null, null, null, null, false);
        TweetDto tweet2 = new TweetDto(102L, null, "testuser",
                java.time.LocalDateTime.now(), 101L, fechaOriginal, "Original text", "originaluser", "testuser", true);

        when(twitterService.listarTweetsDeUsuarioConLimitDto(userId, 15, 0))
                .thenReturn(List.of(tweet1, tweet2));
        when(twitterService.contarTweetsDeUsuario(userId))
                .thenReturn(2);

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
        assertEquals(fechaOriginal, responseDto.tweets().get(1).origenFecha());
    }

    @Test
    void listarTodosLosTweets_ReturnsAllTweets() {
        // Arrange
        Tweet tweet1 = mock(Tweet.class);
        Tweet tweet2 = mock(Tweet.class);
        when(twitterService.listarTodosLosTweets())
                .thenReturn(List.of(tweet1, tweet2));
        when(tweet1.getId()).thenReturn(101L);
        when(tweet1.texto()).thenReturn("First tweet");
        when(tweet1.getAutorUsername()).thenReturn("testuser");
        when(tweet1.getFechaCreacion()).thenReturn(java.time.LocalDateTime.now());
        when(tweet1.getOrigenId()).thenReturn(null);
        // tweet2 es un retweet, para cubrir la rama esRetweet=true del mapper
        when(tweet2.getId()).thenReturn(102L);
        when(tweet2.texto()).thenReturn(null);
        when(tweet2.getAutorUsername()).thenReturn("retweeter");
        when(tweet2.getFechaCreacion()).thenReturn(java.time.LocalDateTime.now());
        when(tweet2.getOrigenId()).thenReturn(101L);
        when(tweet2.getOrigenFecha()).thenReturn(java.time.LocalDateTime.now().minusHours(1));
        when(tweet2.getOrigenTexto()).thenReturn("First tweet");
        when(tweet2.getOrigenAutorUsername()).thenReturn("testuser");

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
        assertNull(tweets.get(1).texto());
        assertEquals(101L, tweets.get(1).origenId());
        assertEquals("First tweet", tweets.get(1).tweetOriginalTexto());
        assertEquals("testuser", tweets.get(1).usuarioOriginal());
        assertEquals("retweeter", tweets.get(1).usuarioRetweet());
        assertTrue(tweets.get(1).esRetweet());
    }

    @Test
    void listarFeedPaginado_ValidRequest_ReturnsFeedResponse() {
        // Arrange
        TweetDto tweet1 = new TweetDto(201L, "Feed tweet", "testuser",
                java.time.LocalDateTime.now(), null, null, null, null, null, false);
        when(twitterService.listarFeedPaginadoDto(0, 10)).thenReturn(List.of(tweet1));
        when(twitterService.contarTweetsNormales()).thenReturn(21);

        // Act
        ResponseEntity<?> response = tweetController.listarFeedPaginado(0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof unrn.DTOs.FeedResponseDto);
        var feed = (unrn.DTOs.FeedResponseDto) response.getBody();
        assertEquals(1, feed.content().size());
        assertEquals(3, feed.totalPages(), "21 tweets con size 10 son 3 páginas");
        assertEquals(0, feed.currentPage());
        assertEquals(21, feed.totalElements());
        assertTrue(feed.hasNext(), "La página 0 de 3 debe tener siguiente");
        assertFalse(feed.hasPrevious(), "La página 0 no debe tener anterior");
    }

    @Test
    void listarFeedPaginado_UltimaPagina_NoTieneSiguiente() {
        // Arrange: página 2 (la última de 3), sí tiene anterior y no tiene siguiente
        when(twitterService.listarFeedPaginadoDto(2, 10)).thenReturn(List.of());
        when(twitterService.contarTweetsNormales()).thenReturn(21);

        // Act
        ResponseEntity<?> response = tweetController.listarFeedPaginado(2, 10);

        // Assert
        var feed = (unrn.DTOs.FeedResponseDto) response.getBody();
        assertFalse(feed.hasNext(), "La última página no debe tener siguiente");
        assertTrue(feed.hasPrevious(), "La página 2 debe tener anterior");
    }

    @Test
    void listarFeedPaginado_PageYSizeInvalidos_UsaValoresPorDefecto() {
        // Arrange: page negativo y size fuera de rango deben corregirse a 0 y 10
        when(twitterService.listarFeedPaginadoDto(0, 10)).thenReturn(List.of());
        when(twitterService.contarTweetsNormales()).thenReturn(0);

        // Act
        tweetController.listarFeedPaginado(-1, 0);

        // Assert
        verify(twitterService).listarFeedPaginadoDto(0, 10);
        verify(twitterService).contarTweetsNormales();
    }

    @Test
    void listarTweetsDeUsuario_LimitYOffsetInvalidos_UsaValoresPorDefecto() {
        // Arrange: limit fuera de rango y offset negativo deben corregirse a 15 y 0
        when(twitterService.listarTweetsDeUsuarioConLimitDto(1L, 15, 0)).thenReturn(List.of());
        when(twitterService.contarTweetsDeUsuario(1L)).thenReturn(0);

        // Act
        tweetController.listarTweetsDeUsuario(1L, 500, -10);

        // Assert
        verify(twitterService).listarTweetsDeUsuarioConLimitDto(1L, 15, 0);
    }

    @Test
    void eliminarTweet_ValidId_ReturnsNoContent() {
        // Arrange
        doNothing().when(twitterService).eliminarTweet(1L);

        // Act
        ResponseEntity<?> response = tweetController.eliminarTweet(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(twitterService, times(1)).eliminarTweet(1L);
    }

    @Test
    void listarTweetsDeUsuario_ServiceThrowsException_ReturnsBadRequest() {
        // Arrange
        Long userId = 1L;
        String errorMessage = "Usuario no encontrado";
        when(twitterService.listarTweetsDeUsuarioConLimitDto(userId, 15, 0))
                .thenThrow(new RuntimeException(errorMessage));

        // Act & Assert
        var ex = assertThrows(RuntimeException.class, () -> {
            tweetController.listarTweetsDeUsuario(userId, 15, 0);
        });
        assertEquals(errorMessage, ex.getMessage());
    }
}
