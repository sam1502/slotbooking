package com.postman.slotbooking.repository;

import com.postman.slotbooking.models.PSchedleAvailable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SchedulingRepository extends JpaRepository<PSchedleAvailable, Integer> {

    @Query(value = "select * from available_schedule where user_id=?1", nativeQuery = true)
    List<PSchedleAvailable> availableSchedules(Integer userId);

    @Query(value = "select * from available_schedule where start_time>=?1 and end_time<=?2 and status=true and user_id=?3", nativeQuery = true)
    List<PSchedleAvailable> getAvailableSlotsWithTrueStatus(LocalDateTime startTime, LocalDateTime endTime, Integer userId);

    @Query(value = "select * from available_schedule where start_time>=?1 and end_time<=?2 and user_id=?3", nativeQuery = true)
    List<PSchedleAvailable> getAvailableSlot(LocalDateTime startTime, LocalDateTime endTime, Integer userId);

    @Query(value = "select * from available_schedule where user_id=?1", nativeQuery = true)
    List<PSchedleAvailable> getAllSlotsForUserId(Integer userId);
}
