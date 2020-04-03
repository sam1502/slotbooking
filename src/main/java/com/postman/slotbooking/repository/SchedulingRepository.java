package com.postman.slotbooking.repository;

import com.postman.slotbooking.models.PSchedleAvailable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SchedulingRepository extends JpaRepository<PSchedleAvailable, Integer> {

    @Query(value = "select * from available_schedule where user_id=?1", nativeQuery = true)
    List<PSchedleAvailable> availableSchedules(Integer userId);
}
