package com.recycle.main;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Created by recycle on 2017/2/3.
 */
class Snake {
    //init snake when game starting
    static ArrayList<Ball> initBalls(Component component) {
        ArrayList<Ball> balls = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            balls.add(new Ball(alterInt(component.getWidth()/2 - i * 10), alterInt(component.getHeight()/2)));
        }

        //generate a random ball offered snake to eat, add it to the last position of the arrayList
        Ball randomBall = new Ball(alterInt(Math.random() * component.getWidth()), alterInt(Math.random() * component.getHeight()));
        //if the random ball appeared as one part of snake, generate again
        boolean flag = true;
        while (flag) {
            flag = false;
            for (Ball ball : balls) {
                if (randomBall.equals(ball)) {
                    randomBall = new Ball(alterInt(Math.random() * component.getWidth()), alterInt(Math.random() * component.getHeight()));
                    flag = true;
                }
            }
        }
        balls.add(randomBall);
        return balls;
    }

    //decide action for next step
    //situations: knock into the wall, knock into the snake itself, eat the random ball, nothing to happened
    static ArrayList<Ball> nextStep(ArrayList<Ball> balls, Component component, SnakeFrame.Direction direction, SnakeFrame snakeFrame) {
        int dx = 0;
        int dy = 0;
        switch (direction) {
            case East:
                dx = 10;
                break;
            case West:
                dx = -10;
                break;
            case North:
                dy = -10;
                break;
            case South:
                dy = 10;
                break;
        }

        //new position of the first ball in arrayList
        Ball testBall = balls.get(0).move(dx, dy);
        //error direction, using callback function to change direction
        if (testBall.equals(balls.get(1))) {
            snakeFrame.alterDirection();
            testBall = testBall.move(-2 * dx, -2 * dy);
        }
        //knock into the wall
        if (testBall.x < 0 || testBall.x >= component.getWidth() || testBall.y < 0 || testBall.y >= component.getHeight()) {
            return null;
        }
        //knock into the snake itself
        for (int i = 2; i < balls.size() - 1; i++) {
            if (testBall.equals(balls.get(i))) {
                return null;
            }
        }

        ArrayList<Ball> balls1 = new ArrayList<>();
        balls1.add(testBall);
        for (int i = 0; i < balls.size() - 1; i++) {
            balls1.add(balls.get(i));
        }
        //eat one
        if (testBall.equals(balls.get(balls.size() - 1))) {
            Ball randomBall = new Ball(alterInt(Math.random() * component.getWidth()), alterInt(Math.random() * component.getHeight()));
            boolean flag = true;
            while (flag) {
                flag = false;
                for (Ball ball : balls) {
                    if (randomBall.equals(ball)) {
                        randomBall = new Ball(alterInt(Math.random() * component.getWidth()), alterInt(Math.random() * component.getHeight()));
                        flag = true;
                    }
                }
            }
            balls1.add(randomBall);
        }
        //nothing happened
        else {
            balls1.remove(balls1.size() - 1);
            balls1.add(balls.get(balls.size() - 1));
        }

        return balls1;
    }

    private static int alterInt(double before) {
        return (int)before / 10 * 10;
    }
}

class Ball {
    int x = 0;
    int y = 0;

    Ball(int x, int y) {
        this.x = x;
        this.y = y;
    }

    Ball move(int dx, int dy) {
        return new Ball(x + dx, y + dy);
    }

    Rectangle2D getShape() {
        return new Rectangle2D.Double(x, y, 9,9);
    }

    boolean equals(Ball other) {
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public String toString() {
        return "x=" + x + " y=" + y;
    }
}

