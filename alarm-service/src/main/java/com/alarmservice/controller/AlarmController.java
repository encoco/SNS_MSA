package com.alarmservice.controller;

import com.common.security.AuthInfoUtil;
import com.alarmservice.dto.AlarmDTO;
import com.alarmservice.service.AlarmService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarms")
public class AlarmController{
    private final AlarmService alarmservice;

    @GetMapping("/getAlarm")
    public ResponseEntity<?> getAlarm() {
        List<AlarmDTO> dto = alarmservice.getAlarmList(AuthInfoUtil.getUserId());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/CheckNewAlarm")
    public ResponseEntity<?> checkAlarm() {
        boolean isRead = alarmservice.getCheckAlarm(AuthInfoUtil.getUserId());
        return ResponseEntity.ok(isRead);
    }

    @PostMapping("/delAllAlarm")
    public ResponseEntity<?> deleteAllAlarm() {
        alarmservice.delAllAlarm(AuthInfoUtil.getUserId());
        return ResponseEntity.ok("삭제완료");
    }
}
