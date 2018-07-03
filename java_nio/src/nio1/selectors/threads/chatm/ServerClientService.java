package nio1.selectors.threads.chatm;

import util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.List;


public class ServerClientService implements Runnable {
    private static final Charset TELNET_CHARSET = Charset.forName("WINDOWS-1251");
    private static final Charset TELNET_CHARSETU = Charset.forName("KOI8-R");

    private Socket sc;
    private final BufferedReader br;
    private final BufferedWriter bw;
    private String scName;
    private List<ServerClientService> listClients;

    public ServerClientService(Socket sc, List<ServerClientService> list) throws IOException {
        this.sc = sc;
        this.br = new BufferedReader(new InputStreamReader(sc.getInputStream(),TELNET_CHARSET));
        this.bw = new BufferedWriter(new OutputStreamWriter(sc.getOutputStream(),TELNET_CHARSET));
        this.scName = String.format("SC%d", sc.getPort());
        this.listClients = list;
        list.add(this);
    }

    @Override
    public void run() {
        try {
            bw.write(String.format("Welcome to NIO Chat!%n"));
            bw.flush();
            while (true) {
                String s = br.readLine();  // блокирующий метод  нет смысла организовывать флаги
                if (s == null) {
                    if (br.read() == -1) break;  // channel closed confirmed
                    else continue;
                }
                broadcast(String.format("%s%n", s));
            }
        }catch (SocketException e) {
            System.out.printf("Exception:%s%n",e);
        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(sc, br, bw);
        }
        listClients.remove(this);
        System.out.printf("%s:server client service closed...%n", scName);
    }

    private void broadcast(String s) {
        if (listClients == null || listClients.isEmpty()) return;
        for (ServerClientService scs : listClients) {
            try {
                scs.bw.write(scName + ":" + s);
                scs.bw.flush();
            } catch (IOException e) {
                listClients.remove(scs);   // remove closed
            }
        }
    }

}
