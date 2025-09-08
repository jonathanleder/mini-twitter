package unrn.repositorios;

import jakarta.persistence.EntityManager;

public class RepositoryFactory {
    public static UsuarioRepository usuarioRepository(EntityManager em) {
        return new JpaUsuarioRepository(em);
    }
    public static TweetRepository tweetRepository(EntityManager em) {
        return new JpaTweetRepository(em);
    }
}
