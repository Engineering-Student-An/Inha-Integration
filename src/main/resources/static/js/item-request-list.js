document.addEventListener('DOMContentLoaded', function () {
    let mainSwitchButton = document.getElementById('write-btn');

    mainSwitchButton.addEventListener('mouseover', () => {
        mainSwitchButton.classList.add('active');
    });

    mainSwitchButton.addEventListener('mouseout', () => {
        mainSwitchButton.classList.remove('active');
    });

    mainSwitchButton.addEventListener('click', function (event) {
        window.location.href='/item/request';
    });
});

document.addEventListener("DOMContentLoaded", function () {
    const checkRequestBtns = document.querySelectorAll(".checkRequestBtn");

    checkRequestBtns.forEach(btn => {
        btn.addEventListener("click", function () {
            const itemRequestId = this.getAttribute("data-id");
            console.log('id : ' + itemRequestId);
            fetch(`/api/admin/item/request/${itemRequestId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                }
            })
                .then(response => {
                    if (response.ok) {
                        alert("요청이 확인되었습니다.");
                        window.location.href = '/item/requests';
                    } else {
                        alert("요청 확인에 실패했습니다.");
                    }
                })
                .catch(error => {
                    console.error("오류 발생:", error);
                    alert("서버와의 통신 중 오류가 발생했습니다.");
                });
        });
    });
});
