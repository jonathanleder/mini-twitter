package unrn.repositorios;

import org.springframework.data.mongodb.repository.MongoRepository;
import unrn.model.Tweet;

import java.util.List;

interface MongoTweetRepository extends MongoRepository<Tweet, Long> {
    List<Tweet> findByAutorId(Long autorId);

    List<Tweet> findByOrigenId(Long origenId);

    long countByAutorId(Long autorId);

    long countByOrigenIdIsNull();

    void deleteByAutorId(Long autorId);
}
