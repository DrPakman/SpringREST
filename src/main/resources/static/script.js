document.getElementById("createUserForm").addEventListener("submit",function (event) {
    event.preventDefault();

    // Создаем объект FormData из формы
    const formData = new FormData(this);

    // Преобразуем FormData в объект
    const user = Object.fromEntries(formData.entries());

    const selectedRoles = Array.from(formData.getAll("roleNames"));

    user.roles = selectedRoles.map(role => ({ name: role })); // предполагается, что Role имеет поле name

    fetch("/api/users", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(user),
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error("Something went wrong!");
        })
    .then(data => {
        console.log(data);
        const table = document.querySelector("table tbody");
        const newRow = table.insertRow();

        newRow.innerHTML = `
            <td>${data.id}</td>
        <td>${data.username}</td>
        <td>${data.lastname}</td>
        <td>${data.age}</td>
        <td>${data.email}</td>
        <td>${data.roles.map(role => role.name).join(', ')}</td>
        <td>
            <button type="button" class="btn btn-success" data-bs-toggle="modal"
                    data-bs-target="#editModal${data.id}">
                Edit
            </button>
        </td>
        <td>
            <button type="button" class="btn btn-danger" data-bs-toggle="modal"
                    data-bs-target="#deleteModal${data.id}">
                Delete
            </button>
        </td>
            `;

            document.getElementById("createUserForm").reset();
        const userTab = new bootstrap.Tab(document.getElementById("nav-home-tab"));
        userTab.show();

    })
    .catch(error => {console.log(error);
    });

});
document.querySelectorAll('form[id^="deleteUserForm"]').forEach(form => {
    form.addEventListener('submit', function(event) {
        event.preventDefault(); // Отменяем стандартное поведение формы

        const userId = this.getAttribute('data-user-id'); // Получаем ID пользователя

        fetch(`/api/users/${userId}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (response.ok) {
                    console.log('Пользователь успешно удалён.');

                    // Удаляем строку из таблицы (при наличии)
                    const row = document.getElementById('user-row-${user.id}'); // Ищем строку таблицы
                    if (row) {
                        row.remove(); // Удаляем строку из DOM
                    }

                    // Закрываем модальное окно
                    const modalId = 'deleteModal' + userId; // Формируем ID модального окна
                    const modalElement = document.getElementById(modalId); // Получаем элемент модального окна
                    const modal = bootstrap.Modal.getInstance(modalElement); // Получаем экземпляр модального окна

                    if (modal) {
                        modal.hide(); // Если экземпляр найден, закрываем его
                    }
                } else {
                    console.error('Ошибка при удалении пользователя.');
                }
            })
            .catch(error => console.error('Ошибка сети:', error));
    });
});
