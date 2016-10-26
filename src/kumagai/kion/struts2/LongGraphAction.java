package kumagai.kion.struts2;

import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.xml.transform.*;
import com.microsoft.sqlserver.jdbc.*;
import org.apache.struts2.*;
import org.apache.struts2.convention.annotation.*;
import org.apache.struts2.convention.annotation.Result;
import ktool.datetime.*;
import kumagai.kion.*;

/**
 * 長期グラフ表示アクション。
 * @author kumagai
 */
@Namespace("/kion")
@Result(name="success", location="/kion/longgraph.jsp")
public class LongGraphAction
{
	public int hour;
	public DailyKionGraphDocument document;

	/**
	 * グラフSVGドキュメントを文字列として取得。
	 * @return 文字列によるグラフSVGドキュメント
	 */
	public String getXml()
		throws TransformerFactoryConfigurationError, TransformerException
	{
		// XML書き出し準備。
		Transformer transformer =
			TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

		StringWriter writer = new StringWriter();

		// XML書き出し。
		document.write(transformer, writer);

		return writer.toString();
	}

	/**
	 * 長期グラフ表示アクション。
	 * @return 処理結果
	 * @throws Exception
	 */
	@Action("longgraph")
	public String execute()
		throws Exception
	{
		ServletContext context = ServletActionContext.getServletContext();

		DriverManager.registerDriver(new SQLServerDriver());

		Connection connection =
			DriverManager.getConnection
				(context.getInitParameter("KionSqlserverUrl"));

		int hourStep = 24;

		KionCollection kionCollection =
			new KionCollection(connection, hourStep, hour, 360);

		connection.close();

		ArrayList<Integer> locationCollection =
			kionCollection.getLocationCollection();

		DateTime start = kionCollection.getStartDatetime();
		DateTime end = kionCollection.getEndDatetime();

		Kion [][] kionTable =
			kionCollection.getTable
				(locationCollection, start, end, hourStep);

		float fmax = kionCollection.getMaxCelsius();
		float fmin = kionCollection.getMinCelsius();
		int max = ((int)(fmax + 4.5f) / 5) * 5;
		int min = ((int)(fmin - 4.5f) / 5) * 5;

		if (max > 45)
		{
			// ４５度を超える。

			max = 45;
		}

		int height;

		if (max > min)
		{
			// 最高が最低を上回る。

			height = max - min;
		}
		else
		{
			// 最高が最低を上回らない。

			height = 1;
		}

		document =
			new DailyKionGraphDocument(
				kionTable,
				locationCollection,
				start,
				end,
				max,
				min,
				new Dimension(3, 550 / height),
				true);

		return "success";
	}
}
