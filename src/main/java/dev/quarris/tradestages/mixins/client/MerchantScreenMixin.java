package dev.quarris.tradestages.mixins.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.quarris.tradestages.ModRef;
import dev.quarris.tradestages.ModRoot;
import dev.quarris.tradestages.helper.StageHelper;
import dev.quarris.tradestages.trades.IStagedOffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.MerchantScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends ContainerScreen<MerchantContainer> {

    @Shadow private int shopItem;
    private static final ResourceLocation INVALID_STAGE_ICON = ModRef.res("textures/gui/invalid_stage_icon.png");

    private boolean validShopItem = false;

    public MerchantScreenMixin(MerchantContainer menu, PlayerInventory inv, ITextComponent title) {
        super(menu, inv, title);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/inventory/MerchantScreen;renderButtonArrows(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/item/MerchantOffer;II)V", shift = At.Shift.BY, by = 3), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void renderInvalidStageIcon(MatrixStack matrix, int mouseX, int mouseY, float delta, CallbackInfo ci, MerchantOffers merchantoffers, int i, int j, int entryY, int entryX, int i1, Iterator<?> var11, MerchantOffer merchantOffer) {
        if (StageHelper.canTrade(Minecraft.getInstance().player, merchantOffer)) {
            return;
        }

        Minecraft.getInstance().getTextureManager().bind(INVALID_STAGE_ICON);
        AbstractGui.blit(matrix, entryX + 51, entryY + 6, 7, 7, 0, 0, 7, 7, 16, 16);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void renderInvalidTradeTexts(MatrixStack matrix, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int left = this.getGuiLeft();
        int top = this.getGuiTop();
        ITextComponent invalidStageText = new TranslationTextComponent("merchant.invalid_stage");
        int textWidth = this.font.width(invalidStageText);

        if (!this.validShopItem || this.shopItem < 0 || this.shopItem > this.menu.getOffers().size()) {
            return;
        }

        MerchantOffer offer = this.menu.getOffers().get(this.shopItem);
        if (!(offer instanceof IStagedOffer)) {
            return;
        }

        IStagedOffer stagedOffer = (IStagedOffer) offer;
        if (StageHelper.canTrade(Minecraft.getInstance().player, offer)) {
            return;
        }

        this.font.draw(matrix, invalidStageText, left + 156 - textWidth / 2f, top + 60, 0xff404040);

        // Render hover over tooltip
        if (mouseX < left + 153 - textWidth / 2f || mouseX > left + 159 + textWidth / 2f ||
            mouseY < top + 57 || mouseY > top + 63 + this.font.lineHeight) {
            return;
        }

        List<ITextComponent> tooltip = ModRoot.stagedTrades.getStages(stagedOffer.getTradeLevel(), stagedOffer.getProfessionId()).stream().map(stage -> {
            if (I18n.exists("stage." + stage)) {
                return new TranslationTextComponent("stage." + stage);
            } else {
                return new StringTextComponent(stage);
            }
        }).collect(Collectors.toList());
        this.renderComponentTooltip(matrix, tooltip, mouseX, mouseY);
    }

    @Inject(method = "postButtonClick", at = @At(value = "HEAD"))
    public void setShopItemValid(CallbackInfo ci) {
        this.validShopItem = true;
    }

}
