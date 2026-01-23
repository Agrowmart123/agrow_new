package com.agrowmart.dto.auth.order;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

//com.agrowmart.dto.auth.order.ScheduleRequestDTO.java

public record ScheduleRequestDTO(
 String orderId,
 @JsonFormat(pattern = "yyyy-MM-dd")
 LocalDate scheduledDate,
 String scheduledSlot
) {}