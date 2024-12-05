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
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DepthChartServiceImpl implements DepthChartService {

    private static final Logger LOG = LoggerFactory.getLogger(DepthChartServiceImpl.class);
    private final DepthChartMapper depthChartMapper;
    private final DepthChartRepository depthChartRepository;
    private final PlayerChartPositionRepository playerChartPositionRepository;

    public DepthChartServiceImpl(
            DepthChartMapper depthChartMapper,
            DepthChartRepository depthChartRepository,
            PlayerChartPositionRepository playerChartPositionRepository
    ) {
        this.depthChartMapper = depthChartMapper;
        this.depthChartRepository = depthChartRepository;
        this.playerChartPositionRepository = playerChartPositionRepository;
    }

    @Override
    @Transactional
    public ResponseWrapper<Map<String, Map<String, List<PlayerChartDepthResponse>>>> addToChart(
            String position,
            @Valid NFLPlayer nflPlayer,
            Optional<Integer> positionDepth
    ) throws NFLDepthChartException {

        try {
            Player player = new Player(nflPlayer.getNumber(), nflPlayer.getFirstName(), nflPlayer.getLastName(), null);
            DepthChart depthChart = depthChartRepository.findByPosition(position);
            Set<PlayerChartPosition> playerChartPositions;
            PlayerChartPosition toAdd;
            DepthChart modifiedDepthChart;

            if (positionDepth.isEmpty()) {
                Integer deepestPlayerPosition = depthChart
                        .getPlayerChartPositions()
                        .stream()
                        .map(PlayerChartPosition::getDepth)
                        .max(Comparator.comparing(Integer::intValue))
                        .orElse(0);

                toAdd = new PlayerChartPosition(null, deepestPlayerPosition + 1, depthChart, player);
                playerChartPositions = Set.of(toAdd);

            } else {
                Integer depth = positionDepth.get();
                playerChartPositions = depthChart
                        .getPlayerChartPositions()
                        .stream()
                        .map(playerChartPosition -> new PlayerChartPosition(playerChartPosition.getId(),
                                (playerChartPosition.getDepth() >= depth) ?
                                        playerChartPosition.getDepth() + 1 : playerChartPosition.getDepth(),
                                playerChartPosition.getDepthChart(),
                                playerChartPosition.getPlayer()))
                        .collect(Collectors.toSet());

                toAdd = new PlayerChartPosition(null, depth, depthChart, player);
                playerChartPositions.add(toAdd);
            }

            modifiedDepthChart = new DepthChart(depthChart.getId(), depthChart.getCategory(), depthChart.getPosition(), playerChartPositions);
            DepthChart saved = depthChartRepository.save(modifiedDepthChart);

            return depthChartMapper.transformDepthChartEntryToResponse.apply(saved);

        } catch (Exception e) {
            LOG.error("Couldn't update the depth chart. Please try again later.", e);
            throw new NFLDepthChartException("Depth chart couldn't be updated. Please try again later.");
        }
    }

    @Override
    @Transactional
    public ResponseWrapper<List<PlayerChartDepthResponse>> removeFromChart(
            String position,
            NFLPlayer nflPlayer
    ) throws NFLDepthChartException {

        try {
            List<PlayerChartPosition> mappingsForPosition = getPlayerChartPositions(position, nflPlayer);

            playerChartPositionRepository.deleteAll(mappingsForPosition);
            return depthChartMapper.transformToListOfResponseObjects.apply(nflPlayer, mappingsForPosition);

        } catch (Exception e) {
            LOG.error("Couldn't delete player from depth chart. Please try again later.", e);
            throw new NFLDepthChartException("Depth chart couldn't be updated. Please try again later.");
        }
    }

    @Override
    @Transactional
    public ResponseWrapper<List<PlayerChartDepthResponse>> findBackups(String position, NFLPlayer nflPlayer) throws NFLDepthChartException {

        try {
            List<PlayerChartPosition> mappingsForPosition = getPlayerChartPositions(position, nflPlayer);
            PlayerChartPosition playerChartPosition =
                    mappingsForPosition
                            .stream()
                            .findFirst()
                            .orElse(null);

            return switch (playerChartPosition) {
                case PlayerChartPosition pcp ->  depthChartMapper.transformPlayerResponse.apply(findBackupPlayers(pcp));
                case null -> depthChartMapper.transformPlayerResponse.apply(List.of());
            };
        } catch (Exception e) {
            LOG.error("Couldn't retrieve players. Please try again later.", e);
            throw new NFLDepthChartException("Couldn't retrieve players. Please try again later.");
        }

    }

    @Override
    public ResponseWrapper<Map<String, Map<String, List<PlayerChartDepthResponse>>>> getFullDepthChart() throws NFLDepthChartException {
        try {
            Iterable<PlayerChartPosition> results = playerChartPositionRepository.findAll();

            HashSet<PlayerChartDepthResponse> convertedResults = new HashSet<>();
            results.forEach(playerChartPosition ->
                    convertedResults.add(depthChartMapper.toPlayerChartDepthResponse.apply(playerChartPosition)));

            return depthChartMapper.toFullChartResponse.apply(convertedResults);

        } catch (Exception e) {
            LOG.error("Couldn't load the depth chart. Please try again later.", e);
            throw new NFLDepthChartException("Depth chart couldn't be loaded. Please try again later.");
        }
    }

    private List<PlayerChartPosition> getPlayerChartPositions(String position, NFLPlayer nflPlayer) {
        List<PlayerChartPosition> depthChartMappings =
                playerChartPositionRepository.findByPlayer(depthChartMapper.transformToPlayer.apply(nflPlayer));

        List<PlayerChartPosition> mappingsForPosition =
                depthChartMappings
                        .stream()
                        .filter(playerChartPosition -> position.equals(playerChartPosition.getDepthChart().getPosition()))
                        .toList();
        return mappingsForPosition;
    }

    private List<Player> findBackupPlayers(PlayerChartPosition pcp) {
        Integer depth = pcp.getDepth();
        DepthChart depthChart = pcp.getDepthChart();
        List<PlayerChartPosition> mappingsForDepthChart = playerChartPositionRepository.findByDepthChart(depthChart);

        return mappingsForDepthChart
                .stream()
                .filter(entry -> entry.getDepth() > depth)
                .map(PlayerChartPosition::getPlayer)
                .toList();
    }

}
