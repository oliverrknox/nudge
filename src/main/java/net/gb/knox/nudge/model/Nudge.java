package net.gb.knox.nudge.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Nudge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String userId;

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private LocalDate due;

    @NotNull
    @Size(min = 1)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Trigger> triggers;

    public Nudge(String userId, String title, String description, LocalDate due, List<Trigger> triggers) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.due = due;
        this.triggers = triggers;
    }
}
