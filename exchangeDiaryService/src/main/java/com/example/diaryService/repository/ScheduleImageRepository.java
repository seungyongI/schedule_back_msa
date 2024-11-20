package com.example.diaryService.repository;

import com.example.diaryService.entity.Schedule;
import com.example.diaryService.entity.ScheduleImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScheduleImageRepository extends JpaRepository<ScheduleImage, Long> {
    List<ScheduleImage> findBySchedule(Schedule schedule);
    void deleteByImgUrl(String imgUrl);

}
