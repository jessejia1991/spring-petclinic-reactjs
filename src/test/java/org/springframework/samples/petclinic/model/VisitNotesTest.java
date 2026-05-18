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
    void setNotes_storesValue_getNotesReturnsIt() {
        Visit visit = new Visit();

        visit.setNotes("some notes");

        assertThat(visit.getNotes()).isEqualTo("some notes");
    }
}
