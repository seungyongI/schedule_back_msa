package com.example.diaryService.controller;

import com.example.diaryService.dto.StatisticsCategoryDto;
import com.example.diaryService.dto.StatisticsMonthDto;
import com.example.diaryService.dto.StatisticsYearDto;
import com.example.diaryService.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;


    @GetMapping("/category")
    public ResponseEntity<List<StatisticsCategoryDto>> getCategoryStatistics(@RequestParam(name = "userIdx") Long userIdx) {
        List<StatisticsCategoryDto> statisticsList = statisticsService.getCategoryStatisticsByUser(userIdx);
        return ResponseEntity.ok(statisticsList);
    }


    @GetMapping("/month")
    public ResponseEntity<List<StatisticsMonthDto>> getMonthlyStatistics(
            @RequestParam(name = "userIdx") Long userIdx,
            @RequestParam(name = "year") int year,
            @RequestParam(name = "month") int month) {

        List<StatisticsMonthDto> statistics = statisticsService.getMonthStatistics(userIdx, year, month);

        return ResponseEntity.ok(statistics);
    }


    @GetMapping("/year")
    public ResponseEntity<List<StatisticsYearDto>> getYearStatistics(
            @RequestParam(name = "userIdx") Long userIdx,
            @RequestParam(name = "year") int year) {

        List<StatisticsYearDto> statistics = statisticsService.getYearStatistics(userIdx, year);

        return ResponseEntity.ok(statistics);
    }
}