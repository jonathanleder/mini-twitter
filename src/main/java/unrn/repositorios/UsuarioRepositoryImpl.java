package unrn.repositorios;

import org.springframework.stereotype.Repository;
import unrn.model.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
class UsuarioRepositoryImpl implements UsuarioRepository {
    private final MongoUsuarioRepository mongoRepo;
    private final SequenceGeneratorService sequenceGenerator;
    private final TweetRepository tweetRepository;

    UsuarioRepositoryImpl(MongoUsuarioRepository mongoRepo, SequenceGeneratorService sequenceGenerator,
            TweetRepository tweetRepository) {
        this.mongoRepo = mongoRepo;
        this.sequenceGenerator = sequenceGenerator;
        this.tweetRepository = tweetRepository;
    }

    @Override
    public Optional<Usuario> buscarPorUserName(String userName) {
        return mongoRepo.findByUserName(userName).map(this::conTweets);
    }

    @Override
    public void agregar(Usuario usuario) {
        usuario.asignarId(sequenceGenerator.generarSiguienteId("usuarios"));
        mongoRepo.save(usuario);
    }

    @Override
    public void eliminar(Usuario usuario) {
        mongoRepo.deleteById(usuario.getId());
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        // Spring Data lanza IllegalArgumentException si el id es null; lo tratamos
        // como "no encontrado", igual que hacía EntityManager.find con JPA.
        if (id == null) {
            return Optional.empty();
        }
        return mongoRepo.findById(id).map(this::conTweets);
    }

    @Override
    public List<Usuario> listarTodos() {
        return mongoRepo.findAll().stream().map(this::conTweets).toList();
    }

    // Cada usuario debe conocer todos los tweets que hizo (requisito de dominio).
    // Como Mongo no persiste la lista embebida, se reconstruye consultando la
    // colección de tweets por autorId cada vez que se carga un Usuario.
    private Usuario conTweets(Usuario usuario) {
        tweetRepository.listarPorAutorId(usuario.getId()).forEach(usuario::agregarTweet);
        return usuario;
    }
}
