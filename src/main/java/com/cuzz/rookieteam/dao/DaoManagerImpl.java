package com.cuzz.rookieteam.dao;

import com.cuzz.rookieteam.mapper.TeamMapper;
import com.cuzz.rookieteam.mapper.TeamPlayerMapper;
import com.cuzz.rookieteam.model.Team;
import com.cuzz.rookieteam.model.TeamPlayer;
import com.cuzz.rookieteam.model.TeamPlayerExample;
import org.apache.ibatis.session.SqlSession;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.cuzz.rookieteam.RookieTeam.sqlSessionFactory;

public class DaoManagerImpl implements DaoManager{
    @Override
    public Team getTeamByPlayer(Player player) {

        return getTeamByPlayer(player.getUniqueId());
        
    }

    @Override
    public Team getTeamByPlayer(UUID uuid) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            TeamMapper teamMapper = sqlSession.getMapper(TeamMapper.class);
            TeamPlayerMapper teamPlayerMapper = sqlSession.getMapper(TeamPlayerMapper.class);
            TeamPlayer teamPlayer = teamPlayerMapper.selectByPrimaryKey(uuid.toString());
            if (teamPlayer == null) {
                return null;
            } else {
                Integer teamId = teamPlayer.getTeamId();
                Team team = teamMapper.selectByPrimaryKey(teamId);
                return team;
            }
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public Player getLeaderFromTeam(Team team) {
        if (team == null || team.getLeaderId() == null) {
            return null;
        }
        
        // Get the Bukkit server instance
        Bukkit.getServer();
        // Get the player by UUID
        return Bukkit.getPlayer(UUID.fromString(team.getLeaderId()));
    }

    @Override
    public Player getLeaderFromPlayer(Player player) {
        Team team = getTeamByPlayer(player);
        if (team == null) {
            return null;
        }
        return getLeaderFromTeam(team);
    }

    @Override
    public List<Player> getMembersFromTeam(Team team) {
        if (team == null) {
            return new ArrayList<>();
        }
        
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            TeamPlayerMapper teamPlayerMapper = sqlSession.getMapper(TeamPlayerMapper.class);
            TeamPlayerExample example = new TeamPlayerExample();
            example.createCriteria().andTeamIdEqualTo(team.getTeamId());
            List<TeamPlayer> teamPlayers = teamPlayerMapper.selectByExample(example);
            
            List<Player> members = new ArrayList<>();
            for (TeamPlayer teamPlayer : teamPlayers) {
                Player player = Bukkit.getPlayer(UUID.fromString(teamPlayer.getPlayerId()));
                if (player != null) {
                    members.add(player);
                }
            }
            return members;
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public List<Player> getMembersFromMember(Player member) {
        Team team = getTeamByPlayer(member);
        if (team == null) {
            return new ArrayList<>();
        }
        return getMembersFromTeam(team);
    }

    @Override
    public boolean createNewTeam(Team team) {
        if (team == null || team.getLeaderId() == null) {
            return false;
        }
        
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            TeamMapper teamMapper = sqlSession.getMapper(TeamMapper.class);
            int result = teamMapper.insert(team);
            
            if (result > 0) {
                // Add the leader to the team_players table
                TeamPlayerMapper teamPlayerMapper = sqlSession.getMapper(TeamPlayerMapper.class);
                TeamPlayer teamPlayer = new TeamPlayer();
                teamPlayer.setPlayerId(team.getLeaderId());
                teamPlayer.setTeamId(team.getTeamId());
                teamPlayerMapper.insert(teamPlayer);
                
                sqlSession.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            sqlSession.rollback();
            return false;
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public boolean createNewTeam(Player leader) {
        if (leader == null) {
            return false;
        }
        
        Team team = new Team();
        team.setLeaderId(leader.getUniqueId().toString());
        team.setTeamName(leader.getName() + "'s Team");
        team.setCreateTime(new Date());
        
        return createNewTeam(team);
    }

    @Override
    public boolean deleteTeam(Team team) {
        if (team == null || team.getTeamId() == null) {
            return false;
        }
        
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            // First delete all team players
            TeamPlayerMapper teamPlayerMapper = sqlSession.getMapper(TeamPlayerMapper.class);
            TeamPlayerExample example = new TeamPlayerExample();
            example.createCriteria().andTeamIdEqualTo(team.getTeamId());
            teamPlayerMapper.deleteByExample(example);
            
            // Then delete the team
            TeamMapper teamMapper = sqlSession.getMapper(TeamMapper.class);
            int result = teamMapper.deleteByPrimaryKey(team.getTeamId());
            
            sqlSession.commit();
            return result > 0;
        } catch (Exception e) {
            sqlSession.rollback();
            return false;
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public boolean deleteTeam(Player leader) {
        Team team = getTeamByPlayer(leader);
        if (team == null) {
            return false;
        }
        
        // Check if the player is actually the leader
        if (!team.getLeaderId().equals(leader.getUniqueId().toString())) {
            return false;
        }
        
        return deleteTeam(team);
    }

    @Override
    public boolean changeTeamLeader(Player newLeader, Team team) {
        if (newLeader == null || team == null) {
            return false;
        }
        
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            // Update the team leader
            team.setLeaderId(newLeader.getUniqueId().toString());
            TeamMapper teamMapper = sqlSession.getMapper(TeamMapper.class);
            int result = teamMapper.updateByPrimaryKey(team);
            
            if (result > 0) {
                // Make sure the new leader is in the team_players table
                TeamPlayerMapper teamPlayerMapper = sqlSession.getMapper(TeamPlayerMapper.class);
                TeamPlayer teamPlayer = teamPlayerMapper.selectByPrimaryKey(newLeader.getUniqueId().toString());
                
                if (teamPlayer == null) {
                    teamPlayer = new TeamPlayer();
                    teamPlayer.setPlayerId(newLeader.getUniqueId().toString());
                    teamPlayer.setTeamId(team.getTeamId());
                    teamPlayerMapper.insert(teamPlayer);
                }
                
                sqlSession.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            sqlSession.rollback();
            return false;
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public boolean removeMemberFromTeam(Player member, Team team) {
        if (member == null || team == null) {
            return false;
        }
        
        // Don't allow removing the leader
        if (team.getLeaderId().equals(member.getUniqueId().toString())) {
            return false;
        }
        
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            TeamPlayerMapper teamPlayerMapper = sqlSession.getMapper(TeamPlayerMapper.class);
            int result = teamPlayerMapper.deleteByPrimaryKey(member.getUniqueId().toString());
            
            sqlSession.commit();
            return result > 0;
        } catch (Exception e) {
            sqlSession.rollback();
            return false;
        } finally {
            sqlSession.close();
        }
    }
}
