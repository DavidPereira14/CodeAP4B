package model;

import java.util.Collections;

public class DeckJoueur extends EnsembleDeCartes {

    public DeckJoueur() {
        super();
    }

    public Cartes recupererCPH() {
        if (listeCartes.isEmpty())
            return null;
        Cartes maxCarte = listeCartes.get(0);
        for (int i = 1; i < listeCartes.size(); i++) {
            if (listeCartes.get(i).getValeur() > maxCarte.getValeur()) {
                maxCarte = listeCartes.get(i);
            }
        }
        return maxCarte;
    }

    public Cartes recupererCPB() {
        if (listeCartes.isEmpty())
            return null;
        Cartes minCarte = listeCartes.get(0);
        for (int i = 1; i < listeCartes.size(); i++) {
            if (listeCartes.get(i).getValeur() < minCarte.getValeur()) {
                minCarte = listeCartes.get(i);
            }
        }
        return minCarte;
    }

    public void trierCartes() {
        Collections.sort(this.listeCartes);
    }

    public void afficherCartes() {
        for (int i = 0; i < this.listeCartes.size(); i++) {
            System.out.println(this.listeCartes.get(i).getNom() + " " + this.listeCartes.get(i).getValeur());
        }
    }
}
