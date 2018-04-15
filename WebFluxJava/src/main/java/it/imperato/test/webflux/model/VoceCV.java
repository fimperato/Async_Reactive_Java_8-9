package it.imperato.test.webflux.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "vociCV")
public class VoceCV {

    @Id
    private String id;

    private String sezione;
    private String sottosezione;
    private String chiave;
    private String valore;

    public VoceCV() {
        super();
    }

    public VoceCV(String sezione, String chiave, String valore) {
        super();
        this.sezione = sezione;
        this.chiave = chiave;
        this.valore = valore;
    }

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

    @Override
    public String toString() {
        return "VoceCV{" +
                "id='" + id + '\'' +
                ", sezione='" + sezione + '\'' +
                ", sottosezione='" + sottosezione + '\'' +
                ", chiave='" + chiave + '\'' +
                ", valore='" + valore + '\'' +
                '}';
    }
}
