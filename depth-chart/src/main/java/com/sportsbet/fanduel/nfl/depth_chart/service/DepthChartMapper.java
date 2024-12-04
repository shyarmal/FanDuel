package com.sportsbet.fanduel.nfl.depth_chart.service;

import com.sportsbet.fanduel.nfl.depth_chart.controller.dto.NFLPlayer;
import com.sportsbet.fanduel.nfl.depth_chart.controller.dto.PlayerChartDepthResponse;
import com.sportsbet.fanduel.nfl.depth_chart.controller.dto.ResponseWrapper;
import com.sportsbet.fanduel.nfl.depth_chart.data.model.DepthChart;
import com.sportsbet.fanduel.nfl.depth_chart.data.model.Player;
import com.sportsbet.fanduel.nfl.depth_chart.data.model.PlayerChartPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DepthChartMapper {

    BiFunction<NFLPlayer, PlayerChartPosition, ResponseWrapper<PlayerChartDepthResponse>>
            transformToSingleResponseObject = (nflPlayer, playerChartPosition) -> {

        PlayerChartDepthResponse response = buildResponse(nflPlayer, playerChartPosition);
        return new ResponseWrapper<>(response);
    };

    BiFunction<NFLPlayer, List<PlayerChartPosition>, ResponseWrapper<List<PlayerChartDepthResponse>>> transformToListOfResponseObjects =
            ((nflPlayer, playerChartPositions) -> {
                List<PlayerChartDepthResponse> response = playerChartPositions
                        .stream()
                        .map(position -> buildResponse(nflPlayer, position))
                        .toList();

                return new ResponseWrapper<>(response);
            });

    Function<NFLPlayer, Player> transformToPlayer = nflPlayer ->
            new Player(nflPlayer.getNumber(), nflPlayer.getFirstName(), nflPlayer.getLastName(), Set.of());

    Function<List<Player>, ResponseWrapper<List<PlayerChartDepthResponse>>> transformPlayerResponse = players -> {
        List<PlayerChartDepthResponse> response =
                players
                        .stream()
                        .map(player -> buildResponse(player.getNumber(), player.getFirstName(), player.getLastName(), null, null, null))
                        .toList();

        return new ResponseWrapper<>(response);
    };

    Function<PlayerChartPosition, PlayerChartDepthResponse> toPlayerChartDepthResponse =
            playerChartPosition -> buildResponse(
                    playerChartPosition.getPlayer().getNumber(),
                    playerChartPosition.getPlayer().getFirstName(),
                    playerChartPosition.getPlayer().getLastName(),
                    playerChartPosition.getDepthChart().getPosition(),
                    playerChartPosition.getDepth(),
                    playerChartPosition.getDepthChart().getCategory()
            );

    Function<Set<PlayerChartDepthResponse>, ResponseWrapper<Map<String, Map<String, List<PlayerChartDepthResponse>>>>> toFullChartResponse =
            playerChartDepthResponses -> {
                Map<String, Map<String, List<PlayerChartDepthResponse>>> response = playerChartDepthResponses
                        .stream()
                        .collect(Collectors
                                .groupingBy(PlayerChartDepthResponse::category,
                                        Collectors
                                                .groupingBy(PlayerChartDepthResponse::position,
                                                        Collectors.collectingAndThen(
                                                                Collectors.toCollection(ArrayList::new),
                                                                list -> list
                                                                        .stream()
                                                                        .map(c -> buildResponse(c.number(),c.firstName(),c.lastName(),null,c.depth(),null))
                                                                        .toList()))
                                ));

                return new ResponseWrapper<>(response);
            };

    Function<DepthChart, ResponseWrapper<Map<String, Map<String, List<PlayerChartDepthResponse>>>>> transformDepthChartEntryToResponse =
            depthChart -> {
                Map<String, Map<String, List<PlayerChartDepthResponse>>> response = Map.of(
                        depthChart.getCategory(), Map.of(depthChart.getPosition(),
                                depthChart
                                        .getPlayerChartPositions()
                                        .stream()
                                        .map(playerChartPosition ->
                                                buildResponse(
                                                        playerChartPosition.getPlayer().getNumber(),
                                                        playerChartPosition.getPlayer().getFirstName(),
                                                        playerChartPosition.getPlayer().getLastName(),
                                                        null,
                                                        playerChartPosition.getDepth(),
                                                        null
                                                )
                                        ).toList()
                        )
                );

                return new ResponseWrapper<>(response);
            };

    private PlayerChartDepthResponse buildResponse(NFLPlayer nflPlayer, PlayerChartPosition playerChartPosition) {
        return buildResponse(
                nflPlayer.getNumber(),
                nflPlayer.getFirstName(),
                nflPlayer.getLastName(),
                playerChartPosition.getDepthChart().getPosition(),
                playerChartPosition.getDepth(),
                null
        );
    }

    private PlayerChartDepthResponse buildResponse(Integer number, String firstName, String lastName, String position, Integer depth, String category) {
        return new PlayerChartDepthResponse(number, firstName, lastName, position, depth, category);
    }

}
