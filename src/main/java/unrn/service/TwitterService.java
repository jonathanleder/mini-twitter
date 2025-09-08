
package unrn.service;


import jakarta.persistence.EntityManagerFactory;
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
		// Devuelve el texto del tweet original de un retweet, accediendo dentro de la transacción
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
}
