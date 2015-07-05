package yaploo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) {
        /*
        What's needed to build a repl?

        How to send a message?
        */

        String source = "(hello)";
        //InputStream inputStream = new ByteArrayInputStream(source.getBytes());
        YaplObject inputStreamEnv = new YaplEnvironment();
        /*inputStreamEnv.define("input", new YaplNative(inputStream));
        inputStreamEnv.define("next", new YaplPrimitive() {
            @Override
            public void evalOnWith(YaplObject thread, YaplObject self, YaplObject environment, YaplObject args) {
                InputStream inputStream = (InputStream)environment.resolve("input").toNative();

                try {
                    int b = inputStream.read();
                    thread.getFrame().push(new YaplInteger(b));
                    thread.getFrame().incrementIP();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });*/
        YaplObject inputStreamObj = new YaplProgrammableObject(inputStreamEnv) {
            InputStream inputStream = new ByteArrayInputStream(source.getBytes());

            {
                environment.define("next", new YaplPrimitive() {
                    @Override
                    public void evalOnWith(YaplObject thread, YaplObject self, YaplObject environment, YaplObject args) {
                        try {
                            int b = inputStream.read();
                            thread.getFrame().push(new YaplInteger(b));
                            thread.getFrame().incrementIP();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        YaplObject program = new YaplArray(new YaplObject[]{
            YaplPrimitive.Factory.push(inputStreamObj),
            YaplPrimitive.Factory.push(new YaplSelectorArgsMessage(new YaplString("next"), new YaplArray(new YaplObject[]{}))),
            YaplPrimitive.Factory.send,
            YaplPrimitive.Factory.finish
        });

        /*YaplObject addNumbersBody = new YaplArray(new YaplObject[]{
            YaplPrimitive.Factory.load("x"),
            YaplPrimitive.Factory.load("y"),
            YaplPrimitive.Factory.integerAdd,
            YaplPrimitive.Factory.respond
        });

        YaplObject env = new YaplEnvironment();
        env.define("addNumbers", new YaplBehavior(new YaplArray(new YaplObject[]{new YaplString("x"), new YaplString("y")}), addNumbersBody));
        YaplProgrammableObject obj = new YaplProgrammableObject(env);

        YaplObject program = new YaplArray(new YaplObject[]{
            YaplPrimitive.Factory.push(obj),
            YaplPrimitive.Factory.push(new YaplSelectorArgsMessage(new YaplString("addNumbers"), new YaplArray(new YaplObject[]{new YaplInteger(7), new YaplInteger(3)}))),
            YaplPrimitive.Factory.send,
            YaplPrimitive.Factory.finish
        });*/

        YaplObject thread = new YaplThread(new YaplFrame(null, null, null, program, null));
        thread.run();

        YaplObject result = thread.getFrame().pop();

        System.out.println(result);
    }
}
