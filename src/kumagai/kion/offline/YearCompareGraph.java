package kumagai.kion.offline;

import java.awt.geom.*;
import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import ktool.datetime.*;
import ktool.xml.*;
import kumagai.kion.*;

/**
 * 年ごとの平均気温近似値グラフ描画。
 */
public class YearCompareGraph
{
	/**
	 * 年ごとの平均気温近似値グラフ描画。
	 * @param args [0]=温度ファイルディレクトリ [1]=出力ファイルパス
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws TransformerConfigurationException
	 */
	public static void main(String[] args)
		throws IOException, TransformerException, TransformerConfigurationException, ParserConfigurationException
	{
		int location = 0;

		DateTime now = new DateTime();

		ArrayList<ArrayList<Point2D.Double>> kinjiList =
			new ArrayList<ArrayList<Point2D.Double>>();

		for (int i=0 ; i<5 ; i++) // year loop
		{
			DateTime start = new DateTime(now.getYear() - i, 1, 1, 0, 0, 0);
			DateTime end = new DateTime(now.getYear() - i, 12, 31, 0, 0, 0);

			KionCollection kionCollection =
				new KionCollectionFromFile(args[0], location, start, end);

			Kion [][] kionTable = kionCollection.getAverage(start, end);

			int day = end.diff(start).getDay();

			if (day > 0)
			{
				// １日でもある

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
			int height = (int)(max - min);

			KDocument document =
				new YearCompareGraphDocument(
					kinjiList,
					(float)900 / (float)365,
					(float)550 / (float)height,
					now.getYear(),
					new DateTime(now.getYear(), 1, 1, 0, 0, 0),
					new DateTime(now.getYear(), 12, 31, 0, 0, 0));

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
