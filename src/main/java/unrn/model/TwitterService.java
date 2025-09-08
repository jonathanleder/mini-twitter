package unrn.model;


import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class TwitterService {
	private final EntityManagerFactory emf;

	public TwitterService(EntityManagerFactory emf) {
		this.emf = emf;
	}

	// Alta de usuario
	public void crearUsuario(String userName) {
		emf.runInTransaction(em -> {
			var existe = em.createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.userName = :userName", Long.class)
					.setParameter("userName", userName)
					.getSingleResult();
			if (existe > 0) {
				throw new RuntimeException("Ya existe un usuario con ese userName");
			}
			em.persist(new Usuario(userName));
		});
	}

	// Baja de usuario (elimina también sus tweets)
	public void eliminarUsuario(Long usuarioId) {
		emf.runInTransaction(em -> {
			Usuario usuario = em.find(Usuario.class, usuarioId);
			if (usuario != null) {
				em.remove(usuario);
			}
		});
	}

	// Alta de tweet
	public void crearTweet(Long usuarioId, String texto) {
		emf.runInTransaction(em -> {
			Usuario usuario = em.find(Usuario.class, usuarioId);
			if (usuario == null) {
				throw new RuntimeException("Usuario no encontrado");
			}
			Tweet tweet = new Tweet(usuario, texto);
			em.persist(tweet);
			usuario.agregarTweet(tweet);
			em.merge(usuario);
		});
	}

	// Alta de retweet
	public void crearRetweet(Long usuarioId, Long tweetOrigenId) {
		emf.runInTransaction(em -> {
			Usuario usuario = em.find(Usuario.class, usuarioId);
			Tweet tweetOrigen = em.find(Tweet.class, tweetOrigenId);
			if (usuario == null || tweetOrigen == null) {
				throw new RuntimeException("Usuario o tweet de origen no encontrado");
			}
			Tweet retweet = new Tweet(usuario, tweetOrigen);
			em.persist(retweet);
			usuario.agregarTweet(retweet);
			em.merge(usuario);
		});
	}

	// Listar tweets de un usuario
	public List<Tweet> listarTweetsDeUsuario(Long usuarioId) {
		return emf.callInTransaction(em -> {
			Usuario usuario = em.find(Usuario.class, usuarioId);
			if (usuario == null) {
				throw new RuntimeException("Usuario no encontrado");
			}
			return usuario.obtenerTweets();
		});
	}

	// Buscar usuario por username
	public Optional<Usuario> buscarUsuarioPorUserName(String userName) {
		return emf.callInTransaction(em -> {
			TypedQuery<Usuario> q = em.createQuery("SELECT u FROM Usuario u WHERE u.userName = :userName", Usuario.class);
			q.setParameter("userName", userName);
			try {
				return Optional.of(q.getSingleResult());
			} catch (NoResultException e) {
				return Optional.empty();
			}
		});
	}
}
