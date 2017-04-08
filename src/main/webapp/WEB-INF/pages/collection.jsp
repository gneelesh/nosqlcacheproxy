<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Showing Collection Information</title>
		<style>

table.excelTable {border-collapse:collapse; border:1px solid #ccc;}
table.excelTable  th{padding:4px 4px; border:1px solid #ccc; background:#eee; border-top:1px solid #333; border-bottom:1px solid #333; color:black;}
table.excelTable  td{padding:4px 4px; border:1px solid #ccc; color:black;}
</style>
</head>
<body>
	<table align="center" class="excelTable" with="100%" cellspacing="10">
		<tr>
			<th>#</th>
			<th>Collection Name</th>
			<th>Count</th>
			<th>Last Cached On</th>
			<th>Action</th>
		</tr> 
			<c:forEach var="listValue" items="${clist}">
			<tr>
				<c:forEach var="str" items="${listValue}">
					<td>${str}</td>
				</c:forEach>
				<td><a href="">delete</a></td>
				</tr>
			</c:forEach>
			
	</table>
</body>
</html>