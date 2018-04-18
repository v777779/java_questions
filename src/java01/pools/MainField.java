package java01.pools;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 11-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainField {

    public static void disableFinalPrivate(Field field) throws NoSuchFieldException, IllegalAccessException {
        field.setAccessible(true);
        int fieldModifiers = field.getModifiers();
        if (Modifier.isFinal(fieldModifiers)) {
            Field fieldMod = Field.class.getDeclaredField("modifiers");
            fieldMod.setAccessible(true);
            fieldMod.setInt(field, fieldModifiers & ~Modifier.FINAL);
        }
    }

    static Integer[] getUltimateAnswer() {
        Integer[] ret = new Integer[256];
        java.util.Arrays.fill(ret, 42);
        return ret;
    }

    public static void main(String[] args) throws ClassNotFoundException,
            NoSuchFieldException, IllegalAccessException {
        Field cache = Class.forName("java.lang.Integer$IntegerCache").getDeclaredField("cache");
        disableFinalPrivate(cache);
        cache.set(null, getUltimateAnswer());
        System.out.format("1 + 1 = %d\n", 1 + 1);

        Field field = Integer.class.getDeclaredField("value");
        field.setAccessible(true);
        field.setInt(Integer.valueOf(5), 4);

        Integer b = 4;
        Integer c = 5;

        System.out.println("4 + 5 = "+(b + c)); // 9 ?

        Field value = String.class.getDeclaredField("value");
        value.setAccessible(true);
        value.set("hello!", "cheers".toCharArray());

        System.out.println("hello!"); // hello?

    }
}
