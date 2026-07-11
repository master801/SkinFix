package org.slave.minecraft.skinfix.asm.transformers;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Master on 6/30/2026 at 4:09 PM
 *
 * @author Master
 */
public final class TransformerAbstractClientPlayer implements IClassTransformer {

    @Override
    public byte[] transform(final String untransformedName, final String transformedName, final byte[] bytes) {
        if (transformedName.equals("net.minecraft.client.entity.AbstractClientPlayer")) {
//            System.out.println("SkinFix: Found class AbstractClientPlayer!");//TODO Debugging
            ClassReader classReader = new ClassReader(bytes);
            ClassNode classNode = new ClassNode(Opcodes.ASM4);
            classReader.accept(classNode, 0);

            boolean foundSkin = false, foundCape = false;
            for(MethodNode methodNode : classNode.methods) {
                if (methodNode.access == (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC) && methodNode.desc.equals("(Ljava/lang/String;)Ljava/lang/String;")) {
                    if (methodNode.name.equals("d") || methodNode.name.equals("func_110300_d") || methodNode.name.equals("getSkinUrl")) {
//                        System.out.println("SkinFix: Found getSkinUrl!");//TODO Debugging
                        foundSkin = true;

                        AbstractInsnNode injectionNode = null;
                        Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                        while(iterator.hasNext()) {
                            AbstractInsnNode next = iterator.next();
                            if (next instanceof LdcInsnNode) {
                                LdcInsnNode ldcInsnNode = (LdcInsnNode)next;
                                if (ldcInsnNode.cst != null && ldcInsnNode.cst.equals("http://skins.minecraft.net/MinecraftSkins/%s.png")) {
                                    injectionNode = next;
                                    break;
                                }
                            }
                        }

                        if (injectionNode != null) {
                            InsnList newInstructions = new InsnList();
                            newInstructions.add(
                                    new VarInsnNode(Opcodes.ALOAD, 0)
                            );
                            newInstructions.add(
                                    new MethodInsnNode(
                                            Opcodes.INVOKESTATIC,
                                            "org/slave/minecraft/skinfix/hooks/HookAbstractClientPlayer",
                                            "getSkinURL",
                                            "(Ljava/lang/String;)Ljava/lang/String;"
                                    )
                            );

                            //Insert new instructions first
                            methodNode.instructions.insertBefore(injectionNode, newInstructions);

                            //Remove all old instructions
                            List<AbstractInsnNode> toRemove = new ArrayList<AbstractInsnNode>();
                            while(injectionNode.getOpcode() != Opcodes.ARETURN) {
                                toRemove.add(injectionNode);
                                injectionNode = injectionNode.getNext();
                            }
                            for(AbstractInsnNode insnNode : toRemove) methodNode.instructions.remove(insnNode);
                        }
                    } else if (methodNode.name.equals("e") || methodNode.name.equals("func_110308_e") || methodNode.name.equals("getCapeUrl")) {
//                        System.out.println("SkinFix: Found getCapeUrl!");//TODO Debugging
                        foundCape = true;

                        AbstractInsnNode injectionNode = null;
                        Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                        while(iterator.hasNext()) {
                            AbstractInsnNode next = iterator.next();
                            if (next instanceof LdcInsnNode) {
                                LdcInsnNode ldcInsnNode = (LdcInsnNode)next;
                                if (ldcInsnNode.cst != null && ldcInsnNode.cst.equals("http://skins.minecraft.net/MinecraftCloaks/%s.png")) {
                                    injectionNode = next;
                                    break;
                                }
                            }
                        }

                        if (injectionNode != null) {
                            InsnList newInstructions = new InsnList();
                            newInstructions.add(
                                    new VarInsnNode(Opcodes.ALOAD, 0)
                            );
                            newInstructions.add(
                                    new MethodInsnNode(
                                            Opcodes.INVOKESTATIC,
                                            "org/slave/minecraft/skinfix/hooks/HookAbstractClientPlayer",
                                            "getCapeURL",
                                            "(Ljava/lang/String;)Ljava/lang/String;"
                                    )
                            );

                            //Insert new instructions first
                            methodNode.instructions.insertBefore(injectionNode, newInstructions);

                            //Remove all old instructions
                            List<AbstractInsnNode> toRemove = new ArrayList<AbstractInsnNode>();
                            while(injectionNode.getOpcode() != Opcodes.ARETURN) {
                                toRemove.add(injectionNode);
                                injectionNode = injectionNode.getNext();
                            }
                            for(AbstractInsnNode insnNode : toRemove) methodNode.instructions.remove(insnNode);
                        }
                    }
                }
                if (foundSkin && foundCape) break;
            }

            ClassWriter classWriter = new ClassWriter(0);
            classNode.accept(classWriter);
            byte[] classData = classWriter.toByteArray();

            //TODO This is for debugging. Comment this out for release
//            try {
//                File fileClassDump = new File(".", "AbstractClientPlayer.class");
//                FileOutputStream fos = new FileOutputStream(fileClassDump);
//                fos.write(classData);
//                fos.close();
//            } catch(IOException e) {
//                System.out.println(e.toString());
//            }

            return classData;
        }
        return bytes;
    }

}
