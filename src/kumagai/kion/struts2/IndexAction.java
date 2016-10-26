package kumagai.kion.struts2;

import org.apache.struts2.convention.annotation.*;
import ktool.datetime.*;

/**
 * トップページ表示アクション。
 * @author kumagai
 */
@Namespace("/kion")
@Result(name="success", location="/kion/index.jsp")
public class IndexAction
{
	public String start;
	public String end;

	/**
	 * トップページ表示アクション。
	 * @return 処理結果
	 * @throws Exception
	 */
	@Action("index")
	public String execute()
		throws Exception
	{
		DateTime start = new DateTime();
		DateTime end = new DateTime();

		for (int i=0 ; i<60 ; i++)
		{
			start.add(new TimeSpan(-24, 0, 0));
		}

		this.start = start.toString();
		this.end = end.toString();

		return "success";
	}
}
