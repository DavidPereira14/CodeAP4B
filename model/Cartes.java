package model;

public class Cartes implements Comparable<Cartes> {
    private int valeur;
    private String nom;
    private String image;

    public Cartes(int valeur, String nom, String image) {
        this.valeur = valeur;
        this.nom = nom;
        this.image = image;
    }

    public int getValeur() {
        return this.valeur;
    }

    public String getNom() {
        return this.nom;
    }

    public String getImage() {
        return this.image;
    }

    @Override
    public int compareTo(Cartes o) {
        return Integer.compare(this.valeur, o.valeur);
    }

    public void afficherCarte() {
        System.out.println(this.nom + " " + this.valeur);
    }
}
