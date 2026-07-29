package unrn.repositorios;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// Colección auxiliar usada para simular el autoincremento de Long que
// Hibernate hacía automáticamente y que Mongo no provee.
@Document(collection = "database_sequences")
class DatabaseSequence {
    @Id
    private String id;
    private long seq;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }
}
