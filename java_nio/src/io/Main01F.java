package io;


import io.objstream.BaseFactory;
import util.IOUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static util.IOUtils.FORMAT;
import static util.IOUtils.PATH;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 18-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main01F {
    private static void checkSerializableOut(Class<? extends ObjectOutputStream> c, String path) throws IOException,
            ClassNotFoundException, SecurityException {
        FileOutputStream out = null;
        ObjectOutputStream os = null;
        try {
            out = new FileOutputStream(path);
            os = c.getDeclaredConstructor(OutputStream.class).newInstance(out);
            os.writeObject(BaseFactory.newPerson());
            if (c.getName().contains("SubTest4Out")) {
                os.getClass().getDeclaredMethod("enableReplaceObject", boolean.class).invoke(os, true);
            }
            if (c.getName().contains("SubTest5Out")) {
                os.getClass().getDeclaredMethod("customMethod", int.class).invoke(os, -1);
            }
            os.flush();

        } catch (NoSuchMethodException | IllegalAccessException
                | InvocationTargetException | InstantiationException e) {
            if (e.getCause() instanceof SecurityException) {
                System.out.println(e.getCause().toString());
            } else {
                System.out.println(e);
            }
        } finally {
            IOUtils.close(os);
            IOUtils.close(out);
        }
    }

    private static void checkSerializableIn(Class<? extends ObjectInputStream> c, String path) throws IOException,
            ClassNotFoundException, SecurityException {
        FileInputStream in = null;
        ObjectInputStream is = null;
        try {
            in = new FileInputStream(path);
            is = c.getDeclaredConstructor(InputStream.class).newInstance(in);
            System.out.println(is.readObject());
            if (c.getName().contains("SubTest4In")) {
                is.getClass().getDeclaredMethod("enableResolveObject", boolean.class).invoke(is, true);
            }
            if (c.getName().contains("SubTest5In")) {
                is.getClass().getDeclaredMethod("customMethod", int.class).invoke(is, -1);
            }

        } catch (NoSuchMethodException | IllegalAccessException
                | InvocationTargetException | InstantiationException e) {
            if (e.getCause() instanceof SecurityException) {
                System.out.println(e.getCause().toString());
            } else {
                System.out.println(e);
            }
        } finally {
            IOUtils.close(is);
            IOUtils.close(in);
        }
    }


    private static void checkSerializable(ObjectOutputStream os, BufferedOutputStream out, String path)
            throws IOException, ClassNotFoundException, SecurityException {
// write
        BufferedInputStream in = null;
        ObjectInputStream is = null;

        try {
            os.writeObject(BaseFactory.newPerson());
            os.writeObject(BaseFactory.newPerson());
            os.writeObject(BaseFactory.newPerson());
            os.flush();
// read
            in = new BufferedInputStream(new FileInputStream(path));
            is = new ObjectInputStream(in);
            for (int i = 0; i < 3; i++) {
                System.out.println(is.readObject());
            }

        } finally {
            IOUtils.close(os);
            IOUtils.close(out);
            IOUtils.close(is);
            IOUtils.close(in);
        }
    }

    private static FilenameFilter filter(final String wildCard) {
        return new FilenameFilter() {
            final String regex = wildCard.replaceAll("\\*", ".*");
            private Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

            @Override
            public boolean accept(File dir, String name) {
                return pattern.matcher(name).matches();
            }
        };
    }

    private static List<String> getDir(String sPath, String sPattern) {
        File path = new File(sPath);
        if (!path.exists()) {
            System.out.println("Path not exists!");
            return null;
        }

        String[] paths = path.list(filter(sPattern));
        if (paths == null) return null;

        return Stream.of(paths)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }

    private static class TreeInfo implements Iterable<File> {
        private List<File> listDirs;

        public TreeInfo() {
            this.listDirs = new ArrayList<>();
        }


        @Override
        public Iterator<File> iterator() {
            return listDirs.iterator();
        }

        public List<File> getListDirs() {
            return listDirs;
        }

        public boolean isEmpty() {
            return listDirs == null || listDirs.isEmpty();
        }

        public void addAll(TreeInfo treeInfo) {
            if (treeInfo == null || treeInfo.isEmpty()) return;
            listDirs.addAll(treeInfo.getListDirs());
        }

        public void add(File file) {
            if (file == null || !file.exists()) return;
            listDirs.add(file);
        }

        private FilenameFilter filter(final String regex) {
            return new FilenameFilter() {
                private Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

                @Override
                public boolean accept(File dir, String name) {
                    return pattern.matcher(name).matches();
                }
            };
        }

        @Override
        public String toString() {
            if (listDirs == null || listDirs.isEmpty()) return "[]";

            StringJoiner sj = new StringJoiner("");
            IntStream.range(0, listDirs.size())
                    .forEach(n -> {
                                sj.add(String.format("%-50s", listDirs.get(n)));
                                if ((n + 1) % 2 == 0) sj.add(String.format(",%n"));
                                else {
                                    if (n < listDirs.size() - 1) sj.add(", ");
                                }

                            }
                    );
            return sj.toString();
        }

        private static TreeInfo recursive(File filePath, Pattern pattern) {
            TreeInfo treeInfo = new TreeInfo();
            File[] paths = filePath.listFiles();                                // любые каталоги совпадение по факту

            if (paths == null) return null;

            for (File f : paths) {
                if (f.isFile()) continue;
                treeInfo.addAll(recursive(f, pattern));
                if (pattern.matcher(f.getName()).matches())
                    treeInfo.add(f);    // проверка тут, так как совпадение может
            }                                                                   // на любом уровне

            return treeInfo;
        }

        public static TreeInfo walk(String path, String wildCard) {
            File filePath = new File(path);
            if (!filePath.exists()) return null;

            String regex = wildCard.replaceAll("\\*", "\\.\\*");
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            return recursive(filePath, pattern);
        }


    }

    public static void main(String[] args) {
        System.out.printf(FORMAT, "IO File:");

        List<String> list = getDir("D:/temp2", "*b*");
        System.out.println(list);

        TreeInfo treeInfo = TreeInfo.walk("D:/temp2", "*b[al]*");
        System.out.println(treeInfo);

// Serializable
        System.out.printf(FORMAT, "Serializable:");
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        ObjectInputStream is = null;
        ObjectOutputStream os = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(PATH + "person.dat"));
            os = new ObjectOutputStream(out);
            os.writeObject(BaseFactory.newPerson());
            os.writeObject(BaseFactory.newPerson());
            os.writeObject(BaseFactory.newPerson());
            os.flush();
            os.close();

            in = new BufferedInputStream(new FileInputStream(PATH + "person.dat"));
            is = new ObjectInputStream(in);
            System.out.println(is.readObject());
            System.out.println(is.readObject());
            System.out.println(is.readObject());
            is.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(os);
            IOUtils.close(is);

            IOUtils.close(in);
            IOUtils.close(out);
        }
// File


//FileDescriptor
        System.out.printf(FORMAT, "FileDescriptor:");
        FileInputStream fs = null;
        FileInputStream fs2 = null;
        FileDescriptor fd = null;
        try {
            fs = new FileInputStream(PATH + "result.txt");
            fd = fs.getFD();
            fs2 = new FileInputStream(fd);

            IOUtils.readout(fs);
            IOUtils.readout(fs2, new byte[10]);  // IOException т.к. это тот же поток и он уже закрыт

// читаем попеременно
            fs = new FileInputStream(PATH + "result.txt");
            fd = fs.getFD();
            fs2 = new FileInputStream(fd);
            IOUtils.readout(fs, new byte[50], 50);            // читаем попеременно
            IOUtils.readout(fs2, new byte[50], 50);
            IOUtils.readout(fs, new byte[50], 50);             // не закрываем оба потока

            System.out.println("fs2:" + fs2.available());  // поток тоже закрыт

            System.out.printf(FORMAT, "FileDescriptor valid()");
            System.out.println("fs opened:" + fd.valid());
            fs.close();
            System.out.println("fs closed:" + fd.valid());

        } catch (IOException e) {
            System.out.println("IOException:" + e);
        } finally {
            IOUtils.close(fs);
            IOUtils.close(fs2);
        }


//FilePermission
        System.out.printf(FORMAT, "FilePermission:");
        try {
            String path = PATH + "result.txt";
            FilePermission fp1 = new FilePermission(PATH + "-", "read");
            PermissionCollection pc = fp1.newPermissionCollection();
            pc.add(fp1);
            FilePermission fp2 = new FilePermission(path, "write");
            pc.add(fp2);
            Enumeration<Permission> en = pc.elements();
            while (en.hasMoreElements()) {
                Permission p = en.nextElement();
                System.out.println(p.getName() + " actions:" + p.getActions());
            }

            if (pc.implies(new FilePermission(path, "read,write"))) {
                System.out.println("Permission for " + path + " is read and write.");
            } else {
                System.out.println("No read, write permission for " + path);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

// SerializablePermission
        System.out.printf(FORMAT, "SerializablePermission:");
// classes
        final class SubTest1Out extends ObjectOutputStream {
            private SubTest1Out(OutputStream out) throws IOException, SecurityException {
                super(out);
            }
        }
        final class SubTest2Out extends ObjectOutputStream {
            /**
             * <p>If a security manager is installed, this constructor will check for
             * the "enableSubclassImplementation" SerializablePermission when invoked
             * directly or indirectly by the constructor of a subclass which overrides
             * the ObjectOutputStream.putFields or ObjectOutputStream.writeUnshared
             * methods.
             */
            protected SubTest2Out(OutputStream out) throws IOException, SecurityException {
                super(out);
            }

            @Override
            public void writeUnshared(Object obj) throws IOException {
                super.writeUnshared(obj);
            }
        }
        final class SubTest3Out extends ObjectOutputStream {
            protected SubTest3Out(OutputStream out) throws IOException, SecurityException {
                super(out);
            }

            /**
             * <p>If a security manager is installed, this constructor will check for
             * the "enableSubclassImplementation" SerializablePermission when invoked
             * directly or indirectly by the constructor of a subclass which overrides
             * the ObjectOutputStream.putFields or ObjectOutputStream.writeUnshared
             * methods.
             */
            @Override
            public PutField putFields() throws IOException {
                return super.putFields();
            }
        }

        final class SubTest4Out extends ObjectOutputStream {
            protected SubTest4Out(OutputStream out) throws IOException, SecurityException {
                super(out);
            }

            /*
             * {@code SerializablePermission("enableSubstitution")} permission to
             * ensure that the caller is permitted to enable the stream to do replacement
             * of objects written to the stream.
             */
            @Override
            public boolean enableReplaceObject(boolean enable) throws SecurityException {
                return super.enableReplaceObject(enable);
            }
        }

        final class SubTest5Out extends ObjectOutputStream {
            private final SerializablePermission USER_SERIALIZABLE_PERMISSION =
                    new SerializablePermission("userSerializable");

            protected SubTest5Out(OutputStream out) throws IOException, SecurityException {
                super(out);
            }

            public boolean customMethod(int value) {
                if (value < 0) {
                    SecurityManager sm = System.getSecurityManager();
                    if (sm != null) {
                        sm.checkPermission(USER_SERIALIZABLE_PERMISSION);
                    }
                }
                return value > 0;
            }
        }

        final class SubTest1In extends ObjectInputStream {
            /**
             * <p>If a security manager is installed, this constructor will check for
             * the "enableSubclassImplementation" SerializablePermission when invoked
             * directly or indirectly by the constructor of a subclass which overrides
             * the ObjectInputStream.readFields or ObjectInputStream.readUnshared
             * methods.
             */
            public SubTest1In(InputStream in) throws IOException {
                super(in);
            }


        }
        final class SubTest2In extends ObjectInputStream {
            /**
             * <p>If a security manager is installed, this constructor will check for
             * the "enableSubclassImplementation" SerializablePermission when invoked
             * directly or indirectly by the constructor of a subclass which overrides
             * the ObjectInputStream.readFields or ObjectInputStream.readUnshared
             * methods.
             */
            public SubTest2In(InputStream in) throws IOException {
                super(in);
            }

            // Serializable Permission  enableSubclassImplementation
            @Override
            public GetField readFields() throws IOException, ClassNotFoundException {
                return super.readFields();
            }
        }
        final class SubTest3In extends ObjectInputStream {
            /**
             * <p>If a security manager is installed, this constructor will check for
             * the "enableSubclassImplementation" SerializablePermission when invoked
             * directly or indirectly by the constructor of a subclass which overrides
             * the ObjectInputStream.readFields or ObjectInputStream.readUnshared
             * methods.
             */
            public SubTest3In(InputStream in) throws IOException {
                super(in);
            }

            // Serializable Permission  enableSubclassImplementation
            @Override
            public Object readUnshared() throws IOException, ClassNotFoundException {
                return super.readUnshared();
            }
        }

        final class SubTest4In extends ObjectInputStream {
            /**
             * <p>If a security manager is installed, this constructor will check for
             * the "enableSubclassImplementation" SerializablePermission when invoked
             * directly or indirectly by the constructor of a subclass which overrides
             * the ObjectInputStream.readFields or ObjectInputStream.readUnshared
             * methods.
             */
            public SubTest4In(InputStream in) throws IOException {
                super(in);
            }
            // Serializable Permission  enableSubstitution

            /**
             * <p>If object replacement is currently not enabled, and
             * {@code enable} is true, and there is a security manager installed,
             * this method first calls the security manager's
             * {@code checkPermission} method with the
             * {@code SerializablePermission("enableSubstitution")} permission to
             * ensure that the caller is permitted to enable the stream to do replacement
             * of objects read from the stream.
             */
            @Override
            protected boolean enableResolveObject(boolean enable) throws SecurityException {
                return super.enableResolveObject(enable);
            }

        }

        final class SubTest5In extends ObjectInputStream {
            private final SerializablePermission USER_SERIALIZABLE_PERMISSION =
                    new SerializablePermission("userSerializable");

            /**
             * <p>If a security manager is installed, this constructor will check for
             * the "enableSubclassImplementation" SerializablePermission when invoked
             * directly or indirectly by the constructor of a subclass which overrides
             * the ObjectInputStream.readFields or ObjectInputStream.readUnshared
             * methods.
             */
            public SubTest5In(InputStream in) throws IOException {
                super(in);
            }

            public boolean customMethod(int value) {
                if (value < 0) {
                    SecurityManager sm = System.getSecurityManager();
                    if (sm != null) {
                        sm.checkPermission(USER_SERIALIZABLE_PERMISSION);
                    }
                }
                return value > 0;
            }

        }
// code check
        final Class<?>[] TEST_CLASSES = {
                SubTest1Out.class, SubTest1In.class,
                SubTest2Out.class, SubTest2In.class,
                SubTest3Out.class, SubTest3In.class,
                SubTest4Out.class, SubTest4In.class,
                SubTest5Out.class, SubTest5In.class

        };
        in = null;
        out = null;
        is = null;
        os = null;
        SecurityManager oldSm = null;
        try {
            SecurityManager sm = new SecurityManager() {
                final SerializablePermission SUBCLASS_IMPLEMENTATION_PERMISSION =
                        new SerializablePermission("enableSubclassImplementation");
                final SerializablePermission SUBSTITUTION_PERMISSION =
                        new SerializablePermission("enableSubstitution");
                final SerializablePermission USER_SERIALIZABLE_PERMISSION =
                        new SerializablePermission("userSerializable");

                @Override
                public void checkPermission(Permission perm) {
                    if (perm.equals(SUBCLASS_IMPLEMENTATION_PERMISSION))
                        throw new SecurityException("Enable Subclass Implementation");
                    if (perm.equals(SUBSTITUTION_PERMISSION))
                        throw new SecurityException("Enable Substitution");
                    if (perm.equals(USER_SERIALIZABLE_PERMISSION))
                        throw new SecurityException("User Serializable");

                }
            };

            oldSm = System.getSecurityManager();
            System.setSecurityManager(sm);
// common stream
            System.out.printf(FORMAT, "ObjectOutputStream.class:");
            try {
                checkSerializableOut(ObjectOutputStream.class, PATH + "person.dat");
            } catch (SecurityException e) {
                System.out.println("SecurityException:" + e);
            }

            System.out.printf(FORMAT, "ObjectOutputStream:");
            try {
                out = new BufferedOutputStream(new FileOutputStream(PATH + "person.dat"));
                os = new SubTest1Out(out);
                checkSerializable(os, out, PATH + "person.dat");
            } catch (SecurityException e) {
                System.out.println("SecurityException:" + e);
            }

// subclass
            for (Class c : TEST_CLASSES) {
                System.out.printf(FORMAT, c.getSimpleName() + ".class:");
                try {
                    if (c.getSuperclass().equals(ObjectOutputStream.class)) {
                        checkSerializableOut((Class<? extends ObjectOutputStream>) c, PATH + "person.dat");
                    } else {
                        checkSerializableIn((Class<? extends ObjectInputStream>) c, PATH + "person.dat");
                    }
                } catch (SecurityException e) {
                    System.out.println("SecurityException:" + e);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(os);
            IOUtils.close(out);
            IOUtils.close(is);
            IOUtils.close(in);

            if (oldSm != null) {
                System.setSecurityManager(oldSm);  // restore SecurityManager
            }
        }


// RandomAccessFile
        System.out.printf(FORMAT, "RandomAccessFile:");
        RandomAccessFile ra = null;
        BufferedReader br = null;
        try {
            ra = new RandomAccessFile(PATH + "random.txt", "rw"); // чтение и запись
            br = new BufferedReader(new FileReader(PATH + "result.txt"), 100);
            String s;
            double d = 1.23;
            int counter = 1;
            while ((s = br.readLine()) != null) {
                s = String.format("%s", s);
                ra.writeInt(s.length());        // 4 bytes
                ra.writeUTF(s);                 // s.length() + 2
                ra.writeDouble(d);              // 8 bytes
                ra.writeDouble(d * counter);   // 8 bytes
                counter++;
            }
// rewind
            ra.seek(0);         // set to 0 pos
            long pos2 = 0;
            long pos3 = 0;
            for (int i = 0; i < 4; i++) {
                int len = ra.readInt();  //
                s = ra.readUTF();        //
                if(i == 2) pos2 = ra.getFilePointer();
                if(i == 3) pos3 = ra.getFilePointer();
                double d1 = ra.readDouble();
                double d2 = ra.readDouble();
                System.out.printf("%02d <%-25s> %5.2f %5.2f%n", len, s, d1, d2);
            }
            ra.seek(pos2);
            ra.writeDouble(Math.random()*100);
            ra.writeDouble(Math.random()*100);
            ra.seek(pos3);
            ra.writeDouble(Math.random()*100);
            ra.writeDouble(Math.random()*100);
            ra.close();

            System.out.printf(FORMAT,"Random seek():");
            ra = new RandomAccessFile(PATH + "random.txt", "rw"); // чтение и запись
            ra.seek(25);         // set to 0 pos
            for (int i = 0; i < 4; i++) {
                int len = ra.readInt();  //
                s = ra.readUTF();        //
                double d1 = ra.readDouble();
                double d2 = ra.readDouble();
                System.out.printf("%02d <%-25s> %5.2f %5.2f%n", len, s, d1, d2);
            }
            ra.close();
            System.out.printf(FORMAT,"win-1251:");
            ra = new RandomAccessFile(PATH + "result_w.txt", "r"); // чтение и запись
            int len;
            byte[] bytes = new byte[50];
            while((len = ra.read(bytes))> 0) {
                System.out.printf("%s",new String(bytes,0,len,Charset.forName("WINDOWS-1251")));
            }
            ra.close();
            System.out.printf(FORMAT,"koi8r:");
            ra = new RandomAccessFile(PATH + "result_k.txt", "r"); // чтение и запись
            while((len = ra.read(bytes))> 0) {
                System.out.printf("%s",new String(bytes,0,len,Charset.forName("KOI8-R")));
            }
            ra.close();

        } catch (
                IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(ra);
            IOUtils.close(br);
        }
        System.exit(0);

//// DecoratorInputStream
//        System.out.printf(FORMAT, "DecoratorInputStream:");
//        in = null;
//        try {
//            in = new BufferedInputStream(new FileInputStream(PATH + "result.txt"), 100);  // internal buffer
//            util.IOUtils.readout(in);
//        } catch (
//                IOException e) {
//            e.printStackTrace();
//        } finally {
//            util.IOUtils.close(in);
//        }
//        System.exit(0);


    }


}

