package prism4291.henachoko;

import io.socket.client.Socket;
import org.json.JSONArray;

import java.util.ArrayList;
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
    public static boolean isHost=true;
    public static JSONArray PLAYERDATA=new JSONArray();
    public static String userName;
    public static Map<Integer,Texture> MYPUYOTEXTURES=new HashMap<>();
    public static long timeDelta=0;
    public static List<Long> timeDeltas=new ArrayList<>();
}
