package java02.jmxmodel.jmxx;

public class Status2 implements StatusMXBean {
    private StatusEnum statusEnum = StatusEnum.FAILURE;

    public Status2() {
    }

    public StatusEnum getStatus() {
        return this.statusEnum;
    }

    public void setStatus(final StatusEnum status) {
        this.statusEnum = status;
    }

    @Override
    public StatusEnum printStatus() {
        return this.statusEnum;
    }
}  