package model;

public class Pioche extends EnsembleDeCartes {
    private int idPioche;

    public Pioche() {
        super();
        this.idPioche = 7;
    }

    public Cartes devoilerCarte(int index) {
        if (index >= 0 && index < this.listeCartes.size()) {
            return this.listeCartes.get(index);
        }
        return null;
    }

}
