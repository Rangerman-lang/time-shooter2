package com.timeshooter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Represents an enemy in the game
 */
public class Enemy {
    private double x, y;
    private double velocityX, velocityY;
    private double baseVelocityY;
    private static final double SPEED = 100;
    private static final double RADIUS = 12;
    private int maxHealth;
    private int currentHealth;
    private double waveTime = 0;
    private boolean active = true;

    public Enemy(double x, double y, int wave) {
        this.x = x;
        this.y = y;
        this.baseVelocityY = SPEED + (wave * 15);
        this.velocityY = baseVelocityY;
        this.velocityX = 0;
        
        // Health scales with wave
        this.maxHealth = 1 + (wave / 3);
        this.currentHealth = maxHealth;
    }

    public void update(double deltaTime, double timeScale) {
        waveTime += deltaTime;
        
        // Wavy movement pattern
        velocityX = Math.sin(waveTime * 3) * 80;
        velocityY = baseVelocityY;
        
        x += velocityX * deltaTime * timeScale;
        y += velocityY * deltaTime * timeScale;
    }

    public void render(GraphicsContext gc) {
        // Draw enemy body
        gc.setFill(Color.color(1, 0.3, 0.3));
        gc.fillOval(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2);

        // Draw health bar
        double barWidth = RADIUS * 2 + 4;
        gc.setFill(Color.BLACK);
        gc.fillRect(x - barWidth / 2, y - RADIUS - 8, barWidth, 4);
        
        double healthPercent = (double) currentHealth / maxHealth;
        Color healthColor = healthPercent > 0.5 ? Color.GREEN : Color.ORANGE;
        if (healthPercent <= 0.25) healthColor = Color.RED;
        
        gc.setFill(healthColor);
        gc.fillRect(x - barWidth / 2, y - RADIUS - 8, barWidth * healthPercent, 4);
    }

    public void takeDamage(int damage) {
        currentHealth -= damage;
        if (currentHealth <= 0) {
            active = false;
        }
    }

    public boolean isOutOfBounds(int height) {
        return y > height;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getRadius() { return RADIUS; }
    public boolean isActive() { return active; }
    public int getMaxHealth() { return maxHealth; }
}
