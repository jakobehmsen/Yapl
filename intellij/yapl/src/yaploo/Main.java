package yaploo;

public class Main {
    public static void main(String[] args) {
        /*
        What's needed to build a repl?

        How to send a message?
        */

        YaplArray addNumbersBody = new YaplArray(new YaplObject[]{
            YaplPrimitive.Factory.push(new YaplInteger(3)),
            YaplPrimitive.Factory.push(new YaplInteger(5)),
            YaplPrimitive.Factory.integerAdd
        });

        YaplEnvironment env = new YaplEnvironment();
        env.define("addNumbers", new YaplBehavior(new YaplArray(), addNumbersBody));
        YaplProgrammableObject obj = new YaplProgrammableObject(env);

        YaplArray program = new YaplArray(new YaplObject[]{
            YaplPrimitive.Factory.push(obj),
            YaplPrimitive.Factory.push(new YaplSelectorArgsMessage(new YaplString("addNumbers"), new YaplArray())),
            YaplPrimitive.Factory.send
        });

        YaplThread thread = new YaplThread(new YaplFrame(null, null, null));
        program.eval(thread);
        YaplObject result = thread.getFrame().pop();

        System.out.println(result);
    }
}
