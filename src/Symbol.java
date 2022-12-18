import java.util.ArrayList;
import java.util.List;

public class Symbol{
    public Type type; // 符号类型
    public String name; // 符号名称
    public Scope scope; // 符号所在作用域
    public List<int[]> usePos; // 该符号使用的位置
    public boolean isNeedReplace; // 该符号是否需要被重命名
    public int row; // 声明该符号时符号所处的行号
    public int col; // 声明该符号时符号所处的列号


    public Symbol(Type type, String name, Scope scope){
        this.type = type;
        this.name = name;
        this.scope = scope;
        this.usePos = new ArrayList<>();
        this.isNeedReplace = false;
    }

    public void addUsePos(int row, int col){
        if (row == this.row && col == this.col) return;
        usePos.add(new int[]{row, col});
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<Symbol>: ").append("name: ").append(name).append(" | type: [")
                .append(type.toString()).append("] | position: (")
                .append(row).append(", ").append(col).append(") | usePos:");
        if (this.usePos.size() != 0){
            for (int[] pos : this.usePos){
                sb.append(" (").append(pos[0]).append(", ").append(pos[1]).append(")");
            }
        } else {
            sb.append(" no use");
        }
        if (isNeedReplace){
            sb.append(" need to replace.");
        }
        return sb.toString();
    }
}
