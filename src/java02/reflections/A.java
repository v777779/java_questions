package java02.reflections;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 17-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
@AA(name = "Name Class A")
public class A implements IA {
    @AA(name = "String_name", value = 2)
    @BB(name = "String_name", value = 4)
    private String name;

    @AA(name = "int_id", value = 3)
    private int id;

    @AA(name = "Name Constructor A", value = 5)
    public A(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @AA(name = "start() A", value = 7)
    @Override
    public IA start(int value, String name) {
        return new A(name, id);
    }

    @AA(name = "stop() A", value = 8)
    @Override
    public IA stop(int value) {
        return new A(name, id);
    }


    @Override
    public String toString() {
        return "A name:" + name + " value:" + id;
    }
}
