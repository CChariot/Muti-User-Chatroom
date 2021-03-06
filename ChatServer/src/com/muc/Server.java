package com.muc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread{
    private final int serverPort;

    private ArrayList<ServerWorker> workerList = new ArrayList<>();
    public Server(int serverPort) {
        this.serverPort = serverPort;
    }

    List<ServerWorker> getWorkerList(){
        return workerList;
    }
    @Override
    public void run(){
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            while(true){
                System.out.println("About to accept client connection...");
                Socket clientSocket = serverSocket.accept();//accept the incoming connection from client
                System.out.println("Accepted connection from " + clientSocket);
                ServerWorker worker = new ServerWorker(this, clientSocket);
                workerList.add(worker);
                worker.start();
            }
        }catch(IOException e){ //catch two exceptions
            e.printStackTrace();
        }
    }

    void removeWorker(ServerWorker serverWorker) {
        workerList.remove(serverWorker);
    }
}
