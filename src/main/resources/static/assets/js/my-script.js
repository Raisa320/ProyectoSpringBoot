 
var mobile = {
  Android: function() {
    return navigator.userAgent.match(/Android/i);
  },
  BlackBerry: function() {
    return navigator.userAgent.match(/BlackBerry/i);
  },
    iOS: function () { 
    return navigator.userAgent.match(/iPhone|iPad|iPod/i);
  },
  Opera: function() {
    return navigator.userAgent.match(/Opera Mini/i);
  },
  Windows: function() {
    return navigator.userAgent.match(/IEMobile/i);
  },
  any: function() {
    return (mobile.Android() || mobile.BlackBerry() || mobile.iOS() || mobile.Opera() || mobile.Windows());
  }
};


if (mobile.any() !== mobile.Android() || mobile.any() !== mobile.BlackBerry() || mobile.any() !== mobile.iOS() || mobile.any() !== mobile.Opera() || mobile.any() !== mobile.Windows()) {
    var elem = document.getElementById("perfil");
    var elem1 = document.getElementById("cuenta");
    // alert(elem.id);
    if (elem!==null && elem1!==null) {
        //elem.style.display = 'none';
        //elem1.style.display = 'none';
       //elem.id = "kt_quick_user_toggle";
    }
    console.log(mobile.any());
} else if (mobile.any() === null) {
//  var elem1 = document.getElementById("kt_quick_user_toggle2");
//  // alert(elem1.id);
//  elem1.id = "kt_quick_user_toggle";
//  // alert(document.getElementById("kt_quick_user_toggle").id);
//console.log("holi")
  console.log(mobile.any());
}
