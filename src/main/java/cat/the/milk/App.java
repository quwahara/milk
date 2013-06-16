package cat.the.milk;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException
    {
        //  http://asm.ow2.org/asm40/javadoc/user/index.html
        ClassWriter cw;
//        MethodVisitor mv;
//        cw = new ClassWriter(0);
////        cw = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
//        cw.visit(Opcodes.V1_2, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, "xxx/X", null, "java/lang/Object", null);
//        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
//        
//        mv.visitCode();
//        mv.visitVarInsn(Opcodes.ALOAD, 0);
//        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object",
//            "<init>", "()V");
//        mv.visitInsn(Opcodes.RETURN);
//        mv.visitMaxs(1, 1);
//        mv.visitEnd();
        
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw = new ClassWriter(0);
        cw.visit(49,
                Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
                "Hello",
                null,
                "java/lang/Object",
                null);

        cw.visitSource("Hello.java", null);

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
        {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,
                    "main",
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
        
        
        cw.visitEnd();
        byte[] bs = cw.toByteArray();
//        File f = new File("../JavaApplication2/mylib/Hello.class");
        File f = new File("Hello.class");
        FileUtils.writeByteArrayToFile(f, bs);
        
//        Milk m;
//        try {
//            m = new Milk();
//            m.eval("");
//        } catch (Exception ex) {
//            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}
