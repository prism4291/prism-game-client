package prism4291.henachoko;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static prism4291.henachoko.PrismGameVariable.*;

public class PrismGameMain {
    int fuse;
    int seq;
    int menuSelect;
    JSONArray roomList;
    boolean roomJoined;
    boolean gameStarting;
    List<List<Integer>> roomTexts;
    int myRoomImage;
    String myRoomName;
    JSONObject currentRoom;
    List<Integer> roomMemberImage;
    boolean memberUpdated;
    static final int pWidth = 6;
    static final int pHeight = 14;
    List<List<Integer>> board;
    String mode;
    boolean puyoMoving;
    int playSequence;//0:move 1:kesu
    PrismGamePuyopuyo prismGamePuyopuyo;

    PrismGameMain() {
        fuse = 0;
        //
        seq = 0;
        //
        images.put("title", Texture.loadTexture("/title.png"));
        images.put("host", Texture.loadTexture("/host.png"));
        images.put("guest", Texture.loadTexture("/guest.png"));
        images.put("sousa",Texture.loadTexture("/sousa.png"));
        images.put("enter",Texture.loadTexture("/enter.png"));
        menuSelect = 0;
        roomJoined = false;
        gameStarting = false;
        board = new ArrayList<>();
        for (int i = 0; i < pHeight; i++) {
            board.add(new ArrayList<>());
            for (int j = 0; j < pWidth; j++) {
                board.get(i).add(0);
            }
        }
        puyoMoving = false;
        mode = "start";
        playSequence = 0;
        PrismGameVariable.socket.on("serverStartGame", objects -> {
            JSONObject jo = (JSONObject) objects[0];
            //System.out.println("serverStartGame  "+jo);
            if (jo.getString("status").equals("start")) {
                gameStarting = true;
            }
        });
        PrismGameVariable.socket.on("serverReStartGame", objects -> {
            //System.out.println(objects);
            //JSONObject jo = (JSONObject) objects[0];
            //System.out.println("serverReStartGame  "+jo);
            gameStarting = false;
        });
        currentRoom = null;
        PrismGameVariable.socket.on("serverJoinMember", objects -> {
            JSONObject jo = (JSONObject) objects[0];
            //System.out.println(jo);
            currentRoom = jo.getJSONObject("room");
            memberUpdated = true;
        });
        roomList = null;
        myRoomImage = -1;
        myRoomName = null;
        roomMemberImage = new ArrayList<>();
        memberUpdated = false;
        PrismGameVariable.socket.on("serverCreateRoomRes", objects -> {
            JSONObject jo = (JSONObject) objects[0];
            //System.out.println(jo);
            myRoomName = jo.getString("name");
            roomJoined = true;
            currentRoom = jo.getJSONObject("room");
            memberUpdated = true;
        });
        PrismGameVariable.socket.on("serverGetRoomRes", objects -> {
            JSONObject jo = (JSONObject) objects[0];
            //System.out.println(jo);
            roomList = jo.getJSONArray("rooms");
            roomTexts = null;

        });
        PrismGameVariable.socket.on("serverJoinRoomRes", objects -> {
            JSONObject jo = (JSONObject) objects[0];
            //System.out.println(jo);
            if (jo.getString("status").equals("success")) {
                roomJoined = true;
                myRoomName = jo.getJSONObject("room").getString("name");
                currentRoom = jo.getJSONObject("room");
                memberUpdated = true;
            }

        });
        PrismGameVariable.socket.on("serverRoomMessage", objects -> {
            JSONObject jo = (JSONObject) objects[0];
            //if(!jo.getString("from").equals(socketId)) {
            //    System.out.println(jo.getString("from"));
            //prismGamePuyopuyo.updateData(jo);
            //}
            PrismGameVariable.PLAYERDATA.put(jo);
        });
    }
    void drawSousa(){
        glColor3d(1,1,1);
        glBindTexture(GL_TEXTURE_2D,images.get("sousa").getId());
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2d(-1, -0.8);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2d(-1, -1);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2d(-0.8, -1);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2d(-0.8, -0.8);
        GL11.glEnd();
        glDisable(GL_TEXTURE_2D);
    }
    void drawEnter(){
        glColor3d(1,1,1);
        glBindTexture(GL_TEXTURE_2D,images.get("enter").getId());
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2d(0.8, -0.8);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2d(0.8, -1);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2d(1, -1);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2d(1, -0.8);
        GL11.glEnd();
        glDisable(GL_TEXTURE_2D);
    }
    void Main(boolean flag) {
        if (fuse == 0) {
            getPuyoTextures();
        }
        fuse++;
        if (socketId == null) {
            return;
        }
        if (!socketId.equals(socket.id())) {
            return;
        }
        if(fuse%100==0&&fuse>0&&timeDeltas.size()<5){
            PrismGameVariable.socket.emit("ping",System.currentTimeMillis());
        }/*else if(fuse%100==0){
            System.out.println("fuse "+fuse);
            System.out.println(timeDeltas);
        }*/
        if (seq == 0) {
            int res = showTitle();
            if (res == 1) {
                seq = 1;
            }
        } else if (seq == 1) {
            int res = showMenu();
            if (res == 1) {
                seq = 2;

                PrismGameVariable.socket.emit("clientCreateRoom");

            }
            if (res == 2) {
                seq = 3;

                menuSelect = 0;
                PrismGameVariable.socket.emit("clientGetRoom");
            }
            drawEnter();
        } else if (seq == 2) {
            int res = showHost();
            if (res == 1) {

                PrismGameVariable.socket.emit("clientStartGame", currentRoom.toString());
            }
            if (gameStarting) {
                seq = 5;
                isHost = true;
            }

        } else if (seq == 3) {
            if (fuse % 300 == 0) {
                PrismGameVariable.socket.emit("clientGetRoom");

            }

            int res = showGuest();
            //System.out.println(res);
            if (res > 0) {
                if (roomList.length() > res - 1) {
                    JSONObject jr = roomList.getJSONObject(res - 1);
                    JSONObject msg = new JSONObject();
                    msg.put("room", jr.getString("name"));
                    PrismGameVariable.socket.emit("clientJoinRoom", msg.toString());
                    seq = 4;
                }
            }
            drawEnter();
        } else if (seq == 4) {
            showJoinedRoom();
            if (gameStarting) {
                seq = 5;
                isHost = false;

            }
        } else if (seq == 5) {
            if (gameStarting) {
                if (prismGamePuyopuyo == null) {
                    prismGamePuyopuyo = new PrismGamePuyopuyo();
                }
//            int n=0;
//            for(Object str:currentRoom.getJSONArray("guest")){
//                prismGamePuyopuyo.opponents.put(n,(String) str);
//            }
                int res = MainGame(flag);
                if (res == 1) {
                    JSONObject msg = new JSONObject();
                    PrismGameVariable.socket.emit("clientReStartGame", msg.toString());
                }
            } else {
                prismGamePuyopuyo = new PrismGamePuyopuyo();
                gameStarting = true;
            }

        }
        drawSousa();
        //System.out.println(seq);
        myActionMain();
    }

    int MainGame(boolean flag) {

        int res = prismGamePuyopuyo.PuyoLoop();

        if (flag) {
            prismGamePuyopuyo.draw();
        }
        if (res == 1 && isHost ) {
            drawEnter();
            if(KEY_BUTTON[GLFW.GLFW_KEY_ENTER] == 1) {
                return 1;
            }
        }
        if (fuse % 4 == 0) {
            prismGamePuyopuyo.PuyoSend();
        }
        //System.out.println(PLAYERDATA);

        JSONArray tmpDATA = new JSONArray(PLAYERDATA.toString());
        for (int i = 0; i < tmpDATA.length(); i++) {
            PLAYERDATA.remove(0);
        }
        long[] times = new long[tmpDATA.length()];
        for (int n = 0; n < tmpDATA.length(); n++) {
            try {
                times[n] = tmpDATA.getJSONObject(n).getLong("time");
            } catch (JSONException e) {
                times[n] = -1;
            }
        }
        for (int m = 0; m < tmpDATA.length(); m++) {
            for (int n = 0; n < tmpDATA.length() - 1; n++) {
                if (times[n] >= 0 && times[n + 1] >= 0) {
                    if (times[n] > times[n + 1]) {
                        long t = times[n];
                        times[n] = times[n + 1];
                        times[n + 1] = t;
                        JSONObject jo = tmpDATA.getJSONObject(n);
                        tmpDATA.put(n, tmpDATA.getJSONObject(n + 1));
                        tmpDATA.put(n + 1, jo);
                    }
                }
            }
        }
        for (int n = 0; n < tmpDATA.length(); n++) {
            if (times[n] >= 0) {
                JSONObject jo = tmpDATA.getJSONObject(n);
                prismGamePuyopuyo.updateData(jo);
            }
        }

        return 0;
    }


    void updateMember() {
        for (int n : roomMemberImage) {
            glDeleteTextures(n);
        }
        roomMemberImage = new ArrayList<>();
        roomMemberImage.add(Texture.drawStrImage(currentRoom.getString("host")).getId());
        for (Object str : currentRoom.getJSONArray("guest")) {
            //System.out.println(str);
            roomMemberImage.add(Texture.drawStrImage((String) str).getId());
        }
        memberUpdated = false;
    }

    int showTitle() {
        glBegin(GL_TRIANGLE_FAN);
        glColor4d(1, 1, 1, 1);
        glVertex2d(-1, -1);
        glVertex2d(-1, 1);
        glVertex2d(1, 1);
        glVertex2d(1, -1);
        glEnd();
        glBindTexture(GL_TEXTURE_2D, images.get("title").getId());
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(-1, 1);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(-1, -1);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(1, -1);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(1, 1);
        GL11.glEnd();
        for (int j : KEY_BUTTON) {
            if (j == 1) {
                return 1;
            }
        }
        return 0;
    }

    int showMenu() {
        glBegin(GL_TRIANGLE_FAN);
        glColor4d(1, 1, 1, 1);
        glVertex2d(-1, -1);
        glVertex2d(-1, 1);
        glVertex2d(1, 1);
        glVertex2d(1, -1);
        glEnd();
        glColor4d(0, 1, 0, 1);
        switch (menuSelect) {
            case 0:
                glBegin(GL_TRIANGLE_FAN);
                glVertex2d(-0.8, -1);
                glVertex2d(-0.8, 1);
                glVertex2d(-0.1, 1);
                glVertex2d(-0.1, -1);
                glEnd();
                break;
            case 1:
                glBegin(GL_TRIANGLE_FAN);
                glVertex2d(0.1, -1);
                glVertex2d(0.1, 1);
                glVertex2d(0.8, 1);
                glVertex2d(0.8, -1);
                glEnd();
                break;
        }
        glColor4d(1, 1, 1, 1);
        glBindTexture(GL_TEXTURE_2D, images.get("host").getId());
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(0, 0);
        GL11.glVertex2d(-0.7, 0.7);
        GL11.glTexCoord2d(0, 1);
        GL11.glVertex2d(-0.7, -0.7);
        GL11.glTexCoord2d(1, 1);
        GL11.glVertex2d(-0.2, -0.7);
        GL11.glTexCoord2d(1, 0);
        GL11.glVertex2d(-0.2, 0.7);
        GL11.glEnd();
        glBindTexture(GL_TEXTURE_2D, images.get("guest").getId());
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(0, 0);
        GL11.glVertex2d(0.2, 0.7);
        GL11.glTexCoord2d(0, 1);
        GL11.glVertex2d(0.2, -0.7);
        GL11.glTexCoord2d(1, 1);
        GL11.glVertex2d(0.7, -0.7);
        GL11.glTexCoord2d(1, 0);
        GL11.glVertex2d(0.7, 0.7);
        GL11.glEnd();
        if (KEY_BUTTON[GLFW.GLFW_KEY_A] == 1) {
            menuSelect--;
        }
        if (KEY_BUTTON[GLFW.GLFW_KEY_D] == 1) {
            menuSelect++;
        }
        if (menuSelect < 0) {
            menuSelect = 1;
        }
        if (menuSelect >= 2) {
            menuSelect = 0;
        }
        if (KEY_BUTTON[GLFW.GLFW_KEY_ENTER] == 1) {
            return menuSelect + 1;
        }
        return 0;
    }

    int showHost() {
        if (memberUpdated) {
            updateMember();
        }
        glBegin(GL_TRIANGLE_FAN);
        glColor4d(0.8, 1, 0.8, 1);
        glVertex2d(-1, -1);
        glVertex2d(-1, 1);
        glVertex2d(1, 1);
        glVertex2d(1, -1);
        glEnd();
        if (myRoomImage == -1) {
            if (myRoomName != null) {
                myRoomImage = Texture.drawStrImage(myRoomName).getId();
            }
        } else {
            double y = 0.5;

            glBindTexture(GL_TEXTURE_2D, myRoomImage);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2d(0, 0);
            GL11.glVertex2d(-0.6, y);
            GL11.glTexCoord2d(0, 1);
            GL11.glVertex2d(-0.6, y - 0.2);
            GL11.glTexCoord2d(1, 1);
            GL11.glVertex2d(0, y - 0.2);
            GL11.glTexCoord2d(1, 0);
            GL11.glVertex2d(0, y);
            GL11.glEnd();

        }
        double y = 0;
        for (int n : roomMemberImage) {
            glBindTexture(GL_TEXTURE_2D, n);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2d(0, 0);
            GL11.glVertex2d(-0.6, y);
            GL11.glTexCoord2d(0, 1);
            GL11.glVertex2d(-0.6, y - 0.1);
            GL11.glTexCoord2d(1, 1);
            GL11.glVertex2d(-0.3, y - 0.1);
            GL11.glTexCoord2d(1, 0);
            GL11.glVertex2d(-0.3, y);
            GL11.glEnd();
            y -= 0.1;
        }

            if (currentRoom != null) {
                if (currentRoom.getJSONArray("guest").length() >= 1) {
                    drawEnter();
                    if (KEY_BUTTON[GLFW.GLFW_KEY_ENTER] == 1) {
                    return 1;
                }
            }
        }
        return 0;
    }

    int showGuest() {
        glBegin(GL_TRIANGLE_FAN);
        glColor4d(0.8, 0.8, 1, 1);
        glVertex2d(-1, -1);
        glVertex2d(-1, 1);
        glVertex2d(1, 1);
        glVertex2d(1, -1);
        glEnd();
        if (roomList != null) {
            if (roomTexts == null) {
                roomTexts = new ArrayList<>();
                for (Object elem : roomList) {
                    JSONObject room = (JSONObject) elem;
                    int n = Texture.drawStrImage(room.getString("name")).getId();
                    int n2 = Texture.drawStrImage(room.getString("host")).getId();
                    List<Integer> r = new ArrayList<>();
                    r.add(n);
                    r.add(n2);
                    roomTexts.add(r);
                }
            }

            double y = 0.9;
            for (List<Integer> n : roomTexts) {
                glBindTexture(GL_TEXTURE_2D, n.get(0));
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glTexCoord2d(0, 0);
                GL11.glVertex2d(-0.6, y);
                GL11.glTexCoord2d(0, 1);
                GL11.glVertex2d(-0.6, y - 0.1);
                GL11.glTexCoord2d(1, 1);
                GL11.glVertex2d(-0.3, y - 0.1);
                GL11.glTexCoord2d(1, 0);
                GL11.glVertex2d(-0.3, y);
                GL11.glEnd();
                glBindTexture(GL_TEXTURE_2D, n.get(1));
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glTexCoord2d(0, 0);
                GL11.glVertex2d(-0.1, y);
                GL11.glTexCoord2d(0, 1);
                GL11.glVertex2d(-0.1, y - 0.1);
                GL11.glTexCoord2d(1, 1);
                GL11.glVertex2d(0.2, y - 0.1);
                GL11.glTexCoord2d(1, 0);
                GL11.glVertex2d(0.2, y);
                GL11.glEnd();
                y -= 0.1;
            }
            y = 0.9 - 0.1 * menuSelect;
            GL11.glColor4d(1, 0, 0, Math.abs(Math.sin(fuse * 0.04)));
            GL11.glBegin(GL_TRIANGLE_FAN);
            GL11.glVertex2d(-0.64, y - 0.02);
            GL11.glVertex2d(-0.64, y - 0.08);
            GL11.glVertex2d(-0.61, y - 0.05);
            GL11.glEnd();
            if (KEY_BUTTON[GLFW.GLFW_KEY_W] == 1) {
                menuSelect--;
            }
            if (KEY_BUTTON[GLFW.GLFW_KEY_S] == 1) {
                menuSelect++;
            }
            if (menuSelect < 0) {
                menuSelect = roomList.length() - 1;
            }
            if (menuSelect >= roomList.length()) {
                menuSelect = 0;
            }
            if (KEY_BUTTON[GLFW.GLFW_KEY_ENTER] == 1) {
                return menuSelect + 1;
            }
        }
        return 0;
    }

    void showJoinedRoom() {
        if (memberUpdated) {
            updateMember();
        }
        glBegin(GL_TRIANGLE_FAN);
        glColor4d(0.8, 0.8, 1, 1);
        glVertex2d(-1, -1);
        glVertex2d(-1, 1);
        glVertex2d(1, 1);
        glVertex2d(1, -1);
        glEnd();
        if (myRoomImage == -1) {
            if (myRoomName != null) {
                myRoomImage = Texture.drawStrImage(myRoomName).getId();
            }
        } else {
            double y = 0.5;

            glBindTexture(GL_TEXTURE_2D, myRoomImage);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2d(0, 0);
            GL11.glVertex2d(-0.6, y);
            GL11.glTexCoord2d(0, 1);
            GL11.glVertex2d(-0.6, y - 0.2);
            GL11.glTexCoord2d(1, 1);
            GL11.glVertex2d(0, y - 0.2);
            GL11.glTexCoord2d(1, 0);
            GL11.glVertex2d(0, y);
            GL11.glEnd();
            y = 0;
            for (int n : roomMemberImage) {
                glBindTexture(GL_TEXTURE_2D, n);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glTexCoord2d(0, 0);
                GL11.glVertex2d(-0.6, y);
                GL11.glTexCoord2d(0, 1);
                GL11.glVertex2d(-0.6, y - 0.1);
                GL11.glTexCoord2d(1, 1);
                GL11.glVertex2d(-0.3, y - 0.1);
                GL11.glTexCoord2d(1, 0);
                GL11.glVertex2d(-0.3, y);
                GL11.glEnd();
                y -= 0.1;
            }
        }
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

    public static void getPuyoTextures() {


        Path path = Paths.get("textures");
        if (!Files.exists(path)) {

            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String file;
        for (int i = 0; i < 16; i++) {
            switch (i) {
                case 0:
                    file = "/red.png";
                    break;
                case 1:
                    file = "/green.png";
                    break;
                case 2:
                    file = "/blue.png";
                    break;
                case 3:
                    file = "/yellow.png";
                    break;
                case 5:
                    file = "/ojama.png";
                    break;
                case 9:
                    file = "/ojamaSmall.png";
                    break;
                case 10:
                    file = "/ojamaBig.png";
                    break;
                case 11:
                    file = "/ojamaRock.png";
                    break;
                case 12:
                    file = "/ojamaStar.png";
                    break;
                case 13:
                    file = "/ojamaMoon.png";
                    break;
                case 14:
                    file = "/ojamaCrown.png";
                    break;
                case 15:
                    file = "/ojamaComet.png";
                    break;
                default:
                    continue;
            }
            path = Paths.get("textures" + file);
            if (!Files.exists(path)) {

                InputStream stream = PrismGameClient.class.getResourceAsStream(file);
                try {
                    Files.copy(Objects.requireNonNull(stream), path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            PrismGameVariable.MYPUYOTEXTURES.put(i, Texture.getTexture2(path));

        }


    }
}
