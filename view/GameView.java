package view;

import model.Cartes;
import model.Joueur;
import controller.GameController;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class GameView extends JFrame {
    private JPanel panelCentre;
    private JPanel panelGrille;
    private JPanel panelNord, panelSud, panelEst, panelOuest;
    private JPanel panelMainJoueurActif;
    private JPanel containerJoueursNord;
    
    // Nouveaux composants pour la manche
    private JPanel panelCartesChoisies;
    private JLabel[] labelsCartesChoisies;

    private JLabel labelMessage;
    private JButton[] boutonsGrille;
    private String CHEMIN_DOS = "carte/back.png";
    private GameController controller;

    // Constantes de taille harmonisées
    private final int CARTE_W = 70;
    private final int CARTE_H = 100;

    public GameView() {
        this.setTitle("Trio - Table de Jeu");
        this.setSize(1200, 800);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        // 1. Zone Centrale
        panelCentre = new JPanel(new BorderLayout());
        labelMessage = new JLabel("Bienvenue dans Trio !", SwingConstants.CENTER);
        labelMessage.setFont(new Font("Arial", Font.BOLD, 18));
        panelCentre.add(labelMessage, BorderLayout.NORTH);

        initGrille(); // Grille 3x3 centrée
        this.add(panelCentre, BorderLayout.CENTER);

        // 2. Zones Joueurs et Manche
        panelNord = new JPanel(new BorderLayout());
        panelSud = new JPanel(new BorderLayout());
        panelEst = new JPanel(new GridBagLayout());
        panelOuest = new JPanel(new GridBagLayout());

        // Initialisation du bloc en haut à droite
        initPanelCartesChoisies();
        panelNord.add(panelCartesChoisies, BorderLayout.EAST);

        // Conteneur joueurs haut-gauche
        containerJoueursNord = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNord.add(containerJoueursNord, BorderLayout.WEST);

        // Panneau main joueur (Sud)
        panelMainJoueurActif = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelMainJoueurActif.setBorder(BorderFactory.createTitledBorder("Vos Cartes"));
        panelSud.add(panelMainJoueurActif, BorderLayout.SOUTH);

        this.add(panelNord, BorderLayout.NORTH);
        this.add(panelSud, BorderLayout.SOUTH);
        this.add(panelEst, BorderLayout.EAST);
        this.add(panelOuest, BorderLayout.WEST);

        this.setVisible(true);
    }

    // --- NOUVELLE FONCTION : Gestion des 3 rectangles ---
    private void initPanelCartesChoisies() {
        panelCartesChoisies = new JPanel(new GridLayout(1, 3, 5, 0));
        panelCartesChoisies.setBorder(BorderFactory.createTitledBorder("Manche"));
        labelsCartesChoisies = new JLabel[3];
        for (int i = 0; i < 3; i++) {
            labelsCartesChoisies[i] = new JLabel();
            labelsCartesChoisies[i].setPreferredSize(new Dimension(CARTE_W, CARTE_H));
            labelsCartesChoisies[i].setBorder(BorderFactory.createLineBorder(Color.GRAY));
            labelsCartesChoisies[i].setHorizontalAlignment(SwingConstants.CENTER);
            panelCartesChoisies.add(labelsCartesChoisies[i]);
        }
    }

    public void actualiserCartesManche(List<Cartes> selection) {
        for (int i = 0; i < 3; i++) {
            if (selection != null && i < selection.size()) {
                Cartes c = selection.get(i);
                labelsCartesChoisies[i].setIcon(changerImageRedimensionnee(c.getImage(), CARTE_W, CARTE_H));
            } else {
                labelsCartesChoisies[i].setIcon(null);
            }
        }
    }

    // --- FONCTIONS EXISTANTES (Maintenues pour le Contrôleur) ---
    public void actualiserTourJoueur(String nom) {
        labelMessage.setText("C'est au tour de " + nom);
        labelMessage.setForeground(Color.BLUE);
    }

    public void revelerCarteDepuisMain(int idJoueur, Cartes carte) {
        ImageIcon icone = changerImageRedimensionnee(carte.getImage(), 150, 220);
        JOptionPane.showMessageDialog(this, "Valeur : " + carte.getValeur(),
                "Carte révélée par " + idJoueur, JOptionPane.INFORMATION_MESSAGE, icone);
    }

    public void initialiserZoneJoueurs(List<Joueur> joueurs, GameController controller) {
        containerJoueursNord.removeAll();
        panelEst.removeAll();
        panelOuest.removeAll();
        
        BorderLayout layoutSud = (BorderLayout) panelSud.getLayout();
        Component centerC = layoutSud.getLayoutComponent(BorderLayout.CENTER);
        if (centerC != null) panelSud.remove(centerC);

        JPanel containerJStatiqueSud = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelSud.add(containerJStatiqueSud, BorderLayout.CENTER);

        for (int i = 0; i < joueurs.size(); i++) {
            boolean estHorizontal = (i % 2 == 0);
            JPanel p = creerPanelJoueur(joueurs.get(i), controller, estHorizontal);
            switch (i % 4) {
                case 0: containerJStatiqueSud.add(p); break;
                case 1: panelOuest.add(p); break;
                case 2: containerJoueursNord.add(p); break;
                case 3: panelEst.add(p); break;
            }
        }
        rafraichirVue();
    }

    private JPanel creerPanelJoueur(Joueur j, GameController controller, boolean estHorizontal) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createTitledBorder(j.getNom()));
        Dimension dim = estHorizontal ? new Dimension(260, 160) : new Dimension(160, 280);
        p.setPreferredSize(dim);

        JButton btnHaut = new JButton("Haut");
        JButton btnBas = new JButton("Bas");
        btnHaut.addActionListener(e -> controller.choisirCarteJoueur(j.getId(), true));
        btnBas.addActionListener(e -> controller.choisirCarteJoueur(j.getId(), false));

        JPanel cmd = new JPanel(new GridLayout(1, 2, 5, 0));
        cmd.add(btnHaut); cmd.add(btnBas);
        p.add(cmd);

        JPanel trios = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (Cartes c : j.getStock().getCartes()) {
            trios.add(new JLabel(changerImageRedimensionnee(c.getImage(), 30, 45)));
        }
        p.add(new JScrollPane(trios));
        return p;
    }

    public void afficherMainJoueurActif(Joueur j) {
        panelMainJoueurActif.removeAll();
        ((TitledBorder) panelMainJoueurActif.getBorder()).setTitle("Main de " + j.getNom());
        for (Cartes c : j.getDeckJoueur().getCartes()) {
            panelMainJoueurActif.add(new JLabel(changerImageRedimensionnee(c.getImage(), 80, 115)));
        }
        rafraichirVue();
    }

    private void initGrille() {
        panelGrille = new JPanel(new GridLayout(3, 3, 10, 10));
        boutonsGrille = new JButton[9];
        for (int i = 0; i < 9; i++) {
            boutonsGrille[i] = new JButton();
            ImageIcon dos = changerImageRedimensionnee(CHEMIN_DOS, CARTE_W, CARTE_H);
            if (dos != null) {
                boutonsGrille[i].setIcon(dos);
                boutonsGrille[i].setContentAreaFilled(false);
                boutonsGrille[i].setBorderPainted(false);
            }
            boutonsGrille[i].setPreferredSize(new Dimension(CARTE_W, CARTE_H));
            panelGrille.add(boutonsGrille[i]);
        }
        JPanel wrapperCentral = new JPanel(new GridBagLayout());
        wrapperCentral.add(panelGrille);
        panelCentre.add(wrapperCentral, BorderLayout.CENTER);
    }

    public void revelerCarteGrille(int index, Cartes c) {
        if (c != null) {
            boutonsGrille[index].setIcon(changerImageRedimensionnee(c.getImage(), CARTE_W, CARTE_H));
        } else {
            boutonsGrille[index].setEnabled(false);
            boutonsGrille[index].setIcon(null);
        }
    }

    public void actualiserGrille(model.Pioche pioche) {
        for (int i = 0; i < 9; i++) {
            Cartes c = pioche.devoilerCarte(i);
            if (c == null) {
                boutonsGrille[i].setEnabled(false);
                boutonsGrille[i].setIcon(null);
            } else {
                boutonsGrille[i].setIcon(changerImageRedimensionnee(CHEMIN_DOS, CARTE_W, CARTE_H));
                boutonsGrille[i].setEnabled(true);
            }
        }
    }

    public void cacherCartesGrille() {
        for (JButton b : boutonsGrille) {
            if (b.isEnabled()) b.setIcon(changerImageRedimensionnee(CHEMIN_DOS, CARTE_W, CARTE_H));
        }
    }

    public void actualiserTout(List<Joueur> j, Joueur act, model.Pioche p) {
        initialiserZoneJoueurs(j, controller);
        afficherMainJoueurActif(act);
        actualiserGrille(p);
        actualiserTourJoueur(act.getNom());
    }

    public List<String> demanderNomsJoueurs() {
        List<String> joueurs = new ArrayList<>();
        String input = JOptionPane.showInputDialog(this, "Nombre de joueurs (3-6) ?");
        if (input == null) return new ArrayList<>();
        try {
            int nb = Math.min(6, Math.max(3, Integer.parseInt(input)));
            for (int i = 0; i < nb; i++) {
                String n = JOptionPane.showInputDialog(this, "Nom joueur " + (i + 1));
                joueurs.add(n != null && !n.isEmpty() ? n : "J" + (i + 1));
            }
        } catch (Exception e) { }
        return joueurs;
    }

    private ImageIcon changerImageRedimensionnee(String chemin, int w, int h) {
        if (chemin == null) return null;
        try {
            ImageIcon icon = new ImageIcon(chemin);
            Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) { return null; }
    }

    public void setController(GameController c) {
        this.controller = c;
        for (int i = 0; i < 9; i++) {
            final int idx = i;
            boutonsGrille[i].addActionListener(e -> controller.choisirCarteGrille(idx));
        }
    }

    public void afficherMessage(String m) { labelMessage.setText(m); }
    public void afficherVictoire(String n) { JOptionPane.showMessageDialog(this, "Gagné par " + n); }
    public void actualiserScores(List<Joueur> j) { initialiserZoneJoueurs(j, this.controller); }
    private void rafraichirVue() { this.revalidate(); this.repaint(); }
}