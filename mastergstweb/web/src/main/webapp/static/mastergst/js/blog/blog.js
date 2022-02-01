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