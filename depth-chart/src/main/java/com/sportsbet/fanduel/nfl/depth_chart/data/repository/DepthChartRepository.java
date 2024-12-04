package com.sportsbet.fanduel.nfl.depth_chart.data.repository;

import com.sportsbet.fanduel.nfl.depth_chart.data.model.DepthChart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepthChartRepository extends CrudRepository<DepthChart, Long> {

    DepthChart findByPosition(String position);
}
