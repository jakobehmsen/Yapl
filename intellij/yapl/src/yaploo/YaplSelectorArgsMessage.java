package yaploo;

public class YaplSelectorArgsMessage implements YaplObject {
    private final YaplObject selector;
    private final YaplObject arguments;

    public YaplSelectorArgsMessage(YaplObject selector, YaplObject arguments) {
        this.selector = selector;
        this.arguments = arguments;
    }

    @Override
    public void send(YaplObject message) {

    }

    @Override
    public YaplObject getSelector() {
        return selector;
    }

    @Override
    public YaplObject getArgs() {
        return arguments;
    }


}
