package com.muc;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class ServerWorker extends Thread{
    private final Socket clientSocket;
    private String login = null;
    public ServerWorker(Socket clientSocket){
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
        OutputStream outputStream = clientSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while((line = reader.readLine()) != null){
            String[] tokens = StringUtils.split(line);

                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if ("quit".equalsIgnoreCase(cmd)) {
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

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if(tokens.length == 3){
            String login = tokens[1];
            String password = tokens[2];

            if((login.equals("guest") && password.equals("guest")) || (login.equals("ben") && password.equalsIgnoreCase("ben"))){
                String msg = "ok login\n";
                outputStream.write(msg.getBytes());
                this.login = login;
                System.out.println("User logged in successfully: " + login);
            }else{
                String msg = "error login\n";
                outputStream.write(msg.getBytes());
            }
        }
    }
}
