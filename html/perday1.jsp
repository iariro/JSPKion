<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="/struts-tags" prefix="s" %>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>気温</title>
	</head>

	<body>
		<div align="center">
		<h3>指定時刻の気温を表示</h3>

		<s:form action="perday" theme="simple">
			<table cellpadding="3">

				<tr bgcolor="#eeeeff">
					<td>場所</td>
					<td>
						<s:select name="location" list="#{ '1':'船橋', '2':'阿智' }" />
					</td>
				</tr>

				<tr bgcolor="#eeeeff">
					<td>時間</td>
					<td>
						<s:select name="hour" list="#{ '0':'0', '1':'1', '2':'2', '3':'3', '4':'4', '5':'5', '6':'6', '7':'7', '8':'8', '9':'9', '10':'10', '11':'11', '12':'12', '13':'13', '14':'14', '15':'15', '16':'16', '17':'17', '18':'18', '19':'19', '20':'20', '21':'21', '22':'22', '23':'23' }" />
					</td>
				</tr>

			</table>
			<s:submit value="時間ごと" />
		</s:form>
		</div>

	</body>
</html>
