package kumagai.kiontest;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import junit.framework.*;
import kumagai.kion.*;
import ktool.datetime.*;

/**
 * KionGraphDocumentのテスト。
 * @author kumagai
 */
public class KionGraphDocumentTest
	extends TestCase
{
	/**
	 * KionGraphDocument生成テスト。
	 * @param args 未使用
	 * @throws ParserConfigurationException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 * @throws IOException
	 */
	static public void main(String [] args)
		throws ParserConfigurationException,
		TransformerFactoryConfigurationError, TransformerException, IOException
	{
		KionCollection kionCollection = new KionCollection();

		kionCollection.add
			(new Kion(1, new DateTime(2009, 8, 19, 0, 0, 0), 21));
		kionCollection.add
			(new Kion(2, new DateTime(2009, 8, 19, 1, 0, 0), 23));
		kionCollection.add
			(new Kion(1, new DateTime(2009, 8, 19, 2, 0, 0), 22));
		kionCollection.add
			(new Kion(2, new DateTime(2009, 8, 19, 3, 0, 0), 24));

		ArrayList<Integer> locationCollection =
			kionCollection.getLocationCollection();

		DateTime start = kionCollection.getStartDatetime();
		DateTime end = kionCollection.getEndDatetime();

		Kion [][] kionTable =
			kionCollection.getTable(locationCollection, start, end, 1);

		KionGraphDocument document =
			new KionGraphDocument(kionTable, locationCollection, start, end, 60, 15, 1, new Dimension(5, 20), true);

		Transformer transformer =
			TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

		document.write(
			transformer,
			new OutputStreamWriter(new FileOutputStream("kion.svg"), "utf-8"));
	}
}
