package unrn.repositorios;

import jakarta.persistence.EntityManager;
import unrn.model.Usuario;
import java.util.Optional;

class JpaUsuarioRepository implements UsuarioRepository {
    private final EntityManager em;

    public JpaUsuarioRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<Usuario> buscarPorUserName(String userName) {
        var q = em.createQuery("SELECT u FROM Usuario u WHERE u.userName = :userName", Usuario.class);
        q.setParameter("userName", userName);
        return Optional.ofNullable(q.getResultStream().findFirst().orElse(null));
    }

    @Override
    public void agregar(Usuario usuario) {
        em.persist(usuario);
    }

    @Override
    public void eliminar(Usuario usuario) {
        em.remove(usuario);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return Optional.ofNullable(em.find(Usuario.class, id));
    }
}
