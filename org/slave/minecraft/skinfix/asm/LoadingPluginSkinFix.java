package org.slave.minecraft.skinfix.asm;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.Name;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import org.slave.minecraft.skinfix.asm.transformers.TransformerAbstractClientPlayer;

import java.util.Map;

/**
 * Created by Master on 6/30/2026 at 4:04 PM
 *
 * @author Master
 */
@Name("SkinFix")
@MCVersion("1.6.4")
@TransformerExclusions({"org.slave.minecraft.skinfix.asm."})
public class LoadingPluginSkinFix implements IFMLLoadingPlugin {

    @Override
    public String[] getLibraryRequestClass() {
        return null;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[] {
                TransformerAbstractClientPlayer.class.getName()
        };
    }

    @Override
    public String getModContainerClass() {
        return ModContainerSkinFix.class.getName();
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

}
