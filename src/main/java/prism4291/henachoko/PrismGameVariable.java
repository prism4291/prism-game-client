package prism4291.henachoko;

import io.socket.client.Socket;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrismGameVariable {
    private PrismGameVariable() {
    }

    public static String TITLE = "prismApp";
    public static int WIDTH = 1280;
    public static int HEIGHT = 720;
    public static int CURRENT_WIDTH = 1280;
    public static int CURRENT_HEIGHT = 720;
    public static boolean FULLSCREEN = false;
    public static long WIN;
    public static int FPS = 100;
    public static int CURRENT_FPS;
    public static int[] MOUSE_BUTTON = new int[8];
    public static int MOUSE_X = 0;
    public static int MOUSE_Y = 0;
    public static int[] KEY_BUTTON = new int[500];
    public static Socket socket;
    public static String socketId;
    public static Map<String, Texture> images=new HashMap<>();
}