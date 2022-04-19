var KTFormControls = function () {
    // Private functions
    var _initDemo1 = function () {
        var form = document.getElementById('modalForm');
        var validator = FormValidation.formValidation(
                document.getElementById('modalForm'),
                {
                    fields: {
                        titulo: {
                            validators: {
                                notEmpty: {
                                    message: 'Título requerido'
                                }
                            }
                        },
                        tipoRecurso: {
                            validators: {
                                notEmpty: {
                                    message: 'Tipo Requerido'
                                }
                            }
                        },
                        urlPag: {
                            validators: {
                                notEmpty: {
                                    message: 'URL requerida'
                                },
                                uri: {
                                    message: 'La dirección del sitio web no es válida.'
                                }
                            }
                        },
                        file: {
                            validators: {
                                notEmpty: {
                                    message: 'Subir al menos 1 archivo'
                                },
                                file: {
                                    extension: 'jpeg,jpg,png,doc,pdf,xlsx,xls,ppt,rar,zip,docx',
                                    maxSize: 5000000, // 2048 * 1024
                                    message: 'El elemento seleccionado no es válido',
                                },
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
            console.log($("form#modalForm"))
            var form_data = new FormData($("form#modalForm")[0]);
            form_data.append("dato", "valor");
            //formData.append(f.attr("name"), $(this)[0].files[0]);
            $.ajax({
                url: "/recurso/crear",
                type: "post",
                dataType: "html",
                data: form_data,
                cache: false,
                contentType: false,
                processData: false
            }).done(function (res) {
                console.log("exito");
                window.location.reload();
            }).fail(function () {
                console.log("error");
            });

        });


    };

    function mensaje() {
        var mensaje = `<span class="svg-icon svg-icon-primary svg-icon-2x"><svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="24px" height="24px" viewBox="0 0 24 24" version="1.1">
            <g stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                <g>
                  <polygon points="0 0 24 0 24 24 0 24"/>
                </g>
                <path d="M12,4 L12,6 C8.6862915,6 6,8.6862915 6,12 C6,15.3137085 8.6862915,18 12,18 C15.3137085,18 18,15.3137085 18,12 C18,10.9603196 17.7360885,9.96126435 17.2402578,9.07513926 L18.9856052,8.09853149 C19.6473536,9.28117708 20,10.6161442 20,12 C20,16.418278 16.418278,20 12,20 C7.581722,20 4,16.418278 4,12 C4,7.581722 7.581722,4 12,4 Z" fill="#000000" fill-rule="nonzero" opacity="0.3" transform="translate(12.000000, 12.000000) scale(-1, 1) translate(-12.000000, -12.000000) ">
                  <animateTransform attributeName="transform" type="rotate" from="0 12 12" to="360 12 12" dur="0.7s" repeatCount="indefinite"/>
                </path>
            </g>
        </svg></span> Cargando `;
        $('#kt_uppy_5 .uppy-input-label').text("");
        $('#kt_uppy_5 .uppy-input-label').append(mensaje);
        $("#spanLabel").hide();
        setTimeout(() => {
            $('#kt_uppy_5 .uppy-input-label').hide();
            $("#kt_uppy_5 .uppy-input-label").empty();
            var imagen = document.getElementsByClassName("subirimagen")[0].files[0];
            var uploadListHtml = '<div class="uppy-list-item" data-id="imagen-file"><div class="uppy-list-label">' + imagen.name + ' (' + parseInt(imagen.size / 1024) + 'KB)</div><span class="uppy-list-remove" data-id="imagen-file"><i class="flaticon2-cancel-music"></i></span></div>';
            $("#wrapper").hide();
            $("#kt_uppy_5 .uppy-list").append(uploadListHtml);
        }, 2000);
    }
    $(document).on("change", "#kt_uppy_5_input_control", function (event) {
        var imagen = document.getElementsByClassName("subirimagen")[0].files[0];
        var filePath = imagen.name;
        var allowedExtensions = /(.jpg|.jpeg|.png|.doc|.pdf|.xlsx|.xls|.ppt|.rar|.zip|.docx)$/i;
        if (imagen.size < 5000000 && allowedExtensions.exec(filePath)) {
            mensaje();
        }

    });
    $(document).on('click', '#kt_uppy_5 .uppy-list .uppy-list-remove', function () {
        var itemId = $(this).attr('data-id');
        $("#kt_uppy_5_input_control").val("");
        $('#kt_uppy_5 .uppy-input-label').text("Adjuntar Archivo");
        $('#kt_uppy_5 .uppy-input-label').show();
        $("#spanLabel").show();
        $("#wrapper").show();
        $('#kt_uppy_5 .uppy-list-item[data-id="' + itemId + '"').remove();
    });

    $(document).on("change", "#tipoRecurso", function (event) {
        event.preventDefault();
        var x = document.getElementById("tipoRecurso").value;
        if (x === "web") {
            $("#tipoWeb").show();
            $("#urlPage").prop("disabled", false);
            removerArchivo();
            $("#tipoArchivo").hide();
        } else if (x === "archivo") {
            $("#tipoWeb").hide();
            $("#urlPage").prop("disabled", true);
            $("#urlPage").val("");
            $("#tipoArchivo").show();

        }
    });
    function removerArchivo() {
        var itemId = $("span.uppy-list-remove").attr('data-id');
        $("#kt_uppy_5_input_control").val("");
        $('#kt_uppy_5 .uppy-input-label').text("Adjuntar Archivo");
        $('#kt_uppy_5 .uppy-input-label').show();
        $("#spanLabel").show();
        $("#wrapper").show();
        $('#kt_uppy_5 .uppy-list-item[data-id="' + itemId + '"').remove();
    }
    
    
    return {
        // public functions
        init: function () {
            _initDemo1();
        }
    };
}();

jQuery(document).ready(function () {
    KTFormControls.init();
    
});

function mostrarModal(idProyec) {
    $('#'+idProyec).modal('show'); 
}