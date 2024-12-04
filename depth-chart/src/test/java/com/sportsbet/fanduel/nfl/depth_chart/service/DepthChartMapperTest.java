package com.sportsbet.fanduel.nfl.depth_chart.service;

import com.sportsbet.fanduel.nfl.depth_chart.controller.dto.NFLPlayer;
import com.sportsbet.fanduel.nfl.depth_chart.controller.dto.PlayerChartDepthResponse;
import com.sportsbet.fanduel.nfl.depth_chart.controller.dto.ResponseWrapper;
import com.sportsbet.fanduel.nfl.depth_chart.data.model.DepthChart;
import com.sportsbet.fanduel.nfl.depth_chart.data.model.Player;
import com.sportsbet.fanduel.nfl.depth_chart.data.model.PlayerChartPosition;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;

public class DepthChartMapperTest {

    private final DepthChartMapper depthChartMapper = new DepthChartMapper();

    @Test
    public void shouldTransformToSingleResponseObjectAsExpected() {
        DepthChart testDepthChart = new DepthChart(1, "OFFENSE", "LWR", Set.of());
        Player testPlayer = new Player(223, "Mike", "Evans", Set.of());

        ResponseWrapper<PlayerChartDepthResponse> result =
                depthChartMapper
                        .transformToSingleResponseObject
                        .apply(
                                new NFLPlayer(223, "Mike", "Evans"),
                                new PlayerChartPosition(2, 1, testDepthChart, testPlayer));

        assertNull(result.getError());
        assertEquals(223, result.getData().number());
        assertEquals("Mike", result.getData().firstName());
        assertEquals("Evans", result.getData().lastName());
        assertEquals(1, result.getData().depth());
        assertEquals("LWR", result.getData().position());
    }

    @Test
    public void shouldTransformToListOfObjectsAsExpected() {
        Player testPlayer = new Player(223, "Mike", "Evans", Set.of());
        DepthChart testDepthChart1 = new DepthChart(1, "OFFENSE", "LWR", Set.of());
        DepthChart testDepthChart2 = new DepthChart(1, "DEFENSE", "DE", Set.of());
        List<PlayerChartPosition> playerChartPositions = List.of(
                new PlayerChartPosition(2, 1, testDepthChart1, testPlayer),
                new PlayerChartPosition(4, 3, testDepthChart2, testPlayer)
        );

        ResponseWrapper<List<PlayerChartDepthResponse>> result =
                depthChartMapper
                        .transformToListOfResponseObjects
                        .apply(
                                new NFLPlayer(223, "Mike", "Evans"),
                                playerChartPositions);

        assertNull(result.getError());
        assertEquals(2, result.getData().size());

        result.getData().forEach(response -> {
            assertEquals(223, response.number());
            assertEquals("Mike", response.firstName());
            assertEquals("Evans", response.lastName());

        });

        assertTrue(
                result.getData()
                        .stream()
                        .anyMatch(response -> response.depth() == 1 && response.position().equals("LWR"))
        );

        assertTrue(
                result.getData()
                        .stream()
                        .anyMatch(response -> response.depth() == 3 && response.position().equals("DE"))
        );

    }

    @Test
    public void shouldReturnEmptyListFromTransformToListOfObjectsIfPlayerNotInTheChart() {
        ResponseWrapper<List<PlayerChartDepthResponse>> result =
                depthChartMapper
                        .transformToListOfResponseObjects
                        .apply(
                                new NFLPlayer(223, "Mike", "Evans"),
                                List.of());

        assertNull(result.getError());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    public void shouldTransformNFLPlayerToPlayer() {
        Player result = depthChartMapper.transformToPlayer.apply(new NFLPlayer(333, "Josh", "Wells"));
        assertEquals(333, result.getNumber());
        assertEquals("Josh", result.getFirstName());
        assertEquals("Wells", result.getLastName());
        assertTrue(result.getPlayerChartPositions().isEmpty());
    }

    @Test
    public void shouldTransformToPlayerResponseAsExpected() {
        ResponseWrapper<List<PlayerChartDepthResponse>> response = depthChartMapper.transformPlayerResponse.apply(
                List.of(
                        new Player(223, "Mike", "Evans", Set.of()),
                        new Player(333, "Josh", "Wells", Set.of())
                )
        );

        assertNull(response.getError());
        assertEquals(2, response.getData().size());

        response.getData().forEach(playerChartDepthResponse -> {
            assertNull(playerChartDepthResponse.position());
            assertNull(playerChartDepthResponse.depth());
            assertNull(playerChartDepthResponse.category());
        });

        assertTrue(
                response.
                        getData()
                        .stream()
                        .anyMatch(playerChartDepthResponse ->
                                playerChartDepthResponse.number() == 223
                                        && playerChartDepthResponse.firstName().equals("Mike")
                                        && playerChartDepthResponse.lastName().equals("Evans"))

        );

        assertTrue(
                response.
                        getData()
                        .stream()
                        .anyMatch(playerChartDepthResponse ->
                                playerChartDepthResponse.number() == 333
                                        && playerChartDepthResponse.firstName().equals("Josh")
                                        && playerChartDepthResponse.lastName().equals("Wells"))

        );

    }

    @Test
    public void shouldReturnPlayerChartDepthResponse() {
        PlayerChartDepthResponse result =
                depthChartMapper.toPlayerChartDepthResponse
                        .apply(new PlayerChartPosition(
                                1, 2,
                                new DepthChart(1, "DEFENSE", "DE", Set.of()),
                                new Player(333, "Josh", "Wells", Set.of())
                        ));

        assertEquals("DEFENSE", result.category());
        assertEquals("DE", result.position());
        assertEquals("Josh", result.firstName());
        assertEquals("Wells", result.lastName());
        assertEquals(333, result.number());
        assertEquals(2, result.depth());
    }

    @Test
    public void shouldTransformDepthChartEntryToResponseAsExpected() {
        DepthChart testDepthChart1 = new DepthChart(1, "OFFENSE", "LWR", Set.of());
        ResponseWrapper<Map<String, Map<String, List<PlayerChartDepthResponse>>>> result =
                depthChartMapper.transformDepthChartEntryToResponse.apply(testDepthChart1);

        assertEquals(1, result.getData().size());

        Map<String, List<PlayerChartDepthResponse>> positionMap = result.getData().get("OFFENSE");
        assertEquals(1, positionMap.size());
        assertTrue(positionMap.get("LWR").isEmpty());

        Player testPlayer = new Player(223, "Mike", "Evans", Set.of());
        Set<PlayerChartPosition> positions = Set.of(new PlayerChartPosition(4, 3, null, testPlayer));
        DepthChart testDepthChart2 = new DepthChart(1, "DEFENSE", "DE", positions);

        result = depthChartMapper.transformDepthChartEntryToResponse.apply(testDepthChart2);

        assertEquals(1, result.getData().size());

        positionMap = result.getData().get("DEFENSE");
        assertEquals(1, positionMap.size());

        List<PlayerChartDepthResponse> playerChartPosition = positionMap.get("DE");
        assertEquals(1, playerChartPosition.size());
        assertEquals(3, playerChartPosition.get(0).depth());
        assertEquals("Mike", playerChartPosition.get(0).firstName());
        assertEquals("Evans", playerChartPosition.get(0).lastName());
    }

    @Test
    public void shouldReturnFullChartResponse() {
        Set<PlayerChartDepthResponse> playerChartDepthResponses = Set.of(
                new PlayerChartDepthResponse(333, "Josh", "Wells", "DE", 1, "DEFENSE"),
                new PlayerChartDepthResponse(223, "Mike", "Evans", "NT", 2, "DEFENSE"),
                new PlayerChartDepthResponse(223, "Mike", "Evans", "RWR", 1, "OFFENSE")
        );

        ResponseWrapper<Map<String, Map<String, List<PlayerChartDepthResponse>>>> result =
                depthChartMapper.toFullChartResponse.apply(playerChartDepthResponses);

        assertNull(result.getError());
        assertEquals(2, result.getData().size());

        assertFullChartResponse
                .accept(
                        result.getData().get("DEFENSE"),
                        Map.of("DE", List.of(333, "Josh", "Wells"), "NT", List.of(223, "Mike", "Evans"))
                );

        assertFullChartResponse
                .accept(
                        result.getData().get("OFFENSE"),
                        Map.of("RWR", List.of(223, "Mike", "Evans"))
                );
    }

    private BiConsumer<Map<String, List<PlayerChartDepthResponse>>, Map<String, List<?>>> assertFullChartResponse = (positionMap, expected) -> {
        positionMap.forEach((key, value) -> {
            List<?> expectedValues = expected.get(key);

            assertTrue(
                    value
                            .stream()
                            .anyMatch(val -> val.number().equals(expectedValues.get(0))
                                    && val.firstName().equals(expectedValues.get(1))
                                    && val.lastName().equals(expectedValues.get(2)))
            );

        });
    };
}
