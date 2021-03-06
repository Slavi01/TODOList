
    (function (root) {
        'use strict';
        if (!root) return;

        var api = {
            request: function (method, endpoint, data, onSuccess, onError) {
                var r = new XMLHttpRequest();
                r.overrideMimeType('application/json');
                r.open(method, window.location.origin + '/api' + endpoint, true);
                r.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
                r.addEventListener('load', function () {
                    var d = {};
                    try {
                        d = JSON.parse(r.responseText);
                    } catch (e) {};
                    if (onSuccess) onSuccess(d);
                });
                if (onError) {
                    r.addEventListener('error', function () {
                        onError();
                    });
                }
                r.send((typeof data === 'object' ? JSON.stringify(data) : null));
            }
        };

        var selectors = {
            newForm: root.querySelector('.todo-new'),
            newContent: root.querySelector('.todo-new .todo-new__content'),
            taskList: root.querySelector('.todo-tasks')
        };
        function renderTask(state) {
            var el = document.createElement('div');
            el.className = 'todo-task';
            el.innerHTML = '<input type="checkbox" class="todo-task__done"><div class="todo-task__content"></div><button type="button" class="todo-task__remove">&times;</button>';
            var tpl = {
                content: el.querySelector('.todo-task__content'),
                remove: el.querySelector('.todo-task__remove'),
                done: el.querySelector('.todo-task__done')
            };

            if (!('id' in state)) state.id = null;
            if (!('content' in state)) state.content = ''; else state.content = String(state.content);
            if (!('done' in state)) state.done = false; else state.done = !!state.done;

            tpl.content.innerText = state.content;
            tpl.done.checked = state.done;

            tpl.remove.addEventListener('click', function () {
                if (!state.id) return;

                el.parentNode.removeChild(el);
                api.request('DELETE', '/' + state.id);
            });
            tpl.done.addEventListener('change', function () {
                if (!state.id) return;

                state.done = tpl.done.checked;
                api.request('POST', '/' + state.id, {
                    done: state.done,
                });
            });
            selectors.taskList.appendChild(el);

            if (!state.id) {
                // If no id is provided, this means the user has just added
                // a new task, so we're adding it in the backend as well
                api.request('PUT', '/add', {
                    content: state.content
                }, function (response) {
                    if (!('id' in response)) return;
                    state.id = response.id;
                });
            }
        }
        selectors.newForm.addEventListener('submit', function (e) {
            e.preventDefault();
            var content = selectors.newContent.value.trim();
            if (content.length === 0) return;

            renderTask({
                content: content
            });
            selectors.newContent.value = '';
        });

        api.request('GET', '/list', null, function (response) {
            if (!('list' in response)) return;

            for (var i = 0; i < response.list.length; i++) {
                var task = response.list[i];
                if (!('id' in task && 'content' in task && 'done' in task)) continue;

                renderTask(task);
            }
        });
    })(document.getElementsByClassName('todo')[0]);