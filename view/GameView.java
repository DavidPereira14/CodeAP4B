package view;

import model.Cartes;
import model.Game;
import model.Joueur;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class GameView extends JFrame {
    private JPanel panelGrille; // Pour les 9 cartes au centre
    private JPanel panelJoueurs; // Pour les 4 joueurs
    private JLabel labelMessage; // Pour afficher le message
    private JButton[] boutonsGrille; // Les 9 boutons de la grille
    private List<JButton> boutonsActionJoueurs; // Pour "Haut/Bas"

    public GameView() {
        // 1. Configurer la fenêtre (titre, taille, layout)
        // 2. Initialiser les composants (boutons, labels)
        // 3. Organiser le layout (BorderLayout, GridLayout)

        this.setTitle("Trio");
        this.setSize(900, 700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout(10, 10));

        // 1. Zone de message (Haut)
        labelMessage = new JLabel("Bienvenue dans Trio ! Attente du lancement...", SwingConstants.CENTER);
        labelMessage.setFont(new Font("Arial", Font.BOLD, 18));
        this.add(labelMessage, BorderLayout.NORTH);

        // 2. Grille centrale 3x3 (Centre)
        panelGrille = new JPanel(new GridLayout(3, 3, 5, 5));
        boutonsGrille = new JButton[9];
        for (int i = 0; i < 9; i++) {
            boutonsGrille[i] = new JButton("?"); // Dos de la carte à faire
            boutonsGrille[i].setPreferredSize(new Dimension(100, 150));
            panelGrille.add(boutonsGrille[i]);
        }
        this.add(panelGrille, BorderLayout.CENTER);

        // 3. Zone des Joueurs (Droite)
        panelJoueurs = new JPanel();
        panelJoueurs.setLayout(new BoxLayout(panelJoueurs, BoxLayout.Y_AXIS));
        panelJoueurs.setPreferredSize(new Dimension(250, 0));
        panelJoueurs.setBorder(BorderFactory.createTitledBorder("Joueurs"));
        this.add(panelJoueurs, BorderLayout.EAST);

        this.boutonsActionJoueurs = new ArrayList<>();
        this.setVisible(true);
    }

    public void setController(controller.GameController controller) {
        for (int i = 0; i < 9; i++) {
            final int index = i;
            this.boutonsGrille[i].addActionListener(e -> controller.choisirCarteGrille(index));
        }
    }

    public void initialiserZoneJoueurs(List<Joueur> joueurs, controller.GameController controller) {
        panelJoueurs.removeAll();
        for (Joueur J : joueurs) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel n = new JLabel(J.getNom() + "(Trios : 0)");

            JButton btnHaut = new JButton("Haut");
            JButton btnBas = new JButton("Bas");

            btnHaut.addActionListener(e -> controller.choisirCarteJoueur(J.getId(), true));
            btnBas.addActionListener(e -> controller.choisirCarteJoueur(J.getId(), false));

            p.add(n);
            p.add(btnHaut);
            p.add(btnBas);
            panelJoueurs.add(p);
        }
        panelJoueurs.revalidate();
        panelJoueurs.repaint();
    }

}
