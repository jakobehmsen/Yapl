package yapldynscope;

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

    public Environment getEnvironment() {
        return environment;
    }

    public void apply(Object item, Pair args) {
        if(item instanceof BiConsumer) {
            // Built-in function
            BiConsumer<Evaluator, Pair> itemAsBuiltin = (BiConsumer<Evaluator, Pair>)item;
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
                        // Custom function

                        List<Object> argsAsList = args.stream().collect(Collectors.toList());

                        environment = new Environment(environment);

                        // Bind arguments
                        IntStream.range(0, parametersAsList.size()).forEach(i -> {
                            Object arg = argsAsList.get(i);
                            String name = parametersAsList.get(i).toString();
                            environment.define(name, arg);
                        });

                        evaluate(body, result -> {
                            environment = environment.outer;
                            popFrame(result);
                        });

                        return;
                    }
                }
            }
        }

        popFrame(item);
    }

    public void evaluate(Object item, Consumer<Object> responseHandler) {
        pushFrame(responseHandler);
        evaluate(item);
    }

    public void evaluate(Pair item, BiConsumer<Object, Object> responseHandler) {
        evaluate(item.current, first ->
            evaluate(item.next.current, second ->
                responseHandler.accept(first, second)));
    }

    public void evaluate(Object item) {
        if(item != null) {
            if(item instanceof Pair) {
                Pair itemAsPair = (Pair)item;
                if(itemAsPair.current instanceof String) {
                    String operator = (String)itemAsPair.current;

                    Pair args = itemAsPair.next;
                    Object function = environment.get(operator);
                    apply(function, args);

                    return;
                } else {
                    pushFrame(result -> popFrame(result));

                    evaluateList(itemAsPair, results ->
                        popFrame(results.get(results.size() - 1)));

                    return;
                }
            }
        }

        popFrame(item);
    }

    public void evaluateList(Pair pair, Consumer<Object> collector, Runnable finish) {
        if(pair == null)
            finish.run();
        else {
            evaluate(pair.current, result -> {
                collector.accept(result);
                evaluateList(pair.next, collector, finish);
            });
        }
    }

    private void evaluateList(Pair pair, Consumer<List<Object>> listHandler) {
        ArrayList<Object> listBuilder = new ArrayList<>();

        evaluateList(pair, result -> listBuilder.add(result), () -> listHandler.accept(listBuilder));
    }
}
