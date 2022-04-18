$(document).on("click", "#unirse", function (event) {
    event.preventDefault();
    var urlVincular = this.href;
    Swal.fire({
        title: 'Atención',
        html: "¿Deseas unirte al proyecto? <br>Puedes enviar un mensaje adicional mencionando la razón de esta solicitud.</br>",
        input: 'text',
        inputPlaceholder: "(* Opcional)",
        inputAttributes: {
            autocapitalize: 'off'
        },
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sí',
        cancelButtonText: 'No'
    }).then(function (result) {

        if (result.value === "" || result.value) {
            if (result.value) {
                urlVincular = urlVincular + "/" + result.value;
            }
            //console.log(urlVincular)
            window.location = urlVincular;
        }
    });
});

function finalizar(id) {
    var numeroParticipantes = document.getElementById("check").name;
    Swal.fire({
        title: 'Atención',
        text: "¿Estás seguro que deseas finalizar el proyecto?",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sí',
        cancelButtonText: 'No'
    }).then(function (result) {
        if (result.value) {
            Swal.fire({
                title: '<strong><u>Finalización de Proyecto</u></strong>',
                icon: 'info',
                html:
                        '<center>En el siguiente campo usted puede incluir un link del repositorio o demo del proyecto de forma opcional:</center>',
                input: 'text',
                inputValidator: (value) => {
                    return new Promise((resolve) => {
                        var res = value.match(/(http(s)?:\/\/.)?(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)/g);
                        if (res !== null || value === '') {
                            resolve();
                        } else {
                            resolve('Url inválida');
                        }
                    })
                },
                inputPlaceholder: "(* Opcional)",
                showCancelButton: true,
                focusConfirm: false,
                confirmButtonText:
                        'Finalizar',
                cancelButtonText:
                        'Cancelar'
            }).then(function (result) {
                console.log(result)
                if (result.isConfirmed) {
                    var request = $.ajax({
                        url: "/proyecto/finalizarProyecto",
                        type: "POST",
                        data: {id: id, urlFinal: result.value}
                    });
                    // Callback handler that will be called on success
                    request.done(function (response, textStatus, jqXHR) {
                        //console.log("exito");  
                        var respuesta = JSON.parse(response);
                        console.log(respuesta);
                        if (respuesta[0].status === "ok")
                        {
                            if (numeroParticipantes > 0) {
                                Swal.fire({
                                    title: 'Atención',
                                    text: "¿Deseas calificar a los participantes de tu proyecto?",
                                    icon: 'warning',
                                    showCancelButton: true,
                                    confirmButtonText: 'Sí',
                                    cancelButtonText: 'No'
                                }).then(function (result) {
                                    if (result.value) {
                                        //console.log("dale a calificar");
                                        window.location = "/proyecto/calificar/" + id;
                                    } else {
                                        window.location = "/proyecto/finalizar/" + id;
                                    }
                                });
                            } else {
                                window.location = "/proyecto/finalizar/" + id;
                            }
                        }
                    });
                    request.fail(function (jqXHR, textStatus) {
                        console.log("fallo en algo :c contacte a alguien si es que llego a ver esto ");
                    });
//                    
                }
            });
//            
        }
    });
}

function meGusta(idProyecto, idUser) {
    let elemento = document.getElementById("megusta");
    let elementoUrl = document.getElementById("etiquetaUrl");
    let urlG = "/proyecto/like/" + idProyecto + "/" + idUser;
    if (elemento.classList.contains("text-danger")) {
        //dislike
        elemento.classList.replace("text-danger", "text-muted");
        urlG = "/proyecto/dislike/" + idProyecto + "/" + idUser;
    } else {
        elemento.classList.replace("text-muted", "text-danger");
    }
    if (elementoUrl.classList.contains("text-hover-danger")) {
        //like posible
        elementoUrl.classList.replace("text-hover-danger", "text-hover-muted");
    } else {
        //dislike posible
        elementoUrl.classList.replace("text-hover-muted", "text-hover-danger");
    }
    var request = $.ajax({
        url: urlG,
        type: "get"
    });
    // Callback handler that will be called on success
    request.done(function (response, textStatus, jqXHR) {
        //console.log("exito");  
        var respuesta = JSON.parse(response);
        //console.log(respuesta);
        if (respuesta[0].status === "ok")
        {
            document.getElementById("nroGusta").textContent = respuesta[0].nroMegusta;
        }
    });
    request.fail(function (jqXHR, textStatus) {
        console.log("fallo en algo :c contacte a alguien si es que llego a ver esto ");
    });
}
