package com.afkalert;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup(AfkAlertConfig.GROUP)
public interface AfkAlertConfig extends Config
{
	String GROUP = "afkalert";

	// ── Timer ──────────────────────────────────────────────────────────────

	@ConfigItem(
		keyName = "timerDuration",
		name = "Timer duration (seconds)",
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
		return true;
	}

	@ConfigItem(
		keyName = "resetOnAnimation",
		name = "Reset on player animation",
		description = "Resets the timer when the player's animation changes (attack swing, skill action, etc.).",
		position = 2
	)
	default boolean resetOnAnimation()
	{
		return true;
	}

	@ConfigItem(
		keyName = "resetOnHitsplat",
		name = "Reset on hitsplat received",
		description = "Resets the timer when the player takes damage (auto-retaliate would engage).",
		position = 3
	)
	default boolean resetOnHitsplat()
	{
		return true;
	}

	@ConfigItem(
		keyName = "resetOnInteract",
		name = "Reset on NPC interaction",
		description = "Resets the timer when the player targets an NPC (click-to-attack, auto-retaliate pick).",
		position = 4
	)
	default boolean resetOnInteract()
	{
		return false;
	}

	@ConfigItem(
		keyName = "resetOnMenuClick",
		name = "Reset on any player input",
		description = "Catch-all: resets the timer on any menu click or player-initiated action in the game world.",
		position = 5
	)
	default boolean resetOnMenuClick()
	{
		return false;
	}

	// ── Alerts ────────────────────────────────────────────────────────────

	@ConfigItem(
		keyName = "sendNotification",
		name = "Send tray notification",
		description = "Sends a RuneLite notification popup when the timer expires.",
		position = 6
	)
	default boolean sendNotification()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showCountdown",
		name = "Show countdown timer",
		description = "Shows remaining time in the corner; blinks when the alert fires.",
		position = 7
	)
	default boolean showCountdown()
	{
		return true;
	}
}
