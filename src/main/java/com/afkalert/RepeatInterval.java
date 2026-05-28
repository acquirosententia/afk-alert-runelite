package com.afkalert;

public enum RepeatInterval
{
	SECONDS_15(15),
	SECONDS_30(30),
	SECONDS_60(60);

	private final int seconds;

	RepeatInterval(int seconds)
	{
		this.seconds = seconds;
	}

	public int getSeconds()
	{
		return seconds;
	}

	@Override
	public String toString()
	{
		return seconds + "s";
	}
}
