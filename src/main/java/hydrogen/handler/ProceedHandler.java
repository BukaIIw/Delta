package hydrogen.handler;

import hydrogen.lib.javassist.ASTList;
import hydrogen.lib.javassist.Bytecode_2;
import hydrogen.lib.javassist.CompileError;
import hydrogen.lib.javassist.JvstCodeGen;
import hydrogen.lib.javassist.JvstTypeChecker;

public interface ProceedHandler {
    void a(JvstCodeGen jvstCodeGen, Bytecode_2 bytecode_2, ASTList aSTList) throws CompileError;

    void a(JvstTypeChecker jvstTypeChecker, ASTList aSTList) throws CompileError;
}
