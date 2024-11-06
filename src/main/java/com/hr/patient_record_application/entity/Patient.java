package com.hr.patient_record_application.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long userId;
    private String name;
    private String gender;
    private String medicalHistory;
    private String diagnosis;
    private int age;
    @Column(length = 512)
    private String encryptedData;
    @Column(length = 512)
    private String encryptedAESKey;
    private String iv;
}
