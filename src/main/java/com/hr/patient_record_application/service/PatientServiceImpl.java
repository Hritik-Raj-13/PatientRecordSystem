package com.hr.patient_record_application.service;

import com.hr.patient_record_application.entity.Patient;
import com.hr.patient_record_application.repository.PatientRepository;
import com.hr.patient_record_application.utilities.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class PatientServiceImpl implements PatientService{

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private EncryptionService encryptionService;

    @Override
    public void save(Patient patient) {
//        return patientRepository.save(patient);
            try {
                // Generate AES key
                SecretKey aesKey = encryptionService.generateAESKey();

                // Generate IV (Initialization Vector)
                byte[] iv = new byte[12];
                new SecureRandom().nextBytes(iv);  // Random IV for each encryption

                // Encrypt patient data using AES
                String patientData = patient.toString();  // assuming patient has overridden toString()
                byte[] encryptedData = encryptionService.encryptData(patientData, aesKey, iv);

                // Encrypt AES key using RSA
                byte[] encryptedAESKey = encryptionService.encryptAESKeyWithRSA(aesKey);

                // Encode data to store in database
                patient.setEncryptedData(Base64.getEncoder().encodeToString(encryptedData));
                patient.setEncryptedAESKey(Base64.getEncoder().encodeToString(encryptedAESKey));
                patient.setIv(Base64.getEncoder().encodeToString(iv));

                // Save the encrypted patient to the database
                patientRepository.save(patient);
            } catch (Exception e) {
                e.printStackTrace();
            }

    }


    public String viewPatientData(Long patientId) throws Exception {
        Patient patient = patientRepository.findById(patientId).orElseThrow(() -> new RuntimeException("Patient not found"));

        String encryptedData = patient.getEncryptedData();
        String encryptedAESKey = patient.getEncryptedAESKey();
        String iv = patient.getIv();

        // Decrypt data
        String decryptedData = encryptionService.decryptPatientData(encryptedData, encryptedAESKey, iv);

        // Print decrypted data to console
        System.out.println("Decrypted Patient Data: " + decryptedData);

        return decryptedData;
    }

}
