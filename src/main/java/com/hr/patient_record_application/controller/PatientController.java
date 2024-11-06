package com.hr.patient_record_application.controller;

import com.hr.patient_record_application.entity.Patient;
import com.hr.patient_record_application.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping("/")
    public String showLandingPage() {
        return "index";
    }

    @GetMapping("/create")
    public String showPatientForm(Model model) {
        model.addAttribute("Patient", new Patient());
        return "create_patient";
    }

    @PostMapping("/created")
    public String createPatient(@ModelAttribute Patient patient, Model model){
        patientService.save(patient);
        return "redirect:/patients/success";
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "success";
    }
}
