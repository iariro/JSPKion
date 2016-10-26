package kumagai.kiontest;

import ktool.datetime.*;

public class EtcTest
{
	public static void main(String[] args)
	{
		DateTime calendar = new DateTime();

		System.out.println(calendar.toString());
		System.out.println(calendar.toTimeString());
	}
}
