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
 * 日ごと平均気温表示アクション。
 * @author kumagai
 */
@Namespace("/kion")
@Results
({
	@Result(name="success", location="/kion/dailyaveragetable.jsp"),
	@Result(name="error", location="/kion/error.jsp")
})
public class DailyAverageTableAction
{
	public int location;
	public String start;
	public String end;

	public ArrayList<Kion> average;

	/**
	 * 日ごと平均気温表示アクション。
	 * @return 処理結果
	 * @throws Exception
	 */
	@Action("dailyaveragetable")
	public String execute()
		throws Exception
	{
		ServletContext context = ServletActionContext.getServletContext();

		DriverManager.registerDriver(new SQLServerDriver());

		Connection connection =
			DriverManager.getConnection
				(context.getInitParameter("KionSqlserverUrl"));

		DateTime start = DateTime.parseDateString(this.start);
		DateTime end = DateTime.parseDateString(this.end);

		KionCollection kionCollection =
			new KionCollection(connection, location, start, end);
		connection.close();

		Kion [][] kionTable = kionCollection.getAverage(start, end, 0);

		average = new ArrayList<Kion>();

		for (int i=0 ; i<kionTable.length ; i++)
		{
			if (kionTable[i][2] != null)
			{
				average.add(kionTable[i][2]);
			}
		}

		return "success";
	}
}
