package kumagai.kiontest;

import java.sql.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import com.microsoft.sqlserver.jdbc.*;
import junit.framework.*;
import ktool.datetime.*;
import kumagai.kion.*;

/**
 * DailyKionGraphDocumentのテスト。
 * @author kumagai
 */
public class DailyKionGraphDocumentTest
	extends TestCase
{
	/**
	 * DailyKionGraphDocumentのテスト。
	 * @param args 未使用
	 * @throws ParserConfigurationException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 * @throws IOException
	 * @throws SQLException
	 */
	static public void main(String [] args)
		throws ParserConfigurationException,
		TransformerFactoryConfigurationError, TransformerException, IOException,
		SQLException
	{
		DriverManager.registerDriver(new SQLServerDriver());

		Connection connection =
			DriverManager.getConnection
				("jdbc:sqlserver://localhost:2144;DatabaseName=Kion;User=sa;Password=p@ssw0rd;");

		int hourStep = 24;
		int hour = 7;

		KionCollection kionCollection =
			new KionCollection(connection, hourStep, hour, 24);

		ArrayList<Integer> locationCollection =
			kionCollection.getLocationCollection();

		DateTime start = kionCollection.getStartDatetime();
		DateTime end = kionCollection.getEndDatetime();

		Kion [][] kionTable =
			kionCollection.getTable(locationCollection, start, end, 24);

		for (int i=0 ; i<kionTable.length ; i++)
		{
			System.out.println(kionTable[i][0]);
		}

		DailyKionGraphDocument document =
			new DailyKionGraphDocument(
				kionTable,
				locationCollection,
				start,
				end,
				40,
				15,
				new Dimension(50, 20),
				true);

		Transformer transformer =
			TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

		document.write(
			transformer,
			new OutputStreamWriter(new FileOutputStream("kion.svg"), "utf-8"));
	}

	/**
	 * ５℃の丸めテスト。
	 */
	public void testGodo()
	{
		float f = -5.5f;

		assertEquals(-10, ((int)(f - 5) / 5) * 5);
	}
}
