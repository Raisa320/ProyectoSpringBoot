// Class definition
var KTFormControls = function () {
    // Private functions
    var email = $("#email").val();
    var form = document.getElementById('formEditar');
    var _initDemo1 = function () {
        var validator = FormValidation.formValidation(
                document.getElementById('formEditar'),
                {
                    fields: {
                        email: {
                            validators: {
                                notEmpty: {
                                    message: 'Correo requerido'
                                },
                                emailAddress: {
                                    message: 'El valor no es un correo válido'
                                }
                            }
                        },
                        nombre: {
                            validators: {
                                notEmpty: {
                                    message: 'Nombre requerido'
                                }
                            }
                        },
                        apellido: {
                            validators: {
                                notEmpty: {
                                    message: 'Apellido requerido'
                                }
                            }
                        },
                        sexo: {
                            validators: {
                                notEmpty: {
                                    message: 'Sexo requerido'
                                }
                            }
                        },
                        cicloEstudios: {
                            validators: {
                                notEmpty: {
                                    message: 'Ciclo de Estudios requerido'
                                }
                            }
                        },
                        nivel: {
                            validators: {
                                notEmpty: {
                                    message: 'Nivel requerido'
                                }
                            }
                        },
                        rol: {
                            validators: {
                                notEmpty: {
                                    message: 'Rol requerido'
                                }
                            }
                        },
                        temas: {
                            validators: {
                                notEmpty: {
                                    message: 'Temas requeridos'
                                }
                            }
                        },
                        lenguajes: {
                            validators: {
                                notEmpty: {
                                    message: 'Lenguajes requeridos'
                                }
                            }
                        }
                    },

                    plugins: {//Learn more: https://formvalidation.io/guide/plugins
                        trigger: new FormValidation.plugins.Trigger(),
                        // Bootstrap Framework Integration
                        bootstrap: new FormValidation.plugins.Bootstrap(),
                        // Validate fields when clicking the Submit button
                        submitButton: new FormValidation.plugins.SubmitButton(),
                        // Submit the form when all fields are valid
                        // defaultSubmit: new FormValidation.plugins.DefaultSubmit(),
                    }
                }
        ).on('core.form.valid', function () {
            var email2 = $("#email").val();
            if (email === email2) {
                form.submit();
            } else {
                var request = $.ajax({
                    url: "/verificaEmail/" + email,
                    type: "get"
                });
                request.done(function (response, textStatus, jqXHR) {
                    console.log(response);
                    if (response === "si") {
                        form.submit();
                    } else {
                        Swal.fire({
                            text: "El correo que proporciona ya se encuentra registrado, intente con otro.",
                            icon: "error",
                            buttonsStyling: false,
                            confirmButtonText: "Ok, entendido!",
                            customClass: {
                                confirmButton: "btn font-weight-bold btn-light"
                            }
                        });
                    }

                });
                request.fail(function (jqXHR, textStatus) {
                    Swal.fire({
                        text: "¡Error en el servidor!.",
                        icon: "error",
                        buttonsStyling: false,
                        confirmButtonText: "Ok, entendido!",
                        customClass: {
                            confirmButton: "btn font-weight-bold btn-primary",
                        }
                    });
                });
            }

        });
        $('#selectSexo').on('change', function () {
            // Revalidate field
            validator.revalidateField('sexo');
        });
        $('#selectCiclo').on('change', function () {
            // Revalidate field
            validator.revalidateField('cicloEstudios');
        });
        $('#kt_select2_10').on('change', function () {
            // Revalidate field
            validator.revalidateField('nivel');
        });
        $('#selectRol').on('change', function () {
            // Revalidate field
            validator.revalidateField('rol');
        });
        $('#selectLengaujes').on('change', function () {
            // Revalidate field
            validator.revalidateField('lenguajes');
        });
    };

    var _initDemo2 = function () {
        var form = document.getElementById('formContra');
        var validator2 = FormValidation.formValidation(
                form,
                {
                    fields: {
                        contrsActual: {
                            validators: {
                                notEmpty: {
                                    message: 'Contraseña Actual Requerida'
                                }
                            }
                        },
                        contraNueva: {
                            validators: {
                                notEmpty: {
                                    message: 'Nueva Contraseña Requerida'
                                },
                                different: {
                                    compare: function () {
                                        return form.querySelector('[name="contrsActual"]').value;
                                    },
                                    message: 'La Contraseña debe ser distinta a la Actual'
                                },
                                stringLength: {
                                    min: 6,
                                    message: 'La contraseña debe ser al menos de 6 caracteres',
                                }
                            }
                        },
                        contraRepetida: {
                            validators: {
                                notEmpty: {
                                    message: 'Confirmación de Contraseña Requerida'
                                },
                                identical: {
                                    compare: function () {
                                        return form.querySelector('[name="contraNueva"]').value;
                                    },
                                    message: 'Contraseñas Distintas'
                                }
                            }
                        }
                    },

                    plugins: {
                        trigger: new FormValidation.plugins.Trigger(),
                        // Validate fields when clicking the Submit button
                        submitButton: new FormValidation.plugins.SubmitButton(),
                        // Submit the form when all fields are valid
                        //defaultSubmit: new FormValidation.plugins.DefaultSubmit(),
                        // Bootstrap Framework Integration
                        bootstrap: new FormValidation.plugins.Bootstrap({
                            eleInvalidClass: '',
                            eleValidClass: '',
                        })
                    }
                }
        ).on('core.form.valid', function () {
            var form_data = new FormData(form);
            var objeto = {};
            for (let [name, value] of form_data) {
                Object.defineProperty(objeto, `${name}`, {enumerable: true, configurable: true, writable: true, value: `${value}`});
            }

            var request = $.ajax({
                url: "/usuario/edit-psw",
                type: "post",
                data: objeto
            });

            // Callback handler that will be called on success
            request.done(function (response, textStatus, jqXHR) {
                console.log("exito");
                var respuesta = JSON.parse(response);
                console.log(respuesta);
                if (respuesta[0].status === "error") {
                    $("#mensajePendientes").show();
                    $("#mensaje").empty();
                    $("#color").removeClass("alert-light-success");
                    $("#color").addClass("alert-light-danger");

                    var mensajeErr = `<span class="svg-icon svg-icon-danger svg-icon-2x"><svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="24px" height="24px" viewBox="0 0 24 24" version="1.1">
                                        <g stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                                            <rect x="0" y="0" width="24" height="24"/>
                                            <circle fill="#000000" opacity="0.3" cx="12" cy="12" r="10"/>
                                            <path d="M12.0355339,10.6213203 L14.863961,7.79289322 C15.2544853,7.40236893 15.8876503,7.40236893 16.2781746,7.79289322 C16.6686989,8.18341751 16.6686989,8.81658249 16.2781746,9.20710678 L13.4497475,12.0355339 L16.2781746,14.863961 C16.6686989,15.2544853 16.6686989,15.8876503 16.2781746,16.2781746 C15.8876503,16.6686989 15.2544853,16.6686989 14.863961,16.2781746 L12.0355339,13.4497475 L9.20710678,16.2781746 C8.81658249,16.6686989 8.18341751,16.6686989 7.79289322,16.2781746 C7.40236893,15.8876503 7.40236893,15.2544853 7.79289322,14.863961 L10.6213203,12.0355339 L7.79289322,9.20710678 C7.40236893,8.81658249 7.40236893,8.18341751 7.79289322,7.79289322 C8.18341751,7.40236893 8.81658249,7.40236893 9.20710678,7.79289322 L12.0355339,10.6213203 Z" fill="#000000"/>
                                        </g>
                                      </svg></span> ` + " " + respuesta[0].mensaje;
                    $("#mensaje").append(mensajeErr);
                }
                if (respuesta[0].status === "ok") {
                    Swal.fire({
                        text: "El cambio de contraseña se ha realizado con éxito. En este momento se cerrará su sesión actual y actualizar sus datos.",
                        icon: "success",
                        buttonsStyling: false,
                        confirmButtonText: "Ok, entendido!",
                        customClass: {
                            confirmButton: "btn font-weight-bold btn-light-primary"
                        }
                    }).then(function () {
                        window.location.assign("/login?logout");
                    });

                }
            });
            request.fail(function (jqXHR, textStatus) {
                console.log("fallo respuesta");
            });
        });
        form.querySelector('[name="contrsActual"]').addEventListener('input', function () {
            validator2.revalidateField('contraNueva');
        });
        form.querySelector('[name="contraNueva"]').addEventListener('input', function () {
            validator2.revalidateField('contraRepetida');
        });
    };

    return {
        // public functions
        init: function () {
            _initDemo1();
            _initDemo2();

        }
    };
}();

jQuery(document).ready(function () {

    KTFormControls.init();
});


