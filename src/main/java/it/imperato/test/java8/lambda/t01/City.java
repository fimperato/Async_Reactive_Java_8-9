package it.imperato.test.java8.lambda.t01;

public class City {
	 
    private String name;
    private String code;
    private long numInhabitants;
    private int area; // km2
    private String saintPatron;
    
    public City(String name, String code, long numInhabitants, int area, String saintPatron) {
		super();
		this.name = name;
		this.code = code;
		this.numInhabitants = numInhabitants;
		this.area = area;
		this.saintPatron = saintPatron;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public long getNumInhabitants() {
		return numInhabitants;
	}

	public void setNumInhabitants(long numInhabitants) {
		this.numInhabitants = numInhabitants;
	}

	public int getArea() {
		return area;
	}

	public void setArea(int area) {
		this.area = area;
	}

	public String getSaintPatron() {
		return saintPatron;
	}

	public void setSaintPatron(String saintPatron) {
		this.saintPatron = saintPatron;
	}

	////////////////////////
	
    public City(String nome, String cognome, String sesso, int eta, String nazionalita) {
        this.name = nome;
        this.cognome = cognome;
        this.sesso = sesso;
        this.eta = eta;
        this.nazionalita = nazionalita;
    }
    
    private String cognome;
    private String sesso="";
    private int eta;
    private String nazionalita="";

	public String getNome() {
		return name;
	}

	public void setNome(String nome) {
		this.name = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getSesso() {
		return sesso;
	}

	public void setSesso(String sesso) {
		this.sesso = sesso;
	}

	public int getEta() {
		return eta;
	}

	public void setEta(int eta) {
		this.eta = eta;
	}

	public String getNazionalita() {
		return nazionalita;
	}

	public void setNazionalita(String nazionalita) {
		this.nazionalita = nazionalita;
	}
	
}
