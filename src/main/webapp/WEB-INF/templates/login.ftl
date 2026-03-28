<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Вход</title>
</head>
<body>
<h2>Вход</h2>
<#if message??>
    <div class="alert alert-success">${message}</div>
</#if>
<#if error??>
    <div class="alert alert-danger">${error}</div>
</#if>
<form action="/login" method="post">
    <label>Имя пользователя:</label>
    <input type="text" name="username" required>
    <br>
    <label>Пароль:</label>
    <input type="password" name="password" required>
    <br>
    <button type="submit">Войти</button>
</form>
<br>
<a href="/signUp">Регистрация</a>
</body>
</html>