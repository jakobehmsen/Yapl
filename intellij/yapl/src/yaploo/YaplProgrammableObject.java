package yaploo;

public class YaplProgrammableObject implements YaplObject {
    private YaplObject environment;

    public YaplProgrammableObject(YaplObject environment) {
        this.environment = environment;
    }

    @Override
    public void send(YaplObject thread, YaplObject message) {
        YaplObject behavior = environment.resolve(message.getSelector().toString());

        YaplObject environment = this.environment.extend();

        behavior.bindArguments(message.getArgs(), environment);

        thread.pushFrame(this, environment, behavior.getInstructions());
    }
}
