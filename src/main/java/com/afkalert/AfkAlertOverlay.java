package com.afkalert;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

public class AfkAlertOverlay extends OverlayPanel
{
	private static final int FLASH_PERIOD_MS = 600;
	private static final Color COUNTDOWN_SAFE = new Color(255, 255, 255, 180);
	private static final Color COUNTDOWN_WARN = new Color(255, 180, 0, 220);
	private static final Color COUNTDOWN_URGENT = new Color(255, 50, 50, 255);

	private final AfkAlertPlugin plugin;
	private final AfkAlertConfig config;

	@Inject
	AfkAlertOverlay(AfkAlertPlugin plugin, AfkAlertConfig config)
	{
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(AfkAlertOverlay.PRIORITY_HIGH);
	}

	@Override
	public Dimension render(Graphics2D g)
	{
		if (!config.showCountdown())
		{
			return null;
		}

		panelComponent.getChildren().clear();

		if (plugin.isAlertFired())
		{
			long phase = (System.currentTimeMillis() / FLASH_PERIOD_MS) % 2;
			if (phase == 0)
			{
				return null;
			}
			panelComponent.getChildren().add(LineComponent.builder()
				.left("AFK!")
				.leftColor(COUNTDOWN_URGENT)
				.build());
		}
		else
		{
			long millis = plugin.millisRemaining();
			long totalSecs = millis / 1000;
			long mins = totalSecs / 60;
			long secs = totalSecs % 60;

			Color color = totalSecs <= 10 ? COUNTDOWN_URGENT
				: totalSecs <= 30          ? COUNTDOWN_WARN
				:                            COUNTDOWN_SAFE;

			String time;
			if (totalSecs <= 10)
			{
				long tenths = (millis % 1000) / 100;
				time = String.format("%d:%02d.%d", mins, secs, tenths);
			}
			else
			{
				time = String.format("%d:%02d", mins, secs);
			}

			panelComponent.getChildren().add(LineComponent.builder()
				.left("AFK")
				.right(time)
				.rightColor(color)
				.build());
		}

		return super.render(g);
	}
}
