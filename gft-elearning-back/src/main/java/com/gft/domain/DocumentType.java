package com.gft.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "document_types")
public @Entity class DocumentType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_document_type")
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    public DocumentType(String name){
        this.name = name;
    }

}
