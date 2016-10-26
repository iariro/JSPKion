package kumagai.kion.struts2;

import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import com.microsoft.sqlserver.jdbc.*;
import org.apache.struts2.*;
import org.apache.struts2.convention.annotation.*;
import org.apache.struts2.convention.annotation.Result;
import ktool.datetime.*;
import kumagai.kion.*;
import ktool.xml.*;

/**
 * 時間毎グラフ表示アクション。
 * @author kumagai
 */
@Namespace("/kion")
@Results
({
	@Result(name="success", location="/kion/perhourgraph.jsp"),
	@Result(name="error", location="/kion/error.jsp")
})
public class PerHourGraphAction
{
	static public void main(String [] args)
		throws Exception
	{
		DriverManager.registerDriver(new SQLServerDriver());

		Connection connection =
			DriverManager.getConnection
				("jdbc:sqlserver://localhost:2144;DatabaseName=Kion;User=sa;Password=p@ssw0rd;");

		DateTime startDate = DateTime.parseDateString("2015/08/01");
		DateTime endDate = DateTime.parseDateString("2015/08/31");

		KionCollection kionCollection =
			new KionCollection(connection, 2, startDate, endDate);

		KDocument document =
			new PerHourGraphAction().createDocument(kionCollection);
		connection.close();

		Transformer transformer =
			TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty
			(OutputKeys.OMIT_XML_DECLARATION, "yes");

		document.write(
			transformer,
			new OutputStreamWriter(
				new FileOutputStream("PerHourGraph.xml"), "utf-8"));
	}

	public String start;
	public String end;
	public int location;

	public KionGraphDocument document;

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
	 * 時間毎グラフ表示アクション。
	 * @return 処理結果
	 * @throws Exception
	 */
	@Action("perhourgraph")
	public String execute()
		throws Exception
	{
		ServletContext context = ServletActionContext.getServletContext();

		DriverManager.registerDriver(new SQLServerDriver());

		Connection connection =
			DriverManager.getConnection
				(context.getInitParameter("KionSqlserverUrl"));

		DateTime startDate = DateTime.parseDateString(start);
		DateTime endDate = DateTime.parseDateString(end);

		KionCollection kionCollection =
			new KionCollection(connection, location, startDate, endDate);

		connection.close();

		document = createDocument(kionCollection);

		return "success";
	}

	/**
	 * グラフドキュメント生成
	 * @param kionCollection 気温コレクション
	 * @return グラフドキュメント
	 */
	private KionGraphDocument createDocument(KionCollection kionCollection)
			throws ParserConfigurationException,
			TransformerConfigurationException,
			TransformerFactoryConfigurationError
	{
		ArrayList<Integer> locationCollection =
			kionCollection.getLocationCollection();

		DateTime start = kionCollection.getStartDatetime();
		DateTime end = kionCollection.getEndDatetime();

		int hourStep = 1;

		Kion [][] kionTable =
			kionCollection.getTable
				(locationCollection, start, end, hourStep);

		float fmax = kionCollection.getMaxCelsius();
		float fmin = kionCollection.getMinCelsius();
		int max = (((int)(fmax + 4.5f) / 5) * 5);
		int min = (((int)(fmin - 4.5f) / 5) * 5);

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

		return
			new KionGraphDocument(
				kionTable,
				locationCollection,
				start,
				end,
				max,
				min,
				hourStep,
				new Dimension(5, 550 / height),
				true);
	}
}
