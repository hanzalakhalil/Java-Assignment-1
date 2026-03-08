// Name: Hanzala khalil
// ID: 2025400279

/**
 * 2042: Interceptor - A space shooter game developed using StdDraw.
 * Features player movement, enemy AI, bullet physics, and life-drop mechanics.
 * * @author Hanzala khalil
 * 
 * @id 2025400279
 */
public class Main {

    // --- Global Game Settings ---
    static int FPS = 20;
    static double speed = 20.0; // Pixels the player moves per frame
    static int lives = 3;
    static int score = 0;

    // --- Canvas and Screen Settings ---
    static final int CANVAS_WIDTH = 600;
    static final int CANVAS_HEIGHT = 800;

    // --- Player (Interceptor) Dimensions ---
    static final int INTERCEPTOR_WIDTH = 80;
    static final int INTERCEPTOR_HEIGHT = 80;
    static final int HALF_INTERCEPTOR_WIDTH = INTERCEPTOR_WIDTH / 2;
    static final int HALF_INTERCEPTOR_HEIGHT = INTERCEPTOR_HEIGHT / 2;

    // --- Enemy Dimensions and Physics ---
    static final int ENEMY_WIDTH = 80;
    static final int ENEMY_HEIGHT = 80;
    static final int HALF_ENEMY_WIDTH = ENEMY_WIDTH / 2;
    static final int HALF_ENEMY_HEIGHT = ENEMY_HEIGHT / 2;
    static final double BASE_ENEMY_SPEED = 4.0;

    // --- Projectile (Bullet) Settings ---
    static final int BULLET_WIDTH = 5;
    static final int BULLET_HEIGHT = 22;
    static final double BASE_BULLET_SPEED = 10.0;
    static final int HALF_BULLET_WIDTH = BULLET_WIDTH / 2;
    static final int HALF_BULLET_HEIGHT = BULLET_HEIGHT / 2;

    // --- Gameplay Balancing and Cooldowns ---
    static final double ENEMY_SHOOT_COOLDOWN = 1.0; // Cooldown in seconds
    static final double INTERCEPTOR_SHOOT_COOLDOWN = 0.75; // Cooldown in seconds
    static final double TARGET_SHOOT_CHANCE_PER_SEC = 0.2; // Adjust this for difficulty
    static final double LIFE_DROP_CHANCE = 0.20; // 20% chance to drop a heart on enemy death


    // --- State Counters ---
    static int[] enemyShootCounter = { 0, 0, 0, 0, 0, 0, 0, 0 };
    static int interceptorShootCounter = 0;

    // --- Bullet Arrays (Object Pooling Pattern) ---
    // Uses pre-allocated arrays to manage bullets without constant memory
    // allocation
    static int maxBullets = 50;
    static double[] bulletX = new double[maxBullets];
    static double[] bulletY = new double[maxBullets];
    static boolean[] bulletActive = new boolean[maxBullets];
    static boolean[] bulletDirection = new boolean[maxBullets]; // true: Up (Player), false: Down (Enemy)

    // --- Collectible Life Drops ---
    static double[] lifeDropX = new double[5];
    static double[] lifeDropY = new double[5];
    static boolean[] lifeDropActive = new boolean[5];

    // --- Enemy Formation and Movement Data ---
    static double[] enemyX = { 90.0, 230.0, 370.0, 510.0, 90.0, 230.0, 370.0, 510.0 };
    static double[] enemyY = { 720.0, 720.0, 720.0, 720.0, 620.0, 620.0, 620.0, 620.0 };
    static boolean[] enemyActive = { true, true, true, true, true, true, true, true };
    static boolean[] enemyDirection = { true, true, true, true, true, true, true, true };

    // --- Visual Effects (Explosions) ---
    static double[] explosionX = new double[10];
    static double[] explosionY = new double[10];
    static int[] explosionTimer = new int[10];
    static boolean[] explosionActive = new boolean[10];

    // --- Player Instance Variables ---
    static int interceptorX = 300;
    static int interceptorY = 200;

    /**
     * Initializes the game environment and launches the main menu.
     * * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        StdDraw.setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        StdDraw.setXscale(0, CANVAS_WIDTH);
        StdDraw.setYscale(0, CANVAS_HEIGHT);
        StdDraw.setTitle("2042: Interceptor");
        StdDraw.enableDoubleBuffering();
        menu();
    }

    /**
     * Displays the main menu, handles game settings (FPS/Speed), and initiates the
     * game session.
     */
    public static void menu() {
        char typedKey;

        while (true) {
            StdDraw.clear();
            // Render background and static assets
            StdDraw.picture(300, 400, "../assets/background.png", CANVAS_WIDTH, CANVAS_HEIGHT);
            StdDraw.picture(300, 550, "../assets/title.png", 400, 166);

            // Menu Text
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 30));
            StdDraw.text(300, 350, "> Start Game <");

            StdDraw.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 20));
            StdDraw.text(300, 300, String.format("FPS: %d | Speed: %.1f", FPS, speed));

            StdDraw.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 14));
            StdDraw.text(300, 100, "Move: [←] , [→] , [↑] , [↓] | Shoot: [Space]");
            StdDraw.text(300, 75, "Press [ENTER] to start");
            StdDraw.text(300, 50, "FPS: A/D | Speed: Q/E");

            // Preview the player ship
            StdDraw.picture(interceptorX, interceptorY, "../assets/interceptor.png", INTERCEPTOR_WIDTH,
                    INTERCEPTOR_HEIGHT);

            bulletMovement(); // Updates any active bullets from preview

            StdDraw.show();
            StdDraw.pause(1000 / FPS);

            // Input handling for Menu state
            if (StdDraw.hasNextKeyTyped()) {
                typedKey = StdDraw.nextKeyTyped();
                switch (typedKey) {
                    case '\n': // ENTER key to begin
                        resetGame();
                        playGame();
                        break;
                    case 'a', 'A': // Decrease framerate
                        if (FPS > 5) {
                            FPS -= 5;
                        }
                        break;
                    case 'd', 'D': // Increase framerate
                        if (FPS < 60) {
                            FPS += 5;
                        }
                        break;
                    case 'q', 'Q': // Decrease movement sensitivity
                        if (speed > 1.0) {
                            speed--;
                        }
                        break;
                    case 'e', 'E': // Increase movement sensitivity
                        if (speed < 80.0) {
                            speed++;
                        }
                        break;
                    default:
                        break;
                }
            }

            interceptorMovement(); // Allows the user to test controls in the menu
        }
    }

    /**
     * Main game loop handling frame updates, collision logic, and asset rendering.
     */
    public static void playGame() {
        boolean isPaused = false;

        while (true) {
            // Check terminal conditions
            if (lives <= 0) {
                endGameMenu("gameOver");
                return;
            } else if (score == 240) {
                endGameMenu("victory");
                return;
            }

            StdDraw.clear();
            drawUI();

            // Toggle Pause state
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (isPaused) {
                    if (key == '\n') { // ENTER while paused returns to menu
                        resetGame();
                        menu();
                    }
                }
                if (key == 'p' || key == 'P') {
                    isPaused = !isPaused;
                }
            }

            if (!isPaused) {
                // Execute core movement and logic
                enemyMovement();

                // Logic for a ~0.5 second explosion animation
                int halfLife = FPS / 4; 

                // Process and animate active explosions
                for (int i = 0; i < 10; i++) {
                    if (explosionActive[i]) {
                        if (explosionTimer[i] < halfLife) {
                            StdDraw.picture(explosionX[i], explosionY[i], "../assets/explosionSmall.png", 60, 60);
                        } else if (explosionTimer[i] < halfLife*2) {
                            StdDraw.picture(explosionX[i], explosionY[i], "../assets/explosionBig.png", 80, 80);
                            if (explosionTimer[i] == (halfLife*2)-1) {
                                explosionActive[i] = false;
                            }
                        }
                        explosionTimer[i]++;
                    }
                }

                // Render active game objects
                StdDraw.picture(interceptorX, interceptorY, "../assets/interceptor.png", INTERCEPTOR_WIDTH,
                        INTERCEPTOR_HEIGHT);
                bulletMovement();
                lifeDropMovement();
                interceptorMovement();
                checkCollisions();

            } else {
                // Render Pause Screen
                StdDraw.picture(interceptorX, interceptorY, "../assets/interceptor.png", INTERCEPTOR_WIDTH,
                        INTERCEPTOR_HEIGHT);
                for (int i = 0; i < 8; i++) {

                    if (enemyActive[i]) {
                        StdDraw.picture(enemyX[i], enemyY[i], "../assets/enemyFighter.png", ENEMY_WIDTH, ENEMY_HEIGHT);
                    }
                }
                StdDraw.picture(300, 450, "../assets/paused.png", 429, 98);
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 30));
                StdDraw.text(300, 350, "> Main Menu <");
            }

            StdDraw.show();
            StdDraw.pause(1000 / FPS);
        }
    }

    /**
     * Renders the UI elements including score display and life hearts.
     */
    public static void drawUI() {
        StdDraw.picture(300, 400, "../assets/background.png", CANVAS_WIDTH, CANVAS_HEIGHT);

        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 20));
        StdDraw.text(80, 770, "Score: " + score);

        // Draw life hearts in the upper right
        for (int i = 0; i < lives; i++) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.picture(570 - (i * 40), 770, "../assets/heart.png", 30, 30);
        }
    }

    /**
     * Handles keyboard events for player movement and weapon fire.
     */
    public static void interceptorMovement() {
        if (interceptorShootCounter > 0) {
            interceptorShootCounter--;
        }

        // Move ship based on arrow keys with screen boundary checks
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

        // Fire a bullet if Space is pressed and cooldown has elapsed
        if (StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_SPACE) && interceptorShootCounter == 0) {
            for (int i = 0; i < maxBullets; i++) {
                if (!bulletActive[i]) {
                    bulletActive[i] = true;
                    bulletX[i] = interceptorX;
                    bulletY[i] = interceptorY + HALF_INTERCEPTOR_HEIGHT;
                    bulletDirection[i] = true; // Player bullets move UP
                    interceptorShootCounter = (int)(INTERCEPTOR_SHOOT_COOLDOWN * FPS);
                    break;
                }
            }
        }
    }

    /**
     * Updates enemy positions, handles wall-bouncing logic for the formation,
     * and triggers randomized enemy fire.
     */
    public static void enemyMovement() {
        // 1. Sync row-based movement
        boolean row1HitWall = false;
        boolean row2HitWall = false;

        // Calculate normalized speed so they move the same distance per second
        // (BASE_SPEED * 20) / FPS ensures it feels like the original speed at 20 FPS
        double normalizedEnemySpeed = (BASE_ENEMY_SPEED * 20.0) / FPS;

        for (int i = 0; i < 8; i++) {
            if (enemyActive[i]) {
                if (i < 4 && (enemyX[i] + (ENEMY_WIDTH / 2) >= 600 || enemyX[i] - (ENEMY_WIDTH / 2) <= 0)) {
                    row1HitWall = true;
                }
                if (i >= 4 && (enemyX[i] + (ENEMY_WIDTH / 2) >= 600 || enemyX[i] - (ENEMY_WIDTH / 2) <= 0)) {
                    row2HitWall = true;
                }
            }
        }

        for (int i = 0; i < 8; i++) {
            if (enemyShootCounter[i] > 0) {
                enemyShootCounter[i]--;
            }

            if (enemyActive[i]) {
                StdDraw.picture(enemyX[i], enemyY[i], "../assets/enemyFighter.png", ENEMY_WIDTH, ENEMY_HEIGHT);

                if ((i < 4 && row1HitWall) || (i >= 4 && row2HitWall)) {
                    enemyDirection[i] = !enemyDirection[i];
                    if (enemyX[i] + (ENEMY_WIDTH / 2) >= 600)
                        enemyX[i] = 600 - (ENEMY_WIDTH / 2);
                    if (enemyX[i] - (ENEMY_WIDTH / 2) <= 0)
                        enemyX[i] = (ENEMY_WIDTH / 2);
                }

                // Move using normalized speed
                if (enemyDirection[i]) {
                    enemyX[i] += normalizedEnemySpeed;
                } else {
                    enemyX[i] -= normalizedEnemySpeed;
                }

                // Enemy Shooting: Divide chance by FPS to keep probability consistent per second
                double adjustedShootChance = TARGET_SHOOT_CHANCE_PER_SEC / FPS;

                if (Math.random() < adjustedShootChance && enemyShootCounter[i] == 0) {
                    for (int j = 0; j < maxBullets; j++) {
                        if (!bulletActive[j]) {
                            bulletActive[j] = true;
                            bulletX[j] = enemyX[i];
                            bulletY[j] = enemyY[i] - HALF_ENEMY_HEIGHT;
                            bulletDirection[j] = false;
                            enemyShootCounter[i] = (int) (ENEMY_SHOOT_COOLDOWN * FPS);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Animates bullets across the screen and deactivates them when off-screen.
     */
    public static void bulletMovement() {
        // Normalize bullet speed based on FPS
        double normalizedBulletSpeed = (BASE_BULLET_SPEED * 20.0) / FPS;

        for (int i = 0; i < maxBullets; i++) {
            if (bulletActive[i]) {
                if (bulletDirection[i]) { // Player bullet
                    StdDraw.picture(bulletX[i], bulletY[i], "../assets/bullet.png", BULLET_WIDTH, BULLET_HEIGHT);
                    bulletY[i] += normalizedBulletSpeed;
                    if (bulletY[i] > 800)
                        bulletActive[i] = false;
                } else { // Enemy bullet
                    StdDraw.picture(bulletX[i], bulletY[i], "../assets/enemyBullet.png", BULLET_WIDTH, BULLET_HEIGHT);
                    bulletY[i] -= normalizedBulletSpeed;
                    if (bulletY[i] < 0)
                        bulletActive[i] = false;
                }
            }
        }
    }

    /**
     * Animates health pickups and checks for intersection with the player.
     */
    public static void lifeDropMovement() {
    // 1. Calculate normalized falling speed
    // (Base speed of 5 pixels * 20 FPS reference) / Current FPS
    double normalizedDropSpeed = (5.0 * 20.0) / FPS;

    for (int i = 0; i < lifeDropActive.length; i++) {
        if (lifeDropActive[i]) {
            StdDraw.picture(lifeDropX[i], lifeDropY[i], "../assets/heart.png", 30, 30);
            
            // 2. Use the normalized speed instead of the hardcoded 5
            lifeDropY[i] -= normalizedDropSpeed;

            // Pickup logic: if heart touches interceptor
            if (lifeDropX[i] > interceptorX - HALF_INTERCEPTOR_WIDTH &&
                    lifeDropX[i] < interceptorX + HALF_INTERCEPTOR_WIDTH &&
                    lifeDropY[i] > interceptorY - HALF_INTERCEPTOR_HEIGHT &&
                    lifeDropY[i] < interceptorY + HALF_INTERCEPTOR_HEIGHT) {

                if (lives < 5)
                    lives++;
                lifeDropActive[i] = false;
            }

            // Cleanup if missed
            if (lifeDropY[i] < 0)
                lifeDropActive[i] = false;
        }
    }
}

    /**
     * Checks for and resolves collisions between bullets, enemies, and the player.
     */
    public static void checkCollisions() {
        for (int i = 0; i < maxBullets; i++) {
            if (bulletActive[i]) {
                if (bulletDirection[i]) { // Check Player bullets hitting enemies
                    for (int j = 0; j < 8; j++) {
                        if (enemyActive[j]) {
                            if (bulletX[i] + HALF_BULLET_WIDTH > enemyX[j] - HALF_ENEMY_WIDTH &&
                                    bulletX[i] - HALF_BULLET_WIDTH < enemyX[j] + HALF_ENEMY_WIDTH &&
                                    bulletY[i] + HALF_BULLET_HEIGHT > enemyY[j] - HALF_ENEMY_HEIGHT &&
                                    bulletY[i] - HALF_BULLET_HEIGHT < enemyY[j] + HALF_ENEMY_HEIGHT) {

                                bulletActive[i] = false;
                                enemyActive[j] = false;
                                // Create an explosion object at enemy position
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

                                // Drop life heart if probability check passes
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
                } else { // Check Enemy bullets hitting the Player
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

        // Direct collision between player and active enemies
        for (int i = 0; i < 8; i++) {
            if (enemyActive[i]) {
                if (interceptorX + HALF_INTERCEPTOR_WIDTH > enemyX[i] - HALF_ENEMY_WIDTH &&
                        interceptorX - HALF_INTERCEPTOR_WIDTH < enemyX[i] + HALF_ENEMY_WIDTH &&
                        interceptorY + HALF_INTERCEPTOR_HEIGHT > enemyY[i] - HALF_ENEMY_HEIGHT &&
                        interceptorY - HALF_INTERCEPTOR_HEIGHT < enemyY[i] + HALF_ENEMY_HEIGHT) {

                    enemyActive[i] = false;
                    lives--;
                    // Create explosion at collision point
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

    /**
     * Renders the end-of-game overlay with score and navigation options.
     * * @param result State of the game ("gameOver" or "victory").
     */
    public static void endGameMenu(String result) {
        boolean isRestartSelected = true;

        while (true) {
            StdDraw.clear();
            StdDraw.picture(300, 400, "../assets/background.png", CANVAS_WIDTH, CANVAS_HEIGHT);

            if (result.equals("gameOver")) {
                StdDraw.picture(300, 550, "../assets/gameOver.png", 400, 210);
            } else if (result.equals("victory")) {
                StdDraw.picture(300, 550, "../assets/victory.png", 400, 86);
            }

            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 30));
            StdDraw.text(300, 420, "Score: " + score);

            StdDraw.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 25));

            String restartText = isRestartSelected ? "> Restart <" : "Restart";
            StdDraw.text(300, 350, restartText);

            String endText = !isRestartSelected ? "> End Game <" : "End Game";
            StdDraw.text(300, 300, endText);

            StdDraw.show();

            // Menu selection logic
            if (StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_UP)
                    || StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_DOWN)) {
                isRestartSelected = !isRestartSelected;
                StdDraw.pause(150); // Small delay to prevent rapid flickering
            }

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
            StdDraw.pause(100);
        }
    }

    /**
     * Resets all game state variables to prepare for a new session.
     */
    public static void resetGame() {
        lives = 3;
        score = 0;
        interceptorX = 300;
        interceptorY = 200;

        double[] startX = { 90.0, 230.0, 370.0, 510.0, 90.0, 230.0, 370.0, 510.0 };
        double[] startY = { 720.0, 720.0, 720.0, 720.0, 620.0, 620.0, 620.0, 620.0 };

        for (int i = 0; i < 8; i++) {
            enemyX[i] = startX[i];
            enemyY[i] = startY[i];
            enemyActive[i] = true;
            enemyDirection[i] = true;
            enemyShootCounter[i] = 0;
        }

        // Clear object pools
        for (int i = 0; i < maxBullets; i++)
            bulletActive[i] = false;
        for (int i = 0; i < 5; i++)
            lifeDropActive[i] = false;
        for (int i = 0; i < 10; i++)
            explosionActive[i] = false;
    }
}