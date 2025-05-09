const emailVerify = document.getElementById('emailInput');
const submitButton = document.getElementById('submitButton');

emailVerify.addEventListener('input', () => {
    submitButton.disabled = emailVerify.value.trim() === "";
});