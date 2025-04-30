package com.cuzz.rookieteam.commands;

import com.cuzz.rookieteam.RookieTeam;
import com.cuzz.rookieteam.menu.TeamMenu;
import com.cuzz.rookieteam.utils.PopupUtils;
import nl.odalitadevelopments.menus.menu.providers.MenuProvider;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RookieTeamCmd implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args[0].equalsIgnoreCase("menu")) {
            RookieTeam.getInstance().getOdalitaMenus().openMenu(new TeamMenu(), (Player) sender);
        } else if(args[0].equalsIgnoreCase("test")){
            PopupUtils.displayTeammateHealth((Player) sender, sender.getName());
            for(int i = 0; i < 12; i++){
                PopupUtils.displayTeammateHealth((Player) sender, "PKC_" + i);
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1){
            ArrayList<String> tips = new ArrayList<>();
            tips.add("menu");
            tips.add("test");
            return tips;
        }
        return null;
    }
}