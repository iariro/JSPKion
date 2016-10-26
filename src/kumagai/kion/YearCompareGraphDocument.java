package kumagai.kion;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import org.w3c.dom.*;
import ktool.datetime.*;
import ktool.xml.*;

/**
 * 年ごとの平均気温近似値グラフXMLドキュメント。
 */
public class YearCompareGraphDocument
	extends KDocument
{
	static private final int rmargin = 10;
	static private final Point origin = new Point(50, 20);
	static private final String fontFamily = "MS-Mincho";
	static private final String [] colors =
		new String [] { "blue", "red", "green", "purple", "black", "yellow" };

	/**
	 * 年ごとの平均気温近似値グラフXMLドキュメントを構築。
	 * @param kinjiList 近似値リスト
	 * @param width 横縮尺
	 * @param height 縦縮尺
	 * @param startYear 開始年
	 * @param startDate 開始年月
	 * @param endDate 終了年月
	 * @throws ParserConfigurationException
	 * @throws ParserConfigurationException
	 * @throws TransformerConfigurationException
	 */
	public YearCompareGraphDocument
		(ArrayList<ArrayList<Point2D.Double>> kinjiList, float width,
		float height, int startYear, DateTime startDate, DateTime endDate)
		throws ParserConfigurationException, ParserConfigurationException, TransformerConfigurationException
	{
		DateTime date1 = new DateTime(startDate);

		Element top = createElement("svg");
		appendChild(top);

		top.setAttribute("xmlns", "http://www.w3.org/2000/svg");

		Element element = createElement("title");
		top.appendChild(element);
		element.appendChild(createTextNode("長期グラフ"));

		int dayRange = endDate.diff(startDate).getDay();

		Float maxf = null;
		Float minf = null;

		for (int i=0 ; i<kinjiList.size() ; i++)
		{
			for (int j=0 ; j<kinjiList.get(i).size() ; j++)
			{
				if (maxf == null || kinjiList.get(i).get(j).y > maxf)
				{
					// 最大値を上回る。

					maxf = (float)kinjiList.get(i).get(j).y;
				}

				if (minf == null || kinjiList.get(i).get(j).y < minf)
				{
					// 最小値を下回る。

					minf = (float)kinjiList.get(i).get(j).y;
				}
			}
		}

		int max = ((maxf.intValue() + 4) / 5) * 5;
		int min = ((minf.intValue() - 4) / 5) * 5;

		// 枠線。
		element = createElement("rect");
		element.setAttribute
			("x", String.valueOf(origin.x));
		element.setAttribute
			("y", String.valueOf(origin.y));
		element.setAttribute
			("width", String.valueOf(width * dayRange + rmargin));
		element.setAttribute
			("height", String.valueOf(height * (max - min)));
		element.setAttribute
			("fill", "#dddddd");
		element.setAttribute
			("stroke", "black");
		top.appendChild(element);

		// 縦の目盛り。
		for (float i=min ; i<=max ; i++)
		{
			element = createElement("line");
			element.setAttribute(
				"x1",
				String.valueOf(origin.x - ((max - i) % 10 == 0 ? 10 : 5)));
			element.setAttribute(
				"y1",
				String.valueOf(origin.y + height * (max - i)));
			element.setAttribute(
				"x2",
				String.valueOf(origin.x));
			element.setAttribute(
				"y2",
				String.valueOf(origin.y + height * (max - i)));
			element.setAttribute("stroke", "black");
			top.appendChild(element);

			if ((max - i) % 5 == 0)
			{
				// ５℃刻み。

				element = createElement("text");
				element.setAttribute(
					"x",
					String.valueOf(origin.x - 40));
				element.setAttribute(
					"y",
					String.valueOf(origin.y + height * (max - i) + 5));
				element.setAttribute("font-family", fontFamily);
				element.appendChild(
					createTextNode(String.valueOf((int)i) + "℃"));
				top.appendChild(element);

				element = createElement("line");
				element.setAttribute(
					"x1",
					String.valueOf(origin.x));
				element.setAttribute(
					"y1",
					String.valueOf(origin.y + height * (max - i)));
				element.setAttribute(
					"x2",
					String.valueOf(origin.x + width * dayRange + rmargin));
				element.setAttribute(
					"y2",
					String.valueOf(origin.y + height * (max - i)));
				element.setAttribute(
					"style",
					"stroke:#000000;stroke-width:" + (i == 0 ? "1" : "0.5"));
				top.appendChild(element);
			}
		}

		// 横の目盛り。
		for (int i=0 ; i<dayRange+1 ; i++)
		{
			if (((date1.getDay() < 30) && (date1.getDay() % 10 == 1)) ||
				((date1.getDay() >= 30) && (i == dayRange)))
			{
				// １日・１１日・２１日・３０日・３１日の場合。

				element = createElement("line");
				element.setAttribute(
					"x1",
					String.valueOf(origin.x + width * i));
				element.setAttribute(
					"y1",
					String.valueOf(origin.y + height * (max - min)));
				element.setAttribute(
					"x2",
					String.valueOf(origin.x + width * i));
				element.setAttribute(
					"y2",
					String.valueOf(origin.y + height * (max - min) + 5));
				element.setAttribute("stroke", "black");
				top.appendChild(element);

				String text = String.valueOf(date1.getDay());
				int adjustX = 8;

				if (date1.getDay() == 1 || i < 10)
				{
					// １日、または左端の場合。

					text = String.valueOf(date1.getMonth()) + "/" + text;
					adjustX = 10;

					if (i < 10)
					{
						// 左端の場合。

						text = String.valueOf(date1.getYear()) + "/" + text;
					}
				}

				int y = (int)(origin.y + height * (max - min) + 20);

				if (date1.getDay() == 1)
				{
					// １日の場合。

					y += 20;
				}

				element = createElement("text");
				element.setAttribute(
					"x",
					String.valueOf(origin.x + width * i - adjustX));
				element.setAttribute(
					"y",
					String.valueOf(y));
				element.setAttribute("font-family", fontFamily);
				element.appendChild(createTextNode(text));
				top.appendChild(element);
			}

			date1 = date1.makeAdd(new TimeSpan(24, 0, 0));
		}

		for (int i=0 ; i<kinjiList.size() ; i++)
		{
			// 近似値グラフ。
			String points = new String();

			for (int j=0 ; j<kinjiList.get(i).size() ; j++)
			{
				if (j > 0)
				{
					// ２回目以降。

					points += ", ";
				}

				points +=
					origin.x + width * kinjiList.get(i).get(j).x + " " +
					(origin.y + height * (max - kinjiList.get(i).get(j).y) + i * 5);
			}

			element = createElement("polyline");
			element.setAttribute("points", points);
			element.setAttribute("stroke", colors[i]);
			element.setAttribute("stroke-width", "2");
			element.setAttribute("fill", "none");

			top.appendChild(element);

			// ラベル。
			element = createElement("text");
			element.setAttribute(
				"x",
				String.valueOf(origin.x + width * dayRange + rmargin - 80));
			element.setAttribute(
				"y",
				String.valueOf(origin.y + i * 30 + 25));
			element.setAttribute("font-family", fontFamily);
			element.appendChild(
				createTextNode(String.valueOf(startYear - i)));
			top.appendChild(element);

			element = createElement("line");
			element.setAttribute(
				"x1",
				String.valueOf(origin.x + width * dayRange + rmargin - 40));
			element.setAttribute(
				"y1",
				String.valueOf(origin.y + i * 30 + 20));
			element.setAttribute(
				"x2",
				String.valueOf(origin.x + width * dayRange + rmargin - 20));
			element.setAttribute(
				"y2",
				String.valueOf(origin.y + i * 30 + 20));
			element.setAttribute("stroke", colors[i]);
			element.setAttribute("stroke-width", "2");
			top.appendChild(element);
		}
	}
}
