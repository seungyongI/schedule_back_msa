package com.example.calendarservice.entity;

import com.example.calendarservice.constant.Color;
import com.example.calendarservice.constant.RepeatType;
import com.example.calendarservice.constant.Share;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "schedule")
@Getter @Setter
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "s_idx")
    private Long idx;

    @Column(name = "s_title", nullable = false)
    private String title;

    @Column(name = "s_content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "s_start", nullable = false)
    private LocalDateTime start;

    @Column(name = "s_end", nullable = false)
    private LocalDateTime end;

    @Column(name = "s_location")
    private String location;

    @Column(name = "s_color", nullable = false)
    @Enumerated(EnumType.STRING)
    private Color color = Color.ORANGE;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_type", nullable = false)
    private RepeatType repeatType = RepeatType.NONE;

    @Column(name = "repeat_end_date")
    private LocalDate repeatEndDate;

    @Column(name = "repeat_group_id", nullable = false)
    private Long repeatGroupId;

    @ManyToOne
    @JoinColumn(name = "cal_idx", nullable = false)
    private Calendars calendars;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleImage> scheduleImages = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "s_share", nullable = false)
    private Share share = Share.NONE;


    // 날짜 유효성 검사
    @PrePersist
    @PreUpdate
    private void validateDates() {
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End time must be after the start time.");
        }
    }

}
