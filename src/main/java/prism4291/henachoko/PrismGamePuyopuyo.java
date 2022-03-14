package prism4291.henachoko;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class PrismGamePuyopuyo {
    static String status;
    Puyopuyo currentPuyo;
    Puyopuyo currentPuyoSub;
    List<Puyopuyo> puyos;
    Map<Integer,Puyopuyo> backPuyos;
    static int puyoMaxY=14;
    static int puyoMaxX=6;
    static int delayMax=50;
    int muki;
    double theta;
    int delay;
    boolean canFall;

    PrismGamePuyopuyo(){
        status="ready";
        status="go";
        puyos=new ArrayList<>();
        muki = 3;
        theta=Math.PI*1.5;
        backPuyos=new HashMap<>();
        delay=0;
        canFall=false;
    }
    void createPuyo(int color1, int color2){
        currentPuyo=new Puyopuyo(color1);
        currentPuyoSub=new Puyopuyo(color2);

    }
    static class Puyopuyo {
        int puyoColor;

        int puyoX;
        int puyoY;
        double puyoMoveX;
        double puyoMoveY;
        int frameX;
        int frameY;
        int frameMaxX;
        int frameMaxY;


        Puyopuyo(int color) {
            puyoColor = color;

            puyoMoveX = 0;
            puyoMoveY = 1;
            frameMaxX=1;
            frameMaxY=40;
            frameY=20;
            frameX=0;
            puyoX = 2;
            puyoY = 2;
        }
    }
    void move(Puyopuyo puyo){
        if(canFall){
            puyo.frameY+=1;
            if(puyo.frameY>=frameMaxY){
                status="check";
            }
        }else{
            delay+=1;
            if(delay>delayMax){
                status="fallStart";
            }
            
        }
        
    }
    int PuyoLoop(){
        switch (status){
            case "go":
                status="summon";
                break;
            case "summon":
                createPuyo(0,0);
                delay=0;
                canFall=true;
                status="move";
                break;
            case "move":
                move(currentPuyo);

                break;
            case "check":
                System.out.println("check "+currentPuyo.frameY);
                currentPuyo.frameY=0;
                setSubPuyoXY(currentPuyo,currentPuyoSub);
                if(currentPuyo.puyoY<puyoMaxY&&backPuyos.get(calPuyoMap(currentPuyo.puyoX,currentPuyo.puyoY+1))==null&&backPuyos.get(calPuyoMap(currentPuyoSub.puyoX,currentPuyoSub.puyoY+1))==null){
                    currentPuyo.frameMaxY=40;
                    currentPuyo.puyoY+=1;
                    currentPuyo.puyoMoveY=1;
                
                    setSubPuyoXY(currentPuyo,currentPuyoSub);
                    status="move";
                    move(currentPuyo);
                }else{
                    canFall=false;
                    
                }

                break;
            case "fallStart":
                puyos.add(currentPuyo);
                puyos.add(currentPuyoSub);
                backPuyos.put(calPuyoMap(currentPuyo.puyoX,currentPuyo.puyoY),currentPuyo);
                backPuyos.put(calPuyoMap(currentPuyo.puyoX,currentPuyo.puyoY),currentPuyoSub);
                currentPuyo=null;
                currentPuyoSub=null;
                status="fall";
                break;
            case "fall":
                status="kesu";
                break;
            case "kesu":

                status="summon";
                break;
        }
        //System.out.println(status);
        draw();
        return 0;
    }
    int calPuyoMap(int x,int y){
        return y*puyoMaxX+x;
    }
    void setSubPuyoXY(Puyopuyo main,Puyopuyo sub){
        sub.puyoX=main.puyoX;
        sub.puyoY=main.puyoY;
        switch (muki) {
            case 0 -> sub.puyoX += 1;
            case 1 -> sub.puyoY += 1;
            case 2 -> sub.puyoX -= 1;
            case 3 -> sub.puyoY -= 1;
        }
        sub.frameX=0;
        sub.frameY=0;
    }
    String getData(){
        return "";
    }
    void updateData(JSONObject obj){

    }
    void draw(){
        glBegin(GL_TRIANGLE_FAN);
        glColor4d(1, 1, 1, 1);
        glVertex2d(-1, -1);
        glVertex2d(-1, 1);
        glVertex2d(1, 1);
        glVertex2d(1, -1);
        glEnd();
//field
        glBegin(GL_QUADS);
        glColor4d(0.8,1,0.8,1);
        glVertex2d(calWinX(0,0),calWinY(2));
        glVertex2d(calWinX(0,0),calWinY(14));
        glVertex2d(calWinX(6,0),calWinY(14));
        glVertex2d(calWinX(6,0),calWinY(2));
        glEnd();
        //current puyo
        if(currentPuyo!=null) {
            drawPuyo(currentPuyo);
            if(currentPuyoSub!=null){
                drawPuyoSub(currentPuyo,currentPuyoSub,theta);
            }
        }

        for(Puyopuyo puyo:puyos){
            drawPuyo(puyo);
        }
    }
    void drawPuyo(Puyopuyo puyo){
        glBegin(GL_QUADS);
        glColor4d(1, 0, 0, 1);
        glVertex2d(calWinX(puyo.puyoX- puyo.puyoMoveX+ puyo.puyoMoveX* puyo.frameX/ puyo.frameMaxX, 0), calWinY(puyo.puyoY- puyo.puyoMoveY+ puyo.puyoMoveY* puyo.frameY/ puyo.frameMaxY));
        glVertex2d(calWinX(puyo.puyoX- puyo.puyoMoveX+ puyo.puyoMoveX* puyo.frameX/ puyo.frameMaxX , 0), calWinY(puyo.puyoY- puyo.puyoMoveY+ puyo.puyoMoveY* puyo.frameY/ puyo.frameMaxY+1));
        glVertex2d(calWinX(puyo.puyoX- puyo.puyoMoveX+ puyo.puyoMoveX* puyo.frameX/ puyo.frameMaxX+ 1, 0), calWinY(puyo.puyoY- puyo.puyoMoveY+ puyo.puyoMoveY* puyo.frameY/ puyo.frameMaxY+1));
        glVertex2d(calWinX(puyo.puyoX- puyo.puyoMoveX+ puyo.puyoMoveX* puyo.frameX/ puyo.frameMaxX+1, 0), calWinY(puyo.puyoY- puyo.puyoMoveY+ puyo.puyoMoveY* puyo.frameY/ puyo.frameMaxY));
        glEnd();

    }
    void drawPuyoSub(Puyopuyo puyo,Puyopuyo sub,double t){
        glBegin(GL_QUADS);
        glColor4d(1, 0, 0, 1);
        glVertex2d(calWinX(puyo.puyoX- puyo.puyoMoveX+ puyo.puyoMoveX* puyo.frameX/ puyo.frameMaxX+Math.cos(t), 0), calWinY(puyo.puyoY- puyo.puyoMoveY+ puyo.puyoMoveY* puyo.frameY/ puyo.frameMaxY+Math.sin(t)));
        glVertex2d(calWinX(puyo.puyoX- puyo.puyoMoveX+ puyo.puyoMoveX* puyo.frameX/ puyo.frameMaxX +Math.cos(t), 0), calWinY(puyo.puyoY- puyo.puyoMoveY+ puyo.puyoMoveY* puyo.frameY/ puyo.frameMaxY+1+Math.sin(t)));
        glVertex2d(calWinX(puyo.puyoX- puyo.puyoMoveX+ puyo.puyoMoveX* puyo.frameX/ puyo.frameMaxX+ 1+Math.cos(t), 0), calWinY(puyo.puyoY- puyo.puyoMoveY+ puyo.puyoMoveY* puyo.frameY/ puyo.frameMaxY+1+Math.sin(t)));
        glVertex2d(calWinX(puyo.puyoX- puyo.puyoMoveX+ puyo.puyoMoveX* puyo.frameX/ puyo.frameMaxX+1+Math.cos(t), 0), calWinY(puyo.puyoY- puyo.puyoMoveY+ puyo.puyoMoveY* puyo.frameY/ puyo.frameMaxY+Math.sin(t)));
        glEnd();

    }
    double calWinX(double x,int n){
        return -1+x*9.0/120+0.2;
    }
    double calWinY(double y){
        return 1-y*16.0/120+0.1;
    }
}
