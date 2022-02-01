var custTable = new Object();
$(document).ready(function(){
	$.ajax({
		url: contextPath+'/bnkdtls'+urlSuffixs,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(bankDetails) {
			customerClientBankDetails = new Array();
				$("#selectCustomerBank").append($("<option></option>").attr("value","").text("-- Select Bank --"));
				for (var i=0; i<bankDetails.length; i++) {
					$("#selectCustomerBank").append($("<option></option>").attr("value",bankDetails[i].accountnumber).text(bankDetails[i].bankname));
					customerClientBankDetails.push(bankDetails[i]);
				}
		}
	});
	
	$('#bname').keyup(function() {
		var spath = $("#bname").val();
		if(spath != ''){
			$('#customerLedgerName').val(spath);
		}else{
			$('#customerLedgerName').val('');
		}
	});
	$('.addCustomerTermsDetails').click(function(){
		if($(".addCustomerTermsDetails").is(':checked')){
	      $("#selectTermsDiv").css('display','block');
	    }else{
	    	 $("#selectTermsDiv").css('display','none');
	    	 $('#customerterms').val('');
	    }
	});
	$('.addCustomerBankDetails').click(function(){
		if($(".addCustomerBankDetails").is(':checked')){
	      $("#selectBankDiv2").css('display','none');
	      $("#selectBankDiv3").css('display','block');
	      $("#notrequired").css('display','none');
		  
	    }else{
			$('#selectCustomerBank').val('');
			$('#customerBankName').val(' ');
			$('#customerBankAcctNo').val(' ');
			$('#customerBankBranch').val(' ');
			$('#customerBankIFSC').val(' ');
			$('#customerBankAccountName').val(' ');
			$('#customerBankName_txt').html(' ');
			$('#customerBankAcctNo_txt').html(' ');
			$('#customerBankBranch_txt').html(' ');
			$('#customerBankIFSC_txt').html(' ');
			$('#customerBankAccountName_txt').html(' ');
		  $("#selectBankDiv2").css('display','none');
		  $("#selectBankDiv3").css('display','none');
		  $("#notrequired").css('display','block');
	   }
	});
	var stateoptions = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return _getContextPath()+"/stateconfig?query="+ phrase + "&format=json";
			},
			getValue: "name",
			list: {
				onLoadEvent: function() {
					if($("#eac-container-state ul").children().length == 0) {
						//$("#state").val('');
						$("#stateempty").show();
					} else {
						$("#stateempty").hide();
					}
				},
				maxNumberOfElements: 37
			}
		};

		$(".state").easyAutocomplete(stateoptions);
		//google.maps.event.addDomListener(window, 'load', initialize);
		$('input[type=radio][name=type]').change(function() {
			updateState(this.value);
		});
		function updateState(value) {
			if (value == 'Business') {
				$('#idName').html('Business Name');
				$('.category_business').show();
			} else {
				$('#idName').html('Individual Name');
				$('.category_business').hide();
				$('#bgstnnumber').val("");
				$('#bgstnnumber').attr("required",false);
			}
			$('form[name="userform"]').validator('update');
		}
		$.ajax({
			url: _getContextPath()+"/countrieslist",
			contentType: 'application/json',
			success : function(response) {
				$('#countriesList').append('<option value="India">India</option>');
				for(var i = 0; i < response.length; i++) {
					$('#countriesList').append("<option value="+response[i].name+">"+response[i].name+"</option>");    
				}
			}
		});	
		$('#customers_form').submit(function(e) {
			var err = 0;
			var customerid=$('#customerId').val();
			var oldcustomerid=$('#oldcustomerid').val();
			//var clientid='<c:out value="${client.id}"/>';
			if(oldcustomerid == '' || oldcustomerid != customerid){
				$.ajax({
					type : "GET",
					async: false,
					contentType : "application/json",
					url: _getContextPath()+"/customeridexits/"+clientId+"/"+customerid,
					success : function(response) {
						if(response == 'success'){
							$('#customerId_Msg').text('customer id already exists');
							$('.customersid').addClass('has-error has-danger');
							err=1;
							}else{
							$('#customerId_Msg').text('');
							$('.customersid').removeClass('has-error has-danger');
						}
					}
				});
			}else{
				err = 0;
			}
			if(err != 0){
				$('#customerId_Msg').text('customer id already exists');
				$('.customersid').addClass('has-error has-danger');
				return false;
			}
		});
		
		$('.state').prop('readonly',false);

		$('.country').change(function(){
			var country=$('.country').val();
			if(country == 'India'){
				$('.state').val('');
				$('.state').prop('readonly',false);
				$('#bgstnnumber').prop('readonly',false);
				$('#bgstnnumber').val('');
				$('.with-errors').text("");
			}else if(country == ''){
				$('.state').val('');
				$('.state').prop('readonly',true);
				$('#bgstnnumber').prop('readonly',false);
				$('.state').prop('readonly',true);
				$('#bgstnnumber').val('');
			}else{
				$('.state').val('');
				$('.state').prop('readonly',true);
				$('.state').val('97-Other Territory');
				$('#bgstnnumber').prop('readonly',true);		
				$('#bgstnnumber').val('');
			}
		});
		$(".custind_format").each(function(){
			    $(this).html($(this).html().replace(/,/g , ''));
			});
		  OSREC.CurrencyFormatter.formatAll({selector : '.custind_format'});
});

function loadCustomerTable(id, clientId, month, year, userType, fullname){
	var cUrl = _getContextPath()+'/getcustomers/'+id+'/'+fullname+'/'+userType+'/'+clientId+'/'+month+'/'+year;
	custTable = $('#dbTable1').DataTable({
		"dom": '<"toolbar"f>lrtip<"clear">',
		 "processing": true,
		 "serverSide": true,
		 "lengthMenu": [ [10, 25, 50, 100, -1], [10, 25, 50, 100, "All"] ],
	     "ajax": {
	         url: cUrl,
	         type: 'GET',
	         contentType: 'application/json; charset=utf-8',
	         dataType: "json",
	         'dataSrc': function(resp){
	        	 $('#customerCheck').prop('checked', false);
				 resp.recordsTotal = resp.totalElements;
	        	 resp.recordsFiltered = resp.totalElements;
	        	 return resp.content;
	         }
	     },
		"paging": true,
		'pageLength':10,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		"order": [[2,'desc']],
		'columns': getCustomerColumns(id, clientId, userType, month, year),
		'columnDefs' : getCustomerColumnDefs()
		 
	});
	
	$('#customersBody').on('click','tr', function(e){
		if (!$(e.target).closest('.nottoedit').length) {
			var dat = custTable.row($(this)).data();
			editCustomersPopup(dat.userid);
		}
	});
	//$("div.toolbar").html('<h4 style="margin-top: 6px;">Customers</h4>');
}
function editCustomersPopup(customerId){
	$('#editModal').modal("show");
	$.ajax({
		url: _getContextPath()+"/countrieslist",
		contentType: 'application/json',
		success : function(response) {
			$('#countriesList').append('<option value="India">India</option>');
			for(var i = 0; i < response.length; i++) {
				$('#countriesList').append("<option value="+response[i].name+">"+response[i].name+"</option>");    
			}
		}
	});
	 getCustomersData(customerId, function(customer) {
	        if (customer.userid == customerId) {
	        	$('#bname').val(customer.name);
	        	if(customer.type != '') {
					$('input[name=type][value="'+customer.type+'"]').prop('checked', 'checked');
					updateState(customer.type);
				}
	        	$('#bgstnnumber').val(customer.gstnnumber);
				$('#bcontactperson').val(customer.contactperson);
				$('.email').val(customer.email);
				$('#bmobilenumber').val(customer.mobilenumber);
				$('.address').val(customer.address);
				$('.pincode').val(customer.pincode);
				$('.city').val(customer.city);
				$('.landline').val(customer.landline);
				$('.state').val(customer.state);
				$('.credit_p').val(customer.creditPeriod);
				$('.creditAmt').val(customer.creditAmount);
				$('.openingbalance').val(customer.openingbalance);
				$("#customerLedgerName").val(customer.customerLedgerName);
				$('#countriesList').append("<option value="+customer.country+">"+customer.country+"</option>");
				if(customer.customerAccountNumber != ''){
					$('.addCustomerBankDetails').prop("checked",true);
				}else{
					$('.addCustomerBankDetails').prop("checked",false);
				}
				if($('.addCustomerBankDetails').is(":checked")){
					$("#selectBankDiv2").show();
					$("#selectBankDiv3").show();
					$("#notrequired").css('display','none');
				}else{
					$("#selectBankDiv2").hide();
					$("#selectBankDiv3").hide();
					$("#notrequired").css('display','block');
				}
				
				$('#selectCustomerBank').val(customer.customerAccountNumber);
				$('#customerBankName').val(customer.customerBankName);
				$('#customerBankAcctNo').val(customer.customerAccountNumber);
				$('#customerBankBranch').val(customer.customerBranchName);
				$('#customerBankIFSC').val(customer.customerBankIfscCode);
				$('#customerBankAccountName').val(customer.customerAccountName);
				$('#customerBankName_txt').html(customer.customerBankName);
				$('#customerBankAcctNo_txt').html(customer.customerAccountNumber);
				$('#customerBankBranch_txt').html(customer.customerBranchName);
				$('#customerBankIFSC_txt').html(customer.customerBankIfscCode);
				$('#customerBankAccountName_txt').html(customer.customerAccountName);
				$('#custid').val(customer.userid).attr('name', 'id');
				
				//$("input[name='createdDate']").val(customer.createdDate);
				//$("input[name='createdBy']").val(customer.createdBy);
				$('#oldcustomerid').val(customer.customerId);
				$('#customerId').val(customer.customerId);
				$('#customerTanPanNumber').val(customer.customerTanPanNumber);
				$('#customerPanNumber').val(customer.customerPanNumber);
				$('#customerTanNumber').val(customer.customerTanNumber);
				if(customer.isCustomerTermsDetails == true || customer.isCustomerTermsDetails == "true"){
					$("#selectTermsDiv").css("display","block");
					$('.addCustomerTermsDetails').prop("checked",true);
					var custTerms = customer.customerterms;
					custTerms = custTerms.replace("#mgst# ", "\r\n");
					$("#customerterms").val(custTerms);
				}else{
					$("#selectTermsDiv").css("display","none");
					$('.addCustomerTermsDetails').prop("checked",false);
				}
	        }
	 });
}
function getCustomersData(customerId, popudateCustomerData){
	var urlStr = _getContextPath()+'/getCustomer/'+customerId;
	$.ajax({
		url: urlStr,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			popudateCustomerData(response);
		}
	});
}
function getCustomerColumns(id, clientId, userType, month, year){
	var chkBx = {data: function ( data, type, row ) {
			return '<div class="checkbox nottoedit" index="'+data.userid+'"><label><input type="checkbox" id="customerFilter'+data.userid+'" onClick="updateCustomerSelection(\''+data.userid+'\',this)"/><i class="helper"></i></label></div>';
	}};
	var custId = {data:  function ( data, type, row ) {
		var customerid = data.customerId ? data.customerId : "";
		return '<span class="text-left invoiceclk">'+customerid+'</span>';
		}};
	var custname = {data:  function ( data, type, row ) {
		var name = data.name ? data.name : "";
		return '<span class="text-left invoiceclk">'+name+'</span>';
		}};
	var category = {data:  function ( data, type, row ) {
		var ctype = data.type ? data.type : "";
		return '<span class="text-left invoiceclk">'+ctype+'</span>';
		}};
	var mobileNo = {data:  function ( data, type, row ) {
		var mobilenumber = data.mobilenumber ? data.mobilenumber : "";
		return '<span class="text-left invoiceclk">'+mobilenumber+'</span>';
		}};
	var state = {data:  function ( data, type, row ) {
		var cstate = data.state ? data.state : "";
		return '<span class="text-left invoiceclk">'+cstate+'</span>';
		}};
	var email = {data:  function ( data, type, row ) {
		var cemail = data.email ? data.email : "";
		return '<span class="text-left invoiceclk">'+cemail+'</span>';
		}};
	var credPeriod = {data:  function ( data, type, row ) {
		var credPeriod = data.creditPeriod ? data.creditPeriod : "";
		return '<span class="text-left invoiceclk">'+credPeriod+'</span>';
		}};
	var credAmt = {data:  function ( data, type, row ) {
		var credAmount = data.creditAmount ? data.creditAmount : "";
		return '<span class="text-left invoiceclk">'+credAmount+'</span>';
		}};
	
	return [chkBx , custId,custname, category, mobileNo, state, email,credPeriod,credAmt,
        {data: function ( data, type, row ) {
     		 return '<a class="btn-edt permissionSettings-Customers-Edit" href="#" data-toggle="modal" data-target="#editModal" onClick=""><i class="fa fa-edit"></i> </a><a href="#" class="permissionSettings-Customers-Delete nottoedit" onClick="showDeletePopup(\''+data.userid+'\',\''+data.name+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a>';
            }} 
        ];
}
function getCustomerColumnDefs(){
	return  [
		{
			"targets":  [1,2,3,4,5,6,7, 8, 9],
			className: 'dt-body-left'
		},
		{
			"targets": 0,
			"orderable": false
		}
		
	];
}

function updateCustomerSelection(id,chkBox){
	if(chkBox.checked) {
		customerArray.push(id);
	} else {
		var cArray=new Array();
		customerArray.forEach(function(customer) {
			if(customer != id) {
				cArray.push(customer);
			}
		});
		customerArray = cArray;
	}
	if(customerArray.length > 0){
		$('#customerDelete').removeClass("disabled");
	}else{
		$('#customerDelete').addClass("disabled");
	}
}
function updateCustomersMainSelection(chkBox) {
	customerArray = new Array();
	var check = $('#customerCheck').prop("checked");
    var rows = custTable.rows().nodes();
    if(check) {
    	custTable.rows().every(function () {
	    	var row = this.data();
	    	customerArray.push(row.userid);
	   });
    }
    if(customerArray.length > 0){
		$('#customerDelete').removeClass("disabled");
	}else{
		$('#customerDelete').addClass("disabled");
	}
    $('input[type="checkbox"]', rows).prop('checked', check);
}
function showCustomersDeleteAllPopUp(){
	$('#deleteModal').modal('show');
	$('#delPopupDetails').html(name);
	$('#btnDelete').attr('onclick', "deleteSelectedCustomer()");
}
function deleteSelectedCustomer(){
	if(customerArray.length > 0) {
		for(var i=0;i<customerArray.length;i++) {
			deleteSelectCustomers(customerArray[i]);
		}
	} else {
		deleteSelectCustomers(new Array());
	}
}
function deleteSelectCustomers(custArray){
	$.ajax({
			url: _getContextPath()+"/delcustomer/"+custArray,
			success : function(response) {
				custTable.row( $('#row'+custArray) ).remove().draw();
				location.reload(true);
			}
		});
}

function removemsg1(){
	$('.errormsg').css('display','none');
} 
function initialize() {
	var address = document.getElementById('addresss');
	var autocomplete = new google.maps.places.Autocomplete(address);
}
function showDeletePopup(customerId, name) {
	$('#deleteModal').modal('show');
	$('#delPopupDetails').html(name);
	$('#btnDelete').attr('onclick', "deleteProduct('"+customerId+"')");
}

function deleteProduct(customerId) {
	$.ajax({
		url: contextPath+"/delcustomer/"+customerId,
		success : function(response) {
			custTable.row( $('#row'+customerId) ).remove().draw();
			location.reload(true);
		}
	});
}

$('#editModal').on('hidden.bs.modal', function (e) {
	$('.state').val('');  
});

function selectCustomerBankName() {
	//var bankaccountNumber = $('#selectBank').val();
	$("#selectBankDiv2").css('display','none');
	$('#customerBankName_txt').html(' ');
	$('#customerBankAcctNo_txt').html(' ');
	$('#customerBankBranch_txt').html(' ');
	$('#customerBankIFSC_txt').html(' ');
	$('#customerBankAccountName_txt').html(' ');
	$('#customerBankName').val(' ');
	$('#customerBankAcctNo').val(' ');
	$('#customerBankBranch').val(' ');
	$('#customerBankIFSC').val(' ');
	$('#customerBankAccountName').val(' ');
	if($('#selectCustomerBank').val() !=''){
		$("#selectBankDiv2").css('display','block');
		var bankaccountNumber = $('#selectCustomerBank').val();
		customerClientBankDetails.forEach(function(bankdetail) {
			if(bankdetail.accountnumber == bankaccountNumber) {
				$('#customerBankName_txt').html(bankdetail.bankname);
				$('#customerBankAcctNo_txt').html(bankdetail.accountnumber);
				$('#customerBankBranch_txt').html(bankdetail.branchname);
				$('#customerBankIFSC_txt').html(bankdetail.ifsccode);
				$('#customerBankAccountName_txt').html(bankdetail.accountName);
				
				$('#customerBankName').val(bankdetail.bankname);
				$('#customerBankAcctNo').val(bankdetail.accountnumber);
				$('#customerBankBranch').val(bankdetail.branchname);
				$('#customerBankIFSC').val(bankdetail.ifsccode);
				$('#customerBankAccountName').val(bankdetail.accountName);
			}
		});
	}
}
function invokePublicAPI(btn) {
	var gstnno = $("#bgstnnumber").val();
	updatePan(gstnno);
	var userid = $("#custuserid").val();
	if(gstnno != '') {
		var gstnumber = gstnno.toUpperCase();
		$(btn).addClass('btn-loader');
		$.ajax({
			url: _getContextPath()+"/publicsearch?gstin="+gstnumber+"&userid="+userid,
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				if(response.error && response.error.message) {	
					if(response.error.message == 'SWEB_9035'){
						$('#bgstnnumber_Msg').text("No Records Found");	
				  	} else{
						$('#bgstnnumber_Msg').text(response.error.message);
				  	}
				}
				if(response.status_cd == '1') {
					if(response.data) {
						var address = "";
						if(response.data['tradeNam'] == '' || response.data['tradeNam'] == null){
							$('#bname').val(response.data['lgnm']);
							$('#customerLedgerName').val(response.data['lgnm']);
							if(response.data['lgnm'] != ''){
								$('#bmsg ul.list-unstyled li').html('');
								$('#bmsg').removeClass('has-error has-danger');
							}
						}else{
							$('#bname').val(response.data['tradeNam']);
							$('#customerLedgerName').val(response.data['tradeNam']);
							if(response.data['tradeNam'] != ''){
								$('#bmsg ul.list-unstyled li').html('');
								$('#bmsg').removeClass('has-error has-danger');
							}
						}
					
					Object.keys(response.data).forEach(function(key) {
						if(key == 'pradr'){
							Object.keys(response.data['pradr']['addr']).forEach(function(key){
								if(response.data['pradr']['addr'][key] != ''){
									if(key != 'pncd' && key != 'stcd'){
										address = address.concat(response.data['pradr']['addr'][key]+",");
									}
									if(key == 'pncd'){
										$('#customerpincode').val(response.data['pradr']['addr'][key]);
									}
									if(key == 'stcd'){
										$('#customercity').val(response.data['pradr']['addr'][key]);
									}
								}
							});
						}
					});
					$('#addresss').val(address.slice(0,-1));
					}
				}
				$(btn).removeClass('btn-loader');
				$('#customersave').removeClass('disabled');
			},
			error : function(e, status, error) {
				$(btn).removeClass('btn-loader');
			}
		});
	}
}
function labelcustsubmit(){
	$('#customers_form').submit();
}
$('#customerimportModal').submit(function(e) {
	  var err = 0;
	    if (!$('#messages6').html()) {    	
	      err = 1;
	    }
		  if (err != 0) {
		   $('.errormsg').css('display','block');
	    return false;
	  }
	});
	function choosecustomerfileSheets(){
		$('#customerFile')[0].click()
	}
	function shoeCustomersPopup(){
		$('#editModal').modal("show");
		 var today = new Date();
		 var year = today.getFullYear();
		 $('.openingDtText').text(year);
	}