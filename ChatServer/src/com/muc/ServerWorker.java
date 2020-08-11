package com.muc;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.List;

public class ServerWorker extends Thread{
    private final Socket clientSocket;
    private final Server server;
    private String login = null;
    private OutputStream outputStream;

    public ServerWorker(Server server, Socket clientSocket){
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run(){
        try {
            handleClientSocket();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void handleClientSocket() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while((line = reader.readLine()) != null){
            String[] tokens = StringUtils.split(line);

                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if ("logoff".equalsIgnoreCase(cmd)||"quit".equalsIgnoreCase(cmd)) {
                        handleLogoff();
                        break;
                    }else if("login".equalsIgnoreCase(cmd)){
                        handleLogin(outputStream, tokens);
                    }
                    else{
                        String msg = "unknown " + cmd + "\n";
                        outputStream.write(msg.getBytes());
                    }
                }
        }
        clientSocket.close();//close out the connection
    }

    private void handleLogoff() throws IOException {
        server.removeWorker(this);
        List<ServerWorker> workerList = server.getWorkerList();
        //send other users current status
        String onlineMsg = "offline " + login + "\n";
        for(ServerWorker worker : workerList){
            if( !login.equals(worker.getLogin())) {
                worker.send(onlineMsg);
            }
        }
        clientSocket.close();//close out the connection
    }

    public String getLogin(){
        return login;
    }
    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if(tokens.length == 3){
            String login = tokens[1];
            String password = tokens[2];

            if((login.equals("guest") && password.equals("guest")) || (login.equals("ben") && password.equalsIgnoreCase("ben"))){
                String msg = "ok login\n";
                outputStream.write(msg.getBytes());
                this.login = login;
                System.out.println("User logged in successfully: " + login);

                List<ServerWorker> workerList = server.getWorkerList();
                //notify current user all other online logins
                for(ServerWorker worker : workerList){
                        if(worker.getLogin() != null) {
                            if( !login.equals(worker.getLogin())) {
                                String msg2 = "online " + worker.getLogin();
                                send(msg2);
                            }
                        }
                    }
                //send other users current status
                String onlineMsg = "online " + login + "\n";
                for(ServerWorker worker : workerList){
                    if( !login.equals(worker.getLogin())) {
                        worker.send(onlineMsg);
                    }
                }
            }else{
                String msg = "error login\n";
                outputStream.write(msg.getBytes());
            }
        }
    }

    private void send(String msg) throws IOException {
        if(login != null) {
            outputStream.write(msg.getBytes());
        }
    }
}
