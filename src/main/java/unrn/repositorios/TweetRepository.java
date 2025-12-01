package unrn.repositorios;

import unrn.model.Tweet;
import unrn.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface TweetRepository {
    void agregar(Tweet tweet);

    void eliminar(Tweet tweet);

    Optional<Tweet> buscarPorId(Long id);

    List<Tweet> listarPorUsuario(Usuario usuario);

    List<Tweet> findAll();

    List<Tweet> buscarRetweetsDeOrigen(Long tweetOrigenId);
}
