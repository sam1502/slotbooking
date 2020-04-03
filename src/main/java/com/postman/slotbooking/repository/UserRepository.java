package com.postman.slotbooking.repository;

import com.postman.slotbooking.models.PUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<PUsers, Integer> {

    @Query(value = "select * from users where user_name=?1", nativeQuery = true)
    PUsers findByuserName(String userName);
}
