{
    var questionCounter = 0;
    var answerCounter = 0;
}

onload = (event) => {
    addQuestion();
};

function trash(e) {
    e.parentNode.parentNode.removeChild(e.parentNode);
}

function addAnswer(e) {
    const hidden = document.getElementById('answer');
    const newEl = hidden.cloneNode(true);

    e.parentNode.parentNode.children[0].appendChild(newEl);
    newEl.style.display = 'flex';

    newEl.id = 'answer_' + answerCounter + '_' + e.parentNode.parentNode.parentNode.id;

    const answer = document
        .querySelector('#answer_' + answerCounter + '_' + e.parentNode.parentNode.parentNode.id + ' > .input-custom');

    answer.setAttribute('name', 'answer_' + answerCounter + '_' + e.parentNode.parentNode.parentNode.id);
    answer.required = true;

    answerCounter += 1;
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
}
