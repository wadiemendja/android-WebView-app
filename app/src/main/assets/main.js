var counter = 0 ;
setInterval(function(){
    document.getElementById('image').style.transform='rotate('+counter+'deg)'
    counter++
},5)