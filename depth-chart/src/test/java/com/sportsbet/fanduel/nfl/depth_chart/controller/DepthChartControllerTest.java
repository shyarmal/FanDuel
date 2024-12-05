package com.sportsbet.fanduel.nfl.depth_chart.controller;

import com.sportsbet.fanduel.nfl.depth_chart.data.model.DepthChart;
import com.sportsbet.fanduel.nfl.depth_chart.data.model.Player;
import com.sportsbet.fanduel.nfl.depth_chart.data.model.PlayerChartPosition;
import com.sportsbet.fanduel.nfl.depth_chart.data.repository.DepthChartRepository;
import com.sportsbet.fanduel.nfl.depth_chart.data.repository.PlayerChartPositionRepository;
import com.sportsbet.fanduel.nfl.depth_chart.data.repository.PlayerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DepthChartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DepthChartRepository depthChartRepository;

    @Autowired
    PlayerChartPositionRepository playerChartPositionRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @BeforeEach
    public void init() {
        deleteTestData();

        Player player1 = new Player(223, "Mike", "Evans", null);
        Player player2 = new Player(333, "Josh", "Wells", null);
        Player player3 = new Player(234, "Scott", "Miller", null);

        DepthChart depthChart1 = new DepthChart(null, "OFFENSE", "LWR", Set.of());
        DepthChart depthChart2 = new DepthChart(null, "OFFENSE", "RWR", Set.of());
        DepthChart depthChart3 = new DepthChart(null, "DEFENSE", "DE", Set.of());

        PlayerChartPosition playerChartPosition1 = new PlayerChartPosition(null, 1, depthChart3, player1);
        PlayerChartPosition playerChartPosition2 = new PlayerChartPosition(null, 2, depthChart3, player2);
        PlayerChartPosition playerChartPosition3 = new PlayerChartPosition(null, 3, depthChart3, player3);

        playerRepository.saveAll(List.of(player1, player2, player3));
        depthChartRepository.saveAll(List.of(depthChart1, depthChart2, depthChart3));
        playerChartPositionRepository.saveAll(List.of(playerChartPosition1, playerChartPosition2, playerChartPosition3));
    }

    @AfterEach
    public void cleanUp() {
        deleteTestData();
    }

    private void deleteTestData() {
        playerChartPositionRepository.deleteAll();
        depthChartRepository.deleteAll();
        playerRepository.deleteAll();
    }


    @Test
    public void testAddPlayerToDepthChart() throws Exception {
        mockMvc
                .perform(post("/nfl/depth-chart/LWR")
                        .contentType(APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam("depth", "1")
                        .content("{ \"number\" : 234, \"firstName\" : \"Scott\", \"lastName\" : \"Miller\" }"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("{\"data\":{\"OFFENSE\":{\"LWR\":[{\"number\":234,\"firstName\":\"Scott\",\"lastName\":\"Miller\",\"depth\":1}]}}}")));

        mockMvc
                .perform(post("/nfl/depth-chart/RWR")
                        .contentType(APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{ \"number\" : 234, \"firstName\" : \"Scott\", \"lastName\" : \"Miller\" }"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("{\"data\":{\"OFFENSE\":{\"RWR\":[{\"number\":234,\"firstName\":\"Scott\",\"lastName\":\"Miller\",\"depth\":1}]}}}")));
    }

    @Test
    public void testGetFullDepthChart() throws Exception {
        mockMvc
                .perform(get("/nfl/depth-chart"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"data\":{\"DEFENSE\":{\"DE\":[{\"number\":223,\"firstName\":\"Mike\",\"lastName\":\"Evans\",\"depth\":1},{\"number\":234,\"firstName\":\"Scott\",\"lastName\":\"Miller\",\"depth\":3},{\"number\":333,\"firstName\":\"Josh\",\"lastName\":\"Wells\",\"depth\":2}]}}}")));
    }

    @Test
    public void testRemovePlayerFromDepthChart() throws Exception {
        mockMvc
                .perform(delete("/nfl/depth-chart/LWR")
                        .contentType(APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{ \"number\" : 234, \"firstName\" : \"Scott\", \"lastName\" : \"Miller\" }"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string(containsString("{\"data\":[]}")));

        mockMvc
                .perform(delete("/nfl/depth-chart/DE")
                        .contentType(APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{ \"number\" : 234, \"firstName\" : \"Scott\", \"lastName\" : \"Miller\" }"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string(containsString("{\"data\":[{\"number\":234,\"firstName\":\"Scott\",\"lastName\":\"Miller\",\"position\":\"DE\",\"depth\":3}]}")));
    }

    @Test
    public void testGetBackups() throws Exception {
        mockMvc
                .perform(put("/nfl/depth-chart/backup/DE")
                        .contentType(APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{ \"number\" : 223, \"firstName\" : \"Mike\", \"lastName\" : \"Evans\" }"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"data\":[{\"number\":333,\"firstName\":\"Josh\",\"lastName\":\"Wells\"},{\"number\":234,\"firstName\":\"Scott\",\"lastName\":\"Miller\"}]}")));

        mockMvc
                .perform(put("/nfl/depth-chart/backup/DE")
                        .contentType(APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{ \"number\" : 234, \"firstName\" : \"Scott\", \"lastName\" : \"Miller\" }"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"data\":[]}")));

        mockMvc
                .perform(put("/nfl/depth-chart/backup/RWR")
                        .contentType(APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{ \"number\" : 234, \"firstName\" : \"Scott\", \"lastName\" : \"Miller\" }"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"data\":[]}")));
    }

    @Test
    public void testBadRequest() throws Exception {
        mockMvc
                .perform(delete("/nfl/depth-chart/LWR")
                        .contentType(APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{ \"firstName\" : \"Scott\", \"lastName\" : \"Miller\" }"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
