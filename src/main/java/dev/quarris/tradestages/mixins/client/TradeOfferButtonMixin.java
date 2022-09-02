package dev.quarris.tradestages.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.quarris.tradestages.ModRoot;
import dev.quarris.tradestages.helper.StageHelper;
import dev.quarris.tradestages.mixins.client.accessors.IMerchantScreenAccessor;
import dev.quarris.tradestages.trades.IStagedOffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(MerchantScreen.TradeOfferButton.class)
public abstract class TradeOfferButtonMixin extends Button {

    @Shadow
    @Final
    private MerchantScreen this$0;

    @Shadow
    @Final
    private int index;

    public TradeOfferButtonMixin(int p_93721_, int p_93722_, int p_93723_, int p_93724_, Component p_93725_, OnPress p_93726_) {
        super(p_93721_, p_93722_, p_93723_, p_93724_, p_93725_, p_93726_);
    }

    @Inject(method = "renderToolTip", at = @At("TAIL"))
    public void renderInvalidStageTooltip(PoseStack matrix, int mouseX, int mouseY, CallbackInfo ci) {
        IMerchantScreenAccessor accessor = (IMerchantScreenAccessor) this$0;
        MerchantOffer offer = this.this$0.getMenu().getOffers().get(this.index + accessor.getScrollOff());

        if (!(offer instanceof IStagedOffer stagedOffer)) {
            return;
        }

        if (StageHelper.canTrade(Minecraft.getInstance().player, offer)) {
            return;
        }

        if (this.isHovered && this.this$0.getMenu().getOffers().size() > this.index + accessor.getScrollOff()) {
            int mouseOverX = mouseX - this.x;
            int mouseOverY = mouseY - this.y;

            if (mouseOverX > 53 && mouseOverX < 66 && mouseOverY > 2 && mouseOverY < 14) {
                List<String> stages = ModRoot.stagedTrades.getStages(stagedOffer.getTradeLevel(), stagedOffer.getProfessionId());
                List<Component> stagesTooltip = new ArrayList<>();
                stagesTooltip.add(new TranslatableComponent("merchant.invalid_stage"));
                stages.forEach(stage -> {
                    if (I18n.exists("stage." + stage)) {
                        stagesTooltip.add(new TranslatableComponent("stage." + stage));
                    } else {
                        stagesTooltip.add(new TextComponent(stage));
                    }
                });
                this$0.renderTooltip(matrix, stagesTooltip, Optional.empty(), mouseX, mouseY);
            }
        }
    }
}
