package com.sportsbet.fanduel.nfl.depth_chart.data.repository;

import com.sportsbet.fanduel.nfl.depth_chart.data.model.DepthChart;
import com.sportsbet.fanduel.nfl.depth_chart.data.model.Player;
import com.sportsbet.fanduel.nfl.depth_chart.data.model.PlayerChartPosition;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PlayerChartPositionRepository extends CrudRepository<PlayerChartPosition, Long> {

    List<PlayerChartPosition> findByPlayer(Player player);

    List<PlayerChartPosition> findByDepthChart(DepthChart depthChart);

}
