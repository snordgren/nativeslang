function main() {
	autosize($("#comment-text-area"));
}

function insert(text) {
	const commentTextArea = document.getElementById("comment-text-area");
	const area = $("#comment-text-area");
	area.val(area.val() + text + text);
	const newPos = area.val().length - text.length;
	commentTextArea.focus();
	commentTextArea.setSelectionRange(newPos, newPos);
}

function onBoldClick() { insert("**"); }

function onItalicClick() { insert("*"); }

function onQuote() {
	const area = $("#comment-text-area");
	const postText = $("#quote-button").data("post-text");
	area.val(area.val() + postText);
	area.focus();
	autosize.update(area);
}

function onStrikeThroughClick() { insert("~~"); }
