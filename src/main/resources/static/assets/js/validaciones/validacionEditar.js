// Class definition
var KTFormControls = function () {
    // Private functions
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
                        defaultSubmit: new FormValidation.plugins.DefaultSubmit(),
                    }
                }
        );
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
        var validator2= FormValidation.formValidation(
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
                                    compare: function() {
                                        return form.querySelector('[name="contrsActual"]').value;
                                    },
                                    message: 'La Contraseña debe ser distinta a la Actual'
                                }
                            }
                        },
                        contraRepetida: {
                            validators: {
                                notEmpty: {
                                    message: 'Confirmación de Contraseña Requerida'
                                },
                                identical: {
                                    compare: function() {
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
                        defaultSubmit: new FormValidation.plugins.DefaultSubmit(),
                        // Bootstrap Framework Integration
                        bootstrap: new FormValidation.plugins.Bootstrap({
                            eleInvalidClass: '',
                            eleValidClass: '',
                        })
                    }
                }
        );
        form.querySelector('[name="contrsActual"]').addEventListener('input', function() {
            validator2.revalidateField('contraNueva');
        });
        form.querySelector('[name="contraNueva"]').addEventListener('input', function() {
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


