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

        public static YaplPrimitive send(String selector, int argumentCount) {
            return new YaplPrimitive() {
                @Override
                public void evalOnWith(YaplObject thread, YaplObject self, YaplObject environment, YaplObject args) {
                    YaplObject[] arguments = new YaplObject[argumentCount];
                    for(int i = arguments.length - 1; i >= 0; i--)
                        arguments[i] = thread.getFrame().pop();
                    YaplObject receiver = thread.getFrame().pop();
                    receiver.send(thread, new YaplSelectorArgsMessage(new YaplString(selector), new YaplArray(arguments)));
                }
            };
        }

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

        public static YaplPrimitive dup = new YaplPrimitive() {
            @Override
            public void evalOnWith(YaplObject thread, YaplObject self, YaplObject environment, YaplObject args) {
                thread.getFrame().dup();
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
                    YaplObject obj = environment.resolve(name);
                    thread.getFrame().push(obj);
                    thread.getFrame().incrementIP();
                }
            };
        }

        public static YaplObject define(String name) {
            return new YaplPrimitive() {
                @Override
                public void evalOnWith(YaplObject thread, YaplObject self, YaplObject environment, YaplObject args) {
                    YaplObject value = thread.getFrame().pop();
                    environment.define(name, value);
                    thread.getFrame().incrementIP();
                }
            };
        }

        public static YaplObject store(String name) {
            return new YaplPrimitive() {
                @Override
                public void evalOnWith(YaplObject thread, YaplObject self, YaplObject environment, YaplObject args) {
                    YaplObject value = thread.getFrame().pop();
                    environment.set(name, value);
                    thread.getFrame().incrementIP();
                }
            };
        }
    }
}
