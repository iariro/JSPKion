package kumagai.kiontest;

import junit.framework.*;
import ktool.datetime.*;
import kumagai.kion.*;

/**
 * Kionのテスト。
 * @author kumagai
 */
public class KionTest
	extends TestCase
{
	/**
	 * コンストラクタのテスト。
	 */
	public void test01()
	{
		Kion kion = new Kion(1, new DateTime(2009, 8, 19, 1, 2, 3), 20.5f);

		assertEquals(1, kion.location);
		assertEquals(2009, kion.datetime.getYear());
		assertEquals(8, kion.datetime.getMonth());
		assertEquals(19, kion.datetime.getDay());
		assertEquals(1, kion.datetime.getHour());
		assertEquals(2, kion.datetime.getMinute());
		assertEquals(3, kion.datetime.getSecond());
		assertEquals(20.5f, kion.celsius);
	}
}
