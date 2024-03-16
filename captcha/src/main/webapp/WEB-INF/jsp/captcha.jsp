<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Captcha Doğrulama</title>
</head>
<body>
<h1>Captcha Doğrulama</h1>
<img src="${pageContext.request.contextPath}/captcha/image" alt="Captcha Resmi">
<form action="${pageContext.request.contextPath}/captcha/validate" method="post">
    <label for="captchaCode">Captcha Kodunu Girin:</label>
    <input type="text" id="captchaCode" name="captchaCode" required>
    <button type="submit">Doğrula</button>
</form>
<div id="result"></div>

<script>
    const form = document.querySelector('form');
    const resultDiv = document.getElementById('result');

    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        const formData = new FormData(form);
        const response = await fetch('${pageContext.request.contextPath}/captcha/validate', {
            method: 'POST',
            body: formData
        });
        const isValid = await response.json();
        resultDiv.textContent = isValid ? 'Doğrulama başarılı!' : 'Doğrulama başarısız!';
    });
</script>
</body>
</html>