package prism4291.henachoko;

import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;

public class PrismGameClient {
    public static void main(String[] args) throws URISyntaxException {

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
}
