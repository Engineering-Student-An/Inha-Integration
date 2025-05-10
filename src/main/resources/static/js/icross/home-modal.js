// 모달 열기 - AJAX를 이용하여 페이지 내용을 로드
function openModal(modalId, url) {
    var modal = document.getElementById(modalId);
    var contentDiv = modal.querySelector('.modal-content'); // iframe 대신 modal-content를 사용

    fetch("http://ec2-13-209-198-107.ap-northeast-2.compute.amazonaws.com:8082" + url) // 서버에 요청
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
    var scheduleId = checkboxElem.getAttribute('data-id');
    var targetUrl = '/schedule/' + scheduleId + '/completed';
    window.location.href = targetUrl; // 현재 페이지를 새 URL로 리디렉션
}

// 일정 삭제
function deleteSchedule(scheduleId) {
    const confirmation = confirm("해당 스케줄을 삭제하시겠습니까?");
    if (confirmation) {
        window.location.href = `/schedule/${scheduleId}/delete`;
    }
}

// 스케줄 재생성 버튼
// 버튼 클릭 시 폼 제출
document.querySelector('.retry-btn').addEventListener('click', function(event) {
    event.preventDefault(); // 기본 폼 제출 방지
    document.getElementById('retry-form').submit(); // 폼 제출
});