package java02.reflections;


import java.lang.annotation.Annotation;
import java.lang.reflect.*;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 15-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */

public class Main01 {
    private static String getAnnotations(Annotation[] annotations) {
        StringBuilder sb = new StringBuilder();
        for (Annotation an : annotations) {
            Class anClass = an.annotationType();
            Method[] aMethods = anClass.getDeclaredMethods();
            sb.append(an.annotationType().getSimpleName());
            sb.append(":\n");
            for (Method m : aMethods) {
                try {
                    if (m.getReturnType() == Integer.TYPE) {
                        sb.append(m.getName());
                        sb.append("():");
                        sb.append(String.valueOf((int) m.invoke(an)));
                        sb.append(" ");
                    }
                    if (m.getReturnType() == String.class) {
                        sb.append(m.getName());
                        sb.append("():");
                        sb.append((String) m.invoke(an));
                        sb.append(" ");

                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        Object a = new A("A", 12);

// Annotations
        Class aClass = a.getClass();
        Annotation[] annotations = aClass.getAnnotations();
        for (Annotation an : annotations) {
            Class anClass = an.annotationType();
            Method[] aMethods = anClass.getDeclaredMethods();
            StringBuilder sb = new StringBuilder();
            for (Method m : aMethods) {
                try {
                    if (m.getReturnType() == Integer.TYPE) {
                        sb.append(String.valueOf((int) m.invoke(an)));
                        sb.append(" ");
                    }
                    if (m.getReturnType() == String.class) {
                        sb.append((String) m.invoke(an));
                        sb.append(" ");

                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Annotation: " + sb.toString());
        }

// Methods
        Method[] methods = a.getClass().getDeclaredMethods();
        for (Method m : methods) {
            Class[] p = m.getParameterTypes();
            String mName = m.getName();
            System.out.println("Method: " + mName + "():");
            String[] ss = new String[3];

            for (int i = 0; i < p.length; i++) {
                Class c = p[i];
                if (c.getName().equals("int")) {
                    System.out.println("p: Integer");
                    ss[i] = "int";
                }
                if (c.getName().equals("java.lang.String")) {
                    System.out.println("p: String");
                    ss[i] = "String";
                }
            }

            if (m.getReturnType().isAssignableFrom(IA.class)) {
                ss[2] = "A";
            }
            if (m.getReturnType().isAssignableFrom(String.class)) {
                System.out.println("Return: String");
                ss[2] = "String";
            }

            try {
                Object aa = null;
                if (ss[2].equals("A")) {
                    if (ss[0].equals("int") && ss[1] == null) {
                        Integer value = 5;
                        aa = m.invoke(a, value);
                    } else if (ss[0].equals("int") && ss[1].equals("String")) {
                        Integer value = 7;
                        String s = "Start";
                        aa = m.invoke(a, value, s);
                    }

                    System.out.println(aa);
                }
                if (ss[2].equals("String")) {
                    aa = m.invoke(a);
                    System.out.println(aa);
                }

            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

// Fields
        try {
            System.out.println("\nFields: ");
            System.out.println("Object before change A:\n" + a);

            Field[] fields = a.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getModifiers() == Modifier.PRIVATE) {
                    field.setAccessible(true);
                }
                System.out.print("Field: name:" + field.getName() + " type:" + field.getType().getSimpleName() + " ");
                if (field.getType().isAssignableFrom(Integer.TYPE)) {
                    System.out.println("value: " + (Integer) field.get(a));
                    field.set(a, 12);

                }
                if (field.getType().isAssignableFrom(String.class)) {
                    System.out.println("  value: " + (String) field.get(a));
                    field.set(a, "Stop");
                }

                Annotation[] an = field.getDeclaredAnnotations();
                if (an.length > 0) {
                    System.out.println("Annotations:");
                    System.out.println(getAnnotations(an));
                }
            }

            System.out.println("Object after change A:" + a);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
// Constructors
        Object ab = null;
        try {
            aClass = a.getClass();
            Constructor[] cs = aClass.getConstructors();

            for (Constructor c : cs) {
                Class[] params = c.getParameterTypes();

                if (params != null && params.length == 2 &&
                        params[0].getSimpleName().equals("String") &&
                        params[1].getSimpleName().equals("int")) {
                    ab = c.newInstance("Start", 17);

                    System.out.println(ab);
                }

            }
        } catch (IllegalAccessException | InstantiationException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
// Interfaces
        Class[] interfaces = a.getClass().getInterfaces();
        for (Class i : interfaces) {
            System.out.println(i.getSimpleName()+":");
            Method[] ms = i.getDeclaredMethods();
            for (Method m : ms) {
                Class[] ps =  m.getParameterTypes();
                System.out.println("Method:"+m.getName()+"():");
                for (int j = 0; j < ps.length; j++) {
                    System.out.println("param"+j+":"+ps[j].getSimpleName());
                }
                System.out.println("return:"+m.getReturnType().getSimpleName());
            }
        }

    }
}
