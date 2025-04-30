package com.cuzz.rookieteam.menu;

import nl.odalitadevelopments.menus.annotations.Menu;
import nl.odalitadevelopments.menus.contents.MenuContents;
import nl.odalitadevelopments.menus.contents.action.MenuProperty;
import nl.odalitadevelopments.menus.menu.providers.PlayerMenuProvider;
import nl.odalitadevelopments.menus.menu.type.MenuType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@Menu(
        title = "添加成员...",
        type = MenuType.ANVIL
)
public class AddMemberMenu implements PlayerMenuProvider {
    private final Consumer<String> inputConsumer;

    public AddMemberMenu(Consumer<String> inputConsumer) {
        this.inputConsumer = inputConsumer;
    }

    @Override
    public void onLoad(@NotNull Player player, @NotNull MenuContents contents) {
        ItemStack itemStack = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GRAY + "请输入玩家名");
        itemStack.setItemMeta(itemMeta);
        contents.setDisplay(0, itemStack);

        contents.events().onInventoryEvent(PrepareAnvilEvent.class, event -> {
            contents.actions().setProperty(MenuProperty.REPAIR_COST, 0);
        });

        contents.events().onInventoryEvent(InventoryClickEvent.class, event -> {
            if (event.getSlot() != 2) return;

            ItemStack currentItem = event.getCurrentItem();
            if (currentItem == null || currentItem.getType().isAir()) return;

            event.getWhoClicked().closeInventory();

            AnvilInventory anvilInventory = (AnvilInventory) event.getInventory();
            String text = anvilInventory.getResult().getItemMeta().getDisplayName();

            this.inputConsumer.accept(text);
        });
    }
}
