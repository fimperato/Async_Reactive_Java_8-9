package it.imperato.test.reactor.model;

public class NomeCompleto {
    private String nome;
    private String cognome;
    public NomeCompleto() {
        super();
    }
    public NomeCompleto(String nome, String cognome) {
        super();
        this.nome = nome;
        this.cognome = cognome;
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
}
