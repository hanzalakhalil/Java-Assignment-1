// Name: Hanzala khalil
// ID: 2025400279


public class HanzalaKhalil {

    static final int CANVAS_WIDTH = 600;
    static final int CANVAS_HEIGHT = 800;
    static final int INTERCEPTOR_WIDTH = 80;
    static final int INTERCEPTOR_HEIGHT = 80;
    static final int BULLET_WIDTH = 5;
    static final int BULLET_HEIGHT = 20;

    static int maxBullets = 50;
    static double[] bulletX = new double[maxBullets];
    static double[] bulletY = new double[maxBullets];
    static boolean[] bulletActive = new boolean[maxBullets];

    static int FPS=20;
    static double speed=20.0;
    static int shootCooldown = 0;

    public static void main(String[] args) {
    
        StdDraw.setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        StdDraw.setXscale(0, CANVAS_WIDTH);
        StdDraw.setYscale(0, CANVAS_HEIGHT);
        StdDraw.setTitle("2042: Interceptor");
        StdDraw.enableDoubleBuffering();
        menu();
        
    }

    public static void menu(){
        char typedKey;
        int startGame = 0;
        int interceptorX = 300;
        int interceptorY = 200;

        while(startGame==0)
        {
            if (shootCooldown>0) {
                shootCooldown--;
            }
            StdDraw.clear();
            StdDraw.picture(300, 400, "../assets/background.png",CANVAS_WIDTH,CANVAS_HEIGHT);
            StdDraw.picture(300, 550, "../assets/title.png",400,133);
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

            StdDraw.picture(interceptorX, interceptorY, "../assets/interceptor.png",INTERCEPTOR_WIDTH,INTERCEPTOR_HEIGHT);
            for (int i = 0; i < maxBullets; i++) {
                    if (bulletActive[i]) {
                        StdDraw.picture(bulletX[i],bulletY[i], "../assets/bullet.png",BULLET_WIDTH,BULLET_HEIGHT);
                        bulletY[i]+= BULLET_HEIGHT;
                        if(bulletY[i]>800){
                            bulletActive[i]=false;
                        }
                        
                    }
                }
            StdDraw.show();
            StdDraw.pause(1000/FPS);

            if (StdDraw.hasNextKeyTyped()) {
                typedKey = StdDraw.nextKeyTyped();
                switch (typedKey) {
                case 'a','A':
                    if(FPS>5){
                        FPS-=5;
                    }
                    break;
                case 'd','D':
                    if(FPS<60){
                        FPS+=5;
                    }
                    break;
                case 'q','Q':
                    if(speed>1.0){
                        speed--;
                    }
                    break;
                case 'e','E':
                    if(speed<80.0){
                        speed++;
                    }
                    break;
                default:
                    break;
                    }
            }

            if (StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_UP) && interceptorY+(INTERCEPTOR_HEIGHT/2) < 800) {
                interceptorY += speed; 
                if (interceptorY+(INTERCEPTOR_HEIGHT/2) > 800) {
                    interceptorY = 800-(INTERCEPTOR_HEIGHT/2);
                }
            }
            if (StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_DOWN) && interceptorY-(INTERCEPTOR_HEIGHT/2) > 0) {
                interceptorY -= speed;
                if (interceptorY-(INTERCEPTOR_HEIGHT/2) < 0) {
                    interceptorY = 0+(INTERCEPTOR_HEIGHT/2);
                }
            }
            if (StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_RIGHT) && interceptorX+(INTERCEPTOR_WIDTH/2) < 600) {
                interceptorX += speed;
                if (interceptorX+(INTERCEPTOR_WIDTH/2) > 600) {
                    interceptorX = 600-(INTERCEPTOR_WIDTH/2);
                }
            }
            if (StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_LEFT) && interceptorX-(INTERCEPTOR_WIDTH/2) > 0) {
                interceptorX -= speed;
                if (interceptorX-(INTERCEPTOR_WIDTH/2) < 0) {
                    interceptorX = 0+(INTERCEPTOR_WIDTH/2);
                }
            }

            if (StdDraw.isKeyPressed(java.awt.event.KeyEvent.VK_SPACE) && shootCooldown==0) {
                for (int i = 0; i < maxBullets; i++) {
                    if (!bulletActive[i]) {
                        bulletActive[i] = true;
                        bulletX[i] = interceptorX;
                        bulletY[i] = interceptorY;
                        shootCooldown=10;
                        break;
                    }
                }


            } 
        
        
        }

    }

}

