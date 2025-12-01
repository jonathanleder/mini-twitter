package unrn.service;

import jakarta.persistence.EntityManagerFactory;
import unrn.DTOs.TweetDto;
import unrn.model.Tweet;
import unrn.model.Usuario;
import unrn.repositorios.RepositoryFactory;
import unrn.repositorios.TweetRepository;
import unrn.repositorios.UsuarioRepository;

import java.util.List;
import java.util.Optional;

public class TwitterService {
    private final EntityManagerFactory emf;

    public TwitterService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public List<Usuario> listarUsuarios() {
        return emf.callInTransaction(em -> {
            UsuarioRepository usuarioRepo = RepositoryFactory.usuarioRepository(em);
            return usuarioRepo.listarTodos();
        });
    }

    // Alta de usuario
    public void crearUsuario(String userName) {
        emf.runInTransaction(em -> {
            UsuarioRepository usuarioRepo = RepositoryFactory.usuarioRepository(em);
            if (usuarioRepo.buscarPorUserName(userName).isPresent()) {
                throw new RuntimeException("Ya existe un usuario con ese userName");
            }
            usuarioRepo.agregar(new Usuario(userName));
        });
    }

    // Baja de usuario (elimina también sus tweets)
    public void eliminarUsuario(Long usuarioId) {
        emf.runInTransaction(em -> {
            UsuarioRepository usuarioRepo = RepositoryFactory.usuarioRepository(em);
            usuarioRepo.buscarPorId(usuarioId).ifPresent(usuarioRepo::eliminar);
        });
    }

    // Alta de tweet
    public void crearTweet(Long usuarioId, String texto) {
        emf.runInTransaction(em -> {
            UsuarioRepository usuarioRepo = RepositoryFactory.usuarioRepository(em);
            TweetRepository tweetRepo = RepositoryFactory.tweetRepository(em);
            Usuario usuario = usuarioRepo.buscarPorId(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Tweet tweet = new Tweet(usuario, texto);
            tweetRepo.agregar(tweet);
            usuario.agregarTweet(tweet);
        });
    }

    // Alta de retweet
    public void crearRetweet(Long usuarioId, Long tweetOrigenId) {
        emf.runInTransaction(em -> {
            UsuarioRepository usuarioRepo = RepositoryFactory.usuarioRepository(em);
            TweetRepository tweetRepo = RepositoryFactory.tweetRepository(em);
            Usuario usuario = usuarioRepo.buscarPorId(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Tweet tweetOrigen = tweetRepo.buscarPorId(tweetOrigenId)
                    .orElseThrow(() -> new RuntimeException("Tweet de origen no encontrado"));
            Tweet retweet = new Tweet(usuario, tweetOrigen);
            tweetRepo.agregar(retweet);
            usuario.agregarTweet(retweet);
        });
    }

    // Listar tweets de un usuario
    public List<Tweet> listarTweetsDeUsuario(Long usuarioId) {
        return emf.callInTransaction(em -> {
            UsuarioRepository usuarioRepo = RepositoryFactory.usuarioRepository(em);
            TweetRepository tweetRepo = RepositoryFactory.tweetRepository(em);
            Usuario usuario = usuarioRepo.buscarPorId(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            return tweetRepo.listarPorUsuario(usuario);
        });
    }

    public List<Tweet> listarTodosLosTweets() {
        return emf.callInTransaction(em -> {
            TweetRepository tweetRepo = RepositoryFactory.tweetRepository(em);
            return tweetRepo.findAll();
        });
    }

    // Devuelve el texto del tweet original de un retweet, accediendo dentro de la
    // transacción
    public String textoDeRetweet(Long retweetId) {
        return emf.callInTransaction(em -> {
            var tweetRepo = unrn.repositorios.RepositoryFactory.tweetRepository(em);
            return tweetRepo.buscarPorId(retweetId)
                    .map(Tweet::textoDeRetweet)
                    .orElse(null);
        });
    }

    // Buscar usuario por username
    public Optional<Usuario> buscarUsuarioPorUserName(String userName) {
        return emf.callInTransaction(em -> {
            UsuarioRepository usuarioRepo = RepositoryFactory.usuarioRepository(em);
            return usuarioRepo.buscarPorUserName(userName);
        });
    }

    // Feed paginado (tweets normales sin retweets)
    public List<Tweet> listarFeedPaginado(int page, int size) {
        return emf.callInTransaction(em -> {
            TweetRepository tweetRepo = RepositoryFactory.tweetRepository(em);
            List<Tweet> allTweets = tweetRepo.findAll();

            // Filtrar tweets normales (sin retweets)
            List<Tweet> normalTweets = allTweets.stream()
                    .filter(t -> t.origen() == null)
                    .sorted((t1, t2) -> t2.getId().compareTo(t1.getId())) // Orden descendente
                    .toList();

            // Aplicar paginación
            int start = page * size;
            int end = Math.min(start + size, normalTweets.size());

            if (start >= normalTweets.size()) {
                return List.of();
            }

            return normalTweets.subList(start, end);
        });
    }

    // Contar tweets normales (sin retweets)
    public int contarTweetsNormales() {
        return emf.callInTransaction(em -> {
            TweetRepository tweetRepo = RepositoryFactory.tweetRepository(em);
            List<Tweet> allTweets = tweetRepo.findAll();
            return (int) allTweets.stream()
                    .filter(t -> t.origen() == null)
                    .count();
        });
    }

    // Tweets de usuario con limit y offset
    public List<Tweet> listarTweetsDeUsuarioConLimit(Long usuarioId, int limit, int offset) {
        return emf.callInTransaction(em -> {
            UsuarioRepository usuarioRepo = RepositoryFactory.usuarioRepository(em);
            Usuario usuario = usuarioRepo.buscarPorId(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<Tweet> tweets = usuario.obtenerTweets().stream()
                    .sorted((t1, t2) -> t2.getId().compareTo(t1.getId())) // Orden descendente
                    .toList();

            int start = offset;
            int end = Math.min(start + limit, tweets.size());

            if (start >= tweets.size()) {
                return List.of();
            }

            return tweets.subList(start, end);
        });
    }

    // Contar tweets de usuario
    public int contarTweetsDeUsuario(Long usuarioId) {
        return emf.callInTransaction(em -> {
            UsuarioRepository usuarioRepo = RepositoryFactory.usuarioRepository(em);
            Usuario usuario = usuarioRepo.buscarPorId(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            return usuario.obtenerTweets().size();
        });
    }

    // Mapear tweet a DTO (dentro de la transacción para evitar lazy loading issues)
    private TweetDto mapTweetToDto(Tweet t) {
        return new TweetDto(
                t.getId(),
                t.texto(),
                t.autor().obtenerUserName(),
                t.getFechaCreacion(),
                t.origen() != null ? t.origen().getId() : null,
                t.origen() != null ? t.origen().texto() : null,
                t.origen() != null ? t.origen().autor().obtenerUserName() : null,
                t.origen() != null ? t.autor().obtenerUserName() : null,
                t.origen() != null);
    }

    // Feed paginado retornando DTOs (con lazy loading resuelto)
    public List<TweetDto> listarFeedPaginadoDto(int page, int size) {
        return emf.callInTransaction(em -> {
            TweetRepository tweetRepo = RepositoryFactory.tweetRepository(em);
            List<Tweet> allTweets = tweetRepo.findAll();

            // Filtrar tweets normales (sin retweets)
            List<Tweet> normalTweets = allTweets.stream()
                    .filter(t -> t.origen() == null)
                    .sorted((t1, t2) -> t2.getId().compareTo(t1.getId())) // Orden descendente
                    .toList();

            // Aplicar paginación
            int start = page * size;
            int end = Math.min(start + size, normalTweets.size());

            if (start >= normalTweets.size()) {
                return List.of();
            }

            // Mapear DENTRO de la transacción para que Hibernate cargue los proxies
            return normalTweets.subList(start, end).stream()
                    .map(this::mapTweetToDto)
                    .toList();
        });
    }

    // Tweets de usuario con DTO
    public List<TweetDto> listarTweetsDeUsuarioConLimitDto(Long usuarioId, int limit, int offset) {
        return emf.callInTransaction(em -> {
            UsuarioRepository usuarioRepo = RepositoryFactory.usuarioRepository(em);
            Usuario usuario = usuarioRepo.buscarPorId(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<Tweet> tweets = usuario.obtenerTweets().stream()
                    .sorted((t1, t2) -> t2.getId().compareTo(t1.getId())) // Orden descendente
                    .toList();

            int start = offset;
            int end = Math.min(start + limit, tweets.size());

            if (start >= tweets.size()) {
                return List.of();
            }

            // Mapear DENTRO de la transacción
            return tweets.subList(start, end).stream()
                    .map(this::mapTweetToDto)
                    .toList();
        });
    }

    // Eliminar tweet (y sus retweets)
    public void eliminarTweet(Long tweetId) {
        emf.runInTransaction(em -> {
            TweetRepository tweetRepo = RepositoryFactory.tweetRepository(em);
            tweetRepo.buscarPorId(tweetId).ifPresent(tweet -> {
                // Primero eliminar todos los retweets de este tweet
                List<Tweet> retweets = tweetRepo.buscarRetweetsDeOrigen(tweetId);
                for (Tweet retweet : retweets) {
                    // Eliminar el retweet del usuario que lo creó
                    retweet.autor().eliminarTweet(retweet);
                    tweetRepo.eliminar(retweet);
                }
                // Luego eliminar el tweet original
                tweet.autor().eliminarTweet(tweet);
                tweetRepo.eliminar(tweet);
            });
        });
    }
}
