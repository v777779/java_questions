package java_03.softhashmap;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class WeakHashMapExample {

    public static void main(String[] args) {

        // Created HashMap and WeakHashMapUser objects
        Map hashmapObject = new HashMap();
        Map weakhashmapObject = new WeakHashMap();

        // Created HashMap and WeakHashMapUser keys
        String hashmapKey = new String("hashmapkey");
        String weakhashmapKey = new String("weakhashmapkey");

        // Created HashMap and WeakHashMapUser values
        String hashmapValue = "hashmapvalue";
        String weakhashmapValue = "weakhashmapvalue";

        // Putting key and value in HashMap and WeakHashMapUser Object
        hashmapObject.put(hashmapKey, hashmapValue);
        weakhashmapObject.put(weakhashmapKey, weakhashmapValue);

        // Print HashMap and WeakHashMapUser Object : Before Garbage Collection
        System.out.println("HashMap before Garbage Collected :" + hashmapObject);

        System.out.println("WeakHashMapUser before Garbage Collected :" +
                weakhashmapObject);

        // Set HashMap and WeakHashMapUser Object keys to null
        hashmapKey = null;
        weakhashmapKey = null;

        // Calling Garbage Collection
        System.gc();

        // Print HashMap and WeakHashMapUser Object : After Garbage Collection
        System.out.println("HashMap after Garbage Collected :" + hashmapObject);
        System.out.println("WeakHashMapUser after Garbage Collected :" + weakhashmapObject);

    }
}
