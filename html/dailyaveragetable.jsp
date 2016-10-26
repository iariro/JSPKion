<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="/struts-tags" prefix="s" %>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv=Content-Style-Type content=text/css>
		<link media=all href="hatena.css" type=text/css rel=stylesheet>
		<title>気温</title>
	</head>

	<body>
		<h1>気温一覧</h1>
		<div class=hatena-body>
		<div class=main>
		<div class=day>

		<s:property value="start" /> - <s:property value="end" /><br>

		<table>
			<tr><th>日</th>
			<th>平均気温</th>
			</tr>

			<s:iterator value="average">
				<tr>
					<td><s:property value="datetime" /></td>
					<td align="right"><s:property value="celsius2keta" /></td>
				</tr>
			</s:iterator>

		</div>
		</div>
		</div>

	</body>
</html>
