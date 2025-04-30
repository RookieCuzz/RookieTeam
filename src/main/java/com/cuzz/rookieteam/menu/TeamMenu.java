package com.cuzz.rookieteam.menu;

import com.cuzz.bukkitmybatis.BukkitMybatis;
import com.cuzz.rookieteam.RookieTeam;
import com.cuzz.rookieteam.mapper.TeamMapper;
import com.cuzz.rookieteam.mapper.TeamPlayerMapper;
import com.cuzz.rookieteam.model.Team;
import com.cuzz.rookieteam.model.TeamExample;
import com.cuzz.rookieteam.model.TeamPlayer;
import com.cuzz.rookieteam.model.TeamPlayerExample;
import com.cuzz.rookieteam.utils.DateUtils;
import com.cuzz.rookieteam.utils.TeamUtils;
import com.cuzz.rookieteam.utils.ToastUtils;
import me.trytofeel.rookieFonts.RookieFonts;
import me.trytofeel.rookieFonts.manager.TemplateManager;
import me.trytofeel.rookieFonts.models.Template;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import nl.odalitadevelopments.menus.annotations.Menu;
import nl.odalitadevelopments.menus.contents.MenuContents;
import nl.odalitadevelopments.menus.items.buttons.CloseItem;
import nl.odalitadevelopments.menus.menu.providers.PlayerMenuProvider;
import nl.odalitadevelopments.menus.menu.type.MenuType;
import org.apache.ibatis.session.SqlSession;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.*;

@Menu(
        title = "支付窗口",
        type = MenuType.CHEST_6_ROW
)
public class TeamMenu implements PlayerMenuProvider {

    @Override
    public void onLoad(@NotNull Player player, @NotNull MenuContents menuContents) {
        Team team;
        List<TeamPlayer> teamPlayers;
        try (SqlSession sqlSession = BukkitMybatis.instance.getSqlSessionFactory().openSession()){
            TeamMapper teamMapper = sqlSession.getMapper(TeamMapper.class);
            TeamPlayerMapper teamPlayerMapper = sqlSession.getMapper(TeamPlayerMapper.class);
            TeamPlayer teamPlayer = teamPlayerMapper.selectByPrimaryKey(player.getUniqueId().toString());
            if(teamPlayer == null){
                teamPlayer = new TeamPlayer();
                teamPlayer.setTeamId(0);
                teamPlayer.setPlayerId(player.getUniqueId().toString());
                teamPlayer.setUsername(player.getName());
                teamPlayerMapper.insert(teamPlayer);
                sqlSession.commit();
            }
            int teamId = teamPlayer.getTeamId();
            TeamPlayerExample teamPlayerExample = new TeamPlayerExample();
            teamPlayerExample.createCriteria().andTeamIdEqualTo(teamId);
            teamPlayers = teamPlayerMapper.selectByExample(teamPlayerExample);
            team = teamMapper.selectByPrimaryKey(teamId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 如果玩家不在队伍中，则创建一支队伍
        if(team == null){
            try (SqlSession sqlSession = BukkitMybatis.instance.getSqlSessionFactory().openSession()){
                TeamMapper teamMapper = sqlSession.getMapper(TeamMapper.class);
                team = new Team();
                team.setTeamName(player.getName() + "的队伍");
                team.setExperience(0);
                team.setTotalDamage(0L);
                team.setLeaderId(player.getUniqueId().toString());
                team.setCreateTime(DateUtils.asDate(LocalDateTime.now()));
                teamMapper.insert(team);
                sqlSession.commit();

                TeamExample teamExample = new TeamExample();
                teamExample.createCriteria().andLeaderIdEqualTo(player.getUniqueId().toString());
                team = teamMapper.selectByExample(teamExample).getFirst();

                TeamPlayerMapper teamPlayerMapper = sqlSession.getMapper(TeamPlayerMapper.class);
                TeamPlayer teamPlayer = new TeamPlayer();
                teamPlayer.setTeamId(team.getTeamId());
                teamPlayer.setPlayerId(player.getUniqueId().toString());
                teamPlayer.setUsername(player.getName());
                teamPlayerMapper.updateByPrimaryKey(teamPlayer);
                sqlSession.commit();

                int teamId = team.getTeamId();
                TeamPlayerExample teamPlayerExample = new TeamPlayerExample();
                teamPlayerExample.createCriteria().andTeamIdEqualTo(teamId);
                teamPlayers = teamPlayerMapper.selectByExample(teamPlayerExample);

                player.getPersistentDataContainer().set(new NamespacedKey("rookieteam", "team_id"), PersistentDataType.INTEGER, teamId);

                ToastUtils.displayTo(player, "netherite_sword", "队伍成功创建！", ToastUtils.Style.GOAL);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // for test
//        teamPlayers = new ArrayList<>();
//        for(int i = 0; i < 11; i++){
//            TeamPlayer teamPlayer = new TeamPlayer();
//            teamPlayer.setPlayerId(player.getUniqueId().toString());
//            teamPlayer.setUsername(player.getName());
//            teamPlayers.add(teamPlayer);
//        }

        for(TeamPlayer teamPlayer : teamPlayers){
            ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            skullMeta.setOwningPlayer(Bukkit.getPlayer(teamPlayer.getUsername()));
            skullMeta.setDisplayName(ChatColor.WHITE + teamPlayer.getUsername());
            skullMeta.setItemModel(new NamespacedKey("minecraft","custom_head"));
            skullMeta.setCustomModelData(1);
            if(player.getUniqueId().toString().equals(team.getLeaderId())) {
                if(teamPlayer.getPlayerId().equals(team.getLeaderId())){
                    skullMeta.setLore(List.of(ChatColor.GRAY + "身份：" + ChatColor.GOLD + "队长"));
                    itemStack.setItemMeta(skullMeta);
                    menuContents.setDisplay(2 + teamPlayers.indexOf(teamPlayer) / 7, 1 + teamPlayers.indexOf(teamPlayer) % 7, itemStack);
                } else {
                    skullMeta.setLore(List.of(ChatColor.GRAY + "身份：" + ChatColor.GREEN + "成员", ChatColor.YELLOW + "点击以将TA移出队伍"));
                    itemStack.setItemMeta(skullMeta);
                    List<TeamPlayer> finalTeamPlayers = teamPlayers;
                    menuContents.setClickable(2 + teamPlayers.indexOf(teamPlayer) / 7, 1 + teamPlayers.indexOf(teamPlayer) % 7, itemStack, event -> {
                        ItemStack warnStack = new ItemStack(Material.BRICK);
                        ItemMeta warnMeta = warnStack.getItemMeta();
                        warnMeta.setItemModel(new NamespacedKey("oraxen","party_warn"));
                        warnMeta.setDisplayName(ChatColor.RED + "你确认要这么做吗？");
                        warnMeta.setLore(Arrays.asList(ChatColor.YELLOW + "再次点击本按钮将踢出玩家：" + teamPlayer.getUsername(), ChatColor.YELLOW + "这是一个不可撤销的操作！"));
                        warnStack.setItemMeta(warnMeta);

                        menuContents.setClickable(2 + finalTeamPlayers.indexOf(teamPlayer) / 7, 1 + finalTeamPlayers.indexOf(teamPlayer) % 7, warnStack, event1 -> {
                            try (SqlSession sqlSession = BukkitMybatis.instance.getSqlSessionFactory().openSession()){
                                TeamPlayerMapper teamPlayerMapper = sqlSession.getMapper(TeamPlayerMapper.class);
                                TeamPlayer teamPlayer1 = teamPlayerMapper.selectByPrimaryKey(teamPlayer.getPlayerId());
                                teamPlayer1.setTeamId(0);
                                teamPlayerMapper.updateByPrimaryKey(teamPlayer1);
                                Player targetPlayer = Bukkit.getPlayer(teamPlayer.getUsername());
                                if(targetPlayer != null){
                                    ToastUtils.displayTo(targetPlayer, "barrier", "你已被移出队伍！", ToastUtils.Style.GOAL);
                                }
                                targetPlayer.sendMessage(ChatColor.RED + "组队>>> 你已被移出队伍！");
                                ToastUtils.displayTo(player, "barrier", teamPlayer.getUsername() + " 已被移出队伍！", ToastUtils.Style.GOAL);
                                player.sendMessage(ChatColor.RED + "组队>>> " + teamPlayer.getUsername() + " 已被移出队伍！");
                                sqlSession.commit();
                                targetPlayer.getPersistentDataContainer().set(new NamespacedKey("rookieteam", "team_id"), PersistentDataType.INTEGER, 0);
                                if(RookieTeam.getInstance().getOdalitaMenus().getOpenMenuSession(player) != null){
                                    RookieTeam.getInstance().getOdalitaMenus().getOpenMenuSession(player).reopen();
                                }
                            }
                        });
                    });
                }
            } else {
                if(teamPlayer.getPlayerId().equals(team.getLeaderId())){
                    skullMeta.setLore(List.of(ChatColor.GRAY + "身份：" + ChatColor.GOLD + "队长"));
                } else {
                    skullMeta.setLore(List.of(ChatColor.GRAY + "身份：" + ChatColor.GREEN + "成员"));
                }
                itemStack.setItemMeta(skullMeta);
                menuContents.setDisplay(2 + teamPlayers.indexOf(teamPlayer) / 7, 1 + teamPlayers.indexOf(teamPlayer) % 7, itemStack);
            }
        }

        if(teamPlayers.size() < 14) {
            ItemStack itemStack = new ItemStack(Material.BRICK);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setItemModel(new NamespacedKey("oraxen","party_member_add"));
            itemMeta.setDisplayName(ChatColor.WHITE + "添加");
            itemStack.setItemMeta(itemMeta);
            menuContents.setClickable(2 + teamPlayers.size() / 7, 1 + teamPlayers.size() % 7, itemStack, event -> {
                AddMemberMenu addMemberMenu = new AddMemberMenu(input -> {
                   if(input == null || input.isEmpty()){
                       ToastUtils.displayTo(player, "barrier", "玩家名不能为空！", ToastUtils.Style.GOAL);
                       player.sendMessage(ChatColor.RED + "组队>>> 玩家名不能为空！");
                       return;
                   }
                   Player member = Bukkit.getPlayer(input);
                   if(member == null){
                       ToastUtils.displayTo(player, "barrier", "玩家不存在！", ToastUtils.Style.GOAL);
                       player.sendMessage(ChatColor.RED + "组队>>> 指定的玩家不存在！");
                       return;
                   }
                   try (SqlSession sqlSession = BukkitMybatis.instance.getSqlSessionFactory().openSession()){
                       TeamMapper teamMapper = sqlSession.getMapper(TeamMapper.class);
                       TeamPlayerMapper teamPlayerMapper = sqlSession.getMapper(TeamPlayerMapper.class);
                       Team targetTeam = teamMapper.selectByPrimaryKey(teamPlayerMapper.selectByPrimaryKey(player.getUniqueId().toString()).getTeamId());
                       TeamPlayer targetPlayer = teamPlayerMapper.selectByPrimaryKey(member.getUniqueId().toString());
                       if(targetPlayer == null) {
                           try (SqlSession sqlSession1 = BukkitMybatis.instance.getSqlSessionFactory().openSession()){
                               TeamPlayerMapper teamPlayerMapper1 = sqlSession1.getMapper(TeamPlayerMapper.class);
                               TeamPlayer teamPlayer1 = new TeamPlayer();
                               teamPlayer1.setTeamId(0);
                               teamPlayer1.setPlayerId(member.getUniqueId().toString());
                               teamPlayer1.setUsername(member.getName());
                               teamPlayerMapper1.insert(teamPlayer1);
                               sqlSession1.commit();
                               targetPlayer = teamPlayerMapper1.selectByPrimaryKey(member.getUniqueId().toString());
                           }
                       }
                       if(targetPlayer.getTeamId() != 0 && !Objects.equals(targetPlayer.getTeamId(), targetTeam.getTeamId())) {
                           ToastUtils.displayTo(player, "barrier", "该玩家已加入其他队伍！", ToastUtils.Style.GOAL);
                           player.sendMessage(ChatColor.RED + "组队>>> 该玩家已加入其他队伍！");
                       } else if(Objects.equals(targetPlayer.getTeamId(), targetTeam.getTeamId())){
                           ToastUtils.displayTo(player, "barrier", "该玩家已加入队伍！", ToastUtils.Style.GOAL);
                           player.sendMessage(ChatColor.YELLOW + "组队>>> 该玩家已加入队伍！");
                       } else {
                           targetPlayer.setTeamId(targetTeam.getTeamId());
                           teamPlayerMapper.updateByPrimaryKey(targetPlayer);
                           sqlSession.commit();
                           ToastUtils.displayTo(player, "netherite_sword", "添加成功！", ToastUtils.Style.GOAL);
                           player.sendMessage(ChatColor.GREEN + "组队>>> 添加成功！");
                           ToastUtils.displayTo(member, "netherite_sword", "已加入队伍！", ToastUtils.Style.GOAL);
                           member.sendMessage(ChatColor.GREEN + "组队>>> 你已受邀加入队伍【" + targetTeam.getTeamName() + "】");
                           member.getPersistentDataContainer().set(new NamespacedKey("rookieteam", "team_id"), PersistentDataType.INTEGER, targetTeam.getTeamId());

                           member.getPersistentDataContainer().set(new NamespacedKey("rookieteam", "teamdamage"), PersistentDataType.BOOLEAN, Objects.requireNonNullElse(player.getPersistentDataContainer().get(new NamespacedKey("rookieteam", "teamdamage"), PersistentDataType.BOOLEAN), false));
                           if(Objects.requireNonNullElse(player.getPersistentDataContainer().get(new NamespacedKey("rookieteam", "teamdamage"), PersistentDataType.BOOLEAN), false)) {
                               member.sendMessage(ChatColor.RED + "组队>>> 组队伤害已启用！");
                           } else {
                               member.sendMessage(ChatColor.GREEN + "组队>>> 组队伤害已禁用！");
                           }

                           if(RookieTeam.getInstance().getOdalitaMenus().getOpenMenuSession(player) != null) {
                               RookieTeam.getInstance().getOdalitaMenus().getOpenMenuSession(player).reopen();
                           }
                       }
                   }
                });
                RookieTeam.getInstance().getOdalitaMenus().openMenu(addMemberMenu, player);
            });
        }

        ItemStack aboutStack = new ItemStack(Material.BRICK);
        ItemMeta aboutMeta = aboutStack.getItemMeta();
        aboutMeta.setItemModel(new NamespacedKey("oraxen","party_about"));
        aboutMeta.setDisplayName(ChatColor.BLUE + "关于");
        aboutMeta.setLore(Arrays.asList("组队系统可以...", "(稍后更改)"));
        aboutStack.setItemMeta(aboutMeta);
        menuContents.setDisplay(0, 8, aboutStack);

        ItemStack closeStack = new ItemStack(Material.BRICK);
        ItemMeta closeMeta = closeStack.getItemMeta();
        closeMeta.setItemModel(new NamespacedKey("oraxen","party_close"));
        closeMeta.setDisplayName(ChatColor.RED + "关闭");
        closeStack.setItemMeta(closeMeta);
        menuContents.set(0,0, CloseItem.of(closeStack));

        menuContents.setUpdatable(0, 5, () -> {
            ItemStack chatToggleStack = new ItemStack(Material.BRICK);
            ItemMeta chatToggleMeta = chatToggleStack.getItemMeta();
            if (player.getPersistentDataContainer().get(new NamespacedKey("rookieteam", "chat"), PersistentDataType.BOOLEAN) == null) {
                player.getPersistentDataContainer().set(new NamespacedKey("rookieteam", "chat"), PersistentDataType.BOOLEAN, false);
            }
            if (player.getPersistentDataContainer().get(new NamespacedKey("rookieteam", "chat"), PersistentDataType.BOOLEAN).equals(false)) {
                chatToggleMeta.setItemModel(new NamespacedKey("oraxen", "party_chat"));
                chatToggleMeta.setDisplayName(ChatColor.WHITE + "组队聊天: " + ChatColor.RED + "未启用");
            } else {
                chatToggleMeta.setItemModel(new NamespacedKey("oraxen", "party_chat_on"));
                chatToggleMeta.setDisplayName(ChatColor.WHITE + "组队聊天: " + ChatColor.GREEN + "已启用");
            }
            chatToggleStack.setItemMeta(chatToggleMeta);
            return chatToggleStack;
        }, event -> {
            player.getPersistentDataContainer().set(new NamespacedKey("rookieteam", "chat"), PersistentDataType.BOOLEAN, !player.getPersistentDataContainer().get(new NamespacedKey("rookieteam", "chat"), PersistentDataType.BOOLEAN));
        });

        menuContents.setUpdatable(0, 1, () -> {
            ItemStack damageToggleStack = new ItemStack(Material.BRICK);
            ItemMeta damageToggleMeta = damageToggleStack.getItemMeta();
            if (player.getPersistentDataContainer().get(new NamespacedKey("rookieteam", "teamdamage"), PersistentDataType.BOOLEAN) == null) {
                Team team1 = TeamUtils.getPlayerTeam(player);
                List<TeamPlayer> teamMembers = TeamUtils.getTeamMembers(team1);
                for (TeamPlayer teamMember : teamMembers) {
                    Player player1 = Bukkit.getPlayer(UUID.fromString(teamMember.getPlayerId()));
                    if (player1 != null) {
                        player1.getPersistentDataContainer().set(new NamespacedKey("rookieteam", "teamdamage"), PersistentDataType.BOOLEAN, false);
                    }
                }
            }
            if (player.getPersistentDataContainer().get(new NamespacedKey("rookieteam", "teamdamage"), PersistentDataType.BOOLEAN).equals(false)) {
                damageToggleMeta.setItemModel(new NamespacedKey("oraxen", "party_team_damage_off"));
                damageToggleMeta.setDisplayName(ChatColor.WHITE + "组队伤害: " + ChatColor.GREEN + "未启用");
            } else {
                damageToggleMeta.setItemModel(new NamespacedKey("oraxen", "party_team_damage_on"));
                damageToggleMeta.setDisplayName(ChatColor.WHITE + "组队伤害: " + ChatColor.RED + "已启用");
            }
            damageToggleStack.setItemMeta(damageToggleMeta);
            return damageToggleStack;
        }, event -> {
            if(TeamUtils.getPlayerTeam(player).getLeaderId().equals(player.getUniqueId().toString())) {
                Team team1 = TeamUtils.getPlayerTeam(player);
                List<TeamPlayer> teamMembers = TeamUtils.getTeamMembers(team1);
                boolean damage = player.getPersistentDataContainer().get(new NamespacedKey("rookieteam", "teamdamage"), PersistentDataType.BOOLEAN);
                for (TeamPlayer teamMember : teamMembers) {
                    Player player1 = Bukkit.getPlayer(UUID.fromString(teamMember.getPlayerId()));
                    if (player1 != null) {
                        player1.getPersistentDataContainer().set(new NamespacedKey("rookieteam", "teamdamage"), PersistentDataType.BOOLEAN, !damage);
                        if(!damage) {
                            player1.sendMessage(ChatColor.RED + "组队>>> 组队伤害已开启！");
                        } else {
                            player1.sendMessage(ChatColor.GREEN + "组队>>> 组队伤害已关闭！");
                        }
                    }
                }
            } else {
                player.sendMessage(ChatColor.YELLOW + "组队>>> 只有队长才能更改组队伤害设置！");
            }
        });

        ItemStack leaveStack = new ItemStack(Material.BRICK);
        ItemMeta leaveMeta = leaveStack.getItemMeta();
        leaveMeta.setItemModel(new NamespacedKey("oraxen","party_leave"));
        leaveMeta.setDisplayName(ChatColor.RED + "离开队伍");
        leaveStack.setItemMeta(leaveMeta);
        menuContents.setClickable(0,7, leaveStack, event -> {
            try (SqlSession sqlSession = BukkitMybatis.instance.getSqlSessionFactory().openSession()){
                List<TeamPlayer> teamPlayersN;
                TeamMapper teamMapper = sqlSession.getMapper(TeamMapper.class);
                TeamPlayerMapper teamPlayerMapper = sqlSession.getMapper(TeamPlayerMapper.class);

                int teamId = teamPlayerMapper.selectByPrimaryKey(player.getUniqueId().toString()).getTeamId();
                TeamPlayerExample teamPlayerExample = new TeamPlayerExample();
                teamPlayerExample.createCriteria().andTeamIdEqualTo(teamId);
                teamPlayersN = teamPlayerMapper.selectByExample(teamPlayerExample);

                if(teamMapper.selectByPrimaryKey(teamId).getLeaderId().equals(player.getUniqueId().toString())){
                    ItemStack warnStack = new ItemStack(Material.BRICK);
                    ItemMeta warnMeta = warnStack.getItemMeta();
                    warnMeta.setItemModel(new NamespacedKey("oraxen","party_warn"));
                    warnMeta.setDisplayName(ChatColor.RED + "你确认要这么做吗？");
                    warnMeta.setLore(Arrays.asList(ChatColor.YELLOW + "再次点击本按钮将解散队伍！", ChatColor.YELLOW + "这是一个不可撤销的操作", ChatColor.YELLOW + "将使你失去本队伍的所有进度！"));
                    warnStack.setItemMeta(warnMeta);
                    RookieTeam.getInstance().getOdalitaMenus().getOpenMenuSession(player).getMenuContents().setClickable(0,7, warnStack, event1 -> {
                        try (SqlSession sqlSession1 = BukkitMybatis.instance.getSqlSessionFactory().openSession()){
                            List<TeamPlayer> teamPlayersN1;
                            TeamMapper teamMapper1 = sqlSession1.getMapper(TeamMapper.class);
                            TeamPlayerMapper teamPlayerMapper1 = sqlSession1.getMapper(TeamPlayerMapper.class);

                            int teamId1 = teamPlayerMapper1.selectByPrimaryKey(player.getUniqueId().toString()).getTeamId();
                            TeamPlayerExample teamPlayerExample1 = new TeamPlayerExample();
                            teamPlayerExample1.createCriteria().andTeamIdEqualTo(teamId1);
                            teamPlayersN1 = teamPlayerMapper1.selectByExample(teamPlayerExample1);

                            for(TeamPlayer updatePlayer1 : teamPlayersN1){
                                updatePlayer1.setTeamId(0);
                                teamPlayerMapper1.updateByPrimaryKey(updatePlayer1);
                            }
                            teamMapper1.deleteByPrimaryKey(teamId1);

                            teamPlayersN1.forEach(teamPlayer -> {
                                Player player1 = Bukkit.getPlayer(UUID.fromString(teamPlayer.getPlayerId()));
                                if(player1 != null){
                                    ToastUtils.displayTo(player1, "barrier", "你所在的队伍已被解散！", ToastUtils.Style.GOAL);
                                    player1.sendMessage(ChatColor.RED + "组队>>> 你所在的队伍已被解散！");
                                    player1.getPersistentDataContainer().set(new NamespacedKey("rookieteam", "team_id"), PersistentDataType.INTEGER, 0);
                                    if(RookieTeam.getInstance().getOdalitaMenus().getOpenMenuSession(player1) != null) {
                                        RookieTeam.getInstance().getOdalitaMenus().getOpenMenuSession(player1).getPlayer().closeInventory();
                                    }
                                }
                            });

                            player.getPersistentDataContainer().set(new NamespacedKey("rookieteam", "team_id"), PersistentDataType.INTEGER, 0);
                            sqlSession1.commit();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        event.getWhoClicked().closeInventory();
                        ToastUtils.displayTo(player, "barrier", "队伍已被解散！", ToastUtils.Style.GOAL);
                        player.sendMessage(ChatColor.RED + "组队>>> 队伍已被解散！");
                    });
                    return;
                } else {
                    TeamPlayer teamPlayer = teamPlayerMapper.selectByPrimaryKey(player.getUniqueId().toString());
                    teamPlayer.setTeamId(0);
                    teamPlayerMapper.updateByPrimaryKey(teamPlayer);
                    ToastUtils.displayTo(player, "barrier", "你已退出队伍！", ToastUtils.Style.GOAL);
                    player.sendMessage(ChatColor.RED + "组队>>> 你已退出队伍！");
                    player.getPersistentDataContainer().set(new NamespacedKey("rookieteam", "team_id"), PersistentDataType.INTEGER, 0);
                    Player leader = Bukkit.getPlayer(UUID.fromString(teamMapper.selectByPrimaryKey(teamId).getLeaderId()));
                    ToastUtils.displayTo(leader, "barrier", player.getName() + " 已退出队伍！", ToastUtils.Style.GOAL);
                    leader.sendMessage(ChatColor.RED + "组队>>> " + player.getName() + " 已退出队伍！");
                    sqlSession.commit();
                    if(RookieTeam.getInstance().getOdalitaMenus().getOpenMenuSession(leader) != null){
                        RookieTeam.getInstance().getOdalitaMenus().getOpenMenuSession(leader).reopen();
                    }
                    event.getWhoClicked().closeInventory();
                }

                teamPlayersN.removeIf(teamPlayer -> teamPlayer.getPlayerId().equals(player.getUniqueId().toString()));
                if(teamPlayersN.isEmpty()){
                    teamMapper.deleteByPrimaryKey(teamId);
                }
                sqlSession.commit();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            event.getWhoClicked().closeInventory();
        });

        String memberBox = "嶈{shift:-3}", memberAdd = "麷{shift:-3}";

        Map<String, String> stringStringMap = RookieFonts.playerPapiMap.get(player.getName());
        if(stringStringMap == null){
            stringStringMap = new HashMap<>();
        }
        stringStringMap.put("%member1%", memberBox.repeat(Math.min(teamPlayers.size() - 1, 6)));
        stringStringMap.put("%member2%", memberBox.repeat(Math.max(Math.min(teamPlayers.size() - 7, 7), 0)));
        RookieFonts.playerPapiMap.put(player.getName(),stringStringMap);
        Template template = TemplateManager.getTemplateManager().TemplateList.get("team");
        Component defaultComponentByString = template.getDefaultComponentByString(player.getName());
        String jsonText = GsonComponentSerializer.gson().serialize(defaultComponentByString);

        menuContents.setTitle(jsonText);

        menuContents.events().onInventoryEvent(InventoryDragEvent.class, event -> {
            Bukkit.getScheduler().runTaskLater(RookieTeam.getInstance(), () -> {

            }, 2);
        });
    }
}