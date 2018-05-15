package java_03;


import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.*;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 20-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main {
    private static class MList<T> implements Iterable<T> {
        private List<T> list;

        public MList() {
            this.list = new ArrayList<>();
        }

        public void addAll(List<T> subList) {
            if (list == null || subList == null) throw new IllegalArgumentException();
            list.addAll(subList);
        }


        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                private int index = 0;

                @Override
                public boolean hasNext() {
                    return !(list == null || list.isEmpty() || index >= list.size());
                }

                @Override
                public T next() {
                    if (!hasNext()) throw new IndexOutOfBoundsException();
                    T value = list.get(index);
                    index++;
                    return value;
                }
            };
        }

        @Override
        public String toString() {
            if (list == null) return "";
            return list.toString();
        }
    }

    private static final class User {
        private final String name;
        private final boolean isActive;
        private final String userId;


        // can be constructed using this constructor ONLY !
        public User(String name, boolean isActive, String userId) {
            this.name = name;
            this.isActive = isActive;
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public boolean isActive() {
            return isActive;
        }

        public String getUserId() {
            return userId;
        }
    }


    // methods
    public static int hashCode(int hash, char[] value) {
        int h = hash;
        if (h == 0 && value.length > 0) {
            char val[] = value;

            for (int i = 0; i < value.length; i++) {
                h = 31 * h + val[i];
            }
            hash = h;
        }
        return h;
    }
// open address


    public static void main(String[] args) {
        System.out.println("Java Collection 01 Started");
        Collection c = (Collection) new ArrayList<>();  // Collection interface
        HashMap hm;
        LinkedHashMap lhm;
        TreeMap<String, Integer> tMap;
        ArrayList<String> a;
        Stack s;
        LinkedList ll;
        CopyOnWriteArrayList sa;
        CopyOnWriteArraySet ss;
        ConcurrentHashMap cm;
        ConcurrentLinkedDeque cd;
        ConcurrentSkipListMap csm;
        HashSet<String> hs;
        LinkedHashSet lhs;
        TreeSet ts;
        NavigableSet ns;
        SortedSet sts;
        Iterator iterator;


        a = new ArrayList<>();
        hs = new HashSet<>(a);
        tMap = new TreeMap<>();
        Set<String> k = tMap.keySet();
        Iterator<String> ik = k.iterator();
        for (String s1 : k) {
        }
        Collection<Integer> v = tMap.values();
        Iterator<Integer> iv = v.iterator();
        for (Integer integer : v) {

        }
        Set<Map.Entry<String, Integer>> entrySet = tMap.entrySet();
        Iterator<Map.Entry<String, Integer>> ie = entrySet.iterator();
        for (Map.Entry<String, Integer> entry : entrySet) {

        }

// SortedMap
        SortedMap<Integer, Integer> sMap = new TreeMap<Integer, Integer>();
        sMap.put(12, 25);
        sMap.put(1, 72);
        sMap.put(5, 255);
        sMap.put(3, 51);
        sMap.put(27, 11);

        System.out.println("Sorted Map:");
        System.out.println("toString     : " + sMap.toString());

        System.out.print("Iterator  key:");
        for (Integer key : sMap.keySet()) {
            System.out.print(key + "=" + sMap.get(key) + " ");
        }

        System.out.println();
        System.out.print("Iterator  val:");
        for (Integer value : sMap.values()) {
            System.out.print("v=" + value + " ");
        }
        System.out.println();

// toArray
        Integer[] ints = sMap.keySet().toArray(new Integer[sMap.keySet().size()]);
        System.out.println("Collection to Array:");
        System.out.println(Arrays.toString(ints));

// symmetric
        List<String> list = new ArrayList<>();
        List<String> listA = new ArrayList<>();
        List<String> listB = new ArrayList<>();
        Random rnd = new Random();
        for (int i = 20; i < 40; i++) {
            list.add("listA" + i);
        }
        listA.addAll(list.subList(0, 10));
        listB.addAll(list.subList(5, 15));

        System.out.println("listA:" + listA);
        System.out.println("listB:" + listB);

        System.out.println("Symmetric");
        List<String> listI = new ArrayList<>(listA);
        listI.retainAll(listB);                         // intersection
        List<String> listS = new ArrayList<>(listA);
        listS.addAll(listB);                            // both
        listS.removeAll(listI);                         // symmetric difference

        System.out.println("symm :" + listS);
        System.out.println();

// Enumeration  old interface
        System.out.println("Enumeration and Iterator:");
        StringTokenizer stringTokenizer = new StringTokenizer("this is a test");
        System.out.println("Enumeration:");
        while (stringTokenizer.hasMoreElements()) {
            System.out.print(stringTokenizer.nextToken() + " . ");
        }
        System.out.println();
        System.out.println("Iterator:");
        List<String> stringList = new ArrayList<>(Arrays.asList("this is a test".split(" ")));
        Iterator<String> stringIterator = stringList.iterator();
        while (stringIterator.hasNext()) {
            System.out.print(stringIterator.next() + " | ");
        }
        System.out.println();

// Iterable
        System.out.println("Iterable: ");
        MList<String> mList = new MList<>();
        mList.addAll(list.subList(0, 10));

        System.out.println(mList);
        System.out.println("Iterable elements:");
        for (String s1 : mList) {
            System.out.print(s1 + " ");
        }
        System.out.println();

// listIterator
        System.out.println();
        List<String> listC = new ArrayList<>(list.subList(0, 10));
        List<String> listD = new ArrayList<>(list.subList(0, 10));

        System.out.println("\nIterator:");
        Iterator<String> it = listC.iterator();
        int count = 0;
        while (it.hasNext()) {
            String sNext = it.next();
            if (count % 2 == 0) {
                it.remove();   // remove odd elements
            }
            System.out.print("iNext:  " + "iPrev:  " + " hasNext:" + it.hasNext());
            System.out.println(" next:" + sNext);
            count++;
        }

        System.out.println("ListIterator:");
        ListIterator<String> lit = list.listIterator();
        count = 0;
        while (lit.hasNext()) {
            String sNext = lit.next();
            String sPrev = lit.previous();
            System.out.printf("iNext:  %2d iPrev: %2d hasNext: %5b hasPrev: %5b", lit.nextIndex(),
                    lit.previousIndex(), lit.hasNext(), lit.hasPrevious());
            System.out.println("   next:" + sNext + "   prev:" + sPrev);
            if (count % 2 == 0) {
                lit.remove();   // remove odd elements
            } else {
                lit.set(sNext + sPrev);  // нельзя использовать во время удаления
            }

            if (count % 5 == 0) {
                lit.add("Checked_" + count);
            }

            lit.next();
            count++;
        }

        System.out.println("ListIterator Changed:");
        lit = list.listIterator();
        while (lit.hasNext()) {
            System.out.printf("iNext: %2d  next: %s\n", lit.nextIndex(), lit.next());
        }

        System.out.println("\nCollection Iterator:");
        System.out.println("Remove from it >> it.next >> it.remove():");
        List<String> listE = new ArrayList<>(listA);
        it = listE.iterator();
        System.out.println(listE);
        try {
            it.next();
            it.remove();
            System.out.println(listE);
        } catch (ConcurrentModificationException e) {
            System.out.println(e.getMessage() + " " + listE);
        }

        System.out.println("Remove from it >> listF.remove it.next():");
        List<String> listF = new ArrayList<>(listA);
        it = listF.iterator();
        System.out.println(listF);
        try {
            listF.remove(0); // remove 2nd elemebt
            it.next();
        } catch (ConcurrentModificationException e) {
            System.out.println(e);
            System.out.println(listF);
        }

// vector
        Vector<String> vector = new Vector<>(listA);
        Enumeration<String> enumeration = vector.elements();

// ArrayList elementData[]
        System.out.println("ArrayList elementData[]:");
        List<String> listG = new ArrayList<>();
        listG.add("start");
        System.out.println("listG size:" + listG.size());
        ((ArrayList<String>) listG).ensureCapacity(5);
        System.out.println("listG size:" + listG.size());

// LinkedList
        List<String> linkedList = new LinkedList<>();

// ArrayList
        List<Byte> arrayList = new ArrayList<>();
        arrayList.add((byte) 12);

// HashTable
        Hashtable<String, Integer> ht = new Hashtable<>();
        HashMap<String, Integer> hashMap = new HashMap<>();


        Integer m = hashMap.put(null, 1);   // oldValue = null
        int n = hashMap.put(null, 2);       // oldValue = 1
//         ht.put(null,1);            // NullPointerException   key.hachCode()

        hashMap.put("key1", null);
        hashMap.put("key2", null);

//        ht.put("key1",null);        // NullPointerException  value == null
//        ht.put("key2",null);


// HashMap
        HashMap<String, String> hMap = new HashMap<>();

        int hash = hashCode(0, "key1".toCharArray());
        int hash2 = hashCode(0, "key2".toCharArray());
        hash = 31 * (31 * (31 * (31 * 0 + 107) + 101) + 121) + 49;
        hash2 = 31 * (31 * (31 * (31 * 0 + 107) + 101) + 121) + 50;

        hMap.put("key1", "String1");
        hMap.put("key2", "String2");
        hMap.put("key3", "String3");
        for (int i = 4; i < 16; i++) {
            hMap.put("key" + i, "String" + i);
        }
        hMap.put("key16", "String16");
        hMap.put("key17", "String17");
        hMap.put("key18", "String18");

        for (int i = 19; i < 100; i++) {
            hMap.put("key" + i, "String" + i);
        }
        System.out.println(hMap);

// IdentityHashMap
        System.out.println("\nIdentityHashMap:");
        IdentityHashMap<String, String> iMap = new IdentityHashMap<>(hMap);
        System.out.println(iMap);

// WeakHashMapUser
        System.out.println("\nWeakHashMapUser:");
        WeakHashMap<String, String> wMap = new WeakHashMap<>(hMap);
        System.out.println(wMap);

// PhantomReference
        ReferenceQueue<String> queue = new ReferenceQueue<>();
        PhantomReference<String> phantomReference = new PhantomReference<>("s1", queue);
        phantomReference = new PhantomReference<>("s2", queue);

        try {
            System.gc();
            System.gc();
            phantomReference = null;
            System.gc();

            Reference ref = queue.remove(50);
            ref = queue.remove(50);

            int k2 = 1;
            int k3 = 2;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

// keyset()
        System.out.println("\nHashMap:");
        HashSet<String> hashSet = new HashSet<>(hMap.keySet());
        System.out.println("keyset() : " + hashSet);

        HashSet<Map.Entry<String, String>> entryHashSet = new HashSet<>(hMap.entrySet());
        System.out.println("entries(): " + entryHashSet);

// Queue
        System.out.println("\nQueue:");
        Queue<String> q = new LinkedList<>(listA.subList(0, 10));
        Deque<String> deque = new LinkedList<>(listA.subList(0, 10));

        System.out.println("q:            :" + q);
        q.add("newAddItem");
        q.offer("newOfferItem");
        System.out.println("add >> offer  :" + q);
        q.remove();
        q.poll();
        System.out.println("remove >> poll:" + q);
        System.out.println("peek:" + q.peek() + " element:" + q.element());
        System.out.println("q:            :" + q);

        System.out.println("\nDeque:");
        System.out.println("deque:        :" + deque);
        deque.add("newAddItem");
        deque.offer("newOfferItem");
        System.out.println("add >> offer  :" + deque);
        deque.remove();
        deque.poll();
        System.out.println("remove >> poll:" + deque);
        System.out.println("peek:" + deque.peek() + " element:" + deque.element());
        System.out.println("deque:        :" + deque);
// Arrays and Array
        System.out.println("\nArrays int[]: " + Arrays.toString(new int[10]));
        int[] array = (int[]) Array.newInstance(int.class, 10);
        array[1] = 1;
        array[9] = 18;
        System.out.println("Array  int[]: " + Arrays.toString(array));

// Collections and Collection
        System.out.println("\nCollections:");
        Enumeration<String> en = Collections.enumeration(listA);
        while (en.hasMoreElements()) {
            System.out.print(en.nextElement() + " ");
        }


        System.out.println("\nCollection:");
        Collection<String> collection = listA;
        for (String string : collection) {
            System.out.print(string + " ");

        }

// EnumSet

// Stack
        System.out.println("\nStack");
        Stack<Integer> stack = new Stack<>();
        stack.push(12);
        stack.push(22);
        stack.push(24);
        System.out.println(stack);
        System.out.println("stack[2]:" + stack.get(2));
        System.out.println(stack.pop());

        Deque<Integer> dStack = new ArrayDeque<>();
        dStack.push(12);
        dStack.push(22);
        dStack.push(24);
        System.out.println(dStack);
        System.out.println(dStack.pop());

        Deque<Integer> lStack = new LinkedList<>();
        lStack.push(12);
        lStack.push(22);
        lStack.push(24);
        System.out.println(lStack);
        System.out.println(lStack.pop());

    }


}
