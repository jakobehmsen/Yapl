package yaploo;

public class YaplProgrammableObject implements YaplObject {
    protected YaplObject environment;

    public YaplProgrammableObject(YaplObject environment) {
        this.environment = environment;
    }

    @Override
    public void send(YaplObject thread, YaplObject message) {
        YaplObject behavior = environment.resolve(message.getSelector().toString());

        behavior.evalOnWith(thread, this, environment, message.getArgs());
    }
}
