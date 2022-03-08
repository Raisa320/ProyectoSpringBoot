// Class definition
var KTFormControls = function () {
    // Private functions
    var _initDemo1 = function () {
        var validator = FormValidation.formValidation(
                document.getElementById('formCrearPregunta'),
                {
                    fields: {
                        pregunta: {
                            validators: {
                                notEmpty: {
                                    message: 'Título requerido'
                                }
                            }
                        },
                        descripcion: {
                            validators: {
                                notEmpty: {
                                    message: 'Descripción requerida'
                                }
                            }
                        },
                        etiquetas: {
                            validators: {
                                notEmpty: {
                                    message: 'Etiqueta requerida'
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
        $('#kt_select2_11').on('change', function () {
            // Revalidate field
            validator.revalidateField('etiquetas');
        });
    
    }
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


