// Generated from ./src/SysYParser.g4 by ANTLR 4.9.1
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SysYParser}.
 */
public interface SysYParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SysYParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(SysYParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link SysYParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(SysYParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link SysYParser#compUnit}.
	 * @param ctx the parse tree
	 */
	void enterCompUnit(SysYParser.CompUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link SysYParser#compUnit}.
	 * @param ctx the parse tree
	 */
	void exitCompUnit(SysYParser.CompUnitContext ctx);
	/**
	 * Enter a parse tree produced by {@link SysYParser#funcDef}.
	 * @param ctx the parse tree
	 */
	void enterFuncDef(SysYParser.FuncDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link SysYParser#funcDef}.
	 * @param ctx the parse tree
	 */
	void exitFuncDef(SysYParser.FuncDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link SysYParser#funcType}.
	 * @param ctx the parse tree
	 */
	void enterFuncType(SysYParser.FuncTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SysYParser#funcType}.
	 * @param ctx the parse tree
	 */
	void exitFuncType(SysYParser.FuncTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SysYParser#funcFParams}.
	 * @param ctx the parse tree
	 */
	void enterFuncFParams(SysYParser.FuncFParamsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SysYParser#funcFParams}.
	 * @param ctx the parse tree
	 */
	void exitFuncFParams(SysYParser.FuncFParamsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SysYParser#funcFParam}.
	 * @param ctx the parse tree
	 */
	void enterFuncFParam(SysYParser.FuncFParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link SysYParser#funcFParam}.
	 * @param ctx the parse tree
	 */
	void exitFuncFParam(SysYParser.FuncFParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link SysYParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(SysYParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link SysYParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(SysYParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link SysYParser#blockItem}.
	 * @param ctx the parse tree
	 */
	void enterBlockItem(SysYParser.BlockItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link SysYParser#blockItem}.
	 * @param ctx the parse tree
	 */
	void exitBlockItem(SysYParser.BlockItemContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ASSIGNMENT}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterASSIGNMENT(SysYParser.ASSIGNMENTContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ASSIGNMENT}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitASSIGNMENT(SysYParser.ASSIGNMENTContext ctx);
	/**
	 * Enter a parse tree produced by the {@code EXP}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterEXP(SysYParser.EXPContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EXP}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitEXP(SysYParser.EXPContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BLOCK}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterBLOCK(SysYParser.BLOCKContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BLOCK}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitBLOCK(SysYParser.BLOCKContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IF_ELSE}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterIF_ELSE(SysYParser.IF_ELSEContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IF_ELSE}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitIF_ELSE(SysYParser.IF_ELSEContext ctx);
	/**
	 * Enter a parse tree produced by the {@code WHILE_STMT}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterWHILE_STMT(SysYParser.WHILE_STMTContext ctx);
	/**
	 * Exit a parse tree produced by the {@code WHILE_STMT}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitWHILE_STMT(SysYParser.WHILE_STMTContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BREAK_STMT}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterBREAK_STMT(SysYParser.BREAK_STMTContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BREAK_STMT}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitBREAK_STMT(SysYParser.BREAK_STMTContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CONTINUE_STMT}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterCONTINUE_STMT(SysYParser.CONTINUE_STMTContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CONTINUE_STMT}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitCONTINUE_STMT(SysYParser.CONTINUE_STMTContext ctx);
	/**
	 * Enter a parse tree produced by the {@code RETURN_STMT}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterRETURN_STMT(SysYParser.RETURN_STMTContext ctx);
	/**
	 * Exit a parse tree produced by the {@code RETURN_STMT}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitRETURN_STMT(SysYParser.RETURN_STMTContext ctx);
	/**
	 * Enter a parse tree produced by {@link SysYParser#decl}.
	 * @param ctx the parse tree
	 */
	void enterDecl(SysYParser.DeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link SysYParser#decl}.
	 * @param ctx the parse tree
	 */
	void exitDecl(SysYParser.DeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link SysYParser#constDecl}.
	 * @param ctx the parse tree
	 */
	void enterConstDecl(SysYParser.ConstDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link SysYParser#constDecl}.
	 * @param ctx the parse tree
	 */
	void exitConstDecl(SysYParser.ConstDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link SysYParser#constDef}.
	 * @param ctx the parse tree
	 */
	void enterConstDef(SysYParser.ConstDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link SysYParser#constDef}.
	 * @param ctx the parse tree
	 */
	void exitConstDef(SysYParser.ConstDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link SysYParser#constInitVal}.
	 * @param ctx the parse tree
	 */
	void enterConstInitVal(SysYParser.ConstInitValContext ctx);
	/**
	 * Exit a parse tree produced by {@link SysYParser#constInitVal}.
	 * @param ctx the parse tree
	 */
	void exitConstInitVal(SysYParser.ConstInitValContext ctx);
	/**
	 * Enter a parse tree produced by {@link SysYParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void enterVarDecl(SysYParser.VarDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link SysYParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void exitVarDecl(SysYParser.VarDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link SysYParser#varDef}.
	 * @param ctx the parse tree
	 */
	void enterVarDef(SysYParser.VarDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link SysYParser#varDef}.
	 * @param ctx the parse tree
	 */
	void exitVarDef(SysYParser.VarDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link SysYParser#initVal}.
	 * @param ctx the parse tree
	 */
	void enterInitVal(SysYParser.InitValContext ctx);
	/**
	 * Exit a parse tree produced by {@link SysYParser#initVal}.
	 * @param ctx the parse tree
	 */
	void exitInitVal(SysYParser.InitValContext ctx);
	/**
	 * Enter a parse tree produced by {@link SysYParser#bType}.
	 * @param ctx the parse tree
	 */
	void enterBType(SysYParser.BTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SysYParser#bType}.
	 * @param ctx the parse tree
	 */
	void exitBType(SysYParser.BTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code EXPR}
	 * labeled alternative in {@link SysYParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterEXPR(SysYParser.EXPRContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EXPR}
	 * labeled alternative in {@link SysYParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitEXPR(SysYParser.EXPRContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UN_OP}
	 * labeled alternative in {@link SysYParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterUN_OP(SysYParser.UN_OPContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UN_OP}
	 * labeled alternative in {@link SysYParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitUN_OP(SysYParser.UN_OPContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FUNC_CALL}
	 * labeled alternative in {@link SysYParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterFUNC_CALL(SysYParser.FUNC_CALLContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FUNC_CALL}
	 * labeled alternative in {@link SysYParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitFUNC_CALL(SysYParser.FUNC_CALLContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NUM}
	 * labeled alternative in {@link SysYParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterNUM(SysYParser.NUMContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NUM}
	 * labeled alternative in {@link SysYParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitNUM(SysYParser.NUMContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PM_OP}
	 * labeled alternative in {@link SysYParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterPM_OP(SysYParser.PM_OPContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PM_OP}
	 * labeled alternative in {@link SysYParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitPM_OP(SysYParser.PM_OPContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LV}
	 * labeled alternative in {@link SysYParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterLV(SysYParser.LVContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LV}
	 * labeled alternative in {@link SysYParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitLV(SysYParser.LVContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MDM_OP}
	 * labeled alternative in {@link SysYParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterMDM_OP(SysYParser.MDM_OPContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MDM_OP}
	 * labeled alternative in {@link SysYParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitMDM_OP(SysYParser.MDM_OPContext ctx);
	/**
	 * Enter a parse tree produced by the {@code OR}
	 * labeled alternative in {@link SysYParser#cond}.
	 * @param ctx the parse tree
	 */
	void enterOR(SysYParser.ORContext ctx);
	/**
	 * Exit a parse tree produced by the {@code OR}
	 * labeled alternative in {@link SysYParser#cond}.
	 * @param ctx the parse tree
	 */
	void exitOR(SysYParser.ORContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AND}
	 * labeled alternative in {@link SysYParser#cond}.
	 * @param ctx the parse tree
	 */
	void enterAND(SysYParser.ANDContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AND}
	 * labeled alternative in {@link SysYParser#cond}.
	 * @param ctx the parse tree
	 */
	void exitAND(SysYParser.ANDContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CONEXP}
	 * labeled alternative in {@link SysYParser#cond}.
	 * @param ctx the parse tree
	 */
	void enterCONEXP(SysYParser.CONEXPContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CONEXP}
	 * labeled alternative in {@link SysYParser#cond}.
	 * @param ctx the parse tree
	 */
	void exitCONEXP(SysYParser.CONEXPContext ctx);
	/**
	 * Enter a parse tree produced by the {@code EN}
	 * labeled alternative in {@link SysYParser#cond}.
	 * @param ctx the parse tree
	 */
	void enterEN(SysYParser.ENContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EN}
	 * labeled alternative in {@link SysYParser#cond}.
	 * @param ctx the parse tree
	 */
	void exitEN(SysYParser.ENContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LG}
	 * labeled alternative in {@link SysYParser#cond}.
	 * @param ctx the parse tree
	 */
	void enterLG(SysYParser.LGContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LG}
	 * labeled alternative in {@link SysYParser#cond}.
	 * @param ctx the parse tree
	 */
	void exitLG(SysYParser.LGContext ctx);
	/**
	 * Enter a parse tree produced by {@link SysYParser#lVal}.
	 * @param ctx the parse tree
	 */
	void enterLVal(SysYParser.LValContext ctx);
	/**
	 * Exit a parse tree produced by {@link SysYParser#lVal}.
	 * @param ctx the parse tree
	 */
	void exitLVal(SysYParser.LValContext ctx);
	/**
	 * Enter a parse tree produced by {@link SysYParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(SysYParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link SysYParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(SysYParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link SysYParser#unaryOp}.
	 * @param ctx the parse tree
	 */
	void enterUnaryOp(SysYParser.UnaryOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link SysYParser#unaryOp}.
	 * @param ctx the parse tree
	 */
	void exitUnaryOp(SysYParser.UnaryOpContext ctx);
	/**
	 * Enter a parse tree produced by {@link SysYParser#funcRParams}.
	 * @param ctx the parse tree
	 */
	void enterFuncRParams(SysYParser.FuncRParamsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SysYParser#funcRParams}.
	 * @param ctx the parse tree
	 */
	void exitFuncRParams(SysYParser.FuncRParamsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SysYParser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(SysYParser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link SysYParser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(SysYParser.ParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link SysYParser#constExp}.
	 * @param ctx the parse tree
	 */
	void enterConstExp(SysYParser.ConstExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link SysYParser#constExp}.
	 * @param ctx the parse tree
	 */
	void exitConstExp(SysYParser.ConstExpContext ctx);
}