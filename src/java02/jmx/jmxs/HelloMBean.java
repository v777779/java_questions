package java02.jmx.jmxs;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 18-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public interface HelloMBean {
    public void setMessage(String message);
    public String getMessage();
    public void sayHello();

}
