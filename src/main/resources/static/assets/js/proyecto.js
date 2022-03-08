$(document).on("click", ".cara", function (event) {
    let arr = this.className.split(' ');
    let check = arr[0];
    let iconos = document.getElementsByClassName(check);
    for (var i = 0; i < iconos.length; i++) {
        iconos[i].classList.remove("text-success");
        iconos[i].classList.remove("text-warning");
        iconos[i].classList.remove("text-danger");
    }
    arr = this.className.split(' ');
    let clase = arr[arr.length - 1];
    switch (clase) {
        case 'colab':
            this.className += " text-success";
            break;
        case 'neut':
            this.className += " text-warning";
            break;
        case 'decep':
            this.className += " text-danger";
            break;
    }
});