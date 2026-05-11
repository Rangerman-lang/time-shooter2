package com.timeshooter;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;

/**
 * Main game window and rendering engine
 */
public class GameWindow {
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 700;
    private static final int FPS = 60;

    private Canvas canvas;
    private GraphicsContext gc;
    private GameManager gameManager;
    private Set<KeyCode> pressedKeys;
    private double mouseX = WIDTH / 2;
    private double mouseY = HEIGHT / 2;
    private long lastFrameTime = System.nanoTime();

    public GameWindow(Stage stage) {
        // Setup canvas
        canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();

        // Setup scene
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Time Shooter 2");
        stage.show();

        // Initialize game
        gameManager = new GameManager(WIDTH, HEIGHT);
        pressedKeys = new HashSet<>();

        // Input handling
        scene.setOnKeyPressed(this::handleKeyPressed);
        scene.setOnKeyReleased(this::handleKeyReleased);
        scene.setOnMouseMoved(this::handleMouseMoved);
        scene.setOnMouseClicked(this::handleMouseClicked);

        // Game loop
        startGameLoop();
    }

    private void handleKeyPressed(KeyEvent event) {
        pressedKeys.add(event.getCode());

        if (event.getCode() == KeyCode.SPACE) {
            gameManager.activateSlowMotion();
        }
    }

    private void handleKeyReleased(KeyEvent event) {
        pressedKeys.remove(event.getCode());
    }

    private void handleMouseMoved(MouseEvent event) {
        mouseX = event.getX();
        mouseY = event.getY();
        gameManager.setPlayerAim(mouseX, mouseY);
    }

    private void handleMouseClicked(MouseEvent event) {
        gameManager.playerShoot();
    }

    private void updateInput() {
        double dx = 0, dy = 0;

        if (pressedKeys.contains(KeyCode.W) || pressedKeys.contains(KeyCode.UP)) dy -= 1;
        if (pressedKeys.contains(KeyCode.S) || pressedKeys.contains(KeyCode.DOWN)) dy += 1;
        if (pressedKeys.contains(KeyCode.A) || pressedKeys.contains(KeyCode.LEFT)) dx -= 1;
        if (pressedKeys.contains(KeyCode.D) || pressedKeys.contains(KeyCode.RIGHT)) dx += 1;

        gameManager.setPlayerMovement(dx, dy);
    }

    private void startGameLoop() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double deltaTime = (now - lastFrameTime) / 1_000_000_000.0;
                lastFrameTime = now;

                update(deltaTime);
                render();
            }
        };
        timer.start();
    }

    private void update(double deltaTime) {
        updateInput();
        gameManager.update(deltaTime);
    }

    private void render() {
        // Clear background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw grid
        gc.setStroke(Color.color(0.2, 0.2, 0.2));
        gc.setLineWidth(0.5);
        int gridSize = 40;
        for (int x = 0; x < WIDTH; x += gridSize) {
            gc.strokeLine(x, 0, x, HEIGHT);
        }
        for (int y = 0; y < HEIGHT; y += gridSize) {
            gc.strokeLine(0, y, WIDTH, y);
        }

        // Render game entities
        gameManager.getPlayer().render(gc);

        for (Enemy enemy : gameManager.getEnemies()) {
            if (enemy.isActive()) {
                enemy.render(gc);
            }
        }

        for (Bullet bullet : gameManager.getBullets()) {
            if (bullet.isActive()) {
                bullet.render(gc);
            }
        }

        // Render time slow-down overlay
        if (gameManager.getTimeManager().isSlowMotionActive()) {
            gc.setFill(Color.color(0, 1, 1, 0.1));
            gc.fillRect(0, 0, WIDTH, HEIGHT);
        }

        // Render UI
        renderUI();

        // Render game over screen
        if (gameManager.isGameOver()) {
            renderGameOver();
        }
    }

    private void renderUI() {
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 24));

        int score = gameManager.getScoreManager().getScore();
        gc.fillText("Score: " + score, 20, 40);

        int wave = gameManager.getWaveManager().getCurrentWave();
        gc.fillText("Wave: " + wave, 20, 80);

        int lives = gameManager.getLives();
        gc.fillText("Lives: " + lives, 20, 120);

        TimeManager timeManager = gameManager.getTimeManager();
        String slowMoStatus = timeManager.isSlowMotionActive() ? "ACTIVE" : "READY";
        Color slowMoColor = timeManager.isSlowMotionActive() ? Color.CYAN : Color.WHITE;
        gc.setFill(slowMoColor);
        gc.fillText("Time Slow: " + slowMoStatus, WIDTH - 300, 40);

        // Draw cooldown bar
        double cooldownProgress = timeManager.getCooldownProgress();
        gc.setFill(Color.color(0.2, 0.2, 0.2));
        gc.fillRect(WIDTH - 300, 60, 200, 20);
        gc.setFill(Color.CYAN);
        gc.fillRect(WIDTH - 300, 60, 200 * cooldownProgress, 20);

        gc.setStroke(Color.CYAN);
        gc.setLineWidth(1);
        gc.strokeRect(WIDTH - 300, 60, 200, 20);
    }

    private void renderGameOver() {
        gc.setFill(Color.color(0, 0, 0, 0.7));
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        gc.setFill(Color.RED);
        gc.setFont(new Font("Arial", 48));
        gc.fillText("GAME OVER", WIDTH / 2 - 150, HEIGHT / 2 - 50);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 32));
        int finalScore = gameManager.getScoreManager().getScore();
        gc.fillText("Final Score: " + finalScore, WIDTH / 2 - 150, HEIGHT / 2 + 50);
    }

    public void show() {
        // Window is shown by the Stage
    }
}
