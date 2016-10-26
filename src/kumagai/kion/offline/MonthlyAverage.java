package kumagai.kion.offline;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import ktool.datetime.*;
import kumagai.kion.*;

/**
 * 月毎の平均を求める。
 */
public class MonthlyAverage
{
	/**
	 * 月毎の平均を求める。
	 * @param args [0]=開始日 [1]=終了日 [2]=入力ファイルパス
	 */
	public static void main(String[] args)
		throws ParserConfigurationException,
		TransformerException,
		IOException, ParseException
	{
		DateTime start0 = DateTime.parseDateString(args[0]);
		DateTime end0 = DateTime.parseDateString(args[1]);

		KionCollection kionCollection =
			new KionCollectionFromFile(args[2], 0, start0, end0);

		ArrayList<MonthlyKionCollection> monthlyKionCollections =
			kionCollection.getMonthlyAverage();

		DateTime startDatetime = kionCollection.getStartDatetime();
		DateTime endDatetime = kionCollection.getEndDatetime();

		for (MonthlyKionCollection monthlyKionCollection :
			monthlyKionCollections)
		{
			System.out.printf(
				"%d/%02d\t%f\n",
				monthlyKionCollection.year,
				monthlyKionCollection.month,
				monthlyKionCollection.getAverage());
		}

		for (int i=startDatetime.getYear() ; i<=endDatetime.getYear() ; i++)
		{
			for (int j=0 ; j<12 ; j++)
			{
				MonthlyKionCollection monthlyKionCollection = null;

				for (MonthlyKionCollection monthlyKionCollection2 :
					monthlyKionCollections)
				{
					if (monthlyKionCollection2.year == i &&
						monthlyKionCollection2.month == j)
					{
						// 合致する

						monthlyKionCollection = monthlyKionCollection2;
						break;
					}
				}

				if (monthlyKionCollection != null)
				{
					// 見つかった

					System.out.printf(
						"%2.2f\t",
						monthlyKionCollection.getAverage());
				}
			}

			System.out.println();
		}
	}
}
