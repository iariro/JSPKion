package kumagai.kion.struts2;

import java.sql.*;
import java.util.*;
import javax.servlet.*;
import com.microsoft.sqlserver.jdbc.*;
import org.apache.struts2.*;
import org.apache.struts2.convention.annotation.*;
import kumagai.kion.*;

/**
 * 日ごとの気温表表示アクション。
 * @author kumagai
 */
@Namespace("/kion")
@Result(name="success", location="/kion/perday2.jsp")
public class PerDayAction
{
	public int location;
	public int hour;
	public ArrayList<PerDayGridItem> items;

	/**
	 * 表内容のサイズを取得。
	 * @return 表内容のサイズ
	 */
	public int getSize()
	{
		return items.size();
	}

	/**
	 * 日ごとの気温表表示アクション。
	 * @return 処理結果
	 * @throws Exception
	 */
	@Action("perday")
	public String execute()
		throws Exception
	{
		ServletContext context = ServletActionContext.getServletContext();

		DriverManager.registerDriver(new SQLServerDriver());

		Connection connection =
			DriverManager.getConnection
				(context.getInitParameter("KionSqlserverUrl"));

		items = KionCollection.getKionPerDay(connection, location, hour);

		connection.close();

		return "success";
	}
}
