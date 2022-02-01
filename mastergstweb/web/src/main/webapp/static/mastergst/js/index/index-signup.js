/*---- window height --*/
	$(window).on('load', function() {
		var ht = $(window).height();
		$('.customerswraps-in').css('min-height', ht);
	});

/*---- dropdown link for href	 --*/
	$(function($) {
		$('.navbar .dropdown').hover(function() {
		$(this).find('.dropdown-menu').first().stop(true, true).delay(250).slideDown();
		
		}, function() {
		$(this).find('.dropdown-menu').first().stop(true, true).delay(100).slideUp();		
		});
		
		$('.navbar .dropdown > a').click(function(){
		location.href = this.href;
		 });		
		}); 			 
/*----- equal height-------*/
/*var highestBox = 0;
$('.sliderbox').each(function() {
    if ($(this).height() > highestBox) {
        highestBox = $(this).height();
    }
});
$('.sliderbox').height(highestBox);*/

/*-----google map --*/
function initialize() {
	var input = document.getElementById('city');
	var autocomplete = new google.maps.places.Autocomplete(input);
}
google.maps.event.addDomListener(window, 'load', initialize);