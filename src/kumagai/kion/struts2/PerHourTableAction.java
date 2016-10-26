package kumagai.kion.struts2;

import java.sql.*;
import javax.servlet.*;
import com.microsoft.sqlserver.jdbc.*;
import org.apache.struts2.*;
import org.apache.struts2.convention.annotation.*;
import kumagai.kion.*;

/**
 * 時間ごとの気温表表示アクション。
 * @author kumagai
 */
@Namespace("/kion")
@Result(name="success", location="/kion/perhourtable.jsp")
public class PerHourTableAction
{
	public boolean exists;
	public PerHourGridItem [] items;

	/**
	 * 時間ごとの気温表表示アクション。
	 * @return 処理結果
	 * @throws Exception
	 */
	@Action("perhourtable")
	public String execute()
		throws Exception
	{
		ServletContext context = ServletActionContext.getServletContext();

		DriverManager.registerDriver(new SQLServerDriver());

		Connection connection =
			DriverManager.getConnection
				(context.getInitParameter("KionSqlserverUrl"));

		int hourStep = 1;
		int hour = 0;

		KionCollection kionCollection =
			new KionCollection(connection, hourStep, hour, 60);

		connection.close();

		PerHourGridItemCollection gridItems =
			new PerHourGridItemCollection(hourStep, kionCollection);

		exists = gridItems.exists;
		items = gridItems.items;

		return "success";
	}
}
