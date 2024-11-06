package com.hr.patient_record_application.service;

import com.hr.patient_record_application.entity.Patient;

public interface PatientService {

    public void save(Patient patient);
    public String viewPatientData(Long patientId) throws Exception;
}
