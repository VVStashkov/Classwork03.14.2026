<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Вход</title>
</head>
<body>
<h2>Вход</h2>
<#if RequestParameters.error??>
    <p style="color: red;">Неверное имя пользователя или пароль</p>
</#if>
<#if RequestParameters.registered??>
    <p style="color: green;">Регистрация успешна. Войдите.</p>
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