function mannualMatchingInv(clientid,monthlyOryearly){
		var returnType;
		var invoiceid;
		if(selMatchArray[0].purchaseId){
			returnType = 'Purchase Register';
			invoiceid = selMatchArray[0].purchaseId;
			$('#invhdr').html('Invoice In Purchases');
			$('#dinvhdr').html('Invoice In GSTR2A');
		}else{
			returnType = 'GSTR2';
			invoiceid = selMatchArray[0].gstrId;
			$('#invhdr').html('Invoice In GSTR2A');
			$('#dinvhdr').html('Invoice In Purchases');
		}
		
		$.ajax({
			url: contextPath+"/mannualMatchingInvoice/"+invoiceid+"/"+returnType,
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response){
				JSON.stringify(response);
				var invoice = updateInvoiceDetails(response);
				var maincontent = '<tr><td class="text-left">'+invoice.invoicetype+'</td><td class="text-left">'+invoice.billedtoname+'</td><td class="text-left" id="manInvoiceNo">'+invoice.invoiceno+'</td><td class="text-left" id="manInvoiceDate">'+formatDate(invoice.dateofinvoice)+'</td><td class="text-left" id="manGSTIN">'+invoice.b2b[0].ctin+'</td><td class="text-right">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</td><td class="text-right">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</td></tr>';
					
					$('#mainMatchInvoices').html(maincontent);
			},error:function(err){
				
			}
		});
		var fp = $('.yearlyoption').text();var fpsplit = fp.split(' - ');var yrs = parseInt(fpsplit[0]);var yrs1 = parseInt(fpsplit[0])+1;
		var yer = year;
		if(monthlyOryearly == 'yearly'){
			yer = yrs;
		}else{
			yer = year;
		}
		
		
		var pUrl = contextPath+"/getInvoicesForMannualMatch/"+clientid+"/"+invoiceid+"/"+returnType+"/"+month+"/"+yer+"/"+monthlyOryearly;
		mannualMatchtable = $('#mannualMatch_table5').DataTable({
			
			"dom": '<"toolbar"f>lrtip<"clear">',
			 "processing": true,
			 "serverSide": true,
		     "ajax": {
		         url: pUrl,
		         type: 'GET',
		         contentType: 'application/json; charset=utf-8',
		         dataType: "json",
		         'dataSrc': function(resp){
		        	 return resp.invoices.content;
		         }
		     },
			"paging": true,
			'pageLength':10,
			"responsive": true,
			"orderClasses": false,
			"searching": true,
			"order": [[6,'desc']],
			'columns': getInvManualMatchColumns(),
			'columnDefs' : getManualMatchInvColumnDefs(),
		});
		
		
		
		
	}
	
	function getInvManualMatchColumns(){
		var chkBx = {data: function ( data, type, row ) {
	      	 			return '<div class="checkbox nottoedit" index="'+data.userid+'"><label><input type="checkbox" id="invManFilter'+data.userid+'" onClick="updateMannulaMatchSelection(\''+data.userid+'\', null,this)"/><i class="helper"></i></label></div>';
	    			}};
		
		var itype = {data: function ( data, type, row ) {
				     	return '<span class="text-left invoiceclk ">'+data.invtype+'</span>';
				    }};
		var invsNo = {data:  function ( data, type, row ) {
						var invoiceno = '';
						if(data.invoiceno != undefined){
							invoiceno = data.invoiceno;
						}
				      	 return '<span class="text-left invoiceclk ">'+invoiceno+'</span>';
				    }};
		var billtoname = {data: function ( data, type, row ) {
							var billedtoname = '';
							if(data.billedtoname != undefined){
								billedtoname = data.billedtoname;
							}
					      	 return '<span class="text-left invoiceclk ">'+billedtoname+'</span>';
					    }};
		var billtogtnn = {data: function ( data, type, row ) {
						var invtype = data.invtype;
						var gstnnum= '';
						var cfs = '';
						if(data.b2b && data.b2b.length > 0){
							if(data.b2b[0].ctin != undefined){
								gstnnum = data.b2b[0].ctin;
							}
						}
						
						var cfs = '';
			        	if(invtype == 'B2B'){
			        		if(data.b2b && data.b2b.length > 0){
								if(data.b2b[0].cfs != undefined){
									cfs = data.b2b[0].cfs;
								}
							}
			        	}else if(invtype == 'Credit/Debit Notes'){
			        		if(data.cdn && data.cdn.length > 0){
								if(data.cdn[0].cfs != undefined){
									cfs = data.cdn[0].cfs;
								}
							}
			        	}
				    }};
		
		var invDate = {data: function ( data, type, row ) {
						var dateofinvoice = "";
							if(data.dateofinvoice != null){
								var invDate = new Date(data.dateofinvoice);
								var day = invDate.getDate() + "";var month = (invDate.getMonth() + 1) + "";	var year = invDate.getFullYear() + "";
								day = checkZero(day);month = checkZero(month);year = checkZero(year);
								dateofinvoice = day + "/" + month + "/" + year;
							}else{dateofinvoice = "";}
					      	 return '<span class="text-left invoiceclk ">'+(new Date(data.dateofinvoice)).toLocaleDateString("en-GB")+'</span>';
					    }};
		var taxblamt = {data: function ( data, type, row ) {
						var totalTaxableAmt = 0.0;
						if(data.totaltaxableamount){
							totalTaxableAmt = data.totaltaxableamount;
						}
					   	 return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(totalTaxableAmt.toFixed(2))+'</span>';
					    }};
		var totlTax = {data: function ( data, type, row ) {
						var totalTax = 0.0;
						if(data.totaltax){
							totalTax = data.totaltax;
						}
					   	 return '<span id="tot_tax" class="ind_formats text-right invoiceclk ">'+formatNumber(totalTax.toFixed(2))+'<div id="tax_tot_drop1" style="display:none"><div id="drop-tottax"></div><h6 style="text-align: center;" class="mb-2 tax_text">TAX AMOUNT</h6><div class="row pl-3" style="height:25px"><p class="mr-3">IGST <span style="margin-left:5px">:<span></p><span><label class="dropdown taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalIgstAmount? data.totalIgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">CGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalCgstAmount ? data.totalCgstAmount.toFixed(2) : 0)+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">SGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(data.totalSgstAmount ? data.totalSgstAmount.toFixed(2) : 0)+'</label></span></div></div></span>';
					    }};
		return [chkBx , itype, billtoname,invsNo, invDate, billtogtnn,taxblamt,  totlTax];
	}


	function getManualMatchInvColumnDefs(){
		return  [
			{
				"targets": 0,
				"orderable": false
			}
			
		];
	}
	
	