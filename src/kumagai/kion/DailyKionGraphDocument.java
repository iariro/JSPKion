package kumagai.kion;

import java.awt.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import org.w3c.dom.*;
import ktool.datetime.*;
import ktool.xml.*;

/**
 * 日ごとの気温グラフドキュメント。
 * @author kumagai
 */
public class DailyKionGraphDocument
	extends KDocument
{
	static private Point origin = new Point(50, 20);
	static private final String fontFamily = "MS-Mincho";
	static private String [] colors = new String [] {"blue", "red"};

	/**
	 * 日ごとの気温グラフドキュメントを構築する。
	 * @param kionTable 気温テーブル
	 * @param locationCollection 観測地点コレクション
	 * @param start グラフ化範囲始点
	 * @param end グラフ化範囲終点
	 * @param max 気温上限
	 * @param min 気温下限
	 * @param size 倍率
	 * @param dot true=頂点を描画する／false=しない
	 * @throws ParserConfigurationException
	 * @throws TransformerConfigurationException
	 * @throws TransformerFactoryConfigurationError
	 */
	public DailyKionGraphDocument(Kion [][] kionTable,
		ArrayList<Integer> locationCollection, DateTime start, DateTime end,
		float max, float min, Dimension size, boolean dot)
		throws ParserConfigurationException,
		TransformerConfigurationException,
		TransformerFactoryConfigurationError
	{
		Element top = createElement("svg");
		appendChild(top);

		top.setAttribute("xmlns", "http://www.w3.org/2000/svg");

		Element element = createElement("title");
		top.appendChild(element);
		element.appendChild(createTextNode("長期グラフ"));

		int dayRange = end.diff(start).getDay();

		// 枠線。
		element = createElement("rect");
		element.setAttribute
			("x", String.valueOf(origin.x));
		element.setAttribute
			("y", String.valueOf(origin.y));
		element.setAttribute
			("width", String.valueOf(size.width * dayRange));
		element.setAttribute
			("height", String.valueOf(size.height * (max - min)));
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
				String.valueOf(origin.y + size.height * (max - i)));
			element.setAttribute(
				"x2",
				String.valueOf(origin.x));
			element.setAttribute(
				"y2",
				String.valueOf(origin.y + size.height * (max - i)));
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
					String.valueOf(origin.y + size.height * (max - i) + 5));
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
					String.valueOf(origin.y + size.height * (max - i)));
				element.setAttribute(
					"x2",
					String.valueOf(origin.x + size.width * dayRange));
				element.setAttribute(
					"y2",
					String.valueOf(origin.y + size.height * (max - i)));
				element.setAttribute(
					"style",
					"stroke:#000000;stroke-width:" + (i == 0 ? "0.5" : "0"));
				top.appendChild(element);
			}
		}

		// 横の目盛り。
		DateTime date1 = new DateTime(start);
		for (int i=0 ; i<dayRange ; i++)
		{
			if ((date1.getDay() % 10) == 1 && (date1.getDay() < 30))
			{
				// １日，１１日，２１日の場合。

				element = createElement("line");
				element.setAttribute(
					"x1",
					String.valueOf(origin.x + size.width * i));
				element.setAttribute(
					"y1",
					String.valueOf(origin.y + size.height * (max - min)));
				element.setAttribute(
					"x2",
					String.valueOf(origin.x + size.width * i));
				element.setAttribute(
					"y2",
					String.valueOf(origin.y + size.height * (max - min) + 5));
				element.setAttribute("stroke", "black");
				top.appendChild(element);

				String text = String.valueOf(date1.getDay());
				int adjustX = 8;

				if (date1.getDay() == 1 || i <= 10)
				{
					// １日、または左端の場合。

					text = String.valueOf(date1.getMonth()) + "/" + text;
					adjustX = 10;
				}

				int y = (int)(origin.y + size.height * (max - min) + 20);

				if (date1.getDay() == 1)
				{
					// １日の場合。

					y += 20;
				}

				element = createElement("text");
				element.setAttribute(
					"x",
					String.valueOf(origin.x + size.width * i - adjustX));
				element.setAttribute(
					"y",
					String.valueOf(y));
				element.setAttribute("font-family", fontFamily);
				element.appendChild(createTextNode(text));
				top.appendChild(element);
			}

			date1 = date1.makeAdd(new TimeSpan(24, 0, 0));
		}

		// 折れ線グラフ。
		for (int i=0 ; i<locationCollection.size() ; i++)
		{
			String points = new String();

			int count = 0;

			for (int j=0 ; j<=dayRange ; j++)
			{
				if (kionTable[j][i] != null)
				{
					// 値はある。

					int date = kionTable[j][i].datetime.diff(start).getDay();

					if (count > 0)
					{
						// ２回目以降。

						points += ", ";
					}

					float celsius = kionTable[j][i].celsius;

					if (celsius > max)
					{
						// 上限を超えている。

						celsius = max;
					}
					else if (celsius < min)
					{
						// 下限を下回っている。

						celsius = min;
					}

					points +=
						origin.x + size.width * date + " " +
						(origin.y + size.height * (max - celsius));

					if (dot)
					{
						// 頂点を描画する。

						element = createElement("rect");
						element.setAttribute(
							"x",
							String.valueOf(
								origin.x +
								size.width * date - 1));
						element.setAttribute(
							"y",
							String.valueOf(
								origin.y +
								size.height * (max - celsius) - 1));
						element.setAttribute("width", "2");
						element.setAttribute("height", "2");
						element.setAttribute("fill", colors[i]);
						element.setAttribute("stroke", colors[i]);
						top.appendChild(element);
					}

					count++;
				}
			}

			element = createElement("polyline");
			element.setAttribute("points", points);
			element.setAttribute("stroke", colors[i]);
			element.setAttribute("fill", "none");
			top.appendChild(element);
		}
	}
}
