package com.agrowmart.service;

import com.agrowmart.dto.auth.doctor.DoctorProfileCreateDTO;
import com.agrowmart.dto.auth.doctor.DoctorProfileResponseDTO;
import com.agrowmart.entity.DoctorProfile;
import com.agrowmart.entity.User;
import com.agrowmart.exception.ResourceNotFoundException;
import com.agrowmart.repository.DoctorProfileRepository;
import com.agrowmart.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service

@Transactional
public class DoctorService {

    private final DoctorProfileRepository doctorProfileRepository;
    private final UserRepository userRepository;
    
    
    public DoctorService(DoctorProfileRepository doctorProfileRepository, UserRepository userRepository) {

this.doctorProfileRepository = doctorProfileRepository;

this.userRepository = userRepository;





}
    // Create or Update (Upsert)
    public DoctorProfileResponseDTO createOrUpdateProfile(Long userId, DoctorProfileCreateDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        DoctorProfile profile = doctorProfileRepository.findByUserId(userId)
                .orElse(new DoctorProfile());

        // Only set user if it's a new profile
        if (profile.getId() == null) {
            profile.setUser(user);
        }

        profile.setSpecialization(dto.specialization());
        profile.setExperience(dto.experience());
        profile.setClinicName(dto.clinicName());
        profile.setClinicAddress(dto.clinicAddress());
        profile.setClinicOpeningTime(dto.clinicOpeningTime());
        profile.setClinicClosingTime(dto.clinicClosingTime());
        profile.setQualification(dto.qualification());
        profile.setRegistrationNumber(dto.registrationNumber());
        profile.setAbout(dto.about());
        profile.setConsultationFee(dto.consultationFee());

        profile = doctorProfileRepository.save(profile);
        return toResponseDTO(profile);
    }

    public DoctorProfileResponseDTO getProfile(Long userId) {
        DoctorProfile profile = doctorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));
        return toResponseDTO(profile);
    }

    public DoctorProfileResponseDTO getPublicProfile(Long doctorId) {
        DoctorProfile profile = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        return toResponseDTO(profile);
    }

    public List<DoctorProfileResponseDTO> getAllDoctors() {
        return doctorProfileRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public void deleteProfile(Long userId) {
        DoctorProfile profile = doctorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));
        doctorProfileRepository.delete(profile);
    }

    private DoctorProfileResponseDTO toResponseDTO(DoctorProfile p) {
        return new DoctorProfileResponseDTO(
            p.getId(),
            p.getUser().getName(),
            p.getUser().getEmail(),
            p.getUser().getPhone(),
            p.getSpecialization(),
            p.getExperience(),
            p.getClinicName(),
            p.getClinicAddress(),
            p.getClinicOpeningTime(),
            p.getClinicClosingTime(),
            p.getQualification(),
            p.getRegistrationNumber(),
            p.getAbout(),
            p.getConsultationFee()
        );
    }
}