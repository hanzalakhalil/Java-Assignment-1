// Name: Hanzala khalil
// ID: 2025400279

import java.util.Random;

public class HanzalaKhalil {

    static final int CANVAS_WIDTH = 600;
    static final int CANVAS_HEIGHT = 800;

    static final int INTERCEPTOR_WIDTH = 80;
    static final int INTERCEPTOR_HEIGHT = 80;

    static final int HALF_INTERCEPTOR_WIDTH = INTERCEPTOR_WIDTH / 2;
    static final int HALF_INTERCEPTOR_HEIGHT = INTERCEPTOR_HEIGHT / 2;

    static final int ENEMY_WIDTH = 80;
    static final int ENEMY_HEIGHT = 80;

    static final int HALF_ENEMY_WIDTH = ENEMY_WIDTH / 2;
    static final int HALF_ENEMY_HEIGHT = ENEMY_HEIGHT / 2;

    static final double ENEMY_SPEED = 3.0;

    static final int BULLET_WIDTH = 5;
    static final int BULLET_HEIGHT = 20;

    static final int HALF_BULLET_WIDTH = BULLET_WIDTH / 2;
    static final int HALF_BULLET_HEIGHT = BULLET_HEIGHT / 2;

    static final int ENEMY_SHOOT_COOLDOWN = 30;
    static final int INTERCEPTOR_SHOOT_COOLDOWN = 15;

    static int[] enemyShootCounter = { 0, 0, 0, 0, 0, 0, 0, 0 };
    static int interceptorShootCounter = 0;

    static int maxBullets = 50;
    static double[] bulletX = new double[maxBullets];
    static double[] bulletY = new double[maxBullets];
    static boolean[] bulletActive = new boolean[maxBullets];
    static boolean[] bulletDirection = new boolean[maxBullets];

    static double[] enemyX = { 90.0, 230.0, 370.0, 510.0, 90.0, 230.0, 370.0, 510.0 };
    static double[] enemyY = { 720.0, 720.0, 720.0, 720.0, 620.0, 620.0, 620.0, 620.0 };
    static boolean[] enemyActive = { true, true, true, true, true, true, true, true };
    static boolean[] enemyDirection = { true, true, true, true, true, true, true, true };

    static int interceptorX = 300;
    static int interceptorY = 200;

    static int FPS = 20;
    static double speed = 20.0;
    static int lives = 3;

    public static void main(String[] args) {

        StdDraw.setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        StdDraw.setXscale(0, CANVAS_WIDTH);
        StdDraw.setYscale(0, CANVAS_HEIGHT);
        StdDraw.setTitle("2042: Interceptor");
        StdDraw.enableDoubleBuffering();
        menu();

    }

    public static void menu() {
        char typedKey;

        while (true) {

            StdDraw.clear();
            StdDraw.picture(300, 400, "../assets/background.png", CANVAS_WIDTH, CANVAS_HEIGHT);
            StdDraw.picture(300, 550, "../assets/title.png", 400, 133);
            StdDraw.setPenColor(StdDraw.LIGHT_GRAY);

            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 30));
            StdDraw.text(300, 350, "> Start Game <");

            StdDraw.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 20));
            StdDraw.text(300, 300, String.format("FPS: %d | Speed: %.1f", FPS, speed));

            StdDraw.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 14));
            StdDraw.text(300, 100, "Move: [←] , [→] , [↑] , [↓] | Shoot: [Space]");
            StdDraw.text(300, 75, "Press [ENTER] to start");
            StdDraw.text(300, 50, "FPS: A/D | Speed: Q/E");

            StdDraw.picture(interceptorX, interceptorY, "../assets/interceptor.png", INTERCEPTOR_WIDTH,
                    INTERCEPTOR_HEIGHT);

            bulletMovement();

            StdDraw.show();
            StdDraw.pause(1000 / FPS);

            if (StdDraw.hasNextKeyTyped()) {
                typedKey = StdDraw.nextKeyTyped();
                switch (typedKey) {
                    case '\n':
                        playGame();
                        break;
                    case 'a', 'A':
                        if (FPS > 5) {
                            FPS -= 5;
                        }
                        break;
                    case 'd', 'D':
                        if (FPS < 60) {
                            FPS += 5;
                        }
                        break;
                    case 'q', 'Q':
                        if (speed > 1.0) {
                            speed--;
                        }
                        break;
                    case 'e', 'E':
                        if (speed < 80.0) {
                            speed++;
                        }
                        break;
                    default:
                        break;
                }
            }

            interceptorMovement();

        }

    }

    public static void playGame() {
        while (true) {

            StdDraw.clear();
            StdDraw.picture(300, 400, "../assets/background.png", CANVAS_WIDTH, CANVAS_HEIGHT);

            enemyMovement();

            for (int i = 0; i < lives; i++) {
                StdDraw.setPenColor(StdDraw.RED);
                StdDraw.picture(570 - (i * 40), 770, "../assets/heart.png", 30, 30);
            }

            StdDraw.picture(interceptorX, interceptorY, "../assets/interceptor.png", INTERCEPTOR_WIDTH,
                    INTERCEPTOR_HEIGHT);

            bulletMovement();

            StdDraw.show();
            StdDraw.pause(1000 / FPS);

            interceptorMovement();

            checkCollisions();

        }

    }

    public static void interceptorMovement() {

        if (interceptorShootCounter > 0) {
            interceptorShootCounter--;
        }

        if (StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_UP) && interceptorY + (INTERCEPTOR_HEIGHT / 2) < 800) {
            interceptorY += speed;
            if (interceptorY + (INTERCEPTOR_HEIGHT / 2) > 800) {
                interceptorY = 800 - (INTERCEPTOR_HEIGHT / 2);
            }
        }
        if (StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_DOWN) && interceptorY - (INTERCEPTOR_HEIGHT / 2) > 0) {
            interceptorY -= speed;
            if (interceptorY - (INTERCEPTOR_HEIGHT / 2) < 0) {
                interceptorY = (INTERCEPTOR_HEIGHT / 2);
            }
        }
        if (StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_RIGHT) && interceptorX + (INTERCEPTOR_WIDTH / 2) < 600) {
            interceptorX += speed;
            if (interceptorX + (INTERCEPTOR_WIDTH / 2) > 600) {
                interceptorX = 600 - (INTERCEPTOR_WIDTH / 2);
            }
        }
        if (StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_LEFT) && interceptorX - (INTERCEPTOR_WIDTH / 2) > 0) {
            interceptorX -= speed;
            if (interceptorX - (INTERCEPTOR_WIDTH / 2) < 0) {
                interceptorX = (INTERCEPTOR_WIDTH / 2);
            }
        }

        if (StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_SPACE) && interceptorShootCounter == 0) {
            for (int i = 0; i < maxBullets; i++) {
                if (!bulletActive[i]) {
                    bulletActive[i] = true;
                    bulletX[i] = interceptorX;
                    bulletY[i] = interceptorY + HALF_INTERCEPTOR_HEIGHT;
                    bulletDirection[i] = true;
                    interceptorShootCounter = INTERCEPTOR_SHOOT_COOLDOWN;
                    break;
                }
            }

        }
    }

    public static void enemyMovement() {
        for (int i = 0; i < 8; i++) {
            if (enemyShootCounter[i] > 0) {
                enemyShootCounter[i]--;
            }

            if (enemyActive[i]) {
                StdDraw.picture(enemyX[i], enemyY[i], "../assets/enemyFighter.png", ENEMY_WIDTH, ENEMY_HEIGHT);

                if (i == 3 || i == 7) {
                    if (enemyDirection[i]) {
                        enemyX[i] += ENEMY_SPEED;
                    } else {
                        enemyX[i] -= ENEMY_SPEED;
                    }

                }

                if (enemyX[i] + (ENEMY_WIDTH / 2) >= 600) {
                    enemyX[i] = 600 - (ENEMY_WIDTH / 2);
                    if (i < 4) {
                        for (int j = 0; j < 4; j++) {
                            enemyDirection[j] = false;
                        }
                    } else {
                        for (int j = 4; j < 8; j++) {
                            enemyDirection[j] = false;
                        }
                    }

                } else if (enemyX[i] - (ENEMY_WIDTH / 2) <= 0) {
                    enemyX[i] = (ENEMY_WIDTH / 2);
                    if (i < 4) {
                        for (int j = 0; j < 4; j++) {
                            enemyDirection[j] = true;
                        }
                    } else {
                        for (int j = 4; j < 8; j++) {
                            enemyDirection[j] = true;
                        }
                    }
                }

                if (i != 3 && i != 7) {
                    if (enemyDirection[i]) {
                        enemyX[i] += ENEMY_SPEED;
                    } else {
                        enemyX[i] -= ENEMY_SPEED;
                    }

                }
                for (int j = 0; j < maxBullets; j++) {
                    if (Math.random() < 0.001 && enemyShootCounter[i] == 0) {

                        if (!bulletActive[j]) {
                            bulletActive[j] = true;
                            bulletX[j] = enemyX[i];
                            bulletY[j] = enemyY[i] - HALF_ENEMY_HEIGHT;
                            bulletDirection[j] = false;
                            enemyShootCounter[i] = ENEMY_SHOOT_COOLDOWN;
                        }
                    }
                }

            }
        }
    }

    public static void bulletMovement() {

        for (int i = 0; i < maxBullets; i++) {
            if (bulletActive[i]) {

                if (bulletDirection[i]) {
                    StdDraw.picture(bulletX[i], bulletY[i], "../assets/bullet.png", BULLET_WIDTH, BULLET_HEIGHT);
                    bulletY[i] += BULLET_HEIGHT;
                    if (bulletY[i] > 800) {
                        bulletActive[i] = false;
                    }

                } else {
                    StdDraw.picture(bulletX[i], bulletY[i], "../assets/enemyBullet.png", BULLET_WIDTH,
                            BULLET_HEIGHT);
                    bulletY[i] -= BULLET_HEIGHT;
                    if (bulletY[i] < 0) {
                        bulletActive[i] = false;
                    }
                }

            }
        }
    }

    public static void checkCollisions() {
        for (int i = 0; i < maxBullets; i++) {
            if (bulletActive[i]) {
                if (bulletDirection[i]) {
                    for (int j = 0; j < 8; j++) {
                        if (enemyActive[j]) {
                            if (bulletX[i] + HALF_BULLET_WIDTH > enemyX[j] - HALF_ENEMY_WIDTH &&
                                    bulletX[i] - HALF_BULLET_WIDTH < enemyX[j] + HALF_ENEMY_WIDTH &&
                                    bulletY[i] + HALF_BULLET_HEIGHT > enemyY[j] - HALF_ENEMY_HEIGHT &&
                                    bulletY[i] - HALF_BULLET_HEIGHT < enemyY[j] + HALF_ENEMY_HEIGHT) {

                                bulletActive[i] = false;
                                enemyActive[j] = false;
                                System.out.println("BOOM");
                            }
                        }
                    }
                } else {
                    if (bulletActive[i]) {
                        if (bulletX[i] + HALF_BULLET_WIDTH > interceptorX - HALF_INTERCEPTOR_WIDTH &&
                                bulletX[i] - HALF_BULLET_WIDTH < interceptorX + HALF_INTERCEPTOR_WIDTH &&
                                bulletY[i] + HALF_BULLET_HEIGHT > interceptorY - HALF_INTERCEPTOR_HEIGHT &&
                                bulletY[i] - HALF_BULLET_HEIGHT < interceptorY + HALF_INTERCEPTOR_HEIGHT) {

                            bulletActive[i] = false;
                            lives--;
                            System.out.println("Ouch! Lives left: " + lives);
                            continue;
                        }
                    }
                }
            }
        }
    }
}
