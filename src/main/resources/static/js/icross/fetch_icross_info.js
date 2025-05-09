function showLoading() {
    document.getElementById('loading').style.display = 'block';
}

function hideLoading() {
    document.getElementById('loading').style.display = 'none';
}

function displayError(message) {
    const errorMessageDiv = document.getElementById('errorMessage');
    document.getElementById('errorText').innerText = message;
    errorMessageDiv.style.display = 'block';

    let countdown = 3;
    const countdownText = document.getElementById('countdownText');
    countdownText.innerText = `3초 후에 I-Class 계정 등록 페이지로 리디렉션 됩니다...`;

    const countdownInterval = setInterval(() => {
        countdown--;
        countdownText.innerText = `${countdown}초 후에 I-Class 계정 등록 페이지로 리디렉션 됩니다...`;
        if (countdown === 0) {
            clearInterval(countdownInterval);
            window.location.href = "/i-cross/univ-info";
        }
    }, 1000);
}

function submitAndReload() {
    showLoading();
    fetch('/api/i-cross/login', {
        method: 'GET',
    })
        .then(response => response.json())
        .then(data => {
            hideLoading();
            if (data.status === 400) {
                console.log('500error');
                displayError(data.message);
            } else {
                window.location.href = "/i-cross";
            }
        })
        .catch((error) => {
            hideLoading();
            displayError('Error: ' + error.message);
        });
}

function submitAndReload() {
    showLoading();
    fetch('/api/i-cross/login', {
        method: 'GET',
    })
        .then(response => response.json())
        .then(data => {
            if (data.status === 400) {
                hideLoading();
                console.log('Login 400 error');
                displayError(data.message);
            } else {
                // 로그인 성공 시 tasks 요청
                fetch('/api/i-cross/tasks', {
                    method: 'GET',
                })
                    .then(response => response.json())
                    .then(taskData => {
                        hideLoading();
                        if (taskData.status === 400) {
                            displayError(taskData.message);
                        } else {
                            // 모든 요청 성공 시 리다이렉트
                            window.location.href = "/i-cross";
                        }
                    })
                    .catch((error) => {
                        hideLoading();
                        displayError('Tasks Error: ' + error.message);
                    });
            }
        })
        .catch((error) => {
            hideLoading();
            displayError('Login Error: ' + error.message);
        });
}


document.addEventListener("DOMContentLoaded", function() {
    submitAndReload();
});