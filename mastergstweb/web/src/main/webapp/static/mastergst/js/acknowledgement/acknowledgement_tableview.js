var invoiceArray = new Object();
var acknlTable = new Object();
var acknlTableUrl = new Object();

function loadAcknowledgementTable(id, clientid, month, year, varRetType, pendingOrUpload, type){
	var contextPath=_getContextPath();
	
	var pUrl =contextPath+'/getpendingacknldgeinvs/'+id+'/'+clientid+'/'+varRetType+"/"+month+'/'+year+'?pendingOrUpload='+pendingOrUpload;		
	if(acknlTable[type]){
		acknlTable[type].clear();
		acknlTable[type].destroy();
	}
	acknlTableUrl[type] = pUrl;
	acknlTable[type] = $('#dbTableAcknowlegement').DataTable({
		"dom": '<"toolbar"f>lrtip<"clear">',
		 "processing": true,
		 "serverSide": true,
	     "ajax": {
	         url: pUrl,
	         type: 'GET',
	         contentType: 'application/json; charset=utf-8',
	         dataType: "json",
	         'dataSrc': function(resp){
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
		"order": [[1,'desc']],
		'columns': getInvColumns(id, clientid, pendingOrUpload, '',type),
		'columnDefs': [
			{
				"targets":  [1,2, 5, 6],
				className: 'dt-body-right'
			}
		],
		"createdRow": function( row, data, dataIndex ) {
		    if ( data.gstStatus == "CANCELLED" ) {
		      $(row).addClass( 'cancelled_line' );
		    }
		}
	});
	setTimeout(function(){
		filedrga(id, clientid, contextPath);
	},1000);
}

function loadAcknowledgementCustomTable(id, clientid, pfrom_value, cfrom_value, varRetType, pendingOrUpload, type){
	var contextPath=_getContextPath();
	
	var pUrl =contextPath+'/getpendingcustomacknldgeinvs/'+id+'/'+clientid+'/'+varRetType+"/"+pfrom_value+'/'+cfrom_value+'?pendingOrUpload='+pendingOrUpload;		
	if(acknlTable[type]){
		acknlTable[type].clear();
		acknlTable[type].destroy();
	}
	acknlTableUrl[type] = pUrl;
	acknlTable[type] = $('#dbTableAcknowlegement').DataTable({
		"dom": '<"toolbar"f>lrtip<"clear">',
		 "processing": true,
		 "serverSide": true,
	     "ajax": {
	         url: pUrl,
	         type: 'GET',
	         contentType: 'application/json; charset=utf-8',
	         dataType: "json",
	         'dataSrc': function(resp){
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
		"order": [[1,'desc']],
		'columns': getInvColumns(id, clientid, pendingOrUpload, '',type),
		'columnDefs': [
			{
				"targets":  [1,2, 5, 6],
				className: 'dt-body-right'
			}
		],
		"createdRow": function( row, data, dataIndex ) {
		    if ( data.gstStatus == "CANCELLED" ) {
		      $(row).addClass( 'cancelled_line' );
		    }
		}
	});
	setTimeout(function(){
		filedrga(id, clientid, contextPath);
	},1000);
}

function getInvColumns(id, clientid, varRetType, userType, month, year, booksOrReturns, urlSuffix, type){
	var varRetTypeCode = varRetType.replace(" ", "_");
	var invsNo = {data:  function ( data, type, row ) {
					var invoiceno = '';
					if(data.invoiceno != undefined){
						invoiceno = data.invoiceno;
					}
			      	 return '<span class="text-left invoiceclk ">'+invoiceno+'</span>';
			    }};
	var invDate = {data: function ( data, type, row ) {
		var dateofinvoice = "";
		
		return '<span class="invoiceclk ">'+(new Date(data.dateofinvoice)).toLocaleDateString("en-GB")+'</span>';
	}};
	var taxblamt = {data: function ( data, type, row ) {
		var totalTaxableAmt = 0.0;
		if(data.totaltaxableamount){
			totalTaxableAmt = data.totaltaxableamount;
		}
	   	 return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(totalTaxableAmt.toFixed(2))+'</span>';
	    }};
	var totalamt = {data: function ( data, type, row ) {
		var totalAmount = 0.0;
		if(data.totalamount){
			totalAmount = data.totalamount;
		}
	   	 return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(totalAmount.toFixed(2))+'</span>';
    }};
	var billtoname = {data: function ( data, type, row ) {
						var billedtoname = '';
						if(data.billedtoname != undefined){
							billedtoname = data.billedtoname;
						}
				      	 return '<span class="text-left invoiceclk ">'+billedtoname+'</span>';
				    }};
	var customerid = {data: function ( data, type, row ) {
		var custmerid = "";
		if(data.invoiceCustomerId){
			custmerid = data.invoiceCustomerId;
		}
	   	 return '<span class="ind_formats text-right invoiceclk ">'+custmerid+'</span>';
	}};
	var attachment = {data: function ( data, type, row ) {
		return '<fieldset style="display:inline-block"><input type="hidden" id="MAX_FILE_SIZE" name="MAX_FILE_SIZE" value="300000" /><div><label for="fileselect" class="f-12 btn btn-blue-dark" style="padding: 4px 10px 4px 10px;font-size:11px">Attach Files</label><input type="file" class="fileselect" id="fileselect" name="fileselect[]" multiple="multiple" /></div></fieldset>';
	}};
	return [invsNo, invDate, taxblamt, totalamt, billtoname, customerid, attachment];
}

function formatNumber(nStr) {
	var negativenumber = false;
	if(nStr && nStr.includes("-")){
		negativenumber = true;
		nStr = nStr.replace("-","");
	}
	nStr=nStr.toString();var afterPoint = '';
	if(nStr.indexOf('.') > 0)
	   afterPoint = nStr.substring(nStr.indexOf('.'),nStr.length);
	nStr = Math.floor(nStr);
	nStr=nStr.toString();
	var lastThree = nStr.substring(nStr.length-3);
	var otherNumbers = nStr.substring(0,nStr.length-3);
	if(otherNumbers != '')
	    lastThree = ',' + lastThree;
	var res = otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ",") + lastThree + afterPoint;
	if(negativenumber){
		res = "-"+res;
	}
	return res;
}
function checkZero(data){
	if(data.length == 1){
		data = "0" + data;
	}
	return data;
}
