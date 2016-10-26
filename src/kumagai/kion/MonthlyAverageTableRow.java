package kumagai.kion;

/**
 * 月毎平均気温テーブル行データ。
 * @author kumagai
 */
public class MonthlyAverageTableRow
{
	public int year;
	public String [] monthlyCelsius;

	/**
	 * 指定の値をメンバーに割り当てる。
	 * @param year 年
	 */
	public MonthlyAverageTableRow(int year)
	{
		this.year = year;
		this.monthlyCelsius = new String [12];
	}

	/**
	 * 月ごと平均温度をセット。
	 * @param month 月（0-11）
	 * @param celsius 平均温度
	 */
	public void setCelsius(int month, float celsius)
	{
		monthlyCelsius[month] = String.format("%2.2f", celsius);
	}
}
