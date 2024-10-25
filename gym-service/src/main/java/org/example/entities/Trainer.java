package org.example.entities;

import lombok.Data;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "trainers")
@Data
public class Trainer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "specialization", nullable = false)
    private Long specialization; //TrainingType id
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Trainee> trainees;

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
