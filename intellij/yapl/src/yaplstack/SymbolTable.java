package yaplstack;

import java.util.Hashtable;

public class SymbolTable {
    private Hashtable<String, Integer> symbolToCode = new Hashtable<>();
    private Hashtable<Integer, String> codeToSymbol = new Hashtable<>();

    public int getCode(String symbol) {
        Integer code = symbolToCode.get(symbol);
        if(code == null) {
            code = symbolToCode.size();
            symbolToCode.put(symbol, code);
            codeToSymbol.put(code, symbol);
        }
        return code;
    }

    public String getSymbol(int code) {
        return codeToSymbol.get(code);
    }
}
