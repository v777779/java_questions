package io;

import util.IOUtils;

import java.io.*;

import static util.IOUtils.FORMAT;
import static util.IOUtils.PATH;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 24-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main02C {
    public static void main(String[] args) {

// DecoratorInputStream
        System.out.printf(FORMAT, "Console:");
// source
        System.out.printf(FORMAT, "Run sources:");
        try {
            String path = "java_nio/src/io;java_nio/src";
            String fileName = "java_nio/src/io/console/MainConsole.java";
            String fileClass = "io.console.MainConsole";

            Runtime.getRuntime().exec("javac -cp " + path + " " + fileName);
            Runtime.getRuntime().exec("cmd /c start java -cp " + path + " " + fileClass);

        } catch (IOException e) {
            e.printStackTrace();
        }

// StreamTokenizer
        System.out.printf(FORMAT, "StreamTokenizer:");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(PATH + "token.txt")));  // internal buffer
            StreamTokenizer st = new StreamTokenizer(br); // использует Reader вместо InputStream из за символов
            st.parseNumbers();
            st.slashSlashComments(false);   // не работает
            st.slashStarComments(true);     // работает и включено
            st.commentChar('#');
            st.quoteChar('"');

            IOUtils.readout(new BufferedInputStream(new FileInputStream(PATH + "token.txt")));
            int typeToken;
            int counter = 0;
            while ((typeToken = st.nextToken()) != StreamTokenizer.TT_EOF) {
                switch (typeToken) {
                    case StreamTokenizer.TT_WORD:
                        System.out.println(st.lineno() + " word:" + st.sval);
                        break;
                    case StreamTokenizer.TT_NUMBER:
                        System.out.println(st.lineno() + " number:" + st.nval);
                        break;
                    case StreamTokenizer.TT_EOL:
                        System.out.println(st.lineno() + " eol: ");
                        break;
                    case '"':
                        System.out.println(st.lineno() + " quoted: " + st.sval);
                        break;
                    case '*':
                        System.out.println(st.lineno() + " commnt: " + st.sval);  // сработает если открыть комменты /*
                        break;
                    default:
//                        System.out.println("unknown: "+st.sval);
                        break;
                }
                counter++;
                if(counter%5 == 0 ){
                    System.out.println("push back:");
                    st.pushBack();  // push back every 5th token
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(br);
        }

// PrintStream
        System.out.printf(FORMAT, "PrintStream:");
        PrintStream ps = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            out =  new BufferedOutputStream(new FileOutputStream(PATH + "print.txt"), 100);
            ps = new PrintStream(out);

            in = new BufferedInputStream(new FileInputStream(PATH + "result.txt"), 100);  // internal buffer
            byte[] bytes =  new byte[100];
            int len;

            while((len = in.read(bytes))> 0) {
                ps.printf("%s",new String(bytes,0,len).replaceAll("\\* ","\\*_"));
            }
            ps.close();
            out.close();
            in.close();
            in = new BufferedInputStream(new FileInputStream(PATH + "print.txt"), 100);  // internal buffer
            ps = new PrintStream(System.out);
            while((len = in.read(bytes))> 0) {
                ps.printf("%s",new String(bytes,0,len));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(in);
            IOUtils.close(out);
            IOUtils.close(ps);
        }
        System.exit(0);

//// DecoratorInputStream
//        System.out.printf(FORMAT, "DecoratorInputStream:");
//        in = null;
//        try {
//            in = new BufferedInputStream(new FileInputStream(PATH + "result.txt"), 100);  // internal buffer
//            util.IOUtils.readout(in);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            util.IOUtils.close(in);
//        }
//        System.exit(0);


    }
}
