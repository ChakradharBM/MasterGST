var dbFilingTable4, dbFilingTable5, dbFilingTable6, dbFilingTable7, dbFilingTable8, dbFilingTable9;
var dbFilingTable10, dbFilingTable11, dbFilingTable12, dbFilingTable13, dbFilingTable14, dbFilingTable15;
var dbFilingTable16, dbFilingTable17, dbFilingTable18, gstSummary = null, indexObj = new Object(), tableObj = new Object();
var ipAddress = '', uploadResponse;
var otpExpirycheck;
$(function() {
	$('#filing_option').html('Monthly');
    var date = new Date();
    var month = '<c:out value="${month}"/>';
    var year = '<c:out value="${year}"/>';
    var dateValue = ((''+month).length<2 ? '0' : '') + month + '-' + year;
    if(month == null || month == '') {month = date.getMonth()+1;year = date.getFullYear();}
    $("#datetimepicker").val(dateValue);
	 var today = new Date();
    var startDate = new Date(today.getFullYear(), 2);
    var endDate = new Date(today.getFullYear(), 2);
  	 $("#datetimepicker").datepicker({
    	viewMode: 1,
    	minViewMode: 1,
    	yearRange: '2018:2020',
    	format: 'mm-yyyy'
    });
    $('#datetimepicker').datepicker('update', dateValue);
	// Add minus icon for collapse element which is open by default
	$(".collapse.in").each(function() {
		$(this).siblings(".main-accordion.panel-heading").find(".fa").addClass("fa-minus").removeClass("fa-plus");
		$(this).parent().find(".main-accordion.panel-heading").addClass("active");
	});
	$('.main-collapse.panel-collapse').on('show.bs.collapse', function(e) {
		$(e.target).closest('.panel').siblings().find('.main-collapse.panel-collapse').collapse('hide');
	});
	// Toggle plus minus icon on show hide of collapse element
	$(".main-collapse.collapse").on('show.bs.collapse', function() {
		$(this).parent().find(".fa").removeClass("fa-plus").addClass("fa-minus");
		$(this).parent().find(".main-accordion.panel-heading").addClass("active");
	}).on('hide.bs.collapse', function() {
		$(this).parent().find(".fa").removeClass("fa-minus").addClass("fa-plus");
		$(this).parent().find(".main-accordion.panel-heading").removeClass("active");
	});
	
	
	$(".tpone-input, .tptwo-input, .tpthree-input, .tpfour-input, .tpfive-input, .tpsix-input , .tpseven-input, .tpten-input, .tpcheck-input").attr('readonly', true);
 	$('.tpone-save, .tpone-cancel,.tptwo-save, .tptwo-cancel,.tpsix-save, .tpsix-cancel,.tpthree-save, .tpthree-cancel,.tpfour-save, .tpfour-cancel,.tpfive-save, .tpfive-cancel,.tpsix-save, .tpsix-cancel,.tpseven-save, .tpseven-cancel,.tpten-save, .tpten-cancel, .addmorewrap').hide();
 	$(".otp_form_input .invoice_otp").keyup(function() {
 		if(this.value.length == this.maxLength) {
 			$(this).next().next('.form-control').focus();
 		}
 	});
 	$('.fy-drop').val('${invoice.fp}');
 	$('#nav-client').addClass('active');
 
 	function forceNumeric() {
 		var $input = $(this);
 		$input.val($input.val().replace(/[^\d.,]+/g, ''));
 	}
 	//$('body').on('propertychange input', 'input.form-control', forceNumeric);
 	var headertext = [],
 		headers = document.querySelectorAll("table.display th"),
 		tablerows = document.querySelectorAll("table.display th"),
 		tablebody = document.querySelector("table.display tbody");
 	for(var i = 0; i < headers.length; i++) {
 		var current = headers[i];
 		headertext.push(current.textContent.replace(/\r?\n|\r/, ""));
 	}
 	for(var i = 0, row; row = tablebody.rows[i]; i++) {
 		for(var j = 0, col; col = row.cells[j]; j++) {
 			col.setAttribute("data-th", headertext[j]);
 		}
 	}
});


 function clickEdit(a, b, c, d, e) {
	$(a).show();
 	$(b).show();
 	$(c).hide();
 	$('.addmorewrap1').css('display', 'block');
 	$('.delmorewrap1').css('display', 'block');
 	$(d).attr('readonly', false);
 	$('.auto-val-input').attr('readonly', true);
 	$('.fy-drop').prop('disabled', false);
 	$('.fy-drop').css('background-color', 'white');
 	$(d).addClass('tpone-input-edit');
 	$(e).show();
 }
 
 function clickSave(a, b, c, d, e) {
 	$(a).hide();
 	$(b).hide();
 	$(c).show();
 	$(d).attr('readonly', true);
 	$(d).addClass('tpone-input-edit');
 	$('.fy-drop').prop('disabled', true);
 	$('.fy-drop').css('background-color', '#eceeef');
 	$(e).hide();
 	var formObj;
	if(a == '.tptwo-save'){
		formObj = document.getElementById('gstr8FormA');
	}else{
		formObj = document.getElementById('gstr8Form');
	}
 	var formURL = formObj.action;
 	var formData = new FormData(formObj);
 	formData.append("fp", $('#sel1').val());
 	
 	$.ajax({
 		url: formURL,
 		type: 'POST',
 		data: formData,
 		mimeType: "multipart/form-data",
 		contentType: false,
 		cache: false,
 		processData: false,
 		success: function(data) {
 			successNotification('Save data successful!');
 		},
 		error: function(e, status, error) {
 			if(e.responseText) {
 				errorNotification(e.responseText);
 			}
 		}
 	});
 }
 
 function clickCancel(a, b, c, d, e, f) {
  	$(a).hide();
  	$(b).hide();
  	$(c).show();
  	$(d).attr('readonly', true);
  	$(d).addClass('tpone-input-edit');
  	$('.fy-drop').prop('disabled', true);
  	$('.fy-drop').css('background-color', '#eceeef');
  	$('.addmorewrap1').css('display', 'none');
 	$('.delmorewrap1').css('display', 'none');
 	$('.delrow').css('display', 'none');
  	$(e).hide();
  	if(f == 1) {
  		$('#dbTable').find(':input').each(function() {
  			switch(this.type) {
  				case 'text':
  					$(this).val('');
  					break;
  			}
  		});
  	} else if(f == 2) {
  		$('#dbTable' + f + ',#dbTableAddMore' + f).find(':input').each(function() {
  			switch(this.type) {
  				case 'text':
  					$(this).val('');
  					break;
  			}
  		});
  	} else {
  		$('#dbTable' + f).find(':input').each(function() {
  			switch(this.type) {
  				case 'text':
  					$(this).val('');
  					break;
  			}
  		});
  	}
  }
  
  function invokeOTP(btn) {
  	var state = "${client.statename}";
  	var gstname = "${client.gstname}";
  	$.ajax({
  		url: contextPath+"/verifyotp?state=" + state + "&gstName=" + gstname,
  		async: false,
  		cache: false,
  		dataType: "json",
  		contentType: 'application/json',
  		success: function(response) {
  			uploadResponse = response;
  			$('#downloadOtpModal').modal('show');
  		},
  		error: function(e, status, error) {
  			if(e.responseText) {
  				$('#idClientError').html(e.responseText);
  			}
  		}
  	});
  }
  
  function validateDownloadOtp() {
  	var otp1 = $('#dotp1').val();
  	var otp2 = $('#dotp2').val();
  	var otp3 = $('#dotp3').val();
  	var otp4 = $('#dotp4').val();
  	var otp5 = $('#dotp5').val();
  	var otp6 = $('#dotp6').val();
  	var otp = otp1 + otp2 + otp3 + otp4 + otp5 + otp6;
  	var pUrl = contextPath+"/ihubauth/" + otp;
  	$("#dwnldOtpEntryForm")[0].reset();
  	$.ajax({
  		type: "POST",
  		url: pUrl,
  		async: false,
  		cache: false,
  		data: JSON.stringify(uploadResponse),
  		dataType: "json",
  		contentType: 'application/json',
  		success: function(authResponse) {
  			$('#downloadOtpModalClose').click();
  			closeNotifications();
  		},
  		error: function(e, status, error) {
  			$('#downloadOtpModalClose').click();
  			if(e.responseText) {
  				errorNotification(e.responseText);
  			}
  		}
  	});
  }
  
  function evcFilingOTP() {
  	$.ajax({
  		url: contextPath+"/fotpevc/${id}/${clientid}/${returntype}/${month}/${year}",
  		async: false,
  		cache: false,
  		dataType: "json",
  		contentType: 'application/json',
  		success: function(data) {
  			$('#evcOtpModal').modal('show');
  		}
  	});
  }
  
  function fileEVC() {
  	var otp = $('#evcotp1').val();
  	$('#evcOtpModal').modal('hide');
  	$.ajax({
  		url: contextPath+'/fretevcfile/${id}/${clientid}/${returntype}/' + otp + '/${month}/${year}',
  		async: false,
  		cache: false,
  		dataType: "json",
  		contentType: 'application/json',
  		success: function(retResponse) {
  			if(retResponse.error && retResponse.error.message) {
  				errorNotification(retResponse.error.message);
  			} else if(retResponse.status_cd == '0') {
  				errorNotification(retResponse.status_desc);
  			} else if(retResponse.status_cd == '1') {
  				successNotification('Return filing successful!');
  			} else {
  				errorNotification('Unable to file returns!');
  			}
  		}
  	});
  }
  
  var k = 1;
  var l = 1;
  var x = 1;
  var y = 1
  
  function addmorerow(a) {
	  var rowCount1a = $('#table3a tbody tr').length;
	  var tablen1a = rowCount1a;
	  k = rowCount1a-1;
  	//var m = k - 1;
  	var tab = a;
  	if(a == "1a") {
  	    if(k == 1) {
  			$('.dbTable' + a).append('<tr id="addrow_' + a + '' + k + '"><td><input type="text" name="tcsR['+k+'].stin" class="form-control tpone-input" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please Enter Supplies GSTN" placeholder="07CQZCD1111I4Z7" maxlength="15" /></td><td><input type="text" name="tcsR['+k+'].supR" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsR['+k+'].retsupR" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsR['+k+'].amt" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsR['+k+'].iamt" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsR['+k+'].camt" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsR['+k+'].samt" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td class="text-left"><img src="'+contextPath+'/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(\''+k+'\',\'1a\')" class="delrow"></td></tr>');
  	      	k++;
  		} else {
  	    	$('.dbTable' + a).append('<tr id="addrow_' + a + '' + k + '"><td><input type="text" name="tcsR['+k+'].stin" class="form-control tpone-input" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please Enter Supplies GSTN" placeholder="07CQZCD1111I4Z7" maxlength="15" /></td><td><input type="text" name="tcsR['+k+'].supR" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsR['+k+'].retsupR" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsR['+k+'].amt" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsR['+k+'].iamt" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsR['+k+'].camt" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsR['+k+'].samt" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td class="text-left"><img src="'+contextPath+'/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(\''+k+'\',\'1a\')" class="delrow"></td></tr>');
  	      	k++;
  		}
  	}
  	 var rowCount1b = $('#table3b tbody tr').length;
	  var tablen1b = rowCount1b;
	  l = rowCount1b-1;
  	if(a == "1b") {
  		if(l == 1) {
  			$('.dbTable' + a).append('<tr id="addrow_' + a + '' + l + '"><td><input type="text" name="tcsU['+l+'].stin" class="form-control tpone-input" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please Enter Supplies GSTN" placeholder="07CQZCD1111I4Z7" maxlength="15" /></td><td><input type="text" name="tcsU['+l+'].supU" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsU['+l+'].retsupU" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsU['+l+'].amt" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsU['+l+'].iamt" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsU['+l+'].camt" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsU['+l+'].samt" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td class="text-left"><img src="'+contextPath+'/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(\''+l+'\',\'1b\')" class="delrow"></td></tr>');
  	      	l++;
  		} else {
  	    	$('.dbTable' + a).append('<tr id="addrow_' + a + '' + l + '"><td><input type="text" name="tcsU['+l+'].stin" class="form-control tpone-input" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please Enter Supplies GSTN" placeholder="07CQZCD1111I4Z7" maxlength="15" /></td><td><input type="text" name="tcsU['+l+'].supU" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsU['+l+'].retsupU" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsU['+l+'].amt" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsU['+l+'].iamt" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsU['+l+'].camt" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsU['+l+'].samt" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td class="text-left"><img src="'+contextPath+'/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(\''+k+'\',\'1b\')" class="delrow"></td></tr>');
  	      	l++;
  		}
  	}
  	 var rowCount2a = $('#table4a tbody tr').length;
	  var tablen2a = rowCount2a;
	  x = rowCount2a-1;
  	if(a == "2a") {
  		if(x == 1) {
  			$('.dbTable' + a).append('<tr id="addrow_' + a + '' + x + '"><td><input type="text" name="tcsaR['+x+'].stin" class="form-control tptwo-input" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please Enter Supplies GSTN" placeholder="07CQZCD1111I4Z7" maxlength="15" /></td><td><input type="text" name="tcsaR['+x+'].supR" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsaR['+x+'].retsupR" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsaR['+x+'].amt" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsaR['+x+'].iamt" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsaR['+x+'].camt" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsaR['+x+'].samt" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td class="text-left"><img src="'+contextPath+'/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(\''+x+'\',\'2a\')" class="delrow"></td></tr>');
  	      	x++;
  		} else {
  	    	$('.dbTable' + a).append('<tr id="addrow_' + a + '' + x + '"><td><input type="text" name="tcsaR['+x+'].stin" class="form-control tptwo-input" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please Enter Supplies GSTN" placeholder="07CQZCD1111I4Z7" maxlength="15" /></td><td><input type="text" name="tcsaR['+x+'].supR" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsaR['+x+'].retsupR" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsaR['+x+'].amt" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsaR['+x+'].iamt" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsaR['+x+'].camt" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsaR['+x+'].samt" class="form-control tpone-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td class="text-left"><img src="'+contextPath+'/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(\''+x+'\',\'2a\')" class="delrow"></td></tr>');
  	      	x++;
  		}
  	}
  	var rowCount2b = $('#table4b tbody tr').length;
	  var tablen2b = rowCount2b;
	  y = rowCount2b-1;
  	if(a == "2b") {
  		if(y == 1) {
  			$('.dbTable' + a).append('<tr id="addrow_' + a + '' + y + '"><td><input type="text" name="tcsaU['+y+'].stin" class="form-control tptwo-input" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please Enter Supplies GSTN" placeholder="07CQZCD1111I4Z7" maxlength="15" /></td><td><input type="text" name="tcsaU['+y+'].supU" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsaU['+y+'].retsupU" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsaU['+y+'].amt" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsaU['+y+'].iamt" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsaU['+y+'].camt" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsaU['+y+'].samt" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td class="text-left"><img src="'+contextPath+'/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(\''+y+'\',\'2b\')" class="delrow"></td></tr>');
  	      	y++;
  		} else {
  	    	$('.dbTable' + a).append('<tr id="addrow_' + a + '' + y + '"><td><input type="text" name="tcsaU['+y+'].stin" class="form-control tptwo-input" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please Enter Supplies GSTN" placeholder="07CQZCD1111I4Z7" maxlength="15" /></td><td><input type="text" name="tcsaU['+y+'].supU" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsaU['+y+'].retsupU" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsaU['+y+'].amt" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsaU['+y+'].iamt" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsaU['+y+'].camt" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td><input type="text" name="tcsaU['+y+'].samt" class="form-control tptwo-input" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter number with max 2 decimals" placeholder="0.00" maxlength="15" /></td><td class="text-left"><img src="'+contextPath+'/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(\''+y+'\',\'2b\')" class="delrow"></td></tr>');
  	      	y++;
  		}
  	}
  }
  
  
  var i = 1;
  $('.addmore-other-row').click(function() {
  	var j = i - 1;
  	if(i == 1) {
  		$('<tr id="addrow_' + i + '"><td>(H' + i + ')Other reversals (pl. specify)</td><td><input type="text" class="form-control" id="table7.other[' + i + '].desc" name= "table7.other[' + i + '].desc"></td><td><input type="text" class="form-control" id="table7.other[' + i + '].camt" name= "table7.other[' + i + '].camt" data-variavel="tab7H' + i + 'field1"></td><td><input type="text" class="form-control" id="table7.other[' + i + '].samt" name= "table7.other[' + i + '].samt" data-variavel="tab7H' + i + 'field2"></td><td><input type="text" class="form-control" id="table7.other[' + i + '].iamt" name= "table7.other[' + i + '].iamt" data-variavel="tab7H' + i + 'field3"></td><td><input type="text" class="form-control" id="table7.other[' + i + '].csamt" name= "table7.other[' + i + '].csamt" data-variavel="tab7H' + i + 'field4"></td><td class="text-left"><img src='+contextPath+'"/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow"></td></tr>').insertAfter($(this).closest('tr'));
  		var totalitcrev1 = $('#totalitcrev1').attr('data-formula');
  		var netitcutil1 = $('#netitcutil1').attr('data-formula');
  		totalitcrev1 = totalitcrev1 + '+#tab7H' + i + 'field1#';
  		$('#totalitcrev1').attr('data-formula', totalitcrev1);
  		netitcutil1 = netitcutil1 + '- #tab7H' + i + 'field1#';
  		$('#netitcutil1').attr('data-formula', netitcutil1);
  		var totalitcrev2 = $('#totalitcrev2').attr('data-formula');
  		var netitcutil2 = $('#netitcutil2').attr('data-formula');
  		totalitcrev2 = totalitcrev2 + '+#tab7H' + i + 'field2#';
  		$('#totalitcrev2').attr('data-formula', totalitcrev2);
  		netitcutil2 = netitcutil2 + '- #tab7H' + i + 'field2#';
  		$('#netitcutil2').attr('data-formula', netitcutil2);
  		var totalitcrev3 = $('#totalitcrev3').attr('data-formula');
  		var netitcutil3 = $('#netitcutil3').attr('data-formula');
  		totalitcrev3 = totalitcrev3 + '+#tab7H' + i + 'field3#';
  		$('#totalitcrev3').attr('data-formula', totalitcrev3);
  		netitcutil3 = netitcutil3 + '- #tab7H' + i + 'field3#';
  		$('#netitcutil3').attr('data-formula', netitcutil3);
  		var totalitcrev4 = $('#totalitcrev4').attr('data-formula');
  		var netitcutil4 = $('#netitcutil4').attr('data-formula');
  		totalitcrev4 = totalitcrev4 + '+#tab7H' + i + 'field4#';
  		$('#totalitcrev4').attr('data-formula', totalitcrev4);
  		netitcutil4 = netitcutil4 + '- #tab7H' + i + 'field4#';
  		$('#netitcutil4').attr('data-formula', netitcutil4);
  		i++;
  	} else {
  		$('<tr id="addrow_' + i + '"><td>(H' + i + ')Other reversals (pl. specify)</td><td><input type="text" class="form-control" id="table7.other[' + i + '].desc" name= "table7.other[' + i + '].desc"></td><td><input type="text" class="form-control" id="table7.other[' + i + '].camt" name= "table7.other[' + i + '].camt" data-variavel="tab7H' + i + 'field1"></td><td><input type="text" class="form-control" id="table7.other[' + i + '].samt" name= "table7.other[' + i + '].samt" data-variavel="tab7H' + i + 'field2"></td><td><input type="text" class="form-control" id="table7.other[' + i + '].iamt" name= "table7.other[' + i + '].iamt" data-variavel="tab7H' + i + 'field3"></td><td><input type="text" class="form-control" id="table7.other[' + i + '].csamt" name= "table7.other[' + i + '].csamt" data-variavel="tab7H' + i + 'field4"></td><td class="text-left"><img src="'+contextPath+'/static/mastergst/images/master/delicon.png" alt="Delete" onclick="deleteDocItem(this)" class="delrow"></td></tr>').insertAfter($('#addrow_' + j).closest('tr'));
  		var totalitcrev1 = $('#totalitcrev1').attr('data-formula');
  		var netitcutil1 = $('#netitcutil1').attr('data-formula');
  		totalitcrev1 = totalitcrev1 + '+#tab7H' + i + 'field1#';
  		$('#totalitcrev1').attr('data-formula', totalitcrev1);
  		netitcutil1 = netitcutil1 + '- #tab7H' + i + 'field1#';
  		$('#netitcutil1').attr('data-formula', netitcutil1);
  		var totalitcrev2 = $('#totalitcrev2').attr('data-formula');
  		var netitcutil2 = $('#netitcutil2').attr('data-formula');
  		totalitcrev2 = totalitcrev2 + '+#tab7H' + i + 'field2#';
  		$('#totalitcrev2').attr('data-formula', totalitcrev2);
  		netitcutil2 = netitcutil2 + '- #tab7H' + i + 'field2#';
  		$('#netitcutil2').attr('data-formula', netitcutil2);
  		var totalitcrev3 = $('#totalitcrev3').attr('data-formula');
  		var netitcutil3 = $('#netitcutil3').attr('data-formula');
  		totalitcrev3 = totalitcrev3 + '+#tab7H' + i + 'field3#';
  		$('#totalitcrev3').attr('data-formula', totalitcrev3);
  		netitcutil3 = netitcutil3 + '- #tab7H' + i + 'field3#';
  		$('#netitcutil3').attr('data-formula', netitcutil3);
  		var totalitcrev4 = $('#totalitcrev4').attr('data-formula');
  		var netitcutil4 = $('#netitcutil4').attr('data-formula');
  		totalitcrev4 = totalitcrev4 + '+#tab7H' + i + 'field4#';
  		$('#totalitcrev4').attr('data-formula', totalitcrev4);
  		netitcutil4 = netitcutil4 + '- #tab7H' + i + 'field4#';
  		$('#netitcutil4').attr('data-formula', netitcutil4);
  		i++;
  	}
  	dataFormula();
  });
  
  function deleteDocItem(no,table) {
	  	
		  if(table == '1a'){
			  var table3=document.getElementById("table3a");
			  if(no >= k){
				  no=no-1;
			  }
			  table3.deleteRow(parseInt(no)+1);
			  k--;
			  $("#table3a tbody tr").each(function(index) {
					 var rownoo = (index-1).toString();
					 $(this).find('input').each (function() {
							var inputname = $(this).attr('name');
							if(inputname != undefined){
								if(inputname.indexOf("tcsR[") >= 0) {
									inputname = replaceAt(inputname,5,rownoo);
									$(this).attr('name',inputname);
								}
							}
						});
			  });
		  }else if(table == '1b'){
			  var table3=document.getElementById("table3b");
			  if(no >= l){
				  no=no-1;
			  }
			  table3.deleteRow(parseInt(no)+1);
			  l--;
			  $("#table3b tbody tr").each(function(index) {
					 var rownoo = (index-1).toString();
					 $(this).find('input').each (function() {
							var inputname = $(this).attr('name');
							if(inputname != undefined){
								if(inputname.indexOf("tcsU[") >= 0) {
									inputname = replaceAt(inputname,5,rownoo);
									$(this).attr('name',inputname);
								}
							}
						});
			  });
		  }else if(table == '2a'){
			  var table3=document.getElementById("table4a");
			  if(no >= x){
				  no=no-1;
			  }
			  table3.deleteRow(parseInt(no)+1);
			  x--;
			  $("#table4a tbody tr").each(function(index) {
					 var rownoo = (index-1).toString();
					 $(this).find('input').each (function() {
							var inputname = $(this).attr('name');
							if(inputname != undefined){
								if(inputname.indexOf("tcsaR[") >= 0) {
									inputname = replaceAt(inputname,6,rownoo);
									$(this).attr('name',inputname);
								}
							}
						});
			  });
		  }else if(table == '2b'){
			  var table3=document.getElementById("table4b");
			  if(no >= y){
				  no=no-y;
			  }
			  table3.deleteRow(parseInt(no)+1);
			  y--;
			  $("#table4b tbody tr").each(function(index) {
					 var rownoo = (index-1).toString();
					 $(this).find('input').each (function() {
							var inputname = $(this).attr('name');
							if(inputname != undefined){
								if(inputname.indexOf("tcsaU[") >= 0) {
									inputname = replaceAt(inputname,6,rownoo);
									$(this).attr('name',inputname);
								}
							}
						});
			  });
		  }
	  }
  
  function updateInvoiceStatus() {
  	$.ajax({
  		url: _getContextPath() + '/otpexpiry/' + clientId,
  		async: false,
  		cache: false,
  		contentType: 'application/json',
  		success: function(response) {
  			if(response == "OTP_VERIFIED") {
  				window.location.href = _getContextPath() + '/updateInvStatus' + commonSuffix + '/' + clientId + '/GSTR1/' + month + '/' + year;
  			} else {
  				errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
  			}
  		}
  	});
  }
  function otpExpiryCheck(){
		otpExpirycheck = "";
		$.ajax({
			url : _getContextPath()+'/otpexpiry/'+clientId,
			async: false,
			cache: false,
			contentType: 'application/json',
			success : function(response) {
				otpExpirycheck = response;
			}
		});
	}
  function uploadInvoice(id, usertype, clientid, returntype, month, year) {
  	otpExpiryCheck();
  	//otpExpirycheck = "OTP_VERIFIED";
  	var fUrl = contextPath+"/ihubsavestatus/"+id+"/"+usertype+"/"+clientid+"/"+returntype+"?month="+month+"&year="+year+ "&hsn=hsn";
  	if(otpExpirycheck == "OTP_VERIFIED") {
  		//$(btn).addClass('btn-loader');
  		var invArray = new Array();
  		var pUrl = contextPath+"/ihubsavestatus/"+id+"/"+usertype+"/"+clientid+"/"+returntype+"?month="+month+"&year="+year+ "&hsn=hsn";
  		$.ajax({
  			type: "POST",
  			url: pUrl,
  			async: false,
  			cache: false,
  			dataType: "json",
  			data: JSON.stringify(invArray),
  			contentType: 'application/json',
  			success: function(response) {
  				//$(btn).removeClass('btn-loader');
  				if(response.data && response.data.error_report && response.data.error_report.error_msg) {
  					errorNotification(response.data.error_report.error_msg);
  				} else if(response.status_cd == '1') {
  					successNotification('Upload GSTR8 completed successfully!');
  				} else {
  					if(response.error && response.error.message) {
  						errorNotification(response.error.message);
  					} else if(response.status_cd == '0') {
  						if(response.status_desc == 'OTP verification is not yet completed!' || response.status_desc == 'Invalid Session' || response.status_desc == 'Unauthorized User!' || response.status_desc == 'Missing Mandatory Params'  || response.status_desc == 'API Authorization Failed') {
  							errorNotification('Your OTP Session Expired, Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
  						} else if(response.status_desc == 'Your subscription has expired. Kindly subscribe to proceed further!') {
  							errorNotification('Your subscription has expired. Kindly <a href="'+contextPath+'/dbllng/${id}/${fullname}/${usertype}/${month}/${year}" class="btn btn-sm btn-blue-dark">Subscribe</a> to proceed further! ');
  						} else {
  							errorNotification(response.status_desc);
  						}
  					}
  				}
  			},
  			error: function(e, status, error) {
  				//$(btn).removeClass('btn-loader');
  				if(e.responseText) {
  					errorNotification(e.responseText);
  				}
  			}
  		});
  	} else {
  		errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
  	}
  }