package com.cuzz.rookieteam.mapper;

import com.cuzz.rookieteam.model.Team;
import com.cuzz.rookieteam.model.TeamExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TeamMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table rookieteam_teams
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    int countByExample(TeamExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table rookieteam_teams
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    int deleteByExample(TeamExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table rookieteam_teams
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    int deleteByPrimaryKey(Integer teamId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table rookieteam_teams
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    int insert(Team record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table rookieteam_teams
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    int insertSelective(Team record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table rookieteam_teams
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    List<Team> selectByExample(TeamExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table rookieteam_teams
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    Team selectByPrimaryKey(Integer teamId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table rookieteam_teams
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    int updateByExampleSelective(@Param("record") Team record, @Param("example") TeamExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table rookieteam_teams
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    int updateByExample(@Param("record") Team record, @Param("example") TeamExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table rookieteam_teams
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    int updateByPrimaryKeySelective(Team record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table rookieteam_teams
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    int updateByPrimaryKey(Team record);
}