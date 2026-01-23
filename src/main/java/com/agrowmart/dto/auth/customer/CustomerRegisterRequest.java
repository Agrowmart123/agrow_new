package com.agrowmart.dto.auth.customer;


//
//public record CustomerRegisterRequest(
//     String name,
//     String email,
//     String phone,
//     String password
//) {}

public record CustomerRegisterRequest(
	    String fullName,
	    String email,
	    String phone,
	    String password
	) {}