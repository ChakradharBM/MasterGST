	var stockSummaryTable = new Object();
	var stockLedgerTable = new Object();
	var stockDetailTable = new Object();
	var stockSummaryTable = new Object();
	var customerWiseTable = new Object();
	var lowStockTable = new Object();
	var ageTable = new Object();
	$(function() {
		$('.itemdrop_search').select2({placeholder: "Please select Item",allowClear: true});
	});
	function getval(sel) {
		document.getElementById('filing_option').innerHTML = sel;
		$('#processing').css('top','10%');
		if (sel == 'Custom') {
			$('.monthely-sp').css("display", "none");$('.yearly-sp').css("display", "none");$('.custom-sp').css("display", "inline-block");$('.dropdown-menu.ret-type-trail').css("left", "16%");
		} else if (sel == 'Yearly') {
			$('.monthely-sp').css("display", "none");$('.yearly-sp').css("display", "inline-block");$('.custom-sp').css("display", "none");$('.dropdown-menu.ret-type-trail').css("left", "19%");
		} else {
			$('.monthely-sp').css("display", "inline-block");$('.yearly-sp').css("display", "none");$('.custom-sp').css("display", "none");$('.dropdown-menu.ret-type-trail').css("left", "19%");
		}
	};
	function updateYearlyOption(value){
		document.getElementById('yearlyoption').innerHTML=value;
	}
	function loadStockSummaryTable(id, clientId, month, year, userType, fullname,tablename){
		var cUrl = _getContextPath()+'/getItems/'+id+'/'+fullname+'/'+userType+'/'+clientId+'/'+month+'/'+year+'?reportType=reports';
		stockSummaryTable = $('#reports_stock'+tablename).DataTable({
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
				'columns': getStockColumns(id, clientId, userType, month, year,tablename),
				'columnDefs' : getStockColumnDefs(tablename)
	});
}
		
		function getStockColumns(id, clientId, userType, month, year,tablename){
			var itemNo = {data:  function ( data, type, row ) {
				var itemno = data.itemno ? data.itemno : "";
				return '<span class="text-left invoiceclk">'+itemno+'</span>';
				}};
			
			var itemdescription = {data:  function ( data, type, row ) {
				var description = data.description ? data.description : "";
				return '<span class="text-left invoiceclk">'+description+'</span>';
				}};
			var currentStock = {data:  function ( data, type, row ) {
				var currentstock = data.currentStock ? data.currentStock : "";
				var unit = data.unit ? data.unit : "";
				return '<span class="text-left invoiceclk">'+currentstock+' '+unit+'</span>';
				}};
			var sellingPrice = {data:  function ( data, type, row ) {
				var saleprice = data.salePrice ? data.salePrice : 0.00;
				return '<span class="text-left invoiceclk">&#8377;'+formatNumber(saleprice.toFixed(2))+'</span>';
				}};
			var purchasePrice = {data:  function ( data, type, row ) {
				var purchaseprice = data.purchasePrice ? data.purchasePrice : 0.00;
				return '<span class="text-left invoiceclk">&#8377;'+formatNumber(purchaseprice.toFixed(2))+'</span>';
				}};
			var mrpPrice = {data:  function ( data, type, row ) {
				var mrpprice = data.mrpPrice ? data.mrpPrice : 0.00;
				return '<span class="text-left invoiceclk">&#8377;'+formatNumber(mrpprice.toFixed(2))+'</span>';
				}};
			var stockVal = {data:  function ( data, type, row ) {
				var sqty = data.currentStock ? data.currentStock : 0.00;
				var purchaseprice = data.purchasePrice ? data.purchasePrice : 0.00;
				var val = sqty*purchaseprice;
				return '<span class="text-left invoiceclk">&#8377;'+formatNumber(val.toFixed(2))+'</span>';
				}};
			if(tablename == 'Summary'){
				return [itemNo,itemdescription, sellingPrice, purchasePrice, currentStock, stockVal];
			}else if(tablename == 'Rate'){
				return [itemNo,itemdescription, mrpPrice, sellingPrice];
			}
		}
function getStockColumnDefs(tablename) {
	if(tablename == 'Summary'){
		return [{
			"targets" : [ 2, 3, 4, 5 ],
			className : 'dt-body-right'
		}];
	}else if(tablename == 'Rate'){
		return [{
			"targets" : [ 2, 3 ],
			className : 'dt-body-right'
		}];

	}
}
function stockSummaryExcelDownloads(id, clientId, month, year, userType, fullname, type, tablename, itemname){
	var dwnldurl = '';
	if(tablename == "Summary" || tablename == "Rate" || tablename == "Sales"){
		dwnldurl = _getContextPath()+"/dwnldStockSummaryXls/"+id+"/"+clientId+"/"+month+"/"+year+"?reportType="+tablename;
		$(".reportsdbTableStock"+tablename+" div.toolbar").append('<a href="'+dwnldurl+'" class="btn btn-blue excel mr-3" onClick="excelreport()">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
	}else if(tablename == "Detail"){
		if(type == 'Monthly'){
			dwnldurl = _getContextPath()+"/dwnldStockDetailXls/"+id+"/"+clientId+"/"+month+"/"+year+"?itemname="+itemname;	
		}else if(type == 'Yearly'){
			var yr=parseInt(year);
			dwnldurl = _getContextPath()+"/dwnldStockDetailXls/"+id+"/"+clientId+"/"+month+"/"+yr+"?itemname="+itemname;	
		}else{
			dwnldurl = _getContextPath()+"/dwnldCustomStockDetailXls/"+id+"/"+clientId+"/"+month+"/"+year+"?itemname="+itemname;
		}
		$(".reportsdbTableStock"+tablename+" div.toolbar").append('<a href="'+dwnldurl+'" class="btn btn-blue excel mr-3" onClick="excelreport()">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
	}else if(tablename == "Ledger"){
		if(type == 'Monthly'){
			dwnldurl = _getContextPath()+"/dwnldStockLedgerXls/"+id+"/"+clientId+"/"+month+"/"+year+"?itemname="+itemname;	
		}else if(type == 'Yearly'){
			var yr=parseInt(year);
			dwnldurl = _getContextPath()+"/dwnldStockLedgerXls/"+id+"/"+clientId+"/"+month+"/"+yr+"?itemname="+itemname;	
		}else{
			dwnldurl = _getContextPath()+"/dwnldCustomStockLedgerXls/"+id+"/"+clientId+"/"+month+"/"+year+"?itemname="+itemname;
		}
		$('.ledger_dwld').attr('href',dwnldurl);
	}
	
}
function loadStockLedgerTable(id, clientId, month, year, userType, fullname, type, itemname){
	var sUrl = _getContextPath()+'/getStockLedgers/'+id+'/'+fullname+'/'+userType+'/'+clientId+'/'+month+'/'+year+"?itemname="+itemname;
	if(type == 'custom'){
		sUrl = _getContextPath()+'/getCustomStockLedgers/'+id+'/'+fullname+'/'+userType+'/'+clientId+'/'+month+'/'+year+"?itemname="+itemname;
	}
	$('#reports_stockLedger #stockLedger_body').empty();
	var content='';
	$.ajax({
		url: sUrl,
		async: true,
		cache: false,
		dataType:"json",
		beforeSend: function () {$('#processing').text('Processing...');},
		contentType: 'application/json',
		success : function(data) {
			if(data.content == ""){
				content+='<tr><td colspan="7" style="text-align: center;height: 25px;vertical-align: inherit;border-right: 1px solid lightgray !important;">No Records Found</td></tr>';
			}else{
				for(var i=0;i<data.content.length;i++){
					var stock = data.content[i];
						content += '<tr data-node-id="'+i+'" style="background-color: #c7beb0;">';
						content += '<td>'+stock.itemno+'</td><td></td><td></td><td align="right" class="totInVal">'+stock.totInQty+'</td><td align="right" class="totOutVal">'+stock.totOutQty+'</td><td></td>';
						content+='<td align="right" class="totBalVal">'+stock.totBalQty+'</td></tr>';
						for(var j=0;j<stock.stocks.length;j++){
							var datanodeid=i+'.'+j;
							var datanodepid=i;
							content += '<tr data-node-id="'+datanodeid+'" data-node-pid="'+datanodepid+'">';
							content += '<td></td>';
							content += '<td align="center">'+(new Date(stock.stocks[j].dateOfMovement)).toLocaleDateString("en-GB")+'</td>';
							if(stock.stocks[j].stockPoNo != "" && stock.stocks[j].stockPoNo != null){
								content += '<td>'+stock.stocks[j].stockPoNo+'</td>';
							}else{
								content += '<td></td>';
							}
							if(stock.stocks[j].stockMovement == "In"){
								content += '<td align="right">'+stock.stocks[j].stockItemQty+'</td><td></td>';
							}else if(stock.stocks[j].stockMovement == "Out"){
								content += '<td></td><td align="right">'+stock.stocks[j].stockItemQty+'</td>';
							}
							content+='<td align="right">&#8377; '+formatNumber(stock.stocks[j].stockPurchaseCost.toFixed(2))+'</td>';
							content+='<td align="right">'+stock.stocks[j].currentStock+'</td>';
							content += '</tr>';
						}
				}
			}
			$('#reports_stockLedger #stockLedger_body').append(content);
			$('#reports_stockLedger').simpleTreeTable({margin: '20'});
		},error:function(err){
			$('#reports_stockLedger #stockLedger_body tr').detach();
			$('#reports_stockLedger #stockLedger_body').append('<tr><td colspan="7" style="text-align: center;height: 25px;vertical-align: inherit;border-right: 1px solid lightgray !important;">something went wrong</td></tr>');
		}
	});
}

function loadStockDetailTable(id, clientId, month, year, userType, fullname,type, itemName){
	var sUrl = _getContextPath()+'/getStockDetails/'+id+'/'+fullname+'/'+userType+'/'+clientId+'/'+month+'/'+year+"?itemname="+itemName;
	if(type == "custom"){
		sUrl = _getContextPath()+'/getCustomStockDetails/'+id+'/'+fullname+'/'+userType+'/'+clientId+'/'+month+'/'+year+"?itemname="+itemName;
	}
	stockDetailTable = $('#reports_stockDetail').DataTable({
		"dom": '<"toolbar"f>lrtip<"clear">',
		 destroy: true,
		 "processing": true,
		 "serverSide": true,
		 "lengthMenu": [ [10, 25, 50, 100, -1], [10, 25, 50, 100, "All"] ],
	     "ajax": {
	         url: sUrl,
	         type: 'GET',
	         contentType: 'application/json',
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
			"order": [[0,'desc']],
			'columns': getStockDetailColumns(id, clientId, userType, month, year),
			'columnDefs' : getStockDetailColumnDefs()
	});
}

function getStockDetailColumns(id, clientId, userType, month, year){
	var date = {data:  function ( data, type, row ) {
		var dateOfMovement = data.dateOfMovement ? data.dateOfMovement : "";
		return '<span class="text-left invoiceclk">'+(new Date(data.dateOfMovement)).toLocaleDateString("en-GB")+'</span>';
		}};
	var stockItemno = {data:  function ( data, type, row ) {
		var stockItemNo = data.stockItemNo ? data.stockItemNo : "";
		return '<span class="text-left invoiceclk">'+stockItemNo+'</span>';
		}};
	var transactiontype = {data:  function ( data, type, row ) {
		var transactionType = data.transactionType ? data.transactionType : "";
		return '<span class="text-left invoiceclk">'+transactionType+'</span>';
		}};
	var quantity = {data:  function ( data, type, row ) {
		var stockMovement = data.stockMovement ? data.stockMovement : "";
		var stockItemQty = data.stockItemQty ? data.stockItemQty : "";
		var unit = data.stockUnit ? data.stockUnit : "";
		if(stockMovement == "Out"){
			return '<span class="text-left invoiceclk">-'+stockItemQty+' '+unit+'</span>';
		}else{
			return '<span class="text-left invoiceclk">'+stockItemQty+' '+unit+'</span>';
		}
		}};
	var currentStock = {data:  function ( data, type, row ) {
		var currentstock = data.currentStock ? data.currentStock : "";
		var unit = data.stockUnit ? data.stockUnit : "";
		return '<span class="text-left invoiceclk">'+currentstock+' '+unit+'</span>';
		}};
	return [date,stockItemno,transactiontype, quantity, currentStock];
}
function getStockDetailColumnDefs() {
	return [{
		"targets" : [ 3, 4],
		className : 'dt-body-right'
	}];
}
function loadStockSalesSummaryTable(id, clientId, month, year, userType, fullname){
	var sUrl = _getContextPath()+'/getStockSummaryDetails/'+id+'/'+fullname+'/'+userType+'/'+clientId+'/'+month+'/'+year;
	
	stockSummaryTable = $('#reports_stockSales').DataTable({
		"dom": '<"toolbar"f>lrtip<"clear">',
		 destroy: true,
		 "processing": true,
		 "serverSide": true,
		 "lengthMenu": [ [10, 25, 50, 100, -1], [10, 25, 50, 100, "All"] ],
	     "ajax": {
	         url: sUrl,
	         type: 'GET',
	         contentType: 'application/json',
	         dataType: "json",
	         'dataSrc': function(resp){
	        	 resp.recordsTotal = resp.items.totalElements;
	        	 resp.recordsFiltered = resp.items.totalElements;
	        	 loadQuantityTotals(resp.totalQtyAmts);
	        	 return resp.items.content;
	         }
	     },
	     	"paging": true,
			'pageLength':10,
			"responsive": true,
			"orderClasses": false,
			"searching": true,
			"order": [[0,'desc']],
			'columns': getStockSalesColumns(id, clientId, userType, month, year)
	});
}
function getStockSalesColumns(id, clientId, userType, month, year){
	var itemQty="";
	var itemdescription = {data:  function ( data, type, row ) {
		var description = data.description ? data.description : "";
		return '<span class="text-left invoiceclk">'+description+'</span>';
		}};
	var uqc = {data:  function ( data, type, row ) {
		var qty = data.totalQtyUsage ? data.totalQtyUsage : 0.00;
		itemQty = qty;
		var unit = data.unit ? data.unit : "";
		return '<span class="text-left invoiceclk">'+qty+' '+unit+'</span>';
		}};
		return [itemdescription,uqc];
	
}
function loadQuantityTotals(totals){
	$('#total_stock_qty').text(totals.stockItemQty ? formatNumber(totals.stockItemQty.toFixed(2)) : '0.00');
}
function loadPartyWiseTable(id, clientId, month, year, userType, fullname,type, custName){
	var iUrl = _getContextPath()+'/getPartyWiseDetails/'+id+'/'+fullname+'/'+userType+'/'+clientId+'/'+month+'/'+year+"?custName="+custName;
	customerWiseTable = $('#reports_stockItm').DataTable({
		"dom": '<"toolbar"f>lrtip<"clear">',
		 destroy: true,
		 "processing": true,
		 "serverSide": true,
		 "lengthMenu": [ [10, 25, 50, 100, -1], [10, 25, 50, 100, "All"] ],
	     "ajax": {
	         url: iUrl,
	         type: 'GET',
	         contentType: 'application/json',
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
			"order": [[0,'desc']]
			//'columns': getStockSalesColumns(id, clientId, userType, month, year)
	});
	
}

function loadLowStockTable(id, clientId, month, year, userType, fullname){
	var iUrl = _getContextPath()+'/getLowStockDetails/'+id+'/'+fullname+'/'+userType+'/'+clientId+'/'+month+'/'+year;
	lowStockTable = $('#reports_lowStock').DataTable({
		"dom": '<"toolbar"f>lrtip<"clear">',
		 destroy: true,
		 "processing": true,
		 "serverSide": true,
		 "lengthMenu": [ [10, 25, 50, 100, -1], [10, 25, 50, 100, "All"] ],
	     "ajax": {
	         url: iUrl,
	         type: 'GET',
	         contentType: 'application/json',
	         dataType: "json",
	         'dataSrc': function(resp){
	        	 resp.recordsTotal = resp.items.totalElements;
	        	 resp.recordsFiltered = resp.items.totalElements;
	        	 //loadQuantityTotals(resp.totalQtyAmts);
	        	 return resp.items.content;
	         }
	     },
	     	"paging": true,
			'pageLength':10,
			"responsive": true,
			"orderClasses": false,
			"searching": true,
			"order": [[0,'desc']],
			'columns': getLowStockColumns(id, clientId, userType, month, year)
	});
}
function getLowStockColumns(id, clientId, userType, month, year){
	var itemdescription = {data:  function ( data, type, row ) {
		var description = data.description ? data.description : "";
		return '<span class="text-left invoiceclk">'+description+'</span>';
		}};
	var itemcode = {data:  function ( data, type, row ) {
		var itemno = data.itemno ? data.itemno : "";
		return '<span class="text-left invoiceclk">'+itemno+'</span>';
		}};
	var uqc = {data:  function ( data, type, row ) {
		var qty = data.totalQtyUsage ? data.totalQtyUsage : 0.00;
		itemQty = qty;
		var unit = data.unit ? data.unit : "";
		return '<span class="text-left invoiceclk">'+qty+' '+unit+'</span>';
		}};
	var stocklevel = {data:  function ( data, type, row ) {
		var stocklevel = data.stocklevel ? data.stocklevel : "";
		return '<span class="text-left invoiceclk">'+stocklevel+'</span>';
		}};
	var stockVal = {data:  function ( data, type, row ) {
		var sqty = data.currentStock ? data.currentStock : 0.00;
		var purchaseprice = data.purchasePrice ? data.purchasePrice : 0.00;
		var val = sqty*purchaseprice;
		return '<span class="text-left invoiceclk">&#8377;'+formatNumber(val.toFixed(2))+'</span>';
		}};
		return [itemdescription,itemcode,uqc,stocklevel,stockVal];
}

function loadAgingReportTable(id, clientId, month, year, userType, fullname, type, intervalValue){
	var aUrl = _getContextPath()+'/getStockAgingDetails/'+id+'/'+fullname+'/'+userType+'/'+clientId+'/'+month+'/'+year+'?intervalValue='+intervalValue;
	
	ageTable = $('#reports_age').DataTable({
		"dom": '<"toolbar"f>lrtip<"clear">',
		 destroy: true,
		 "processing": true,
		 "serverSide": true,
		 "lengthMenu": [ [10, 25, 50, 100, -1], [10, 25, 50, 100, "All"] ],
	     "ajax": {
	         url: aUrl,
	         type: 'GET',
	         contentType: 'application/json',
	         dataType: "json",
	         'dataSrc': function(resp){
	        	 resp.recordsTotal = resp.stocks.totalElements;
	        	 resp.recordsFiltered = resp.stocks.totalElements;
	        	 return resp.stocks.content;
	         }
	     },
	     	"paging": true,
			'pageLength':10,
			"responsive": true,
			"orderClasses": false,
			"searching": true,
			"order": [[0,'desc']],
			'columns': getStockAgingColumns(id, clientId, userType, month, year, intervalValue),
			'columnDefs' : getStockAgingColumnDefs(intervalValue)
	});
	
}
function getStockAgingColumns(id, clientId, userType, month, year, intervalValue){
	var itemdescription = {data:  function ( data, type, row ) {
		var item = data.item ? data.item : "";
		return '<span class="text-left invoiceclk">'+item+'</span>';
		}};
	var stockunit = {data:  function ( data, type, row ) {
		var stockUnit = data.uqc ? data.uqc : "";
		return '<span class="text-left invoiceclk">'+stockUnit+'</span>';
		}};
	
	var currentstock = {data:  function ( data, type, row ) {
		var currentStock = data.currentStock ? data.currentStock : 0.00;
		return '<span class="text-left invoiceclk">'+currentStock+'</span>';
		}};
	var days0 = {data:  function ( data, type, row ) {
		var days = data.days0 ? data.days0 : 0.00;
		return '<span class="text-left invoiceclk">'+days+'</span>';
		}};
	var days30 = {data:  function ( data, type, row ) {
		var days = data.days30 ? data.days30 : 0.00;
		return '<span class="text-left invoiceclk">'+days+'</span>';
		}};
	var days60 = {data:  function ( data, type, row ) {
		var days = data.days60 ? data.days60 : 0.00;
		return '<span class="text-left invoiceclk">'+days+'</span>';
		}};
	var days90 = {data:  function ( data, type, row ) {
		var days = data.days90 ? data.days90 : 0.00;
		return '<span class="text-left invoiceclk">'+days+'</span>';
		}};
	var days120 = {data:  function ( data, type, row ) {
		var days = data.days120 ? data.days120 : 0.00;
		return '<span class="text-left invoiceclk">'+days+'</span>';
		}};
	var days150 = {data:  function ( data, type, row ) {
		var days = data.days150 ? data.days150 : 0.00;
		return '<span class="text-left invoiceclk">'+days+'</span>';
		}};
	var days180 = {data:  function ( data, type, row ) {
		var days = data.days180 ? data.days180 : 0.00;
		return '<span class="text-left invoiceclk">'+days+'</span>';
		}};
	if(intervalValue == "30"){
		return [itemdescription, stockunit, currentstock, days0, days30, days60, days90, days120, days150, days180];
	}else if(intervalValue == "90"){
		return [itemdescription, stockunit, currentstock, days0, days90, days180];
	}else if(intervalValue == "180"){
		return [itemdescription, stockunit, currentstock, days0, days180];
	}
}
function getStockAgingColumnDefs(intervalValue) {
	if(intervalValue == "30"){
		return [{
			"targets" : [ 2, 3, 4, 5, 6, 7, 8, 9],
			className : 'dt-body-right'
		}];
	}else if(intervalValue == "90"){
		return [{
			"targets" : [ 3, 4, 5 ],
			className : 'dt-body-right'
		}];
	}else if(intervalValue == "180"){
		return [{
			"targets" : [ 3, 4 ],
			className : 'dt-body-right'
		}];
	}
}