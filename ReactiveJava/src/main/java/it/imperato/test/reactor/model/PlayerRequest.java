package it.imperato.test.reactor.model;

import java.util.Date;

public class PlayerRequest {

    private String nome;
    private String cognome;
    private Date dataNascita;
    private String squadra;
    private boolean scoring=false;
    private boolean anagrafica=false;
    private Integer numLastPartite; // per calcolare il valore degli indicatori su un certo numero di partite ultime

    private String requestUrl;

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

    public boolean isScoring() {
        return scoring;
    }

    public void setScoring(boolean scoring) {
        this.scoring = scoring;
    }

    public boolean isAnagrafica() {
        return anagrafica;
    }

    public void setAnagrafica(boolean anagrafica) {
        this.anagrafica = anagrafica;
    }

    public Integer getNumLastPartite() {
        return numLastPartite;
    }

    public void setNumLastPartite(Integer numLastPartite) {
        this.numLastPartite = numLastPartite;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }
}
