package com.lessnop.customevents.ox;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.data.MessageManager;
import com.lessnop.customevents.database.DatabaseManager;
import com.lessnop.customevents.database.LocationData;
import com.lessnop.customevents.exception.PlayerInGameException;
import com.lessnop.customevents.exception.PlayerNotInGameException;
import com.lessnop.customevents.exception.QuestionIsActiveException;
import com.lessnop.customevents.exception.WrongConfigurationException;
import com.lessnop.customevents.utils.GameEventStatus;
import com.lessnop.customevents.utils.StringUtils;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OXManager {

	private Random r = new Random();
	private int startTime, questionTime;
	private boolean customQuestions, customRewards;
	private String yesRegionName, noRegionName;
	private List<Question> questions;
	private List<List<String>> rewardsCommands = new ArrayList<>();
	private BossBar bossBar;
	private BarColor barColor;
	private BarStyle barStyle;


	private GameEventStatus eventStatus = GameEventStatus.OFF;
	private Location eventSpawn, mainSpawn;
	private int actStartTime = -1;
	private List<Player> players;
	private Question actQuestion;
	private int actQuestionTime = -1;
	private ProtectedRegion yesRegion, noRegion;

	public GameEventStatus getEventStatus() {
		return eventStatus;
	}

	public void start(int startTime, boolean customQuestions, boolean customRewards) throws WrongConfigurationException {
		validateSettings();
		this.actStartTime = startTime;
		this.startTime = startTime;
		this.customQuestions = customQuestions;
		this.customRewards = customRewards;
		this.players = new ArrayList<>();
		this.eventStatus = GameEventStatus.WAITING_FOR_PLAYERS;

		MessageManager messageManager = CustomEvents.getInstance().getMessageManager();
		String announcementMsg = messageManager.getMsg("ox.announcement");
		announcementMsg = StringUtils.replaceVars(announcementMsg, "%time%", startTime + "");
		for (Player player : Bukkit.getOnlinePlayers())
			player.sendMessage(announcementMsg);

		String waitingMsg = messageManager.getMsg("ox.bossBar.waiting");
		waitingMsg = StringUtils.replaceVars(waitingMsg, "%time%", "" + actStartTime);
		this.bossBar = Bukkit.createBossBar(waitingMsg, barColor, barStyle);
		bossBar.setVisible(true);
		for (Player player : Bukkit.getOnlinePlayers()) {
			bossBar.addPlayer(player);
		}

		countDown();
	}

	private void countDown() {
		if (!eventStatus.equals(GameEventStatus.WAITING_FOR_PLAYERS)) return;

		CustomEvents main = CustomEvents.getInstance();
		MessageManager messageManager = main.getMessageManager();
		if (actStartTime == 0) {
			startEvent();
		} else {
			String chatMsg = messageManager.getMsg("ox.starting" + actStartTime);
			if (!chatMsg.equals("")) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.sendMessage(chatMsg);
				}
			}

			String msg = messageManager.getMsg("ox.bossBar.waiting");
			msg = StringUtils.replaceVars(msg, "%time%", "" + actStartTime);
			bossBar.setTitle(msg);
			Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
				actStartTime--;
				countDown();
			}, 20);
		}
	}

	private void startEvent() {
		eventStatus = GameEventStatus.ACTIVE;
		bossBar.removeAll();
		for (Player p : players) {
			bossBar.addPlayer(p);
		}

		CustomEvents main = CustomEvents.getInstance();
		MessageManager messageManager = main.getMessageManager();
		String chatMsg = messageManager.getMsg("ox.started");
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(chatMsg);
		}
		bossBar.setTitle(messageManager.getMsg("ox.bossBar.waitingForQuestion"));
		if (!customQuestions) {
			try {
				askRandomQuestion();
			} catch (QuestionIsActiveException e) {}
		}
	}

	public void askRandomQuestion() throws QuestionIsActiveException {
		Question question = getRandomQuestion();
		askQuestion(question);
	}

	public void askQuestion(Question question) throws QuestionIsActiveException {
		if (actQuestion != null) throw new QuestionIsActiveException();
		MessageManager messageManager = CustomEvents.getInstance().getMessageManager();
		String msg = messageManager.getMsg("ox.question");
		msg = StringUtils.replaceVars(msg, "%question%", question.getQuestion());
		for (Player p : players) {
			p.sendMessage(msg);
		}
		String bossBarMsg = messageManager.getMsg("ox.bossBar.question");
		bossBarMsg = StringUtils.replaceVars(bossBarMsg, "%question%", question.getQuestion());
		bossBar.setTitle(bossBarMsg);
		actQuestion = question;
		actQuestionTime = questionTime;
		countDownQuestion();
	}

	private void countDownQuestion() {
		if (!eventStatus.equals(GameEventStatus.ACTIVE)) return;
		if (actQuestionTime == 0) {
			MessageManager messageManager = CustomEvents.getInstance().getMessageManager();
			String lostMsg = messageManager.getMsg("ox.lost");
			List<Player> toRemove = new ArrayList<>();
			ProtectedRegion region = actQuestion.isAnswer() ? yesRegion : noRegion;
			for (Player p : players) {
				if (!eventSpawn.getWorld().equals(p.getWorld()) ||
						!region.contains(p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ())) {
					toRemove.add(p);
					p.sendMessage(lostMsg);
					p.teleport(mainSpawn);
					bossBar.removePlayer(p);
				}
			}
			players.removeAll(toRemove);
			String answerMsg = messageManager.getMsg(actQuestion.isAnswer() ? "ox.yesAnswer" : "ox.noAnswer");
			for (Player p : players)
				p.sendMessage(answerMsg);

			actQuestion = null;

			if (players.size() == 0) {
				String nobodyWonMsg = messageManager.getMsg("ox.nobodyWon");
				for (Player p : Bukkit.getOnlinePlayers())
					p.sendMessage(nobodyWonMsg);
				cancel(false);
			} else if (players.size() == 1) {
				String winnerNickName = players.get(0).getName();
				String playerWonMsg = messageManager.getMsg("ox.playerWon");
				playerWonMsg = StringUtils.replaceVars(playerWonMsg, "%player%", winnerNickName);
				for (Player p : Bukkit.getOnlinePlayers())
					p.sendMessage(playerWonMsg);
				if (!customRewards) {
					List<String> commands = getRandomRewards();
					for (String cmd : commands) {
						String command = StringUtils.replaceVars(cmd, "%player%", winnerNickName);
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
					}
				}
				cancel(false);
			} else if (!customQuestions) {
				try {
					askRandomQuestion();
				} catch (QuestionIsActiveException e) {}
			} else {
				bossBar.setTitle(messageManager.getMsg("ox.bossBar.waitingForQuestion"));
			}

		} else {
			bossBar.setProgress((double) (questionTime - actQuestionTime) / questionTime);
			Bukkit.getScheduler().scheduleSyncDelayedTask(CustomEvents.getInstance(), () -> {
				actQuestionTime--;
				countDownQuestion();
			}, 1);
		}
	}

	private Question getRandomQuestion() {
		return questions.get(r.nextInt(questions.size()));
	}

	private List<String> getRandomRewards() {
		return rewardsCommands.get(r.nextInt(rewardsCommands.size()));
	}

	public void cancel() {
		this.cancel(true);
	}

	public void cancel(boolean sendMessage) {
		if (bossBar != null) {
			for (Player p : bossBar.getPlayers())
				bossBar.removePlayer(p);
			bossBar.setVisible(false);
		}

		actStartTime = -1;
		eventStatus = GameEventStatus.OFF;
		actQuestionTime = -1;
		actQuestion = null;

		if (sendMessage) {
			MessageManager messageManager = CustomEvents.getInstance().getMessageManager();
			String msg = messageManager.getMsg("ox.canceled");
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage(msg);
			}
		}

	}

	public void joinPlayer(Player player) throws PlayerInGameException {
		if (players.contains(player)) throw new PlayerInGameException();
		players.add(player);
		bossBar.addPlayer(player);
		player.teleport(eventSpawn);
	}

	public void leavePlayer(Player player) throws PlayerNotInGameException {
		if (!players.contains(player)) throw new PlayerNotInGameException();
		players.remove(player);
		bossBar.removePlayer(player);
		player.teleport(mainSpawn);
	}

	public void setBarStyle(BarStyle barStyle) {
		this.barStyle = barStyle;
	}

	public void setBarColor(BarColor barColor) {
		this.barColor = barColor;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	public void setQuestionTime(int questionTime) {
		this.questionTime = questionTime;
	}

	private void validateSettings() throws WrongConfigurationException {
		DatabaseManager databaseManager = CustomEvents.getInstance().getDatabaseManager();
		LocationData eventLocationData = databaseManager.getLocationData("ox_event_spawn");
		LocationData mainLocationData = databaseManager.getLocationData("ox_main_spawn");
		if (eventLocationData == null || mainLocationData == null) throw new WrongConfigurationException();
		eventSpawn = eventLocationData.toLocation();
		mainSpawn = mainLocationData.toLocation();
		if (eventSpawn == null || mainSpawn == null) throw new WrongConfigurationException();
		World world = eventSpawn.getWorld();
		com.sk89q.worldedit.world.World worldGuardWorld = WorldGuard.getInstance().getPlatform().getMatcher().getWorldByName(world.getName());
		RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(worldGuardWorld);
		if (regionManager == null) throw new WrongConfigurationException();
		yesRegion = regionManager.getRegion(yesRegionName);
		if (yesRegion == null) throw new WrongConfigurationException();
		noRegion = regionManager.getRegion(noRegionName);
		if (noRegion == null) throw new WrongConfigurationException();
	}


	public void setYesRegionName(String yesRegionName) {
		this.yesRegionName = yesRegionName;
	}

	public void setNoRegionName(String noRegionName) {
		this.noRegionName = noRegionName;
	}

	public void setRewardsCommands(List<List<String>> rewardsCommands) {
		this.rewardsCommands = rewardsCommands;
	}

	public void addPlayerToBossBar(Player player) {
		if (bossBar != null) {
			bossBar.addPlayer(player);
		}
	}

	public void removePlayerFromBossBar(Player player) {
		if (bossBar != null) {
			bossBar.removePlayer(player);
		}
	}
}
