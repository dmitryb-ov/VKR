package com.module.bpmn.socket;

import java.io.*;
import java.net.Socket;

public class Client {

    private static Socket clientSocket; //сокет для общения
    private static BufferedReader reader; // нам нужен ридер читающий с консоли, иначе как
    // мы узнаем что хочет сказать клиент?
    private static BufferedReader in; // поток чтения из сокета
    private static BufferedWriter out; // поток записи в сокет

    public void sendMessage(SocketConstants constants, String mes) {
        try {
            try {
                clientSocket = new Socket("127.0.0.1", 4004); // этой строкой мы запрашиваем
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                if (!mes.isEmpty()) {
                    if (constants == SocketConstants.COMMAND_BLENDER_PATH) {
                        String message =
                                SocketConstants.COMMAND_BLENDER_PATH + "__" + mes;
                        out.write(message);
                        out.flush();
                    }

                    if (constants == SocketConstants.CONNECTED) {
                        String message =
                                SocketConstants.CONNECTED + "__" + mes;
                        out.write(message);
                        out.flush();
                    }

                    if(constants == SocketConstants.COMMAND_BLENDER_SAVE_PATH_OPEN){
                        String message =
                                SocketConstants.COMMAND_BLENDER_SAVE_PATH_OPEN + "__" + mes;
                        out.write(message);
                        out.flush();
//                        Thread.sleep(1000);
//                        String response = in.readLine();
//                        System.out.println(response);
                    }
                    if(constants == SocketConstants.CONVERT){
                        String message = SocketConstants.CONVERT + "__" + mes;
                        out.write(message);
                        out.flush();
                    }
                }
            } finally { // в любом случае необходимо закрыть сокет и потоки
                System.out.println("Клиент был закрыт...");
                clientSocket.close();
                in.close();
                out.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
