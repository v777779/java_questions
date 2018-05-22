package io;


import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static nio.Main01.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 18-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main01 {



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
                if (pattern.matcher(f.getName()).matches()) treeInfo.add(f);    // проверка тут, так как совпадение может
            }                                                                   // на любом уровне

            return treeInfo;
        }

        public static TreeInfo walk(String path, String wildCard) {
            File filePath = new File(path);
            if (!filePath.exists()) return null;

            String regex = wildCard.replaceAll("\\*", "\\.\\*");
            Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
            return recursive(filePath, pattern);
        }


    }

    public static void main(String[] args) {
        System.out.printf(FORMAT, "IO Stream:");

        List<String> list = getDir("D:/temp2", "*b*");
        System.out.println(list);

        TreeInfo treeInfo = TreeInfo.walk("D:/temp2", "*b[al]*");
        System.out.println(treeInfo);

    }
}
