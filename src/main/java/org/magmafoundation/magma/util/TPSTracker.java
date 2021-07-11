package org.magmafoundation.magma.util;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.ChatColor;

import net.minecraft.server.dedicated.DedicatedServer;

/**
 * TPSTracker
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 12/02/2020 - 06:11 am
 */
public class TPSTracker {

	public static ArrayList<Double> tpsValues = new ArrayList();
	public static ArrayList<String> lines = new ArrayList(7);
	private final Timer t = new Timer();

	public void trackTPS() {
		(new Thread(this.schedule(() -> {
			int i;
			if (tpsValues.isEmpty()) {
				for (i = 1; i < (48); ++i) {
					tpsValues.add(20.0D);
				}
			}

			tpsValues.remove(0);
			BigDecimal currentTps = DedicatedServer.currentTps;
			if(currentTps == null){
				return;
			}
			tpsValues.add(Math.ceil(DedicatedServer.currentTps.doubleValue()));
			lines.clear();

			for (i = 0; i < (7); ++i) {
				lines.add("");

				for (double tpsValue : tpsValues) {
					if (i == 0) {
						if (tpsValue >= 18.0D) {
							lines.set(i, ChatColor.GREEN + "|||" + (String) lines.get(i));
						} else {
							lines.set(i, ChatColor.BLACK + "|||" + (String) lines.get(i));
						}
					} else if (i == (1)) {
						if (tpsValue >= 18.0D) {
							lines.set(i, ChatColor.GREEN + "|||" + (String) lines.get(i));
						} else if (tpsValue >= 15.0D) {
							lines.set(i, ChatColor.GREEN + "|||" + (String) lines.get(i));
						} else {
							lines.set(i, ChatColor.BLACK + "|||" + (String) lines.get(i));
						}
					} else if (i == (2)) {
						if (tpsValue >= 18.0D) {
							lines.set(i, ChatColor.GREEN + "|||" + (String) lines.get(i));
						} else if (tpsValue >= 15.0D) {
							lines.set(i, ChatColor.GREEN + "|||" + (String) lines.get(i));
						} else if (tpsValue >= 12.0D) {
							lines.set(i, ChatColor.YELLOW + "|||" + (String) lines.get(i));
						} else {
							lines.set(i, ChatColor.BLACK + "|||" + (String) lines.get(i));
						}
					} else if (i == (3)) {
						if (tpsValue >= 18.0D) {
							lines.set(i, ChatColor.GREEN + "|||" + (String) lines.get(i));
						} else if (tpsValue >= 15.0D) {
							lines.set(i, ChatColor.GREEN + "|||" + (String) lines.get(i));
						} else if (tpsValue >= 12.0D) {
							lines.set(i, ChatColor.YELLOW + "|||" + (String) lines.get(i));
						} else if (tpsValue >= 9.0D) {
							lines.set(i, ChatColor.RED + "|||" + (String) lines.get(i));
						} else {
							lines.set(i, ChatColor.BLACK + "|||" + (String) lines.get(i));
						}
					} else if (i == (4)) {
						if (tpsValue >= 18.0D) {
							lines.set(i, ChatColor.GREEN + "|||" + (String) lines.get(i));
						} else if (tpsValue >= 15.0D) {
							lines.set(i, ChatColor.GREEN + "|||" + (String) lines.get(i));
						} else if (tpsValue >= 12.0D) {
							lines.set(i, ChatColor.YELLOW + "|||" + (String) lines.get(i));
						} else if (tpsValue >= 9.0D) {
							lines.set(i, ChatColor.RED + "|||" + (String) lines.get(i));
						} else if (tpsValue >= 6.0D) {
							lines.set(i, ChatColor.DARK_RED + "|||" + (String) lines.get(i));
						} else {
							lines.set(i, ChatColor.BLACK + "|||" + (String) lines.get(i));
						}
					} else if (i == (5)) {
						if (tpsValue >= 18.0D) {
							lines.set(i, ChatColor.GREEN + "|||" + (String) lines.get(i));
						} else if (tpsValue >= 15.0D) {
							lines.set(i, ChatColor.GREEN + "|||" + (String) lines.get(i));
						} else if (tpsValue >= 12.0D) {
							lines.set(i, ChatColor.YELLOW + "|||" + (String) lines.get(i));
						} else if (tpsValue >= 9.0D) {
							lines.set(i, ChatColor.RED + "|||" + (String) lines.get(i));
						} else if (tpsValue >= 6.0D) {
							lines.set(i, ChatColor.DARK_RED + "|||" + (String) lines.get(i));
						} else if (tpsValue >= 3.0D) {
							lines.set(i, ChatColor.DARK_GRAY + "|||" + (String) lines.get(i));
						} else {
							lines.set(i, ChatColor.BLACK + "|||" + (String) lines.get(i));
						}
					} else if (i == (6)) {
						if (tpsValue >= 18.0D) {
							lines.set(i, ChatColor.GREEN + "|||" + (String) lines.get(i));
						} else if (tpsValue >= 15.0D) {
							lines.set(i, ChatColor.GREEN + "|||" + (String) lines.get(i));
						} else if (tpsValue >= 12.0D) {
							lines.set(i, ChatColor.YELLOW + "|||" + (String) lines.get(i));
						} else if (tpsValue >= 9.0D) {
							lines.set(i, ChatColor.RED + "|||" + (String) lines.get(i));
						} else if (tpsValue >= 6.0D) {
							lines.set(i, ChatColor.DARK_RED + "|||" + (String) lines.get(i));
						} else if (tpsValue >= 3.0D) {
							lines.set(i, ChatColor.DARK_GRAY + "|||" + (String) lines.get(i));
						} else if (tpsValue >= 2.0D) {
							lines.set(i, ChatColor.BLACK + "|||" + (String) lines.get(i));
						} else {
							lines.set(i, ChatColor.BLACK + "|||" + (String) lines.get(i));
						}
					}
				}
			}

		}, 1000L))).start();
	}

	private TimerTask schedule(Runnable r, long delay) {
		final TimerTask task = new TimerTask() {
			@Override
			public void run() {
				r.run();
			}
		};
		this.t.scheduleAtFixedRate(task, delay, delay);
		return task;
	}

}

