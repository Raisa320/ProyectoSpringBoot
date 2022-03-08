// Class definition
var KTFormControls = function () {
    // Private functions
    var _initDemo1 = function () {
        var validator = FormValidation.formValidation(
                document.getElementById('formProyecto'),
                {
                    fields: {
                        nombre: {
                            validators: {
                                notEmpty: {
                                    message: 'Nombre requerido'
                                }
                            }
                        },
                        tema: {
                            validators: {
                                notEmpty: {
                                    message: 'Tema requerido'
                                }
                            }
                        },
                        lenguajes: {
                            validators: {
                                notEmpty: {
                                    message: 'Lenguajes requeridos'
                                }
                            }
                        },
                        cupos: {
                            validators: {
                                notEmpty: {
                                    message: 'Vacantes requeridos'
                                },
                                integer: {
                                    message: 'Ingrese solo Números'
                                },
                                greaterThan: {
                                    message: 'El numero de Vacantes debe ser mayor o igual a 1',
                                    min: 1
                                }
                            }
                        },
                        estadosProyecto: {
                            validators: {
                                notEmpty: {
                                    message: 'Estado requerido'
                                }
                            }
                        },
                        descripcion: {
                            validators: {
                                notEmpty: {
                                    message: 'Descripcion requerida'
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
        $('#selectTemas').on('change', function () {
            // Revalidate field
            validator.revalidateField('tema');
        });
        $('#selectLengaujes').on('change', function () {
            // Revalidate field
            validator.revalidateField('lenguajes');
        });
    };


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
