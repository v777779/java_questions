package nio1.selectors.sockets;

import nio1.selectors.pipes.UserPipeSink;
import nio1.selectors.pipes.UserPipeSource;

public class UserSocketInfo {
    String name;
    int id;
    int delay;

    public UserSocketInfo(UserPipeSource pipe) {
        this.name = pipe.getSource().getClass().getSimpleName();
        this.id = pipe.getId();
        this.delay = pipe.getDelay();
    }

    public UserSocketInfo(UserPipeSink pipe) {
        this.name = pipe.getSource().getClass().getSimpleName();
        this.id = pipe.getId();
        this.delay = pipe.getDelay();
    }
}