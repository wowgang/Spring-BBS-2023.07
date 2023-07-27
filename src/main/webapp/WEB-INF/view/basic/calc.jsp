<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>계산기</title>
</head>
<body style="margin: 40px;">
	<h1>계산기</h1>
	<hr>
	<form action="/sbbs/basic/calc" method="post">
		<input type="number" name="num1">
		<select name="operator">
			<option value="+">+</option>
			<option value="-">-</option>
			<option value="*">*</option>
			<option value="/">/</option>
		</select>
		<input type="number" name="num2">
		<input type="submit" value="=">
		${result}
	</form>
</body>
</html>