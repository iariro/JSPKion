package kumagai.kion.offline;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import ktool.datetime.*;
import kumagai.kion.*;

/**
 * 平均気温グラフ出力。
 * @author kumagai
 */
public class AverageKionGraph
{
	/**
	 * ドキュメント生成。
	 * @param args [0]=当日を起点とした対象範囲日 [1]=遡り年 [2]=出力ファイルパス [3]=入力ファイルパス
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void main(String[] args)
		throws ParserConfigurationException,
		TransformerException,
		IOException, ParseException
	{
		DateTime start0 = DateTime.parseDateString(args[0]);
		DateTime end0 = DateTime.parseDateString(args[1]);

		KionCollection kionCollection =
			new KionCollectionFromFile(args[3], 0, start0, end0);

		ArrayList<Integer> locationCollection = new ArrayList<Integer>();
		locationCollection.add(0);
		locationCollection.add(1);
		locationCollection.add(2);

		DateTime start = kionCollection.getStartDatetime();
		DateTime end = kionCollection.getEndDatetime();
		int day = end.diff(start).getDay();

		Kion [][] kionTable = kionCollection.getAverage(start, end);

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

		AverageKionGraphDocument document =
			new AverageKionGraphDocument(
				kionTable,
				kinjiList,
				start,
				end,
				max,
				min,
				new Dimension(800 / day, 550 / height));

		Transformer transformer =
			TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty
			(OutputKeys.OMIT_XML_DECLARATION, "yes");

		document.write(
			transformer,
			new OutputStreamWriter(
				new FileOutputStream(args[2]), "utf-8"));
	}
}
