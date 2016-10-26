package kumagai.kion.struts2;

import java.sql.*;
import java.util.*;
import javax.servlet.*;
import com.microsoft.sqlserver.jdbc.*;
import org.apache.struts2.*;
import org.apache.struts2.convention.annotation.*;
import org.apache.struts2.convention.annotation.Result;
import ktool.datetime.*;
import kumagai.kion.*;

/**
 * 月毎平均気温表表示アクション。
 * @author kumagai
 */
@Namespace("/kion")
@Results
({
	@Result(name="success", location="/kion/monthlyaveragetable.jsp"),
	@Result(name="error", location="/kion/error.jsp")
})
public class MonthlyAverageTableAction
{
	public int location;
	public String start;
	public String end;
	public MonthlyAverageTableRow [] monthlyAverageTableRows;

	/**
	 * 長期平均気温グラフ表示アクション。
	 * @return 処理結果
	 * @throws Exception
	 */
	@Action("monthlyaveragetable")
	public String execute()
		throws Exception
	{
		ServletContext context = ServletActionContext.getServletContext();

		DriverManager.registerDriver(new SQLServerDriver());

		Connection connection =
			DriverManager.getConnection
				(context.getInitParameter("KionSqlserverUrl"));

		DateTime start0 = null;
		DateTime end0 = null;

		if (start != null && end != null)
		{
			// 開始・終了指定あり。

			start0 = DateTime.parseDateString(start);
			end0 = DateTime.parseDateString(end);
		}

		KionCollection kionCollection =
			new KionCollection(connection, location, start0, end0);

		DateTime startDatetime = kionCollection.getStartDatetime();
		DateTime endDatetime = kionCollection.getEndDatetime();

		ArrayList<MonthlyKionCollection> monthlyKionCollections =
			kionCollection.getMonthlyAverage();

		int yearnum = endDatetime.getYear() - startDatetime.getYear() + 1;

		monthlyAverageTableRows = new MonthlyAverageTableRow [yearnum];

		for (int i=0 ; i<yearnum ; i++)
		{
			monthlyAverageTableRows[i] =
				new MonthlyAverageTableRow(startDatetime.getYear() + i);

			for (int j=0 ; j<12 ; j++)
			{
				MonthlyKionCollection monthlyKionCollection = null;

				for (MonthlyKionCollection monthlyKionCollection2 :
					monthlyKionCollections)
				{
					if (monthlyKionCollection2.year == startDatetime.getYear() + i &&
						monthlyKionCollection2.month == j + 1)
					{
						// 指定の月日を見つけた。

						monthlyKionCollection = monthlyKionCollection2;
						break;
					}
				}

				if (monthlyKionCollection != null)
				{
					// 指定の月日を見つけた。

					monthlyAverageTableRows[i].setCelsius
						(j, monthlyKionCollection.getAverage());
				}
			}
		}

		return "success";
	}
}
