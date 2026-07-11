package org.slave.minecraft.skinfix.asm;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;

/**
 * Created by Master on 6/30/2026 at 4:21 PM
 *
 * @author Master
 */
public final class ModContainerSkinFix extends DummyModContainer {

    public ModContainerSkinFix() {
        super(new ModMetadata());
        super.getMetadata().modId = super.getMetadata().name = "SkinFix";
        super.getMetadata().version = "v1.0.0";
        super.getMetadata().description = "Fixes broken skins and capes due to server migration";
        super.getMetadata().authorList.add("Master801");
    }

    @Override
    public boolean registerBus(final EventBus bus, final LoadController controller) {
        return true;
    }

}
