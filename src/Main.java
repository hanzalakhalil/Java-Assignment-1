// Name: Hanzala khalil
// ID: 2025400279
public class Main{

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
    static final int BULLET_HEIGHT = 22;
    static final int BULLET_SPEED = 10;

    static final int HALF_BULLET_WIDTH = BULLET_WIDTH / 2;
    static final int HALF_BULLET_HEIGHT = BULLET_HEIGHT / 2;

    static final int ENEMY_SHOOT_COOLDOWN = 30;
    static final int INTERCEPTOR_SHOOT_COOLDOWN = 15;

    static final double ENEMY_SHOOT_CHANCE = 0.001;
    static final double LIFE_DROP_CHANCE = 0.20;

    static int[] enemyShootCounter = { 0, 0, 0, 0, 0, 0, 0, 0 };
    static int interceptorShootCounter = 0;

    static int maxBullets = 50;
    static double[] bulletX = new double[maxBullets];
    static double[] bulletY = new double[maxBullets];
    static boolean[] bulletActive = new boolean[maxBullets];
    static boolean[] bulletDirection = new boolean[maxBullets];

    static double[] lifeDropX = new double[5];
    static double[] lifeDropY = new double[5];
    static boolean[] lifeDropActive = new boolean[5];

    static double[] enemyX = { 90.0, 230.0, 370.0, 510.0, 90.0, 230.0, 370.0, 510.0 };
    static double[] enemyY = { 720.0, 720.0, 720.0, 720.0, 620.0, 620.0, 620.0, 620.0 };
    static boolean[] enemyActive = { true, true, true, true, true, true, true, true };
    static boolean[] enemyDirection = { true, true, true, true, true, true, true, true };

    static double[] explosionX = new double[10];
    static double[] explosionY = new double[10];
    static int[] explosionTimer = new int[10];
    static boolean[] explosionActive = new boolean[10];

    static int interceptorX = 300;
    static int interceptorY = 200;

    static int FPS = 20;
    static double speed = 20.0;
    static int lives = 3;
    static int score = 0;

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
            StdDraw.picture(300, 550, "../assets/title.png", 400, 166);

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
        boolean isPaused = false;

        while (true) {

            if (lives <= 0) {
                endGameMenu("gameOver");
                return;
            } else if (score == 240) {
                endGameMenu("victory");
                return;
            }

            StdDraw.clear();

            drawUI();

            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (isPaused) {
                    if (key == '\n') {
                        resetGame();
                        menu();
                    }
                }
                if (key == 'p' || key == 'P') {
                    isPaused = !isPaused;
                }
            }

            if (!isPaused) {
                enemyMovement();

                for (int i = 0; i < 10; i++) {
                    if (explosionActive[i]) {
                        if (explosionTimer[i] < 5) {
                            StdDraw.picture(explosionX[i], explosionY[i], "../assets/explosionSmall.png", 60, 60);

                        } else if (explosionTimer[i] < 10) {
                            StdDraw.picture(explosionX[i], explosionY[i], "../assets/explosionBig.png", 80, 80);
                            if (explosionTimer[i] == 9) {
                                explosionActive[i] = false;
                            }

                        }
                        explosionTimer[i]++;

                    }
                }

                StdDraw.picture(interceptorX, interceptorY, "../assets/interceptor.png", INTERCEPTOR_WIDTH,
                        INTERCEPTOR_HEIGHT);

                bulletMovement();

                lifeDropMovement();

                interceptorMovement();

                checkCollisions();

            } else {
                StdDraw.picture(300, 400, "../assets/paused.png", 429, 98);
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 30));
                StdDraw.text(300, 300, "> Main Menu <");
            }

            StdDraw.show();

            StdDraw.pause(1000 / FPS);
        }

    }

    public static void drawUI() {
        StdDraw.picture(300, 400, "../assets/background.png", CANVAS_WIDTH, CANVAS_HEIGHT);

        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 20));
        StdDraw.text(80, 770, "Score: " + score);

        for (int i = 0; i < lives; i++) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.picture(570 - (i * 40), 770, "../assets/heart.png", 30, 30);
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
                    if (Math.random() < ENEMY_SHOOT_CHANCE && enemyShootCounter[i] == 0) {

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
                    bulletY[i] += BULLET_SPEED;
                    if (bulletY[i] > 800) {
                        bulletActive[i] = false;
                    }

                } else {
                    StdDraw.picture(bulletX[i], bulletY[i], "../assets/enemyBullet.png", BULLET_WIDTH,
                            BULLET_HEIGHT);
                    bulletY[i] -= BULLET_SPEED;
                    if (bulletY[i] < 0) {
                        bulletActive[i] = false;
                    }
                }

            }
        }
    }

    public static void lifeDropMovement() {
        for (int i = 0; i < lifeDropActive.length; i++) {
            if (lifeDropActive[i]) {
                StdDraw.picture(lifeDropX[i], lifeDropY[i], "../assets/heart.png", 30, 30);
                lifeDropY[i] -= 5;

                if (lifeDropX[i] > interceptorX - HALF_INTERCEPTOR_WIDTH &&
                        lifeDropX[i] < interceptorX + HALF_INTERCEPTOR_WIDTH &&
                        lifeDropY[i] > interceptorY - HALF_INTERCEPTOR_HEIGHT &&
                        lifeDropY[i] < interceptorY + HALF_INTERCEPTOR_HEIGHT) {

                    if (lives < 5)
                        lives++;
                    lifeDropActive[i] = false;
                }

                if (lifeDropY[i] < 0)
                    lifeDropActive[i] = false;
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
                                for (int l = 0; l < 10; l++) {
                                    if (!explosionActive[l]) {
                                        explosionActive[l] = true;
                                        explosionX[l] = enemyX[j];
                                        explosionY[l] = enemyY[j];
                                        explosionTimer[l] = 0;
                                        break;
                                    }
                                }
                                score += 30;

                                if (Math.random() < LIFE_DROP_CHANCE) {
                                    for (int k = 0; k < lifeDropActive.length; k++) {
                                        if (!lifeDropActive[k]) {
                                            lifeDropX[k] = enemyX[j];
                                            lifeDropY[k] = enemyY[j] - HALF_ENEMY_HEIGHT;
                                            lifeDropActive[k] = true;
                                            break;
                                        }
                                    }
                                }
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
                            continue;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < 8; i++) {
            if (enemyActive[i]) {
                if (interceptorX + HALF_INTERCEPTOR_WIDTH > enemyX[i] - HALF_ENEMY_WIDTH &&
                        interceptorX - HALF_INTERCEPTOR_WIDTH < enemyX[i] + HALF_ENEMY_WIDTH &&
                        interceptorY + HALF_INTERCEPTOR_HEIGHT > enemyY[i] - HALF_ENEMY_HEIGHT &&
                        interceptorY - HALF_INTERCEPTOR_HEIGHT < enemyY[i] + HALF_ENEMY_HEIGHT) {

                    enemyActive[i] = false;
                    lives--;
                    for (int l = 0; l < 10; l++) {
                        if (!explosionActive[l]) {
                            explosionActive[l] = true;
                            explosionX[l] = enemyX[i];
                            explosionY[l] = enemyY[i];
                            explosionTimer[l] = 0;
                            break;
                        }
                    }
                    score += 30;

                    if (Math.random() < LIFE_DROP_CHANCE) {
                        for (int k = 0; k < lifeDropActive.length; k++) {
                            if (!lifeDropActive[k]) {
                                lifeDropX[k] = enemyX[i];
                                lifeDropY[k] = enemyY[i] - HALF_ENEMY_HEIGHT;
                                lifeDropActive[k] = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

    }

    public static void endGameMenu(String result) {
        boolean isRestartSelected = true; // Highlighting 'Restart' by default

        while (true) {
            StdDraw.clear();
            StdDraw.picture(300, 400, "../assets/background.png", CANVAS_WIDTH, CANVAS_HEIGHT);

            // 1. Draw the Title (Victory or Game Over)
            if (result.equals("gameOver")) {
                StdDraw.picture(300, 550, "../assets/gameOver.png", 400, 210);
            } else if (result.equals("victory")) {
                StdDraw.picture(300, 550, "../assets/victory.png", 400, 86);
            }

            // 2. Display the Final Score
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 30));
            StdDraw.text(300, 420, "Score: " + score);

            // 3. Draw Navigation Options
            StdDraw.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 25));

            // Restart Option
            String restartText = isRestartSelected ? "> Restart <" : "Restart";
            StdDraw.text(300, 350, restartText);

            // End Game Option
            String endText = !isRestartSelected ? "> End Game <" : "End Game";
            StdDraw.text(300, 300, endText);

            StdDraw.show();

            // 4. Handle Navigation Input (Arrow Keys)
            if (StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_UP) ||
                    StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_DOWN)) {
                isRestartSelected = !isRestartSelected;
            }

            // 5. Handle Selection Input (Enter Key)
            if (StdDraw.hasNextKeyTyped()) {
                if (StdDraw.nextKeyTyped() == '\n') {
                    if (isRestartSelected) {
                        resetGame();
                        playGame();
                        return;
                    } else {
                        System.exit(0);
                    }
                }
            }
            StdDraw.pause(100); // Prevents rapid flickering

        }
    }

    public static void resetGame() {
        lives = 3;
        score = 0;
        interceptorX = 300;
        interceptorY = 200;

        // Reset Enemies to their starting positions and make them active
        double[] startX = { 90.0, 230.0, 370.0, 510.0, 90.0, 230.0, 370.0, 510.0 };
        double[] startY = { 720.0, 720.0, 720.0, 720.0, 620.0, 620.0, 620.0, 620.0 };

        for (int i = 0; i < 8; i++) {
            enemyX[i] = startX[i];
            enemyY[i] = startY[i];
            enemyActive[i] = true;
            enemyDirection[i] = true;
            enemyShootCounter[i] = 0;
        }

        // Clear all bullets, life drops, and explosions
        for (int i = 0; i < maxBullets; i++)
            bulletActive[i] = false;
        for (int i = 0; i < 5; i++)
            lifeDropActive[i] = false;
        for (int i = 0; i < 10; i++)
            explosionActive[i] = false;
    }
}
