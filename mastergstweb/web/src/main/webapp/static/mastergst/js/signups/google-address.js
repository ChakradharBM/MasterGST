 
 /*-----google map --*/
function initialize() {

var input = document.getElementById('addressId');
var autocomplete = new google.maps.places.Autocomplete(input);
}

google.maps.event.addDomListener(window, 'load', initialize);