package com.fetchCSV.Tiger.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customTable")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String columnA1;
    private String columnB1;
    private String columnC1;


}
