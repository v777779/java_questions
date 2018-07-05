package nio2.walktree_CHECK_THIS;

import nio2.files.MainFileUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.regex.Pattern;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 13-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class WalkUtils {
    interface Incrementable {
        int inc();

        int skip();
    }

    public static class Anonym {
        private static Incrementable getIncrementStatic() throws Exception {
            final Incrementable m = new Incrementable() {
                private int value = 0;

                @Override
                public int inc() {
                    return value++;
                }

                @Override
                public int skip() {
                    value++;
                    return 0;
                }
            };

            return m.getClass().getDeclaredConstructor().newInstance();
        }

        private Incrementable[] getIncrement() throws Exception {
            final Incrementable m = new Incrementable() {
                private int value = 0;

                @Override
                public int inc() {
                    return value++;
                }

                @Override
                public int skip() {
                    value++;
                    return 0;
                }
            };
            Incrementable m2 = null;
            try {
                m2 = m.getClass().getDeclaredConstructor(this.getClass()).newInstance(new Object[]{this});
            } catch (NoSuchMethodException e) {
                System.out.printf("Exception:%s%n", e);
            }

            Constructor[] cc = m.getClass().getDeclaredConstructors();
            Object[] params = new Object[]{this};
            Incrementable m3 = (Incrementable) cc[0].newInstance(params);

            return new Incrementable[]{m, m2, m3, Anonym.getIncrementStatic()};
        }
    }

    public static void createAnonymInstances() {
        // anonym
        System.out.printf(FORMAT, "Incrementable Anonym Class Demo:");
        try {
            Incrementable[] mm = new Anonym().getIncrement();

            System.out.printf("inc :m:%d m2:%d m3:%d m4:%d%n", mm[0].inc(), mm[1].inc(), mm[2].inc(), mm[3].inc());
            System.out.printf("skip:m:%d m2:%d m3:%d m4:%d%n", mm[0].skip(), mm[1].skip(), mm[2].skip(), mm[3].skip());
            System.out.printf("inc :m:%d m2:%d m3:%d m4:%d%n", mm[0].inc(), mm[1].inc(), mm[2].inc(), mm[3].inc());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void createUnderTree(Path path, Path pathT) throws IOException {  // under threshold for files sources
        if (Files.exists(path)) MainFileUtils.deleteFolder(path); // remove
        Files.createDirectories(path);

// local class
        class MyInt {
            private int value = 0;

            private int inc() {
                value++;
                return value;
            }

            private int skip() {
                value++;
                return 0;
            }
        }

// create walk tree under temp
        final MyInt m = new MyInt();
        int index = Pattern.compile("\\\\").splitAsStream(path.toString())
                .mapToInt(s -> s.equals(pathT.getFileName().toString()) ? m.inc() : m.skip())
                .reduce(0, (v1, v2) -> v1 + v2);

        Path pathC = path.subpath(0, index);
        Path pathD = Paths.get(pathC.toString(), "result.txt");
        Path pathE = Paths.get(pathC.toString(), "result_k.txt");
        Path pathR;

        pathR = Paths.get(path.toString(), "walk");
        if (!Files.exists(pathR)) Files.createDirectory(pathR);
        Files.copy(pathD, pathR.resolve(pathD.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(pathE, pathR.resolve(pathE.getFileName()), StandardCopyOption.REPLACE_EXISTING);

        pathR = Paths.get(path.toString(), "wmode");
        if (!Files.exists(pathR)) Files.createDirectory(pathR);
        Files.copy(pathD, pathR.resolve(pathD.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(pathD, pathR.resolve("result_k.txt"), StandardCopyOption.REPLACE_EXISTING);

        pathR = Paths.get(path.toString(), "wnet");
        if (!Files.exists(pathR)) Files.createDirectory(pathR);
    }


    public static void createIfFolder(Path path) throws IOException {
        if (Files.exists(path)) return;
        Files.createDirectory(path);
    }

    public static void createForceFolder(Path path) throws IOException {
        if(Files.exists(path)) MainFileUtils.deleteFolder(path);
        Files.createDirectory(path);
    }

    public static void createIfTree(Path path) throws IOException {
        if (Files.exists(path)) return;
        Files.createDirectory(path);

// local class
        class MyInt {
            private int value = 0;

            private int inc() {
                return value++;
            }

            private int skip() {
                value++;
                return 0;
            }
        }

        Path pathD = Paths.get(path.toString(), "result.txt");
        Path pathE = Paths.get(path.toString(), "result_k.txt");
        Path pathR;

        pathR = Paths.get(path.toString(), "walk");
        if (!Files.exists(pathR)) Files.createDirectory(pathR);
        Files.copy(pathD, pathR.resolve(pathD.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(pathE, pathR.resolve(pathE.getFileName()), StandardCopyOption.REPLACE_EXISTING);

        pathR = Paths.get(path.toString(), "wmode");
        if (!Files.exists(pathR)) Files.createDirectory(pathR);
        Files.copy(pathD, pathR.resolve(pathD.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(pathD, pathR.resolve("result_k.txt"), StandardCopyOption.REPLACE_EXISTING);

        pathR = Paths.get(path.toString(), "wnet");
        if (!Files.exists(pathR)) Files.createDirectory(pathR);
    }

    public static void removeIfTree(Path path) throws IOException {
        if (!Files.exists(path)) return;
        MainFileUtils.deleteFolder(path);  // recursive
    }


}
