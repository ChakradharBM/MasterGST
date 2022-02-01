var itemTable = new Object();
$(document).ready(function() {
	$('.addExempted').click(function(){
		if($(".addExempted").is(':checked')){
			$('.addExempted').val("true");
		  $(".exemptedval").css("display","flex");
		  $('#isExempted').val('true');
	    }else{
	      $('.addExempted').val("false");
	      $(".exemptedval").hide();
	      $('#itemisExempted').val('false');
	   }
	});
	$('.addDiscount').click(function(){
		if($(".addDiscount").is(':checked')){
			  $('.addDiscount').val("true");
			  $(".discountval").css("display","flex");
			  $('#itemisDiscount').val('true');
	    }else{
	          $('.addDiscount').val("false");
	          $(".discountval").hide();
	          $('#itemisDiscount').val('false');
	   }
	});
  $.ajax({
		url: _getContextPath()+"/getCustomFields/"+clientId,
		async: false,
		cache: false,
		success : function(response) {customFieldsData = response;},
		error : function(e) {}
	});

	$('#asOndate').datetimepicker({
		timepicker: false,
		format: 'd/m/Y',
        scrollMonth: true
	});
	$('.asOndatewrap').click(function(){
		$('#asOndate').focus();
	});
	var dateOfMovement = new Date().getDate() + "/" + month + "/" + year;
	$('#dateOfMovement').datetimepicker({
		value: new Date(),
		timepicker: false,
		format: 'd/m/Y',
        scrollMonth: true
	});
	$('.dateOfMovementwrap').click(function(){
		$('#dateOfMovement').focus();
	});
	$('input[type=radio][name=stockMovement]').change(function() {
		updateStock(this.value);
	});
	$('input[type=radio][name=itemType]').change(function() {
		updateProduct(this.value);
	});
	function updateProduct(value){
		if(value == "Product"){
			$('.itemCode_txt').text("Item Code");
			$('.itemName_txt').text("Item Name");
			$('.item_grpCode_txt').text("Item Group Code");
			$('.item_grpName_txt').text("Item Group Name");
			$('.item_description_txt').text("Item Description");
			$('.hsnSacTxt').text("HSN Code");
			$('.unitofMeasurement').val("NA");
			$('.itemDescription').attr("placeholder","Item Description");
			$('.itemSalesPriceTxt').text('Item Sale Price');
			$('.item_description_txt').css("margin-left","2px");
			$('.descInput').css({"padding-left":"52px"});
			$('.descInput').addClass("pr-0");
			$('.openingStockDiv,.asOnDateStockDiv,.safteyStockLevelDiv,.reOrderDiv,.ItemPurchasePriceDiv,.wholeSalePriceDiv,.mrpPriceDiv,.serviceDiv,.imagesDiv').removeClass("d-none");
			$('.imagesDiv').hide();
			var uqcoptions = {
					url: function(phrase) {
						phrase = phrase.replace('(',"\\(");
						phrase = phrase.replace(')',"\\)");
						return _getContextPath()+"/uqcconfig?query="+ phrase + "&format=json";
					},
					getValue: "name",
					list: {
						onLoadEvent: function() {
							if($("#eac-container-unitofMeasurement ul").children().length == 0) {
								$('#profileLevelItemUqc').addClass('has-error has-danger');
								$("#unitofMeasurementempty").show();
							} else {
								$("#unitofMeasurementempty").hide();
							}
						},
						maxNumberOfElements: 43
					},
				};
				$("#unitofMeasurement").easyAutocomplete(uqcoptions);
		}else{
			$('.itemCode_txt').text("Service Code");
			$('.itemName_txt').text("Service Name");
			$('.item_grpCode_txt').text("Service Group Code");
			$('.item_grpName_txt').text("Service Group Name");
			$('.item_description_txt').text("Service Description");
			$('.itemDescription').attr("placeholder","Service Description");
			$('.hsnSacTxt').text("SAC Code");
			$('.unitofMeasurement').val("NA");
			
			$('.itemSalesPriceTxt').text('Service Sale Price');
			$('.item_description_txt').css("margin-left","2px");
			$('.descInput').css({"padding-left":"34px"});
			$('.descInput').removeClass("pr-0");
			$('.openingStockDiv,.asOnDateStockDiv,.safteyStockLevelDiv,.reOrderDiv,.ItemPurchasePriceDiv,.wholeSalePriceDiv,.mrpPriceDiv,.serviceDiv,.imagesDiv').addClass("d-none");
			$('.imagesDiv').hide();
			var uqcoptions = {
					data: ["NA"]
			};
			$("#unitofMeasurement").easyAutocomplete(uqcoptions);
		}
	}
	var options = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return _getContextPath()+"/hsnsacconfig?query="+ phrase + "&format=json";
			},
			getValue: "name",
			categories: [{
				listLocation: "hsnConfig",
				header: "--- HSN ---"
			}, {
				listLocation: "sacConfig",
				header: "--- SAC ---"
			}],
			list: {
				match: {
					enabled: false
				},
				onLoadEvent: function() {
					if($("#eac-container-HSNCode ul").children().length == 0) {
						//$("#HSNCode").val('');
						$("#HSNCodeempty").show();
					} else {
						$("#HSNCodeempty").hide();
					}
				},
				maxNumberOfElements: 10
			},
		};
		$("#HSNCode").easyAutocomplete(options);
		var uqcoptions = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return _getContextPath()+"/uqcconfig?query="+ phrase + "&format=json";
			},
			getValue: "name",
			list: {
				onLoadEvent: function() {
					if($("#eac-container-unitofMeasurement ul").children().length == 0) {
						$('#profileLevelItemUqc').addClass('has-error has-danger');
						$("#unitofMeasurementempty").show();
					} else {
						$("#unitofMeasurementempty").hide();
					}
				},
				maxNumberOfElements: 43
			},
		};
		$("#unitofMeasurement").easyAutocomplete(uqcoptions);
});
function loadItemsTable(id, clientId, month, year, userType, fullname){
	var cUrl = _getContextPath()+'/getItems/'+id+'/'+fullname+'/'+userType+'/'+clientId+'/'+month+'/'+year;
	itemTable = $('#dbTable_item').DataTable({
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
	        	 $('#itemCheck').prop('checked', false);
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
			'columns': getItemColumns(id, clientId, userType, month, year),
			'columnDefs' : getItemColumnDefs()
	});
	$('#itemsBody').on('click','tr', function(e){
		if (!$(e.target).closest('.nottoedit').length) {
			var dat = itemTable.row($(this)).data();
			editItemPopup(dat.userid);
		}

});
}
function editItemPopup(itemId){
	$('#editModal').modal("show");
	getItemsData(itemId, function(item) {
		 if (item.userid == itemId) {
			$('.itemNumber').val(item.itemno ? item.itemno : "");
			$('.itemName').val(item.description ? item.description : "");
			$('.itemGroupNumber').val(item.itemgroupno ? item.itemgroupno : "");
			$('.itemGroupDescription').val(item.groupdescription ? item.groupdescription : "");
			$('.itemDescription').val(item.itemDescription ? item.itemDescription : "");
			$('.asOndate').val(item.asOnDate ? (new Date(item.asOnDate)).toLocaleDateString('en-GB') : "");
			$('#salePrice').val(item.salePrice ? item.salePrice : 0.00);
			$('#saleGstType').val(item.saleGstType);
			$('#purchasePrice').val(item.purchasePrice ? item.purchasePrice : 0.00);
			$('#purchaseGstType').val(item.purchaseGstType);
			$('#wholeSalePrice').val(item.wholeSalePrice ? item.wholeSalePrice : 0.00);
			$('#mrpPrice').val(item.mrpPrice ? item.mrpPrice : 0.00);
			
			
			$('.itemCategory').val(item.category);
			$('.unitofMeasurement').val(item.unit ? item.unit : "");
			$('.recommendedSellingPriceForB2B').val(item.sellingpriceb2b);
			/* $('.recommendedSellingPriceForB2C').val(item.sellingpriceb2c); */
			$('.Discount').val(item.discount);
			$('.HSNCode').val(item.code);
			$('.saftlyStockLevel').val(item.stocklevel ? item.stocklevel : "");
			$('.taxrate').val(item.taxrate);
			if(item.discount != null && item.discount != ""){
				$(".discountval").css("display","flex");
				$('.addDiscount').prop("checked",true);
				$('.discountvalue').val(item.discount);
			}else{
				$(".discountval").hide();
				$('.addDiscount').prop("checked",false);
			}
			if(item.exmepted != null && item.exmepted != ""){
				$(".exemptedval").css("display","flex");
				$('.addExempted').prop("checked",true);
				$('.exmeptedvalue').val(item.exmepted);
			}else{
				$(".exemptedval").hide();
				$('.addExempted').prop("checked",false);
			}
			$('#recommendedRetailPriceForB2B').val(item.retailPrice);
			$('#wholesalePriceForB2B').val(item.wholeSalePrice);
			$('#mrpPriceForB2B').val(item.mrpPrice);
			$('#openingStockLevel').val(item.openingStock);
			$('#closingStockLevel').val(item.closingStock);
			$('#itemComments').val(item.itemComments);
			if(item.exemptedType == "Percentage"){
				$('#perType').prop("checked",true);
			}else{
				$('#numType').prop("checked",true);
			}
			if(item.reOrderType == "Yes"){
				$('#reorderYes').prop("checked",true);
			}else{
				$('#reorderNo').prop("checked",true);
			}
			if(item.itemType == "Product"){
				$('#product_type').prop("checked",true);
				$('.itemCode_txt').text("Item Code");
				$('.itemName_txt').text("Item Name");
				$('.item_grpCode_txt').text("Item Group Code");
				$('.item_grpName_txt').text("Item Group Name");
				$('.item_description_txt').text("Item Description");
				$('.hsnSacTxt').text("HSN Code");
				$('.itemDescription').attr("placeholder","Item Description");
				$('.itemSalesPriceTxt').text('Item Sale Price');
				$('.item_description_txt').css("margin-left","2px");
				$('.descInput').css({"padding-left":"52px"});
				$('.descInput').addClass("pr-0");
				//$('#itemform .form-group').css("margin-top","0px");
				//$('.grpCodeDiv,.grpNameDiv,.descDiv,.uqcDiv,.itemSaleDiv,.hsnSacDiv,.gstDiv,.isexempted,.custDiv').css("margin-top","0px");
				$('.openingStockDiv,.asOnDateStockDiv,.safteyStockLevelDiv,.reOrderDiv,.ItemPurchasePriceDiv,.wholeSalePriceDiv,.mrpPriceDiv,.serviceDiv,.imagesDiv').removeClass("d-none");
				$('.imagesDiv').hide();
			}else if(item.itemType == "Service"){
				$('#service_type').prop("checked",true);
				$('.itemCode_txt').text("Service Code");
				$('.itemName_txt').text("Service Name");
				$('.item_grpCode_txt').text("Service Group Code");
				$('.item_grpName_txt').text("Service Group Name");
				$('.item_description_txt').text("Service Description");
				$('.itemDescription').attr("placeholder","Service Description");
				$('.hsnSacTxt').text("SAC Code");
				$('.itemSalesPriceTxt').text('Service Sale Price');
				$('.item_description_txt').css("margin-left","2px");
				$('.descInput').css({"padding-left":"34px"});
				$('.descInput').removeClass("pr-0");
				//$('#itemform .form-group').css("margin-top","-25px");
				//$('.grpCodeDiv,.grpNameDiv,.descDiv,.uqcDiv,.itemSaleDiv,.hsnSacDiv,.gstDiv,.isexempted,.custDiv').css("margin-top","-60px");
				$('.openingStockDiv,.asOnDateStockDiv,.safteyStockLevelDiv,.reOrderDiv,.ItemPurchasePriceDiv,.wholeSalePriceDiv,.mrpPriceDiv,.serviceDiv,.imagesDiv').addClass("d-none");
				$('.imagesDiv').hide();
				var uqcoptions = {
						data: ["NA"]
				};
				$("#unitofMeasurement").easyAutocomplete(uqcoptions);
			}else{
				$('#product_type').prop("checked",true);
				$('#product_type').prop("checked",true);
				$('.itemCode_txt').text("Item Code");
				$('.itemName_txt').text("Item Name");
				$('.item_grpCode_txt').text("Item Group Code");
				$('.item_grpName_txt').text("Item Group Name");
				$('.item_description_txt').text("Item Description");
				$('.hsnSacTxt').text("HSN Code");
				$('.itemDescription').attr("placeholder","Item Description");
				$('.itemSalesPriceTxt').text('Item Sale Price');
				$('.item_description_txt').css("margin-left","2px");
				$('.descInput').css({"padding-left":"52px"});
				$('.descInput').addClass("pr-0");
				//$('#itemform .form-group').css("margin-top","0px");
				//$('.grpCodeDiv,.grpNameDiv,.descDiv,.uqcDiv,.itemSaleDiv,.hsnSacDiv,.gstDiv,.isexempted,.custDiv').css("margin-top","0px");
				$('.openingStockDiv,.asOnDateStockDiv,.safteyStockLevelDiv,.reOrderDiv,.ItemPurchasePriceDiv,.wholeSalePriceDiv,.mrpPriceDiv,.serviceDiv,.imagesDiv').removeClass("d-none");
				$('.imagesDiv').hide();
			}
			
			$("form[name='userform']").append('<input type="hidden" name="id" id="itemid" value="'+item.userid+'">');
			var curstock = item.currentStock ? item.currentStock : '0.00';
			$("form[name='userform']").append('<input type="hidden" name="currentStock" id="itemcurrentstock" value="'+curstock+'">');
			if(item.totalQtyUsage !="" && item.totalQtyUsage != null){
				$("form[name='userform']").append('<input type="hidden" name="totalQtyUsage" id="itemtotalqtyusage" value="'+item.totalQtyUsage+'">');
			}
			//$("input[name='createdDate']").val(item.createdDate);
			$("input[name='createdBy']").val(item.createdBy);
			showItemsAdditionalDetails(customFieldsData,false,item);
		 }
	});
	var exp_val = $('.exmeptedvalue').val();
	if(exp_val == ''){
		$('.addExempted').prop('checked',false);
		$(".exemptedval").hide();
	}else{
		$('.addExempted').prop('checked',true);
		$(".exemptedval").show();
	}
}
function getItemsData(itemId, populateItemData){
	var urlStr = _getContextPath()+'/getItem/'+itemId;
	$.ajax({
		url: urlStr,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			populateItemData(response);
		}
	});
}
function getItemColumns(id, clientId, userType, month, year){
	var chkBx = {data: function ( data, type, row ) {
			return '<div class="checkbox nottoedit" index="'+data.userid+'"><label><input type="checkbox" id="itemFilter'+data.userid+'" onClick="updateItemSelection(\''+data.userid+'\',this)"/><i class="helper"></i></label></div>';
	}};
	
	var itemNo = {data:  function ( data, type, row ) {
		var itemno = data.itemno ? data.itemno : "";
		return '<span class="text-left invoiceclk">'+itemno+'</span>';
		}};
	
	var itemdescription = {data:  function ( data, type, row ) {
		var description = data.description ? data.description : "";
		return '<span class="text-left invoiceclk">'+description+'</span>';
		}};
	
	/*var itemunit = {data:  function ( data, type, row ) {
		var unit = data.unit ? data.unit : "";
		return '<span class="text-left invoiceclk">'+unit+'</span>';
		}};
	
	var openingStock = {data:  function ( data, type, row ) {
		var openingstock = data.openingStock ? data.openingStock : "";
		return '<span class="text-left invoiceclk">'+openingstock+'</span>';
		}};*/
	var currentStock = {data:  function ( data, type, row ) {
		var currentstock = data.currentStock ? data.currentStock : "";
		var unit = data.unit ? data.unit : "";
		return '<span class="text-left invoiceclk">'+currentstock+' '+unit+'</span>';
		}};
	var stockVal = {data:  function ( data, type, row ) {
		var currentstock = data.currentStock ? data.currentStock : "";
		var purchaseprice = data.purchasePrice ? data.purchasePrice : 0.00;
		var val = currentstock*purchaseprice;
		return '<span class="text-left invoiceclk">'+formatNumber(val.toFixed(2))+'</span>';
		}};
	
	var itemdiscount = {data:  function ( data, type, row ) {
		var discount = data.discount ? data.discount : "";
		return '<span class="text-left invoiceclk">'+discount+'</span>';
		}};
	
	var hsn = {data:  function ( data, type, row ) {
		var code = data.code ? data.code : "";
		var code1 = code.split(":");
		return '<span class="text-left invoiceclk">'+code1[0]+'</span>';
		}};
	
	var sellingPrice = {data:  function ( data, type, row ) {
		var saleprice = data.salePrice ? data.salePrice : 0.00;
		return '<span class="text-left invoiceclk">'+formatNumber(saleprice.toFixed(2))+'</span>';
		}};
	var purchasePrice = {data:  function ( data, type, row ) {
		var purchaseprice = data.purchasePrice ? data.purchasePrice : 0.00;
		return '<span class="text-left invoiceclk">'+formatNumber(purchaseprice.toFixed(2))+'</span>';
		}};
	return [chkBx , itemNo,itemdescription,hsn, currentStock, stockVal, sellingPrice, purchasePrice,
        {data: function ( data, type, row ) {
     		 return '<a class="btn-edt permissionSettings-Items-Edit" href="#" data-toggle="modal" data-target="#editModal" onClick=""><i class="fa fa-edit"></i> </a><a href="#" class="permissionSettings-Items-Delete nottoedit" onClick="showDeletePopup(\''+data.userid+'\',\''+data.description+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a>';
            }} 
        ];
}
function getItemColumnDefs(){
	return  [
		{
			"targets":  [1,2,3,4],
			className: 'dt-body-left'
		},
		{
			"targets":  [5,6,7],
			className: 'dt-body-right'
		},
		{
			"targets": 0,
			"orderable": false
		}
		
	];
}

function removemsg1(){
	$('.errormsg').css('display','none');
} 


function showDeletePopup(itemId, name) {
	$('#deleteModal').modal('show');
	$('#delPopupDetails').html(name);
	$('#btnDelete').attr('onclick', "deleteProduct('"+itemId+"')");
}

function deleteProduct(itemId) {
	$.ajax({
		url: _getContextPath()+"/delitem/"+itemId,
		success : function(response) {
			itemTable.row( $('#row'+itemId) ).remove().draw();
			location.reload(true);
		}
	});
}

function updateItemSelection(id,chkBox){
	if(chkBox.checked) {
		itemArray.push(id);
	} else {
		var iArray=new Array();
		itemArray.forEach(function(item) {
			if(item != id) {
				iArray.push(item);
			}
		});
		itemArray = iArray;
	}
	if(itemArray.length > 0){
		$('#itemDelete').removeClass("disabled");
	}else{
		$('#itemDelete').addClass("disabled");
	}
}
function updateItemsMainSelection(chkBox) {
	itemArray = new Array();
	var check = $('#itemCheck').prop("checked");
    var rows = itemTable.rows().nodes();
    if(check) {
    	itemTable.rows().every(function () {
	    	var row = this.data();
	    	itemArray.push(row.userid);
	   });
    }
    if(itemArray.length > 0){
		$('#itemDelete').removeClass("disabled");
	}else{
		$('#itemDelete').addClass("disabled");
	}
    $('input[type="checkbox"]', rows).prop('checked', check);
}
function showItemsDeleteAllPopUp(){
	$('#deleteModal').modal('show');
	$('#delPopupDetails').html(name);
	$('#btnDelete').attr('onclick', "deleteSelectedItem()");
}
function deleteSelectedItem(){
	if(itemArray.length > 0) {
		for(var i=0;i<itemArray.length;i++) {
			deleteSelectItems(itemArray[i]);
		}
	} else {
		deleteSelectItems(new Array());
	}
}
function deleteSelectItems(itemArray){
	$.ajax({
			url: _getContextPath()+"/delitem/"+itemArray,
			success : function(response) {
				itemTable.row( $('#row'+itemArray) ).remove().draw();
				location.reload(true);
			}
		});
}

function labelitemsubmit(){
	$( "#itemform" ).submit();
}

$('#itemsimportform').submit(function(e) {
	  var err = 0;
	    if (!$('#messages5').html()) {    	
	      err = 1;
	    }
		  if (err != 0) {
		   $('.errormsg').css('display','block');
	    return false;
	  }
	});
	function chooseitemfileSheets(){
		$('#itemFile')[0].click()
	}
	
	$('#itemform').submit(function(e) {
		var sb2b = $('.recommendedSellingPriceForB2B').val();
	//	var sb2c = $('.recommendedSellingPriceForB2C').val();
		var unit = $('#unitofMeasurement').val();
		var err = 0;
		if(sb2b != ''){
			sb2b = sb2b.replace(/,/g , '');
			$('.recommendedSellingPriceForB2B').val(sb2b);
		}
		/* if(sb2c != ''){
			sb2c = sb2c.replace(/,/g , '');
			$('.recommendedSellingPriceForB2C').val(sb2c);
		} */
		
		if($('#unitofMeasurementempty').is(':hidden')){
			$.ajax({
				url: _getContextPath()+"/validuqcconfig?query="+ unit + "&format=json",
				async: false,
				contentType: 'application/json',
				success : function(response) {
					if(response.length == 0){
						err =1;
						
					}
				}
			});
		}else{
			err =1;
			$('#profileLevelItemUqc #unitofMeasurement_Msg').text('Please Enter Valid UQC');
			return false;
		}
		if(err != 0){
		 	if($('#unitofMeasurement').val() == ''){
			$('#profileLevelItemUqc #unitofMeasurement_Msg').text('');
			return false;
			}
			else{
			$('#profileLevelItemUqc #unitofMeasurement_Msg').text('Please Enter Valid UQC');
			return false;
			}
		} else{
			$('#profileLevelItemUqc #unitofMeasurement_Msg').text(' ');
			
			return true;
		}
		
	});
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
	function stockAdjustModal(){
		$('.stockForm').trigger("reset");
		$('.stockForm').attr("action",contextPath+"/cp_createStock"+commonSuffix+"/"+clientId+"/"+month+"/"+year+"?itemid=");
		$('#add_sstock').attr("checked","checked");
		$('#reduce_stock').removeAttr("checked");
		var stockMoveVal = $('input[type=radio][name=stockMovement]:checked').val();
		var itemoptions = {
				url: function(phrase) {
					return contextPath+"/srchItems?query="+ phrase + "&clientid="+clientId+"&format=json";
				},
				categories: [{
					listLocation: "items"
				}],
				getValue: "itemnoAndDescription",
				list: {
					onChooseEvent: function() {
						var itemData = $("#stockItemno").getSelectedItemData();
						$('.stockItemName').val(itemData.description ? itemData.description : "");
						$('.stockItemGrpCode').val(itemData.itemgroupno ? itemData.itemgroupno : "");
						$('.stockItemGrpName').val(itemData.groupdescription ? itemData.groupdescription : "");
						$('.currentStock').val(itemData.currentStock ? itemData.currentStock : 0.00);
						$('.stockPurchaseCost').val(itemData.purchasePrice ? itemData.purchasePrice : 0.00);
						$('#stockOpeningStock').val(itemData.currentStock ? itemData.currentStock : 0.00);
						$('#stockUnit').val(itemData.unit ? itemData.unit : "");
						$('.stockForm').attr("action",contextPath+"/cp_createStock"+commonSuffix+"/"+clientId+"/"+month+"/"+year+"?itemid="+itemData.userid);
					},
					onLoadEvent: function() {
						if($("#eac-container-stockItemno ul").children().length == 0) {
							$("#remainstockItemno_textempty").show();
							$("#remainstockItemno_textempty p").css({'color':'#CC0000','margin':'0'});
						} else {
							$("#remainstockItemno_textempty").hide();
						}
					}
				}
			};
			$("#stockItemno").easyAutocomplete(itemoptions);
			getPoNos(stockMoveVal);
	}
	function getPoNos(value){
		var poNos = {
				url: function(phrase) {
					if(value == "Out"){
						return contextPath+"/srchPoNos"+urlSuffixs+"/"+Paymenturlprefix+"?query="+ phrase + "&format=json&type=PRO";
					}else{
						return contextPath+"/srchPoNos"+urlSuffixs+"/"+Paymenturlprefix+"?query="+ phrase + "&format=json&type=PO";
					}
				},
				getValue: "invoiceno",
				list: {
					onChooseEvent: function() {
						var poData = $("#stockPoNo").getSelectedItemData();
						$('#stockVendor').val(poData.billedtoname ? poData.billedtoname : "");
					}
				}
		};
		$("#stockPoNo").easyAutocomplete(poNos);
	}
	function labelStockAdjustSubmit(){
		$(".stockForm").submit();
	}
	function adjustItemQuantity(){
		var openStock = $('#stockOpeningStock').val();
		var stockMoveVal = $('input[type=radio][name=stockMovement]:checked').val();
		var qty = $('#stockItemQty').val();
		var cStock = 0;
		if(qty){
			if(stockMoveVal =="Out"){
				cStock = parseFloat(openStock) - parseFloat(qty);
			}else{
				cStock = parseFloat(openStock) + parseFloat(qty);
			}
		}
		$('.currentStock').val(cStock);
	}	
	
	function updateStock(value){
		$('#stockPoNo,#stockVendor').val("");
		var openStock = $('#stockOpeningStock').val();
		var qty = $('#stockItemQty').val();
		var cStock = 0;
		if(value == "Out"){
			$('#reduce_stock').attr("checked","checked");
			$('#add_sstock').removeAttr("checked");
			if(qty && openStock){
				cStock = parseFloat(openStock) - parseFloat(qty);
			}else if(openStock){
				cStock = parseFloat(openStock);
			}
			getPoNos(value);
		}else{
			$('#add_sstock').attr("checked","checked");
			$('#reduce_stock').removeAttr("checked");
			if(qty && openStock){
				cStock = parseFloat(openStock) + parseFloat(qty);
			}else if(openStock){
				cStock = parseFloat(openStock);
			}
			getPoNos(value);
		}
		$('.currentStock').val(cStock);
	}
  
  function showItemsPopup(){
		$('#editModal').modal("show");
		showItemsAdditionalDetails(customFieldsData,true,null);
		var items = {
				url: function(phrase) {
					return contextPath+"/srchItemNames?query="+ phrase + "&format=json";
				},
				getValue: "description",
				list: {
					onChooseEvent: function() {
					}
				}
		};
		$(".itemName").easyAutocomplete(items);
	}
	function showItemsAdditionalDetails(data,addFlag,invoiceCustomData){
		var content ="";
		var indexarry = new Array();
		if(data && data.items){
			$('.custFieldsLink').hide();
		for(var i=0;i<data.items.length;i++){
			var index = i + 1;
			var itemData = data.items[i];
			if(itemData != undefined){
				if(itemData.customFieldType == 'input'){
					var require = itemData.isMandatory ? 'required' : ''; 
					var astrich = itemData.isMandatory ? 'astrich' : '';
					content += '<div class="col-md-6 col-sm-12 form-group" id="inv_CustField1"><div class="row p-0"></span><p id="invcustFieldText1" class="'+astrich+' lable-txt col-md-5 pl-3">'+itemData.customFieldName+'</p><div class="col-md-7 pl-0"><input type="hidden" name="itemCustomFieldText'+index+'" value="'+itemData.customFieldName+'"/><input type="text" class="input_cust" name="itemCustomField'+index+'" id="item_CustomField'+index+'" data-error="Please enter more than 3 characters" placeholder="'+itemData.customFieldName+'" '+require+'><label for="input" class="control-label"></label><div class="help-block with-errors"></div><i class="bar"></i></div></div></div>';
					index++;
				}else if(itemData.customFieldType == 'list'){
					var require = itemData.isMandatory ? 'required' : ''; 
					var astrich = itemData.isMandatory ? 'astrich' : '';
					content += '<div class="col-md-6 col-sm-12 form-group" id="inv_CustField1"><div class="row p-0"><p id="invcustFieldText1" class="'+astrich+' lable-txt col-md-5 pl-3">'+itemData.customFieldName+'</p><div class="col-md-7 pl-0"><input type="hidden" name="itemCustomFieldText'+index+'" value="'+itemData.customFieldName+'"/><select class="form-control" name="itemCustomField'+index+'" data-error="Please enter this value" id="item_CustomField'+index+'" '+require+'><option value="">-Select-</option>';
					for(var j = 0; j < itemData.typeData.length; j++){
						content += "<option value=\'"+itemData.typeData[j]+"\'>"+itemData.typeData[j]+"</option>";
					}
					content +='</select><label for="input" class="control-label"></label><div class="help-block with-errors"></div><i class="bar"></i></div></div></div>';
					index++;
				}else if(itemData.customFieldType == 'checkB'){
					content += '<div class="col-md-6 col-sm-12" id="inv_CustField1"><div class="row p-0"><p id="invcustFieldText1" class="lable-txt col-md-5 pl-3 pt-3">'+itemData.customFieldName+'</p><div class="col-md-7 pl-0 mb-3 pt-3"><input type="hidden" name="itemCustomFieldText'+index+'" value="'+itemData.customFieldName+'"/>';
					/* for(var j = 0; j < itemData.typeData.length; j++){
						if(invoiceCustomData){
							if(itemData.typeData[j] == invoiceCustomData.itemCustomField1 || itemData.typeData[j] ==  invoiceCustomData.itemCustomField2 || itemData.typeData[j] == invoiceCustomData.itemCustomField3 || itemData.typeData[j] ==  invoiceCustomData.itemCustomField4){
								content += '<div class="checkbox checkbox-inline mt-0"><label><input type="checkbox" id="item_CustomField'+index+'" value="'+itemData.typeData[j]+'" checked><i class="helper"></i>'+itemData.typeData[j]+'</label></div>';
							}else{
								content += '<div class="checkbox checkbox-inline mt-0"><label><input type="checkbox" id="item_CustomField'+index+'" value="'+itemData.typeData[j]+'"><i class="helper"></i>'+itemData.typeData[j]+'</label></div>';
							}
						}else{
							content += '<div class="checkbox checkbox-inline mt-0"><label><input type="checkbox" id="item_CustomField'+index+'" value="'+itemData.typeData[j]+'"><i class="helper"></i>'+itemData.typeData[j]+'</label></div>';
						}	
					}
					content += '<input type="hidden" class="cust_check_val" name="itemCustomField'+index+'"><label for="input" class="control-label"></label><div class="help-block with-errors"></div><i class="bar"></i></div></div></div>';
					var val = $('#item_CustomField'+index+'').val();*/
					for(var j = 0; j < itemData.typeData.length; j++){
						if(invoiceCustomData){
							if(invoiceCustomData.itemCustomField1 !="" && invoiceCustomData.itemCustomField1 != null && invoiceCustomData.itemCustomField1.includes(itemData.typeData[j])){
								content += '<div class="checkbox checkbox-inline mt-0 pl-0"><label><input type="checkbox" name="itemCustomField'+index+'" id="item_CustomField'+index+'" value="'+itemData.typeData[j]+'" checked><i class="helper"></i>'+itemData.typeData[j]+'</label></div>&nbsp;&nbsp;';
							}else if(invoiceCustomData.itemCustomField2 !="" && invoiceCustomData.itemCustomField2 != null && invoiceCustomData.itemCustomField2.includes(itemData.typeData[j])){
								content += '<div class="checkbox checkbox-inline mt-0 pl-0"><label><input type="checkbox" name="itemCustomField'+index+'" id="item_CustomField'+index+'" value="'+itemData.typeData[j]+'" checked><i class="helper"></i>'+itemData.typeData[j]+'</label></div>&nbsp;&nbsp;';
							}else if(invoiceCustomData.itemCustomField3 !="" && invoiceCustomData.itemCustomField3 != null && invoiceCustomData.itemCustomField3.includes(itemData.typeData[j])){
								content += '<div class="checkbox checkbox-inline mt-0 pl-0"><label><input type="checkbox" name="itemCustomField'+index+'" id="item_CustomField'+index+'" value="'+itemData.typeData[j]+'" checked><i class="helper"></i>'+itemData.typeData[j]+'</label></div>&nbsp;&nbsp;';
							}else if(invoiceCustomData.itemCustomField4 !="" && invoiceCustomData.itemCustomField4 != null && invoiceCustomData.itemCustomField4.includes(itemData.typeData[j])){
								content += '<div class="checkbox checkbox-inline mt-0 pl-0"><label><input type="checkbox" name="itemCustomField'+index+'" id="item_CustomField'+index+'" value="'+itemData.typeData[j]+'" checked><i class="helper"></i>'+itemData.typeData[j]+'</label></div>&nbsp;&nbsp;';
							}else {
								content += '<div class="checkbox checkbox-inline mt-0 pl-0"><label><input type="checkbox" name="itemCustomField'+index+'" id="item_CustomField'+index+'" value="'+itemData.typeData[j]+'"><i class="helper"></i>'+itemData.typeData[j]+'</label></div>&nbsp;&nbsp;';
							}
						}else{
							content += '<div class="checkbox checkbox-inline mt-0 pl-0"><label><input type="checkbox" name="itemCustomField'+index+'" id="item_CustomField'+index+'" value="'+itemData.typeData[j]+'"><i class="helper"></i>'+itemData.typeData[j]+'</label></div>&nbsp;&nbsp;';
						}
					}
					content += '</div></div></div>';
					indexarry.push(index);
					index++;
				}else if(itemData.customFieldType == 'radio'){
					content += '<div class="col-md-6 col-sm-12" id="inv_CustField1"><div class="row p-0"><p id="invcustFieldText1" class="lable-txt col-md-5 pl-3 pt-3">'+itemData.customFieldName+'</p><div class="col-md-7 pl-0 mb-3 pt-3"><input type="hidden" name="itemCustomFieldText'+index+'" value="'+itemData.customFieldName+'"/>';
					for(var j = 0; j < itemData.typeData.length; j++){
						if(invoiceCustomData){
							if(itemData.typeData[j] == invoiceCustomData.itemCustomField1 || itemData.typeData[j] ==  invoiceCustomData.itemCustomField2 || itemData.typeData[j] == invoiceCustomData.itemCustomField3 || itemData.typeData[j] ==  invoiceCustomData.itemCustomField4){
								content += '<div class="cust_radio"><input type="radio" name="itemCustomField'+index+'" id="item_CustomField'+index+'" value="'+itemData.typeData[j]+'" checked = "true">&nbsp;<label>'+itemData.typeData[j]+'</label>&nbsp;</div>';
							}else{
								content += '<div class="cust_radio"><input type="radio" name="itemCustomField'+index+'" id="item_CustomField'+index+'" value="'+itemData.typeData[j]+'">&nbsp;<label>'+itemData.typeData[j]+'</label>&nbsp;</div>';
							}
						}else{
							content += '<div class="cust_radio"><input type="radio" name="itemCustomField'+index+'" id="item_CustomField'+index+'" value="'+itemData.typeData[j]+'">&nbsp;<label>'+itemData.typeData[j]+'</label>&nbsp;</div>';
						}	
					} 
					content += '<label for="input" class="control-label"></label><div class="help-block with-errors"></div><i class="bar"></i></div></div></div>';
					indexarry.push(index);
					index++;
				}
			}
			$('.item_customFields').html(content);
		}
		if(addFlag){
			}else{
				for(var j=1;j<5;j++){
					if(!indexarry.includes(j)){
						if(j==1){
							if(invoiceCustomData.itemCustomField1 != "" && invoiceCustomData.itemCustomField1 != null){
								$('#item_CustomField1').val(invoiceCustomData.itemCustomField1);
							}
						}else if(j==2){
							if(invoiceCustomData.itemCustomField2 != "" && invoiceCustomData.itemCustomField2 != null){
								$('#item_CustomField2').val(invoiceCustomData.itemCustomField2);
							}
						}else if(j==3){
							if(invoiceCustomData.itemCustomField3 != "" && invoiceCustomData.itemCustomField3 != null){
								$('#item_CustomField3').val(invoiceCustomData.itemCustomField3);
							}
						}else if(j==4){
							if(invoiceCustomData.itemCustomField4 != "" && invoiceCustomData.itemCustomField4 != null){
								$('#item_CustomField4').val(invoiceCustomData.itemCustomField4);	
							}
						}
					}
				}
			}
		}else{
			$('.custFieldsLink').show();
		}
	}
	function custFieldLink(){
		window.location.href = _getContextPath()+'/cp_upload'+paymenturlSuffix+'/'+month+'/'+year+'?type=customfields';
	}