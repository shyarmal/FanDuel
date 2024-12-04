package com.sportsbet.fanduel.nfl.depth_chart.data.repository;

import com.sportsbet.fanduel.nfl.depth_chart.data.model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository  extends CrudRepository<Player, Long> {
}
