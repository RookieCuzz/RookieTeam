package com.cuzz.rookieteam.model;

import java.util.Date;

public class TeamPlayer {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column rookieteam_players.player_id
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    private String playerId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column rookieteam_players.username
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    private String username;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column rookieteam_players.total_damage
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    private Long totalDamage;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column rookieteam_players.last_login
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    private Date lastLogin;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column rookieteam_players.team_id
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    private Integer teamId;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column rookieteam_players.player_id
     *
     * @return the value of rookieteam_players.player_id
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    public String getPlayerId() {
        return playerId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column rookieteam_players.player_id
     *
     * @param playerId the value for rookieteam_players.player_id
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    public void setPlayerId(String playerId) {
        this.playerId = playerId == null ? null : playerId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column rookieteam_players.username
     *
     * @return the value of rookieteam_players.username
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    public String getUsername() {
        return username;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column rookieteam_players.username
     *
     * @param username the value for rookieteam_players.username
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column rookieteam_players.total_damage
     *
     * @return the value of rookieteam_players.total_damage
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    public Long getTotalDamage() {
        return totalDamage;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column rookieteam_players.total_damage
     *
     * @param totalDamage the value for rookieteam_players.total_damage
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    public void setTotalDamage(Long totalDamage) {
        this.totalDamage = totalDamage;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column rookieteam_players.last_login
     *
     * @return the value of rookieteam_players.last_login
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    public Date getLastLogin() {
        return lastLogin;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column rookieteam_players.last_login
     *
     * @param lastLogin the value for rookieteam_players.last_login
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column rookieteam_players.team_id
     *
     * @return the value of rookieteam_players.team_id
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    public Integer getTeamId() {
        return teamId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column rookieteam_players.team_id
     *
     * @param teamId the value for rookieteam_players.team_id
     *
     * @mbggenerated Wed Apr 30 22:37:23 CST 2025
     */
    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }
}