package com.timtrix.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orgId;
    @Column(nullable = false)
    @NotEmpty(message = "Name is required")
    private String name;
    private String description;

}
