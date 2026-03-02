// Name: Hanzala khalil
// ID: 2025400279


public class HanzalaKhalil {

    static final int CANVAS_WIDTH = 600;
    static final int CANVAS_HEIGHT = 800;

    public static void main(String[] args) {
    
        StdDraw.setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        StdDraw.setXscale(0, CANVAS_WIDTH);
        StdDraw.setYscale(0, CANVAS_HEIGHT);
        StdDraw.setTitle("2042: Interceptor");
        StdDraw.enableDoubleBuffering();
        menu();
        
    }

    public static void menu(){
        StdDraw.clear();
        StdDraw.picture(300, 400, "../assets/background.png",CANVAS_WIDTH,CANVAS_HEIGHT);
        StdDraw.picture(300, 550, "../assets/title.png",400,133);
        StdDraw.picture(300, 200, "../assets/interceptor.png",75,75);
        
        
        StdDraw.show();
    }

}

