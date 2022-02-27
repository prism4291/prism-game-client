package prism4291.henachoko;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;


import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static prism4291.henachoko.PrismGameVariable.*;

public class PrismGameMain {
    int fuse;
    int seq;
    int menuSelect;
    JSONArray roomList;
    boolean roomJoined;
    boolean gameStarting;
    List<Integer> roomTexts;
    PrismGameMain() {
        fuse = 0;
        seq=0;
        images.put("title",Texture.loadTexture("/title.png"));
        images.put("host",Texture.loadTexture("/host.png"));
        images.put("guest",Texture.loadTexture("/guest.png"));
        menuSelect=0;
        roomJoined=false;
        gameStarting=false;
        PrismGameVariable.socket.on("serverStartGame", objects -> {
            JSONObject jo=(JSONObject)objects[0];
            System.out.println(jo);
            if(jo.getString("status").equals("start")){
                gameStarting=true;
            }
        });
        roomList=null;

    }

    void Main() {
        fuse++;
        if(socketId==null){
            return;
        }
        if(!socketId.equals(socket.id())){
            return;
        }
        if(seq==0){
            int res=showTitle();
            if(res==1){
                seq=1;
            }
        }
        else if(seq==1){
            int res=showMenu();
            if(res==1){
                seq=2;

                PrismGameVariable.socket.emit("clientCreateRoom");
                roomJoined=true;
            }
            if(res==2){
                seq=3;
                PrismGameVariable.socket.on("serverGetRoomRes", objects -> {
                    JSONObject jo=(JSONObject)objects[0];
                    System.out.println(jo);
                    roomList=jo.getJSONArray("rooms");

                });
                PrismGameVariable.socket.on("serverJoinRoomRes", objects -> {
                    JSONObject jo=(JSONObject)objects[0];
                    System.out.println(jo);
                    if(jo.getString("success").equals("success")){
                        roomJoined=true;
                    }

                });

                PrismGameVariable.socket.emit("clientGetRoom");
            }
        }
        else if(seq==2){
            int res=showHost();
            if(gameStarting){
                seq=4;
            }
        }
        else if(seq==3){
            int res=showGuest();
            if(gameStarting){
                seq=4;
            }
        }
        else if(seq==4){
            int res=MainGame();
        }
        myActionMain();
    }
    int MainGame(){
        return 0;
    }
    int showTitle(){
        glBegin(GL_TRIANGLE_FAN);
        glColor4d(1,1,1,1);
        glVertex2d(-1,-1);
        glVertex2d(-1,1);
        glVertex2d(1,1);
        glVertex2d(1,-1);
        glEnd();
        glBindTexture(GL_TEXTURE_2D, images.get("title").getId());
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0,0);
        GL11.glVertex2f(-1, 1);
        GL11.glTexCoord2f(0,1);
        GL11.glVertex2f(-1, -1);
        GL11.glTexCoord2f(1,1);
        GL11.glVertex2f(1, -1);
        GL11.glTexCoord2f(1,0);
        GL11.glVertex2f(1, 1);
        GL11.glEnd();
        for (int j : KEY_BUTTON) {
            if (j == 1) {
                return 1;
            }
        }
        return 0;
    }
    int showMenu(){
        glBegin(GL_TRIANGLE_FAN);
        glColor4d(1,1,1,1);
        glVertex2d(-1,-1);
        glVertex2d(-1,1);
        glVertex2d(1,1);
        glVertex2d(1,-1);
        glEnd();
        glColor4d(0,1,0,1);
        switch (menuSelect) {
            case 0 -> {
                glBegin(GL_TRIANGLE_FAN);
                glVertex2d(-0.8, -1);
                glVertex2d(-0.8, 1);
                glVertex2d(-0.1, 1);
                glVertex2d(-0.1, -1);
                glEnd();
            }
            case 1 -> {
                glBegin(GL_TRIANGLE_FAN);
                glVertex2d(0.1, -1);
                glVertex2d(0.1, 1);
                glVertex2d(0.8, 1);
                glVertex2d(0.8, -1);
                glEnd();
            }
        }
        glColor4d(1,1,1,1);
        glBindTexture(GL_TEXTURE_2D, images.get("host").getId());
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(0,0);
        GL11.glVertex2d(-0.7, 0.7);
        GL11.glTexCoord2d(0,1);
        GL11.glVertex2d(-0.7, -0.7);
        GL11.glTexCoord2d(1,1);
        GL11.glVertex2d(-0.2, -0.7);
        GL11.glTexCoord2d(1,0);
        GL11.glVertex2d(-0.2, 0.7);
        GL11.glEnd();
        glBindTexture(GL_TEXTURE_2D, images.get("guest").getId());
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(0,0);
        GL11.glVertex2d(0.2, 0.7);
        GL11.glTexCoord2d(0,1);
        GL11.glVertex2d(0.2, -0.7);
        GL11.glTexCoord2d(1,1);
        GL11.glVertex2d(0.7, -0.7);
        GL11.glTexCoord2d(1,0);
        GL11.glVertex2d(0.7, 0.7);
        GL11.glEnd();
        if(KEY_BUTTON[GLFW.GLFW_KEY_A]==1){
            menuSelect--;
        }
        if(KEY_BUTTON[GLFW.GLFW_KEY_D]==1){
            menuSelect++;
        }
        if(menuSelect<0){
            menuSelect=1;
        }
        if(menuSelect>=2){
            menuSelect=0;
        }
        if(KEY_BUTTON[GLFW.GLFW_KEY_ENTER]==1){
            return menuSelect+1;
        }
        return 0;
    }
    int showHost(){
        glBegin(GL_TRIANGLE_FAN);
        glColor4d(0.8,1,0.8,1);
        glVertex2d(-1,-1);
        glVertex2d(-1,1);
        glVertex2d(1,1);
        glVertex2d(1,-1);
        glEnd();
        return 0;
    }
    int showGuest(){
        glBegin(GL_TRIANGLE_FAN);
        glColor4d(0.8,0.8,1,1);
        glVertex2d(-1,-1);
        glVertex2d(-1,1);
        glVertex2d(1,1);
        glVertex2d(1,-1);
        glEnd();
        if(roomList!=null){
            if(roomTexts==null) {
                roomTexts = new ArrayList<>();
                for (Object elem : roomList) {
                    JSONObject room = (JSONObject) elem;
                    int n = Texture.drawStrImage(room.getString("host")).getId();
                    roomTexts.add(n);
                }
            }
            double y=0.9;
            for(int n:roomTexts){
                glBindTexture(GL_TEXTURE_2D, n);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glTexCoord2d(0,0);
                GL11.glVertex2d(-0.6, y);
                GL11.glTexCoord2d(0,1);
                GL11.glVertex2d(-0.6, y-0.1);
                GL11.glTexCoord2d(1,1);
                GL11.glVertex2d(-0.3, y-0.1);
                GL11.glTexCoord2d(1,0);
                GL11.glVertex2d(-0.3, y);
                GL11.glEnd();
                y-=0.1;
            }
        }
        return 0;
    }
    void myActionMain() {
        for (int i = 0; i < MOUSE_BUTTON.length; i++) {
            if (MOUSE_BUTTON[i] <= 0) {
                MOUSE_BUTTON[i] = 0;
            } else if (MOUSE_BUTTON[i] < 1000) {
                MOUSE_BUTTON[i]++;
            }
        }
        for (int i = 0; i < KEY_BUTTON.length; i++) {
            if (KEY_BUTTON[i] <= 0) {
                KEY_BUTTON[i] = 0;
            } else if (KEY_BUTTON[i] < 1000) {
                KEY_BUTTON[i]++;
            }
        }
    }
}