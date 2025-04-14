package com.cuzz.rookieteam.dao;

import com.cuzz.rookieteam.model.Team;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface DaoManager {


    //获取战队
    public Team getTeamByPlayer(Player player);
    public Team getTeamByPlayer(UUID uuid);


    //获取战队队长
    public Player getLeaderFromTeam(Team team);
    public Player getLeaderFromPlayer(Player player);


    //获取战队成员
    public List<Player> getMembersFromTeam(Team team);
    public List<Player> getMembersFromMember(Player member);


    //创建战队
    public boolean createNewTeam(Team team);

    public boolean createNewTeam(Player leader);

    //解散战队
    public boolean deleteTeam(Team team);

    public boolean deleteTeam(Player leader);


    //更换战队队长
    public boolean changeTeamLeader(Player newLeader,Team team);

    //移除战队成员
    public boolean removeMemberFromTeam(Player member,Team team);

}
