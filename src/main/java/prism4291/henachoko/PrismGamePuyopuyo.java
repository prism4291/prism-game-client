package prism4291.henachoko;

import com.google.gson.JsonElement;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
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
    Map<String,Puyopuyo> opponentCurrentPuyo;
    Map<String,Puyopuyo> opponentCurrentPuyoSub;
    List<Puyopuyo> puyos;
    Map<String,List<Puyopuyo>> opponentPuyos;
    //Map<Integer,String> opponents;
    Map<Integer,Puyopuyo> backPuyos;
    static int puyoMaxY=14;
    static int puyoMaxX=6;
    static int delayMax=50;
    static int ojamaRate=70;
    static int maxOjama=30;
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
    int rensaSuu;
    int kosuuBonus;
    int colorBonus;
    int rensaBonus;
    int kosuuCount;
    int totalBonus;
    Set<Integer> kesuColors;
    Map<String,Double> opponentThetaData;
    Map<String,Integer> ojamaIndex;
    Map<String,List<Integer>> myOjama;
    Map<String,List<Boolean>> ojamaStarting;
    boolean ojamaFlag;
    PrismGamePuyopuyo(){
        status="init";
        puyos=new ArrayList<>();
        opponentPuyos=new HashMap<>();
        opponentCurrentPuyo=new HashMap<>();
        opponentCurrentPuyoSub=new HashMap<>();
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
        opponentThetaData=new HashMap<>();
        kesuFlag=false;
        tumoData="";
        random=new Random();
        tumoIndex=0;
        rensaSuu=0;
        kosuuBonus=0;
        colorBonus=0;
        rensaBonus=0;
        kosuuCount=0;
        kesuColors = new HashSet<>();
        totalBonus=0;
        ojamaIndex=new HashMap<>();
        myOjama=new HashMap<>();
        ojamaStarting=new HashMap<>();
        ojamaFlag=true;
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
        JSONObject toJson(){
            JSONObject jo=new JSONObject();
            jo.put("puyoColor",puyoColor);
            jo.put("puyoMoveX",puyoMoveX);
            jo.put("puyoMoveY",puyoMoveY);
            jo.put("frameMaxX",frameMaxX);
            jo.put("frameMaxY",frameMaxY);
            jo.put("frameX",frameX);
            jo.put("frameY",frameY);
            jo.put("puyoX",puyoX);
            jo.put("puyoY",puyoY);
            return jo;
        }
        Puyopuyo(JSONObject jo){
            puyoColor = jo.getInt("puyoColor");
            puyoMoveX = jo.getDouble("puyoMoveX");
            puyoMoveY = jo.getDouble("puyoMoveY");
            frameMaxX=jo.getInt("frameMaxX");
            frameMaxY=jo.getInt("frameMaxY");
            frameY=jo.getInt("frameY");
            frameX=jo.getInt("frameX");
            puyoX = jo.getInt("puyoX");
            puyoY = jo.getInt("puyoY");
        }
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
        if(fastFall&&cooldown==0){
            puyo.frameY+=9;
        }
        int res=moveFrame(puyo);
        if(res==1){
            status="check";
        }


        frameTheta+=1;
        if(puyoRotate!=0&&frameTheta>=frameMaxTheta){
            puyoRotate=0;
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
    int moveFrame(Puyopuyo puyo){
        puyo.frameX+=1;
        if(puyo.puyoMoveX!=0&&puyo.frameX>=puyo.frameMaxX){
            puyo.puyoMoveX=0;
        }
        puyo.frameY+=1;
        if(puyo.puyoMoveY!=0&&puyo.frameY>=puyo.frameMaxY){
            puyo.puyoMoveY=0;

            return 1;
        }
        return 0;
    }
    void setTheta(){
        theta=Math.PI*muki*0.5;
    }
    boolean hasOjama(){
        int n=0;
        if(myOjama!=null) {
            for (String str : myOjama.keySet()) {
                if (str.equals(PrismGameVariable.userName)) {
                    continue;
                }
                for (int i = 0; i < myOjama.get(str).size();i++){
                    if(ojamaStarting.get(str).get(i)) {
                        n += myOjama.get(str).get(i);
                    }
                }
            }
        }
        return n>0;
    }
    void ojamaRakka(){
        int n=0;
        if(myOjama!=null) {
            for (String str : myOjama.keySet()) {
                if (str.equals(PrismGameVariable.userName)) {
                    continue;
                }
                for (int i = 0; i < myOjama.get(str).size();i++){
                    if(ojamaStarting.get(str).get(i)) {
                        if(n + myOjama.get(str).get(i)<=maxOjama){
                            n += myOjama.get(str).get(i);
                            myOjama.get(str).set(i,0);
                        }else{
                            myOjama.get(str).set(i,myOjama.get(str).get(i)-(maxOjama-n));
                            n=maxOjama;
                            break;
                        }

                    }
                }
                if(n>maxOjama){
                    break;
                }
            }
            for(int i=0;i<n;i++) {
                putOjama(i%6);
            }

        }

    }
    void putOjama(int x){
        Puyopuyo puyopuyo=new Puyopuyo(5);
        for(int y=puyoMaxY-1;y>=0;y--){
            if(backPuyos.get(calPuyoMap(x,y))==null){
                puyos.add(puyopuyo);
                backPuyos.put(calPuyoMap(x,y),puyopuyo);
                puyopuyo.puyoX=x;
                puyopuyo.puyoY=y;
                puyopuyo.frameMaxX=1;
                puyopuyo.frameMaxY=50;
                maxFallTime=Math.max(maxFallTime,puyopuyo.frameMaxY);
                puyopuyo.frameX=0;
                puyopuyo.frameY=0;
                puyopuyo.puyoMoveX=0;
                puyopuyo.puyoMoveY=13;
                break;
            }
        }
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
                    msg.put("from",PrismGameVariable.userName);
                    msg.put("type","init");
                    msg.put("startTime",startTime);
                    msg.put("tumoData",tumoData);
                    msg.put("time",System.currentTimeMillis());
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
                ojamaFlag=true;
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
                rensaSuu=0;
                break;
            case "fall":
                if(maxFallTime<0){
                    maxFallTime=1;
                    for(int i=0;i<puyoMaxX;i++){
                        int tatePuyos=0;
                        for(int j=puyoMaxY-1;j>=0;j--){
                            if(backPuyos.get(calPuyoMap(i,j))!=null){
                                if(puyoMaxY-j-1-tatePuyos>0){
                                    Puyopuyo fallingPuyo=backPuyos.get(calPuyoMap(i,j));
                                    fallingPuyo.puyoY=puyoMaxY-1-tatePuyos;
                                    fallingPuyo.puyoMoveY=puyoMaxY-j-1-tatePuyos;
                                    fallingPuyo.frameMaxY=5*(puyoMaxY-j-1-tatePuyos)+20;
                                    fallingPuyo.frameY=0;
                                    maxFallTime=Math.max(maxFallTime,fallingPuyo.frameMaxY);
                                    backPuyos.put(calPuyoMap(i,puyoMaxY-1-tatePuyos),fallingPuyo);
                                    backPuyos.remove(calPuyoMap(i,j));
                                }
                                tatePuyos+=1;
                            }
                        }
                    }
                }else if(maxFallTime==0){
                    status="kesu";
                    kesuCheckY=2;
                    maxFallTime=-1;

                }else{
                    maxFallTime-=1;
                    for(Puyopuyo puyo:puyos){
                        moveFrame(puyo);
                    }
                }
                break;
            case "kesu":
                if(kesuCheckY==2){
                    createKesuMap();
                    kosuuBonus=0;
                    colorBonus=0;
                    rensaBonus=0;
                    kosuuCount=0;
                    kesuColors.clear();
                }
                if(kesuCheckY<puyoMaxY){
                    for(int x=0;x<puyoMaxX;x++){
                        Puyopuyo puyopuyo=backPuyos.get(calPuyoMap(x,kesuCheckY));
                        if(puyopuyo!=null&&kesuMap.get(calPuyoMap(x,kesuCheckY))==0){
                            if(puyopuyo.puyoColor<=4) {
                                int kosuu = kesuCheck(x, kesuCheckY, puyopuyo, 1, false);
                                //System.out.println(kosuu);
                                if (kosuu >= 4) {
                                    kesuCheck(x, kesuCheckY, puyopuyo, 1, true);
                                    kosuuCount += kosuu;
                                    if (kosuu >= 11) {
                                        kosuuBonus += 10;
                                    } else if (kosuu >= 5) {
                                        kosuuBonus += kosuu - 3;
                                    }
                                    kesuColors.add(puyopuyo.puyoColor);
                                }
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
                        rensaSuu+=1;
                        for(int i=1;i<rensaSuu;i++){
                            if(i<=2){
                                rensaBonus+=8;
                            }else if(i==3){
                                rensaBonus+=16;
                            }else{
                                rensaBonus+=32;
                            }
                        }
                        for(int i=1;i<kesuColors.size();i++){
                            if(colorBonus==0){
                                colorBonus=3;
                            }else{
                                colorBonus*=2;
                            }
                        }
                        totalBonus=kosuuCount*10*Math.max(1,rensaBonus+kosuuBonus+colorBonus);
                        System.out.println("kosuu  "+kosuuCount+"  rensa  "+rensaBonus+"  kosuu  "+kosuuBonus+"  color  "+colorBonus);
                        sendOjama(totalBonus,rensaSuu,false);
                        status="fall";
                    }else{
                        if(rensaSuu>0) {
                            sendOjama(0, rensaSuu, true);
                        }
                        if(ojamaFlag) {
                            if (hasOjama()) {
                                ojamaRakka();
                                ojamaFlag=false;
                                status="fall";
                            }else{
                                status="summon";
                            }
                        }else{
                            ojamaFlag=true;
                            status="summon";
                        }

                        beforeRotate=0;
                    }


                }


                break;
        }
        //System.out.println(status);
        draw();
        return 0;
    }
    void sendOjama(int n,int r,boolean end){
        JSONObject msg = new JSONObject();
        msg.put("from",PrismGameVariable.userName);
        msg.put("type","ojama");
        msg.put("ojamaKosuu",n/ojamaRate);
        msg.put("ojamaStart",r<=1);
        msg.put("ojamaLast",end);
        msg.put("time",System.currentTimeMillis());
        PrismGameVariable.socket.emit("clientRoomMessage",msg );
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
            if(x+addX>=0&&x+addX<puyoMaxX&&y+addY>=2&&y+addY<puyoMaxY) {
                puyo = backPuyos.get(calPuyoMap(x + addX, y + addY));
                if (puyo != null) {
                    if (puyopuyo.puyoColor == puyo.puyoColor && (kesuMap.get(calPuyoMap(x + addX, y + addY)) == 0 || (flag && kesuMap.get(calPuyoMap(x + addX, y + addY)) == 1))) {
                        kosuu += 1;
                        kosuu = kesuCheck(x + addX, y + addY, puyopuyo, kosuu, flag);
                    }else if(flag&&puyo.puyoColor==5){
                        kesuMap.put(calPuyoMap(x + addX, y + addY),2);
                    }

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
        JSONArray puyoArray=new JSONArray();
        for(Puyopuyo puyopuyo:puyos){
            puyoArray.put(puyopuyo.toJson());
        }
        jo.put("puyos",puyoArray);
        if(currentPuyo!=null) {
            jo.put("currentPuyo", currentPuyo.toJson());
            if(currentPuyoSub!=null) {
                jo.put("currentPuyoSub", currentPuyoSub.toJson());
            }
        }
        jo.put("thetaData",theta-puyoRotate+puyoRotate*frameTheta/frameMaxTheta);
        return jo;
    }
    void PuyoSend(){
        JSONObject msg=new JSONObject();
        msg.put("from",PrismGameVariable.userName);
        msg.put("type","loop");
        msg.put("data",getData());
        msg.put("time",System.currentTimeMillis());
        PrismGameVariable.socket.emit("clientRoomMessage",msg );
    }
    void updateData(JSONObject jo){
        if(jo.getString("type").equals("init")){
            startTime=jo.getLong("startTime");
            tumoData= jo.getString("tumoData");
        }else if(jo.getString("type").equals("loop")){
            String dataFrom=jo.getString("from");


            opponentPuyos.put(dataFrom,new ArrayList<>());
            JSONObject data=jo.getJSONObject("data");
            for(Object obj:data.getJSONArray("puyos")){
                Puyopuyo puyo=new Puyopuyo((JSONObject) obj);
                opponentPuyos.get(dataFrom).add(puyo);
            }
            opponentCurrentPuyo.remove(dataFrom);
            opponentCurrentPuyoSub.remove(dataFrom);
            if(data.has("currentPuyo")) {
                opponentCurrentPuyo.put(dataFrom, new Puyopuyo(data.getJSONObject("currentPuyo")));
                if(data.has("currentPuyoSub")) {
                    opponentCurrentPuyoSub.put(dataFrom, new Puyopuyo(data.getJSONObject("currentPuyoSub")));
                }
            }
            opponentThetaData.put(dataFrom,data.getDouble("thetaData"));

        }else if(jo.getString("type").equals("ojama")){
            String dataFrom=jo.getString("from");
            if(!ojamaIndex.containsKey(dataFrom)){
                ojamaIndex.put(dataFrom,0);
            }
            if(!myOjama.containsKey(dataFrom)){
                myOjama.put(dataFrom,new ArrayList<>());
            }
            if(!ojamaStarting.containsKey(dataFrom)){
                ojamaStarting.put(dataFrom,new ArrayList<>());
            }
            System.out.println(jo);
            if(jo.getBoolean("ojamaStart")) {
                ojamaStarting.get(dataFrom).add(false);
                myOjama.get(dataFrom).add(jo.getInt("ojamaKosuu"));
                ojamaIndex.put(dataFrom,myOjama.get(dataFrom).size()-1);
            }else{
                myOjama.get(dataFrom).set(ojamaIndex.get(dataFrom),myOjama.get(dataFrom).get(ojamaIndex.get(dataFrom))+jo.getInt("ojamaKosuu"));
            }
            if(jo.getBoolean("ojamaLast")){
                ojamaStarting.get(dataFrom).set(ojamaIndex.get(dataFrom),true);

            }
            System.out.println(myOjama.get(dataFrom));
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
        glVertex2d(calWinX(0,0),calWinY(2,0));
        glVertex2d(calWinX(0,0),calWinY(14,0));
        glVertex2d(calWinX(6,0),calWinY(14,0));
        glVertex2d(calWinX(6,0),calWinY(2,0));
        glEnd();
        //current puyo


        for(Puyopuyo puyo:puyos){
            drawPuyo(puyo,0);
        }
        if(currentPuyo!=null) {
            //System.out.println("current "+currentPuyo.frameY);
            drawPuyo(currentPuyo,0);
            if(currentPuyoSub!=null){
                drawPuyoSub(currentPuyo,currentPuyoSub,theta-puyoRotate+puyoRotate*frameTheta/frameMaxTheta,0);
            }
        }

        //opponent

        glBegin(GL_QUADS);
        glColor4d(0.8,1,0.8,1);
        glVertex2d(calWinX(0,1),calWinY(2,1));
        glVertex2d(calWinX(0,1),calWinY(14,1));
        glVertex2d(calWinX(6,1),calWinY(14,1));
        glVertex2d(calWinX(6,1),calWinY(2,1));
        glEnd();
        int n=1;
        for(String opponent:opponentPuyos.keySet()){
            if(opponent.equals(PrismGameVariable.userName)){
                continue;
            }
            for(Puyopuyo puyopuyo:opponentPuyos.get(opponent)){
                drawPuyo(puyopuyo,n);
            }

            if(opponentCurrentPuyo.containsKey(opponent)) {
                //System.out.println("opponent "+opponentCurrentPuyo.get(opponent).frameY);
                drawPuyo(opponentCurrentPuyo.get(opponent),n);
                if(opponentCurrentPuyoSub.containsKey(opponent)){
                    drawPuyoSub(opponentCurrentPuyo.get(opponent),opponentCurrentPuyoSub.get(opponent),opponentThetaData.get(opponent),n);
                }
            }
            n++;
        }


    }
    void changePuyoColor(int c){
        switch (c) {
            case 0 -> glColor4d(1, 0, 0, 1);
            case 1 -> glColor4d(0, 1, 0, 1);
            case 2 -> glColor4d(0, 0, 1, 1);
            case 3 -> glColor4d(1, 1, 0, 1);
            case 5 -> glColor4d(0.8,0.8,0.8,1);

        }
    }
    void drawPuyo(Puyopuyo puyo,int n){
        glBegin(GL_QUADS);
        changePuyoColor(puyo.puyoColor);
        glVertex2d(calWinX(puyo.puyoX- puyo.puyoMoveX+ puyo.puyoMoveX* puyo.frameX/ puyo.frameMaxX, n), calWinY(puyo.puyoY- puyo.puyoMoveY+ puyo.puyoMoveY* puyo.frameY/ puyo.frameMaxY,n));
        glVertex2d(calWinX(puyo.puyoX- puyo.puyoMoveX+ puyo.puyoMoveX* puyo.frameX/ puyo.frameMaxX , n), calWinY(puyo.puyoY- puyo.puyoMoveY+ puyo.puyoMoveY* puyo.frameY/ puyo.frameMaxY+1,n));
        glVertex2d(calWinX(puyo.puyoX- puyo.puyoMoveX+ puyo.puyoMoveX* puyo.frameX/ puyo.frameMaxX+ 1, n), calWinY(puyo.puyoY- puyo.puyoMoveY+ puyo.puyoMoveY* puyo.frameY/ puyo.frameMaxY+1,n));
        glVertex2d(calWinX(puyo.puyoX- puyo.puyoMoveX+ puyo.puyoMoveX* puyo.frameX/ puyo.frameMaxX+1, n), calWinY(puyo.puyoY- puyo.puyoMoveY+ puyo.puyoMoveY* puyo.frameY/ puyo.frameMaxY,n));
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
    void drawPuyoSub(Puyopuyo puyo,Puyopuyo sub,double t,int n){
        glBegin(GL_QUADS);
        changePuyoColor(sub.puyoColor);
        glVertex2d(calWinX(puyo.puyoX- puyo.puyoMoveX+ puyo.puyoMoveX* puyo.frameX/ puyo.frameMaxX+Math.cos(t), n), calWinY(puyo.puyoY- puyo.puyoMoveY+ puyo.puyoMoveY* puyo.frameY/ puyo.frameMaxY+Math.sin(t),n));
        glVertex2d(calWinX(puyo.puyoX- puyo.puyoMoveX+ puyo.puyoMoveX* puyo.frameX/ puyo.frameMaxX +Math.cos(t), n), calWinY(puyo.puyoY- puyo.puyoMoveY+ puyo.puyoMoveY* puyo.frameY/ puyo.frameMaxY+1+Math.sin(t),n));
        glVertex2d(calWinX(puyo.puyoX- puyo.puyoMoveX+ puyo.puyoMoveX* puyo.frameX/ puyo.frameMaxX+ 1+Math.cos(t), n), calWinY(puyo.puyoY- puyo.puyoMoveY+ puyo.puyoMoveY* puyo.frameY/ puyo.frameMaxY+1+Math.sin(t),n));
        glVertex2d(calWinX(puyo.puyoX- puyo.puyoMoveX+ puyo.puyoMoveX* puyo.frameX/ puyo.frameMaxX+1+Math.cos(t), n), calWinY(puyo.puyoY- puyo.puyoMoveY+ puyo.puyoMoveY* puyo.frameY/ puyo.frameMaxY+Math.sin(t),n));
        glEnd();

    }
    double calWinX(double x,int n){
        return -1+x*9.0/120+0.2+n*1.2;
    }
    double calWinY(double y,int n){
        return 1-y*16.0/120+0.1;
    }
}
