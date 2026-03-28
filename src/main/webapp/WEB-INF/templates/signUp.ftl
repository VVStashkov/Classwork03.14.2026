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
    <input type="text" name="username" placeholder="Username" required/>
    <input type="email" name="email" placeholder="Email" required/>
    <input type="password" name="password" placeholder="Password" required/>
    <button type="submit">Sign Up</button>
</form>
<br>
<a href="/login">Уже есть аккаунт? Войти</a>
</body>
</html>