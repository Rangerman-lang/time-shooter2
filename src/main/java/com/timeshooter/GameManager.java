package com.timeshooter;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all game entities and game state
 */
public class GameManager {
    private Player player;
    private List<Enemy> enemies;
    private List<Bullet> bullets;
    private WaveManager waveManager;
    private ScoreManager scoreManager;
    private TimeManager timeManager;
    private int lives = 3;
    private boolean gameOver = false;
    private int width, height;

    public GameManager(int width, int height) {
        this.width = width;
        this.height = height;
        this.player = new Player(width, height);
        this.enemies = new ArrayList<>();
        this.bullets = new ArrayList<>();
        this.waveManager = new WaveManager();
        this.scoreManager = new ScoreManager();
        this.timeManager = new TimeManager();
    }

    public void update(double deltaTime) {
        if (gameOver) return;

        // Update managers
        timeManager.update(deltaTime);
        double timeScale = timeManager.getTimeScale();
        
        player.update(deltaTime);
        waveManager.update(deltaTime * timeScale);

        // Update enemies
        for (Enemy enemy : enemies) {
            if (enemy.isActive()) {
                enemy.update(deltaTime, timeScale);
            }
        }

        // Update bullets
        for (Bullet bullet : bullets) {
            if (bullet.isActive()) {
                bullet.update(deltaTime, timeScale);
            }
        }

        // Spawn enemies
        if (waveManager.shouldSpawnEnemy()) {
            spawnEnemy();
        }

        // Check collisions
        checkCollisions();

        // Remove inactive entities
        enemies.removeIf(e -> !e.isActive() || e.isOutOfBounds(height));
        bullets.removeIf(b -> !b.isActive() || b.isOutOfBounds(width, height));

        // Check game over
        if (lives <= 0) {
            gameOver = true;
        }
    }

    private void spawnEnemy() {
        double spawnX = Math.random() * (width - 40) + 20;
        Enemy enemy = new Enemy(spawnX, -20, waveManager.getCurrentWave());
        enemies.add(enemy);
    }

    private void checkCollisions() {
        // Bullet-enemy collisions
        for (Bullet bullet : new ArrayList<>(bullets)) {
            for (Enemy enemy : new ArrayList<>(enemies)) {
                double dist = Math.sqrt(Math.pow(bullet.getX() - enemy.getX(), 2) +
                                       Math.pow(bullet.getY() - enemy.getY(), 2));
                if (dist < bullet.getRadius() + enemy.getRadius()) {
                    enemy.takeDamage(1);
                    bullet.deactivate();
                    if (!enemy.isActive()) {
                        scoreManager.enemyKilled();
                    }
                }
            }
        }

        // Enemy-player collisions
        for (Enemy enemy : enemies) {
            if (enemy.isActive()) {
                double dist = Math.sqrt(Math.pow(player.getX() - enemy.getX(), 2) +
                                       Math.pow(player.getY() - enemy.getY(), 2));
                if (dist < player.getRadius() + enemy.getRadius()) {
                    lives--;
                    scoreManager.playerHit();
                    enemy.takeDamage(999); // Remove enemy on collision
                }
            }
        }
    }

    public void playerShoot() {
        Bullet bullet = player.shoot();
        if (bullet != null) {
            bullets.add(bullet);
        }
    }

    public void activateSlowMotion() {
        timeManager.activateSlowMotion();
    }

    public void setPlayerMovement(double dx, double dy) {
        player.setMovement(dx, dy);
    }

    public void setPlayerAim(double mouseX, double mouseY) {
        player.setAimAngle(mouseX, mouseY);
    }

    // Getters
    public Player getPlayer() { return player; }
    public List<Enemy> getEnemies() { return enemies; }
    public List<Bullet> getBullets() { return bullets; }
    public WaveManager getWaveManager() { return waveManager; }
    public ScoreManager getScoreManager() { return scoreManager; }
    public TimeManager getTimeManager() { return timeManager; }
    public int getLives() { return lives; }
    public boolean isGameOver() { return gameOver; }

    public void reset() {
        player = new Player(width, height);
        enemies.clear();
        bullets.clear();
        waveManager = new WaveManager();
        scoreManager.reset();
        timeManager = new TimeManager();
        lives = 3;
        gameOver = false;
    }
}
