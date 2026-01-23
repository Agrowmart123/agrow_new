package com.agrowmart.dto.auth.customer;

public record CustomerLoginRequest(
	    String login, // email or phone
	    String password
	) {}