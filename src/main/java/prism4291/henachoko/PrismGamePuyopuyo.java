package prism4291.henachoko;

import org.json.JSONObject;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.CallbackI;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;

public class PrismGamePuyopuyo {
    static int key_left=GLFW.GLFW_KEY_A;
    static int key_right=GLFW.GLFW_KEY_D;
    static int key_down=GLFW.GLFW_KEY_S;
    static int key_rotate_right=GLFW.GLFW_KEY_E;
    static int key_rotate_left=GLFW.GLFW_KEY_Q;
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
    int cooldown;
    int yokocooldown;
    int timenext;
    int frameTheta;
    int frameMaxTheta;
    double puyoRotate;
    int maxFallTime;
    int beforeRotate;
    boolean fastFall;
    int doubleRotate;
    long startTime;
    int kesuCheckY;
    boolean kesuFlag;
    Map<Integer,Integer> kesuMap;
    String tumoData;
    Random random;
    int tumoIndex;
    PrismGamePuyopuyo(){
        status="init";
        puyos=new ArrayList<>();
        muki = 3;
        theta=Math.PI*1.5;
        frameTheta=0;
        frameMaxTheta=10;
        puyoRotate=0;
        backPuyos=new HashMap<>();
        delay=0;
        yokocooldown=0;
        canFall=false;
        timenext=0;
        maxFallTime=-1;
        beforeRotate=0;
        fastFall=false;
        doubleRotate=0;
        startTime=-1;

        kesuFlag=false;
        tumoData="";
        random=new Random();
        tumoIndex=0;
    }
    void createPuyo(int color1, int color2){

        currentPuyo=new Puyopuyo(color1);
        currentPuyoSub=new Puyopuyo(color2);

    }
    class Puyopuyo {
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
        //JSONObject toJson(){

        //}
    }
    void move(Puyopuyo puyo){
        if (PrismGameVariable.KEY_BUTTON[key_rotate_right]==1||(beforeRotate==1&&PrismGameVariable.KEY_BUTTON[key_rotate_right]>0)) {
            if(checkCanRotateRight()) {
                puyoRotate+=Math.PI*0.5;
                frameTheta=0;
                frameMaxTheta=10;
                muki=(muki+1)%4;
                setTheta();
                setSubPuyoXY(currentPuyo, currentPuyoSub);
                status = "check";
            }
        }
        if (PrismGameVariable.KEY_BUTTON[key_rotate_left]==1||(beforeRotate==-1&&PrismGameVariable.KEY_BUTTON[key_rotate_left]>0)) {
            if(checkCanRotateLeft()) {
                puyoRotate-=Math.PI*0.5;
                frameTheta=0;
                frameMaxTheta=10;
                muki=(muki+3)%4;
                setTheta();
                setSubPuyoXY(currentPuyo, currentPuyoSub);
                status = "check";
            }
        }
        if(cooldown>0){
            cooldown--;
        }else {
            //System.out.println(PrismGameVariable.KEY_BUTTON[key_right]);
            if (PrismGameVariable.KEY_BUTTON[key_right]>0&&yokocooldown<=0) {
                if(checkCanMoveRight()) {
                    currentPuyo.puyoX += 1;
                    setSubPuyoXY(currentPuyo, currentPuyoSub);
                    cooldown = 3;
                    if(PrismGameVariable.KEY_BUTTON[key_right]>0){
                        yokocooldown=5;
                        if(PrismGameVariable.KEY_BUTTON[key_right]<=3) {
                            yokocooldown = 15;
                        }else if(PrismGameVariable.KEY_BUTTON[key_right]<=15) {
                            yokocooldown = 10;
                        }
                    }
                    status = "check";
                    //setFrameY(40);
                }
            }
            if (PrismGameVariable.KEY_BUTTON[key_left] >0&&yokocooldown<=0) {
                if(checkCanMoveLeft()) {
                    currentPuyo.puyoX -= 1;
                    setSubPuyoXY(currentPuyo, currentPuyoSub);
                    cooldown = 3;
                    if(PrismGameVariable.KEY_BUTTON[key_left]>0){
                        yokocooldown=5;
                        if(PrismGameVariable.KEY_BUTTON[key_left]<=3) {
                            yokocooldown = 15;
                        }else if(PrismGameVariable.KEY_BUTTON[key_left]<=15) {
                            yokocooldown = 10;
                        }
                    }
                    status = "check";
                    //setFrameY(40);
                }
            }

            if (cooldown==0&&PrismGameVariable.KEY_BUTTON[key_down] > 0) {
                fastFall=true;

            }
        }
        if(PrismGameVariable.KEY_BUTTON[key_down]<=0){
            fastFall=false;
        }

        beforeRotate=0;
        puyo.frameX+=1;
        if(puyo.puyoMoveX!=0&&puyo.frameX>=puyo.frameMaxX){
            puyo.puyoMoveX=0;
        }
        frameTheta+=1;
        if(puyoRotate!=0&&frameTheta>=frameMaxTheta){
            puyoRotate=0;
        }
        puyo.frameY+=1;
        if(fastFall&&cooldown==0){
            puyo.frameY+=9;
        }
        if(puyo.puyoMoveY!=0&&puyo.frameY>=puyo.frameMaxY){
            puyo.puyoMoveY=0;
            status="check";
        }
        //System.out.println(delay);
        if(puyo.puyoMoveY!=0){
            delay=0;
        }else{
            delay+=1;
            if(fastFall){
                delay+=25;
            }
            if(delay>=delayMax){
                status="fallStart";
                setSubPuyoXY(currentPuyo,currentPuyoSub);
            }

        }

    }
    void setTheta(){
        theta=Math.PI*muki*0.5;
    }
//    void setFrameY(int n){
//        currentPuyo.frameY=currentPuyo.frameY*n/currentPuyo.frameMaxY;
//        currentPuyo.frameMaxY=n;
//
//    }
    int PuyoLoop(){
        if(yokocooldown>0){
            yokocooldown--;
        }
        switch (status){
            case "init":
                if(PrismGameVariable.isHost) {
                    for (int i = 0; i < 256; i++) {
                        tumoData += random.nextInt(4);
                    }
                    startTime = System.currentTimeMillis() + 5000;
                    JSONObject msg = new JSONObject();
                    msg.put("from",PrismGameVariable.socketId);
                    msg.put("type","init");
                    msg.put("startTime",startTime);
                    msg.put("tumoData",tumoData);
                    PrismGameVariable.socket.emit("clientRoomMessage",msg );
                    status = "ready";
                    tumoIndex=0;
                }else if(startTime>=0){
                    status="ready";
                }
                break;
            case "ready":
                if(System.currentTimeMillis()>=startTime) {
                    status="go";
                }
                break;
            case "go":
                status="summon";
                beforeRotate=0;
                break;
            case "summon":
                timenext++;
                if(PrismGameVariable.KEY_BUTTON[key_rotate_right]==1){
                    beforeRotate=1;
                }else if(PrismGameVariable.KEY_BUTTON[key_rotate_left]==1){
                    beforeRotate=-1;
                }
                if(timenext>=30) {

                    createPuyo(Integer.parseInt(tumoData.substring(tumoIndex,tumoIndex+1)),Integer.parseInt(tumoData.substring(tumoIndex+1,tumoIndex+2)));
                    tumoIndex+=2;
                    if(tumoIndex>=tumoData.length()){
                        tumoIndex=0;
                    }
                    delay = 0;
                    canFall = true;
                    status = "move";
                    cooldown = 0;
                    timenext = 0;
                    muki=3;
                    theta=Math.PI*1.5;
                    puyoRotate=0;
                    doubleRotate=0;
                }
                break;
            case "move":
                move(currentPuyo);

                break;
            case "check":
                //System.out.println("check "+currentPuyo.frameY);
                setSubPuyoXY(currentPuyo, currentPuyoSub);

                if(currentPuyo.puyoMoveY==0) {


                    if (checkCanFall()) {
                        canFall=true;
                        currentPuyo.frameY=0;
                        currentPuyo.frameMaxY = 40;
                        currentPuyo.puyoY += 1;
                        currentPuyo.puyoMoveY = setAdditionalMoveY(1);

                        setSubPuyoXY(currentPuyo, currentPuyoSub);
                        status = "move";

                    } else {
                        canFall = false;
                        status = "move";
                        //currentPuyo.puyoMoveY = 0;
                    }
                }
                move(currentPuyo);

                break;
            case "fallStart":
                currentPuyo.puyoMoveX=0;
                currentPuyo.puyoMoveY=0;
                puyoRotate=0;
                puyos.add(currentPuyo);
                puyos.add(currentPuyoSub);
                backPuyos.put(calPuyoMap(currentPuyo.puyoX,currentPuyo.puyoY),currentPuyo);
                backPuyos.put(calPuyoMap(currentPuyoSub.puyoX,currentPuyoSub.puyoY),currentPuyoSub);
                currentPuyo=null;
                currentPuyoSub=null;
                status="fall";
                break;
            case "fall":
                if(maxFallTime<0){
                    maxFallTime=1;
                    for(int i=0;i<puyoMaxX;i++){
                        int tatePuyos=0;
                        for(int j=puyoMaxY-1;j>=0;j--){
                            if(backPuyos.get(calPuyoMap(i,j))!=null){
                                if(puyoMaxY-j-1-tatePuyos>0){
                                    backPuyos.get(calPuyoMap(i,j)).puyoY=puyoMaxY-1-tatePuyos;
                                    backPuyos.put(calPuyoMap(i,puyoMaxY-1-tatePuyos),backPuyos.get(calPuyoMap(i,j)));
                                    backPuyos.remove(calPuyoMap(i,j));
                                }
                                tatePuyos+=1;
                            }
                        }
                    }
                }else if(maxFallTime==0){
                    status="kesu";
                    kesuCheckY=0;
                    maxFallTime=-1;
                }else{
                    maxFallTime-=1;
                }
                break;
            case "kesu":
                if(kesuCheckY==0){
                    createKesuMap();
                }
                if(kesuCheckY<puyoMaxY){
                    for(int x=0;x<puyoMaxX;x++){
                        Puyopuyo puyopuyo=backPuyos.get(calPuyoMap(x,kesuCheckY));
                        if(puyopuyo!=null&&kesuMap.get(calPuyoMap(x,kesuCheckY))==0){
                            int kosuu=kesuCheck(x,kesuCheckY,puyopuyo,1,false);
                            System.out.println(kosuu);
                            if(kosuu>=4){
                                kesuCheck(x,kesuCheckY,puyopuyo,1,true);
                            }
                        }
                    }
                    kesuCheckY+=1;
                }else{
                    kesuFlag=false;
                    for(int x=0;x<puyoMaxX;x++){
                        for(int y=0;y<puyoMaxY;y++){
                            if(kesuMap.get(calPuyoMap(x,y))>=2){
                                for(int i=0;i<puyos.size();i++){
                                    if(puyos.get(i)==backPuyos.get(calPuyoMap(x,y))){
                                        puyos.remove(i);
                                        break;
                                    }
                                }
                                backPuyos.remove(calPuyoMap(x,y));
                                kesuFlag=true;
                            }
                        }
                    }
                    if(kesuFlag){
                        status="fall";
                    }else{
                        status="summon";
                        beforeRotate=0;
                    }


                }


                break;
        }
        //System.out.println(status);
        draw();
        return 0;
    }
    int kesuCheck(int x,int y,Puyopuyo puyopuyo,int kosuu,boolean flag){
        if(flag){
            kesuMap.put(calPuyoMap(x,y),2);
        }else{
            kesuMap.put(calPuyoMap(x,y),1);
        }

        int addX=1;
        int addY=0;
        Puyopuyo puyo;
        for(int n=0;n<4;n++) {
            switch (n) {
                case 1 -> {
                    addX = 0;
                    addY = 1;
                }
                case 2 -> {
                    addX = -1;
                    addY = 0;
                }
                case 3 -> {
                    addX = 0;
                    addY = -1;
                }
            }

            puyo = backPuyos.get(calPuyoMap(x + addX, y + addY));
            if (puyo != null) {
                if (puyopuyo.puyoColor == puyo.puyoColor&&(kesuMap.get(calPuyoMap(x + addX, y + addY))==0||(flag&&kesuMap.get(calPuyoMap(x + addX, y + addY))==1))) {
                    kosuu += 1;
                    kosuu = kesuCheck(x + addX, y + addY, puyopuyo, kosuu, flag);
                }
            }
        }
        return kosuu;
    }

    void createKesuMap(){
        kesuMap=new HashMap<>();
        for(int i=0;i<puyoMaxX;i++){
            for(int j=0;j<puyoMaxY;j++){
                kesuMap.put(calPuyoMap(i,j),0);
            }
        }
    }
    boolean checkCanFall(){
        return currentPuyo.puyoY<puyoMaxY-1&&currentPuyoSub.puyoY<puyoMaxY-1&&backPuyos.get(calPuyoMap(currentPuyo.puyoX,currentPuyo.puyoY+1))==null&&backPuyos.get(calPuyoMap(currentPuyoSub.puyoX,currentPuyoSub.puyoY+1))==null;
    }
    double setAdditionalMoveX(int ax){
        delay=0;
         return ax+(currentPuyo.puyoMoveX-currentPuyo.puyoMoveX*currentPuyo.frameX/currentPuyo.frameMaxX);
    }
    double setAdditionalMoveY(int ay){
        return ay+(currentPuyo.puyoMoveY-currentPuyo.puyoMoveY*currentPuyo.frameY/currentPuyo.frameMaxY);
    }
    boolean checkCanMoveRight(){
        if(currentPuyo.puyoX<puyoMaxX-1&&currentPuyoSub.puyoX<puyoMaxX-1&&backPuyos.get(calPuyoMap(currentPuyo.puyoX+1,currentPuyo.puyoY))==null&&backPuyos.get(calPuyoMap(currentPuyoSub.puyoX+1,currentPuyoSub.puyoY))==null){

            currentPuyo.puyoMoveX=setAdditionalMoveX(1);
            currentPuyo.frameX=0;
            currentPuyo.frameMaxX=5;
            return true;
        }else{
            return false;
        }
    }
    boolean checkCanMoveLeft(){
        if(currentPuyo.puyoX>0&&currentPuyoSub.puyoX>0&&backPuyos.get(calPuyoMap(currentPuyo.puyoX-1,currentPuyo.puyoY))==null&&backPuyos.get(calPuyoMap(currentPuyoSub.puyoX-1,currentPuyoSub.puyoY))==null){
            currentPuyo.puyoMoveX=setAdditionalMoveX(-1);
            currentPuyo.frameX=0;
            currentPuyo.frameMaxX=5;
            return true;
        }else{
            return false;
        }
    }
    boolean checkCanRotateLeft(){
        int hoseiX=0;
        int hoseiY=0;
        boolean canR=false;
        if(muki==0){
            canR=true;
        }else if(muki==1){
            if(currentPuyo.puyoX<puyoMaxX-1&&backPuyos.get(calPuyoMap(currentPuyo.puyoX+1,currentPuyo.puyoY))==null){
                canR=true;
            }else if(currentPuyo.puyoX>0&&backPuyos.get(calPuyoMap(currentPuyo.puyoX-1,currentPuyo.puyoY))==null){
                canR=true;
                hoseiX=-1;
            }else{
                doubleRotate+=1;
                if(doubleRotate>=2) {
                    doubleRotate=0;
                    canR = true;
                    muki += 3;
                    puyoRotate -= Math.PI * 0.5;
                }
            }
        }else if(muki==2){
            if(currentPuyo.puyoY<puyoMaxY-1&&backPuyos.get(calPuyoMap(currentPuyo.puyoX,currentPuyo.puyoY+1))==null){
                canR=true;
            }else{
                canR=true;
                hoseiY=-1;
            }
        }else if(muki==3){
            if(currentPuyo.puyoX>0&&backPuyos.get(calPuyoMap(currentPuyo.puyoX-1,currentPuyo.puyoY))==null){
                canR=true;
            }else if(currentPuyo.puyoX<puyoMaxX-1&&backPuyos.get(calPuyoMap(currentPuyo.puyoX+1,currentPuyo.puyoY))==null){
                canR=true;
                hoseiX=1;
            }else{
                doubleRotate+=1;
                if(doubleRotate>=2) {
                    doubleRotate=0;

                    canR = true;
                    muki += 3;
                    puyoRotate -= Math.PI * 0.5;
                    if(!(currentPuyo.puyoY<puyoMaxY-1&&backPuyos.get(calPuyoMap(currentPuyo.puyoX,currentPuyo.puyoY+1))==null)){
                        hoseiY=-1;
                    }
                }
            }
        }
        if(hoseiX!=0){
            currentPuyo.puyoX+=hoseiX;
            currentPuyo.puyoMoveX=setAdditionalMoveX(hoseiX);
            currentPuyo.frameX=0;
            currentPuyo.frameMaxX=5;
        }
        if(hoseiY!=0){
            currentPuyo.puyoY+=hoseiY;
            currentPuyo.puyoMoveY=setAdditionalMoveY(hoseiY);
            currentPuyo.frameY=0;
            currentPuyo.frameMaxY=5;
        }
        return canR;

    }
    boolean checkCanRotateRight(){
        int hoseiX=0;
        int hoseiY=0;
        boolean canR=false;
        if(muki==0){
            if(currentPuyo.puyoY<puyoMaxY-1&&backPuyos.get(calPuyoMap(currentPuyo.puyoX,currentPuyo.puyoY+1))==null){
                canR=true;
            }else{
                canR=true;
                hoseiY=-1;
            }
        }else if(muki==1){
            if(currentPuyo.puyoX>0&&backPuyos.get(calPuyoMap(currentPuyo.puyoX-1,currentPuyo.puyoY))==null){
                canR=true;
            }else if(currentPuyo.puyoX<puyoMaxX-1&&backPuyos.get(calPuyoMap(currentPuyo.puyoX+1,currentPuyo.puyoY))==null){
                canR=true;
                hoseiX=1;
            }else{
                doubleRotate+=1;
                if(doubleRotate>=2){
                    doubleRotate=0;
                    canR=true;
                    muki+=1;
                    puyoRotate+=Math.PI*0.5;
                }
            }
        }else if(muki==2){
            canR=true;
        }else if(muki==3){
            if(currentPuyo.puyoX<puyoMaxX-1&&backPuyos.get(calPuyoMap(currentPuyo.puyoX+1,currentPuyo.puyoY))==null){
                canR=true;
            }else if(currentPuyo.puyoX>0&&backPuyos.get(calPuyoMap(currentPuyo.puyoX-1,currentPuyo.puyoY))==null){
                canR=true;
                hoseiX=-1;
            }else{
                doubleRotate+=1;
                if(doubleRotate>=2){
                    doubleRotate=0;
                    canR=true;
                    muki+=1;
                    puyoRotate+=Math.PI*0.5;
                    if(!(currentPuyo.puyoY<puyoMaxY-1&&backPuyos.get(calPuyoMap(currentPuyo.puyoX,currentPuyo.puyoY+1))==null)){
                        hoseiY=-1;
                    }
                }
            }
        }
        if(hoseiX!=0){
            currentPuyo.puyoX+=hoseiX;
            currentPuyo.puyoMoveX=setAdditionalMoveX(hoseiX);
            currentPuyo.frameX=0;
            currentPuyo.frameMaxX=5;
        }
        if(hoseiY!=0){
            currentPuyo.puyoY+=hoseiY;
            currentPuyo.puyoMoveY=setAdditionalMoveY(hoseiY);
            currentPuyo.frameY=0;
            currentPuyo.frameMaxY=5;
        }
        return canR;
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
        sub.puyoMoveX=0;
        sub.puyoMoveY=0;

    }
    JSONObject getData(){
        JSONObject jo=new JSONObject();
        //jo.put("puyo",puyos);
        return jo;
    }
    void PuyoSend(){
        JSONObject msg=new JSONObject();
        msg.put("from",socketId);
        msg.put("type","loop");
        msg.put("data",getData());
        PrismGameVariable.socket.emit("clientRoomMessage",msg );
    }
    void updateData(JSONObject jo){
        if(jo.getString("type").equals("init")){
            startTime=jo.getLong("startTime");
            tumoData= jo.getString("tumoData");
        }else if(jo.getString("type").equals("loop")){
            
        }
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


        for(Puyopuyo puyo:puyos){
            drawPuyo(puyo);
        }
        if(currentPuyo!=null) {
            drawPuyo(currentPuyo);
            if(currentPuyoSub!=null){
                drawPuyoSub(currentPuyo,currentPuyoSub,theta-puyoRotate+puyoRotate*frameTheta/frameMaxTheta);
            }
        }
    }
    void changePuyoColor(int c){
        switch (c) {
            case 0 -> glColor4d(1, 0, 0, 1);
            case 1 -> glColor4d(0, 1, 0, 1);
            case 2 -> glColor4d(0, 0, 1, 1);
            case 3 -> glColor4d(1, 1, 0, 1);

        }
    }
    void drawPuyo(Puyopuyo puyo){
        glBegin(GL_QUADS);
        changePuyoColor(puyo.puyoColor);
        glVertex2d(calWinX(puyo.puyoX- puyo.puyoMoveX+ puyo.puyoMoveX* puyo.frameX/ puyo.frameMaxX, 0), calWinY(puyo.puyoY- puyo.puyoMoveY+ puyo.puyoMoveY* puyo.frameY/ puyo.frameMaxY));
        glVertex2d(calWinX(puyo.puyoX- puyo.puyoMoveX+ puyo.puyoMoveX* puyo.frameX/ puyo.frameMaxX , 0), calWinY(puyo.puyoY- puyo.puyoMoveY+ puyo.puyoMoveY* puyo.frameY/ puyo.frameMaxY+1));
        glVertex2d(calWinX(puyo.puyoX- puyo.puyoMoveX+ puyo.puyoMoveX* puyo.frameX/ puyo.frameMaxX+ 1, 0), calWinY(puyo.puyoY- puyo.puyoMoveY+ puyo.puyoMoveY* puyo.frameY/ puyo.frameMaxY+1));
        glVertex2d(calWinX(puyo.puyoX- puyo.puyoMoveX+ puyo.puyoMoveX* puyo.frameX/ puyo.frameMaxX+1, 0), calWinY(puyo.puyoY- puyo.puyoMoveY+ puyo.puyoMoveY* puyo.frameY/ puyo.frameMaxY));
        glEnd();
//
//        glBegin(GL_QUADS);
//        glColor4d(0, 1, 0, 1);
//        glVertex2d(calWinX(puyo.puyoX, 0), calWinY(puyo.puyoY));
//        glVertex2d(calWinX(puyo.puyoX, 0), calWinY(puyo.puyoY+1));
//        glVertex2d(calWinX(puyo.puyoX+ 1, 0), calWinY(puyo.puyoY+1));
//        glVertex2d(calWinX(puyo.puyoX+1, 0), calWinY(puyo.puyoY));
//        glEnd();

    }
    void drawPuyoSub(Puyopuyo puyo,Puyopuyo sub,double t){
        glBegin(GL_QUADS);
        changePuyoColor(sub.puyoColor);
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
