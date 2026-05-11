package com.timeshooter;

/**
 * Manages waves and enemy spawning
 */
public class WaveManager {
    private int currentWave = 1;
    private double waveTimer = 0;
    private static final double WAVE_DURATION = 30.0;
    private double spawnCooldown = 0;
    private double spawnRate = 1.0; // enemies per second

    public void update(double deltaTime) {
        waveTimer += deltaTime;
        spawnCooldown -= deltaTime;

        // Progress to next wave
        if (waveTimer >= WAVE_DURATION) {
            nextWave();
        }
    }

    public boolean shouldSpawnEnemy() {
        if (spawnCooldown <= 0) {
            spawnCooldown = 1.0 / spawnRate;
            return true;
        }
        return false;
    }

    private void nextWave() {
        currentWave++;
        waveTimer = 0;
        spawnRate = 1.0 + (currentWave * 0.3); // Increase spawn rate per wave
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public double getWaveProgress() {
        return waveTimer / WAVE_DURATION;
    }

    public void reset() {
        currentWave = 1;
        waveTimer = 0;
        spawnCooldown = 0;
        spawnRate = 1.0;
    }
}
