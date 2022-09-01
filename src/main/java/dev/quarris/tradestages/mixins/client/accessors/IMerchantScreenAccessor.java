package dev.quarris.tradestages.mixins.client.accessors;

import net.minecraft.client.gui.screen.inventory.MerchantScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MerchantScreen.class)
public interface IMerchantScreenAccessor {

    @Accessor
    int getScrollOff();

}
