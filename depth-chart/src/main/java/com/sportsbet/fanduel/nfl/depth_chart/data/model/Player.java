package com.sportsbet.fanduel.nfl.depth_chart.data.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "NFL_Player")
public class Player {

        @Id Integer number;
        @Column String firstName;
        @Column String lastName;

        @OneToMany(mappedBy = "player")
        Set<PlayerChartPosition> playerChartPositions;

        public Player() {
        }

        public Player(Integer number, String firstName, String lastName, Set<PlayerChartPosition> playerChartPositions) {
                this.number = number;
                this.firstName = firstName;
                this.lastName = lastName;
                this.playerChartPositions = playerChartPositions;
        }

        public Integer getNumber() {
                return number;
        }

        public void setNumber(Integer number) {
                this.number = number;
        }

        public String getFirstName() {
                return firstName;
        }

        public void setFirstName(String firstName) {
                this.firstName = firstName;
        }

        public String getLastName() {
                return lastName;
        }

        public void setLastName(String lastName) {
                this.lastName = lastName;
        }

        public Set<PlayerChartPosition> getPlayerChartPositions() {
                return playerChartPositions;
        }

        public void setPlayerChartPositions(Set<PlayerChartPosition> playerChartPositions) {
                this.playerChartPositions = playerChartPositions;
        }
}
