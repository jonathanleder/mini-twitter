package unrn.repositorios;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
class SequenceGeneratorService {
    private final MongoTemplate mongoTemplate;

    SequenceGeneratorService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    long generarSiguienteId(String nombreSecuencia) {
        DatabaseSequence contador = mongoTemplate.findAndModify(
                Query.query(where("_id").is(nombreSecuencia)),
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                DatabaseSequence.class);
        return contador != null ? contador.getSeq() : 1L;
    }
}
