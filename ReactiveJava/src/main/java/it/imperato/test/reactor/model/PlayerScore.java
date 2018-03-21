package it.imperato.test.reactor.model;

import java.math.BigDecimal;
import java.util.Date;

public class PlayerScore {

    private String nome;
    private String cognome;
    private Date dataNascita;
    private String squadra;
    private BigDecimal averageScore;
    private BigDecimal averageScoreWithBonus;

    public PlayerScore() {
        super();
    }

    public PlayerScore(String nome, String cognome, Date dataNascita, String squadra, BigDecimal averageScore, BigDecimal averageScoreWithBonus) {
        super();
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.squadra = squadra;
        this.averageScore = averageScore;
        this.averageScoreWithBonus = averageScoreWithBonus;
    }

    public PlayerScore(String nome, String cognome, String squadra) {
        super();
        this.nome = nome;
        this.cognome = cognome;
        this.squadra = squadra;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
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
}
