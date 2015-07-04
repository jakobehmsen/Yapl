package yaploo;

public abstract class YaplPrimitive implements YaplObject {
    @Override
    public void send(YaplObject thread, YaplObject message) {

    }

    @Override
    public abstract void eval(YaplObject thread);

    public static class Factory {
        public static final YaplPrimitive arrayGet = new YaplPrimitive() {
            @Override
            public void eval(YaplObject thread) {
                YaplObject index = thread.getFrame().pop();
                YaplObject array = thread.getFrame().pop();

                YaplObject result = array.get(index.toInt());

                thread.getFrame().push(result);
            }
        };

        public static final YaplPrimitive send = new YaplPrimitive() {
            @Override
            public void eval(YaplObject thread) {
                YaplObject message = thread.getFrame().pop();
                YaplObject receiver = thread.getFrame().pop();

                receiver.send(thread, message);
            }
        };

        public static final YaplPrimitive integerAdd = new YaplPrimitive() {
            @Override
            public void eval(YaplObject thread) {
                YaplObject rhs = thread.getFrame().pop();
                YaplObject lhs = thread.getFrame().pop();

                YaplObject result = new YaplInteger(lhs.toInt() + rhs.toInt());

                thread.getFrame().push(result);
            }
        };

        public static YaplPrimitive push(YaplObject obj) {
            return new YaplPrimitive() {
                @Override
                public void eval(YaplObject thread) {
                    thread.getFrame().push(obj);
                }
            };
        }
    }
}
