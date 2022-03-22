"use strict";

// Class Definition
var KTForgot = function () {

    var _buttonSpinnerClasses = 'spinner spinner-right spinner-white pr-15';
    var _handleForgotForm = function (e) {
        var validation;
        var form = document.getElementById('kt_forgot_form');
        var formSubmitButton = KTUtil.getById('kt_login_forgot_form_submit_button');
        // Init form validation rules. For more info check the FormValidation plugin's official documentation:https://formvalidation.io/
        validation = FormValidation.formValidation(
                KTUtil.getById('kt_forgot_form'),
                {
                    fields: {
                        newPassword: {
                            validators: {
                                notEmpty: {
                                    message: 'Es un campo obligatorio.'
                                },
                                stringLength: {
                                    min: 6,
                                    message: 'La contraseña debe ser al menos de 6 caracteres',
                                }
                            }
                        },
                        confirmPassword: {
                            validators: {
                                notEmpty: {
                                    message: 'Es un campo obligatorio.'
                                },
                                identical: {
                                    compare: function () {
                                        return form.querySelector('[name="newPassword"]').value;
                                    },
                                    message: 'La contraseña y su confirmación no son iguales.'
                                }
                            }
                        }
                    },
                    plugins: {
                        trigger: new FormValidation.plugins.Trigger(),

                        //defaultSubmit: new FormValidation.plugins.DefaultSubmit(),
                        submitButton: new FormValidation.plugins.SubmitButton(),
                        bootstrap: new FormValidation.plugins.Bootstrap()
                    }
                }
        ).on('core.form.valid', function () {
            // Show loading state on button
            KTUtil.btnWait(formSubmitButton, _buttonSpinnerClasses, "Por favor, espere...");

            // Simulate Ajax request
            setTimeout(function () {
                form.submit();
            }, 2000);
        })
                .on('core.form.invalid', function () {
                    Swal.fire({
                        text: "Lo siento, parece que se ha detectado un error, por favor intenta nuevamente.",
                        icon: "error",
                        buttonsStyling: false,
                        confirmButtonText: "Ok, entendido!",
                        customClass: {
                            confirmButton: "btn font-weight-bold btn-light-primary"
                        }
                    }).then(function () {
                        KTUtil.scrollTop();
                    });
                });
        ;
        // Revalidate the confirmation password when changing the password
        form.querySelector('[name="newPassword"]').addEventListener('input', function () {
            if (form.querySelector('[name="confirmPassword"]').value !== "") {
                validation.revalidateField('confirmPassword');
            }
        });
    };

    // Public Functions
    return {
        // public functions
        init: function () {


            _handleForgotForm();
        }
    };
}();

// Class Initialization
jQuery(document).ready(function () {
    KTForgot.init();
});
