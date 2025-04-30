package com.cuzz.rookieteam.utils;

import kr.toxicity.hud.api.BetterHudAPI;
import kr.toxicity.hud.api.bukkit.event.CustomPopupEvent;
import kr.toxicity.hud.api.bukkit.update.BukkitEventUpdateEvent;
import kr.toxicity.hud.api.player.HudPlayer;
import kr.toxicity.hud.api.popup.Popup;
import kr.toxicity.hud.api.popup.PopupUpdater;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PopupUtils {
    public static void displayTeammateHealth(Player player, String teammateName){
        CustomPopupEvent dialogEvent = new CustomPopupEvent(player, "teammate_health" + player.getName() + "_" + teammateName);
        Player teammate = Bukkit.getPlayer(teammateName);
        if(teammate != null){
            dialogEvent.getVariables().put("teammate_health", String.format("%.1f", teammate.getHealth()));
        } else {
            return;
        }
        dialogEvent.getVariables().put("teammate_name", teammateName);
        Popup dialogPopup = BetterHudAPI.inst().getPopupManager().getPopup("team_health");
        BukkitEventUpdateEvent updateEvent = new BukkitEventUpdateEvent(dialogEvent, "teammate_health" + player.getName() + "_" + teammateName);

        HudPlayer hudPlayer = BetterHudAPI.inst().getPlayerManager().getHudPlayer(player.getUniqueId());
        if (hudPlayer != null) {
            if (dialogPopup != null) {
                dialogPopup.hide(hudPlayer);
                dialogPopup.show(updateEvent, hudPlayer);
            } else {
                System.out.println("Popup[dialogPopup] is null");
            }
        }
    }
}
