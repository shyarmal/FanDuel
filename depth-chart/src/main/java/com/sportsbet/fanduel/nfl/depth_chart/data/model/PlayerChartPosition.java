package com.sportsbet.fanduel.nfl.depth_chart.data.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Player_Chart")
public class PlayerChartPosition {
        @Id @GeneratedValue(strategy = GenerationType.AUTO) Integer id;
        @Column(nullable = false, name = "depth") Integer depth;

        @ManyToOne
        DepthChart depthChart;

        @ManyToOne(cascade = { CascadeType.MERGE }, fetch = FetchType.EAGER)
        Player player;

        public PlayerChartPosition() {
        }

        public PlayerChartPosition(Integer id, Integer depth, DepthChart depthChart, Player player) {
                this.id = id;
                this.depth = depth;
                this.depthChart = depthChart;
                this.player = player;
        }

        public Integer getId() {
                return id;
        }

        public void setId(Integer id) {
                this.id = id;
        }

        public Integer getDepth() {
                return depth;
        }

        public void setDepth(Integer depth) {
                this.depth = depth;
        }

        public DepthChart getDepthChart() {
                return depthChart;
        }

        public void setDepthChart(DepthChart depthChart) {
                this.depthChart = depthChart;
        }

        public Player getPlayer() {
                return player;
        }

        public void setPlayer(Player player) {
                this.player = player;
        }
}
