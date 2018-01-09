package com.recycle.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Created by recycle on 2017/2/3.
 * Snake Game
 * keyboard "p" or button "▶‖" to start or stop
 * keyboard "<<", ">>" or button "<<", ">>" to adjust speed
 * keyboard "a", "s", "d", "w" to control direction
 */
public class SnakeEating {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame jFrame = new SnakeFrame();
                jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                jFrame.setResizable(false);
                jFrame.requestFocus();
                jFrame.setVisible(true);
            }
        });
    }
}

class SnakeFrame extends JFrame{
    enum Direction {North, South, East, West};  //direction control
    enum Ctrl {start, stop};    //start or stop
    private volatile Direction direction = Direction.East;
    private volatile Ctrl ctrl = Ctrl.stop;
    private static final int DEFAULT_WIDTH = 5;    //multi *100
    private static final int DEFAULT_HEIGHT = 4;    //multi *100
    private int delay = 200;    //mills, speed

    SnakeFrame() {
        JLabel jLabel = new JLabel("    ||||   ");  //speed label

        //inner class of keyListener
        class MyKeyListener extends KeyAdapter {
            @Override
            public void keyTyped(KeyEvent e) {
                char keyChar = e.getKeyChar();
                switch (keyChar) {
                    case 'p':
                    case 'P':
                        ctrl = ctrl = ctrl == Ctrl.stop ? Ctrl.start : Ctrl.stop;
                        break;
                    case ',':
                        delay = (delay * 2) <= 800 ? delay * 2 : delay;
                        jLabel.setText(jLabel.getText().replace("|| ", "|  "));
                        break;
                    case '.':
                        delay = delay / 2 >= 25 ? delay / 2 : delay;
                        jLabel.setText(jLabel.getText().replace("|  ", "|| "));
                        break;
                    case 'd':
                    case 'D':
                        direction = Direction.East;
                        break;
                    case 'a':
                    case 'A':
                        direction = Direction.West;
                        break;
                    case 'w':
                    case 'W':
                        direction = Direction.North;
                        break;
                    case 's':
                    case 'S':
                        direction = Direction.South;
                        break;
                }
            }
        }

        addKeyListener(new MyKeyListener());
        setTitle("Snake Eating");
        //get screen dimension
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenDimension = toolkit.getScreenSize();

        setBounds(screenDimension.width / 2 - (DEFAULT_WIDTH * 100 + 6) / 2, (screenDimension.height / 2 - (DEFAULT_HEIGHT * 100 + 1) / 2) * 3 / 4, DEFAULT_WIDTH * 100 + 6, DEFAULT_HEIGHT * 100 + 1);

        JPanel buttonPanel = new JPanel();
        addButton("▶‖", buttonPanel, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ctrl = ctrl == Ctrl.stop ? Ctrl.start : Ctrl.stop;
            }
        }, new MyKeyListener());
        addButton("<<", buttonPanel, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                delay = (delay * 2) <= 800 ? delay * 2 : delay;
                jLabel.setText(jLabel.getText().replace("|| ", "|  "));
            }
        }, new MyKeyListener());
        addButton(">>", buttonPanel, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                delay = delay / 2 >= 25 ? delay / 2 : delay;
                jLabel.setText(jLabel.getText().replace("|  ", "|| "));
            }
        }, new MyKeyListener());
        //        addButton("＜", buttonPanel, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                direction = Direction.West;
//            }
//        }, new MyKeyListener());
//        addButton("＞", buttonPanel, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                direction = Direction.East;
//            }
//        }, new MyKeyListener());
//        addButton("∧", buttonPanel, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                direction = Direction.North;
//            }
//        }, new MyKeyListener());
//        addButton("∨", buttonPanel, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                direction = Direction.South;
//            }
//        }, new MyKeyListener());
        buttonPanel.add(jLabel);
        add(buttonPanel, BorderLayout.NORTH);

        SnakeComponent snakePanel = new SnakeComponent();
        snakePanel.addKeyListener(new MyKeyListener());
        snakePanel.setBounds(0, 0, DEFAULT_WIDTH * 100, DEFAULT_HEIGHT * 100 -70);
        snakePanel.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.black));
        add(snakePanel, BorderLayout.CENTER);

        snakePanel.balls = Snake.initBalls(snakePanel);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    //stop
                    while (ctrl == Ctrl.stop) {
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //start
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //knock into wall or itself
                    if (Snake.nextStep(snakePanel.balls, snakePanel, direction, SnakeFrame.this) == null) {
//                        ctrl = Ctrl.stop;
                        break;
                    }
                    //move one step
                    else {
                        snakePanel.balls = Snake.nextStep(snakePanel.balls, snakePanel, direction, SnakeFrame.this);
                    }

                    snakePanel.repaint();
                }
            }
        }).start();
    }

    private void addButton(String buttonName, JPanel panel, ActionListener actionListener, KeyListener keyListener) {
        JButton button = new JButton(buttonName);
        button.addActionListener(actionListener);
        button.addKeyListener(keyListener);
        panel.add(button);
    }

    //use for calling back to change direction
    void alterDirection() {
        switch (direction) {
            case East:
                direction = Direction.West;
                break;
            case West:
                direction = Direction.East;
                break;
            case North:
                direction = Direction.South;
                break;
            case South:
                direction = Direction.North;
                break;

        }
    }
}

class SnakeComponent extends JPanel {
    volatile ArrayList<Ball> balls;

    @Override
    //paint a snake
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        if (balls != null && balls.size() != 0) {
            for (Ball ball : balls) {
                g2.fill(ball.getShape());
            }
        }
    }
}