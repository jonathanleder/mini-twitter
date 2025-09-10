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

        // Act
        ResponseEntity<?> response = tweetController.crearTweet(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
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
        when(twitterService.listarTweetsDeUsuario(userId))
            .thenReturn(List.of(tweet1, tweet2));
        when(tweet1.getId()).thenReturn(101L);
        when(tweet1.texto()).thenReturn("First tweet");
        when(tweet1.origen()).thenReturn(null);
        when(tweet2.getId()).thenReturn(102L);
        when(tweet2.texto()).thenReturn("Retweet");
        when(tweet2.origen()).thenReturn(mock(Tweet.class));

        // Act
        ResponseEntity<?> response = tweetController.listarTweetsDeUsuario(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        
        @SuppressWarnings("unchecked")
        List<TweetDto> tweets = (List<TweetDto>) response.getBody();
        assertEquals(2, tweets.size());
        assertEquals(101L, tweets.get(0).id());
        assertEquals("First tweet", tweets.get(0).texto());
        assertNull(tweets.get(0).origenId());
        assertNotNull(tweets.get(1).origenId());
    }

    @Test
    void listarTweetsDeUsuario_ServiceThrowsException_ReturnsBadRequest() {
        // Arrange
        Long userId = 1L;
        String errorMessage = "Usuario no encontrado";
        when(twitterService.listarTweetsDeUsuario(userId))
            .thenThrow(new RuntimeException(errorMessage));

        // Act
        ResponseEntity<?> response = tweetController.listarTweetsDeUsuario(userId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }
}
