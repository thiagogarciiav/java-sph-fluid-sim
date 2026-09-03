package com.example.sphsimulator;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import java.io.IOException;

public class SPHSimulator extends Application {

    // ================= CONSTANTES FÍSICAS =================
    private static final int num_particles = 800;
    private static final double width = 600;
    private static final double height = 400;
    private static final double gravity = -300.0;
    private static final double rest_density = 1000.0;
    private static final double stiffness = 2000.0; // k (rigidez)
    private static final double viscosity = 50.0;
    private static final double dt = 0.0025; //passo de tempo
    private static final double h = 18.0; // smoothing lenght
    private static final double mass = 0.02;
    private static final double cell_size = h *2;

    // ================= ESTRUTURA DE DADOS =================
    private static class Particle {
        double x, y, vx, vy, ax, ay;
        double rho, pressure;
        Particle(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    private final List<Particle> particles = new ArrayList<>();
    private final Map<Long, List<Integer>> grid = new HashMap<>();

    // ================= KERNELS (Matemática) =================
    private double poly6Kernel(double r2) {
        if (r2 >= h * h) return 0;
        double r = Math.sqrt(r2);
        double q = 1 - (r/h);
        return (315.0 / (64.0 * Math.PI * Math.pow(h, 9))) * Math.pow(q, 3) * (r2 + 3 * r * h + h * h);
    }

    private double spikyGradient(double r2, double dx, double dy) {
        if (r2 >= h * h || r2 == 0) return 0;
        double r = Math.sqrt(r2);
        double factor = (-45.0 / (Math.PI * Math.pow(h, 6))) * Math.pow((h - r), 2) / r;
        return factor;
    }

    // ================= SPATIAL HASHING =================
    private long hash(int x, int y) {
        return ((long) x * 73856093) ^ ((long) y * 19349663);
    }

    private void buildGrid() {
        grid.clear();
        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);
            int gx = (int) Math.floor(p.x / cell_size);
            int gy = (int) Math.floor(p.y / cell_size);
            long key = hash(gx, gy);
            grid.computeIfAbsent(key, k -> new ArrayList<>()).add(i);
        }
    }

    private List<Integer> getNeighbors(Particle p) {
        List<Integer> neighbors = new ArrayList<>();
        int gx = (int) Math.floor(p.x / cell_size);
        int gy = (int) Math.floor(p.y / cell_size);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                long key = hash(gx + dx, gy + dy);
                List<Integer> cell = grid.get(key);
                if (cell != null) {
                    for (int idx : cell) {
                        Particle q = particles.get(idx);
                        double dx2 = p.x = q.x;
                        double dy2 = p.y - q.y;
                        if(dx2 * dx2 + dy2 * dy2 < h * h) {
                            neighbors.add(idx);
                        }
                    }
                }
            }
        }
        return neighbors;
    }

    // ================= FÍSICA SPH =================
    private void computeDensityAndPressure() {
        for (Particle p : particles) {
            p.rho = 0;
            List<Integer> neighbors = getNeighbors(p);
            for (int idx : neighbors) {
                Particle q = particles.get(idx);
                double dx = p.x - q.x;
                double dy = p.y - q.y;
                p.rho += mass * poly6Kernel(dx * dx + dy * dy);
            }
            // Correção para bordas (evita densidade zero)
            p.rho = Math.max(p.rho, rest_density * 0.5);

            // Equação de estado (Tait)
            p.pressure = stiffness * (Math.pow(p.rho / rest_density, 7) - 1);
        }
    }

    private void computeForces() {
        // Zera acelerações
        for (Particle p : particles) { p.ax = 0; p.ay = gravity; }

        for (Particle p : particles) {
            List<Integer> neighbors = getNeighbors(p);
            for (int idx : neighbors) {
                if (idx == particles.indexOf(p)) continue;
                Particle q = particles.get(idx);
                double dx = p.x - q.x;
                double dy = p.y - q.y;
                double r2 = dx * dx + dy * dy;
                if (r2 == 0 || r2 >= h * h) continue;

                // Força de Pressão (usando gradiente do kernel Spiky)
                double grad = spikyGradient(r2, dx, dy);
                double pressureForce = -(p.pressure + q.pressure) / (2 * q.rho) * mass;
                p.ax += pressureForce * grad * dx;
                p.ay += pressureForce * grad * dy;

                // Força de Viscosidade (Laplaciano do Poly6)
                double lap = (40.0 / (Math.PI * Math.pow(h, 8))) * (h * h - r2);
                double viscForce = viscosity * (q.vx - p.vx) / q.rho * mass * lap;
                p.ax += viscForce;
                p.ay += viscForce; // simplificado para 2D
            }
        }
    }

    private void integrate() {
        for (Particle p : particles) {
            p.vx += p.ax * dt;
            p.vy += p.ay * dt;
            p.x += p.vx * dt;
            p.y += p.vy * dt;

            // Colisão com paredes (elástica com amortecimento)
            double damping = 0.4;
            if (p.x < h) { p.x = h; p.vx = -p.vx * damping; }
            if (p.x > width - h) { p.x = width - h; p.vx = -p.vx * damping; }
            if (p.y < h) { p.y = h; p.vy = -p.vy * damping; }
            if (p.y > height - h) { p.y = height - h; p.vy = -p.vy * damping; }
        }
    }

    // ================= INICIALIZAÇÃO =================
    private void initParticles() {
        int cols = 30;
        int rows = 25;
        for (int i = 0; i < rows && particles.size() < num_particles; i++) {
            for (int j = 0; j < cols && particles.size() < num_particles; j++) {
                double x = 50 + j * 12;
                double y = 50 + i * 12;
                particles.add(new Particle(x, y));
            }
        }
    }

    // ================= RENDERIZAÇÃO JAVAFX =================
    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        initParticles();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                // 1. Física
                buildGrid();
                computeDensityAndPressure();
                computeForces();
                integrate();

                // 2. Render
                gc.setFill(Color.rgb(20, 25, 45));
                gc.fillRect(0, 0, width, height);

                // Desenha as partículas com gradiente de cor pela pressão
                for (Particle p : particles) {
                    double hue = 220 - (p.pressure / 5000) * 200;
                    hue = Math.max(0, Math.min(220, hue));
                    gc.setFill(Color.hsb(hue, 0.9, 0.9));
                    double radius = 4.0;
                    gc.fillOval(p.x - radius, p.y - radius, radius * 2, radius * 2);
                }

                // Estatísticas no canto
                gc.setFill(Color.WHITE);
                gc.fillText("Partículas: " + particles.size(), 10, 20);
                gc.fillText("Pressão média: " + (int) particles.stream().mapToDouble(p -> p.pressure).average().orElse(0), 10, 40);
            }
        }.start();

        stage.setScene(new Scene(new StackPane(canvas)));
        stage.setTitle("SPH Simulator 2D - Java");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
