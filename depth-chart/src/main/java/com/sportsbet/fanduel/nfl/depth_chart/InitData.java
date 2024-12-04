package com.sportsbet.fanduel.nfl.depth_chart;

import com.sportsbet.fanduel.nfl.depth_chart.data.model.DepthChart;
import com.sportsbet.fanduel.nfl.depth_chart.data.model.Player;
import com.sportsbet.fanduel.nfl.depth_chart.data.model.PlayerChartPosition;
import com.sportsbet.fanduel.nfl.depth_chart.data.repository.DepthChartRepository;
import com.sportsbet.fanduel.nfl.depth_chart.data.repository.PlayerChartPositionRepository;
import com.sportsbet.fanduel.nfl.depth_chart.data.repository.PlayerRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class InitData {

    @Autowired
    private DepthChartRepository depthChartRepository;

    @Autowired
    PlayerChartPositionRepository playerChartPositionRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @PostConstruct
    public void initDb() {
        Player player1 = new Player(223, "Mike", "Evans", null);
        Player player2 = new Player(333, "Josh", "Wells", null);
        Player player3 = new Player(234, "Scott", "Miller", null);
        Player player4 = new Player(250, "Ali", "Marpet", null);
        Player player5 = new Player(373, "Smith", "Donovan", null);
        Player player6 = new Player(249, "Nick", "Leveratte", null);

        DepthChart depthChart1 = new DepthChart(null, "OFFENSE", "LWR", Set.of());
        DepthChart depthChart2 = new DepthChart(null, "OFFENSE", "RWR", Set.of());
        DepthChart depthChart3 = new DepthChart(null, "OFFENSE", "RG", Set.of());
        DepthChart depthChart4 = new DepthChart(null, "DEFENSE", "DE", Set.of());
        DepthChart depthChart5 = new DepthChart(null, "DEFENSE", "NT", Set.of());
        DepthChart depthChart6 = new DepthChart(null, "DEFENSE", "CB", Set.of());

        PlayerChartPosition playerChartPosition1 = new PlayerChartPosition(null, 1, depthChart3, player1);
        PlayerChartPosition playerChartPosition2 = new PlayerChartPosition(null, 2, depthChart3, player2);
        PlayerChartPosition playerChartPosition3 = new PlayerChartPosition(null, 3, depthChart3, player3);
        PlayerChartPosition playerChartPosition4 = new PlayerChartPosition(null, 1, depthChart4, player2);
        PlayerChartPosition playerChartPosition5 = new PlayerChartPosition(null, 1, depthChart5, player1);
        PlayerChartPosition playerChartPosition6 = new PlayerChartPosition(null, 1, depthChart6, player4);

        playerRepository.saveAll(List.of(player1, player2, player3, player4, player5, player6));
        depthChartRepository.saveAll(List.of(depthChart1, depthChart2, depthChart3, depthChart4, depthChart5, depthChart6));
        playerChartPositionRepository.saveAll(List.of(playerChartPosition1, playerChartPosition2, playerChartPosition3, playerChartPosition4, playerChartPosition5, playerChartPosition6));
    }
}
