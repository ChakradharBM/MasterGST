$(function () {
	$('.dropdown').mouseenter(function(){
		$('.poi').removeClass('pending-color');
		$('.pois').removeClass('submitted-color');
		$('.poif').removeClass('filed-color');
	});
	$('.dropdown-content').mouseenter(function(){
		var gh = $(this).attr('class');
		var sdkj = gh.split(" ");
		if(sdkj[1].indexOf("pending") >= 0){
			$('#'+sdkj[1]).addClass('abcd').removeClass('pending-color');
		}else if(sdkj[1].indexOf("submitted") >= 0){
			$('#'+sdkj[1]).addClass('abcd').removeClass('submitted-color');
		}else{
			$('#'+sdkj[1]).addClass('abcd').removeClass('filed-color');
		}
	});
	$('.dropdown-content').mouseleave(function(){
		var gh = $(this).attr('class');
		var sdkj = gh.split(" ");
		if(sdkj[1].indexOf("pending") >= 0){
			$('#'+sdkj[1]).addClass('pending-color').removeClass('abcd')
		}else if(sdkj[1].indexOf("submitted") >= 0){
			$('#'+sdkj[1]).addClass('submitted-color').removeClass('abcd');
		}else{
			$('#'+sdkj[1]).addClass('filed-color').removeClass('abcd');
		}
	});
	$( ".statustxt" ).mouseenter(function(){
		$( this ).children().removeClass('pending-color').addClass('abcd');
	});
	$( ".statustxt" ).mouseleave(function(){
			$( this ).children().addClass('pending-color').removeClass('abcd');
	});
	$( ".submittedtxt" ).mouseenter(function(){
		$( this ).children().removeClass('submitted-color').addClass('abcd');
	});
	$( ".submittedtxt" ).mouseleave(function(){
			$( this ).children().addClass('submitted-color').removeClass('abcd');
	});
	$( ".filledtxt" ).mouseenter(function(){
		$( this ).children().removeClass('filed-color').addClass('abcd');
	});
	$( ".filledtxt" ).mouseleave(function(){
			$( this ).children().addClass('filed-color').removeClass('abcd');
	});
	});
