//package com.module.bpmn.socket;
//
//import org.springframework.stereotype.Service;
//
//import java.io.*;
//import java.net.ServerSocket;
//import java.net.Socket;
//
//public class Server {
//    private static Server INSTANCE;
//    private static Socket clientSocket;
//    private static ServerSocket server;
//    private static BufferedReader in;
//    private static BufferedWriter out;
//
//    private Server() {
//        try {
//            server = new ServerSocket(4004);
//            clientSocket = server.accept();
//            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void sendMessage(SocketConstants constants, String mes) {
//        try {
//            try {
//                if (!mes.isEmpty()) {
//                    if (constants == SocketConstants.COMMAND_BPMN_PATH) {
//                        String message =
//                                SocketConstants.COMMAND_BPMN_PATH + "__" + mes;
//                        out.write(message);
//                        out.flush();
//                    }
//
//                    if (constants == SocketConstants.CONNECTED) {
//                        String message =
//                                SocketConstants.CONNECTED + "__" + mes;
//                        out.write(message);
//                        out.flush();
//                    }
//                }
//            } finally {
////                clientSocket.close();
////                in.close();
////                out.close();
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void close() {
//        try {
//            clientSocket.close();
//            in.close();
//            out.close();
//            server.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static Server getInstance() {
//        if (INSTANCE == null) {
//            INSTANCE = new Server();
//        }
//        return INSTANCE;
//    }
//}
