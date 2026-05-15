package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class VisitNotesTest {

    @Test
    void getNotes_notSet_returnsNull() {
        Visit visit = new Visit();

        assertThat(visit.getNotes()).isNull();
    }
}
