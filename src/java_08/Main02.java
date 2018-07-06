package java_08;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 16-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main02 {

    public static String fun1(String name) {
        System.out.printf("java fun1: %s >> %n", name);
        return "fun1: " + name;
    }

    public static String fun2(Object object) {
        System.out.printf("java fun2: %s%n", object.getClass());
        return "fun2: " + object;
    }

    public static void fun3(ScriptObjectMirror mirror) {
        System.out.println(mirror.getClassName() + ":");
        System.out.println(Arrays.toString(mirror.getOwnKeys(true)));
        Arrays.stream(mirror.getOwnKeys(true))
                .forEach(k -> System.out.printf("%s: %s%n", k, mirror.get(k)));
    }

    public static void fun4(ScriptObjectMirror person) {
        System.out.println("Full Name is: " + person.callMember("getFullName"));
    }

    public static void main(String[] args) throws Exception {
//        http://winterbe.com/posts/2014/04/05/java8-nashorn-tutorial/
        System.out.println("Nashorn:");
        String format = "%n%s%n------------------------%n";
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

        System.out.printf(format, "JS File:");
        Path path = Paths.get("./data/script.js");
        File file = new File(path.toString());

        System.out.println("files: " + path + " exist:" + file.exists());
        try {
            engine.eval("print('Hello World!');");
            FileReader fr = new FileReader(path.toString());
            engine.eval(fr); // from files

        } catch (ScriptException | IOException e) {
            e.printStackTrace();
        }


        System.out.printf(format, "JS function:");
        String fileName = Paths.get("./data/function.js").toString();

        engine.eval(new FileReader(fileName));
        Invocable invocable = (Invocable) engine;

        Object result = invocable.invokeFunction("fun1", "Fun1 argument");
        System.out.println("fun1 return:" + result);

        invocable.invokeFunction("fun2", "Fun2 argument");
        invocable.invokeFunction("fun2", new Object());
        invocable.invokeFunction("fun2", LocalDateTime.now());
        invocable.invokeFunction("fun2", ZonedDateTime.now());
        invocable.invokeFunction("fun2", new Person());
// methods Java from JS
        System.out.printf(format, "InvokeBack via Function:");
        invocable.invokeFunction("fun3");
        System.out.println(engine.get("result"));  // вытаскивает только значение, не выполняет метод


//method
        System.out.printf(format, "InvokeBack via Method:");
        Object obj = engine.get("MethodBack");

        result = invocable.invokeMethod(obj, "jsForward", "Mike");
        System.out.println(result);
        result = invocable.invokeMethod(obj, "jsForward", "123");
        System.out.println(result);

        invocable.invokeMethod(obj, "javaBack", "Peterson");
        result = invocable.invokeMethod(obj, "javaBack", "456");  // js javaBack >> java fun1 >> js return
        System.out.println("back     : " + result);

// method direct script
        System.out.printf(format, "JS Back Java Method:");
        engine.eval(new FileReader("./data/method.js"));

// script object mirror
        System.out.printf(format, "JS Object Mirror:");
        invocable.invokeMethod(obj, "javaMirror");
        invocable.invokeMethod(obj, "javaMirrorArray");

// class created in JS and printed in Java via call method of JS class of JS object
        System.out.printf(format, "JS Mirror Java call JS class member method:");
        invocable.invokeMethod(obj, "javaPerson");

//jjs
        System.out.printf(format, "Nashorn jjs:");
        File f = new File("./data/result.txt");
        if(f.exists()){
            if(!f.delete())throw new RuntimeException("Can't delete files:"+f.getPath());
        }

        Runtime.getRuntime().exec("cmd /c start .\\data\\start.cmd");
        String s ="";
        try {

            while (!f.exists() || f.length() <= 0) {
                Thread.sleep(200);
                System.out.print(".");
            }
            System.out.println();
            FileInputStream fs = new FileInputStream(f);
            BufferedReader br = new BufferedReader(new InputStreamReader(fs, "utf-8"));
            s = br.lines().parallel().collect(Collectors.joining());

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("script.js: " + s);


    }

    private static class Person {

    }
}
