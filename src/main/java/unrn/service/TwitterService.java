package unrn.service;

import org.springframework.stereotype.Service;
import unrn.DTOs.TweetDto;
import unrn.model.Tweet;
import unrn.model.Usuario;
import unrn.repositorios.TweetRepository;
import unrn.repositorios.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TwitterService {
    private final TweetRepository tweetRepository;
    private final UsuarioRepository usuarioRepository;

    public TwitterService(TweetRepository tweetRepository, UsuarioRepository usuarioRepository) {
        this.tweetRepository = tweetRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.listarTodos();
    }

    // Alta de usuario
    public void crearUsuario(String userName) {
        if (usuarioRepository.buscarPorUserName(userName).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con ese userName");
        }
        usuarioRepository.agregar(new Usuario(userName));
    }

    // Baja de usuario (elimina también sus tweets, equivalente al orphanRemoval de JPA)
    public void eliminarUsuario(Long usuarioId) {
        usuarioRepository.buscarPorId(usuarioId).ifPresent(usuario -> {
            tweetRepository.eliminarPorAutorId(usuarioId);
            usuarioRepository.eliminar(usuario);
        });
    }

    // Alta de tweet
    public void crearTweet(Long usuarioId, String texto) {
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Tweet tweet = new Tweet(usuario, texto);
        tweetRepository.agregar(tweet);
    }

    // Alta de retweet
    public void crearRetweet(Long usuarioId, Long tweetOrigenId) {
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Tweet tweetOrigen = tweetRepository.buscarPorId(tweetOrigenId)
                .orElseThrow(() -> new RuntimeException("Tweet de origen no encontrado"));
        Tweet retweet = new Tweet(usuario, tweetOrigen);
        tweetRepository.agregar(retweet);
    }

    // Listar tweets de un usuario
    public List<Tweet> listarTweetsDeUsuario(Long usuarioId) {
        usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return tweetRepository.listarPorAutorId(usuarioId);
    }

    public List<Tweet> listarTodosLosTweets() {
        return tweetRepository.findAll();
    }

    // Devuelve el texto del tweet original de un retweet
    public String textoDeRetweet(Long retweetId) {
        return tweetRepository.buscarPorId(retweetId)
                .map(Tweet::textoDeRetweet)
                .orElse(null);
    }

    // Buscar usuario por username
    public Optional<Usuario> buscarUsuarioPorUserName(String userName) {
        return usuarioRepository.buscarPorUserName(userName);
    }

    // Feed paginado (tweets normales sin retweets)
    public List<Tweet> listarFeedPaginado(int page, int size) {
        return tweetRepository.listarFeedOrdenado(page * size, size);
    }

    // Contar tweets normales (sin retweets)
    public int contarTweetsNormales() {
        return (int) tweetRepository.contarFeed();
    }

    // Tweets de usuario con limit y offset
    public List<Tweet> listarTweetsDeUsuarioConLimit(Long usuarioId, int limit, int offset) {
        return tweetRepository.listarPorAutorIdOrdenado(usuarioId, offset, limit);
    }

    // Contar tweets de usuario
    public int contarTweetsDeUsuario(Long usuarioId) {
        return (int) tweetRepository.contarPorAutorId(usuarioId);
    }

    // Mapear tweet a DTO usando los campos denormalizados (disponibles siempre,
    // incluso cuando el Tweet viene de una lectura a Mongo)
    private TweetDto mapTweetToDto(Tweet t) {
        boolean esRetweet = t.getOrigenId() != null;
        return new TweetDto(
                t.getId(),
                t.texto(),
                t.getAutorUsername(),
                t.getFechaCreacion(),
                t.getOrigenId(),
                t.getOrigenFecha(),
                t.getOrigenTexto(),
                t.getOrigenAutorUsername(),
                esRetweet ? t.getAutorUsername() : null,
                esRetweet);
    }

    // Feed paginado retornando DTOs
    public List<TweetDto> listarFeedPaginadoDto(int page, int size) {
        return tweetRepository.listarFeedOrdenado(page * size, size).stream()
                .map(this::mapTweetToDto)
                .toList();
    }

    // Tweets de usuario con DTO
    public List<TweetDto> listarTweetsDeUsuarioConLimitDto(Long usuarioId, int limit, int offset) {
        return tweetRepository.listarPorAutorIdOrdenado(usuarioId, offset, limit).stream()
                .map(this::mapTweetToDto)
                .toList();
    }

    // Eliminar tweet (y sus retweets)
    public void eliminarTweet(Long tweetId) {
        tweetRepository.buscarPorId(tweetId).ifPresent(tweet -> {
            // Primero eliminar todos los retweets de este tweet
            List<Tweet> retweets = tweetRepository.buscarRetweetsDeOrigen(tweetId);
            retweets.forEach(tweetRepository::eliminar);
            // Luego eliminar el tweet original
            tweetRepository.eliminar(tweet);
        });
    }
}
