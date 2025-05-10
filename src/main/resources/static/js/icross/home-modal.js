// 모달 열기 - AJAX를 이용하여 페이지 내용을 로드
function openModal(modalId, url) {
    var modal = document.getElementById(modalId);
    var contentDiv = modal.querySelector('.schedule-modal-content'); // iframe 대신 modal-content를 사용

    fetch("http://localhost:8080" + url) // 서버에 요청
        .then(response => response.text()) // 응답을 텍스트로 변환
        .then(html => {
            contentDiv.innerHTML = html; // 모달 내용에 HTML 삽입
            modal.style.display = "block"; // 모달 열기
        })
        .catch(error => console.log('Error:', error));
}
// 모달 외부 클릭 시 닫기
window.onclick = function(event) {
    var modal = document.getElementById('scheduleModal');
    if (event.target == modal) {
        modal.style.display = "none";
    }
}

// 일정 체크박스 상태 변경
function checkboxSchedule(checkboxElem) {
    const scheduleId = checkboxElem.getAttribute('data-id');
    const targetUrl = `/api/i-cross/schedule/${scheduleId}`;

    fetch(targetUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include' // 인증 쿠키 포함 필요 시 사용
    })
        .then(response => response.json())
        .then(data => {
            if (data.status === 201) {
                console.log("스케줄 완료 처리 성공");
            } else {
                alert("스케줄 완료 처리 실패: " + data.message);
                checkboxElem.checked = false; // 실패 시 체크 해제
            }
        })
        .catch(error => {
            alert("네트워크 오류");
            checkboxElem.checked = false; // 실패 시 체크 해제
        });
}


// 일정 삭제
function deleteSchedule(scheduleId) {
    const confirmation = confirm("해당 스케줄을 삭제하시겠습니까?");
    if (!confirmation) return;

    console.log(scheduleId);

    fetch(`/api/i-cross/schedule/${scheduleId}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include' // 인증 세션 유지 필요 시
    })
        .then(response => response.json())
        .then(data => {
            if (data.status === 204) {
                location.reload();
            } else {
                alert("스케줄 삭제 처리 실패: " + data.message);

            }
        })
        .catch(error => {
            alert("네트워크 오류");
        });
}

// 스케줄 재생성 버튼
// 버튼 클릭 시 폼 제출
document.querySelector('.retry-btn').addEventListener('click', function(event) {
    event.preventDefault(); // 기본 폼 제출 방지
    document.getElementById('retry-form').submit(); // 폼 제출
});