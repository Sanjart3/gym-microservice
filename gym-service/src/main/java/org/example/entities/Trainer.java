package org.example.entities;

import lombok.Data;

import jakarta.persistence.*;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringExclude;

import java.util.List;

@Entity
@Table(name = "trainers")
@Data
public class Trainer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "specialization")
    private Long specialization; //TrainingType id
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "training_type_id")
    @ToStringExclude
    private TrainingType trainingType;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Trainee> trainees;

    @OneToMany(mappedBy = "trainer", cascade = {CascadeType.REMOVE, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Training> trainings;

    public Trainer(Long id, Long specialization, User user) {
        this.id = id;
        this.specialization = specialization;
        this.user = user;
    }

    public Trainer(Long specialization, User user) {
        this.specialization = specialization;
        this.user = user;
    }

    public Trainer() {
    }

    @Override
    public String toString() {
        return "Trainer{" +
                "specialization=" + specialization +
                ", user=" + user +
                '}';
    }
}
