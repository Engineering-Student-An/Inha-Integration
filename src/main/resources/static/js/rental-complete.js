function cancel(id) {
    if (!confirm("해당 물품을 반납 처리 하시겠습니까?\n반납이 이루어지지 않았을 경우, 불이익이 주어질 수 있습니다!")) {
        return; // 사용자가 아니오를 눌렀을 경우 취소
    }

    var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", "rental/" + id);

    // Spring에서 DELETE 메서드를 처리하도록 하기 위해 hidden input 추가
    var hiddenInput = document.createElement("input");
    hiddenInput.setAttribute("type", "hidden");
    hiddenInput.setAttribute("name", "_method");
    hiddenInput.setAttribute("value", "delete");
    form.appendChild(hiddenInput);

    document.body.appendChild(form);
    form.submit();
}