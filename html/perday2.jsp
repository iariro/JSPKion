<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="/struts-tags" prefix="s" %>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" type="text/css" href="hatena.css">
		<title>気温</title>
	</head>

	<body>
		<h1>気温一覧</h1>
		<div class=hatena-body>
		<div class=main>
		<div class=day>

		<table>
			<tr><th>場所</th><th>日</th><th>時</th><th>℃</th><th>登録日</th></tr>

			<s:iterator value="items">
				<tr bgcolor="<s:property value="color" />">
					<td><s:property value="location" /></td>
					<td><s:property value="date" /></td>
					<td><s:property value="time" /></td>
					<td><s:property value="celsius" /></td>
					<td><s:property value="registerDate" /></td>
				</tr>
			</s:iterator>

		</table>

		<s:property value="size" />件
		</div>
		</div>
		</div>

	</body>
</html>
