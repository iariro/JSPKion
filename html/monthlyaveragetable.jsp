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

		<table>
			<tr><th>年</th>
			<th>1月</th>
			<th>2月</th>
			<th>3月</th>
			<th>4月</th>
			<th>5月</th>
			<th>6月</th>
			<th>7月</th>
			<th>8月</th>
			<th>9月</th>
			<th>10月</th>
			<th>11月</th>
			<th>12月</th>
			</tr>

			<s:iterator value="monthlyAverageTableRows">
				<tr>
					<th><s:property value="year" />年</th>
					<s:iterator value="monthlyCelsius">
						<td align="right"><s:property /></td>
					</s:iterator>
				</tr>
			</s:iterator>

		</div>
		</div>
		</div>

	</body>
</html>
