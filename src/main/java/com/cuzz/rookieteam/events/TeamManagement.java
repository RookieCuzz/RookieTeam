package com.cuzz.rookieteam.events;

import com.cuzz.rookieteam.RookieTeam;
import com.cuzz.rookieteam.model.Team;
import com.cuzz.rookieteam.model.TeamPlayer;
import com.cuzz.rookieteam.utils.TeamUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TeamManagement implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {
        if(event.isCancelled()) {
            return;
        }

        if (event.getPlayer().getPersistentDataContainer().get(new NamespacedKey("rookieteam", "chat"), PersistentDataType.BOOLEAN) == null) {
            event.getPlayer().getPersistentDataContainer().set(new NamespacedKey("rookieteam", "chat"), PersistentDataType.BOOLEAN, false);
        }

        if (event.getPlayer().getPersistentDataContainer().get(new NamespacedKey("rookieteam", "chat"), PersistentDataType.BOOLEAN).equals(true)) {

            Team team = TeamUtils.getPlayerTeam(event.getPlayer());
            if(team == null) return;

            List<TeamPlayer> teamMembers = TeamUtils.getTeamMembers(team);
            for (TeamPlayer teamMember : teamMembers) {
                Player player = Bukkit.getPlayer(UUID.fromString(teamMember.getPlayerId()));
                if (player != null) {
                    if(team.getLeaderId().equals(event.getPlayer().getUniqueId().toString())) {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "[组队] " + ChatColor.RED + event.getPlayer().getName() + ChatColor.WHITE + ": " + event.getMessage());
                    } else {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "[组队] " + ChatColor.WHITE + event.getPlayer().getName() + ": " + event.getMessage());
                    }
                }
            }

            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.isCancelled()) {
            return;
        }

        if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof Player victim) {
            Team attackerTeam = TeamUtils.getPlayerTeam(attacker);
            Team victimTeam = TeamUtils.getPlayerTeam(victim);
            if(attackerTeam == null || victimTeam == null) return;
            if (Objects.equals(attackerTeam.getTeamId(), victimTeam.getTeamId())) {
                if(attacker.getPersistentDataContainer().has(new NamespacedKey("rookieteam", "teamdamage"), PersistentDataType.BOOLEAN))
                    if(attacker.getPersistentDataContainer().get(new NamespacedKey("rookieteam", "teamdamage"), PersistentDataType.BOOLEAN).equals(false)) {
                        event.setCancelled(true);
                    }
            }
        }
    }
}
