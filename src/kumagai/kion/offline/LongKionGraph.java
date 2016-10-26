package kumagai.kion.offline;

import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import ktool.datetime.*;
import kumagai.kion.*;

/**
 * 長期グラフ出力。
 * @author kumagai
 */
public class LongKionGraph
{
	/**
	 * 単体実行エントリポイント。
	 * @param args [0]=時間 [1]=出力ファイルパス [2...]=入力ファイルパス
	 * @throws SQLException
	 * @throws ParserConfigurationException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 * @throws IOException
	 */
	public static void main(String[] args)
		throws SQLException,
		ParserConfigurationException,
		TransformerFactoryConfigurationError,
		TransformerException,
		IOException
	{
		int hour = Integer.parseInt(args[0]);

		KionCollection kionCollection = new KionCollection();
		for (int i=2 ; i<args.length ; i++)
		{
			kionCollection.addAll
				(new DailyKionCollectionFromFile(i - 1, args[i], hour));
		}

		ArrayList<Integer> locationCollection =
			kionCollection.getLocationCollection();

		if (locationCollection.size() > 0)
		{
			// 観測地点は１か所でもある。

			DateTime start = kionCollection.getStartDatetime();
			DateTime end = kionCollection.getEndDatetime();

			Kion [][] kionTable =
				kionCollection.getTable(locationCollection, start, end, 24);

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

			DailyKionGraphDocument document =
				new DailyKionGraphDocument(
					kionTable,
					locationCollection,
					start,
					end,
					max,
					min,
					new Dimension(3, 550 / height),
					true);

			Transformer transformer =
				TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty
				(OutputKeys.OMIT_XML_DECLARATION, "yes");

			document.write(
				transformer,
				new OutputStreamWriter(
					new FileOutputStream(args[1]), "utf-8"));
		}
	}
}
