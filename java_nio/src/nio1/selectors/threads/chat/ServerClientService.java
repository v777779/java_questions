package nio1.selectors.threads.chat;

import util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServerClientService implements Runnable {
    private static final String MARKER_CONNECT = "connect";
    private static final String MARKER_DISCONNECT = "disconnect";
    private static final String MARKER_CHAT = "chat";
    private static final int INDEX_MODE = 0;
    private static final int INDEX_USER = 1;
    private static final int INDEX_MESSAGE = 2;

//    private static final Charset TELNET_CHARSET = Charset.forName("UTF-8");
    private static final Charset TELNET_CHARSET = Charset.forName("WINDOWS-1251");

    private Socket sc;
    private final BufferedReader br;
    private final BufferedWriter bw;
    private boolean isStopped;
    private String userName;
    private String scName;
    private List<ServerClientService> listClients;

    public ServerClientService(Socket sc, List<ServerClientService> list) throws IOException {
        this.sc = sc;
        this.br = new BufferedReader(new InputStreamReader(sc.getInputStream(),TELNET_CHARSET));
        this.bw = new BufferedWriter(new OutputStreamWriter(sc.getOutputStream(),TELNET_CHARSET));
        isStopped = false;
        this.listClients = list;
        list.add(this);

        this.userName = ""; // empty
        this.scName = String.format("SC%d", sc.getPort());
    }

    synchronized public boolean isStopped() {
        return isStopped;
    }

    synchronized public void setStopped() {
        isStopped = true;
    }

    synchronized public void closeService() {
        isStopped = true;
    }

    synchronized public String getUserName() {
        return userName;
    }

    synchronized public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public void run() {
        StringBuilder sb = new StringBuilder();
        try {
            while (!isStopped() && !Thread.interrupted()) {
                if (br.ready()) {
                    String s;
                    while (br.ready() && (s = br.readLine()) != null) {
                        sb.append(s);
                        sb.append(String.format("%n"));
                    }
                    if (sb.length() > 0) {
                        String[] message = sb.toString().split(":");
                        sb.setLength(0);
                        switch (message[INDEX_MODE]) {
                            case MARKER_CONNECT:
                                s = connectUser(message[INDEX_USER]);
                                broadcast(s);

                                break;
                            case MARKER_DISCONNECT:
                                broadcast(disconnectUser(message[INDEX_USER]));
                                setStopped();  // stop service
                                break;
                            case MARKER_CHAT:
                                if (!message[INDEX_MESSAGE].isEmpty()) {
                                    broadcast(String.format("[%s]:%s%n",
                                            message[INDEX_USER], message[INDEX_MESSAGE]));
                                }
                                break;
                            default:
                                break;
                        }

                    }

                }
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            System.out.printf("server client service interrupted:%s%n", e);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(sc, br, bw);
        }
        if (sc.isClosed()) {
            System.out.printf("%s:server client service closed...%n", scName);
        } else {
            System.out.printf("%s:server client service not closed...%n", scName);
        }

    }

    private void broadcast(String s) throws IOException {
        if (listClients == null || listClients.isEmpty()) return;
        for (ServerClientService scs : listClients) {
            if (scs == null || scs.isStopped() || scs.bw == null) continue;
            try {
                scs.bw.write(s);
                scs.bw.flush();
            } catch (IOException e) {
                listClients.remove(scs);   // remove closed
            }
        }
    }

    private String getActiveUsers() {
        setUserName(userName);
        if (listClients == null || listClients.isEmpty()) return "[]";
        List<String> listUsers = new ArrayList<>();
        for (ServerClientService scs : listClients) {
            if (scs == null || scs.isStopped() || scs.bw == null) continue;
            String s = scs.getUserName();
            if (s == null || s.isEmpty()) continue;
            listUsers.add(s); // synchronized
        }
        return listUsers.toString();
    }

    private String connectUser(String userName) throws IOException {
        setUserName(userName);
// personal message
        String users = getActiveUsers();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%n%nWelcome to chat %s! %n", userName));
        sb.append(String.format("Active users are:%s%n", users));
        sb.append(String.format("To exit from chat type \"disconnect\"%n"));
        bw.write(sb.toString());
        bw.flush();
// for broadcast
        return String.format("[%s]: joined us at %2$tT %2$tD%n",
                userName, LocalDateTime.now());
    }

    private String disconnectUser(String userName) throws IOException {
// for broadcast
        listClients.remove(this);
        String users = getActiveUsers();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[%s]: left us at %2$tT %2$tD%n",
                userName, LocalDateTime.now()));
        sb.append(String.format("Active users are:%s%n", users));
        return sb.toString();
    }

}
