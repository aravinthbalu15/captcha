<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Captcha Doğrulama</title>
    </head>
    <body>

    <h1>Captcha Doğrulama</h1>
    <img id="captchaImage" alt="Captcha Resmi">
    <button id="refreshButton">Yenile</button>
    <div id="timer"></div>

    <form>
        <label for="captchaCode">Captcha Kodunu Girin:</label>
        <input type="text" id="captchaCode" name="captchaCode" required>
        <button id="submit" type="submit">Doğrula</button>
    </form>
    <div id="result"></div>

<script>
    const form = document.querySelector('form');
    const submitButton = document.getElementById('submit');
    const resultDiv = document.getElementById('result');
    const captchaImage = document.getElementById('captchaImage');
    const refreshButton = document.getElementById('refreshButton');
    const captchaCodeInput = document.getElementById('captchaCode');
    const timer = document.getElementById("timer");

    window.onload = function () {
        GetCaptchaImage()
    }

    refreshButton.addEventListener('click', async () => {
        await GetCaptchaImage()
    });

    form.addEventListener('submit', async (event) => {
        event.preventDefault();

        fetch('${pageContext.request.contextPath}/captcha/validate', {
                method: 'POST',
                body: captchaCodeInput.value
        })
            .then(response => response.json())
            .then(data => {
                resultDiv.textContent = data ? 'Doğrulama başarılı!' : 'Doğrulama başarısız!';
            })
            .catch(error => {
                console.log(error)
            })

    });

    function GetCaptchaImage() {
        captchaCodeInput.value = ''
        fetch('${pageContext.request.contextPath}/captcha/image?')
            .then(response => {
                if (response.status === 429) {
                    refreshButton.disabled = true
                    submitButton.disabled = true
                    resultDiv.textContent = 'Çok fazla kez yenilendi!';

                    var countdown = 30;
                    var interval = setInterval(function() {
                        timer.textContent = countdown;
                        countdown--;
                        if (countdown < 0) {
                            clearInterval(interval);
                            GetCaptchaImage()
                            timer.textContent = "";
                            refreshButton.disabled = false
                            submitButton.disabled = false
                            resultDiv.textContent = ''
                        }
                    }, 1000);

                }
                return response.blob();
            })
            .then(blob => {
                captchaImage.src = URL.createObjectURL(blob);
            })
            .catch(error => {
                console.error("Resim alınamadı: ", error);
            });
    }

    captchaCodeInput.addEventListener('input', function(event) {
        this.value = this.value.toUpperCase();
    });
</script>
</body>
</html>
