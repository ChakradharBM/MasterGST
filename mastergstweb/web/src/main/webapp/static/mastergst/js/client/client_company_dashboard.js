$(function(){
	var cMonths = ['non','Jan','Feb','Mar','Apr','May','June','July','Aug','Sept','Oct','Nov','Dec'];
    function GSTR3B_download(mnth,year){
    	var mnth=parseInt(mnth);
		$.ajax({
			url: contextPath+"/addsupinvoice_reports/"+userid+"/"+fullname+"/"+usertype+"/"+clntid+"/"+mnth+"/"+year,
			contentType: 'application/json',
			beforeSend: function () {$("#progress-bar").removeClass('d-none');
			$(".customtable.db-ca-view.tabtable1").css('opacity','0.4');},
			success : function(data) {
				$("#progress-bar").addClass('d-none');
				$(".customtable.db-ca-view.tabtable1").css('opacity','1');
				$('#syncinfo_span').text('GSTR3B Invoices Downloaded from GSTN Successfully for '+(cMonths[mnth]+'-'+year)).css({'color':'green'});
				$('#GSTR3B'+mnth).html('Downloaded<br/><a href="#" onclick="GSTR3B_download('+'\''+mnth+'\',\''+year+'\')" style="font-size: 13px;">Download</a>').css({"color": "green","font-size": "14px"});
		    }
		});	
	}
   	function GSTR2A_download(mnth,year){
   		var mnth=parseInt(mnth);
    	$.ajax({
    		url: contextPath+"/dwnldgstr2Asummary/"+userid+"/"+clntid+"/"+mnth+"/"+year,
    		contentType: 'application/json',
    		beforeSend: function () {$("#progress-bar").removeClass('d-none');
			$(".customtable.db-ca-view.tabtable1").css('opacity','0.4');},
    		success : function(data) {
    			$("#progress-bar").addClass('d-none');
    			$(".customtable.db-ca-view.tabtable1").css('opacity','1');
    			$('#syncinfo_span').text('GSTR2A Invoices Downloaded from GSTN Successfully for '+(cMonths[mnth]+'-'+year)).css({'color':'green'});
				$('#GSTR2A'+mnth).html('Downloaded<br/><a href="#" onclick="GSTR2A_download('+'\''+mnth+'\',\''+year+'\')" style="font-size: 13px;">Download</a>').css({"color": "green","font-size": "14px"});
    	    }
    	});	
    }
   	
   	function GSTR1_download(mnth,year){
   		var mnth=parseInt(mnth);
    	$.ajax({
    		url: contextPath+"/downloadsyncgstr1/"+userid+"/"+clntid+"/"+mnth+"/"+year,
    		contentType: 'application/json',
    		beforeSend: function () {$("#progress-bar").removeClass('d-none');
			$(".customtable.db-ca-view.tabtable1").css('opacity','0.4');},
    		success : function(data) {
    			$("#progress-bar").addClass('d-none');
    			$(".customtable.db-ca-view.tabtable1").css('opacity','1');
    			$('#syncinfo_span').text('GSTR1 Invoices Downloaded from GSTN Successfully for '+(cMonths[mnth]+'-'+year)).css({'color':'green'});
				$('#GSTR1'+mnth).html('Downloaded<br/><a href="#" onclick="GSTR1_download('+'\''+mnth+'\',\''+year+'\')" style="font-size: 13px;">Download</a>').css({"color": "green","font-size": "14px"});
    	    }
    	});	
    }
});