$(document).on("click", ".guardar", function(event){
    event.preventDefault();
    var urlVincular = this.href;
    Swal.fire({
        title: 'Atención',
        text: "¿Deseas guardar este proyecto?",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sí',
        cancelButtonText: 'No'
    }).then(function (result) {
        if (result.value) {
            var request = $.ajax({
                url: urlVincular,
                type: "get"
            });
            // Callback handler that will be called on success
            request.done(function (response, textStatus, jqXHR) {
                //console.log("exito");  
                var respuesta = JSON.parse(response);
                //console.log(respuesta);
                if (respuesta[0].status === "ok")
                {
                    Swal.fire("Proyecto Guardado!", "Puedes verlo en mis proyectos", "success");
                }
            });
            request.fail(function (jqXHR, textStatus) {
                Swal.fire("Error", "Raisa programó algo mal :c", "error");
            });
        }
    });
});
$(document).on("click", ".unirse", function(event){
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

