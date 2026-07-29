package unrn.repositorios;

import unrn.model.Tweet;
import java.util.List;
import java.util.Optional;

public interface TweetRepository {
    void agregar(Tweet tweet);

    void eliminar(Tweet tweet);

    Optional<Tweet> buscarPorId(Long id);

    List<Tweet> listarPorAutorId(Long autorId);

    List<Tweet> findAll();

    List<Tweet> buscarRetweetsDeOrigen(Long tweetOrigenId);

    // Tweets normales (sin retweets), ordenados por más reciente primero.
    List<Tweet> listarFeedOrdenado(int skip, int limit);

    long contarFeed();

    List<Tweet> listarPorAutorIdOrdenado(Long autorId, int skip, int limit);

    long contarPorAutorId(Long autorId);

    // Cascada equivalente al orphanRemoval que tenía Usuario.tweets con JPA.
    void eliminarPorAutorId(Long autorId);
}
