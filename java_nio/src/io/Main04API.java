package io;

import java.io.BufferedInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

import static nio.Main01.PATH;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 23-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main04API {
    private static final String PATH_TXT = PATH+"result.txt";
    public static void main(String[] args) {
// Interfaces
//Classes
        FileInputStream fs = null;
        BufferedInputStream in = null;
        try {
            fs =  new FileInputStream(PATH_TXT);
            in = new BufferedInputStream(fs);

            FileDescriptor fd = fs.getFD();
//            FilePermission fp = FileCol



        }catch (IOException e){
            e.printStackTrace();
        }



    }
}
