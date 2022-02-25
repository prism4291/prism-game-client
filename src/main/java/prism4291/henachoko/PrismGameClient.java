package prism4291.henachoko;

import io.socket.client.IO;
import io.socket.client.Socket;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class PrismGameClient {
    static String keyName="name";
    static String keyPassWord="password";
    public static void main(String[] args) throws URISyntaxException {
        Map<String,String> userData=getUserData();
        System.out.println(userData);
        final Socket socket = IO.socket("https://prism-game-server.herokuapp.com/");

        // サーバーからのmessage_from_serverがemitされた時
        socket.on("receiveMessage", objects -> {
            // 最初の引数を表示
            System.out.println(objects[0]);
            //サーバー側にmessage_from_clientで送信
            socket.emit("sendMessage", "This is Java");

        });
        socket.emit("sendMessage", "This is Java");

        socket.connect();
    }
    public static Map<String,String> getUserData(){
        Path path=Paths.get("prismGameData.txt");
        if(Files.exists(path)){
            try {
                List<String> allLines=Files.readAllLines(path,StandardCharsets.UTF_8);
                Map<String,String> data=new HashMap<>();
                for (String s:allLines) {
                    String[] line=s.split("=");
                    if(line.length>1) {
                        data.put(line[0],String.join("=", Arrays.copyOfRange(line,1,line.length)));
                    }
                }
                if(isStringBad(data.get(keyName))){
                    return null;
                }
                if(isStringBad(data.get(keyPassWord))){
                    return null;
                }
                return data;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }else{
            try {
                Files.createFile(path);
                List<String> defaultData=new ArrayList<>();
                defaultData.add(keyName+"=");
                defaultData.add(keyPassWord+"=");
                Files.write(path,defaultData, StandardCharsets.UTF_8, StandardOpenOption.WRITE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public static boolean isStringBad(String str){
        if(str==null){
            return true;
        }
        if(str.equals("")){
            return true;
        }
        return !str.replaceAll("\\w", "").equals("");
    }
}
