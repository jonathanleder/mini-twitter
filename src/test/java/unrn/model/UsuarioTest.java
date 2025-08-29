package unrn.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;


class UsuarioTest {

    @Test
    @DisplayName("Crear usuario con userName válido crea el usuario correctamente")
    void crearUsuario_userNameValido_usuarioCreado() {
        // Setup
        String userName = "usuarioValido";
        // Ejercitación
        Usuario usuario = new Usuario(userName);
        // Verificación
        assertEquals(userName, usuario.obtenerUserName(), "El userName debe coincidir con el ingresado");
    }

    @Test
    @DisplayName("Crear usuario con userName nulo lanza excepción")
    void crearUsuario_userNameNulo_lanzaExcepcion() {
        // Setup & Ejercitación & Verificación
        var ex = assertThrows(RuntimeException.class, () -> new Usuario(null), "Debe lanzar excepción si el userName es nulo");
        assertEquals(Usuario.ERROR_USERNAME_INVALIDO, ex.getMessage());
    }

    @Test
    @DisplayName("Crear usuario con userName menor a 5 caracteres lanza excepción")
    void crearUsuario_userNameCorto_lanzaExcepcion() {
        // Setup & Ejercitación & Verificación
        var ex = assertThrows(RuntimeException.class, () -> new Usuario("abc"), "Debe lanzar excepción si el userName es muy corto");
        assertEquals(Usuario.ERROR_USERNAME_INVALIDO, ex.getMessage());
    }

    @Test
    @DisplayName("Crear usuario con userName mayor a 25 caracteres lanza excepción")
    void crearUsuario_userNameLargo_lanzaExcepcion() {
        // Setup & Ejercitación & Verificación
        String largo = "a".repeat(26);
        var ex = assertThrows(RuntimeException.class, () -> new Usuario(largo), "Debe lanzar excepción si el userName es muy largo");
        assertEquals(Usuario.ERROR_USERNAME_INVALIDO, ex.getMessage());
    }

    @Test
    @DisplayName("Agregar tweet a usuario lo almacena en la lista de tweets")
    void agregarTweet_usuario_tweetAlmacenado() {
        // Setup
        Usuario usuario = new Usuario("usuarioValido");
        Tweet tweet = new Tweet(usuario, "Hola mundo");
        // Ejercitación
        usuario.agregarTweet(tweet);
        // Verificación
        List<Tweet> tweets = usuario.obtenerTweets();
        assertTrue(tweets.contains(tweet), "El tweet debe estar en la lista de tweets del usuario");
    }

    @Test
    @DisplayName("Eliminar tweet de usuario lo elimina de la lista de tweets")
    void eliminarTweet_usuario_tweetEliminado() {
        // Setup
        Usuario usuario = new Usuario("usuarioValido");
        Tweet tweet = new Tweet(usuario, "Hola mundo");
        usuario.agregarTweet(tweet);
        // Ejercitación
        usuario.eliminarTweet(tweet);
        // Verificación
        assertFalse(usuario.obtenerTweets().contains(tweet), "El tweet debe ser eliminado de la lista de tweets del usuario");
    }
    @Test
    @DisplayName("Eliminar todos los tweets del usuario deja la lista vacía")
    void eliminarTodosLosTweets_usuario_listaVacia() {
        // Setup
        Usuario usuario = new Usuario("usuarioValido");
        usuario.agregarTweet(new Tweet(usuario, "tweet1"));
        usuario.agregarTweet(new Tweet(usuario, "tweet2"));
        // Ejercitación
        usuario.eliminarTodosLosTweets();
        // Verificación
        assertTrue(usuario.obtenerTweets().isEmpty(), "La lista de tweets debe quedar vacía tras eliminar todos");
    }
}
