package com.alarmservice.service;

import com.alarmservice.dto.AlarmDTO;
import com.alarmservice.dto.UsersInfoDTO;
import com.alarmservice.entity.AlarmEntity;
import com.alarmservice.repository.AlarmRepository;
import com.alarmservice.client.UserClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final AlarmRepository repository;
    private final UserClient userClient;

    @Transactional
    public List<AlarmDTO> getAlarmList(int userId) {
        List<AlarmEntity> alarms = repository.findByRecipientIdOrderByAlarmIdDesc(userId);

        List<AlarmDTO> result = new ArrayList<>();

        for (AlarmEntity entity : alarms) {
            // 안 읽은 알림이면 읽음 처리
            if (!entity.isRead()) {
                entity.setRead(true);
            }

            UsersInfoDTO user = userClient.getUserInfo(entity.getSenderId());

            AlarmDTO dto = AlarmDTO.fromEntity(entity);
            dto.setNickname(user.getNickname());
            dto.setProfile_img(user.getProfile_img());

            result.add(dto);
        }

        return result;
    }

    public boolean getCheckAlarm(int id) {
        return repository.existsByRecipientIdAndIsReadFalse(id);
    }

    public void delAllAlarm(int id) {
        List<AlarmEntity> entity = repository.findByRecipientId(id);
        repository.deleteAll(entity);
    }


}

