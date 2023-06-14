//package com.desctopbpmn.desctopbpmn;
//
//import java.io.*;
//import java.net.Socket;
//import java.net.UnknownHostException;
//
//public class Client {
//    private static Socket clientSocket; //сокет для общения
//    private static BufferedReader reader; // нам нужен ридер читающий с консоли, иначе как
//    // мы узнаем что хочет сказать клиент?
//    private static BufferedReader in; // поток чтения из сокета
//    private static BufferedWriter out; // поток записи в сокет
//
//    public Client() {
//        try {
//            try {
//                clientSocket = new Socket("localhost", 4004);
//                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
//                String serverMessage = in.readLine();
//                System.out.println(serverMessage);
//                commandResolver(serverMessage);
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
//    private void commandResolver(String message) {
//        String[] mesArr = message.split("__");
//        if (mesArr[0].equals(SocketConstants.COMMAND_BPMN_PATH.toString())) {
//            try {
//                Process process = new ProcessBuilder(mesArr[1]).start();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//    public void close() {
//        try {
//            clientSocket.close();
//            in.close();
//            out.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
