package kumagai.kion;

import java.sql.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;

/**
 * 日ごとの平均気温コレクション。
 * @author kumagai
 */
public class AverageDailyKionCollection
	extends KionCollection
{
	/**
	 * 日ごとの平均気温コレクションを構築する。
	 * @param connection DB接続オブジェクト
	 * @param dayRange 当日を起点としたさかのぼる日数
	 * @throws ParserConfigurationException
	 * @throws TransformerConfigurationException
	 * @throws TransformerFactoryConfigurationError
	 * @throws SQLException
	 */
	public AverageDailyKionCollection(Connection connection, int dayRange)
		throws ParserConfigurationException,
		TransformerConfigurationException,
		TransformerFactoryConfigurationError,
		SQLException
	{
		PreparedStatement statement =
			connection.prepareStatement(
				"select location, convert(datetime, convert(varchar, nichiji, 111)) as nichiji, avg(celsius) as celsius from kion where datediff(day, nichiji, getdate()) <=? group by location, convert(datetime, convert(varchar, nichiji, 111)) order by convert(datetime, convert(varchar, nichiji, 111))");

		statement.setInt(1, dayRange);

		ResultSet resultset = statement.executeQuery();

		while (resultset.next())
		{
			add(new Kion(resultset));
		}

		resultset.close();
		statement.close();
	}
}
