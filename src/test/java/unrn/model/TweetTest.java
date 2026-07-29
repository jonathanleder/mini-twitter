package unrn.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class TweetTest {

    @Test
    @DisplayName("Crear tweet con texto válido crea el tweet correctamente")
    void crearTweet_textoValido_tweetCreado() {
        // Setup
        Usuario usuario = new Usuario("usuarioValido");
        String texto = "Hola mundo";
        // Ejercitación
        Tweet tweet = new Tweet(usuario, texto);
        // Verificación
        assertEquals(usuario, tweet.autor(), "El autor debe ser el usuario pasado por parámetro");
        assertEquals(texto, tweet.texto(), "El texto debe coincidir con el ingresado");
        assertNull(tweet.origen(), "Un tweet normal no debe tener origen");
    }

    @Test
    @DisplayName("Crear tweet con texto vacío lanza excepción")
    void crearTweet_textoVacio_lanzaExcepcion() {
        // Setup
        Usuario usuario = new Usuario("usuarioValido");
        // Ejercitación & Verificación
        var ex = assertThrows(RuntimeException.class, () -> new Tweet(usuario, ""), "Debe lanzar excepción si el texto es vacío");
        assertEquals(Tweet.ERROR_TEXTO, ex.getMessage());
    }

    @Test
    @DisplayName("Crear tweet con texto mayor a 280 caracteres lanza excepción")
    void crearTweet_textoLargo_lanzaExcepcion() {
        // Setup
        Usuario usuario = new Usuario("usuarioValido");
        String texto = "a".repeat(281);
        // Ejercitación & Verificación
        var ex = assertThrows(RuntimeException.class, () -> new Tweet(usuario, texto), "Debe lanzar excepción si el texto es muy largo");
        assertEquals(Tweet.ERROR_TEXTO, ex.getMessage());
    }

    @Test
    @DisplayName("Crear retweet de otro usuario crea el retweet correctamente")
    void crearRetweet_otroUsuario_retweetCreado() {
        // Setup
        Usuario autorOriginal = new Usuario("originalUser");
        Usuario retweeter = new Usuario("retweeter");
        Tweet tweetOriginal = new Tweet(autorOriginal, "Tweet original");
        // Ejercitación
        Tweet retweet = new Tweet(retweeter, tweetOriginal);
        // Verificación
        assertEquals(retweeter, retweet.autor(), "El autor del retweet debe ser el usuario retweeter");
        assertNull(retweet.texto(), "El texto de un retweet debe ser null");
        assertEquals(tweetOriginal, retweet.origen(), "El origen del retweet debe ser el tweet original");
    }

    @Test
    @DisplayName("Crear retweet de un tweet propio lanza excepción")
    void crearRetweet_propio_lanzaExcepcion() {
        // Setup
        Usuario usuario = new Usuario("usuarioValido");
        Tweet tweetOriginal = new Tweet(usuario, "Tweet original");
        // Ejercitación & Verificación
        var ex = assertThrows(RuntimeException.class, () -> new Tweet(usuario, tweetOriginal), "No se puede retweetear un tweet propio");
        assertEquals(Tweet.ERROR_RETWEET_PROPIO, ex.getMessage());
    }
    @Test
    @DisplayName("Crear retweet con origen nulo lanza excepción")
    void crearRetweet_origenNulo_lanzaExcepcion() {
        // Setup
        Usuario usuario = new Usuario("usuarioValido");
        // Ejercitación & Verificación
    var ex = assertThrows(RuntimeException.class, () -> new Tweet(usuario, (Tweet) null), "No se puede crear un retweet con origen nulo");
        assertEquals("El tweet de origen no puede ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("asignarId falla si el tweet ya tiene un id asignado")
    void asignarId_tweetYaTieneId_lanzaExcepcion() {
        // Setup
        Usuario usuario = new Usuario("usuarioValido");
        Tweet tweet = new Tweet(usuario, "Hola mundo");
        tweet.asignarId(1L);
        // Ejercitación & Verificación
        assertThrows(IllegalStateException.class, () -> tweet.asignarId(2L),
                "No se puede reasignar el id de un tweet ya persistido");
    }

    @Test
    @DisplayName("El tweet guarda el id del autor al construirse")
    void getAutorId_devuelveIdDelAutor() {
        // Setup
        Usuario usuario = new Usuario("usuarioValido");
        usuario.asignarId(7L);
        // Ejercitación
        Tweet tweet = new Tweet(usuario, "Hola mundo");
        // Verificación
        assertEquals(7L, tweet.getAutorId(), "El autorId debe coincidir con el id del usuario autor");
    }
}
