package com.sportsbet.fanduel.nfl.depth_chart.service;

import com.sportsbet.fanduel.nfl.depth_chart.controller.dto.NFLPlayer;
import com.sportsbet.fanduel.nfl.depth_chart.controller.dto.PlayerChartDepthResponse;
import com.sportsbet.fanduel.nfl.depth_chart.controller.dto.ResponseWrapper;
import com.sportsbet.fanduel.nfl.depth_chart.data.model.DepthChart;
import com.sportsbet.fanduel.nfl.depth_chart.data.model.Player;
import com.sportsbet.fanduel.nfl.depth_chart.data.model.PlayerChartPosition;
import com.sportsbet.fanduel.nfl.depth_chart.data.repository.DepthChartRepository;
import com.sportsbet.fanduel.nfl.depth_chart.data.repository.PlayerChartPositionRepository;
import com.sportsbet.fanduel.nfl.depth_chart.exception.NFLDepthChartException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepthChartServiceImplTest {

    @Spy
    private DepthChartMapper depthChartMapper;
    @Mock
    private DepthChartRepository depthChartRepository;
    @Mock
    private PlayerChartPositionRepository playerChartPositionRepository;

    @InjectMocks
    private DepthChartServiceImpl depthChartService;

    @Test
    public void testAddToChart() throws NFLDepthChartException {
        final String position = "DE";
        DepthChart depthChart = new DepthChart(1, "DEFENSE", "DE", Set.of());
        NFLPlayer nflPlayer = new NFLPlayer(223, "Mike", "Evans");
        DepthChart savedDepthChart = new DepthChart(1, "DEFENSE", "DE",
                Set.of(new PlayerChartPosition(1, 1, null, new Player(223, "Mike", "Evans", null))));
        when(depthChartRepository.findByPosition(position)).thenReturn(depthChart);
        when(depthChartRepository.save(any(DepthChart.class))).thenReturn(savedDepthChart);

        ResponseWrapper<Map<String, Map<String, List<PlayerChartDepthResponse>>>> response =
                depthChartService.addToChart(position, nflPlayer, Optional.empty());

        assertNotNull(response);
        verify(depthChartRepository).findByPosition(position);
        verify(depthChartRepository).save(any(DepthChart.class));
    }

    @Test
    public void testRemoveFromChart() throws NFLDepthChartException {
        final String position = "DE";
        NFLPlayer nflPlayer = new NFLPlayer(223, "Mike", "Evans");
        List<PlayerChartPosition> positions = List.of();
        when(playerChartPositionRepository.findByPlayer(any(Player.class))).thenReturn(positions);
        doNothing().when(playerChartPositionRepository).deleteAll(positions);

        ResponseWrapper<List<PlayerChartDepthResponse>> response = depthChartService.removeFromChart(position, nflPlayer);

        assertNotNull(response);
        verify(playerChartPositionRepository).findByPlayer(any(Player.class));
        verify(playerChartPositionRepository).deleteAll(positions);
    }

    @Test
    public void testFindBakcups() throws NFLDepthChartException {
        final String position = "DE";
        NFLPlayer nflPlayer = new NFLPlayer(223, "Mike", "Evans");
        List<PlayerChartPosition> positions = List.of();
        when(playerChartPositionRepository.findByPlayer(any(Player.class))).thenReturn(positions);

        ResponseWrapper<List<PlayerChartDepthResponse>> response = depthChartService.findBackups(position, nflPlayer);

        assertNotNull(response);
        verify(playerChartPositionRepository).findByPlayer(any(Player.class));
    }

    @Test
    public void testGetFullDepthChart() throws NFLDepthChartException {
        Iterable<PlayerChartPosition> iterable = List.of();
        when(playerChartPositionRepository.findAll()).thenReturn(iterable);

        ResponseWrapper<Map<String, Map<String, List<PlayerChartDepthResponse>>>> response = depthChartService.getFullDepthChart();

        assertNotNull(response);
        verify(playerChartPositionRepository).findAll();
    }
}
