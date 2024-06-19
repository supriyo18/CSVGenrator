package com.fetchCSV.Tiger.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tableC")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TableC {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String column1;
    private String column2;

}
