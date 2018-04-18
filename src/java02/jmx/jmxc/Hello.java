package java02.jmx.jmxc;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 18-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Hello implements HelloMBean{
    private String message = null;

    public Hello() {
        this.message = "Hello there";
    }

    public Hello(String message) {
        this.message = message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void sayHello() {
        System.out.println(message);
    }
}
