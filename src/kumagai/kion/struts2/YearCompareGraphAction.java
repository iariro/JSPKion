package kumagai.kion.struts2;

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
import ktool.xml.*;
import kumagai.kion.*;

/**
 * 年単位の比較グラフ表示アクション。
 * @author kumagai
 */
@Namespace("/kion")
@Results
({
	@Result(name="success", location="/kion/yearcomparegraph.jsp"),
	@Result(name="error", location="/kion/error.jsp")
})
public class YearCompareGraphAction
{
	/**
	 * テストコード。
	 * @param args 未使用
	 * @throws SQLException
	 */
	public static void main(String[] args)
		throws Exception
	{
		DriverManager.registerDriver(new SQLServerDriver());

		Connection connection =
			DriverManager.getConnection
				("jdbc:sqlserver://localhost:2144;DatabaseName=Kion;User=sa;Password=p@ssw0rd;");

		KDocument document =
			new YearCompareGraphAction().createDocument
				(connection, 2, "8/1", "8/31", 5, 1, 2);
		connection.close();

		Transformer transformer =
			TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty
			(OutputKeys.OMIT_XML_DECLARATION, "yes");

		document.write(
			transformer,
			new OutputStreamWriter(
				new FileOutputStream("YearCompareGraph.xml"), "utf-8"));
	}

	public int location;
	public int yearNum;
	public int kinjiType;
	public int idouheikinRange;
	public String startMonthDay;
	public String endMonthDay;
	public KDocument document;

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
	 * 年単位の比較グラフ表示アクション。
	 * @return 処理結果
	 * @throws Exception
	 */
	@Action("yearcomparegraph")
	public String execute()
		throws Exception
	{
		ServletContext context = ServletActionContext.getServletContext();

		DriverManager.registerDriver(new SQLServerDriver());

		Connection connection =
			DriverManager.getConnection
				(context.getInitParameter("KionSqlserverUrl"));

		document =
			createDocument(
				connection,
				location,
				startMonthDay,
				endMonthDay,
				yearNum,
				kinjiType,
				idouheikinRange);

		connection.close();

		if (document != null)
		{
			return "success";
		}
		else
		{
			return "error";
		}
	}

	/**
	 * グラフSVGオブジェクトを生成。
	 * @param connection DB接続オブジェクト
	 * @param location 観測地点コード
	 * @param startMonthDay 開始月日
	 * @param endMonthDay 終了月日
	 * @param yearNum 年数
	 * @param type 0=近似曲線／1=移動平均法
	 * @param idouheikinRange 移動平均法の際の足し合わせる日数
	 * @return グラフSVGオブジェクト
	 * @throws Exception
	 */
	public KDocument createDocument(Connection connection, int location,
		String startMonthDay, String endMonthDay, int yearNum, int type,
		int idouheikinRange)
		throws Exception
	{
		DateTime now = new DateTime();

		ArrayList<ArrayList<Point2D.Double>> kinjiList =
			new ArrayList<ArrayList<Point2D.Double>>();

		DateTime startDate =
			DateTime.parseDateString
				(String.format("%s/%s", now.getYear(), startMonthDay));
		DateTime endDate =
			DateTime.parseDateString
				(String.format("%s/%s", now.getYear(), endMonthDay));
		int range = endDate.diff(startDate).getDay();

		for (int i=0 ; i<yearNum ; i++) // year loop
		{
			DateTime start =
				DateTime.parseDateString(
					String.format("%d/%s", now.getYear() - i, startMonthDay));
			DateTime end =
				DateTime.parseDateString(
					String.format("%d/%s", now.getYear() - i, endMonthDay));

			KionCollection kionCollection =
				new KionCollection(connection, location, start, end);

			Kion [][] kionTable =
				kionCollection.getAverage(start, end, idouheikinRange);

			int day = end.diff(start).getDay();

			if (day > 0)
			{
				// １日でもある

				if (type == 0)
				{
					// 近似曲線

					double kinjix [] = new double [day];
					double kinjiy [] = new double [day];

					for (int j=0 ; j<day ; j++)
					{
						if (kionTable[j][2] != null)
						{
							// データはある。

							kinjix[j] = j;
							kinjiy[j] = kionTable[j][2].celsius;
						}
					}

					for ( ; (kinjix[day-1] == 0) && (day - 1 > 0) ; day--)
					{
					}

					// 近似値を求める。
					kinjiList.add(
						SaishouNijouhou.getKinji(kinjix, kinjiy, 5, 5, day));
				}
				else
				{
					// 移動平均法

					ArrayList<Point2D.Double> list =
						new ArrayList<Point2D.Double>();

					for (int j=0 ; j<kionTable.length ; j++)
					{
						if (kionTable[j][3] != null)
						{
							// データあり。

							list.add(
								new Point2D.Double(j, kionTable[j][3].celsius));
						}
					}

					kinjiList.add(list);
				}
			}
		}

		Float max = null;
		Float min = null;

		for (int i=0 ; i<kinjiList.size() ; i++)
		{
			for (int j=0 ; j<kinjiList.get(i).size() ; j++)
			{
				if (max == null || kinjiList.get(i).get(j).y > max)
				{
					// 最大値を上回る。

					max = (float)kinjiList.get(i).get(j).y;
				}

				if (min == null || kinjiList.get(i).get(j).y < min)
				{
					// 最小値を下回る。

					min = (float)kinjiList.get(i).get(j).y;
				}
			}
		}

		if (max != null && min != null)
		{
			// 最大値・最小値はあった。

			int height = (int)(((max + 4) / 5) * 5 - ((min - 4) / 5) * 5);

			KDocument document =
				new YearCompareGraphDocument(
					kinjiList,
					(float)900 / (float)range,
					(float)550 / (float)height,
					now.getYear(),
					startDate,
					endDate);

			return document;
		}
		else
		{
			// 最大値・最小値なし。

			return null;
		}
	}
}
