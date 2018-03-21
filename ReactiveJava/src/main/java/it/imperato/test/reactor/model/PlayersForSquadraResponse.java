package it.imperato.test.reactor.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class PlayersForSquadraResponse {

    private List<NomeCompleto> nomeCompletoList;
    private Date dataNascita;
    private String squadra;
    private BigDecimal averageScore;
    private BigDecimal averageScoreWithBonus;

    public PlayersForSquadraResponse() {
        super();
    }

    public PlayersForSquadraResponse(List<NomeCompleto> nomeCompletoList, Date dataNascita, String squadra, BigDecimal averageScore, BigDecimal averageScoreWithBonus) {
        super();
        this.nomeCompletoList = nomeCompletoList;
        this.dataNascita = dataNascita;
        this.squadra = squadra;
        this.averageScore = averageScore;
        this.averageScoreWithBonus = averageScoreWithBonus;
    }

    public Date getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(Date dataNascita) {
        this.dataNascita = dataNascita;
    }

    public String getSquadra() {
        return squadra;
    }

    public void setSquadra(String squadra) {
        this.squadra = squadra;
    }

    public BigDecimal getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(BigDecimal averageScore) {
        this.averageScore = averageScore;
    }

    public BigDecimal getAverageScoreWithBonus() {
        return averageScoreWithBonus;
    }

    public void setAverageScoreWithBonus(BigDecimal averageScoreWithBonus) {
        this.averageScoreWithBonus = averageScoreWithBonus;
    }

    public List<NomeCompleto> getNomeCompletoList() {
        return nomeCompletoList;
    }

    public void setNomeCompletoList(List<NomeCompleto> nomeCompletoList) {
        this.nomeCompletoList = nomeCompletoList;
    }
}
