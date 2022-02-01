$(function () {
	$('.userslabbut').click(function(){
		var acknwldg_Msg = $('#acknwldg_Msg').text();
		if(acknwldg_Msg == ""){
			$('#subuserform').submit();
		}
	});
$('#subuserform').submit(function(e) {
		var err = 0;
		var emailId = $('#profileemail1').val();
		if(emailId != ''){
		$.ajax({
			url: contextPath+"/subuserEmailCheck/"+subuseremailSuffix+"?emailId="+emailId,
			async: false,
			cache: false,
			success : function(response) {if(response){err = 1;}}
		});
		}
		if (err != 0) {return false;}
	});
    $('#all_companies').change(function() {
      	  if($(this).is(':checked')){
    		  var companies = new Array();
    	        $("#company option").each(function(){companies.push($(this).val());});
    	        $('#company').select2().val(companies).trigger('change');        
    	  }else{$('#company').select2().val('').trigger('change');}
    	});
		var length = $('#company > option').length;
$('#company').on('select2:close', function (evt) {
	var uldiv = $(this).siblings('span.select2').find('ul');var count = uldiv.find('li').length - 1;
  if(length == count){$('#all_companies').prop("checked",true);}else{ $('#all_companies').prop("checked",false);}
});
$('#disable').change(function(){
    if($(this).prop("checked") == true){
    	$('#disable').val("false");
    }else if($(this).prop("checked") == false){
    	$('#disable').val("true");
    }
});
$('#addsubuser').change(function(){
    if($(this).prop("checked") == true){
    	$('#addsubuser').val("true");
    }else if($(this).prop("checked") == false){
    	$('#addsubuser').val("false");
    }
});
$('#adduserclient').change(function(){
    if($(this).prop("checked") == true){
    	$('#adduserclient').val("true");
    }else if($(this).prop("checked") == false){
    	$('#adduserclient').val("false");
    }
});
});


function test() {
	document.getElementById("password").disabled = false;
	document.getElementById("confirmPassword").disabled = false;
	document.getElementById("mobile").disabled = false;
}
function selectCompanyName(select){
	$('#acknwldg_Msg').html('');
		var clientList = new Array();
		var clientids = $(select).val();
		 var str = clientids+ '';
		if (str.indexOf(",") != -1) {
			var clntids = str.split(",");
			for(var i=0; i<clntids.length; i++){
				clientList.push(clntids[i]);
			}
		}else{
			clientList.push(clientids);
		}
		$('#branches,#verticals').children('option').remove();
		$.ajax({
			url: contextPath+"/clientsData?clientIds="+clientList,
			async: false,
			cache: false,
			success : function(response) {
				response.forEach(function(branche){
					branche.branches.forEach(function(branch) {
						$("#branches").append($("<option></option>").attr("value",branch.name).text(branche.businessname+"-"+branch.name)); 		
					});
					branche.verticals.forEach(function(vertical) {
						$("#verticals").append($("<option></option>").attr("value",vertical.name).text(branche.businessname+"-"+vertical.name)); 
					});
				});
			}
		});
		$.ajax({
			url: contextPath+"/customersData?clientIds="+clientList,
			async: false,
			cache: false,
			success : function(response) {
				response.forEach(function(customer){
						$("#customers").append($("<option></option>").attr("value",customer.name).text(customer.name)); 	
				});
			}
		});
		/*if(clientList.length >=2){
			var roleid=$('#role').val();
			$.ajax({
				url: contextPath+"/checkAcknowledgementsAndRoles?clientids="+clientList+"&roleid="+roleid,
				async: true,
				cache: false,
				success : function(response) {
					if(response == null || response == ""){
						$('#acknwldg_Msg').html('');
					}else{
						$('#acknwldg_Msg').html('Multiple Companys are cannot assign please change the role permission/remove multiple companys');						
					}
				},error:function(errorRes){
				}
			});
		}*/
}

function userEmailCheck(){
	var emailId = $('#profileemail1').val();
	var cemail ='<c:out value="${cemail}"/>';
	if(emailId != ''){
	$.ajax({
		url: contextPath+"/subuserEmailCheck"+subuseremailSuffix+"?emailId="+emailId,
		async: true,
		cache: false,
		success : function(response) {
			if(response){
				$('#subuseremail').addClass('has-error has-danger');
				if(response.type == 'aspdeveloper'){response.type = 'Developer';}else if(response.type == 'cacmas'){response.type = 'CA/Tax Professionals';
				}else if(response.type == 'suvidha'){response.type ='Suvidha Kendras';}else if(response.type == 'partner'){response.type = 'Partner';
				}else if(response.type == 'business'){response.type = 'Small/Medium Business';}else if(response.type =='enterprise'){response.type = 'Enterprises';}
				if(cemail == response.email){$('#subuserEmail_Msg').html("This User <strong>already logged in </strong>. Please enter different email id.");
				}else{$('#subuserEmail_Msg').html("This Email is not eligible to add as he is <strong>already in our system as "+response.type+" </strong>. Please enter different email id.");}
			}else{$('#subuserEmail_Msg').html("");}
		},error:function(resp){}
	});
	}	
}

function selectRoleid(roleid){
	var roleid = $(roleid).val();
	$('#acknwldg_Msg').html('');
	if(roleid == "" || roleid == null){
	}else{
		var clientList = new Array();
		var clientids = $('#company').val();
		var str = clientids+ '';
		if (str.indexOf(",") != -1) {
			var clntids = str.split(",");
			for(var i=0; i<clntids.length; i++){
				clientList.push(clntids[i]);
			}
		}
		if(clientList.length >=2){
			$.ajax({
				url: contextPath+"/checkAcknowledgementsAndRoles?clientids="+clientList+"&roleid="+roleid,
				async: true,
				cache: false,
				success : function(response) {
					if(response == null || response == ""){
						$('#acknwldg_Msg').html('');
					}else{
						$('#acknwldg_Msg').html('Multiple Companys are cannot assign please change the role permission/remove multiple companys');						
					}
				},error:function(errorRes){
					console.log("errorRes ->"+JSON.stringify(errorRes));
				}
			});
		}
	}
}
