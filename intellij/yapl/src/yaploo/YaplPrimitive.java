package yaploo;

public abstract class YaplPrimitive implements YaplObject {
    @Override
    public void send(YaplObject thread, YaplObject message) {

    }

    @Override
    public void eval(YaplObject thread) {
        evalOnWith(thread, thread.getFrame().getSelf(), thread.getFrame().getEnvironment(), thread.getArgs());
    }

    @Override
    public abstract void evalOnWith(YaplObject thread, YaplObject self, YaplObject environment, YaplObject args);

    public static class Factory {
        public static final YaplPrimitive arrayGet = new YaplPrimitive() {
            @Override
            public void eval(YaplObject thread) {
                YaplObject index = thread.getFrame().pop();
                YaplObject array = thread.getFrame().pop();

                YaplObject result = array.get(index.toInt());

                thread.getFrame().push(result);
                thread.getFrame().incrementIP();
            }

            @Override
            public void evalOnWith(YaplObject thread, YaplObject self, YaplObject environment, YaplObject args) {
                YaplObject index = thread.getFrame().pop();
                YaplObject array = thread.getFrame().pop();

                YaplObject result = array.get(index.toInt());

                thread.getFrame().push(result);
                thread.getFrame().incrementIP();
            }
        };

        public static final YaplPrimitive send = new YaplPrimitive() {
            @Override
            public void evalOnWith(YaplObject thread, YaplObject self, YaplObject environment, YaplObject args) {
                YaplObject message = thread.getFrame().pop();
                YaplObject receiver = thread.getFrame().pop();

                receiver.send(thread, message);
            }
        };

        public static final YaplPrimitive integerAdd = new YaplPrimitive() {
            @Override
            public void evalOnWith(YaplObject thread, YaplObject self, YaplObject environment, YaplObject args) {
                YaplObject rhs = thread.getFrame().pop();
                YaplObject lhs = thread.getFrame().pop();

                YaplObject result = new YaplInteger(lhs.toInt() + rhs.toInt());

                thread.getFrame().push(result);
                thread.getFrame().incrementIP();
            }
        };

        public static final YaplPrimitive respond = new YaplPrimitive() {
            @Override
            public void evalOnWith(YaplObject thread, YaplObject self, YaplObject environment, YaplObject args) {
                YaplObject result = thread.getFrame().pop();
                thread.popFrame();
                thread.getFrame().push(result);
                thread.getFrame().incrementIP();
            }
        };

        public static final YaplPrimitive finish = new YaplPrimitive() {
            @Override
            public void evalOnWith(YaplObject thread, YaplObject self, YaplObject environment, YaplObject args) {
                thread.setFinished();
            }
        };

        public static YaplPrimitive pop = new YaplPrimitive() {
            @Override
            public void evalOnWith(YaplObject thread, YaplObject self, YaplObject environment, YaplObject args) {
                thread.getFrame().pop();
            }
        };

        public static YaplPrimitive pushSelf = new YaplPrimitive() {
            @Override
            public void evalOnWith(YaplObject thread, YaplObject self, YaplObject environment, YaplObject args) {
                thread.getFrame().push(self);
            }
        };

        public static YaplPrimitive push(YaplObject obj) {
            return new YaplPrimitive() {
                @Override
                public void evalOnWith(YaplObject thread, YaplObject self, YaplObject environment, YaplObject args) {
                    thread.getFrame().push(obj);
                    thread.getFrame().incrementIP();
                }
            };
        }

        public static YaplPrimitive load(String name) {
            return new YaplPrimitive() {
                @Override
                public void evalOnWith(YaplObject thread, YaplObject self, YaplObject environment, YaplObject args) {
                    YaplObject obj = thread.getFrame().getEnvironment().resolve(name);
                    thread.getFrame().push(obj);
                    thread.getFrame().incrementIP();
                }
            };
        }

        public static YaplObject define(String name) {
            return new YaplPrimitive() {
                @Override
                public void evalOnWith(YaplObject thread, YaplObject self, YaplObject environment, YaplObject args) {

                }
            };
        }
    }
}
