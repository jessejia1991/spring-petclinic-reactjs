package org.springframework.samples.petclinic.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "pets")
public class Pet extends NamedEntity {

    @Column(name = "birth_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private PetType type;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private Owner owner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pet", fetch = FetchType.EAGER)
    @OrderBy("date ASC")
    private Set<Visit> visits = new HashSet<>();

    @Column(name = "label")
    private String label;

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public PetType getType() {
        return this.type;
    }

    public void setType(PetType type) {
        this.type = type;
    }

    public Owner getOwner() {
        return this.owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public List<Visit> getVisits() {
        List<Visit> sortedVisits = new ArrayList<>(this.visits);
        Collections.sort(sortedVisits, (v1, v2) -> {
            if (v1.getDate() == null) return -1;
            if (v2.getDate() == null) return 1;
            return v1.getDate().compareTo(v2.getDate());
        });
        return Collections.unmodifiableList(sortedVisits);
    }

    public void addVisit(Visit visit) {
        this.visits.add(visit);
        visit.setPet(this);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
