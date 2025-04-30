package com.cuzz.rookieteam.utils;

import com.cuzz.bukkitmybatis.BukkitMybatis;
import com.cuzz.rookieteam.mapper.TeamMapper;
import com.cuzz.rookieteam.mapper.TeamPlayerMapper;
import com.cuzz.rookieteam.model.Team;
import com.cuzz.rookieteam.model.TeamPlayer;
import com.cuzz.rookieteam.model.TeamPlayerExample;
import org.apache.ibatis.session.SqlSession;
import org.bukkit.entity.Player;

import java.util.List;

public class TeamUtils {
    public static Team getPlayerTeam(Player player) {
        try (SqlSession sqlSession = BukkitMybatis.instance.getSqlSessionFactory().openSession()) {
            TeamMapper teamMapper = sqlSession.getMapper(TeamMapper.class);
            TeamPlayerMapper teamPlayerMapper = sqlSession.getMapper(TeamPlayerMapper.class);
            TeamPlayer teamPlayer = teamPlayerMapper.selectByPrimaryKey(player.getUniqueId().toString());
            if (teamPlayer != null) {
                return teamMapper.selectByPrimaryKey(teamPlayer.getTeamId());
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<TeamPlayer> getTeamMembers(Team team) {
        try (SqlSession sqlSession = BukkitMybatis.instance.getSqlSessionFactory().openSession()) {
            TeamPlayerMapper teamPlayerMapper = sqlSession.getMapper(TeamPlayerMapper.class);
            TeamPlayerExample teamPlayerExample = new TeamPlayerExample();
            teamPlayerExample.createCriteria().andTeamIdEqualTo(team.getTeamId());
            return teamPlayerMapper.selectByExample(teamPlayerExample);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
