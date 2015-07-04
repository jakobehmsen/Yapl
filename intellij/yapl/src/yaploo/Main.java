package yaploo;

public class Main {
    public static void main(String[] args) {
        YaplArray program = new YaplArray(new YaplObject[]{
            YaplPrimitive.Factory.push(new YaplInteger(3)),
            YaplPrimitive.Factory.push(new YaplInteger(5)),
            YaplPrimitive.Factory.integerAdd
        });

        YaplThread thread = new YaplThread(new YaplFrame());
        program.eval(thread);
        YaplObject result = thread.getFrame().pop();

        System.out.println(result);
    }
}
