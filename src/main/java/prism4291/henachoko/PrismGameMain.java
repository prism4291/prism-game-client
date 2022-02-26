package prism4291.henachoko;

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

    PrismGameMain() {
        fuse = 0;
        seq=0;
        images.put("title",Texture.loadTexture("/title.png"));
        images.put("host",Texture.loadTexture("/host.png"));
        images.put("guest",Texture.loadTexture("/guest.png"));
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
        if(seq==1){
            int res=showMenu();
            if(res==1){
                seq=2;
            }
        }
        myActionMain();
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
        glBindTexture(GL_TEXTURE_2D, images.get("host").getId());
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0,0);
        GL11.glVertex2f(-1, 1);
        GL11.glTexCoord2f(0,1);
        GL11.glVertex2f(-1, -1);
        GL11.glTexCoord2f(1,1);
        GL11.glVertex2f(0, -1);
        GL11.glTexCoord2f(1,0);
        GL11.glVertex2f(0, 1);
        GL11.glEnd();
        glBindTexture(GL_TEXTURE_2D, images.get("guest").getId());
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0,0);
        GL11.glVertex2f(0, 1);
        GL11.glTexCoord2f(0,1);
        GL11.glVertex2f(0, -1);
        GL11.glTexCoord2f(1,1);
        GL11.glVertex2f(1, -1);
        GL11.glTexCoord2f(1,0);
        GL11.glVertex2f(1, 1);
        GL11.glEnd();
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