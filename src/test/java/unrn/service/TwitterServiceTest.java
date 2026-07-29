package unrn.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import unrn.main.Main;
import unrn.model.Tweet;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Main.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class TwitterServiceTest {

    @Autowired
    private TwitterService service;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void limpiarBaseDeDatos() {
        mongoTemplate.getDb().drop();
    }

    @Test
    @DisplayName("crearUsuario agrega un usuario correctamente")
    void crearUsuario_agregaUsuario() {
        service.crearUsuario("testuser");
        var usuario = service.buscarUsuarioPorUserName("testuser");
        assertTrue(usuario.isPresent(), "El usuario debería existir en la base de datos");
        assertEquals("testuser", usuario.get().obtenerUserName(), "El username debe coincidir");
    }

    @Test
    @DisplayName("Lista los usuarios")
    void ListarUsuarios() {
        service.crearUsuario("testusuario");
        var usuario = service.buscarUsuarioPorUserName("testusuario");
        assertTrue(usuario.isPresent(), "El usuario debería existir en la base de datos");
        assertEquals("testusuario", usuario.get().obtenerUserName(), "El username debe coincidir");
    }

    @Test
    @DisplayName("crearUsuario lanza excepción si el username ya existe")
    void crearUsuario_usuarioDuplicado() {
        service.crearUsuario("repetido");
        var ex = assertThrows(RuntimeException.class, () -> service.crearUsuario("repetido"));
        assertEquals("Ya existe un usuario con ese userName", ex.getMessage());
    }

    @Test
    @DisplayName("crearTweet agrega un tweet a un usuario")
    void crearTweet_agregaTweet() {
        service.crearUsuario("tweetuser");
        var usuario = service.buscarUsuarioPorUserName("tweetuser").orElseThrow();
        service.crearTweet(usuario.getId(), "Hola mundo!");
        List<Tweet> tweets = service.listarTweetsDeUsuario(usuario.getId());
        assertEquals(1, tweets.size(), "El usuario debe tener un tweet");
        assertEquals("Hola mundo!", tweets.get(0).texto(), "El texto del tweet debe coincidir");
    }

    @Test
    @DisplayName("crearRetweet agrega un retweet correctamente")
    void crearRetweet_agregaRetweet() {
        service.crearUsuario("alicia");
        service.crearUsuario("roberto");
        var alicia = service.buscarUsuarioPorUserName("alicia").orElseThrow();
        var roberto = service.buscarUsuarioPorUserName("roberto").orElseThrow();
        service.crearTweet(alicia.getId(), "Tweet original");
        Tweet tweetOriginal = service.listarTweetsDeUsuario(alicia.getId()).get(0);
        service.crearRetweet(roberto.getId(), tweetOriginal.getId());
        List<Tweet> tweetsRoberto = service.listarTweetsDeUsuario(roberto.getId());
        assertEquals(1, tweetsRoberto.size(), "roberto debe tener un retweet");
        assertEquals(tweetOriginal.texto(), service.textoDeRetweet(tweetsRoberto.get(0).getId()),
                "El texto del retweet debe coincidir con el original");
    }

    @Test
    @DisplayName("Un usuario cargado desde el repositorio conoce todos los tweets que hizo")
    void usuarioCargado_conoceSusTweets() {
        service.crearUsuario("conoce_tweets");
        var usuario = service.buscarUsuarioPorUserName("conoce_tweets").orElseThrow();
        service.crearTweet(usuario.getId(), "Primer tweet");
        service.crearTweet(usuario.getId(), "Segundo tweet");

        var usuarioRecargado = service.buscarUsuarioPorUserName("conoce_tweets").orElseThrow();

        assertEquals(2, usuarioRecargado.obtenerTweets().size(),
                "El usuario recargado desde la base debe conocer todos sus tweets");
    }

    @Test
    @DisplayName("eliminarUsuario elimina usuario y sus tweets")
    void eliminarUsuario_eliminaUsuarioYTweets() {
        service.crearUsuario("eliminarme");
        var usuario = service.buscarUsuarioPorUserName("eliminarme").orElseThrow();
        service.crearTweet(usuario.getId(), "Tweet a borrar");
        service.eliminarUsuario(usuario.getId());
        var usuarioBorrado = service.buscarUsuarioPorUserName("eliminarme");
        assertTrue(usuarioBorrado.isEmpty(), "El usuario debe haber sido eliminado");
    }

    @Test
    @DisplayName("crearTweet lanza excepción si el usuario no existe")
    void crearTweet_usuarioInexistente_lanzaExcepcion() {
        var ex = assertThrows(RuntimeException.class, () -> service.crearTweet(9999L, "texto"));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("crearRetweet lanza excepción si el usuario no existe")
    void crearRetweet_usuarioInexistente_lanzaExcepcion() {
        var ex = assertThrows(RuntimeException.class, () -> service.crearRetweet(9999L, 1L));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("crearRetweet lanza excepción si el tweet de origen no existe")
    void crearRetweet_tweetOrigenInexistente_lanzaExcepcion() {
        service.crearUsuario("sinTweetOrigen");
        var usuario = service.buscarUsuarioPorUserName("sinTweetOrigen").orElseThrow();

        var ex = assertThrows(RuntimeException.class, () -> service.crearRetweet(usuario.getId(), 9999L));
        assertEquals("Tweet de origen no encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("crearRetweet lanza excepción si el tweet de origen es nulo")
    void crearRetweet_tweetOrigenNulo_lanzaExcepcion() {
        service.crearUsuario("sinTweetOrigenNulo");
        var usuario = service.buscarUsuarioPorUserName("sinTweetOrigenNulo").orElseThrow();

        var ex = assertThrows(RuntimeException.class, () -> service.crearRetweet(usuario.getId(), null));
        assertEquals("Tweet de origen no encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("listarTweetsDeUsuario lanza excepción si el usuario no existe")
    void listarTweetsDeUsuario_usuarioInexistente_lanzaExcepcion() {
        var ex = assertThrows(RuntimeException.class, () -> service.listarTweetsDeUsuario(9999L));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("textoDeRetweet devuelve null si el tweet no existe")
    void textoDeRetweet_tweetInexistente_devuelveNull() {
        assertNull(service.textoDeRetweet(9999L));
    }

    @Test
    @DisplayName("listarTodosLosTweets devuelve todos los tweets creados")
    void listarTodosLosTweets_devuelveTodos() {
        service.crearUsuario("todos1");
        var usuario = service.buscarUsuarioPorUserName("todos1").orElseThrow();
        service.crearTweet(usuario.getId(), "uno");
        service.crearTweet(usuario.getId(), "dos");

        assertEquals(2, service.listarTodosLosTweets().size());
    }

    @Test
    @DisplayName("El feed paginado devuelve solo tweets normales, ordenados y paginados")
    void listarFeedPaginadoDto_paginaCorrectamente() {
        service.crearUsuario("feeduser");
        var usuario = service.buscarUsuarioPorUserName("feeduser").orElseThrow();
        for (int i = 0; i < 5; i++) {
            service.crearTweet(usuario.getId(), "tweet " + i);
        }
        Tweet tweetOriginal = service.listarTweetsDeUsuario(usuario.getId()).get(0);
        service.crearUsuario("retweetero");
        var retweetero = service.buscarUsuarioPorUserName("retweetero").orElseThrow();
        service.crearRetweet(retweetero.getId(), tweetOriginal.getId());

        assertEquals(5, service.contarTweetsNormales(), "El retweet no debe contarse en el feed");

        var primeraPagina = service.listarFeedPaginadoDto(0, 3);
        assertEquals(3, primeraPagina.size());
        primeraPagina.forEach(dto -> assertFalse(dto.esRetweet()));

        var segundaPagina = service.listarFeedPaginadoDto(1, 3);
        assertEquals(2, segundaPagina.size());
    }

    @Test
    @DisplayName("Los tweets de usuario respetan limit y offset")
    void listarTweetsDeUsuarioConLimitDto_respetaLimiteYOffset() {
        service.crearUsuario("paginado");
        var usuario = service.buscarUsuarioPorUserName("paginado").orElseThrow();
        for (int i = 0; i < 4; i++) {
            service.crearTweet(usuario.getId(), "tweet " + i);
        }

        assertEquals(4, service.contarTweetsDeUsuario(usuario.getId()));
        assertEquals(2, service.listarTweetsDeUsuarioConLimitDto(usuario.getId(), 2, 0).size());
        assertEquals(2, service.listarTweetsDeUsuarioConLimitDto(usuario.getId(), 2, 2).size());
        assertEquals(0, service.listarTweetsDeUsuarioConLimitDto(usuario.getId(), 2, 4).size());
    }

    @Test
    @DisplayName("eliminarTweet elimina también los retweets que apuntan a él")
    void eliminarTweet_eliminaSusRetweets() {
        service.crearUsuario("original");
        service.crearUsuario("retweeter1");
        var original = service.buscarUsuarioPorUserName("original").orElseThrow();
        var retweeter1 = service.buscarUsuarioPorUserName("retweeter1").orElseThrow();
        service.crearTweet(original.getId(), "Tweet a borrar con retweets");
        Tweet tweetOriginal = service.listarTweetsDeUsuario(original.getId()).get(0);
        service.crearRetweet(retweeter1.getId(), tweetOriginal.getId());

        service.eliminarTweet(tweetOriginal.getId());

        assertTrue(service.listarTweetsDeUsuario(original.getId()).isEmpty(),
                "El tweet original debe haber sido eliminado");
        assertTrue(service.listarTweetsDeUsuario(retweeter1.getId()).isEmpty(),
                "El retweet debe haber sido eliminado en cascada");
    }

    @Test
    @DisplayName("eliminarTweet no falla si el tweet no existe")
    void eliminarTweet_tweetInexistente_noFalla() {
        assertDoesNotThrow(() -> service.eliminarTweet(9999L));
    }

    @Test
    @DisplayName("Las variantes sin DTO de feed y tweets de usuario delegan correctamente")
    void variantesSinDto_delegaCorrectamente() {
        service.crearUsuario("sindto");
        var usuario = service.buscarUsuarioPorUserName("sindto").orElseThrow();
        service.crearTweet(usuario.getId(), "tweet sin dto");

        assertEquals(1, service.listarFeedPaginado(0, 10).size());
        assertEquals(1, service.listarTweetsDeUsuarioConLimit(usuario.getId(), 10, 0).size());
    }
}
