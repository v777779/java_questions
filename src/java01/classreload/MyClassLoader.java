package java01.classreload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class MyClassLoader extends ClassLoader{

    public MyClassLoader(ClassLoader parent) {
        super(parent);
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        if(!name.contains("java01.classreload.bin"))
                return super.loadClass(name);

        try {
// src
            String url;
            if(!System.getProperty("user.dir").endsWith("src")) {
                url = "files:"+System.getProperty("user.dir")+"/src/"+name;
            }else {
                url = "files:"+System.getProperty("user.dir")+"/"+name;
            }

            System.out.println(url);
            url =  url.replaceAll("[\\.\\\\]","/")+".class";


// production
//            url = "files:"+System.getProperty("user.dir")+"/out/production/java_questions/"+name;
//            url =  url.replaceAll("[\\.\\\\]","/")+".class";
//            url = url.replaceAll("src/","");  // out


            URL myUrl = new URL(url);
            URLConnection connection = myUrl.openConnection();
            InputStream input = connection.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int data = input.read();

            while(data != -1){
                buffer.write(data);
                data = input.read();
            }

            input.close();

            byte[] classData = buffer.toByteArray();

            return defineClass(name,
                    classData, 0, classData.length);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}