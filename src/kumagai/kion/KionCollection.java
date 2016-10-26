package kumagai.kion;

import java.sql.*;
import java.sql.Date;
import java.text.*;
import java.util.*;
import com.microsoft.sqlserver.jdbc.*;
import ktool.datetime.*;

/**
 * 気温データコレクション。
 * @author kumagai
 */
public class KionCollection
	extends ArrayList<Kion>
{
	static private final SimpleDateFormat formatDate;
	static private final SimpleDateFormat formatTime;

	static
	{
		formatDate = new SimpleDateFormat();
		formatDate.applyPattern("yyyy/MM/dd");

		formatTime = new SimpleDateFormat();
		formatTime.applyPattern("HH:mm");
	}

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

		KionCollection kionCollection =
			new KionCollection(
				connection,
				2,
				new DateTime(2010, 1, 1, 0, 0, 0),
				new DateTime(2015, 8, 31, 0, 0, 0));

		connection.close();

		ArrayList<MonthlyKionCollection> monthlyKionCollections =
			kionCollection.getMonthlyAverage();

		for (MonthlyKionCollection monthlyKionCollection : monthlyKionCollections)
		{
			System.out.printf("%d/%02d\t%f\n", monthlyKionCollection.year, monthlyKionCollection.month, monthlyKionCollection.getAverage());
		}
	}

	/**
	 * 指定の時刻の毎日の気温を取得。
	 * @param connection DB接続オブジェクト
	 * @param location 地域ID
	 * @param hour 時間
	 * @return 気温コレクション
	 * @throws SQLException
	 */
	public static ArrayList<PerDayGridItem> getKionPerDay
		(Connection connection, int location, int hour)
		throws SQLException
	{
		PreparedStatement statement =
			connection.prepareStatement(
				"select location.name, kion.nichiji, kion.celsius, kion.insertdate from kion join location on kion.location=location.id where location=? and datename(hour, nichiji)=? order by nichiji");

		statement.setInt(1, location);
		statement.setInt(2, hour);

		ResultSet results = statement.executeQuery();

		Date date = new Date(0);
		int bgcolorIndex = 1;

		ArrayList<PerDayGridItem> items = new ArrayList<PerDayGridItem>();

		while (results.next())
		{
			if (date.compareTo(results.getDate("nichiji")) != 0)
			{
				// 日が変わった。

				bgcolorIndex = 1 - bgcolorIndex;
			}

			items.add(new PerDayGridItem(bgcolorIndex, results));

			date = results.getDate("nichiji");
		}

		results.close();
		statement.close();

		return items;
	}

	/**
	 * 空のコレクションを生成する。
	 */
	public KionCollection()
	{
		// 何もしない。
	}

	/**
	 * DB取得値からコレクションを生成する。
	 * @param connection DB接続オブジェクト
	 * @param location 観測地点コード
	 * @param start 開始日
	 * @param end 終了日
	 * @throws SQLException
	 */
	public KionCollection
		(Connection connection, int location, DateTime start, DateTime end)
		throws SQLException
	{
		PreparedStatement statement =
			connection.prepareStatement(
				"select * from kion where location=? and nichiji between ? and ? order by nichiji");

		statement.setInt(1, location);
		statement.setString(2, start.toFullString());
		statement.setString(3, end.toFullString());

		ResultSet resultset = statement.executeQuery();

		while (resultset.next())
		{
			add(new Kion(resultset));
		}

		resultset.close();
		statement.close();
	}

	/**
	 * DB取得値からコレクションを生成する。
	 * @param connection DB接続オブジェクト
	 * @param hourStep 時間間隔
	 * @param hour hourStepによる剰余との時間比較値
	 * @param dayRange 当日を起点とした範囲日
	 * @throws SQLException
	 */
	public KionCollection
		(Connection connection, int hourStep, int hour, int dayRange)
		throws SQLException
	{
		PreparedStatement statement =
			connection.prepareStatement(
				"select * from kion where datename(hour, nichiji) % ?=? and datediff(day, nichiji, getdate()) <= ?");

		statement.setInt(1, hourStep);
		statement.setInt(2, hour);
		statement.setInt(3, dayRange);

		ResultSet resultset = statement.executeQuery();

		while (resultset.next())
		{
			add(new Kion(resultset));
		}

		resultset.close();
		statement.close();
	}

	/**
	 * 観測地点コード一覧を取得する。
	 * @return 観測地点コード一覧
	 */
	public ArrayList<Integer> getLocationCollection()
	{
		ArrayList<Integer> ret = new ArrayList<Integer>();

		for (Kion kion : this)
		{
			if (! ret.contains(kion.location))
			{
				// 初出。

				ret.add(kion.location);
			}
		}

		return ret;
	}

	/**
	 * 指定の観測地点分の気温コレクションを取得する。
	 * @param location 観測地点コード
	 * @return 気温コレクション
	 */
	public ArrayList<Kion> getCollectionByLocation(int location)
	{
		ArrayList<Kion> ret = new ArrayList<Kion>();

		for (Kion kion : this)
		{
			if (kion.location == location)
			{
				// 指定の観測地点分データである。

				ret.add(kion);
			}
		}

		return ret;
	}

	/**
	 * 始点日時取得。
	 * @return 開始日時
	 */
	public DateTime getStartDatetime()
	{
		DateTime ret;

		if (size() > 0)
		{
			// １個でも要素がある。

			DateTime start = new DateTime(get(0).datetime);

			for (Kion kion : this)
			{
				if (start.compareTo(kion.datetime) > 0)
				{
					// さらに過去の情報である。

					start.set(kion.datetime);
				}
			}

			ret =
				new DateTime(
					start.getYear(),
					start.getMonth(),
					start.getDay(),
					start.getHour(),
					0,
					0);
		}
		else
		{
			// １個も要素がない。

			ret = null;
		}

		return ret;
	}

	/**
	 * 終点日時取得。
	 * @return 終点日時
	 */
	public DateTime getEndDatetime()
	{
		DateTime ret;

		if (size() > 0)
		{
			// １個でも要素がある。

			DateTime end = new DateTime(get(0).datetime);

			for (Kion kion : this)
			{
				if (end.compareTo(kion.datetime) < 0)
				{
					// さらに過去の情報である。

					end.set(kion.datetime);
				}
			}

			ret =
				new DateTime(
					end.getYear(),
					end.getMonth(),
					end.getDay(),
					end.getHour(),
					0,
					0);
		}
		else
		{
			// １個も要素がない。

			ret = null;
		}

		return ret;
	}

	/**
	 * 最高温度を取得する。
	 * @return 最高温度
	 */
	public float getMaxCelsius()
	{
		float max = 0;

		if (size() > 0)
		{
			// １個でも要素がある。

			max = get(0).celsius;

			for (Kion kion : this)
			{
				if (max < kion.celsius)
				{
					// より高い温度である。

					max = kion.celsius;
				}
			}
		}

		return max;
	}

	/**
	 * 最低温度を取得する。
	 * @return 最低温度
	 */
	public float getMinCelsius()
	{
		float min = 0;

		if (size() > 0)
		{
			// １個でも要素がある。

			min = get(0).celsius;

			for (Kion kion : this)
			{
				if (min > kion.celsius)
				{
					// より低い温度である。

					min = kion.celsius;
				}
			}
		}

		return min;
	}

	/**
	 * 指定の観測地点・時間の気温を取得する。
	 * @param location 観測地点
	 * @param datetime 時間
	 * @return 気温／null=見つからなかった
	 */
	public Kion getKionByLocationAndDatetime(int location, DateTime datetime)
	{
		for (Kion kion : this)
		{
			if (kion.location == location &&
				kion.datetime.getYear() == datetime.getYear() &&
				kion.datetime.getMonth() == datetime.getMonth() &&
				kion.datetime.getDay() == datetime.getDay() &&
				kion.datetime.getHour() == datetime.getHour())
			{
				// 対象のデータである。

				return kion;
			}
		}

		return null;
	}

	/**
	 * 気温テーブルを生成・返却する。
	 * @param locationCollection 観測地点コレクション
	 * @param start 対象開始日
	 * @param end 対象終了日
	 * @param hourStep 時間間隔
	 * @return 気温テーブル
	 */
	public Kion [][] getTable(ArrayList<Integer> locationCollection,
		DateTime start, DateTime end, int hourStep)
	{
		int hourSize = 0;

		if (start != null && end != null)
		{
			// 始点・終点の指定あり。

			DateTime calendar = new DateTime(start);

			for (hourSize=0 ; calendar.compareTo(end)<=0 ; hourSize++)
			{
				calendar = calendar.makeAdd(new TimeSpan(hourStep, 0, 0));
			}
		}

		// テーブルを確保。
		Kion [][] table = new Kion [hourSize][];
		for (int i=0 ; i<hourSize ; i++)
		{
			table[i] = new Kion [locationCollection.size()];
		}

		// テーブルを埋めていく。
		for (Kion kion : this)
		{
			DateTime datetime =
				new DateTime(
					kion.datetime.getYear(),
					kion.datetime.getMonth(),
					kion.datetime.getDay(),
					kion.datetime.getHour(),
					0,
					0);

			int hour = datetime.diff(start).getHour();

			table[hour / hourStep][locationCollection.indexOf(kion.location)] =
				kion;
		}

		return table;
	}

	/**
	 * 日々の平均気温を計算。
	 * @param start 開始日
	 * @param end 終了日
	 * @return 平均気温の配列
	 */
	public Kion [][] getAverage(DateTime start, DateTime end)
	{
		return getAverage(start, end, 2);
	}

	/**
	 * 日々の平均気温を計算。
	 * @param start 開始日
	 * @param end 終了日
	 * @param idouheikinRange 移動平均法で使用する日数
	 * @return 平均気温の配列
	 */
	public Kion [][] getAverage
		(DateTime start, DateTime end, int idouheikinRange)
	{
		float sum = 0;
		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;
		int count = 0;
		DateTime date2 = null;

		int days = ((end.diff(start).getHour() + 23) / 24) + 1;

		Kion [][] array = new Kion [days][];

		for (int i=0 ; i<days ; i++)
		{
			array[i] = new Kion [4];
		}

		for (int i=0 ; i<size() ; i++)
		{
			Kion kion = get(i);

			if (date2 != null)
			{
				// 比較可能。

				if (!(date2.getYear() == kion.datetime.getYear() &&
					date2.getMonth() == kion.datetime.getMonth() &&
					date2.getDay() == kion.datetime.getDay()))
				{
					// 日付は進んだ。

					int day = date2.diff(start).getDay();

					if (day < days)
					{
						// 範囲内。

						array[day][0] = new Kion(kion.location, date2, min);
						array[day][1] = new Kion(kion.location, date2, max);
						array[day][2] = new Kion(kion.location, date2, sum / count);
					}

					sum = 0;
					count = 0;
					min = Float.MAX_VALUE;
					max = Float.MIN_VALUE;
				}
			}

			sum += kion.celsius;

			if (min > kion.celsius)
			{
				// 最低を下回る。

				min = kion.celsius;
			}

			if (max < kion.celsius)
			{
				// 最高を下回る。

				max = kion.celsius;
			}

			count++;

			date2 = kion.datetime;
		}

		if (date2 != null)
		{
			// 最後のデータ

			int day = date2.diff(start).getDay();

			array[day][0] = new Kion(0, date2, min);
			array[day][1] = new Kion(0, date2, max);
			array[day][2] = new Kion(0, date2, sum / count);
		}

		float [] kionArray = getIdouHeikinArray(array, idouheikinRange);

		for (int i=0 ; i<size() ; i++)
		{
			Kion kion = get(i);

			if (date2 != null)
			{
				// 比較可能。

				if (!(date2.getYear() == kion.datetime.getYear() &&
					date2.getMonth() == kion.datetime.getMonth() &&
					date2.getDay() == kion.datetime.getDay()))
				{
					// 日付は進んだ。

					int day = date2.diff(start).getDay();

					if (day < days)
					{
						// 範囲内。

						array[day][3] =
							new Kion(
								array[day][2].location,
								array[day][2].datetime,
								kionArray[day]);
					}
				}
			}

			date2 = kion.datetime;
		}

		return array;
	}

	/**
	 * 移動平均法による平均値算出。
	 * @param kionCollection 元の気温データ
	 * @param range 加算する日数
	 * @return 移動平均法による平均値
	 */
	private float [] getIdouHeikinArray(Kion [][] kionCollection, int range)
	{
		float [] kionArray;

		ArrayList<ArrayList<Float>> kionArray0 =
			new ArrayList<ArrayList<Float>>(kionCollection.length);

		for (int i=0 ; i<kionCollection.length ; i++)
		{
			kionArray0.add(new ArrayList<Float>());
		}

		for (int i=0 ; i<kionCollection.length ; i++)
		{
			for (int j=-range ; j<=range ; j++)
			{
				if ((i + j >= 0) && (i + j < kionCollection.length))
				{
					// 範囲内

					if (kionCollection[i][2] != null)
					{
						// データあり

						kionArray0.get(i + j).add(kionCollection[i][2].celsius);
					}
				}
			}
		}

		kionArray = new float [kionCollection.length];

		for (int i=0 ; i<kionCollection.length ; i++)
		{
			float sum = 0;

			for (int j=0 ; j<kionArray0.get(i).size() ; j++)
			{
				sum += kionArray0.get(i).get(j);
			}

			kionArray[i] = sum / kionArray0.get(i).size();
		}

		return kionArray;
	}

	/**
	 * 月毎集計
	 * @return 月毎気温コレクション
	 */
	public ArrayList<MonthlyKionCollection> getMonthlyAverage()
	{
		ArrayList<MonthlyKionCollection> monthlyKionCollections =
			new ArrayList<MonthlyKionCollection>();

		MonthlyKionCollection kionCollection = null;

		for (Kion kion : this)
		{
			if (kionCollection == null ||
				(kionCollection.year != kion.datetime.getYear() ||
				kionCollection.month != kion.datetime.getMonth()))
			{
				// 初回または次の月に移った。

				kionCollection =
					new MonthlyKionCollection(
						kion.datetime.getYear(),
						kion.datetime.getMonth());

				monthlyKionCollections.add(kionCollection);
			}

			kionCollection.add(kion);
		}

		return monthlyKionCollections;
	}
}
