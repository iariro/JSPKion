package kumagai.kion;

import java.io.*;

/**
 * ファイルから構築可能な日ごとの気温コレクション。
 * @author kumagai
 */
public class DailyKionCollectionFromFile
	extends KionCollection
{
	/**
	 * ファイルから日ごとの気温コレクションを構築する。
	 * @param location 観測地点コード
	 * @param path ファイルパス
	 * @param hour 指定時間
	 * @throws IOException
	 */
	public DailyKionCollectionFromFile(int location, String path, int hour)
		throws IOException
	{
		findRecursive(new File(path), location, hour);
	}

	/**
	 * 再帰的にファイルを検索。
	 * @param path ファイルパス
	 * @param location 観測地点コード
	 * @param hour 時刻
	 * @throws IOException
	 */
	private void findRecursive(File path, int location, int hour)
		throws IOException
	{
		File [] files = path.listFiles();

		for (File file : files)
		{
			if (file.isDirectory())
			{
				// ディレクトリ。

				findRecursive(file, location, hour);
			}
			else
			{
				// ファイル。

				BufferedReader reader =
					new BufferedReader(new FileReader(file));

				String line;

				while ((line = reader.readLine()) != null)
				{
					String [] fields = line.split(",");

					if (fields.length == 3 || fields.length == 4)
					{
						// フィールド数は正しい。

						try
						{
							Kion kion = new Kion(location, fields);

							if (kion.datetime.getHour() == hour)
							{
								// 対象の時間データである。

								add(kion);
							}
						}
						catch (StringIndexOutOfBoundsException exception)
						{
						}
					}
				}
			}
		}
	}
}
