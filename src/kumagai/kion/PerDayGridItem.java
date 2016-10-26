package kumagai.kion;

import java.sql.*;
import java.text.*;

/**
 * 日ごと気温表表示アイテム。
 * @author kumagai
 */
public class PerDayGridItem
{
	static private final String [] bgcolor =
		new String [] {"#ffffff", "#eeeeee"};

	static private final SimpleDateFormat formatDate;
	static private final SimpleDateFormat formatTime;

	static
	{
		formatDate = new SimpleDateFormat();
		formatDate.applyPattern("yyyy/MM/dd");

		formatTime = new SimpleDateFormat();
		formatTime.applyPattern("HH:mm");
	}

	private final String color;
	private final String location;
	private final String date;
	private final String time;
	private final float celsius;
	private final String registerDate;

	/**
	 * １レコードの値をメンバーに割り当てる。
	 * @param bgcolorIndex 背景色インデックス
	 * @param results DBレコードオブジェクト
	 * @throws SQLException
	 */
	public PerDayGridItem(int bgcolorIndex, ResultSet results)
		throws SQLException
	{
		this.color = bgcolor[bgcolorIndex];
		this.location = results.getString("name");
		this.date = formatDate.format(results.getDate("nichiji"));
		this.time = formatTime.format(results.getTime("nichiji"));
		this.celsius = results.getFloat("celsius");

		Date insertdate = results.getDate("insertdate");

		if (insertdate != null)
		{
			// 追加日付あり。

			this.registerDate = formatDate.format(insertdate);
		}
		else
		{
			// 追加日付なし。

			this.registerDate = new String();
		}
	}

	/**
	 * 列の色を取得。
	 * @return color 列の色
	 */
	public String getColor()
	{
		return color;
	}

	/**
	 * 測定位置を取得。
	 * @return 測定位置
	 */
	public String getLocation()
	{
		return location;
	}

	/**
	 * 日を取得。
	 * @return 日
	 */
	public String getDate()
	{
		return date;
	}

	/**
	 * 時刻を取得。
	 * @return 時刻
	 */
	public String getTime()
	{
		return time;
	}

	/**
	 * 温度を取得。
	 * @return 温度
	 */
	public float getCelsius()
	{
		return celsius;
	}

	/**
	 * 登録日を取得。
	 * @return 登録日
	 */
	public String getRegisterDate()
	{
		return registerDate;
	}
}
