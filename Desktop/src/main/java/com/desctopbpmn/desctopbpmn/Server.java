package com.desctopbpmn.desctopbpmn;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static Server INSTANCE;
    private static Socket clientSocket;
    private static ServerSocket server;
    private static Socket socketPython;
    private static BufferedReader in;
    private static BufferedWriter out;

    public Server() {
        try {
            server = new ServerSocket(4004);// серверсокет прослушивает порт 4004
            socketPython = new Socket("127.0.0.1", 4005);
            while (true) {
                System.out.println("Сервер запущен!"); // хорошо бы серверу
                //   объявить о своем запуске
                clientSocket = server.accept(); // accept() будет ждать пока
                //кто-нибудь не захочет подключиться
                // установив связь и воссоздав сокет для общения с клиентом можно перейти
                // к созданию потоков ввода/вывода.
                // теперь мы можем принимать сообщения
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                // и отправлять
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                String line = in.readLine();
                System.out.println(line);
                commandResolver(line);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private void commandResolver(String message) {
        String[] mesArr = message.split("__");
        if (mesArr[0].equals(SocketConstants.COMMAND_BLENDER_PATH.toString())) {
            try {
                Process process = new ProcessBuilder(mesArr[1]).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (mesArr[0].equals(SocketConstants.COMMAND_BLENDER_SAVE_PATH_OPEN.toString())) {
            File folder = new File(mesArr[1]);
            File[] listFiles = folder.listFiles();
            System.out.println(mesArr[2]);
            boolean isMatch = false;
            for (File listFile : listFiles) {
                if (listFile.isFile()) {
                    if (listFile.getName().equals(mesArr[2])) {
                        try {
                            Process process = Runtime.getRuntime()
                                    .exec("\"" + mesArr[3] + "\"" +
                                            "\"" + mesArr[1] + "\\" + mesArr[2] + "\"");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
//                            out.write("true");
//                            out.flush();
//                            isMatch = true;
                        break;
                    }

                }
            }
            if (!isMatch) {
//                    out.write("false");
//                    out.flush();
                System.out.println("Файл не найден по пути " + mesArr[1]);
            }
        }

        if (mesArr[0].equals(SocketConstants.CONVERT.toString())) {
            try {
                DataOutputStream dout = new DataOutputStream(socketPython.getOutputStream());
                DataInputStream in = new DataInputStream(socketPython.getInputStream());
                String msg = (String) in.readUTF();
                System.out.println("Server: " + msg);
                dout.writeUTF(mesArr[1]);
                dout.flush();
                dout.close();
                socketPython.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
