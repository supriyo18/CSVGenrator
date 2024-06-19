package com.fetchCSV.Tiger.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tableA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TableA {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String column1;
    private String column2;

}
