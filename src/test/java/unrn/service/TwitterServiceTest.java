package unrn.service;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import unrn.model.Tweet;
import unrn.util.EmfBuilder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TwitterServiceTest {
    private EntityManagerFactory emf;
    private TwitterService service;

    @BeforeAll
    void setUp() {
        //emf = new EmfBuilder().memory().withDropAndCreateDDL().withTestData().build();
        emf = new EmfBuilder().memory().clientAndServer().withDropAndCreateDDL().build();
        service = new TwitterService(emf);
    }

    @AfterAll
    void tearDown() {
        emf.close();
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
        assertEquals(tweetOriginal.texto(), service.textoDeRetweet(tweetsRoberto.get(0).getId()), "El texto del retweet debe coincidir con el original");
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
}
