import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scope{
    Scope enclosingScope;
    Map<String, Symbol> symbols;
    List<Scope> childScope;

    public Scope(Scope enclosingScope){
        symbols = new HashMap<>();
        childScope = new ArrayList<>();
        this.enclosingScope = enclosingScope;
    }

    public void define(Symbol s) {
        symbols.put(s.name, s);
    }

    public Symbol resolve(String name) {
        Symbol symbol = symbols.get(name);
        if (symbol == null && enclosingScope != null){
            symbol = enclosingScope.resolve(name);
        }
        return symbol;
    }

    public Symbol resolveCurrentScope(String name){
        return symbols.get(name);
    }

    public void addChildScope(Scope s) {
        childScope.add(s);
    }

}
