package unrn.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import unrn.service.TwitterService;

/**
 * Inicializa datos de prueba en la base de datos al levantarse la aplicación.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final TwitterService twitterService;

    public DataInitializer(TwitterService twitterService) {
        this.twitterService = twitterService;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            // Crear usuarios (verificar si ya existen)
            crearUsuarioSiNoExiste("juan_perez");
            crearUsuarioSiNoExiste("maria_garcia");
            crearUsuarioSiNoExiste("carlos_lopez");
            crearUsuarioSiNoExiste("ana_martinez");
            crearUsuarioSiNoExiste("luis_rodriguez");

            // Crear tweets normales
            twitterService.crearTweet(1L, "¡Hola a todos! Este es mi primer tweet en la red social.");
            twitterService.crearTweet(2L, "La programación es una pasión para mí. ¿A ustedes también les gusta?");
            twitterService.crearTweet(3L, "Acabamos de lanzar nuestro nuevo proyecto. ¡Muy emocionante!");
            twitterService.crearTweet(4L, "Bellas tardes para reflexionar sobre la vida y el futuro.");
            twitterService.crearTweet(1L,
                    "Java 21 trae muchas mejoras interesantes. Definitivamente versión recomendada.");
            twitterService.crearTweet(5L, "¿Alguien más sigue el partido de fútbol esta noche?");
            twitterService.crearTweet(2L, "Spring Boot simplifica muchísimo el desarrollo de APIs REST.");
            twitterService.crearTweet(3L, "El trabajo en equipo hace que todo sea posible. 🚀");
            twitterService.crearTweet(4L, "Aprendiendo nuevas tecnologías cada día. Es un viaje emocionante.");
            twitterService.crearTweet(1L, "¿Cuál es su editor de código favorito? Yo soy fan de IntelliJ IDEA.");
            twitterService.crearTweet(5L, "La inteligencia artificial está revolucionando el mundo.");
            twitterService.crearTweet(2L, "Buenos días! Que sea un excelente día de trabajo.");
            twitterService.crearTweet(3L, "Código limpio = Vida limpia. Siempre mantengo los estándares altos.");
            twitterService.crearTweet(4L, "Mirando al atardecer, nada como la naturaleza para relajarse.");
            twitterService.crearTweet(1L, "¿Alguien se anima a un code challenge hoy?");
            twitterService.crearTweet(5L, "La persistencia es la clave del éxito en la programación.");
            twitterService.crearTweet(2L, "Compartiendo mi experiencia con microservicios en mi blog.");
            twitterService.crearTweet(3L, "Feliz viernes a todos! A disfrutar del fin de semana.");
            twitterService.crearTweet(4L, "La música me inspira mientras codifico.");
            twitterService.crearTweet(1L, "JUnit 5 es increíble. Testing made easy!");

            // Crear retweets
            crearRetweetSiNoExiste(2L, 1L);
            crearRetweetSiNoExiste(3L, 5L);
            crearRetweetSiNoExiste(4L, 7L);
            crearRetweetSiNoExiste(5L, 10L);
            crearRetweetSiNoExiste(1L, 11L);

            System.out.println("\n=== BASE DE DATOS INICIALIZADA ===");
            System.out.println("✓ 5 usuarios creados");
            System.out.println("✓ 20 tweets normales creados");
            System.out.println("✓ 5 retweets creados");
            System.out.println("=====================================\n");
        } catch (Exception e) {
            System.err.println("Error crítico al inicializar datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void crearUsuarioSiNoExiste(String userName) {
        try {
            twitterService.crearUsuario(userName);
        } catch (RuntimeException e) {
            if (!e.getMessage().contains("Ya existe un usuario")) {
                throw e; // Re-lanzar si es otro error
            }
            // Si ya existe, simplemente continuar
        }
    }

    private void crearRetweetSiNoExiste(Long usuarioId, Long tweetOrigenId) {
        try {
            twitterService.crearRetweet(usuarioId, tweetOrigenId);
        } catch (RuntimeException e) {
            // Si falla un retweet, simplemente continuar (puede estar duplicado)
            System.err.println("  ⚠ Retweet no creado (" + usuarioId + "→" + tweetOrigenId + "): " + e.getMessage());
        }
    }
}
