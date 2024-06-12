function onTableRowClick(value) {
    location.href = value;
}

function onQuizIdClick(value) {
    navigator.clipboard.writeText(value);

    event.stopPropagation();
}