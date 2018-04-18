package java02.jmxmodel.jmxx;

// Requires Name of Class in the name of Interface
// Does not support enum
public interface StatusMBean {
   public StatusEnum getStatus();
  
   public void setStatus(final StatusEnum status);

    public StatusEnum sayStatus();

   public enum StatusEnum{ SUCCESSFUL, FAILURE, UNSPECIFIED };

}  