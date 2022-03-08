$(document).on("click", "#rechazar", function(event){
    event.preventDefault();
    var urlVincular = this.href;
    Swal.fire({
        title: 'Atención',
        text: "¿Estás seguro que deseas descartar la solicitud?",
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

