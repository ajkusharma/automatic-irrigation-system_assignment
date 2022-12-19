package com.example.automaticirrigationsystem.model;

import lombok.*;

import javax.persistence.*;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "Plot")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Plot {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    public String startTime;
    public String endTime;
    private int amount_water;
}
