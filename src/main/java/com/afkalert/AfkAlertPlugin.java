package com.afkalert;

import com.google.inject.Provides;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.Notifier;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "AFK Alert",
	description = "Alerts you when you've been inactive too long; resets on combat actions that re-engage auto-retaliate.",
	tags = {"afk", "alert", "timer", "combat", "auto retaliate", "idle"}
)
public class AfkAlertPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private AfkAlertConfig config;

	@Inject
	private Notifier notifier;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private AfkAlertOverlay overlay;

	private Instant lastActivity = Instant.now();

	private boolean alertFired = false;

	private Instant lastNotified = Instant.MIN;

	public boolean isAlertFired()
	{
		return alertFired;
	}

	@Override
	protected void startUp()
	{
		resetTimer();
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
	}

	public long secondsRemaining()
	{
		long elapsed = Duration.between(lastActivity, Instant.now()).getSeconds();
		return config.timerDuration() - elapsed;
	}

	public long millisRemaining()
	{
		long elapsed = Duration.between(lastActivity, Instant.now()).toMillis();
		return (long) config.timerDuration() * 1000 - elapsed;
	}

	void resetTimer()
	{
		lastActivity = Instant.now();
		alertFired = false;
		lastNotified = Instant.MIN;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (secondsRemaining() > 0)
		{
			return;
		}

		if (!alertFired)
		{
			alertFired = true;
			sendNotification();
			return;
		}

		if (config.repeatNotification()
			&& Duration.between(lastNotified, Instant.now()).getSeconds() >= config.repeatInterval().getSeconds())
		{
			sendNotification();
		}
	}

	private void sendNotification()
	{
		lastNotified = Instant.now();
		if (config.sendNotification())
		{
			notifier.notify("AFK Alert: you've been idle too long!");
		}
	}

	@Subscribe
	public void onStatChanged(StatChanged event)
	{
		if (config.resetOnXpDrop())
		{
			resetTimer();
		}
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		if (!config.resetOnHitsplat())
		{
			return;
		}
		if (event.getActor() instanceof Player && event.getActor() == client.getLocalPlayer())
		{
			resetTimer();
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (config.resetOnMenuClick())
		{
			resetTimer();
		}
	}

	@Provides
	AfkAlertConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AfkAlertConfig.class);
	}
}
