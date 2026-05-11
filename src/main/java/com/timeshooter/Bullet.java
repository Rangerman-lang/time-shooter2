package com.timeshooter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Represents a bullet projectile
 */
public class Bullet {
    private double x, y;
    private double velocityX, velocityY;
    private static final double SPEED = 600;
    private static final double RADIUS = 4;
    private boolean active = true;
    private double lifetime = 10; // seconds

    public Bullet(double x, double y, double dirX, double dirY) {
        this.x = x;
        this.y = y;
        
        // Normalize direction
        double length = Math.sqrt(dirX * dirX + dirY * dirY);
        if (length > 0) {
            dirX /= length;
            dirY /= length;
        }
        
        this.velocityX = dirX * SPEED;
        this.velocityY = dirY * SPEED;
    }

    public void update(double deltaTime, double timeScale) {
        x += velocityX * deltaTime * timeScale;
        y += velocityY * deltaTime * timeScale;
        lifetime -= deltaTime;

        if (lifetime <= 0) {
            active = false;
        }
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.YELLOW);
        gc.fillOval(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2);

        // Draw trail
        gc.setStroke(Color.color(1, 1, 0, 0.3));
        gc.setLineWidth(2);
        double trailX = x - velocityX * 0.1;
        double trailY = y - velocityY * 0.1;
        gc.strokeLine(x, y, trailX, trailY);
    }

    public boolean isOutOfBounds(int width, int height) {
        return x < 0 || x > width || y < 0 || y > height;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public boolean isActive() { return active; }
    public double getRadius() { return RADIUS; }

    public void deactivate() {
        active = false;
    }
}
