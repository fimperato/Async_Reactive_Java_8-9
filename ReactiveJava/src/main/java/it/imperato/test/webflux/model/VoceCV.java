package it.imperato.test.webflux.model;

public class VoceCV {

    private String id;
    private String sezione;
    private String sottosezione;
    private String chiave;
    private String valore;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSezione() {
        return sezione;
    }

    public void setSezione(String sezione) {
        this.sezione = sezione;
    }

    public String getSottosezione() {
        return sottosezione;
    }

    public void setSottosezione(String sottosezione) {
        this.sottosezione = sottosezione;
    }

    public String getChiave() {
        return chiave;
    }

    public void setChiave(String chiave) {
        this.chiave = chiave;
    }

    public String getValore() {
        return valore;
    }

    public void setValore(String valore) {
        this.valore = valore;
    }
}
