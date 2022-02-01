/*---- resize Modal Window height ----*/
$(function () { 
	$(window).on("load resize scroll",function(e){
		$('.popupright').height($(window).height());
	});
});
$(".modal").css("height", $(document).height() + "px");
/*-------*/
$(function () { 
/*---- modal-backdrop click event ----*/
$(".modal").removeClass('modal-right');
$(".modal-dialog").addClass('modal-right');

$('.modal-backdrop.fade').on('click', function(){	 
 				$('.modal').click();
				$('button.close').click();
				
});
		

$('.modal-backdrop.fade').on('click', function(){
		 $('.modal').removeClass('show').hide();
			$('body').removeClass('modal-open');
			$('.modal-backdrop').remove();
}); 

/*---- view btn ----*/
$('.btn-view-dd .dropdown-menu .dropdown-item').click(function(){
	var ddtxt = $(this).text();
	$('.dropdown-toggle').text(ddtxt);
});
/*---- dropdown menu hover ----*/
  
$('ul.nav li.dropdown, .navbar-left ul li.dropdown, .addlist-dd.dropdown, .addsales.dropdown').hover(function() {
  $(this).find('.dropdown-menu').stop(true, true).delay(200).fadeIn(500);
}, function() {
  $(this).find('.dropdown-menu').stop(true, true).delay(200).fadeOut(500);
});

$('.addsales.dropdown').hover(function(){
	$('.addsales.dropdown .showarrow').hover();										 
});

$('a.dropdown-toggle').click(function(){
	var $url = $(this).attr('href');
	window.location = $url;																											
});
					 
/*---- sidebar menu ----*/
   
	$('.sidebarmenu .submenu').hide();
	$('.sidebarmenu .submenu.open').slideDown(100);	
	$('.sidebarmenu li').hover(function () {
		 clearTimeout($.data(this, 'timer'));
		 $('ul', this).stop(true, true).slideDown(200);
	}, function () {
		$.data(this, 'timer', setTimeout($.proxy(function() {
			$('ul', this).stop(true, true).slideUp(100);
		}, this), 100));
	});
	
/*------*/	
});								 