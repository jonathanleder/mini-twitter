package unrn.repositorios;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import unrn.model.Tweet;

import java.util.List;
import java.util.Optional;

@Repository
class TweetRepositoryImpl implements TweetRepository {
    private final MongoTweetRepository mongoRepo;
    private final MongoTemplate mongoTemplate;
    private final SequenceGeneratorService sequenceGenerator;

    TweetRepositoryImpl(MongoTweetRepository mongoRepo, MongoTemplate mongoTemplate,
            SequenceGeneratorService sequenceGenerator) {
        this.mongoRepo = mongoRepo;
        this.mongoTemplate = mongoTemplate;
        this.sequenceGenerator = sequenceGenerator;
    }

    @Override
    public void agregar(Tweet tweet) {
        tweet.asignarId(sequenceGenerator.generarSiguienteId("tweets"));
        mongoRepo.save(tweet);
    }

    @Override
    public void eliminar(Tweet tweet) {
        mongoRepo.deleteById(tweet.getId());
    }

    @Override
    public Optional<Tweet> buscarPorId(Long id) {
        // Spring Data lanza IllegalArgumentException si el id es null; lo tratamos
        // como "no encontrado", igual que hacía EntityManager.find con JPA.
        if (id == null) {
            return Optional.empty();
        }
        return mongoRepo.findById(id);
    }

    @Override
    public List<Tweet> listarPorAutorId(Long autorId) {
        return mongoRepo.findByAutorId(autorId);
    }

    @Override
    public List<Tweet> findAll() {
        return mongoRepo.findAll();
    }

    @Override
    public List<Tweet> buscarRetweetsDeOrigen(Long tweetOrigenId) {
        return mongoRepo.findByOrigenId(tweetOrigenId);
    }

    @Override
    public List<Tweet> listarFeedOrdenado(int skip, int limit) {
        Query query = Query.query(Criteria.where("origenId").is(null))
                .with(Sort.by(Sort.Direction.DESC, "id"))
                .skip(skip)
                .limit(limit);
        return mongoTemplate.find(query, Tweet.class);
    }

    @Override
    public long contarFeed() {
        return mongoRepo.countByOrigenIdIsNull();
    }

    @Override
    public List<Tweet> listarPorAutorIdOrdenado(Long autorId, int skip, int limit) {
        Query query = Query.query(Criteria.where("autorId").is(autorId))
                .with(Sort.by(Sort.Direction.DESC, "id"))
                .skip(skip)
                .limit(limit);
        return mongoTemplate.find(query, Tweet.class);
    }

    @Override
    public long contarPorAutorId(Long autorId) {
        return mongoRepo.countByAutorId(autorId);
    }

    @Override
    public void eliminarPorAutorId(Long autorId) {
        mongoRepo.deleteByAutorId(autorId);
    }
}
