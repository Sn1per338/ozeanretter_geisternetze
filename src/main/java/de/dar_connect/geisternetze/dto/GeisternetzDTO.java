package de.dar_connect.geisternetze.dto;

public class GeisternetzDTO {
    public double standortLaengengrad;
    public double standortBreitengrad;
    public double groesseInQuadratmeter;
    public String status;
    public String meldedatum;

    public GeisternetzDTO(double laenge, double breite, double groesse, String status, String datum) {
        this.standortLaengengrad = laenge;
        this.standortBreitengrad = breite;
        this.groesseInQuadratmeter = groesse;
        this.status = status;
        this.meldedatum = datum;
    }
}
