package prism4291.henachoko;

import io.socket.client.IO;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrismGameClient {
    static final int clientVersion = 2;
    static String keyName = "username";
    static String keyPassWord = "password";

    public static void main(String[] args) throws URISyntaxException {
        JSONObject userData = getUserData();
        System.out.println(userData);
        if (userData == null) {
            return;
        }
        PrismGameVariable.socket = IO.socket("https://prism-game-server.herokuapp.com/");


        PrismGameVariable.socket.on("serverVerifyLogin", objects -> {
            JSONObject jo = (JSONObject) objects[0];
            System.out.println(jo);
            switch (jo.getString("status")) {
                case "new":
                case "match":
                    PrismGameVariable.socketId = jo.getString("socketid");
                    break;
                case "fail":
                default:
                    GLFW.glfwSetWindowShouldClose(PrismGameVariable.WIN, true);
                    break;
            }

        });

        PrismGameVariable.socket.connect();
        userData.put("version", clientVersion);
        PrismGameVariable.socket.emit("clientLogin", userData.toString());
        PrismGameWindow pgw = new PrismGameWindow();
        pgw.run();
        PrismGameVariable.socket.off();
        PrismGameVariable.socket.disconnect();
        PrismGameVariable.socket.close();

    }

    public static JSONObject getUserData() {
        Path path = Paths.get("prismGameData.txt");
        if (Files.exists(path)) {
            try {
                List<String> allLines = Files.readAllLines(path, StandardCharsets.UTF_8);
                JSONObject data = new JSONObject();
                for (String s : allLines) {
                    String[] line = s.split("=");
                    if (line.length > 1) {
                        data.put(line[0], String.join("=", Arrays.copyOfRange(line, 1, line.length)));
                    }
                }
                if (isStringBad(data.getString(keyName))) {
                    return null;
                }
                PrismGameVariable.userName = data.getString(keyName);
                if (isStringBad(data.getString(keyPassWord))) {
                    return null;
                }
                return data;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            try {
                Files.createFile(path);
                List<String> defaultData = new ArrayList<>();
                defaultData.add(keyName + "=");
                defaultData.add(keyPassWord + "=");
                Files.write(path, defaultData, StandardCharsets.UTF_8, StandardOpenOption.WRITE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static boolean isStringBad(String str) {
        if (str == null) {
            return true;
        }
        if (str.equals("")) {
            return true;
        }
        return !str.replaceAll("\\w", "").equals("");
    }
}
