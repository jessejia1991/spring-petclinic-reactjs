package org.springframework.samples.petclinic.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VisitNotesValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void emptyNotes_doesNotTriggerConstraintViolation() {
        Visit visit = new Visit();
        visit.setDescription("routine check"); // required by @NotEmpty
        visit.setNotes(""); // empty string — no constraint on notes

        Set<ConstraintViolation<Visit>> violations = validator.validate(visit);

        // Only expect violations unrelated to notes
        boolean notesViolationPresent = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("notes"));
        assertThat(notesViolationPresent)
                .as("notes field should not produce a ConstraintViolation for empty string")
                .isFalse();
    }

    @Test
    void nullNotes_doesNotTriggerConstraintViolation() {
        Visit visit = new Visit();
        visit.setDescription("routine check"); // required by @NotEmpty
        visit.setNotes(null); // null — no constraint on notes

        Set<ConstraintViolation<Visit>> violations = validator.validate(visit);

        // Only expect violations unrelated to notes
        boolean notesViolationPresent = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("notes"));
        assertThat(notesViolationPresent)
                .as("notes field should not produce a ConstraintViolation for null")
                .isFalse();
    }
}
