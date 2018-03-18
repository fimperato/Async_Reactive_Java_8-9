package it.imperato.test.reactor.model;

import java.util.Date;

public class Player {

    private String nome;
    private String cognome;
    private Date dataNascita;
    private String squadraAttuale;

    public Player() {
        super();
    }

    public Player(String nome, String cognome, Date dataNascita, String squadraAttuale) {
        super();
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.squadraAttuale = squadraAttuale;
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

    public String getSquadraAttuale() {
        return squadraAttuale;
    }

    public void setSquadraAttuale(String squadraAttuale) {
        this.squadraAttuale = squadraAttuale;
    }
}
