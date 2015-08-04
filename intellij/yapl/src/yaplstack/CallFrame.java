package yaplstack;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CallFrame {
    public CallFrame outer;
    public final CodeSegment codeSegment;
    public int ip;
    private Object[] stackArray;
    private int stackSize;

    public CallFrame(CodeSegment codeSegment) {
        this.codeSegment = codeSegment;
        stackArray = new Object[1 + 1 + codeSegment.maxStackSize];

        //push(new Environment());
    }

    public CallFrame(CallFrame outer, CodeSegment codeSegment) {
        this.outer = outer;
        this.codeSegment = codeSegment;

        stackArray = new Object[codeSegment.maxStackSize];
    }

    public final void incrementIP() {
        ip++;
    }

    public final void setIP(int index) {
        ip = index;
    }

    public final void push(Object obj) {
        stackArray[stackSize] = obj;
        stackSize++;
    }

    public final Object pop() {
        stackSize--;
        return stackArray[stackSize];
    }

    public final void dup() {
        stackArray[stackSize] = stackArray[stackSize - 1];
        stackSize++;
    }

    public final void dupx1down() {
        Object tmp = stackArray[stackSize - 1];
        stackArray[stackSize] = stackArray[stackSize - 1];
        stackArray[stackSize - 1] = stackArray[stackSize - 2];
        stackArray[stackSize - 2] = tmp;
        stackSize++;
    }

    public final void dupx(int delta) {
        stackArray[stackSize] = stackArray[stackSize - delta - 1];
        stackSize++;
    }

    public final void pushTo(CallFrame callFrame, int pushCount) {
        System.arraycopy(this.stackArray, stackSize - pushCount, callFrame.stackArray, callFrame.stackSize, pushCount);
        callFrame.stackSize += pushCount;
        Arrays.fill(this.stackArray, stackSize - pushCount, stackSize, null);
        stackSize -= pushCount;
    }

    public final Object get(int ordinal) {
        return stackArray[ordinal];
    }

    public final Object peek(int delta) {
        return stackArray[stackSize - 1 - delta];
    }

    public final void set(int ordinal, Object value) {
        stackArray[ordinal] = value;
    }

    public String toString(Thread thread) {
        return Arrays.asList(stackArray).stream().map(x ->
            x instanceof Environment ? ((Environment)x).toString(thread) : x != null ? x.toString() : "null"
        ).collect(Collectors.toList()).toString();
    }
}
