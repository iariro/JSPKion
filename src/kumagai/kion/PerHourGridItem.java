package kumagai.kion;

/**
 * 時間ごとの気温表表示アイテム。
 * @author kumagai
 */
public class PerHourGridItem
{
	private final String color;
	private final String date;
	private final String time;
	private final String [] celsius;

	/**
	 * 色を取得。
	 * @return 色
	 */
	public String getColor()
	{
		return color;
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
	public String[] getCelsius()
	{
		return celsius;
	}

	/**
	 * 指定の値をメンバーに割り当てる
	 * @param color 列の色
	 * @param date 日
	 * @param time 時刻
	 * @param celsius 温度
	 */
	public PerHourGridItem
		(String color, String date, String time, String [] celsius)
	{
		this.color = color;
		this.date = date;
		this.time = time;
		this.celsius = celsius;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		String ret = String.format("%s %s /", date, time);

		for (String c : celsius)
		{
			ret += " " + c;
		}

		return ret;
	}
}
