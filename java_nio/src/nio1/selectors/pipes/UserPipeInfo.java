package nio1.selectors.pipes;

public class UserPipeInfo {
    String name;
    int id;
    int delay;

    public UserPipeInfo(UserPipeSource pipe) {
        this.name = pipe.getSource().getClass().getSimpleName();
        this.id = pipe.getId();
        this.delay = pipe.getDelay();
    }

    public UserPipeInfo(UserPipeSink pipe) {
        this.name = pipe.getSource().getClass().getSimpleName();
        this.id = pipe.getId();
        this.delay = pipe.getDelay();
    }
}