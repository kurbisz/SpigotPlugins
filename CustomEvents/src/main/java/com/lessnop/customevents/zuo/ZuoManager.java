package com.lessnop.customevents.zuo;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.data.MessageManager;
import com.lessnop.customevents.database.DatabaseManager;
import com.lessnop.customevents.database.LocationData;
import com.lessnop.customevents.exception.*;
import com.lessnop.customevents.utils.GameEventStatus;
import com.lessnop.customevents.utils.PrefixUtils;
import com.lessnop.customevents.utils.StringUtils;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.*;

public class ZuoManager {

	private Random r = new Random();
	private int startTime;
	private BossBar bossBar;
	private HashMap<String, SingleZuoEvent> zuoEvents;

	private GameEventStatus eventStatus = GameEventStatus.OFF;
	private Location eventSpawn, mainSpawn;
	private SingleZuoEvent actZuoEvent;
	private int actStartTime = -1;
	private int actWave = -1;
	private int totalMobs = 0;
	private List<Player> players;
	private List<ActiveMob> actMobs = new ArrayList<>();
	private ProtectedRegion region;

	public void start(String eventName, int startTime) throws WrongConfigurationException, EventNotFoundException {
		validateSettings(eventName);
		this.actStartTime = startTime;
		this.startTime = startTime;
		this.eventStatus = GameEventStatus.WAITING_FOR_PLAYERS;
		this.players = new ArrayList<>();
		this.actMobs = new ArrayList<>();

		MessageManager messageManager = CustomEvents.getInstance().getMessageManager();
		String announcementMsg = messageManager.getMsg("zuo.announcement");
		announcementMsg = StringUtils.replaceVars(announcementMsg, "%time%", startTime + "");
		for (Player player : Bukkit.getOnlinePlayers())
			player.sendMessage(announcementMsg);

		String waitingMsg = messageManager.getMsg("zuo.bossBar.waiting");
		waitingMsg = StringUtils.replaceVars(waitingMsg, "%time%", "" + actStartTime);
		this.bossBar = Bukkit.createBossBar(waitingMsg, actZuoEvent.getBarColor(), actZuoEvent.getBarStyle());
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
			String chatMsg = messageManager.getMsg("zuo.starting" + actStartTime);
			if (!chatMsg.equals("")) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.sendMessage(chatMsg);
				}
			}

			String msg = messageManager.getMsg("zuo.bossBar.waiting");
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
		String chatMsg = messageManager.getMsg("zuo.started");
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(chatMsg);
		}
		actWave = 0;
		startWave();
	}

	private void startWave() {
		totalMobs = 0;
		MessageManager messageManager = CustomEvents.getInstance().getMessageManager();
		if (actZuoEvent.getMaxWave() <= actWave) {
			String msg = messageManager.getMsg("zuo.ended");
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage(msg);
			}
			cancel(false);
			return;
		}

		actMobs.clear();
		ZuoWave zuoWave = actZuoEvent.getWave(actWave);
		for (ZuoWaveMob zuoWaveMob : zuoWave.getMobs()) {
			int am = r.nextInt(zuoWaveMob.getMaxAm() - zuoWaveMob.getMinAm() + 1) + zuoWaveMob.getMinAm();
			MythicMob mythicMob = MythicProvider.get().getMobManager().getMythicMob(zuoWaveMob.getName()).orElse(null);
			if (mythicMob == null) {
				Bukkit.getConsoleSender().sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
						"Mob o nazwie " + zuoWaveMob.getName() + " nie istnieje!");
				continue;
			}
			for (int i = 0; i < am; i++) {
				int lvl = r.nextInt(zuoWaveMob.getMaxLvl() - zuoWaveMob.getMinLvl() + 1) + zuoWaveMob.getMinLvl();
				spawnZuoWaveMob(mythicMob, lvl);
			}
		}
		String wave = messageManager.getMsg("zuo.bossBar.wave");
		wave = StringUtils.replaceVars(wave, "%wave%", (actWave + 1) + "", "%left%", actMobs.size() + "", "%total%", totalMobs + "");
		bossBar.setTitle(wave);
	}

	private void spawnZuoWaveMob(MythicMob mythicMob, int lvl) {
		AbstractLocation abstractLocation = getRandomLocationInRegion();
		ActiveMob activeMob = mythicMob.spawn(abstractLocation, lvl);
		actMobs.add(activeMob);
		totalMobs++;
	}

	private AbstractLocation getRandomLocationInRegion() {
		World world = eventSpawn.getWorld();
		int minY = actZuoEvent.getMinY();
		int maxY = actZuoEvent.getMaxY();
		double x, z;
		int y;
		BlockVector3 min = region.getMinimumPoint();
		BlockVector3 max = region.getMaximumPoint();
		boolean end = false;
		while (true) {
			x = r.nextDouble() * (max.getBlockX() - min.getBlockX() + 1) + min.getBlockX();
			z = r.nextDouble() * (max.getBlockZ() - min.getBlockZ() + 1) + min.getBlockZ();
			for (y = maxY; y >= minY; y--) {
				Block b = world.getBlockAt((int) x, y, (int) z);
				Block below = world.getBlockAt((int) x, y - 1, (int) z);
				if (b.getType().equals(Material.AIR) && !below.getType().equals(Material.AIR)) {
					end = true;
					break;
				}
			}
			if (end) break;
		}
		return new AbstractLocation(eventSpawn.getWorld().getName(), x, 1 + r.nextDouble() + y, z);
	}

	private void validateSettings(String eventName) throws WrongConfigurationException, EventNotFoundException {
		actZuoEvent = zuoEvents.get(eventName);
		if (actZuoEvent == null) throw new EventNotFoundException();
		DatabaseManager databaseManager = CustomEvents.getInstance().getDatabaseManager();
		LocationData eventLocationData = databaseManager.getLocationData("zuo_" + eventName + "_event_spawn");
		LocationData mainLocationData = databaseManager.getLocationData("zuo_" + eventName + "_main_spawn");
		if (eventLocationData == null || mainLocationData == null) throw new WrongConfigurationException();
		eventSpawn = eventLocationData.toLocation();
		mainSpawn = mainLocationData.toLocation();
		if (eventSpawn == null || mainSpawn == null) throw new WrongConfigurationException();
		World world = eventSpawn.getWorld();
		com.sk89q.worldedit.world.World worldGuardWorld = WorldGuard.getInstance().getPlatform().getMatcher().getWorldByName(world.getName());
		RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(worldGuardWorld);
		if (regionManager == null) throw new WrongConfigurationException();
		region = regionManager.getRegion(actZuoEvent.getRegionName());
		if (region == null) throw new WrongConfigurationException();
	}

	public void removeMob(ActiveMob activeMob) {
		if (actMobs.remove(activeMob)) {
			MessageManager messageManager = CustomEvents.getInstance().getMessageManager();
			String wave = messageManager.getMsg("zuo.bossBar.wave");
			wave = StringUtils.replaceVars(wave, "%wave%", (actWave + 1) + "", "%left%", actMobs.size() + "", "%total%", totalMobs + "");
			bossBar.setTitle(wave);
			if(actMobs.size() == 0) {
				actWave++;
				startWave();
			}
		}
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
		actZuoEvent = null;
		for (ActiveMob activeMob : actMobs) {
			activeMob.remove();
		}
		actMobs = new ArrayList<>();

		if (sendMessage) {
			MessageManager messageManager = CustomEvents.getInstance().getMessageManager();
			String msg = messageManager.getMsg("zuo.canceled");
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage(msg);
			}
		}

	}

	public void joinPlayer(Player player) {
		if (!players.contains(player)) {
			players.add(player);
			bossBar.addPlayer(player);
		}
		player.teleport(eventSpawn);
	}

	public void leavePlayer(Player player) throws PlayerNotInGameException {
		if (!players.contains(player)) throw new PlayerNotInGameException();
		players.remove(player);
		bossBar.removePlayer(player);
		player.teleport(mainSpawn);
	}

	public boolean eventExists(String eventName) {
		return zuoEvents.containsKey(eventName);
	}

	public Set<String> getEventTypes() {
		return zuoEvents.keySet();
	}

	public GameEventStatus getEventStatus() {
		return eventStatus;
	}

	public void setZuoEvents(HashMap<String, SingleZuoEvent> zuoEvents) {
		this.zuoEvents = zuoEvents;
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
