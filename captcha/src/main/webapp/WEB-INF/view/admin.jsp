<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Admin Page</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
        }

        h1, h2 {
            color: #333;
        }

        form {
            margin-bottom: 20px;
        }

        label {
            display: inline-block;
            width: 80px;
            font-weight: bold;
        }

        input[type="text"],
        input[type="number"] {
            padding: 5px;
            border: 1px solid #ccc;
            border-radius: 3px;
        }

        button {
            padding: 5px 10px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 3px;
            cursor: pointer;
        }

        button:hover {
            background-color: #45a049;
        }

        table {
            border-collapse: collapse;
            width: 100%;
        }

        th, td {
            padding: 8px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }

        th {
            background-color: #f2f2f2;
        }

        #pagination {
            margin-top: 10px;
            text-align: center;
        }

        #pagination a {
            display: inline-block;
            padding: 5px 10px;
            text-decoration: none;
            color: #333;
            background-color: #f2f2f2;
            border: 1px solid #ddd;
            margin: 0 2px;
        }

        #pagination a.active {
            background-color: #4CAF50;
            color: white;
            border: 1px solid #4CAF50;
        }
    </style>
</head>
<body>
<h1>Admin Page</h1>

<h2>Add Captcha</h2>
<form id="addCaptchaForm">
    <label for="code">Code:</label>
    <input type="text" id="code" name="code" required>
    <button type="submit">Add Captcha</button>
</form>

<h2>Captchas</h2>
<div>
    <label for="pageSize">Page Size:</label>
    <input type="number" id="pageSize" name="pageSize" value="10" min="1" max="100">
    <button onclick="getCaptchas(0)">Go</button>
</div>
<br>
<table id="captchasTable">
    <thead>
    <tr>
        <th>ID</th>
        <th>Code</th>
        <th>Image</th>
        <th>Created At</th>
        <th>Action</th>
    </tr>
    </thead>
    <tbody></tbody>
</table>
<div id="pagination"></div>

<script type="text/javascript">
    let currentPage = 0;
    let totalPages = 0;
    let pageSize = 10;

    function getCaptchas(page) {
        currentPage = page;
        const first = currentPage * pageSize;
        const url = '${pageContext.request.contextPath}/admin/captcha?first='+first+'&size='+pageSize;

        fetch(url)
            .then(response => response.json())
            .then(data => {
                displayCaptchas(data.captchas);
                getTotalCount();
            })
            .catch(error => console.error(error));
    }

    function displayCaptchas(captchas) {
        const tableBody = document.querySelector("#captchasTable tbody");
        tableBody.innerHTML = "";

        captchas.forEach(captcha => {
            const row = document.createElement("tr");

            const idCell = document.createElement("td");
            idCell.textContent = captcha.id;
            row.appendChild(idCell);

            const codeCell = document.createElement("td");
            codeCell.textContent = captcha.code;
            row.appendChild(codeCell);

            const imageCell = document.createElement("td");
            const imageUrl = '${pageContext.request.contextPath}/admin/captcha/image?id='+captcha.id;
            const img = document.createElement("img");
            img.src = imageUrl;
            img.width = 100;
            imageCell.appendChild(img);
            row.appendChild(imageCell);

            const createdAtCell = document.createElement("td");
            createdAtCell.textContent = new Date(captcha.createdAt.nanos).toLocaleString();
            row.appendChild(createdAtCell);

            const actionCell = document.createElement("td");
            const deleteButton = document.createElement("button");
            deleteButton.textContent = "Delete";
            deleteButton.onclick = () => deleteCaptcha(captcha.id);
            actionCell.appendChild(deleteButton);
            row.appendChild(actionCell);

            tableBody.appendChild(row);
        });
    }

    function getTotalCount() {
        fetch('${pageContext.request.contextPath}/admin/captcha/count')
            .then(response => response.json())
            .then(data => {
                const totalCount = data.result;
                totalPages = Math.ceil(totalCount / pageSize);
                updatePagination();
            })
            .catch(error => console.error(error));
    }

    function updatePagination() {
        const paginationDiv = document.querySelector("#pagination");
        paginationDiv.innerHTML = "";

        const firstButton = document.createElement("button");
        firstButton.textContent = "First";
        firstButton.onclick = () => getCaptchas(0);
        paginationDiv.appendChild(firstButton);

        const prevButton = document.createElement("button");
        prevButton.textContent = "Prev";
        prevButton.onclick = () => getCaptchas(currentPage - 1);
        prevButton.disabled = currentPage === 0;
        paginationDiv.appendChild(prevButton);

        const pageInfo = document.createElement("span");
        pageInfo.textContent = "Page "+ (currentPage + 1)+" of " +totalPages;
        paginationDiv.appendChild(pageInfo);

        const nextButton = document.createElement("button");
        nextButton.textContent = "Next";
        nextButton.onclick = () => getCaptchas(currentPage + 1);
        nextButton.disabled = currentPage === totalPages - 1;
        paginationDiv.appendChild(nextButton);

        const lastButton = document.createElement("button");
        lastButton.textContent = "Last";
        lastButton.onclick = () => getCaptchas(totalPages - 1);
        lastButton.disabled = currentPage === totalPages - 1;
        paginationDiv.appendChild(lastButton);
    }

    function deleteCaptcha(id) {
        const url = '${pageContext.request.contextPath}/admin/captcha?id='+id;
        const confirmed = confirm('Are you sure you want to delete captcha with ID '+id+'?');

        if (confirmed) {
            fetch(url, {
                method: "DELETE"
            })
                .then(() => getCaptchas(currentPage))
                .catch(error => console.error(error));
        }
    }

    const addCaptchaForm = document.querySelector("#addCaptchaForm");
    addCaptchaForm.addEventListener("submit", event => {
        event.preventDefault();
        const formData = new FormData(addCaptchaForm);
        const code = formData.get("code");
        const url = "${pageContext.request.contextPath}/admin/captcha?code="+code;
        fetch(url, {
            method: "PUT",
        })
            .then(response => {
                if (response.ok) {
                    alert("Captcha added successfully!");
                    addCaptchaForm.reset();
                    getCaptchas(0);
                } else {
                    alert("Failed to add captcha. Please try again.");
                }
            })
            .catch(error => console.error(error));
    });

    document.querySelector("#pageSize").addEventListener("change", event => {
        pageSize = event.target.value;
        getCaptchas(0);
    });

    getCaptchas(0);
</script>
</body>
</html>