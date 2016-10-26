package kumagai.kiontest;

import java.sql.*;
import java.util.*;
import com.microsoft.sqlserver.jdbc.*;
import junit.framework.*;
import kumagai.kion.*;
import ktool.datetime.*;

/**
 * KionCollectionのテスト。
 * @author kumagai
 */
public class KionCollectionTestOnline
	extends TestCase
{
	/**
	 * DBからのリスト取得。getKionByLocationAndDatetime()方式。
	 * @throws SQLException
	 */
	public void testListFromDb1()
		throws SQLException
	{
		DriverManager.registerDriver(new SQLServerDriver());

		Connection connection =
			DriverManager.getConnection
				("jdbc:sqlserver://localhost:2144;DatabaseName=Kion;User=sa;Password=p@ssw0rd;");

		KionCollection kionCollection = new KionCollection(connection, 2, 1, 10);

		ArrayList<Integer> locationCollection =
			kionCollection.getLocationCollection();

		DateTime start = kionCollection.getStartDatetime();
		DateTime end = kionCollection.getEndDatetime();
		DateTime calendar = new DateTime(start);

		long startTime = System.currentTimeMillis();

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

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	/**
	 * DBからのリスト取得。getTable()方式。
	 * @throws SQLException
	 */
	public void testListFromDb2()
		throws SQLException
	{
		DriverManager.registerDriver(new SQLServerDriver());

		Connection connection =
			DriverManager.getConnection
				("jdbc:sqlserver://localhost:2144;DatabaseName=Kion;User=sa;Password=p@ssw0rd;");

		int hourStep = 24;
		int hour = 7;

		KionCollection kionCollection =
			new KionCollection(connection, hourStep, hour, 30);

		ArrayList<Integer> locationCollection =
			kionCollection.getLocationCollection();

		DateTime start = kionCollection.getStartDatetime();
		DateTime end = kionCollection.getEndDatetime();

		Kion [][] kionTable =
			kionCollection.getTable(locationCollection, start, end, hourStep);

		DateTime calendar = new DateTime(start);

		long startTime = System.currentTimeMillis();

		for (int i=0 ; calendar.compareTo(end)<=0 ; i++)
		{
			System.out.print(calendar + "   ");

			for (int j=0 ; j<locationCollection.size() ; j++)
			{
				Kion kion = kionTable[i / hourStep][j];

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

			calendar.add(new TimeSpan(1, 0, 0));
		}

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
}
