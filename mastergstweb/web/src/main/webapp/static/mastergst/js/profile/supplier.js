var supTable = new Object();
$(document).ready(function() {
	
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
	var ledgeroptions = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return _getContextPath()+"/ledgerlist/${client.id}?query="+ phrase + "&format=json";
			},
			getValue: "ledgerName",
			
			list: {
				match: {
					enabled: true
				},
			onChooseEvent: function() {
				var groupdetails = $("#ledgerName").getSelectedItemData();
			}, 
				onLoadEvent: function() {
					if($("#eac-container-ledgerName ul").children().length == 0) {
						$("#addlegername").show();
					} else {
						$("#addlegername").hide();
					}
				},
				maxNumberOfElements: 10
			},
		};
	$('#supplierLedgerName').easyAutocomplete(ledgeroptions);
	
	$('#suppliers_form').submit(function(e) {
		var err = 0;
		var customerid=$('#customerId').val();
		var clientid='<c:out value="${client.id}"/>';
		var oldsupplierid = $('#oldsupplierid').val();
		var supplierid=$('#supplierCustomerId').val();
		//var clientid='<c:out value="${client.id}"/>';
		if(oldsupplierid == '' || oldsupplierid != supplierid){
		$.ajax({
			type : "GET",
			async: false,
			contentType : "application/json",
			url: _getContextPath()+"/suppliercustomeridexits/"+clientId+"/"+supplierid,
			success : function(response) {
				if(response == 'success'){
					$('#supplierCustomerId_Msg').text('supplierid already exists');
					$('.suppliersid').addClass('has-error has-danger');
					err=1;
				}else{
					$('#supplierCustomerId_Msg').text('');
					$('.suppliersid').removeClass('has-error has-danger');
				}
			}
		});
		}else{
			err = 0;
		}
		if(err != 0){
			$('#supplierCustomerId_Msg').text('supplierid already exists');
			$('.suppliersid').addClass('has-error has-danger');
			return false;
		}
	});
	$('#editModal').on('hidden.bs.modal', function (e) {
		$('.state').val('');	  
	});
	$('.state').prop('readonly',false);		
	$('.country').change(function(){
		var country=$('.country').val();
		if(country == 'India'){
			$('.state').val('');
			$('.state').prop('readonly',false);
			$('#suppliergstnnumber').prop('readonly',false);
			$('#suppliergstnnumber').val('');
			$('.with-errors').text("");
		}else if(country == ''){
			$('.state').val('');
			$('.state').prop('readonly',true);
			$('#suppliergstnnumber').prop('readonly',false);
			$('.state').prop('readonly',true);
			$('#suppliergstnnumber').val('');
		}else{
			$('.state').val('');
			$('.state').prop('readonly',true);
			$('.state').val('97-Other Territory');
			$('#suppliergstnnumber').prop('readonly',true);		
			$('#suppliergstnnumber').val('');
		}
	});
	$('#suppliername').keyup(function() {
		var spath = $("#suppliername").val();
		if(spath != ''){
			$('#supplierLedgerName').val(spath);
		}else{
			$('#supplierLedgerName').val('');
		}
	});
	$('.addSupplierTermsDetails').click(function(){
		if($(".addSupplierTermsDetails").is(':checked')){
	      $("#selectsTermsDiv").css('display','block');
	    }else{
	    	 $("#selectsTermsDiv").css('display','none');
	    	 $('#suppliererterms').val('');
	    }
	});
});

function loadSupplierTable(id, clientId, month, year, userType, fullname){
	var cUrl = _getContextPath()+'/getSuppliers/'+id+'/'+fullname+'/'+userType+'/'+clientId+'/'+month+'/'+year;
	supTable = $('#supplierTable').DataTable({
		"dom": '<"toolbar"f>lrtip<"clear">',
		 "processing": true,
		 "serverSide": true,
		 "lengthMenu": [ [10, 25, 50, 100, -1], [10, 25, 50, 100, "All"] ],
	     "ajax": {
	         url: cUrl,
	         type: 'GET',
	         contentType: 'application/json',
	         dataType: "json",
	         'dataSrc': function(resp){
	        	 $('#supplierCheck').prop('checked', false);
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
			'columns': getSupplierColumns(id, clientId, userType, month, year),
			'columnDefs' : getSupplierColumnDefs()
	});
	$('#supplierBody').on('click','tr', function(e){
		if (!$(e.target).closest('.nottoedit').length) {
			var dat = supTable.row($(this)).data();
			editSuppliersPopup(dat.userid);
		}
	//$("div.toolbar").html('<h4 style="margin-top: 6px;">Customers</h4>');
});
}
function editSuppliersPopup(supplierId){
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
	 getSuppliersData(supplierId, function(supplier) {
	        if (supplier.userid == supplierId) {
	        	$('#suppliername').val(supplier.name);
	        	
	        	if((supplier.gstnnumber == '' || supplier.gstnnumber == null) && (supplier.type == '' || supplier.type == null)){
	        		$('input[name=type][value="Individual"]').prop('checked', 'checked');
	        		updateState("Individual");
	        	}else{
	        		if((supplier.gstnnumber == '' && supplier.gstnnumber == null) && (supplier.type != '' || supplier.type != null)){
	        			$('input[name=type][value="'+supplier.type+'"]').prop('checked', 'checked');
	        			updateState(supplier.type);
	        		}else if((supplier.gstnnumber != '' && supplier.gstnnumber != null) && (supplier.type == '' || supplier.type == null)) {
	        			$('input[name=type][value="Business"]').prop('checked', 'checked');
	        			updateState("Business");
	        		}else if(supplier.gstnnumber != '' && supplier.gstnnumber != null) {
	        			$('input[name=type][value="'+supplier.type+'"]').prop('checked', 'checked');
	        			updateState(supplier.type);
	        		}	
	        	}
	        	
				$('#beneficiaryName').val(supplier.beneficiaryName);
				$('.bankName').val(supplier.bankName);
				$('#branchAddress').val(supplier.branchAddress);
				$('#accountNumber').val(supplier.accountNumber);
				$('#ifscCode').val(supplier.ifscCode);
				$('#accountType').val(supplier.accountType);
				$('#micrCode').val(supplier.micrCode);
				$('#fulltimeEmployees').val(supplier.fulltimeEmployees);
				$('.faxno').val(supplier.faxNo);
				$('#regNo').val(supplier.companyRegNo);
				$('#dateofinception').val(supplier.dateofInception);
				$('#natuteOfExpertise').val(supplier.natuteOfExpertise);
				$('#turnover').val(supplier.turnover);
				$('#programsAndCert').val(supplier.programsAndCert);
				$('#applicability').val(supplier.applicability);
				$('#suppliergstnnumber').val(supplier.gstnnumber);
				$('#suppliercontactperson').val(supplier.contactperson);
				$('#supplieremail').val(supplier.email);
				$('#suppliermobilenumber').val(supplier.mobilenumber);
				$('#supplieraddresss').val(supplier.address);
				$('#supplierpincode').val(supplier.pincode);
				$('#suppliercity').val(supplier.city);
				$('#supplierlandline').val(supplier.landline);
				$('.supplierstate').val(supplier.state);
				$('#supplierLedgerName').val(supplier.supplierLedgerName);
				$('#countriesList').append("<option value="+supplier.country+">"+supplier.country+"</option>");
				$('#supid').val(supplier.userid).attr('name', 'id');
				//$("input[name='createdDate']").val(supplier.createdDate);
				//$("input[name='createdBy']").val(supplier.createdBy);
				$('#oldsupplierid').val(supplier.supplierCustomerId);
				$('#supplierCustomerId').val(supplier.supplierCustomerId);
				$('#supplierTanPanNumber').val(supplier.supplierTanPanNumber);
				$('#supplierPanNumber').val(supplier.supplierPanNumber);
				$('#supplierTanNumber').val(supplier.supplierTanNumber);
				if(supplier.isSupplierTermsDetails == true || supplier.isSupplierTermsDetails == "true"){
					$("#selectsTermsDiv").css("display","block");
					$('.addSupplierTermsDetails').prop("checked",true);
					var custTerms = supplier.supplierterms;
					custTerms = custTerms.replace("#mgst# ", "\r\n");
					$("#supplierterms").val(custTerms);
					
				}else{
					$("#selectsTermsDiv").css("display","none");
					$('.addSupplierTermsDetails').prop("checked",false);
				}

	        }
	 });
}
function getSuppliersData(supplierId, popudateSupplierData){
	var urlStr = _getContextPath()+'/getSupplier/'+supplierId;
	$.ajax({
		url: urlStr,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			popudateSupplierData(response);
		}
	});
}
function getSupplierColumns(id, clientId, userType, month, year){
	var chkBx = {data: function ( data, type, row ) {
			return '<div class="checkbox nottoedit" index="'+data.userid+'"><label><input type="checkbox" id="supplierFilter'+data.userid+'" onClick="updateSupplierSelection(\''+data.userid+'\',this)"/><i class="helper"></i></label></div>';
	}};
	var supId = {data:  function ( data, type, row ) {
		var supplierid = data.supplierCustomerId ? data.supplierCustomerId : "";
		return '<span class="text-left invoiceclk">'+supplierid+'</span>';
		}};
	
	var supname = {data:  function ( data, type, row ) {
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
	
	return [chkBx , supId,supname, category, mobileNo, state, email,
        {data: function ( data, type, row ) {
     		 return '<a class="btn-edt permissionSettings-Suppliers-Edit" href="#" data-toggle="modal" data-target="#editModal" onClick=""><i class="fa fa-edit"></i> </a><a href="#" class="permissionSettings-Suppliers-Delete nottoedit" onClick="showDeletePopup(\''+data.userid+'\',\''+data.name+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a>';
            }} 
        ];
}
function getSupplierColumnDefs(){
	return  [
		{
			"targets":  [1,2,3,4,5,6],
			className: 'dt-body-left'
		},
		{
			"targets": 0,
			"orderable": false
		}
		
	];
}
function updateState(value) {
	if (value == 'Business') {
		$('#idName').html('Business Name');
		$('.category_business').show();
	} else {
		$('#idName').html('Individual Name');
		$('#bgstnnumber').val("");
		$('.category_business').hide();
	}
}
function removemsg1(){
	$('.errormsg').css('display','none');
} 
function initialize() {
	var address = document.getElementById('supplieraddress');
	var autocomplete = new google.maps.places.Autocomplete(address);
}

function invokePublicAPI(btn) {
	var gstnno = $("#suppliergstnnumber").val();
	updatePan(gstnno);
	var userid = $('#suppuserid').val();
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
						$('#sgstnnumber_Msg').text("No Records Found");	
				  	} else{
						$('#sgstnnumber_Msg').text(response.error.message);
				  	}
				}
				if(response.status_cd == '1') {
					if(response.data) {
						var address = "";
						if(response.data['tradeNam'] == '' || response.data['tradeNam'] == null){
							$('#suppliername').val(response.data['lgnm']);
							$('#supplierLedgerName').val(response.data['lgnm']);
						}else{
							$('#suppliername').val(response.data['tradeNam']);
							$('#supplierLedgerName').val(response.data['tradeNam']);
						}
					Object.keys(response.data).forEach(function(key) {
						if(key == 'pradr'){
						Object.keys(response.data['pradr']['addr']).forEach(function(key){
							if(response.data['pradr']['addr'][key] != ''){
								if(key != 'pncd' && key != 'stcd'){
									address = address.concat(response.data['pradr']['addr'][key]+",");
								}
								if(key == 'pncd'){
									$('#supplierpincode').val(response.data['pradr']['addr'][key]);
								}
								if(key == 'stcd'){
									$('#suppliercity').val(response.data['pradr']['addr'][key]);
								}
							}
						});
					}
					});
					$('#supplieraddresss').val(address.slice(0,-1));
					}
				}
				$(btn).removeClass('btn-loader');
			},
			error : function(e, status, error) {
				$(btn).removeClass('btn-loader');
			}
		});
	}
}

$('#supplierCustomerId').change(function() {
	var supplierid=$('#supplierCustomerId').val();
	var clientid='<c:out value="${client.id}"/>';
	$.ajax({
		type : "GET",
		contentType : "application/json",
		url: _getContextPath()+"/suppliercustomeridexits/"+clientid+"/"+supplierid,
		success : function(response) {
			if(response == 'success'){
				$('#supplierCustomerId_Msg').text('supplierid already exists');
				$('.suppliersid').addClass('has-error has-danger');
			}else{
				$('#supplierCustomerId_Msg').text('');
				$('.suppliersid').removeClass('has-error has-danger');
			}
		}
	});
});

function labelsupppliersubmit(){
	$('#suppliers_form').submit();
}
$('#supplierimportModal').submit(function(e) {
	  var err = 0;
	    if (!$('#messages7').html()) {    	
	      err = 1;
	    }
		  if (err != 0) {
		   $('.errormsg').css('display','block');
	    return false;
	  }
	});
function choosesupplierfileSheets(){
	$('#supplierFile')[0].click()
}

function updateSupplierSelection(id,chkBox){
	if(chkBox.checked) {
		supplierArray.push(id);
	} else {
		var sArray=new Array();
		supplierArray.forEach(function(supplier) {
			if(supplier != id) {
				sArray.push(supplier);
			}
		});
		supplierArray = cArray;
	}
	if(supplierArray.length > 0){
		$('#supplierDelete').removeClass("disabled");
	}else{
		$('#supplierDelete').addClass("disabled");
	}
}
function updateSuppliersMainSelection(chkBox) {
	supplierArray = new Array();
	var check = $('#supplierCheck').prop("checked");
    var rows = supTable.rows().nodes();
    if(check) {
    	supTable.rows().every(function () {
	    	var row = this.data();
	    	supplierArray.push(row.userid);
	   });
    }
    if(supplierArray.length > 0){
		$('#supplierDelete').removeClass("disabled");
	}else{
		$('#supplierDelete').addClass("disabled");
	}
    $('input[type="checkbox"]', rows).prop('checked', check);
}
function showSuppliersDeleteAllPopUp(){
	$('#deleteModal').modal('show');
	$('#delPopupDetails').html(name);
	$('#btnDelete').attr('onclick', "deleteSelectedSupplier()");
}
function deleteSelectedSupplier(){
	if(supplierArray.length > 0) {
		for(var i=0;i<supplierArray.length;i++) {
			deleteSelectSuppliers(supplierArray[i]);
		}
	} else {
		deleteSelectSuppliers(new Array());
	}
}
function deleteSelectSuppliers(supArray){
	$.ajax({
			url: _getContextPath()+"/delsupplier/"+supArray,
			success : function(response) {
				supTable.row( $('#row'+supArray) ).remove().draw();
				location.reload(true);
			}
		});
}

function showDeletePopup(supplierId, name) {
	$('#deleteModal').modal('show');
	$('#delPopupDetails').html(name);
	$('#btnDelete').attr('onclick', "deleteProduct('"+supplierId+"')");
}

function deleteProduct(supplierId) {
	$.ajax({
		url: _getContextPath()+"/delsupplier/"+supplierId,
		success : function(response) {
			supTable.row( $('#row'+supplierId) ).remove().draw();
			location.reload(true);
		}
	});
}
