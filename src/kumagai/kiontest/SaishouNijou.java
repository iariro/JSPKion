package kumagai.kiontest;

import java.util.*;
import java.awt.geom.*;
import kumagai.kion.*;

class SaishouNijou
{
	static public void main(String [] args)
	{
		double x [] =
			new double []
			{
				1.0,
				2.0,
				3.0,
				4.0,
				5.0,
				6.0
			};

		double y [] =
			new double []
			{
				7.987,
				2.986,
				1.998,
				2.224,
				5.678,
				6.678
			};

		/*データの横軸に値する数値を配列xに小さい順に入れ、
		それぞれの横軸の値における縦軸の値を配列yに入れ関数saiに渡す*/
		ArrayList<Point2D.Double> list =
			SaishouNijouhou.getKinji(x, y, 4, 4, 5);

		for (Point2D.Double point : list)
		{
			System.out.printf("%f %f\n", point.x, point.y);
		}
	}
}
