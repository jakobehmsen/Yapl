package yaploo;

public class YaplBehavior implements YaplObject {
    private final YaplObject parameterNames;
    private final YaplObject body;

    public YaplBehavior(YaplObject parameterNames, YaplObject body) {
        this.parameterNames = parameterNames;
        this.body = body;
    }

    @Override
    public void send(YaplObject thread, YaplObject message) {

    }

    @Override
    public void eval(YaplObject thread) {
        body.eval(thread);
    }

    @Override
    public void evalOnWith(YaplObject thread, YaplObject self, YaplObject environment, YaplObject args) {
        YaplObject sendEnvironment = environment.extend();

        int paramCount = parameterNames.length().toInt();
        for(int i = 0; i < paramCount; i++) {
            String paramName = parameterNames.get(i).toString();
            YaplObject obj = args.get(i);

            sendEnvironment.define(paramName, obj);
        }

        thread.pushFrame(this, sendEnvironment, body, args);
    }
}
