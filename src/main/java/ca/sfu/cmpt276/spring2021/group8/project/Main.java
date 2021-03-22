package ca.sfu.cmpt276.spring2021.group8.project;


import ca.sfu.cmpt276.spring2021.group8.project.game.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

public class Main {
    private static final String SCREEN_GAME = "game";
    private static final String SCREEN_MAINMENU = "mainmenu";
    private static final String SCREEN_WIN = "win";
    private static final String SCREEN_LOOSE = "loose";

    private final static int width = 1280;
    private final static int height = 720;

    private Canvas canvas;
    
    private CardLayout cardLayout = new CardLayout();
    private JPanel panel = new JPanel();

    public static void main(String[] args) {
        new Main().showMainMenu();
    }

    private Main() {
        JFrame f = new JFrame();

        panel.setLayout(cardLayout);

        f.add(panel);

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(width, height);

        panel.add(createMainMenu(), SCREEN_MAINMENU);
        panel.add(createGameCanvas(width, height), SCREEN_GAME);
        f.setVisible(true);

        try {
            SoundEffects.BRplayMusic("src/resources/Audio/Background.wav");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Canvas createGameCanvas(int width, int height) {
        canvas = new Canvas();

        canvas.setSize(width, height);
        canvas.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Component component = e.getComponent();
                if (component != null) {
                    Rectangle bounds = component.getBounds();
                    canvas.setSize(bounds.width, bounds.height);
                }
            }
        });

        return canvas;
    }

    private Component createMainMenu() {
        JPanel panel = new JPanel();

        JButton btn = new JButton("Start Game");

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                startGame();
            }
        });
        panel.add(btn);
        panel.setBackground(Color.red);

        return panel;
    }

    private void startGame() {
        cardLayout.show(panel, SCREEN_GAME);
        canvas.requestFocusInWindow();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Game game = new Game();
                try {
                    canvas.addKeyListener(game);

                    GameResult result = game.loop(canvas);
                    switch (result) {
                        case WIN:
                            panel.add(showWinningScreen(),SCREEN_WIN);
                            cardLayout.show(panel, SCREEN_WIN);
                            break;
                        case LOSE:
                            panel.add(showLosingScreen(),SCREEN_LOOSE);
                            cardLayout.show(panel, SCREEN_LOOSE);
                            break;
                        default:
                            System.out.println("game result: " + result.toString());
                            showMainMenu();
                            break;

                    // TODO handle other game results
                    }
                } finally {
                    canvas.removeKeyListener(game);
                }
            }
        }).start();
    }

    private void showMainMenu() {
        cardLayout.show(panel, SCREEN_MAINMENU);
    }

    private Component showWinningScreen(){
        JPanel panel = new JPanel();

        JButton btn = new JButton("Main Menu");
        panel.add(btn);
        panel.setBackground(Color.GREEN);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showMainMenu();
            }
        });

        return panel;
    }

    private Component showLosingScreen(){
        JPanel panel = new JPanel();

        JButton btn = new JButton("Main Menu");
        panel.add(btn);
        panel.setBackground(Color.CYAN);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showMainMenu();
            }
        });

        return panel;
    }
}
