package unrn.repositorios;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseSequenceTest {

    @Test
    void getterYSetter_funcionanCorrectamente() {
        DatabaseSequence sequence = new DatabaseSequence();

        sequence.setId("tweets");
        sequence.setSeq(42L);

        assertEquals("tweets", sequence.getId());
        assertEquals(42L, sequence.getSeq());
    }
}
