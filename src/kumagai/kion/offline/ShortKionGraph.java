package kumagai.kion.offline;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import ktool.datetime.*;
import kumagai.kion.*;

/**
 * 短期グラフ出力。
 * @author kumagai
 */
public class ShortKionGraph
{
	/**
	 * ドキュメント生成。
	 * @param args [0]=当日を起点とした対象範囲日 [1]=出力ファイルパス [2...]=入力ファイルパス
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws IOException
	 */
	public static void main(String[] args)
		throws ParserConfigurationException,
		TransformerException,
		IOException
	{
		int hourStep = 1;
		int day = Integer.parseInt(args[0]);

		DateTime start0 = new DateTime();
		DateTime end0 = new DateTime();
		end0.add(new TimeSpan(-day * 24, 0, 0));

		KionCollection kionCollection = new KionCollection();

		for (int i=2 ; i<args.length ; i++)
		{
			kionCollection.addAll
				(new KionCollectionFromFile(args[i], i - 1, start0, end0));
		}

		ArrayList<Integer> locationCollection =
			kionCollection.getLocationCollection();

		if (locationCollection.size() > 0)
		{
			// 観測地点は１か所でもある。

			DateTime start = kionCollection.getStartDatetime();
			DateTime end = kionCollection.getEndDatetime();

			Kion [][] kionTable =
				kionCollection.getTable
					(locationCollection, start, end, hourStep);

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

			KionGraphDocument document =
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
