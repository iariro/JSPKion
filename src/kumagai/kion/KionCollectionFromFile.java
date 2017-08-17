package kumagai.kion;

import java.io.*;
import ktool.datetime.*;

/**
 * ファイルから構築可能な時間ごとの気温コレクション。
 * @author kumagai
 */
public class KionCollectionFromFile
	extends KionCollection
{
	/**
	 * 時間ごとの気温コレクションをファイルから構築する。
	 * @param path ファイルパス
	 * @param location 観測地点コード
	 * @param start 開始日
	 * @param end 終了日
	 * @throws IOException
	 */
	public KionCollectionFromFile
		(String path, int location, DateTime start, DateTime end)
		throws IOException
	{
		findRecursive(new File(path), location, start, end);
	}

	/**
	 * 再帰的にファイルを検索。
	 * @param path ファイルパス
	 * @param location 観測地点コード
	 * @param start 開始日
	 * @param end 終了日
	 * @throws IOException
	 */
	private void findRecursive
		(File path, int location, DateTime start, DateTime end)
		throws IOException
	{
		File [] files = path.listFiles();

		for (File file : files)
		{
			if (file.isDirectory())
			{
				// ディレクトリである。

				findRecursive(file, location, start, end);
			}
			else
			{
				// ファイルである。

				BufferedReader reader =
					new BufferedReader(new FileReader(file));

				String line;

				reader.readLine(); // ヘッダ行読み飛ばし
				while ((line = reader.readLine()) != null)
				{
					String [] fields = line.split(",");

					if (fields.length == 3 || fields.length == 4)
					{
						// フィールド数は足りている。

						try
						{
							Kion kion = new Kion(location, fields);

							if (start.compareTo(kion.datetime) <= 0 &&
								end.compareTo(kion.datetime) >= 0)
							{
								// 範囲内である。

								add(kion);
							}
						}
						catch (StringIndexOutOfBoundsException exception)
						{
							// 無効な行。
							break;
						}
					}
					else
					{
						// フィールド数不正

						break;
					}
				}
			}
		}
	}
}
