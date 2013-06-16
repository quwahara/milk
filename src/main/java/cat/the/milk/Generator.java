/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.the.milk;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 *
 * @author mitsuaki
 */
public class Generator {
    
    public void recvSrc(Token t) throws IOException {
        Token holder = t.query("expr");
        for (Token s : holder.subs()) {
            recvType(s);
        }
    }
    
    public void recvType(Token t) throws IOException {
        
        String name = t.query("%id").V;
        ClassWriter cw = new ClassWriter(0);
        cw.visit(49,
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
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                    "java/lang/Object",
                    "<init>",
                    "()V");
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        
        
        Token holder = t.query("%bg/expr");
        for (Token s : holder.subs()) {
            if (S.eq("fn", s.G)) {
                recvFun(s, cw);
            }
        }
        
        cw.visitEnd();
        byte[] bs = cw.toByteArray();
        File f = new File(name + ".class");
        FileUtils.writeByteArrayToFile(f, bs);

        
    }
    
    public void recvFun(Token t, ClassWriter cw) {
        
        String fun = t.V;
        String name = t.query("%id").V;
        
        MethodVisitor mv;
        {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,
                    name,
                    "([Ljava/lang/String;)V",
                    null,
                    null);
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
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }
        
        
    }
    
    
}
