/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.the.milk;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 *
 * @author mitsuaki
 */
public class Generator {
    
    public byte[] Bin;
    public boolean saveClassOn = true;
   
    public Generator init() {
        return this;
    }
    
    public Generator setSaveClassOn(boolean v) {
        saveClassOn = v;
        return this;
    }
    
    public Generator recvSrc(Token t) throws IOException {
        Bin = null;
        Token holder = t.query("expr");
        for (Token s : holder.subs()) {
            recvType(s);
        }
        return this;
    }
    
    public void recvType(Token t) throws IOException {
        
        String name = t.query("%id").V;
        int flag = ClassWriter.COMPUTE_MAXS;
        ClassWriter cw = new ClassWriter(flag);
        cw.visit(Opcodes.V1_5,
                Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
                name,
                null,
                "java/lang/Object",
                null
                );

        cw.visitSource(name + ".java", null);
                
        MethodVisitor mv;
        {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                    "java/lang/Object",
                    "<init>",
                    "()V");
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        
        Token holder = t.query("%bg/expr");
        for (Token s : holder.subs()) {
            if (S.eq("fn", s.G)) {
                recvFun(s, cw);
            }
        }
        
        cw.visitEnd();
        
        Bin = cw.toByteArray();
        
        if (saveClassOn) {
            File f = new File(name + ".class");
            FileUtils.writeByteArrayToFile(f, Bin);
        }
    }
    
    public void recvFun(Token t, ClassVisitor cw) {
        
        String fun = t.V;
        String name = t.query("%id").V;
        
        MethodVisitor mv;
        {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,
                    name,
                    "([Ljava/lang/String;)V",
                    null,
                    null);
            mv.visitCode();
            mv.visitFieldInsn(Opcodes.GETSTATIC,
                    "java/lang/System",
                    "out",
                    "Ljava/io/PrintStream;");
            mv.visitLdcInsn("hello");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                    "java/io/PrintStream",
                    "println",
                    "(Ljava/lang/String;)V");
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        
        
    }

    @Override
    public String toString() {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        new ClassReader(Bin).accept(new TraceClassVisitor(pw), 0);
        return sw.toString();
    }
    
    public Generator verify() {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        CheckClassAdapter.verify(new ClassReader(Bin), false, pw);
        return this;
    }
    
}
