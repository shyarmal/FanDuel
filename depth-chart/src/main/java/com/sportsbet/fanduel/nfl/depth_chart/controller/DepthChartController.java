package com.sportsbet.fanduel.nfl.depth_chart.controller;

import com.sportsbet.fanduel.nfl.depth_chart.controller.dto.NFLPlayer;
import com.sportsbet.fanduel.nfl.depth_chart.controller.dto.PlayerChartDepthResponse;
import com.sportsbet.fanduel.nfl.depth_chart.controller.dto.ResponseWrapper;
import com.sportsbet.fanduel.nfl.depth_chart.exception.NFLDepthChartException;
import com.sportsbet.fanduel.nfl.depth_chart.service.DepthChartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(name = "NFL Depth Chart")
@Validated
@RequestMapping("/nfl/depth-chart")
@RestController
public class DepthChartController {

    @Autowired
    private DepthChartService depthChartService;

    @Operation(
            description = """
                    Adds a player to the depth chart at a given position.
                    Adding a player without a position_depth would add them to the end of the depth chart at that position.
                    The added player would get priority. Anyone below that player in the depth chart would get moved down a
                    position_depth.
                    """,
            summary = "Adding player to a position with a depth.",
            responses = {
                    @ApiResponse(
                            description = "Created",
                            responseCode = "201"
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            responseCode = "500"
                    )
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{position}/{depth}")
    public ResponseEntity<ResponseWrapper<Map<String, Map<String, List<PlayerChartDepthResponse>>>>> addPlayerToDepthChart (
            @PathVariable("position") String position,
            @RequestBody @Valid NFLPlayer nflPlayer,
            @PathVariable("depth") Optional<Integer> depth
    ) throws NFLDepthChartException {

        ResponseWrapper<Map<String, Map<String, List<PlayerChartDepthResponse>>>> response = depthChartService.addToChart(position, nflPlayer, depth);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            description = """
                    Removes a player from the depth chart for a given position and returns that player.
                    An empty list should be returned if that player is not listed in the depth chart at that position.
                    """,
            summary = "Removes a player from a position",
            responses = {
                    @ApiResponse(
                            description = "No Content",
                            responseCode = "204"
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            responseCode = "500"
                    )
            }
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{position}")
    public ResponseEntity<ResponseWrapper<List<PlayerChartDepthResponse>>> removePlayerFromDepthChart (
            @PathVariable("position") String position,
            @RequestBody @Valid NFLPlayer nflPlayer
    ) throws NFLDepthChartException {

        ResponseWrapper<List<PlayerChartDepthResponse>> response = depthChartService.removeFromChart(position, nflPlayer);
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @Operation(
            description = """
                    For a given player and position, we want to see all players that are “Backups”, those with a lower position_depth.
                    An empty list should be returned if the given player has no Backups.
                    An empty list should be returned if the given player is not listed in the depth chart at that position.
                    """,
            summary = "Gets backups for player",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            responseCode = "500"
                    )
            }
    )
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/backup/{position}")
    public ResponseEntity<ResponseWrapper<List<PlayerChartDepthResponse>>> getBackups (
            @PathVariable("position") String position,
            @RequestBody @Valid NFLPlayer nflPlayer
    ) throws NFLDepthChartException {

        ResponseWrapper<List<PlayerChartDepthResponse>> response = depthChartService.findBackups(position, nflPlayer);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            description = """
                    Print out the full depth chart with every position on the team and every player within the Depth Chart.
                    """,
            summary = "Gets the full depth chart",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            responseCode = "500"
                    )
            }
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ResponseEntity<ResponseWrapper<Map<String, Map<String, List<PlayerChartDepthResponse>>>>> getFullDepthChart() throws NFLDepthChartException {

        ResponseWrapper<Map<String, Map<String, List<PlayerChartDepthResponse>>>> response = depthChartService.getFullDepthChart();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
