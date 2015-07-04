package yaploo;

public class Main {
    public static void main(String[] args) {
        /*
        What's needed to build a repl?

        How to send a message?
        */

        YaplArray addNumbersBody = new YaplArray(new YaplObject[]{
            YaplPrimitive.Factory.load("x"),
            YaplPrimitive.Factory.load("y"),
            YaplPrimitive.Factory.integerAdd,
            YaplPrimitive.Factory.respond
        });

        YaplEnvironment env = new YaplEnvironment();
        env.define("addNumbers", new YaplBehavior(new YaplArray(new YaplObject[]{new YaplString("x"), new YaplString("y")}), addNumbersBody));
        YaplProgrammableObject obj = new YaplProgrammableObject(env);

        YaplArray program = new YaplArray(new YaplObject[]{
            YaplPrimitive.Factory.push(obj),
            YaplPrimitive.Factory.push(new YaplSelectorArgsMessage(new YaplString("addNumbers"), new YaplArray(new YaplObject[]{new YaplInteger(7), new YaplInteger(3)}))),
            YaplPrimitive.Factory.send,
            YaplPrimitive.Factory.finish
        });

        YaplThread thread = new YaplThread(new YaplFrame(null, null, null, program));
        thread.run();

        YaplObject result = thread.getFrame().pop();

        System.out.println(result);
    }
}
