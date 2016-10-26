package kumagai.kiontest;

import java.util.*;
import junit.framework.*;
import ktool.datetime.*;
import kumagai.kion.*;

/**
 * KionCollectionのテスト。
 * @author kumagai
 */
public class KionCollectionTestOffline
	extends TestCase
{
	/**
	 * getLocationCollection()のテスト。
	 */
	public void testGetLocationCollection()
	{
		KionCollection kionCollection = new KionCollection();

		kionCollection.add(new Kion(1, new DateTime(2009, 1, 1, 0, 0, 0), 20));
		kionCollection.add(new Kion(2, new DateTime(2009, 1, 1, 0, 0, 0), 20));
		kionCollection.add(new Kion(1, new DateTime(2009, 1, 1, 0, 0, 0), 20));
		kionCollection.add(new Kion(2, new DateTime(2009, 1, 1, 0, 0, 0), 20));

		ArrayList<Integer> locationCollection =
			kionCollection.getLocationCollection();

		assertEquals(2, locationCollection.size());
		assertEquals(1, (int)locationCollection.get(0));
		assertEquals(2, (int)locationCollection.get(1));
	}

	/**
	 * getCollectionByLocation()のテスト。
	 */
	public void testGetCollectionByLocation()
	{
		KionCollection kionCollection = new KionCollection();

		kionCollection.add(
			new Kion(1, new DateTime(2009, 8, 19, 1, 2, 3), 20));
		kionCollection.add(
			new Kion(2, new DateTime(2009, 8, 19, 1, 2, 3), 21));
		kionCollection.add(
			new Kion(1, new DateTime(2009, 8, 19, 1, 2, 3), 22));
		kionCollection.add(
			new Kion(2, new DateTime(2009, 8, 19, 1, 2, 3), 23));
		kionCollection.add(
			new Kion(1, new DateTime(2009, 8, 19, 1, 2, 3), 24));

		ArrayList<Kion> collection = kionCollection.getCollectionByLocation(1);

		assertEquals(3, collection.size());
		assertEquals(1, collection.get(0).location);
		assertEquals(2009, collection.get(0).datetime.getYear());
		assertEquals(8, collection.get(0).datetime.getMonth());
		assertEquals(19, collection.get(0).datetime.getDay());
		assertEquals(1, collection.get(0).datetime.getHour());
		assertEquals(2, collection.get(0).datetime.getMinute());
		assertEquals(3, collection.get(0).datetime.getSecond());
		assertEquals(20f, collection.get(0).celsius);
	}

	/**
	 * getStartDatetime()のテスト。
	 */
	public void testGetStartDatetime()
	{
		KionCollection kionCollection = new KionCollection();

		kionCollection.add(
			new Kion(1, new DateTime(2009, 8, 20, 0, 0, 0), 21));
		kionCollection.add(
			new Kion(2, new DateTime(2009, 7, 1, 0, 0, 0), 20));
		kionCollection.add(
			new Kion(3, new DateTime(2009, 8, 19, 0, 0, 0), 22));

		DateTime datetime = kionCollection.getStartDatetime();

		assertEquals(2009, datetime.getYear());
		assertEquals(7, datetime.getMonth());
		assertEquals(1, datetime.getDay());
	}

	/**
	 * getEndDatetime()のテスト。
	 */
	public void testGetEndDatetime()
	{
		KionCollection kionCollection = new KionCollection();

		kionCollection.add(
			new Kion(1, new DateTime(2009, 8, 20, 0, 0, 1), 21));
		kionCollection.add(
			new Kion(2, new DateTime(2009, 7, 1, 0, 0, 0), 20));
		kionCollection.add(
			new Kion(3, new DateTime(2009, 8, 19, 0, 0, 0), 22));

		DateTime datetime = kionCollection.getEndDatetime();

		assertEquals(2009, datetime.getYear());
		assertEquals(8, datetime.getMonth());
		assertEquals(20, datetime.getDay());
	}

	/**
	 * getKionByLocationAndDatetime()のテスト。
	 */
	public void testGetKionByLocationAndDatetime()
	{
		KionCollection kionCollection = new KionCollection();

		kionCollection.add(
			new Kion(1, new DateTime(2009, 8, 20, 0, 0, 0), 21));
		kionCollection.add(
			new Kion(2, new DateTime(2009, 7, 1, 0, 0, 0), 20));
		kionCollection.add(
			new Kion(3, new DateTime(2009, 8, 19, 1, 2, 3), 22));

		DateTime calendar = new DateTime(2009, 8, 19, 1, 0, 0);

		Kion kion =
			kionCollection.getKionByLocationAndDatetime(3, calendar);

		assertEquals(3, kion.location);
		assertEquals(2009, kion.datetime.getYear());
		assertEquals(8, kion.datetime.getMonth());
		assertEquals(19, kion.datetime.getDay());
		assertEquals(1, kion.datetime.getHour());
		assertEquals(2, kion.datetime.getMinute());
		assertEquals(3, kion.datetime.getSecond());
		assertEquals(22f, kion.celsius);
	}

	/**
	 * getKionByLocationAndDatetime()のテスト。
	 */
	public void testList1()
	{
		KionCollection kionCollection = new KionCollection();

		kionCollection.add
			(new Kion(1, new DateTime(2009, 8, 19, 0, 0, 0), 21));
		kionCollection.add
			(new Kion(2, new DateTime(2009, 8, 19, 1, 0, 0), 22));
		kionCollection.add
			(new Kion(1, new DateTime(2009, 8, 19, 2, 0, 0), 23));
		kionCollection.add
			(new Kion(2, new DateTime(2009, 8, 19, 3, 0, 0), 24));

		ArrayList<Integer> locationCollection =
			kionCollection.getLocationCollection();

		DateTime start = kionCollection.getStartDatetime();
		DateTime end = kionCollection.getEndDatetime();
		DateTime calendar = new DateTime(start);

		while (calendar.compareTo(end) <= 0)
		{
			System.out.print(calendar + "   ");

			for (int location : locationCollection)
			{
				Kion kion =
					kionCollection.getKionByLocationAndDatetime
						(location, calendar);

				if (kion != null)
				{
					System.out.print(kion.celsius);
				}
				else
				{
					System.out.print("----");
				}
				System.out.print("  ");
			}
			System.out.println();

			calendar.add(new TimeSpan(1));
		}
	}

	/**
	 * getMaxCelsius(), getMinCelsius()のテスト。
	 */
	public void testGetMaxCelsius()
	{
		KionCollection kionCollection = new KionCollection();

		kionCollection.add
			(new Kion(1, new DateTime(2009, 8, 19, 0, 0, 0), -0.5f));
		kionCollection.add
			(new Kion(2, new DateTime(2009, 8, 19, 1, 0, 0), 22));
		kionCollection.add
			(new Kion(1, new DateTime(2009, 8, 19, 2, 0, 0), 23));
		kionCollection.add
			(new Kion(2, new DateTime(2009, 8, 19, 3, 0, 0), 24.5f));

		assertEquals(24.5f, kionCollection.getMaxCelsius());
		assertEquals(-0.5f, kionCollection.getMinCelsius());
	}
}
