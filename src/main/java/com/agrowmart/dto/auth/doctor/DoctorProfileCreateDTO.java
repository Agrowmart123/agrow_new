package com.agrowmart.dto.auth.doctor;



import java.math.BigDecimal;

public record DoctorProfileCreateDTO(
    String specialization,
    Integer experience,
    String clinicName,
    String clinicAddress,
    String clinicOpeningTime,
    String clinicClosingTime,
    String qualification,
    String registrationNumber,
    String about,
    BigDecimal consultationFee
) {}