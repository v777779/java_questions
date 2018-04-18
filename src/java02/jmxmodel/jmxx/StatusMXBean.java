package java02.jmxmodel.jmxx;

// Model MXBean Interface
// Does not require Name of Class in the name of Interface
// Supports enum
public interface StatusMXBean {
    StatusEnum getStatus();

    void setStatus(final StatusEnum status);

    StatusEnum printStatus();

    enum StatusEnum {SUCCESSFUL, FAILURE, UNSPECIFIED};
}  