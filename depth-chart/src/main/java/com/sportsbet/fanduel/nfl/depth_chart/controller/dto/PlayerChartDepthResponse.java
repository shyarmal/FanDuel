package com.sportsbet.fanduel.nfl.depth_chart.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record PlayerChartDepthResponse (
        Integer number,
        String firstName,
        String lastName,
        String position,
        Integer depth,
        String category
) {

}
