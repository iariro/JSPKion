package kumagai.kion;

import java.sql.*;
import java.util.*;
import com.microsoft.sqlserver.jdbc.*;
import ktool.datetime.*;

/**
 * 時間ごと表データ。
 */
public class PerHourGridItemCollection
{
	/**
	 * テストコード。
	 * @param args 未使用
	 * @throws SQLException
	 */
	public static void main(String[] args)
		throws SQLException
	{
		DriverManager.registerDriver(new SQLServerDriver());

		Connection connection =
			DriverManager.getConnection
				("jdbc:sqlserver://localhost:2144;DatabaseName=Kion;User=sa;Password=p@ssw0rd;");

		int hourStep = 1;
		int hour = 0;

		KionCollection kionCollection =
			new KionCollection(connection, hourStep, hour, 60);

		connection.close();

		PerHourGridItemCollection gridItems =
			new PerHourGridItemCollection(hourStep, kionCollection);

		for (PerHourGridItem item : gridItems.items)
		{
			System.out.println(item);
		}
	}

	public boolean exists;
	public PerHourGridItem [] items;

	/**
	 * 表データを構築する
	 * @param hourStep 時間間隔
	 * @param kionCollection 気温データコレクション
	 */
	public PerHourGridItemCollection
		(int hourStep, KionCollection kionCollection)
	{
		if (kionCollection.size() > 0)
		{
			// １個でも温度情報あり。

			DateTime start = kionCollection.getStartDatetime();
			DateTime end = kionCollection.getEndDatetime();

			ArrayList<Integer> locationCollection =
				kionCollection.getLocationCollection();

			Kion [][] kionTable =
				kionCollection.getTable
					(locationCollection, start, end, hourStep);

			DateTime calendar = new DateTime(start);

			DateTime calendar2 = new DateTime();
			int bgcolorIndex = 1;
			String [] bgcolor = new String [] {"#ffffff", "#eeeeee"};

			int days = end.diff(start).getDay();

			items = new PerHourGridItem [days];

			for (int i=0 ; calendar.compareTo(end)<=0 ; i++)
			{
				if (calendar2.getDay() != calendar.getDay())
				{
					// 日が変わった。

					bgcolorIndex = 1 - bgcolorIndex;
				}

				String [] celsius = new String [locationCollection.size()];

				for (int j=0 ; j<locationCollection.size() ; j++)
				{
					Kion kion = kionTable[i][j];

					if (kion != null)
					{
						// 温度情報あり。

						celsius[j] = Float.toString(kion.celsius);
					}
					else
					{
						// 温度情報なし。

						celsius[j] = new String();
					}
				}

				if (i < items.length)
				{
					// 範囲内である。

					items[i] =
						new PerHourGridItem(
							bgcolor[bgcolorIndex],
							calendar.toString(),
							calendar.toTimeString(),
							celsius);
				}

				calendar2.set(calendar);
				calendar.add(new TimeSpan(24, 0, 0));
			}

			exists = true;
		}
		else
		{
			// １個も温度情報なし。

			exists = false;
		}
	}
}
