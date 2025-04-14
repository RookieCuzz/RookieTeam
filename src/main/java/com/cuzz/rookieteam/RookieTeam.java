package com.cuzz.rookieteam;

import com.cuzz.bukkitmybatis.BukkitMybatis;
import com.cuzz.bukkitmybatis.utils.MapperRegister;
import org.apache.ibatis.session.SqlSessionFactory;
import org.bukkit.plugin.java.JavaPlugin;

public final class RookieTeam extends JavaPlugin {

    public static SqlSessionFactory sqlSessionFactory;

    @Override
    public void onEnable() {
        // Plugin startup logic

        MapperRegister.registerMappers(this);
        sqlSessionFactory= BukkitMybatis.instance.getSqlSessionFactory();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        MapperRegister.unregisterMapper(this);
    }
}
