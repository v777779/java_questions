package java_08;

import java.io.*;
import java.util.Base64;
import java.util.UUID;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 17-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main03 {
    public static void main(String[] args) {
//Base64
        String format = "%n%s%n------------------------%n";

        System.out.printf(format, "Base64 Encode:");
        String originalInput = "test console";
        String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
        System.out.println("console           : " + originalInput);
        System.out.println("Base64          : " + encodedString);


        System.out.printf(format, "Base64 Decode:");

        String encodedWoPadding = encodedString.replaceAll("=*$", "");
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        byte[] decodedWoPadding = Base64.getDecoder().decode(encodedWoPadding);
        System.out.println("decoded         : " + new String(decodedBytes));
        System.out.println("decodedWoPadding: " + new String(decodedWoPadding));


        System.out.printf(format, "MIME Encode:");
        String originalMIME = UUID.randomUUID().toString();
        String encodedMIME = Base64.getMimeEncoder().encodeToString(originalMIME.getBytes());

        String decodedMIME = new String(Base64.getMimeDecoder().decode(encodedMIME));
        System.out.println("original MIME:" + originalMIME);
        System.out.println("encode MIME  :" + encodedMIME);
        System.out.println("decode MIME  :" + decodedMIME);


//        https://dzone.com/articles/base64-encoding-java-8
        try {
//Base
            System.out.printf(format, "Simple encoding:");
// Encode
            String asB64 = Base64.getEncoder().encodeToString("some string".getBytes("utf-8"));
            System.out.println(asB64); // Output will be: c29tZSBzdHJpbmc=

// Decode
            byte[] asBytes = Base64.getDecoder().decode("c29tZSBzdHJpbmc=");
            System.out.println(new String(asBytes, "utf-8")); // And the output is: some string

// URL
            System.out.printf(format, "URL encoding:");
            String basicEncoded = Base64.getEncoder().encodeToString("subjects?abcd".getBytes("utf-8"));
            System.out.println("Using Basic Alphabet: " + basicEncoded);

            String urlEncoded = Base64.getUrlEncoder().encodeToString("subjects?abcd".getBytes("utf-8"));
            System.out.println("Using URL Alphabet: " + urlEncoded);
// MIME
            System.out.printf(format, "MIME encoding:");
            StringBuilder sb = new StringBuilder();
            for (int t = 0; t < 10; ++t) {
                sb.append(UUID.randomUUID().toString());
            }

            byte[] toEncode = sb.toString().getBytes("utf-8");
            String mimeEncoded = Base64.getMimeEncoder().encodeToString(toEncode);
            System.out.println(mimeEncoded);
//wrapping
            System.out.printf(format,"Base64 InputStream:");
            String src = "This is the content of any resource read from somewhere" +
                    " into a stream. This can be text, image, video or any other stream.  ";

            // An encoder wraps an OutputStream. The content of /tmp/buff-base64.txt will be the
            // Base64 encoded form of src.

            OutputStream os = Base64.getEncoder().wrap(new FileOutputStream("./data/buff-base64.txt"));
            os.write(src.getBytes("utf-8"));

            // The section bellow illustrates a wrapping of an InputStream and decoding it as the stream
            // is being consumed. There is no need to buffer the content of the file just for decoding it.
            InputStream is = Base64.getDecoder().wrap(new FileInputStream("./data/buff-base64.txt"));
            int len;
            byte[] bytes = new byte[100];
            while ((len = is.read(bytes)) != -1) {
                System.out.print(new String(bytes, 0, len, "utf-8"));
            }

        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println();
        }

    }
}
