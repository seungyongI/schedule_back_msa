package com.example.calendarservice.dto.request;

import com.example.calendarservice.constant.Color;
import com.example.calendarservice.constant.RepeatType;
import com.example.calendarservice.constant.Share;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleRequestUpdateDto {

        @Schema(hidden = true)
        private Long idx;

        @Length(min = 1, max = 50)
        private String title;

        @Length(max = 3000)
        private String content;

        private LocalDateTime start;

        private LocalDateTime end;

        private String location;

        private Color color;

        private Share share;

        private List<String> deletedImageList;

        private List<Long> friendIdxList;

        // 반복 일정 관련 필드 추가
        private RepeatType repeatType;  // 반복 유형: NONE, DAILY, WEEKLY, MONTHLY, YEARLY

        private LocalDate repeatEndDate;  // 반복 종료 일자 (반복이 언제까지 지속되는지)
        
}
