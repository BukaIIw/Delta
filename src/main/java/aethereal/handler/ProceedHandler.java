package aethereal.handler;

import aethereal.lib.javassist.ASTList;
import aethereal.lib.javassist.Bytecode_2;
import aethereal.lib.javassist.CompileError;
import aethereal.lib.javassist.JvstCodeGen;
import aethereal.lib.javassist.JvstTypeChecker;

public interface ProceedHandler {
    void a(JvstCodeGen jvstCodeGen, Bytecode_2 bytecode_2, ASTList aSTList) throws CompileError;

    void a(JvstTypeChecker jvstTypeChecker, ASTList aSTList) throws CompileError;
}
