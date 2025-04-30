package com.cuzz.rookieteam;

import com.cuzz.bukkitmybatis.BukkitMybatis;
import com.cuzz.bukkitmybatis.utils.MapperRegister;
import com.cuzz.rookieteam.commands.RookieTeamCmd;
import com.cuzz.rookieteam.events.TeamManagement;
import com.cuzz.rookieteam.utils.PopupUtils;
import nl.odalitadevelopments.menus.OdalitaMenus;
import org.apache.ibatis.session.SqlSessionFactory;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.Objects;

public final class RookieTeam extends JavaPlugin {

    public static SqlSessionFactory sqlSessionFactory;
    private static RookieTeam INSTANCE;
    private OdalitaMenus odalitaMenus;
    private BukkitRunnable renderTeamHealth;

    @Override
    public void onEnable() {
        // Plugin startup logic

        odalitaMenus = OdalitaMenus.createInstance(this);

        INSTANCE = this;

        MapperRegister.registerMappers(this);
        sqlSessionFactory= BukkitMybatis.instance.getSqlSessionFactory();

        Bukkit.getPluginManager().registerEvents(new TeamManagement(), this);

        this.getCommand("rteam").setExecutor(new RookieTeamCmd());

        Bukkit.getLogger().info("Staring Team Health Renderer...");
        renderTeamHealth = new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    Bukkit.getOnlinePlayers().forEach(other -> {
                        if(player.equals(other)) return;
                        if(player.getPersistentDataContainer().has(new NamespacedKey("rookieteam","team_id")) && other.getPersistentDataContainer().has(new NamespacedKey("rookieteam","team_id"))){
                            if(player.getPersistentDataContainer().get(new NamespacedKey("rookieteam", "team_id"),PersistentDataType.INTEGER) == 0) return;
                            if(Objects.equals(player.getPersistentDataContainer().get(new NamespacedKey("rookieteam", "team_id"), PersistentDataType.INTEGER), other.getPersistentDataContainer().get(new NamespacedKey("rookieteam", "team_id"), PersistentDataType.INTEGER))){
                                PopupUtils.displayTeammateHealth(player, other.getName());
                            }
                        }
                    });
                });
            }
        };
        renderTeamHealth.runTaskTimer(this, 0L, 5L);
        Bukkit.getLogger().info("Team Health Renderer running");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        MapperRegister.unregisterMapper(this);
        renderTeamHealth.cancel();
    }

    public static RookieTeam getInstance() {
        return INSTANCE;
    }

    public OdalitaMenus getOdalitaMenus() {
        return odalitaMenus;
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
}