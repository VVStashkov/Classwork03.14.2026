<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Регистрация</title>
</head>
<body>
<h2>Регистрация</h2>
<#if RequestParameters.error??>
    <p style="color: red;">Пользователь с таким именем уже существует</p>
</#if>
<form action="/signUp" method="post">
    <label>Имя пользователя:</label>
    <input type="text" name="name" required>
    <br>
    <label>Пароль:</label>
    <input type="password" name="password" required>
    <br>
    <button type="submit">Зарегистрироваться</button>
</form>
<br>
<a href="/login">Уже есть аккаунт? Войти</a>
</body>
</html>