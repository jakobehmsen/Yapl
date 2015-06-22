package yapl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Evaluator {
    private Environment environment;
    private Frame frame;

    public Evaluator(Environment environment, Frame frame) {
        this.environment = environment;
        this.frame = frame;
    }

    public void pushFrame(Consumer<Object> responseHandler) {
        frame = new Frame(
            frame,
            responseHandler
        );
    }

    public void popFrame(Object result) {
        Frame currentFrame = frame;
        frame = frame.outer;
        currentFrame.responseHandler.accept(result);
    }

    public void apply(Object item, Pair args) {
        if(item instanceof BiConsumer) {
            // Built-in function
            BiConsumer<Evaluator, Pair> itemAsBuiltin = (BiConsumer<Evaluator, Pair>)item;
            pushFrame(result -> {
                popFrame(result);
            });
            itemAsBuiltin.accept(this, args);
            return;
        } else if(item instanceof Pair) {
            List<Object> itemAsList = ((Pair)item).stream().collect(Collectors.toList());
            if(itemAsList.size() == 2) {
                Object parameters = itemAsList.get(0);
                Object body = itemAsList.get(1);

                if(parameters instanceof Pair) {
                    List<Object> parametersAsList = ((Pair)parameters).stream().collect(Collectors.toList());

                    if(parametersAsList.stream().allMatch(x -> x instanceof String)) {
                        List<Object> argsAsList = args.stream().collect(Collectors.toList());

                        // Custom function
                        pushFrame(result -> {
                            environment = environment.outer;
                            popFrame(result);
                        });
                        environment = new Environment(environment);

                        IntStream.range(0, parametersAsList.size()).forEach(i -> {
                            Object arg = argsAsList.get(i);
                            String name = parametersAsList.get(i).toString();
                            environment.declare(name);
                            environment.set(name, arg);
                        });

                        // Bind arguments
                        evaluate(item);
                        return;
                    }
                }
            }
        }

        popFrame(item);
    }

    public void evaluate(Object item) {
        if(item != null) {
            if(item instanceof Pair) {
                Pair itemAsPair = (Pair)item;
                if(itemAsPair.current instanceof String) {
                    String operator = (String)itemAsPair.current;

                    evaluateList(itemAsPair.next, new ArrayList<>(), args -> {
                        Pair argsAsPair = Pair.list(args);
                        Object function = environment.get(operator);

                        Object applyFunction = environment.get("apply");
                        apply(applyFunction, Pair.list(function, argsAsPair));
                    });

                    return;
                } else {
                    pushFrame(result -> {
                        popFrame(result);
                    });

                    evaluateList(itemAsPair, new ArrayList<>(), results -> {
                        popFrame(results.get(results.size() - 1));
                    });

                    return;
                }
            }
        }

        popFrame(item);
    }

    private void evaluateList(Pair pair, List<Object> listBuilder, Consumer<List<Object>> listHandler) {
        if(pair == null)
            listHandler.accept(listBuilder);
        else {
            pushFrame(result -> {
                listBuilder.add(result);
                evaluateList(pair.next, listBuilder, listHandler);
            });
            evaluate(pair.current);
        }
    }
}
