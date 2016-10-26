package kumagai.kion;

import java.util.ArrayList;

/**
 * 温度月毎集計。
 * @author kumagai
 */
public class MonthlyKionCollection
	extends ArrayList<Kion>
{
	public final int year;
	public final int month;

	/**
	 * 指定の値をメンバーに割り当てる
	 * @param year 年
	 * @param month 月
	 */
	public MonthlyKionCollection(int year, int month)
	{
		this.year = year;
		this.month = month;
	}

	/**
	 * 平均を求める。
	 * @return 平均気温
	 */
	public float getAverage()
	{
		float average = 0;

		for (Kion kion : this)
		{
			average += kion.celsius;
		}

		return average / size();
	}
}
