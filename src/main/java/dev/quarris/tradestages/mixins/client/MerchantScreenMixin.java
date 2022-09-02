package dev.quarris.tradestages.mixins.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.quarris.tradestages.ModRef;
import dev.quarris.tradestages.ModRoot;
import dev.quarris.tradestages.helper.StageHelper;
import dev.quarris.tradestages.trades.IStagedOffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends AbstractContainerScreen<MerchantMenu> {

    @Shadow private int shopItem;
    private static final ResourceLocation INVALID_STAGE_ICON = ModRef.res("textures/gui/invalid_stage_icon.png");

    private boolean validShopItem = false;

    public MerchantScreenMixin(MerchantMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/MerchantScreen;renderButtonArrows(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/trading/MerchantOffer;II)V", shift = At.Shift.BY, by = 3), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void renderInvalidStageIcon(PoseStack matrix, int mouseX, int mouseY, float delta, CallbackInfo ci, MerchantOffers merchantoffers, int i, int j, int entryY, int entryX, int i1, Iterator<?> var11, MerchantOffer merchantOffer) {
        if (StageHelper.canTrade(Minecraft.getInstance().player, merchantOffer)) {
            return;
        }

        RenderSystem.setShaderTexture(0, INVALID_STAGE_ICON);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        GuiComponent.blit(matrix, entryX + 51, entryY + 6, 7, 7, 0, 0, 7, 7, 16, 16);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void renderInvalidTradeTexts(PoseStack matrix, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int left = this.getGuiLeft();
        int top = this.getGuiTop();
        Component invalidStageText = new TranslatableComponent("merchant.invalid_stage");
        int textWidth = this.font.width(invalidStageText);

        if (!this.validShopItem || this.shopItem < 0 || this.shopItem > this.menu.getOffers().size()) {
            return;
        }

        MerchantOffer offer = this.menu.getOffers().get(this.shopItem);
        if (!(offer instanceof IStagedOffer stagedOffer)) {
            return;
        }

        if (StageHelper.canTrade(Minecraft.getInstance().player, offer)) {
            return;
        }

        this.font.draw(matrix, invalidStageText, left + 156 - textWidth / 2f, top + 60, 0xff404040);

        // Render hover over tooltip
        if (mouseX < left + 153 - textWidth / 2f || mouseX > left + 159 + textWidth / 2f ||
            mouseY < top + 57 || mouseY > top + 63 + this.font.lineHeight) {
            return;
        }

        List<Component> tooltip = ModRoot.stagedTrades.getStages(stagedOffer.getTradeLevel(), stagedOffer.getProfessionId()).stream().map(stage -> {
            if (I18n.exists("stage." + stage)) {
                return new TranslatableComponent("stage." + stage);
            } else {
                return new TextComponent(stage);
            }
        }).collect(Collectors.toList());
        this.renderTooltip(matrix, tooltip, Optional.empty(), mouseX, mouseY);
    }

    @Inject(method = "postButtonClick", at = @At(value = "HEAD"))
    public void setShopItemValid(CallbackInfo ci) {
        this.validShopItem = true;
    }

}
