package com.hr.patient_record_application.controller;

import com.hr.patient_record_application.service.PatientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private PatientServiceImpl patientService;

    @GetMapping("/viewDecrypted/{id}")
    public ResponseEntity<String> testViewDecryptedData(@PathVariable Long id) {
        try {
            String decryptedData = patientService.viewPatientData(id);
            return ResponseEntity.ok("Decrypted data printed to console");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
