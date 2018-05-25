package io;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

import static nio.Main01.FORMAT;
import static nio.Main01.PATH;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 23-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main04R {

    public static void main(String[] args) {
        System.out.printf(FORMAT, "Reader:");

// Charset
        System.out.printf(FORMAT, "Charset:");
        FileInputStream in = null;
        BufferedReader br = null;
        try {
            in = new FileInputStream(PATH + "result.txt");
            Charset cUTF = Charset.forName("UTF-8");
            Charset cWIN = Charset.forName("WINDOWS-1251");
            Charset cKOI8 = Charset.forName("KOI8-R");
            CharsetDecoder dUTF = new UserDecoder(Charset.forName("UTF-8"), 2, 2);
            CharsetDecoder dWIN = new UserDecoder(Charset.forName("WINDOWS-1251"), 2, 2);
            CharsetDecoder dKOI8 = new UserDecoder(Charset.forName("KOI8-R"), 2, 2);

//            for (Charset charset : Charset.availableCharsets().values()) {
//                System.out.println(charset.toString());
//            }
            in.close();

            System.out.printf(FORMAT, "Charset "+cUTF+":");
            in = new FileInputStream(PATH + "result_u.txt");
            br = new BufferedReader(new InputStreamReader(in, cUTF));
            IOUtils.readout(br, in); // closes br >> InputStreamReader, closes in

            System.out.printf(FORMAT, "User CharsetDecoder "+dUTF+":");
            in = new FileInputStream(PATH + "result_u.txt");
            br = new BufferedReader(new InputStreamReader(in, dUTF));       // closes InputStreamReader
            IOUtils.readout(br, in);                                        // closes br >> InputStreamReader, in


            System.out.printf(FORMAT, "Charset "+cWIN+":");
            in = new FileInputStream(PATH + "result_w.txt");
            br = new BufferedReader(new InputStreamReader(in, cWIN));
            IOUtils.readout(br, in);

            System.out.printf(FORMAT, "User CharsetDecoder "+dWIN+":");
            in = new FileInputStream(PATH + "result_w.txt");
            br = new BufferedReader(new InputStreamReader(in, dWIN));           // closes InputStreamReader
            IOUtils.readout(br, in); // closes br >> InputStreamReader, closes in

            System.out.printf(FORMAT, "Charset "+cKOI8+":");
            in = new FileInputStream(PATH + "result_k.txt");
            br = new BufferedReader(new InputStreamReader(in, cKOI8));
            IOUtils.readout(br, in);

            System.out.printf(FORMAT, "User CharsetDecoder "+dKOI8+":");
            in = new FileInputStream(PATH + "result_k.txt");
            br = new BufferedReader(new InputStreamReader(in, dKOI8));           // closes InputStreamReader
            IOUtils.readout(br, in); // closes br >> InputStreamReader, closes in

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(in);
        }
        System.exit(0);

//// DecoratorInputStream
//        System.out.printf(FORMAT, "DecoratorInputStream:");
//        in = null;
//        try {
//            in = new BufferedInputStream(new FileInputStream(PATH + "result.txt"), 100);  // internal buffer
//            IOUtils.readout(in);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.closeStream(in);
//        }
//        System.exit(0);

//// DecoratorInputStream
//        System.out.printf(FORMAT, "DecoratorInputStream:");
//        in = null;
//        try {
//            in = new BufferedInputStream(new FileInputStream(PATH + "result.txt"), 100);  // internal buffer
//            IOUtils.readout(in);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.closeStream(in);
//        }
//        System.exit(0);


    }

    private static class UserDecoder extends CharsetDecoder {
        /**
         * Initializes a new decoder.  The new decoder will have the given
         * chars-per-byte values and its replacement will be the
         * string <code>"&#92;uFFFD"</code>.
         *
         * @param cs                  The charset that created this decoder
         * @param averageCharsPerByte A positive float value indicating the expected number of
         *                            characters that will be produced for each input byte
         * @param maxCharsPerByte     A positive float value indicating the maximum number of
         *                            characters that will be produced for each input byte
         * @throws IllegalArgumentException If the preconditions on the parameters do not hold
         */
        protected UserDecoder(Charset cs, float averageCharsPerByte, float maxCharsPerByte) {
            super(cs, averageCharsPerByte, maxCharsPerByte);
        }

        @Override
        protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
            final CharsetDecoder decoder = charset().newDecoder();
            CoderResult cr = null;
            try {
                if (!in.hasRemaining()) return CoderResult.UNDERFLOW; // no input data

                final CharBuffer cout = decoder.decode(in);
                cr = decoder.flush(cout);                           // check for flush
                while (cout.hasRemaining()) {
                    out.put(cout.get());
                }
            } catch (CharacterCodingException e) {
                e.printStackTrace();
            }
            return cr;
        }

        @Override
        public String toString() {
            return charset().toString();
        }
    }

}
