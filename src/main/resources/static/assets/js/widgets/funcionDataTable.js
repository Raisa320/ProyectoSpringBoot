"use strict";
var KTDatatablesBasicPaginations = function () {

    var initTable1 = function () {
        var table = $('#kt_datatable');
        // begin first table
        table.DataTable({
            responsive: true,
            pagingType: 'full_numbers',
            lengthMenu: [15, 30, 45, 60, 100],
            pageLength: 15,
            drawCallback: function (settings) {
                if ($(this).find("tbody tr").length < 15) {
                    $("#kt_datatable_length").hide();
                    // $("#kt_datatable_info").hide()
                    // $("#kt_datatable_paginate").hide();
                }
            },
            sDom: 'Rfrtlip',
            language: {
                sProcessing: "Procesando...",
                sLengthMenu: "Mostrar _MENU_ registros",
                sZeroRecords: "No se encontraron resultados",
                sEmptyTable: "Ningún dato disponible en esta tabla",
                sInfo: "Mostrando registros del _START_ al _END_ de un total de _TOTAL_",
                sInfoEmpty: "Mostrando registros del 0 al 0 de un total de 0 registros",
                sInfoFiltered: "(filtrado de un total de _MAX_ registros)",
                sInfoPostFix: "",
                sSearch: "",
                sSearchPlaceholder: "Buscar",
                sUrl: "",
                sInfoThousands: ",",
                sLoadingRecords: "Cargando...",
                oAria: {
                    "sSortAscending": ": Activar para ordenar la columna de manera ascendente",
                    "sSortDescending": ": Activar para ordenar la columna de manera descendente"
                },
                buttons: {
                    "copy": "Copiar",
                    "colvis": "Visibilidad"
                }
            },
            columnDefs: [
                {
                    targets: -1,
                    width: '89px',
                    title: 'Acciones',
                    orderable: false
                },
            ],
            search: {
                "smart": false
            }
        });

        //table.column(columnNo).search(regExSearch, true, false).draw();
    };

    return {

        //main function to initiate the module
        init: function () {
            initTable1();
        },

    };

}();

$(document).on("click", ".eliminar", function(event){
    event.preventDefault();
    var urlVincular = this.href;
    Swal.fire({
        title: 'Atención',
        text: "¿Estás seguro que deseas eliminar?",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Eliminar',
        cancelButtonText: 'Cancelar'
    }).then(function (result) {
        if (result.value) {
            window.location = urlVincular;
        }
    });
});

$('.eliminarUbic').click(function (event) {

    event.preventDefault();
    var urlVincular = this.href;
    var idUbic = this.id;
    Swal.fire({
        title: 'Atención',
        text: "¿Estás seguro que deseas eliminar?",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Eliminar',
        cancelButtonText: 'Cancelar'
    }).then(function (result) {
        if (result.value) {
            $.ajax({
                type: "GET",
                url: "/ubicDispenser/nroSolicitud/" + idUbic,
                cache: false
            }).done(function (response) {
                if (response === 0) {
                    window.location = urlVincular;
                } else {
                    Swal.fire({
                        title: 'Atención',
                        text: "Existe 1 solicitud pendiente en el dispenser, si avanza con la eliminación se cancelará la solicitud. ¿Estás seguro?",
                        icon: 'warning',
                        showCancelButton: true,
                        confirmButtonText: 'Eliminar',
                        cancelButtonText: 'Cancelar'
                    }).then(function (results) {
                        if (results.value) {
                            window.location = urlVincular;
                        }
                    });
                }
            }).fail(function () {
                Swal.fire({
                    title: 'Oops...',
                    text: "El servidor no responde, vuelva a intentar más tarde",
                    icon: 'error'
                });
            });
//            Swal.fire({
//                title: 'Atención',
//                text: "¿Estás seguro que deseas eliminar?",
//                icon: 'warning',
//                showCancelButton: true,
//                confirmButtonText: 'Eliminar',
//                cancelButtonText: 'Cancelar'
//            }).then(function (results) {
//                if (results.value) {
//                     window.location = urlVincular;
//                }
//            });

        }
    });
});

jQuery(document).ready(function () {
    KTDatatablesBasicPaginations.init();
});
