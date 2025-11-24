package com.gft.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "months")
public @Entity class Month implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_month")
    private Integer id;

    @NotNull
    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    public Month(String name) {
        this.name = name;
    }
}
