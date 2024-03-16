<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Captcha Doğrulama</title>
</head>
<body>
<h1>Captcha Doğrulama</h1>
<img id="captchaImage" src="${pageContext.request.contextPath}/captcha/image" alt="Captcha Resmi">
<button id="refreshButton">Yenile</button>
<form action="${pageContext.request.contextPath}/captcha/validate" method="post">
    <label for="captchaCode">Captcha Kodunu Girin:</label>
    <input type="text" id="captchaCode" name="captchaCode" required>
    <button type="submit">Doğrula</button>
</form>
<div id="result"></div>

<script>
    const form = document.querySelector('form');
    const resultDiv = document.getElementById('result');
    const captchaImage = document.getElementById('captchaImage');
    const refreshButton = document.getElementById('refreshButton');
    const captchaCodeInput = document.getElementById('captchaCode');

    // Yenile butonuna tıklandığında captcha görüntüsünü yenile
    refreshButton.addEventListener('click', async () => {
        try {
            const response = await fetch('${pageContext.request.contextPath}/captcha/image?' + new Date().getTime());
            if (response.status === 429) {
                resultDiv.textContent = 'Çok fazla kez yenilendi!';
            } else {
                captchaImage.src = response.url;
            }
        } catch (error) {
            console.error('Bir hata oluştu:', error);
        }
    });

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

    // Input alanına girilen değeri otomatik olarak büyük harfe çevir
    captchaCodeInput.addEventListener('input', function(event) {
        this.value = this.value.toUpperCase();
    });
</script>
</body>
</html>
