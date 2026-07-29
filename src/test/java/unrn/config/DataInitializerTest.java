package unrn.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unrn.service.TwitterService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private TwitterService twitterService;

    @Test
    void run_caminoFeliz_creaUsuariosTweetsYRetweets() throws Exception {
        DataInitializer dataInitializer = new DataInitializer(twitterService);

        dataInitializer.run();

        verify(twitterService, times(5)).crearUsuario(anyString());
        verify(twitterService, times(20)).crearTweet(anyLong(), anyString());
        verify(twitterService, times(5)).crearRetweet(anyLong(), anyLong());
    }

    @Test
    void run_usuarioYaExiste_continuaSinFallar() throws Exception {
        doThrow(new RuntimeException("Ya existe un usuario con ese userName"))
                .when(twitterService).crearUsuario("juan_perez");

        DataInitializer dataInitializer = new DataInitializer(twitterService);

        dataInitializer.run();

        verify(twitterService, times(20)).crearTweet(anyLong(), anyString());
    }

    @Test
    void run_errorInesperadoAlCrearUsuario_seCapturaYNoPropaga() {
        doThrow(new RuntimeException("Fallo de conexión"))
                .when(twitterService).crearUsuario("juan_perez");

        DataInitializer dataInitializer = new DataInitializer(twitterService);

        assertDoesNotThrow(() -> dataInitializer.run(),
                "Un error inesperado en la inicialización no debe propagarse fuera de run()");
        verify(twitterService, never()).crearTweet(anyLong(), anyString());
    }

    @Test
    void run_retweetFalla_seLoguéaYContinua() throws Exception {
        doThrow(new RuntimeException("Retweet duplicado"))
                .when(twitterService).crearRetweet(2L, 1L);

        DataInitializer dataInitializer = new DataInitializer(twitterService);

        dataInitializer.run();

        verify(twitterService, times(5)).crearRetweet(anyLong(), anyLong());
    }
}
