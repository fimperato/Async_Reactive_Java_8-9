package it.imperato.test.rx.utils;

import java.util.ArrayList;
import java.util.List;

public class City implements Cloneable, Comparable<City> {
	 
    private String name;
    private String code;
    private long numInhabitants;
    private int area; // km2
    private String saintPatron;

    private List<String> comuniProvincia = new ArrayList<>();

	public City(String name) {
		super();
		this.name = name;
	}

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

	public List<String> getComuniProvincia() { return comuniProvincia; }

	public void setComuniProvincia(List<String> comuniProvincia) { this.comuniProvincia = comuniProvincia; }

	@Override
	public String toString() {
		return "City{" +
				"name='" + name + '\'' +
				", code='" + code + '\'' +
				'}';
	}

	@Override
	public City clone() {
		final City clone;
		try {
			clone = (City) super.clone();
		}
		catch (CloneNotSupportedException ex) {
			throw new RuntimeException("superclass messed up", ex);
		}
		return clone;
	}

	@Override
	public int compareTo(City o) {
		return this.getName().compareTo(o.getName());
	}
}
