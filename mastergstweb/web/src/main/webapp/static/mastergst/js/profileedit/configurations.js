var auditorDetails = [
						{'txt':'place','val':'place'},
						{'txt':'nameofsignatory','val':'nameofsignatory'},
						{'txt':'membershipno','val':'membershipno'},
						{'txt':'panno','val':'panno'},
						{'txt':'buildorflatno','val':'buildorflatno'},
						{'txt':'floorno','val':'floorno'},
						{'txt':'buildingname','val':'buildingname'},
						{'txt':'streetname','val':'streetname'},
						{'txt':'locality','val':'locality'},
						{'txt':'districtname','val':'districtname'},
						{'txt':'statename','val':'statename'},
						{'txt':'pincode','val':'pincode'}
					];
	 
function editAuditorDetails(){
	$('#auditorDetails_cancelbutton,#auditorDetails_savebutton').css("display","block");
	$('#auditorDetails_editbutton').css("display","none");
	var fildval = "";
	$.each(auditorDetails,function(item,index){
		fildval = $("#auditor"+index.txt).text();
		$("#auditor"+index.txt).html('<input type="text" class="form-control '+index.txt+'" name="'+index.txt+'" id="auditorval'+index.txt+'" style="width:200px;height: 31px;"  value="'+fildval+'" >');
	});
}

function saveAuditorDetails(clientid){
	$("#auditorDetails_savebutton,#auditorDetails_cancelbutton").css('display','none');
	$("#auditorDetails_editbutton").css('display','block');
	    var auditorDetail = {};
	    auditoradddDetails = [];

	    $.each(auditorDetails,function(item,index){
	    	var val = $('#auditorval'+index.txt).val();
	    	auditorDetail[index.txt] = val;
	    	$("#auditor"+index.txt).html(val);
	    	item = {}
	        item ["txt"] = index.txt;
	        item ["val"] = val;

	        auditoradddDetails.push(item);
	    });
	    
	    $.ajax({
			url: contextPath+"/saveauditordetails/"+clientid,
			data: auditorDetail,
			type:"POST",
			contentType: 'application/x-www-form-urlencoded',
			success : function(response) {
				successNotification('Save successful!');
				
	},
	     error : function(e) {
			if(e.responseText) {
				errorNotification(e.responseText);
			}
		}
		});
	    auditoraddDetails =  auditoradddDetails;
}

function cancelAuditorDetails(){
	$('#auditorDetails_cancelbutton,#auditorDetails_savebutton').css("display","none");
	$('#auditorDetails_editbutton').css("display","block");
	 $.each(auditoraddDetails,function(item,index){
	    	$("#auditor"+index.txt).html(index.val);
	    });
	
}