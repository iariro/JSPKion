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

		<s:if test="%{exists}">
			<table>
				<tr><th>日</th><th>時</th><th>℃</th><th>℃</th></tr>

				<s:iterator value="items">
					<tr bgcolor="<s:property value="color" />">
						<td><s:property value="date" /></td>
						<td><s:property value="time" /></td>
						<s:iterator value="celsius">
							<td><s:property /></td>
						</s:iterator>
					</tr>
				</s:iterator>
			</table>
		</s:if>
		<s:else>
			最近のデータはありません。
		</s:else>

		</div>
		</div>
		</div>

	</body>
</html>
