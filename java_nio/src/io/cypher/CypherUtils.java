package io.cypher;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.awt.*;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 20-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class CypherUtils {
    public static void cipherInputStream() throws InvalidKeyException, InvalidKeySpecException,
            NoSuchAlgorithmException, NoSuchPaddingException, IOException {
        Set<Point> set = new HashSet<Point>();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            Point point = new Point(random.nextInt(1000), random.nextInt(2000));
            set.add(point);
        }

        int last = random.nextInt(5000);
        String password = "password";
        byte key[] = password.getBytes();
        DESKeySpec desKeySpec = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        desCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        FileOutputStream fos = new FileOutputStream("./data/nio/recordloader.txt");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        CipherOutputStream cos = new CipherOutputStream(bos, desCipher);
        ObjectOutputStream oos = new ObjectOutputStream(cos);
        oos.writeObject(set);
        oos.writeInt(last);
        oos.flush();
        oos.close();
        desCipher.init(Cipher.DECRYPT_MODE, secretKey);
        FileInputStream fis = new FileInputStream("./data/nio/recordloader.txt");
        BufferedInputStream bis = new BufferedInputStream(fis);
        CipherInputStream cis = new CipherInputStream(bis, desCipher);
        BufferedReader br = new BufferedReader(new InputStreamReader(cis));
        System.out.println(br.readLine());
    }

    private final static String KEY_RULE = "password_key";

    private static Key initKey() {
        byte[] keyByte = KEY_RULE.getBytes();
        byte[] byteTemp = new byte[8];
        for (int i = 0; i < byteTemp.length && i < keyByte.length; i++) {
            byteTemp[i] = keyByte[i];
        }
        return new SecretKeySpec(byteTemp, "DES");
    }

    public static void doEncryptFile(File in, File out) {
        if (in == null) return;
        try {
            Cipher encryptCipher = Cipher.getInstance("DES");
            encryptCipher.init(Cipher.ENCRYPT_MODE, initKey());
            FileInputStream is = new FileInputStream(in);
            CipherInputStream cin = new CipherInputStream(is, encryptCipher);
            OutputStream os = new FileOutputStream(out);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = cin.read(buffer)) > 0) {
                os.write(buffer, 0, len);
                os.flush();
            }
            os.close();
            cin.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean doDecryptFile(InputStream in, OutputStream os) throws
            NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        if (in == null)
            return false;
        Cipher decryptCipher = Cipher.getInstance("DES");
        decryptCipher.init(Cipher.DECRYPT_MODE, initKey());
        CipherInputStream cin = new CipherInputStream(in, decryptCipher);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = cin.read(buffer)) > 0) {
            os.write(buffer, 0, len);
        }
        cin.close();
        return true;
    }


    public static void main(String[] args)  {
        //IO
        String format = "%n%s%n------------------------%n";
        System.out.printf(format, "CipherInputStream:");
        String path = "./data/nio/";
        try {
            CypherUtils.cipherInputStream();

        } catch (InvalidKeyException | InvalidKeySpecException |
                NoSuchAlgorithmException | NoSuchPaddingException | IOException e) {
            e.printStackTrace();
        }

        CypherUtils.doEncryptFile(new File(path+"result.txt"), new File(path+"result_encrypted.txt"));

        System.out.printf(format, "CipherINputStream File IO:");
        FileInputStream fIn = null;
        FileOutputStream fOut = null;
        try {
            fIn = new FileInputStream(path+"result_encrypted.txt");
            fOut = new FileOutputStream(path+"result_decrypted.txt");
            CypherUtils.doDecryptFile(fIn, fOut);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fIn != null) fIn.close();
                if (fOut != null) fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("encrypted_file:");
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(path+"result_encrypted.txt");
            byte[] bytes = new byte[100];
            int len;
            while ((len = fs.read(bytes)) > 0) {
                System.out.print(new String(bytes, 0, len));
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("decrypted_file:");
        try {
            fs = new FileInputStream(path+"result_decrypted.txt");
            byte[] bytes = new byte[100];
            int len;
            while ((len = fs.read(bytes)) > 0) {
                System.out.print(new String(bytes, 0, len));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
