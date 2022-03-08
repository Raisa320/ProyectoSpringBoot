$(document).on("click", "#unirse", function (event) {
    event.preventDefault();
    var urlVincular = this.href;
    Swal.fire({
        title: 'Atención',
        text: "¿Por qué deseas unirte al proyecto?",
        input: 'text',
        inputAttributes: {
            autocapitalize: 'off'
        },
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sí',
        cancelButtonText: 'No'
    }).then(function (result) {
        if (result.value) {
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
