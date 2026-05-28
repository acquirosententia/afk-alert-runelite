package com.afkalert;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup(AfkAlertConfig.GROUP)
public interface AfkAlertConfig extends Config
{
	String GROUP = "afkalert";

	// ── Sections ───────────────────────────────────────────────────────────

	@ConfigSection(
		name = "Notification Settings",
		description = "Tray notification settings.",
		position = 5,
		closedByDefault = false
	)
	String notificationsSection = "notifications";

	// ── Timer ──────────────────────────────────────────────────────────────

	@ConfigItem(
		keyName = "timerDuration",
		name = "Timer duration",
		description = "Seconds of inactivity before the alert fires. Default 1140 = 19 min (just before the 20-min logout).",
		position = 0
	)
	@Range(min = 10, max = 1200)
	@Units(Units.SECONDS)
	default int timerDuration()
	{
		return 1140;
	}

	// ── Reset triggers ─────────────────────────────────────────────────────

	@ConfigItem(
		keyName = "resetOnXpDrop",
		name = "Reset on XP drop",
		description = "Resets the timer whenever the player gains XP (landed a hit or completed a skill action).",
		position = 1
	)
	default boolean resetOnXpDrop()
	{
		return false;
	}

	@ConfigItem(
		keyName = "resetOnHitsplat",
		name = "Reset on hitsplat received",
		description = "Resets the timer when the player takes damage (auto-retaliate would engage).",
		position = 2
	)
	default boolean resetOnHitsplat()
	{
		return false;
	}

	@ConfigItem(
		keyName = "resetOnMenuClick",
		name = "Reset on any player input",
		description = "Catch-all: resets the timer on any menu click or player-initiated action in the game world.",
		position = 3
	)
	default boolean resetOnMenuClick()
	{
		return true;
	}

	// ── Alerts ────────────────────────────────────────────────────────────

	@ConfigItem(
		keyName = "sendNotification",
		name = "Send tray notification",
		description = "Sends a RuneLite notification popup when the timer expires.",
		position = 0,
		section = notificationsSection
	)
	default boolean sendNotification()
	{
		return true;
	}

	@ConfigItem(
		keyName = "repeatNotification",
		name = "Repeat notification",
		description = "Re-fires the tray notification (and flashes the taskbar) repeatedly until you act.",
		position = 1,
		section = notificationsSection
	)
	default boolean repeatNotification()
	{
		return true;
	}

	@ConfigItem(
		keyName = "repeatInterval",
		name = "Repeat interval",
		description = "How often to re-fire the notification while you're still idle.",
		position = 2,
		section = notificationsSection
	)
	default RepeatInterval repeatInterval()
	{
		return RepeatInterval.SECONDS_30;
	}

	@ConfigItem(
		keyName = "showCountdown",
		name = "Show countdown timer",
		description = "Shows remaining time in the corner; blinks when the alert fires.",
		position = 4
	)
	default boolean showCountdown()
	{
		return true;
	}
}
