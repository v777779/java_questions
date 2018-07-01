package nio1.selectors.threads.console;

import util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;

public class ServerClientService implements Runnable {
        private Socket sc;
        private BufferedReader br;
        private BufferedWriter bw;
        private String attachment;
        private String message;
        private boolean isStopped;

        public ServerClientService(Socket sc) throws IOException {
            this.sc = sc;
            br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(sc.getOutputStream()));
            attachment = String.format("SC%d", sc.getPort());
            isStopped = false;
            message = "";
        }

        synchronized public boolean isStopped() {
            return isStopped;
        }

        synchronized public void setStopped() {
            isStopped = true;
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
                            s = sb.toString();
                            System.out.printf("%s:%s", attachment, s);
                            if (s.matches(".*cc\\s*")) {
                                setStopped();
                                System.out.printf("%s exits by command:cc%n", attachment);
                                break;
                            }
                            message = s.replaceAll("\\s*", "") + attachment;
                            sb.setLength(0);
                        }

                    }
                    if (message.matches("aa" + attachment)) {
                        String s = String.format("answer %s: at:%2$tT %2$tD%n", attachment, LocalDateTime.now());
                        bw.write(s);
                        bw.flush();
                        message = "";
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.close(sc, br, bw);
            }
            System.out.printf("%s:server client service closed... socket closed:%b%n", attachment, sc.isClosed());
            try {
                System.out.printf("%s waiting for 2500ms%n", attachment);
                Thread.sleep(2500);
                System.out.printf("%s waiting for 2500ms finished%n", attachment);
            } catch (InterruptedException e) {
                System.out.printf("%s interrupted by shutdownNow()%n", attachment);
                System.out.printf("%s:server client service closed... socket closed:%b%n", attachment, sc.isClosed());
            }

        }
    }
