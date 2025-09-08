package unrn.repositorios;

import jakarta.persistence.EntityManager;
import unrn.model.Tweet;
import unrn.model.Usuario;
import java.util.List;
import java.util.Optional;

class JpaTweetRepository implements TweetRepository {
    private final EntityManager em;

    public JpaTweetRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public void agregar(Tweet tweet) {
        em.persist(tweet);
    }

    @Override
    public void eliminar(Tweet tweet) {
        em.remove(tweet);
    }

    @Override
    public Optional<Tweet> buscarPorId(Long id) {
        return Optional.ofNullable(em.find(Tweet.class, id));
    }

    @Override
    public List<Tweet> listarPorUsuario(Usuario usuario) {
        var q = em.createQuery("SELECT t FROM Tweet t WHERE t.autor = :usuario", Tweet.class);
        q.setParameter("usuario", usuario);
        return q.getResultList();
    }
}
