package java02.jmxmodel.jmxx;

// Standard MBean Interface
public class Status implements StatusMBean {
    private StatusEnum statusEnum = StatusEnum.UNSPECIFIED;

    public Status() {
    }


    public StatusEnum getStatus() {
        return this.statusEnum;
    }

    public void setStatus(StatusEnum status) {
        this.statusEnum = status;
    }


    public StatusEnum printStatus() {
        return null;
    }

    public StatusEnum sayStatus(){
        return this.statusEnum;
    }
}  