package java_c01;

import java.util.*;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 25-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main02 {
    private static class A {
    }

    private static class B extends A {
    }

    private static class ArrayListM<T> extends ArrayList<T> {
    }

    public static void main(String[] args) {
        // UnsupportedOperationException
        System.out.println("\nUnsupportedOperationException:");

        List<String> nList = new ArrayList<>();
        nList.add("s1");
        nList.add("s2");
        nList.add("s3");
        List<String> uList = Collections.unmodifiableList(nList);
        List<String> uList2 = Arrays.asList("s1", "s2", "s3");
        List<String> eList = Collections.emptyList();
        List<String> sList = Collections.singletonList("String");

        try {
            uList.add("s5");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.toString());
        }
        try {
            uList2.add("s5");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.toString());
        }
        try {
            eList.add("s5");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.toString());
        }
        try {
            sList.add("s5");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.toString());
        }
        Map<String, String> map = new HashMap<>();
        map.put("s1", "v1");
        map.put("s2", "v2");
        map.put("s3", "v3");

        try {
            Set<String> set = map.keySet();
            set.remove("s1");
            set.add("s5");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.toString());
        }

        try {
            Collection<String> vSet = map.values();
            vSet.remove("s1");
            vSet.add("s5");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.toString());
        }
        try {
            Set<Map.Entry<String, String>> enSet = map.entrySet();
            enSet.remove("s1");
            enSet.add(new Map.Entry<String, String>() {
                @Override
                public String getKey() {
                    return "s5";
                }

                @Override
                public String getValue() {
                    return "v5";
                }

                @Override
                public String setValue(String value) {
                    return "v5";
                }
            });
        } catch (UnsupportedOperationException e) {
            System.out.println(e.toString());
        }
        try {
            Set<Map.Entry<String, String>> enSet = map.entrySet();
            enSet.add(enSet.iterator().next());

        } catch (UnsupportedOperationException e) {
            System.out.println(e.toString());
        }

// ArrayList
        ArrayList<? extends List> listA = new ArrayList<ArrayList>();  // аргумент должен точно соответствовать либо расширять
        ArrayList<? extends List> listB = new ArrayList<ArrayListM>();  // аргумент должен точно соответствовать либо расширять
        List<ArrayList> listC = new ArrayList<ArrayList>();
        ArrayList<? extends A> listD = new ArrayList<B>();

// LinkedHashMap
        System.out.println("\nLinkedHashMap:");
        LinkedHashMap<String, String> lhMap = new LinkedHashMap<>();
        lhMap.put("s1", "v1");
        lhMap.put("s2", "v2");
        lhMap.put("s3", "v3");

        lhMap.get("s3");  // access
        lhMap.get("s2");  // access
        lhMap.get("s1");  // access
        System.out.println(lhMap);

        LinkedHashMap<String, String> lrMap = new LinkedHashMap<>(1 << 4, 0.75f, true);
        lrMap.put("s1", "v1");
        lrMap.put("s2", "v2");
        lrMap.put("s3", "v3");

        lrMap.get("s3");  // access
        lrMap.get("s2");  // access
        lrMap.get("s1");  // access
        System.out.println(lrMap);

// LinkedHashSet
        System.out.println("\nLinkedHashSet:");
        LinkedHashSet<String> lhSet = new LinkedHashSet<>();
        lhSet.add("s1");
        lhSet.add("s2");
        lhSet.add("s3");
        lhSet.add("s9");
        lhSet.add("s8");
        lhSet.add("s5");
        System.out.println(lhSet);


        System.out.println("\nHashSet:");
        HashSet<String> hSet = new HashSet<>();
        hSet.add("s1");
        hSet.add("s2");
        hSet.add("s3");
        hSet.add("s9");
        hSet.add("s8");
        hSet.add("s5");
        System.out.println(hSet);


// LRU Cache
        System.out.println("\nLRUCache");
        LinkedHashMap<String, String> lruMap = new LRUCache<>();
        lruMap.put("s1", "v1");
        lruMap.put("s2", "v2");
        lruMap.put("s3", "v3");
        lruMap.put("s4", "v4");
        lruMap.put("s5", "v5");
        lruMap.put("s6", "v6");
        lruMap.put("s7", "v7");
        lruMap.put("s8", "v8");
        lruMap.put("s9", "v9");
        System.out.println("put:"+lruMap);
// access
        lruMap.get("s2");
        lruMap.get("s8");
        lruMap.get("s5");
        lruMap.get("s3");
        System.out.println("get:"+lruMap);
        lruMap.put("s15", "v5");
        System.out.println("put:"+lruMap);
        lruMap.put("s16", "v6");
        System.out.println("put"+lruMap);

// Comparator
        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return 0;
            }
        };

        Comparable<String> comparable = new Comparable<String>() {
            @Override
            public int compareTo(String o) {
                return 0;
            }
        };

    }

    private static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private static final int MAX_ENTRIES = 7;

        public LRUCache() {
            super(16, 0.75f, true);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > MAX_ENTRIES;  // request to remove node
        }
    }


}
