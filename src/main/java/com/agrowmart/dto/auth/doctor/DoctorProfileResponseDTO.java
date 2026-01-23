package com.agrowmart.dto.auth.doctor;


import java.math.BigDecimal;

public record DoctorProfileResponseDTO(
    Long doctorId,
    String name,
    String email,
    String phone,
    String specialization,
    Integer experience,
    String clinicName,
    String clinicAddress,
    String openingTime,
    String closingTime,
    String qualification,
    String registrationNumber,
    String about,
    BigDecimal consultationFee
) {}