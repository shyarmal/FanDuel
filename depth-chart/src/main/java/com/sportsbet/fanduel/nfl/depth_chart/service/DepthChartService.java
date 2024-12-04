package com.sportsbet.fanduel.nfl.depth_chart.service;

import com.sportsbet.fanduel.nfl.depth_chart.controller.dto.NFLPlayer;
import com.sportsbet.fanduel.nfl.depth_chart.controller.dto.PlayerChartDepthResponse;
import com.sportsbet.fanduel.nfl.depth_chart.controller.dto.ResponseWrapper;
import com.sportsbet.fanduel.nfl.depth_chart.exception.NFLDepthChartException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DepthChartService {

    ResponseWrapper<Map<String, Map<String, List<PlayerChartDepthResponse>>>> addToChart(String position, NFLPlayer NFLPlayer, Optional<Integer> depth) throws NFLDepthChartException;

    ResponseWrapper<List<PlayerChartDepthResponse>> removeFromChart(String position, NFLPlayer NFLPlayer) throws NFLDepthChartException;

    ResponseWrapper<List<PlayerChartDepthResponse>> findBackups(String position, NFLPlayer nflPlayer) throws NFLDepthChartException;

    ResponseWrapper<Map<String, Map<String, List<PlayerChartDepthResponse>>>> getFullDepthChart() throws NFLDepthChartException;
}
