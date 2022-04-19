"use strict";

// Class definition

var KTKanbanBoardDemo = function () {
    // Private functions
    var _demo1 = function () {
        var kanban = new jKanban({
            element: '#kt_kanban_1',
            gutter: '0',
            widthBoard: '250px',
            boards: [{
                    'id': '_inprocess',
                    'title': 'In Process',
                    'item': [{
                            'title': '<span class="font-weight-bold">You can drag me too</span>'
                        },
                        {
                            'title': '<span class="font-weight-bold">Buy Milk</span>'
                        }
                    ]
                }, {
                    'id': '_working',
                    'title': 'Working',
                    'item': [{
                            'title': '<span class="font-weight-bold">Do Something!</span>'
                        },
                        {
                            'title': '<span class="font-weight-bold">Run?</span>'
                        }
                    ]
                }, {
                    'id': '_done',
                    'title': 'Done',
                    'item': [{
                            'title': '<span class="font-weight-bold">All right</span>'
                        },
                        {
                            'title': '<span class="font-weight-bold">Ok!</span>'
                        }
                    ]
                }
            ]
        });
    }

    var _demo2 = function () {
        var kanban = new jKanban({
            element: '#kt_kanban_2',
            gutter: '0',
            widthBoard: '250px',
            boards: [{
                    'id': '_inprocess',
                    'title': 'In Process',
                    'class': 'primary',
                    'item': [{
                            'title': '<span class="font-weight-bold">You can drag me too</span>',
                            'class': 'light-primary',
                        },
                        {
                            'title': '<span class="font-weight-bold">Buy Milk</span>',
                            'class': 'light-primary',
                        }
                    ]
                }, {
                    'id': '_working',
                    'title': 'Working',
                    'class': 'success',
                    'item': [{
                            'title': '<span class="font-weight-bold">Do Something!</span>',
                            'class': 'light-success',
                        },
                        {
                            'title': '<span class="font-weight-bold">Run?</span>',
                            'class': 'light-success',
                        }
                    ]
                }, {
                    'id': '_done',
                    'title': 'Done',
                    'class': 'danger',
                    'item': [{
                            'title': '<span class="font-weight-bold">All right</span>',
                            'class': 'light-danger',
                        },
                        {
                            'title': '<span class="font-weight-bold">Ok!</span>',
                            'class': 'light-danger',
                        }
                    ]
                }
            ]
        });
    }

    var _demo3 = function () {
        var proyectoID = $("#idPt").val();
        $.ajax({
            url: "/tablero/lista/" + proyectoID,
            type: "get",
            dataType: "html",
            cache: false,
            contentType: false,
            processData: false
        }).done(function (res) {
            var kanban = new jKanban({
                element: '#kt_kanban_3',
                gutter: '0',
                widthBoard: '250px',
                dropEl: function (el, target, source, sibling) {
                    console.log(el)
                    var boardFinal = el.parentNode.parentNode;
                    var idBoard = boardFinal.dataset.id;
                    var idTarea = el.dataset.tarea;
                    switch (idBoard) {
                        case "_todo":
                            el.dataset.class = "primary toDo";
                            break;
                        case "_working":
                            el.dataset.class = "warning work";
                            break;
                        case "_done":
                            el.dataset.class = "success Done";
                            break;
                        case "_notes":
                            el.dataset.class = "danger Not";
                            break;
                    }
                    $.ajax({
                        url: "/tablero/cambio/" + idTarea+"/"+idBoard,
                        type: "get",
                        dataType: "html",
                        cache: false,
                        contentType: false,
                        processData: false
                    });
                },
                boards: JSON.parse(res),
                dragBoards: false,
                itemAddOptions: {
                    enabled: true, // add a button to board for easy item creation
                    content: '+', // text or html content of the board button   
                    class: 'kanban-title-button btn btn-default btn-xs', // default class of the button
                    footer: false                                                // position the button on footer
                }
            });
            var validator = FormValidation.formValidation(
                    document.getElementById('formTablero'),
                    {
                        fields: {
                            tarea: {
                                validators: {
                                    notEmpty: {
                                        message: 'Título requerido'
                                    }
                                }
                            }
                        },

                        plugins: {//Learn more: https://formvalidation.io/guide/plugins
                            trigger: new FormValidation.plugins.Trigger(),
                            // Bootstrap Framework Integration
                            bootstrap: new FormValidation.plugins.Bootstrap(),
                            excluded: new FormValidation.plugins.Excluded(),
                            // Validate fields when clicking the Submit button
                            submitButton: new FormValidation.plugins.SubmitButton(),
                            // Submit the form when all fields are valid
                            //defaultSubmit: new FormValidation.plugins.DefaultSubmit(),
                        }
                    }
            ).on('core.form.valid', function () {
                var tipo = $("#valueTipo").val();
                var tarea = $("#tarea").val();
                var nombreUser = $("#nombreUser").val();
                formarItem(tipo, tarea, nombreUser);

                var form_data = new FormData($("form#formTablero")[0]);
                form_data.append("dato", "valor");
                $("#modalToDO").modal("hide");
                $("#tarea").val("");
                //formData.append(f.attr("name"), $(this)[0].files[0]);
                $.ajax({
                    url: "/tablero/crearTarea",
                    type: "post",
                    dataType: "html",
                    data: form_data,
                    cache: false,
                    contentType: false,
                    processData: false
                }).done(function (res) {
                    console.log("exito");

                }).fail(function () {
                    console.log("error");
                });

            });

            var toDoButton = document.getElementById('_todobutton');

            toDoButton.addEventListener('click', function () {
                $("#modalToDO").modal("show");
                $("#valueTipo").val("_todo");

            });
            var workButton = document.getElementById('_workingbutton');
            workButton.addEventListener('click', function () {
                $("#modalToDO").modal("show");
                $("#valueTipo").val("_working");

            });
            var doneButton = document.getElementById('_donebutton');
            doneButton.addEventListener('click', function () {
                $("#modalToDO").modal("show");
                $("#valueTipo").val("_done");

            });
            var noteButton = document.getElementById('_notesbutton');
            noteButton.addEventListener('click', function () {
                $("#modalToDO").modal("show");
                $("#valueTipo").val("_notes");

            });

            function formarItem(tipo, tarea, nombreUser) {
                switch (tipo) {
                    case "_todo":
                        kanban.addElement(
                                '_todo', {
                                    'title': tarea + '<br> <span class="label label-inline label-light-success font-weight-bold">' + nombreUser + '</span>',
                                    'class': 'primary toDo'
                                }
                        );
                        break;
                    case "_working":
                        kanban.addElement(
                                '_working', {
                                    'title': tarea + '<br> <span class="label label-inline label-light-success font-weight-bold">' + nombreUser + '</span>',
                                    'class': 'warning work'
                                }
                        );
                        break;
                    case "_done":
                        kanban.addElement(
                                '_done', {
                                    'title': tarea + '<br> <span class="label label-inline label-light-success font-weight-bold">' + nombreUser + '</span>',
                                    'class': 'success Done'
                                }
                        );
                        break;
                    case "_notes":
                        kanban.addElement(
                                '_notes', {
                                    'title': tarea + '<br> <span class="label label-inline label-light-success font-weight-bold">' + nombreUser + '</span>',
                                    'class': 'danger Not'
                                }
                        );
                        break;
                }
            }
        }).fail(function () {
            console.log("error");
        });

    };

    var _demo4 = function () {
        var kanban = new jKanban({
            element: '#kt_kanban_4',
            gutter: '0',
            click: function (el) {
                alert(el.innerHTML);
            },
            boards: [{
                    'id': '_backlog',
                    'title': 'Backlog',
                    'class': 'light-dark',
                    'item': [{
                            'title': `
                                <div class="d-flex align-items-center">
                        	        <div class="symbol symbol-success mr-3">
                        	            <img alt="Pic" src="assets/media/users/300_24.jpg" />
                        	        </div>
                        	        <div class="d-flex flex-column align-items-start">
                        	            <span class="text-dark-50 font-weight-bold mb-1">SEO Optimization</span>
                        	            <span class="label label-inline label-light-success font-weight-bold">In progress</span>
                        	        </div>
                        	    </div>
                            `,
                        },
                        {
                            'title': `
                                <div class="d-flex align-items-center">
                        	        <div class="symbol symbol-success mr-3">
                        	            <span class="symbol-label font-size-h4">A.D</span>
                        	        </div>
                        	        <div class="d-flex flex-column align-items-start">
                        	            <span class="text-dark-50 font-weight-bold mb-1">Finance</span>
                        	            <span class="label label-inline label-light-danger font-weight-bold">Pending</span>
                        	        </div>
                        	    </div>
                            `,
                        }
                    ]
                },
                {
                    'id': '_todo',
                    'title': 'To Do',
                    'class': 'light-danger',
                    'item': [{
                            'title': `
                                <div class="d-flex align-items-center">
                        	        <div class="symbol symbol-success mr-3">
                        	            <img alt="Pic" src="assets/media/users/300_16.jpg" />
                        	        </div>
                        	        <div class="d-flex flex-column align-items-start">
                        	            <span class="text-dark-50 font-weight-bold mb-1">Server Setup</span>
                        	            <span class="label label-inline label-light-dark font-weight-bold">Completed</span>
                        	        </div>
                        	    </div>
                            `,
                        },
                        {
                            'title': `
                                <div class="d-flex align-items-center">
                        	        <div class="symbol symbol-success mr-3">
                        	            <img alt="Pic" src="assets/media/users/300_15.jpg" />
                        	        </div>
                        	        <div class="d-flex flex-column align-items-start">
                        	            <span class="text-dark-50 font-weight-bold mb-1">Report Generation</span>
                        	            <span class="label label-inline label-light-warning font-weight-bold">Due</span>
                        	        </div>
                        	    </div>
                            `,
                        }
                    ]
                },
                {
                    'id': '_working',
                    'title': 'Working',
                    'class': 'light-primary',
                    'item': [{
                            'title': `
                                <div class="d-flex align-items-center">
                        	        <div class="symbol symbol-success mr-3">
                            	         <img alt="Pic" src="assets/media/users/300_24.jpg" />
                        	        </div>
                        	        <div class="d-flex flex-column align-items-start">
                        	            <span class="text-dark-50 font-weight-bold mb-1">Marketing</span>
                        	            <span class="label label-inline label-light-danger font-weight-bold">Planning</span>
                        	        </div>
                        	    </div>
                            `,
                        },
                        {
                            'title': `
                                <div class="d-flex align-items-center">
                        	        <div class="symbol symbol-light-info mr-3">
                        	            <span class="symbol-label font-size-h4">A.P</span>
                        	        </div>
                        	        <div class="d-flex flex-column align-items-start">
                        	            <span class="text-dark-50 font-weight-bold mb-1">Finance</span>
                        	            <span class="label label-inline label-light-primary font-weight-bold">Done</span>
                        	        </div>
                        	    </div>
                            `,
                        }
                    ]
                },
                {
                    'id': '_done',
                    'title': 'Done',
                    'class': 'light-success',
                    'item': [{
                            'title': `
                                <div class="d-flex align-items-center">
                        	        <div class="symbol symbol-success mr-3">
                        	            <img alt="Pic" src="assets/media/users/300_11.jpg" />
                        	        </div>
                        	        <div class="d-flex flex-column align-items-start">
                        	            <span class="text-dark-50 font-weight-bold mb-1">SEO Optimization</span>
                        	            <span class="label label-inline label-light-success font-weight-bold">In progress</span>
                        	        </div>
                        	    </div>
                            `,
                        },
                        {
                            'title': `
                                <div class="d-flex align-items-center">
                        	        <div class="symbol symbol-success mr-3">
                        	            <img alt="Pic" src="assets/media/users/300_20.jpg" />
                        	        </div>
                        	        <div class="d-flex flex-column align-items-start">
                        	            <span class="text-dark-50 font-weight-bold mb-1">Product Team</span>
                        	            <span class="label label-inline label-light-danger font-weight-bold">In progress</span>
                        	        </div>
                        	    </div>
                            `,
                        }
                    ]
                },
                {
                    'id': '_deploy',
                    'title': 'Deploy',
                    'class': 'light-primary',
                    'item': [{
                            'title': `
                                <div class="d-flex align-items-center">
                        	        <div class="symbol symbol-light-warning mr-3">
                        	            <span class="symbol-label font-size-h4">D.L</span>
                        	        </div>
                        	        <div class="d-flex flex-column align-items-start">
                        	            <span class="text-dark-50 font-weight-bold mb-1">SEO Optimization</span>
                        	            <span class="label label-inline label-light-success font-weight-bold">In progress</span>
                        	        </div>
                        	    </div>
                            `,
                        },
                        {
                            'title': `
                                <div class="d-flex align-items-center">
                        	        <div class="symbol symbol-light-danger mr-3">
                        	            <span class="symbol-label font-size-h4">E.K</span>
                        	        </div>
                        	        <div class="d-flex flex-column align-items-start">
                        	            <span class="text-dark-50 font-weight-bold mb-1">Requirement Study</span>
                        	            <span class="label label-inline label-light-warning font-weight-bold">Scheduled</span>
                        	        </div>
                        	    </div>
                            `,
                        }
                    ]
                }
            ]
        });

        var toDoButton = document.getElementById('addToDo');
        toDoButton.addEventListener('click', function () {
            kanban.addElement(
                    '_todo', {
                        'title': `
                        <div class="d-flex align-items-center">
                            <div class="symbol symbol-light-primary mr-3">
                                <img alt="Pic" src="assets/media/users/300_14.jpg" />
                            </div>
                            <div class="d-flex flex-column align-items-start">
                                <span class="text-dark-50 font-weight-bold mb-1">Requirement Study</span>
                                <span class="label label-inline label-light-success font-weight-bold">Scheduled</span>
                            </div>
                        </div>
                    `
                    }
            );
        });

        var addBoardDefault = document.getElementById('addDefault');
        addBoardDefault.addEventListener('click', function () {
            kanban.addBoards(
                    [{
                            'id': '_default',
                            'title': 'New Board',
                            'class': 'primary-light',
                            'item': [{
                                    'title': `
                                <div class="d-flex align-items-center">
                                    <div class="symbol symbol-success mr-3">
                                        <img alt="Pic" src="assets/media/users/300_13.jpg" />
                                    </div>
                                    <div class="d-flex flex-column align-items-start">
                                        <span class="text-dark-50 font-weight-bold mb-1">Payment Modules</span>
                                        <span class="label label-inline label-light-primary font-weight-bold">In development</span>
                                    </div>
                                </div>
                        `}, {
                                    'title': `
                                <div class="d-flex align-items-center">
                                    <div class="symbol symbol-success mr-3">
                                        <img alt="Pic" src="assets/media/users/300_12.jpg" />
                                    </div>
                                    <div class="d-flex flex-column align-items-start">
                                    <span class="text-dark-50 font-weight-bold mb-1">New Project</span>
                                    <span class="label label-inline label-light-danger font-weight-bold">Pending</span>
                                </div>
                            </div>
                        `}
                            ]
                        }]
                    )
        });

        var removeBoard = document.getElementById('removeBoard');
        removeBoard.addEventListener('click', function () {
            kanban.removeBoard('_done');
        });
    }

    // Public functions
    return {
        init: function () {
            //_demo1();
            //_demo2();
            _demo3();
            //_demo4();
        }
    };
}();

jQuery(document).ready(function () {
    KTKanbanBoardDemo.init();
});
