// Author Vishal Sapkal 
//Date :- 10-12-2025
// this is not work because payment  for google console subscraption - 15 k 

package com.agrowmart.service;

import com.agrowmart.entity.Role;
import com.agrowmart.entity.User;
import com.agrowmart.repository.RoleRepository;
import com.agrowmart.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public CustomOAuth2UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oauth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oauth2User.getAttributes();

        // ------------------ EMAIL (100% SAFE – NO REASSIGNMENT) ------------------
        String rawEmail = (String) attributes.get("email");
        if (rawEmail == null || rawEmail.trim().isEmpty()) {
            throw new OAuth2AuthenticationException("Email not provided by social provider");
        }
        final String email = rawEmail.toLowerCase().trim();   // ← ONE ASSIGNMENT ONLY → effectively final

        // ------------------ NAME (100% SAFE) ------------------
        String name = (String) attributes.getOrDefault("name", "");
        if (name == null || name.trim().isEmpty()) {
            String first = (String) attributes.getOrDefault("given_name", "");
            String last  = (String) attributes.getOrDefault("family_name", "");
            name = (first + " " + last).trim();
        }
        if (name.isEmpty()) name = "User";
        final String finalName = name;   // ← also effectively final

        // ------------------ FIND OR CREATE USER ------------------
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseGet(() -> createNewSocialUser(email, finalName));

        return new CustomOAuth2User(user, attributes);
    }

    private User createNewSocialUser(String email, String name) {
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setPasswordHash("SOCIAL_NO_PASSWORD");
        newUser.setPhoneVerified(true);
        newUser.setProfileCompleted("false");

        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseGet(() -> roleRepository.save(new Role("CUSTOMER")));

        newUser.setRole(customerRole);
        return userRepository.save(newUser);
    }
}