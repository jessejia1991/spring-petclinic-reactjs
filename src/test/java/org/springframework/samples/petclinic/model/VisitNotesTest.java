package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class VisitNotesTest {

    @Test
    void getNotes_notSet_returnsNull() {
        Visit visit = new Visit();

        assertThat(visit.getNotes()).isNull();
    }

    @Test
    void setNotes_doesNotAffectDescriptionField() {
        Visit visit = new Visit();
        visit.setDescription("annual checkup");
        visit.setNotes("patient is healthy");

        assertThat(visit.getDescription()).isEqualTo("annual checkup");
        assertThat(visit.getNotes()).isEqualTo("patient is healthy");
    }
}
