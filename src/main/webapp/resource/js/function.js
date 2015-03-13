function validate_input(){
    var Judul=document.getElementById("kolomberita:Judul").value;
    var Konten=document.getElementById("kolomberita:Konten").value;
    var errorMessage=document.getElementById("messages");
    if(Judul === "" || Konten=== "") {
            errorMessage.innerHTML= "*Info berita tidak lengkap"; 
            return false;			
    }
    else{
            Judul="";
            Konten="";
            return true;
    }
}

function myFunction() {
    var resultBerita=document.getElementById("output").value;
    var tobeshown= document.getElementById("dewa");
    if(resultBerita==null){
    }
    else{
        var r = confirm("Berita yang dimuat adalah "+resultBerita);
        if (r === true) {
            //Do nothing
        } else {
           tobeshown.style.display="block";
        }
    }
    
}