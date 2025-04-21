package com.alarmservice.repository;


import com.alarmservice.entity.AlarmEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface AlarmRepository extends JpaRepository<AlarmEntity, Integer> {
    List<AlarmEntity> findByRecipientIdOrderByAlarmIdDesc(int userId);

    boolean existsByRecipientIdAndIsReadFalse(int id);

    List<AlarmEntity> findByRecipientId(int id);

    boolean existsBySenderIdAndRecipientIdAndContent(int senderId, int receiverId, String content);
}
