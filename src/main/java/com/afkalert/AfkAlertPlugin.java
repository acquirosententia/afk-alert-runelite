package com.afkalert;

import com.google.inject.Provides;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import lombok.Getter;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.InteractingChanged;
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

	/** Wall-clock time of the last activity that resets the timer. */
	@Getter
	private Instant lastActivity = Instant.now();

	/** True once the alert has fired for the current idle window (prevents spam). */
	@Getter
	private boolean alertFired = false;

	public boolean isAlertFired() {
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

	// ── Public helpers used by the overlay ────────────────────────────────

	/** Seconds remaining before the alert fires; negative means already expired. */
	public long secondsRemaining()
	{
		long elapsed = Duration.between(lastActivity, Instant.now()).getSeconds();
		return config.timerDuration() - elapsed;
	}

	/** Milliseconds remaining — used by the overlay for sub-second precision. */
	public long millisRemaining()
	{
		long elapsed = Duration.between(lastActivity, Instant.now()).toMillis();
		return (long) config.timerDuration() * 1000 - elapsed;
	}

	// ── Internal ──────────────────────────────────────────────────────────

	void resetTimer()
	{
		lastActivity = Instant.now();
		alertFired = false;
	}

	// ── Game tick: check expiry ────────────────────────────────────────────

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (!alertFired && secondsRemaining() <= 0)
		{
			alertFired = true;
			if (config.sendNotification())
			{
				notifier.notify("AFK Alert: you've been idle too long!");
			}
		}
	}

	// ── Reset triggers ────────────────────────────────────────────────────

	@Subscribe
	public void onStatChanged(StatChanged event)
	{
		if (config.resetOnXpDrop())
		{
			resetTimer();
		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (!config.resetOnAnimation())
		{
			return;
		}
		Actor actor = event.getActor();
		if (actor instanceof Player && actor == client.getLocalPlayer())
		{
			if (actor.getAnimation() != -1)
			{
				resetTimer();
			}
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
	public void onInteractingChanged(InteractingChanged event)
	{
		if (!config.resetOnInteract())
		{
			return;
		}
		if (event.getSource() == client.getLocalPlayer() && event.getTarget() instanceof NPC)
		{
			resetTimer();
		}
	}

	/**
	 * MenuOptionClicked fires on every player-initiated action in the game world —
	 * clicking an NPC, object, item, spell, prayer, etc. Good catch-all for
	 * "any input the player made that would reset auto-retaliate."
	 */
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
