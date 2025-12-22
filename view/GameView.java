package view;

import model.Cartes;
import model.Game;
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
    private JPanel panelNord, panelSud, panelEst, panelOuest; // Zones pour les joueurs
    private JPanel panelMainJoueurActif; // Sous-panneau sud pour la main
    private JLabel labelMessage;
    private JButton[] boutonsGrille;
    private String CHEMIN_DOS = "carte/back.png";
    private GameController controller;

    public GameView() {
        this.setTitle("Trio - Table de Jeu");
        this.setSize(1200, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        // 1. Zone Centrale (Grille + Message)
        panelCentre = new JPanel(new BorderLayout());

        // Message en haut du centre
        labelMessage = new JLabel("Bienvenue dans Trio !", SwingConstants.CENTER);
        labelMessage.setFont(new Font("Arial", Font.BOLD, 18));
        panelCentre.add(labelMessage, BorderLayout.NORTH);

        // Grille 3x3 (Initialisation via méthode dédiée)
        initGrille();

        this.add(panelCentre, BorderLayout.CENTER);

        // 2. Initialisation des zones joueurs (N, S, E, O)
        panelNord = new JPanel();
        panelSud = new JPanel(new BorderLayout()); // Sud contient aussi la main
        panelEst = new JPanel();
        panelOuest = new JPanel();

        // Config layout des panneaux latéraux
        panelNord.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelSud.setLayout(new BorderLayout());
        panelEst.setLayout(new GridBagLayout());
        panelOuest.setLayout(new GridBagLayout());

        // Panneau pour la main du joueur actif (dans le Sud)
        panelMainJoueurActif = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelMainJoueurActif.setBorder(BorderFactory.createTitledBorder("Vos Cartes"));
        panelSud.add(panelMainJoueurActif, BorderLayout.SOUTH);

        // Ajout au JFrame
        this.add(panelNord, BorderLayout.NORTH);
        this.add(panelSud, BorderLayout.SOUTH);
        this.add(panelEst, BorderLayout.EAST);
        this.add(panelOuest, BorderLayout.WEST);

        this.setVisible(true);
    }

    public List<String> demanderNomsJoueurs() {
        List<String> joueurs = new ArrayList<>();
        String input = JOptionPane.showInputDialog(this, "Nombre de joueurs (3 - 6) ?");
        if (input != null) {
            try {
                int nb = Math.min(6, Math.max(3, Integer.parseInt(input)));
                for (int i = 0; i < nb; i++) {
                    String nom = JOptionPane.showInputDialog(this, "Nom du joueur " + (i + 1) + " ?");
                    joueurs.add(nom != null && !nom.isEmpty() ? nom : "Joueur " + (i + 1));
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un nombre valide.", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        return joueurs;
    }

    public void setController(GameController controller) {
        this.controller = controller;
        for (int i = 0; i < 9; i++) {
            final int index = i;
            this.boutonsGrille[i].addActionListener(e -> controller.choisirCarteGrille(index));
        }
    }

    public void initialiserZoneJoueurs(List<Joueur> joueurs, GameController controller) {
        // Nettoyer les zones
        panelNord.removeAll();

        // Nettoyage spécifique pour le Sud
        BorderLayout layoutSud = (BorderLayout) panelSud.getLayout();
        Component centerC = layoutSud.getLayoutComponent(BorderLayout.CENTER);
        if (centerC != null)
            panelSud.remove(centerC);

        JPanel containerJStatiqueSud = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelSud.add(containerJStatiqueSud, BorderLayout.CENTER);

        panelEst.removeAll();
        panelOuest.removeAll();

        // Distribution simple
        for (int i = 0; i < joueurs.size(); i++) {
            // Orientation : Nord/Sud (index 0 et 2) => Horizontal, Ouest/Est (index 1 et 3)
            // => Vertical
            boolean estHorizontal = (i % 2 == 0);
            JPanel p = creerPanelJoueur(joueurs.get(i), controller, estHorizontal);

            switch (i % 4) {
                case 0:
                    containerJStatiqueSud.add(p);
                    break;
                case 1:
                    panelOuest.add(p);
                    break;
                case 2:
                    panelNord.add(p);
                    break;
                case 3:
                    panelEst.add(p);
                    break;
                default:
                    panelNord.add(p);
            }
        }

        rafraichirVue();
    }

    // Création d'un panel individuel pour un joueur (Nom, Boutons, Trios gagnés)
    // Modifié par Blusk
    private JPanel creerPanelJoueur(Joueur j, GameController controller, boolean estHorizontal) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createTitledBorder(j.getNom()));

        Dimension dim;
        Dimension dimScroll;

        if (estHorizontal) {
            // Plus large, moins haut (Nord / Sud)
            dim = new Dimension(300, 200);
            dimScroll = new Dimension(280, 90);
        } else {
            // Plus haut, moins large (Est / Ouest)
            dim = new Dimension(180, 350);
            dimScroll = new Dimension(160, 240);
        }

        p.setPreferredSize(dim);
        p.setMinimumSize(dim);
        p.setMaximumSize(dim);

        // Commandes (Haut / Bas)
        JPanel cmd = new JPanel(new GridLayout(1, 2, 5, 0));
        // Taille fixe pour les commandes, adaptée à la largeur
        Dimension dimCmd = new Dimension(estHorizontal ? 280 : 160, 30);
        cmd.setMaximumSize(dimCmd);
        cmd.setPreferredSize(dimCmd);
        cmd.setMinimumSize(dimCmd);

        JButton btnHaut = new JButton("Haut");
        JButton btnBas = new JButton("Bas");
        btnHaut.setMargin(new Insets(2, 2, 2, 2));
        btnBas.setMargin(new Insets(2, 2, 2, 2));

        btnHaut.addActionListener(e -> controller.choisirCarteJoueur(j.getId(), true));
        btnBas.addActionListener(e -> controller.choisirCarteJoueur(j.getId(), false));
        cmd.add(btnHaut);
        cmd.add(btnBas);

        p.add(Box.createVerticalStrut(5));
        p.add(cmd);
        p.add(Box.createVerticalStrut(5));

        // Zone des trios gagnés
        // Si horizontal, on veut remplir en largeur (plus de colonnes)
        // Si vertical, on veut remplir en hauteur (moins de colonnes)
        int cols = estHorizontal ? 6 : 2;
        JPanel trios = new JPanel(new GridLayout(0, cols, 5, 5));
        trios.setBorder(BorderFactory.createTitledBorder("Gagné"));

        List<Cartes> stock = j.getStock().getCartes();

        for (Cartes c : stock) {
            ImageIcon icon = changerImageRedimensionnee(c.getImage(), 40, 60);
            JLabel ico = new JLabel();
            ico.setPreferredSize(new Dimension(40, 60));
            if (icon != null) {
                ico.setIcon(icon);
            } else {
                ico.setText(c.getNom());
                ico.setFont(new Font("Arial", Font.PLAIN, 10));
            }
            trios.add(ico);
        }

        // ScrollPane
        JScrollPane scroll = new JScrollPane(trios);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        scroll.setPreferredSize(dimScroll);
        scroll.setMaximumSize(dimScroll);

        p.add(scroll);

        return p;
    }

    public void afficherMainJoueurActif(Joueur j) {
        panelMainJoueurActif.removeAll();
        // Titre
        ((TitledBorder) panelMainJoueurActif.getBorder()).setTitle("Main de " + j.getNom());

        // Afficher toutes les cartes du deck
        for (Cartes c : j.getDeckJoueur().getCartes()) {
            // Carte main un peu plus grandes
            ImageIcon icon = changerImageRedimensionnee(c.getImage(), 90, 135);
            JLabel l = new JLabel();
            l.setToolTipText(c.getNom() + " (" + c.getValeur() + ")");

            if (icon != null) {
                l.setIcon(icon);
            } else {
                l.setText("<html><center>" + c.getNom() + "<br>" + c.getValeur() + "</center></html>");
                l.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                l.setPreferredSize(new Dimension(90, 135));
                l.setHorizontalAlignment(SwingConstants.CENTER);
            }
            panelMainJoueurActif.add(l);
        }
        panelMainJoueurActif.revalidate();
        panelMainJoueurActif.repaint();
    }

    // Modifié par Blusk
    private ImageIcon changerImageRedimensionnee(String chemin, int w, int h) {
        // Validation simple du chemin
        if (chemin == null)
            return null;

        // Tentative de correction si le fichier n'est pas trouvé direct
        java.io.File f = new java.io.File(chemin);
        if (!f.exists()) {
            // Essai avec préfixe CodeAP4B si lancé depuis le dossier parent
            java.io.File f2 = new java.io.File("CodeAP4B/" + chemin);
            if (f2.exists()) {
                chemin = "CodeAP4B/" + chemin;
            } else {
                // Debug log
                System.err.println("Image introuvable: " + f.getAbsolutePath());
                return null;
            }
        }

        try {
            ImageIcon icone = new ImageIcon(chemin);
            Image img = icone.getImage();
            if (img.getWidth(null) == -1)
                return null;

            Image nouvelleImage = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(nouvelleImage);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Modifié par Blusk
    public void revelerCarteGrille(int index, Cartes c) {
        if (c != null) {
            ImageIcon icon = changerImageRedimensionnee(c.getImage(), 90, 130);
            if (icon != null) {
                boutonsGrille[index].setIcon(icon);
                boutonsGrille[index].setText(""); // Pas de texte si image
                boutonsGrille[index].setContentAreaFilled(false);
            } else {
                // Fallback si image ratée
                boutonsGrille[index].setIcon(null);
                boutonsGrille[index].setText(c.getNom());
                boutonsGrille[index].setContentAreaFilled(true);
                boutonsGrille[index].setBackground(Color.CYAN);
            }
        } else {
            // Case vide (gagnée)
            boutonsGrille[index].setIcon(null);
            boutonsGrille[index].setText("");
            boutonsGrille[index].setEnabled(false);
            boutonsGrille[index].setContentAreaFilled(false);
        }
    }

    // Modification aussi dans l'initialisation pour gérer le fallback du dos de
    // carte
    // Modifié par Blusk
    private void initGrille() {
        panelGrille = new JPanel(new GridLayout(3, 3, 10, 10));
        panelGrille.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        boutonsGrille = new JButton[9];
        for (int i = 0; i < 9; i++) {
            boutonsGrille[i] = new JButton();
            ImageIcon dos = changerImageRedimensionnee(CHEMIN_DOS, 90, 130);

            if (dos != null) {
                boutonsGrille[i].setIcon(dos);
                boutonsGrille[i].setContentAreaFilled(false);
                boutonsGrille[i].setBorderPainted(false);
            } else {
                // Fallback visible
                boutonsGrille[i].setText("DOS");
                boutonsGrille[i].setBackground(Color.GRAY);
                boutonsGrille[i].setContentAreaFilled(true);
                boutonsGrille[i].setBorderPainted(true);
            }
            panelGrille.add(boutonsGrille[i]);
        }
        panelCentre.add(panelGrille, BorderLayout.CENTER);
    }

    public void revelerCarteDepuisMain(int idJoueur, Cartes carte) {
        ImageIcon icone = changerImageRedimensionnee(carte.getImage(), 150, 220);
        JOptionPane.showMessageDialog(this, carte.getNom(),
                "Carte révélée par Joueur " + idJoueur,
                JOptionPane.INFORMATION_MESSAGE, icone);
    }

    public void cacherCartesGrille() {
        for (JButton b : boutonsGrille) {
            // Remet le dos SEULEMENT si le bouton est activé (donc pas vide)
            if (b.isEnabled()) {
                b.setIcon(changerImageRedimensionnee(CHEMIN_DOS, 90, 130));
            }
        }
    }

    // Met à jour l'affichage global quand on change de tour ou gagne
    // Modifié par Blusk
    public void actualiserTout(List<Joueur> joueurs, Joueur joueurActif, model.Pioche pioche) {
        actualiserScores(joueurs);
        afficherMainJoueurActif(joueurActif);
        actualiserTourJoueur(joueurActif.getNom());
        actualiserGrille(pioche);
    }

    // Fait par Blusk
    public void actualiserGrille(model.Pioche pioche) {
        for (int i = 0; i < 9; i++) {
            Cartes c = pioche.devoilerCarte(i);
            // Si c est null, c'est que la carte est gagnée et donc presence d'un trou dans
            // la grille de pioche
            // Si c est non-null, elle est présente mais doit être de dos
            if (c == null) {
                // Case vide
                revelerCarteGrille(i, null);
            } else {
                // Carte présente, on affiche le dos
                ImageIcon dos = changerImageRedimensionnee(CHEMIN_DOS, 90, 130);
                if (dos != null) {
                    boutonsGrille[i].setIcon(dos);
                } else {
                    boutonsGrille[i].setText("DOS");
                    boutonsGrille[i].setBackground(Color.GRAY);
                    boutonsGrille[i].setContentAreaFilled(true);
                }
                boutonsGrille[i].setEnabled(true);
                // On doit être sûr qu'on ne garde pas l'ancienne image ou texte
                if (dos != null)
                    boutonsGrille[i].setText("");
            }
        }
    }

    public void actualiserTourJoueur(String nom) {
        labelMessage.setText("C'est au tour de " + nom);
        labelMessage.setForeground(Color.BLUE);
    }

    public void actualiserScores(List<Joueur> joueurs) {
        // On réinitialise les zones joueurs pour mettre à jour les trios
        initialiserZoneJoueurs(joueurs, this.controller);
    }

    public void afficherMessage(String msg) {
        labelMessage.setText(msg);
    }

    public void afficherVictoire(String nom) {
        JOptionPane.showMessageDialog(this, "BRAVO ! " + nom + " a gagné la partie !");
    }

    private void rafraichirVue() {
        this.revalidate();
        this.repaint();
    }
}
