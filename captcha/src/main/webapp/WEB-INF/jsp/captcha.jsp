<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Captcha Doğrulama</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f0f0f0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }

        .container {
            padding: 20px;
            max-width: 400px;
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            text-align: center;
        }

        h1 {
            margin-top: 0;
        }

        #captchaImage {
            display: block;
            margin: 0 auto;
            margin-bottom: 10px;
            max-height: 140px;
            max-width: 330px;
        }

        form {
            margin-top: 10px;
        }

        label {
            display: block;
            margin-bottom: 5px;
        }

        input[type="text"] {
            width: calc(100% - 20px);
            padding: 8px;
            margin-bottom: 10px;
            border-radius: 4px;
            border: 1px solid #ccc;
            box-sizing: border-box;
        }

        button {
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            background-color: #007bff;
            color: #fff;
            cursor: pointer;
        }

        button:hover {
            background-color: #0056b3;
        }

        #result {
            margin-top: 15px;
            text-align: center;
        }

        #timer {
            margin-bottom: 10px;
            margin-top: 10px;
        }

        button:disabled {
            background-color: #cccccc;
            cursor: not-allowed;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Captcha Doğrulama</h1>
    <img id="captchaImage" alt="Captcha Resmi">
    <div id="timer"></div>
    <button id="refreshButton">Yenile</button>
    <form>
        <label for="captchaCode">Captcha Kodunu Girin:</label>
        <input type="text" id="captchaCode" name="captchaCode" required>
        <button id="submit" type="submit">Doğrula</button>
    </form>
    <div id="result"></div>
</div>
</body>

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
                resultDiv.textContent = data ? 'Doğrulama başarılı!' : 'Doğrulama başarısız!'
                resultDiv.style.color = data ? 'green' : 'red';
            })
            .catch(error => {
                console.log(error)
            })

    });

    var interval

    function GetCaptchaImage() {
        captchaCodeInput.value = ''
        fetch('${pageContext.request.contextPath}/captcha/image?')
            .then(response => {
                if (response.status === 429) {
                    refreshButton.disabled = true
                    submitButton.disabled = true
                    resultDiv.textContent = 'Çok fazla kez yenilendi! Lütfen 30 saniye bekleyiniz.';
                    resultDiv.style.color = 'red'
                }
                clearInterval(interval);
                var countdown = 30;
                interval = setInterval(function() {
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
