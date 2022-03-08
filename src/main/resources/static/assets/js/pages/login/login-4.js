"use strict";

// Class Definition
var KTLogin = function () {
    var _buttonSpinnerClasses = 'spinner spinner-right spinner-white pr-15';

    var _handleFormSignin = function () {
        var form = KTUtil.getById('kt_login_singin_form');
        var formSubmitUrl = KTUtil.attr(form, 'action');
        var formSubmitButton = KTUtil.getById('kt_login_singin_form_submit_button');

        if (!form) {
            return;
        }

        FormValidation
                .formValidation(
                        form,
                        {
                            fields: {
                                username: {
                                    validators: {
                                        notEmpty: {
                                            message: 'Nombre de usuario requerido.'
                                        }
                                    }
                                },
                                password: {
                                    validators: {
                                        notEmpty: {
                                            message: 'Contraseña requerida'
                                        }
                                    }
                                }
                            },
                            plugins: {
                                trigger: new FormValidation.plugins.Trigger(),
                                //submitButton: new FormValidation.plugins.SubmitButton(),
                                defaultSubmit: new FormValidation.plugins.DefaultSubmit(), // Uncomment this line to enable normal button submit after form validation
                                bootstrap: new FormValidation.plugins.Bootstrap({
                                    //	eleInvalidClass: '', // Repace with uncomment to hide bootstrap validation icons
                                    //	eleValidClass: '',   // Repace with uncomment to hide bootstrap validation icons
                                })
                            }
                        }
                )
                .on('core.form.valid', function () {
                    // Show loading state on button
                    KTUtil.btnWait(formSubmitButton, _buttonSpinnerClasses, "Por favor, espere...");

                    // Simulate Ajax request
                    setTimeout(function () {
                        KTUtil.btnRelease(formSubmitButton);
                    }, 2000);
                }).on('core.form.invalid', function () {
            Swal.fire({
                text: "Lo siento, parece que se ha detectado un error, intente de nuevo.",
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
    }

    var _handleFormForgot = function () {
        var form = KTUtil.getById('kt_login_forgot_form');
        var formSubmitUrl = KTUtil.attr(form, 'action');
        var formSubmitButton = KTUtil.getById('kt_login_forgot_form_submit_button');

        if (!form) {
            return;
        }

        FormValidation
                .formValidation(
                        form,
                        {
                            fields: {
                                email: {
                                    validators: {
                                        notEmpty: {
                                            message: 'Correo requerido.'
                                        },
                                        emailAddress: {
                                            message: 'El valor ingresado no corresponde a una dirección de correo.'
                                        }
                                    }
                                }
                            },
                            plugins: {
                                trigger: new FormValidation.plugins.Trigger(),
                                submitButton: new FormValidation.plugins.SubmitButton(),
                                //defaultSubmit: new FormValidation.plugins.DefaultSubmit(), // Uncomment this line to enable normal button submit after form validation
                                bootstrap: new FormValidation.plugins.Bootstrap({
                                    //	eleInvalidClass: '', // Repace with uncomment to hide bootstrap validation icons
                                    //	eleValidClass: '',   // Repace with uncomment to hide bootstrap validation icons
                                })
                            }
                        }
                )
                .on('core.form.valid', function () {
                    // Show loading state on button
                    KTUtil.btnWait(formSubmitButton, _buttonSpinnerClasses, "Por favor, espere...");

                    // Simulate Ajax request
                    setTimeout(function () {
                        KTUtil.btnRelease(formSubmitButton);
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
    }

    var _handleFormSignup = function () {
        // Base elements
        var wizardEl = KTUtil.getById('kt_login');
        var form = KTUtil.getById('kt_login_signup_form');
        var wizardObj;
        var validations = [];

        if (!form) {
            return;
        }

        // Init form validation rules. For more info check the FormValidation plugin's official documentation:https://formvalidation.io/
        // Step 1
        validations.push(FormValidation.formValidation(
                form,
                {
                    fields: {
                        nombre: {
                            validators: {
                                notEmpty: {
                                    message: 'El nombre es requerido.'
                                }
                            }
                        },
                        apellido: {
                            validators: {
                                notEmpty: {
                                    message: 'Los apellidos son requeridos.'
                                }
                            }
                        },
                        cicloEstudios: {
                            validators: {
                                notEmpty: {
                                    message: 'Seleccione al menos una opción.'
                                }
                            }
                        },
                        sexo: {
                            validators: {
                                notEmpty: {
                                    message: 'Seleccione al menos una opción.'
                                }
                            }
                        }
                    },
                    plugins: {
                        trigger: new FormValidation.plugins.Trigger(),
                        // Bootstrap Framework Integration
                        bootstrap: new FormValidation.plugins.Bootstrap({
                            //eleInvalidClass: '',
                            eleValidClass: '',
                        })
                    }
                }
        ));
        // Step 2
        validations.push(FormValidation.formValidation(
                form,
                {
                    fields: {

                        nivel: {
                            validators: {
                                notEmpty: {
                                    message: 'Seleccione al menos 1 opción.'
                                }
                            }
                        },
                        temas: {
                            validators: {
                                notEmpty: {
                                    message: 'Seleccione al menos 1 tema.'
                                }
                            }
                        },
                        rol: {
                            validators: {
                                notEmpty: {
                                    message: 'Seleccione al menos 1 opción.'
                                }
                            }
                        },
                        lenguajes: {
                            validators: {
                                notEmpty: {
                                    message: 'Seleccione al menos 1 opción.'
                                }
                            }
                        }
                    },
                    plugins: {
                        trigger: new FormValidation.plugins.Trigger(),
                        // Bootstrap Framework Integration
                        bootstrap: new FormValidation.plugins.Bootstrap({
                            //eleInvalidClass: '',
                            eleValidClass: '',
                        })
                    }
                }
        ));
        // Step 3
        validations.push(FormValidation.formValidation(
                form,
                {
                    fields: {

                        email: {
                            validators: {
                                notEmpty: {
                                    message: 'El correo es requerido.'
                                },
                                emailAddress: {
                                    message: 'Formato de correo incorrecto.'
                                }
                            }
                        },
                        clave: {
                            validators: {
                                notEmpty: {
                                    message: 'Contraseña Requerida.'
                                }
                            }
                        }
                    },
                    plugins: {
                        trigger: new FormValidation.plugins.Trigger(),
                        // Bootstrap Framework Integration
                        bootstrap: new FormValidation.plugins.Bootstrap({
                            //eleInvalidClass: '',
                            eleValidClass: '',
                        })
                    }
                }
        ));

        // Initialize form wizard
        wizardObj = new KTWizard(wizardEl, {
            startStep: 1, // initial active step number
            clickableSteps: false  // allow step clicking
        });

        // Validation before going to next page
        wizardObj.on('change', function (wizard) {
            if (wizard.getStep() > wizard.getNewStep()) {
                return; // Skip if stepped back
            }

            // Validate form before change wizard step
            var validator = validations[wizard.getStep() - 1]; // get validator for currnt step
            if (validator) {
                validator.validate().then(function (status) {
                    if (status == 'Valid') {
                        wizard.goTo(wizard.getNewStep());

                        KTUtil.scrollTop();
                    } else {
                        Swal.fire({
                            text: "Lo siento, parece que se ha detectado un error, por favor intenta nuevamente.",
                            icon: "error",
                            buttonsStyling: false,
                            confirmButtonText: "Ok, entendido!",
                            customClass: {
                                confirmButton: "btn font-weight-bold btn-light"
                            }
                        }).then(function () {
                            KTUtil.scrollTop();
                        });
                    }
                });
            }

            return false;  // Do not change wizard step, further action will be handled by he validator
        });

        // Change event
        wizardObj.on('changed', function (wizard) {
            KTUtil.scrollTop();
        });

        // Submit event
        wizardObj.on('submit', function (wizard) {
            var validator = validations[2]; // get validator for currnt step
            if (validator) {
                validator.validate().then(function (status) {
                    if (status == 'Valid') {
                        var email = $("#email").val();
                        var request = $.ajax({
                            url: "/verificaEmail/" + email,
                            type: "get"
                        });
                        request.done(function (response, textStatus, jqXHR) {
                            console.log(response);
                            if (response === "si") {
                                Swal.fire({
                                    text: "!Todo es correcto! Porfavor confirme el envío del formulario.",
                                    icon: "success",
                                    showCancelButton: true,
                                    buttonsStyling: false,
                                    confirmButtonText: "Si, enviar!",
                                    cancelButtonText: "No, cancelar",
                                    customClass: {
                                        confirmButton: "btn font-weight-bold btn-primary",
                                        cancelButton: "btn font-weight-bold btn-default"
                                    }
                                }).then(function (result) {
                                    if (result.value) {
                                        form.submit(); // Submit form
                                    } else if (result.dismiss === 'cancel') {
                                        Swal.fire({
                                            text: "!Su formulario no ha sido enviado!.",
                                            icon: "error",
                                            buttonsStyling: false,
                                            confirmButtonText: "Ok, entendido!",
                                            customClass: {
                                                confirmButton: "btn font-weight-bold btn-primary",
                                            }
                                        });
                                    }
                                });
                            } else {
                                Swal.fire({
                                    text: "El correo que proporciona ya se encuentra registrado, intente con otro.",
                                    icon: "error",
                                    buttonsStyling: false,
                                    confirmButtonText: "Ok, entendido!",
                                    customClass: {
                                        confirmButton: "btn font-weight-bold btn-light"
                                    }
                                }).then(function () {
                                    KTUtil.scrollTop();
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
                    } else {
                        Swal.fire({
                            text: "Lo siento, parece que se ha detectado un error, por favor intenta nuevamente.",
                            icon: "error",
                            buttonsStyling: false,
                            confirmButtonText: "Ok, entendido!",
                            customClass: {
                                confirmButton: "btn font-weight-bold btn-light"
                            }
                        }).then(function () {
                            KTUtil.scrollTop();
                        });
                    }
                });
            }
        });
    }

    // Public Functions
    return {
        init: function () {
            _handleFormSignin();
            _handleFormForgot();
            _handleFormSignup();
        }
    };
}();

// Class Initialization
jQuery(document).ready(function () {
    KTLogin.init();
});
