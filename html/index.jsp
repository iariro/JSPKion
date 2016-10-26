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
		<h1>気温</h1>

		<ul>
		<li>
			<s:form action="shortgraph" theme="simple">
				<s:submit value="短期グラフ" />
			</s:form>

		<li>長期グラフ
		<div class="day">
		<s:form action="longgraph" theme="simple">
			<table cellpadding="3">
				<tr bgcolor="#eeeeff">
					<td>
						<s:select name="hour" list="#{ '0':'0', '1':'1', '2':'2', '3':'3', '4':'4', '5':'5', '6':'6', '7':'7', '8':'8', '9':'9', '10':'10', '11':'11', '12':'12', '13':'13', '14':'14', '15':'15', '16':'16', '17':'17', '18':'18', '19':'19', '20':'20', '21':'21', '22':'22', '23':'23' }" value="7" />
					</td>
					<td>時</td>
				</tr>
				<tr>
					<td colspan="2"><s:submit value="長期グラフ" /></td>
				</tr>
			</table>
		</s:form>
		</div>

		<li>
			<s:form action="longaveragegraph" theme="simple">
				<s:select name="location" value="2" list="#{ '1':'船橋', '2':'阿智' }" />
				<input type="text" name="start" value="<s:property value="start" />" size="12" />
				<input type="text" name="end" value="<s:property value="end" />" size="12" />
				<input type="text" name="idouheikinRange" value="3" size="2" />日
				<s:submit value="平均気温" />
			</s:form>

		<li>
			<s:form action="monthlyaveragetable" theme="simple">
				<s:select name="location" value="2" list="#{ '1':'船橋', '2':'阿智' }" />
				<input type="text" name="start" value="<s:property value="start" />" size="12" />
				<input type="text" name="end" value="<s:property value="end" />" size="12" />
				<s:submit value="月ごと平均気温一覧表" />
			</s:form>

		<li>
			<s:form action="dailyaveragetable" theme="simple">
				<s:select name="location" value="2" list="#{ '1':'船橋', '2':'阿智' }" />
				<input type="text" name="start" value="<s:property value="start" />" size="12" />
				<input type="text" name="end" value="<s:property value="end" />" size="12" />
				<s:submit value="日ごと平均気温一覧表" />
			</s:form>

		<li>
			<s:form action="perhourtable" theme="simple">
				<s:submit value="時間ごと表" />
			</s:form>

		<li>
			<s:form action="perhourgraph" theme="simple">
				<s:select name="location" value="2" list="#{ '1':'船橋', '2':'阿智' }" />
				<input type="text" name="start" value="<s:property value="start" />" size="12" />
				<input type="text" name="end" value="<s:property value="end" />" size="12" />
				<s:submit value="時間ごとグラフ" />
			</s:form>

		<li>
			<s:form action="yearcomparegraph" theme="simple">
				<s:submit value="年ごと平均気温近似値比較" />
				<s:select name="location" value="2" list="#{ '1':'船橋', '2':'阿智' }" />
				<input type="text" name="yearNum" value="5" size="2" />年分
				<input type="text" name="startMonthDay" value="1/1" size="6" /> -
				<input type="text" name="endMonthDay" value="12/31" size="6" />
				<s:select name="kinjiType" value="1" list="#{ '0':'近似曲線', '1':'移動平均法' }" />
				<input type="text" name="idouheikinRange" value="3" size="2" />日
			</s:form>

		<li><a href="perday1.jsp">日ごと</a>
		</ul>

	</body>
</html>
