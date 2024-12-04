package com.sportsbet.fanduel.nfl.depth_chart.data.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "Depth_Chart", uniqueConstraints = {@UniqueConstraint(columnNames = {"position"})})
public class DepthChart {
        @Id @GeneratedValue(strategy = GenerationType.AUTO) Integer id;
        @Column(nullable = false) String category;
        @Column(nullable = false) String position;

        @OneToMany(mappedBy = "depthChart", cascade = CascadeType.MERGE)
        Set<PlayerChartPosition> playerChartPositions;

        public DepthChart() {
        }

        public DepthChart(Integer id, String category, String position, Set<PlayerChartPosition> playerChartPositions) {
                this.id = id;
                this.category = category;
                this.position = position;
                this.playerChartPositions = playerChartPositions;
        }

        public Integer getId() {
                return id;
        }

        public void setId(Integer id) {
                this.id = id;
        }

        public String getCategory() {
                return category;
        }

        public void setCategory(String category) {
                this.category = category;
        }

        public String getPosition() {
                return position;
        }

        public void setPosition(String position) {
                this.position = position;
        }

        public Set<PlayerChartPosition> getPlayerChartPositions() {
                return playerChartPositions;
        }

        public void setPlayerChartPositions(Set<PlayerChartPosition> playerChartPositions) {
                this.playerChartPositions = playerChartPositions;
        }
}
