function checkboxTask(checkboxElem) {
    if (checkboxElem.checked) {

        const confirmation = confirm("해당 할 일을 완료 처리 하시겠습니까?");
        if (!confirmation) return;

        var webId = checkboxElem.getAttribute('data-id');
        var targetUrl = '/api/i-cross/task/' + webId;

        fetch(targetUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include' // 인증 쿠키 포함 필요 시 사용
        })
            .then(response => response.json())
            .then(data => {
                if (data.status === 200) {
                    location.reload();
                } else {
                    alert("할 일 완료 처리 실패: " + data.message);
                    checkboxElem.checked = false; // 실패 시 체크 해제
                }
            })
            .catch(error => {
                alert("네트워크 오류");
                checkboxElem.checked = false; // 실패 시 체크 해제
            });
    }
}