package kumagai.kion.struts2;

import java.awt.*;
import java.awt.geom.*;
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
 * 長期平均気温グラフ表示アクション。
 * @author kumagai
 */
@Namespace("/kion")
@Results
({
	@Result(name="success", location="/kion/longgraph.jsp"),
	@Result(name="error", location="/kion/error.jsp")
})
public class LongAverageGraphAction
{
	public int location;
	public String start;
	public String end;
	public int idouheikinRange;
	public AverageKionGraphDocument document;

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
	 * 長期平均気温グラフ表示アクション。
	 * @return 処理結果
	 * @throws Exception
	 */
	@Action("longaveragegraph")
	public String execute()
		throws Exception
	{
		ServletContext context = ServletActionContext.getServletContext();

		DriverManager.registerDriver(new SQLServerDriver());

		Connection connection =
			DriverManager.getConnection
				(context.getInitParameter("KionSqlserverUrl"));

		DateTime start0;
		DateTime end0;

		if (start != null && end != null)
		{
			// 開始・終了指定あり。

			start0 = DateTime.parseDateString(start);
			end0 = new DateTime(end + " 23:00:00");
		}
		else
		{
			// 開始・終了指定なし。

			start0 = new DateTime();
			end0 = new DateTime();

			for (int i=0 ; i<60 ; i++)
			{
				start0.add(new TimeSpan(-24, 0, 0));
			}
		}

		KionCollection kionCollection =
			new KionCollection(connection, location, start0, end0);

		connection.close();

		if (kionCollection.size() > 0)
		{
			// １個でもデータあり。

			DateTime start = kionCollection.getStartDatetime();
			DateTime end = kionCollection.getEndDatetime();

			int day = end.diff(start).getDay();

			if (day > 0)
			{
				// １日でもある

				Kion [][] kionTable =
					kionCollection.getAverage(start, end, idouheikinRange);

				float fmax = kionCollection.getMaxCelsius();
				float fmin = kionCollection.getMinCelsius();
				int max = ((int)(fmax + 4.5f) / 5) * 5;
				int min = ((int)(fmin - 4.5f) / 5) * 5;

				int height;

				if (max > min)
				{
					// 最高気温＞最低気温である。

					height = max - min;
				}
				else
				{
					// 最高気温・最低気温に差がない。

					height = 1;
				}

				double kinjix [] = new double [day];
				double kinjiy [] = new double [day];

				for (int i=0 ; i<day ; i++)
				{
					kinjix[i] = i;
					kinjiy[i] = kionTable[i][2].celsius;
				}

				// 近似値を求める。
				ArrayList<Point2D.Double> kinjiList =
					SaishouNijouhou.getKinji(kinjix, kinjiy, 5, 5, day);

				document =
					new AverageKionGraphDocument(
						kionTable,
						kinjiList,
						start,
						end,
						max,
						min,
						new Dimension(800 / day, 550 / height));

				return "success";
			}
			else
			{
				// １日もない

				return "error";
			}
		}
		else
		{
			// １個もデータなし。

			return "error";
		}
	}
}
