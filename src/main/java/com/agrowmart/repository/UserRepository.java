//package com.agrowmart.repository;
//
//
//-------------------------------------
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import com.agrowmart.entity.User;
//
//import java.util.Optional;
//
//public interface UserRepository extends JpaRepository<User, Long> {
//    Optional<User> findByEmail(String email);
//    Optional<User> findByPhone(String phone);
//    boolean existsByEmail(String email);
//    boolean existsByPhone(String phone);
//}

////////////////////////////////////
//package com.agrowmart.repository;
//
//import com.agrowmart.entity.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface UserRepository extends JpaRepository<User, Long> {
//
//    // Basic lookups
//    Optional<User> findByEmail(String email);
//    
//    Optional<User> findByPhone(String phone);
//
//    // For registration - check if email/phone already exists (any user)
//    boolean existsByEmail(String email);
//    
//    boolean existsByPhone(String phone);
//
//    // For profile update - check if email/phone is taken by ANOTHER user
//    boolean existsByEmailAndIdNot(String email, Long id);
//    
//    boolean existsByPhoneAndIdNot(String phone, Long id);
//
//    // Optional: Case-insensitive email search (recommended for real apps)
//    Optional<User> findByEmailIgnoreCase(String email);
//
//    // Optional: Useful for login with email OR phone
//    Optional<User> findByEmailOrPhone(String email, String phone);
//}



package com.agrowmart.repository;

import com.agrowmart.admin_seller_management.enums.AccountStatus;
import com.agrowmart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 1. Find user by email (used in social login & normal login)
    Optional<User> findByEmail(String email);

    // 2. Find user by phone (used in OTP login)
    Optional<User> findByPhone(String phone);

    // 3. Check duplicates during registration
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    // 4. Check duplicates during profile update (exclude current user)
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByPhoneAndIdNot(String phone, Long id);

    // 5. Case-insensitive email search (RECOMMENDED – avoids duplicate emails like Test@gmail.com and test@gmail.com)
    Optional<User> findByEmailIgnoreCase(String email);

    // 6. Login with either email OR phone in one query (very useful)
    Optional<User> findByEmailOrPhone(String email, String phone);

    // 7. Extra: Find by email ignoring case OR phone (best for login)
    Optional<User> findByEmailIgnoreCaseOrPhone(String email, String phone);

	List<User> findByRoleName(String roleName);
	
	//Added by Aakanksha - 19/01/2026
	// ================= ADMIN – SELLER LIST =================

    Page<User> findByRoleIdIn(List<Short> roleIds, Pageable pageable);

    Page<User> findByRoleIdInAndAccountStatus(
            List<Short> roleIds,
            AccountStatus accountStatus,
            Pageable pageable
    );

    @Query("""
        SELECT u FROM User u
        WHERE u.role.id IN :roleIds
          AND (
            LOWER(u.businessName) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
            OR u.phone LIKE CONCAT('%', :search, '%')
          )
    """)
    Page<User> searchVendors(
            @Param("roleIds") List<Short> roleIds,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("""
        SELECT u FROM User u
        WHERE u.role.id IN :roleIds
          AND u.accountStatus = :status
          AND (
            LOWER(u.businessName) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
            OR u.phone LIKE CONCAT('%', :search, '%')
          )
    """)
    Page<User> searchVendorsWithStatus(
            @Param("roleIds") List<Short> roleIds,
            @Param("search") String search,
            @Param("status") AccountStatus status,
            Pageable pageable
    );

    // ================= EXPORT =================
    @Query("""
        SELECT u FROM User u
        WHERE u.role.id IN :roleIds
          AND (:start IS NULL OR u.createdAt BETWEEN :start AND :end)
    """)
    Page<User> findVendorsForExport(
            @Param("roleIds") List<Short> roleIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

}