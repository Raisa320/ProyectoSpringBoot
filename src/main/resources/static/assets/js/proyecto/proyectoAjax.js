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
        text: "¿Estás seguro que deseas solicitar unirte?",
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

