package kumagai.kion;

import java.sql.*;
import java.text.*;
import ktool.datetime.*;

/**
 * 気温データ。
 */
public class Kion
{
	static SimpleDateFormat formatDate;

	/**
	 * 日付書式情報生成。
	 */
	static
	{
		formatDate = new SimpleDateFormat();
		formatDate.applyPattern("yyyy/MM/dd HH:mm");
	}

	public final int location;
	public final DateTime datetime;
	public final float celsius;

	/**
	 * 指定の値から気温データを構築する。
	 * @param location 観測地点コード
	 * @param fields CSVを分解したフィールドの配列
	 * @throws StringIndexOutOfBoundsException
	 */
	public Kion(int location, String [] fields)
		throws StringIndexOutOfBoundsException
	{
		this.location = location;

		this.datetime =
			new DateTime(
				Integer.valueOf(fields[1].substring(6, 10)),
				Integer.valueOf(fields[1].substring(3, 5)),
				Integer.valueOf(fields[1].substring(0, 2)),
				Integer.valueOf(fields[1].substring(11, 13)),
				Integer.valueOf(fields[1].substring(14, 16)),
				Integer.valueOf(fields[1].substring(17, 19)));

		this.celsius = Float.valueOf(fields[2]);
	}

	/**
	 * 指定の値を割り当て気温データを構築する。
	 * @param location 観測地点コード
	 * @param datetime 日付
	 * @param celsius 温度
	 */
	public Kion(int location, DateTime datetime, float celsius)
	{
		this.location = location;
		this.datetime = datetime;
		this.celsius = celsius;
	}

	/**
	 * DB取得値から気温データを構築する。
	 * @param resultset DBレコード
	 * @throws SQLException
	 */
	public Kion(ResultSet resultset)
		throws SQLException
	{
		location = resultset.getInt(1);

		DateTime date = new DateTime(resultset.getDate("nichiji"));
		DateTime time = new DateTime(resultset.getTime("nichiji"));

		datetime =
			new DateTime(
				date.getYear(),
				date.getMonth(),
				date.getDay(),
				time.getHour(),
				time.getMinute(),
				time.getSecond());

		celsius = resultset.getFloat("celsius");
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return String.format("%s:%s %s", location, datetime, celsius);
	}

	/**
	 * 温度を２．２桁形式で取得する。
	 * @return ２．２桁形式の温度
	 */
	public String getCelsius2keta()
	{
		return String.format("%2.2f", celsius);
	}
}
