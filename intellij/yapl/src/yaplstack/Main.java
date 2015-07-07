package yaplstack;

public class Main {
    public static void main(String[] args) {
        Object result = new Thread(new Frame(new Instruction[] {
            Instruction.Factory.push(5),
            Instruction.Factory.store("x"),
            Instruction.Factory.push(7),
            Instruction.Factory.load("x"),
            Instruction.Factory.addi,
            Instruction.Factory.finish
        })).evalAll().frame.pop();

        System.out.println(result);
    }
}
