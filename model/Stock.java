package model;

public class Stock extends EnsembleDeCartes {
    public Stock() {
        super();
    }

    public void ajouterCarte(Cartes c) {
        this.listeCartes.add(c);
    }

    public boolean a_gagner() {
        if (this.listeCartes.size() == 3) {
            return true;
        }
        for (int i = 0; i < this.listeCartes.size(); i++) {
            if (this.listeCartes.get(i).getValeur() == 77) {
                return true;
            }
        }
        return false;
    }

    public Cartes premiereCarte() {
        return this.listeCartes.get(0);
    }

    public void supprimerTrio() {
        this.listeCartes.clear();
    }
}
