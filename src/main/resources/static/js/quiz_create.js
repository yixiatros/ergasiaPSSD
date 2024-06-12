{
    var questionIndex = 0;
    var questionCounter = 0;
    var answerCounter = 0;
}

onload = (event) => {
    addQuestion();
};

function trashAnswer(e) {
    const prnt = e.parentNode.parentNode;
    e.parentNode.parentNode.removeChild(e.parentNode);

    editNumberingOfAnswer(prnt);
}
function trash(e) {
    e.parentNode.parentNode.removeChild(e.parentNode);
}

function addAnswer(e) {
    const hidden = document.getElementById('answer');
    let newEl = hidden.cloneNode(true);

    e.parentNode.parentNode.children[0].appendChild(newEl);
    newEl.style.display = 'flex';

    // number the question visually (eg '1)')
    newEl.childNodes.item(1).textContent = e.parentNode.parentNode.children[0].children.length - 1 + ')';

    // edit the radio input name (eg 'question_2')
    newEl.childNodes.item(3).childNodes.item(1).name = 'radio_' + e.parentNode.parentNode.parentNode.id;
    newEl.childNodes.item(3).childNodes.item(1).value = e.parentNode.parentNode.children[0].children.length - 2;
    newEl.childNodes.item(3).childNodes.item(1).checked = true;

    newEl.id = 'answer_' + answerCounter + '_' + e.parentNode.parentNode.parentNode.id;

    const answer = document
        .querySelector('#answer_' + answerCounter + '_' + e.parentNode.parentNode.parentNode.id + ' > .input-custom');

    answer.setAttribute('name', 'answer_' + answerCounter + '_' + e.parentNode.parentNode.parentNode.id);
    answer.required = true;

    answerCounter += 1;

    // editNumberingOfAnswer(newEl.parentNode);
    return answer;
}

function editNumberingOfAnswer(el) {
    for (let i = 3; i < el.children.length + 2; i++) {
        console.log(i + ":" + el.children.length);
        console.log(el.childNodes.item(i).id);
        el.childNodes.item(i).childNodes.item(1).textContent = i - 2 + ')';
    }
}

function addQuestion() {
    const hidden = document.getElementById('question');
    const newEl = hidden.cloneNode(true);

    document.getElementById('newQuestion').appendChild(newEl);
    newEl.style.display = 'block';

    newEl.id = 'question_' + questionCounter;

    const question = document
        .querySelector('#question_' + questionCounter + ' > .question-container > .input-custom');

    question.setAttribute('name', 'question_' + questionCounter);
    question.required = true;

    questionCounter += 1;

    return question;
}
