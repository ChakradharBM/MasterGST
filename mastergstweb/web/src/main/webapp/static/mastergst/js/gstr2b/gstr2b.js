var gstrInvoiceArray = new Array();
var transCount = 0, totalValue = 0, totalTax = 0, totalTaxableValue = 0, totalIGST = 0, totalCGST = 0, totalSGST = 0, totalCESS = 0;
var gstr2bDbTable;
function syncWithGstr2b(type){
	if(type != 'GSTN'){
		$('a#excelDownloadGstr2b').addClass('btn-loader');
		$('a#excelDownloadGstr2b').addClass('disable');
	}else{
		$('a#dwnldFromGstinBtn').addClass('btn-loader');
		$('a#dwnldFromGstinBtn').addClass('disable');
	}
}
$(function(){
	$('#divFiltersGstr2b').on('click', '.deltag', function(e) {
		var val = $(this).data('val');
		$('#multiselectInvType').multiselect('deselect', [val]);
		$('#multiselectUsers').multiselect('deselect', [val]);
		$('#multiselectSuppliers').multiselect('deselect', [val]);
		$('#multiselectReverseCharge').multiselect('deselect', [val]);
		$('#multiselectITCAvailablity').multiselect('deselect', [val]);
		applyGSTR2BFilters();
	});
	
});

function loadTables(id,clientid,returntype,month,year){
	var urlStr = _getContextPath()+'/getGSTR2BData/'+id+'/'+clientId+'/'+month+'/'+year;
	$.ajax({
		url: urlStr,
		async: true,
		cache: false,
		//dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			if(response != ""){
				if(response.data != "" && response.data != null){
					if(response.data.itcsumm != "" && response.data.itcsumm != null){
						if(response.data.itcsumm.itcavl != "" && response.data.itcsumm.itcavl != null){
							if(response.data.itcsumm.itcavl.nonrevsup != "" && response.data.itcsumm.itcavl.nonrevsup != null){
								$('#itcorCGST').html(response.data.itcsumm.itcavl.nonrevsup.cgst ? formatNumber(response.data.itcsumm.itcavl.nonrevsup.cgst.toFixed(2)) : "0.00");
								$('#itcorSGST').html(response.data.itcsumm.itcavl.nonrevsup.sgst ? formatNumber(response.data.itcsumm.itcavl.nonrevsup.sgst.toFixed(2)) : "0.00");
								$('#itcorIGST').html(response.data.itcsumm.itcavl.nonrevsup.igst ? formatNumber(response.data.itcsumm.itcavl.nonrevsup.igst.toFixed(2)) : "0.00");
								$('#itcorCESS').html(response.data.itcsumm.itcavl.nonrevsup.cess ? formatNumber(response.data.itcsumm.itcavl.nonrevsup.cess.toFixed(2)) : "0.00");
								if(response.data.itcsumm.itcavl.nonrevsup.b2b != "" && response.data.itcsumm.itcavl.nonrevsup.b2b != null){
									$('#itcorB2BCGST').html(response.data.itcsumm.itcavl.nonrevsup.b2b.cgst ? formatNumber(response.data.itcsumm.itcavl.nonrevsup.b2b.cgst.toFixed(2)) : "0.00");
									$('#itcorB2BSGST').html(response.data.itcsumm.itcavl.nonrevsup.b2b.sgst ? formatNumber(response.data.itcsumm.itcavl.nonrevsup.b2b.sgst.toFixed(2)) : "0.00");
									$('#itcorB2BIGST').html(response.data.itcsumm.itcavl.nonrevsup.b2b.igst ? formatNumber(response.data.itcsumm.itcavl.nonrevsup.b2b.igst.toFixed(2)) : "0.00");
									$('#itcorB2BCESS').html(response.data.itcsumm.itcavl.nonrevsup.b2b.cess ? formatNumber(response.data.itcsumm.itcavl.nonrevsup.b2b.cess.toFixed(2)) : "0.00");
								}
								if(response.data.itcsumm.itcavl.nonrevsup.cdnr != "" && response.data.itcsumm.itcavl.nonrevsup.cdnr != null){
									$('#itcorCDNCGST').html(response.data.itcsumm.itcavl.nonrevsup.cdnr.cgst ? formatNumber(response.data.itcsumm.itcavl.nonrevsup.cdnr.cgst.toFixed(2)) : "0.00");
									$('#itcorCDNSGST').html(response.data.itcsumm.itcavl.nonrevsup.cdnr.sgst ? formatNumber(response.data.itcsumm.itcavl.nonrevsup.cdnr.sgst.toFixed(2)) : "0.00");
									$('#itcorCDNIGST').html(response.data.itcsumm.itcavl.nonrevsup.cdnr.igst ? formatNumber(response.data.itcsumm.itcavl.nonrevsup.cdnr.igst.toFixed(2)) : "0.00");
									$('#itcorCDNCESS').html(response.data.itcsumm.itcavl.nonrevsup.cdnr.cess ? formatNumber(response.data.itcsumm.itcavl.nonrevsup.cdnr.cess.toFixed(2)) : "0.00");
								}
								if(response.data.itcsumm.itcavl.nonrevsup.b2ba != "" && response.data.itcsumm.itcavl.nonrevsup.b2ba != null){
									$('#itcorB2BACGST').html(response.data.itcsumm.itcavl.nonrevsup.b2ba.cgst ? formatNumber(response.data.itcsumm.itcavl.nonrevsup.b2ba.cgst.toFixed(2)) : "0.00");
									$('#itcorB2BASGST').html(response.data.itcsumm.itcavl.nonrevsup.b2ba.sgst ? formatNumber(response.data.itcsumm.itcavl.nonrevsup.b2ba.sgst.toFixed(2)) : "0.00");
									$('#itcorB2BAIGST').html(response.data.itcsumm.itcavl.nonrevsup.b2ba.igst ? formatNumber(response.data.itcsumm.itcavl.nonrevsup.b2ba.igst.toFixed(2)) : "0.00");
									$('#itcorB2BACESS').html(response.data.itcsumm.itcavl.nonrevsup.b2ba.cess ? formatNumber(response.data.itcsumm.itcavl.nonrevsup.b2ba.cess.toFixed(2)) : "0.00");					
								}
								if(response.data.itcsumm.itcavl.nonrevsup.cdnra != "" && response.data.itcsumm.itcavl.nonrevsup.cdnra != null){
									$('#itcorCDNACGST').html(response.data.itcsumm.itcavl.nonrevsup.cdnra.cgst ? formatNumber(response.data.itcsumm.itcavl.nonrevsup.cdnra.cgst.toFixed(2)) : "0.00");
									$('#itcorCDNASGST').html(response.data.itcsumm.itcavl.nonrevsup.cdnra.sgst ? formatNumber(response.data.itcsumm.itcavl.nonrevsup.cdnra.sgst.toFixed(2)) : "0.00");
									$('#itcorCDNAIGST').html(response.data.itcsumm.itcavl.nonrevsup.cdnra.igst ? formatNumber(response.data.itcsumm.itcavl.nonrevsup.cdnra.igst.toFixed(2)) : "0.00");
									$('#itcorCDNACESS').html(response.data.itcsumm.itcavl.nonrevsup.cdnra.cess ? formatNumber(response.data.itcsumm.itcavl.nonrevsup.cdnra.cess.toFixed(2)) : "0.00");
								}
							}
						
							if(response.data.itcsumm.itcavl.isdsup != "" && response.data.itcsumm.itcavl.isdsup != null){
								$('#itcCGST').html(response.data.itcsumm.itcavl.isdsup.cgst ? formatNumber(response.data.itcsumm.itcavl.isdsup.cgst.toFixed(2)) : "0.00");
								$('#itcSGST').html(response.data.itcsumm.itcavl.isdsup.sgst ? formatNumber(response.data.itcsumm.itcavl.isdsup.sgst.toFixed(2)): "0.00");
								$('#itcIGST').html(response.data.itcsumm.itcavl.isdsup.igst ? formatNumber(response.data.itcsumm.itcavl.isdsup.igst.toFixed(2)) : "0.00");
								$('#itcCESS').html(response.data.itcsumm.itcavl.isdsup.cess ? formatNumber(response.data.itcsumm.itcavl.isdsup.cess.toFixed(2)) : "0.00");								
								
								if(response.data.itcsumm.itcavl.isdsup.isd != "" && response.data.itcsumm.itcavl.isdsup.isd != null){
									$('#itcisdCGST').html(response.data.itcsumm.itcavl.isdsup.isd.cgst ? formatNumber(response.data.itcsumm.itcavl.isdsup.isd.cgst.toFixed(2)): "0.00");
									$('#itcisdSGST').html(response.data.itcsumm.itcavl.isdsup.isd.sgst ? formatNumber(response.data.itcsumm.itcavl.isdsup.isd.sgst.toFixed(2)) : "0.00");
									$('#itcisdIGST').html(response.data.itcsumm.itcavl.isdsup.isd.igst ? formatNumber(response.data.itcsumm.itcavl.isdsup.isd.igst.toFixed(2)) : "0.00");
									$('#itcisdCESS').html(response.data.itcsumm.itcavl.isdsup.isd.cess ? formatNumber(response.data.itcsumm.itcavl.isdsup.isd.cess.toFixed(2)) : "0.00");									
								}
								if(response.data.itcsumm.itcavl.isdsup.isda != "" && response.data.itcsumm.itcavl.isdsup.isda != null){
									$('#itcisdaCGST').html(response.data.itcsumm.itcavl.isdsup.isda.cgst ? formatNumber(response.data.itcsumm.itcavl.isdsup.isda.cgst.toFixed(2)) : "0.00");
									$('#itcisdaSGST').html(response.data.itcsumm.itcavl.isdsup.isda.sgst ? formatNumber(response.data.itcsumm.itcavl.isdsup.isda.sgst.toFixed(2)) : "0.00");
									$('#itcisdaIGST').html(response.data.itcsumm.itcavl.isdsup.isda.igst ? formatNumber(response.data.itcsumm.itcavl.isdsup.isda.igst.toFixed(2)) : "0.00");
									$('#itcisdaCESS').html(response.data.itcsumm.itcavl.isdsup.isda.cess ? formatNumber(response.data.itcsumm.itcavl.isdsup.isda.cess.toFixed(2)) : "0.00");									
								}
							}
							if(response.data.itcsumm.itcavl.revsup != "" && response.data.itcsumm.itcavl.revsup != null){
								$('#itcrCGST').html(response.data.itcsumm.itcavl.revsup.cgst ? formatNumber(response.data.itcsumm.itcavl.revsup.cgst.toFixed(2)) : "0.00");
								$('#itcrSGST').html(response.data.itcsumm.itcavl.revsup.sgst ? formatNumber(response.data.itcsumm.itcavl.revsup.sgst.toFixed(2)) : "0.00");
								$('#itcrIGST').html(response.data.itcsumm.itcavl.revsup.igst ? formatNumber(response.data.itcsumm.itcavl.revsup.igst.toFixed(2)) : "0.00");
								$('#itcrCESS').html(response.data.itcsumm.itcavl.revsup.cess ? formatNumber(response.data.itcsumm.itcavl.revsup.cess.toFixed(2)): "0.00");
								if(response.data.itcsumm.itcavl.revsup.b2b != "" && response.data.itcsumm.itcavl.revsup.b2b != null){
									$('#itcrB2BCGST').html(response.data.itcsumm.itcavl.revsup.b2b.cgst ? formatNumber(response.data.itcsumm.itcavl.revsup.b2b.cgst.toFixed(2)) : "0.00");
									$('#itcrB2BSGST').html(response.data.itcsumm.itcavl.revsup.b2b.sgst ? formatNumber(response.data.itcsumm.itcavl.revsup.b2b.sgst.toFixed(2)) : "0.00");
									$('#itcrB2BIGST').html(response.data.itcsumm.itcavl.revsup.b2b.igst ? formatNumber(response.data.itcsumm.itcavl.revsup.b2b.igst.toFixed(2)) : "0.00");
									$('#itcrB2BCESS').html(response.data.itcsumm.itcavl.revsup.b2b.cess ? formatNumber(response.data.itcsumm.itcavl.revsup.b2b.cess.toFixed(2)) : "0.00");
								}
								if(response.data.itcsumm.itcavl.revsup.cdnr != "" && response.data.itcsumm.itcavl.revsup.cdnr != null){
									$('#itcrCDNCGST').html(response.data.itcsumm.itcavl.revsup.cdnr.cgst ? formatNumber(response.data.itcsumm.itcavl.revsup.cdnr.cgst.toFixed(2)) : "0.00");
									$('#itcrCDNSGST').html(response.data.itcsumm.itcavl.revsup.cdnr.sgst ?formatNumber(response.data.itcsumm.itcavl.revsup.cdnr.sgst.toFixed(2)): "0.00");
									$('#itcrCDNIGST').html(response.data.itcsumm.itcavl.revsup.cdnr.igst ? formatNumber(response.data.itcsumm.itcavl.revsup.cdnr.igst.toFixed(2)) : "0.00");
									$('#itcrCDNCESS').html(response.data.itcsumm.itcavl.revsup.cdnr.cess ? formatNumber(response.data.itcsumm.itcavl.revsup.cdnr.cess.toFixed(2)) : "0.00");
								}
								if(response.data.itcsumm.itcavl.revsup.b2ba != "" && response.data.itcsumm.itcavl.revsup.b2ba != null){
									$('#itcrB2BACGST').html(response.data.itcsumm.itcavl.revsup.b2ba.cgst ? formatNumber(response.data.itcsumm.itcavl.revsup.b2ba.cgst.toFixed(2)) : "0.00");
									$('#itcrB2BASGST').html(response.data.itcsumm.itcavl.revsup.b2ba.sgst ? formatNumber(response.data.itcsumm.itcavl.revsup.b2ba.sgst.toFixed(2)) : "0.00");
									$('#itcrB2BAIGST').html(response.data.itcsumm.itcavl.revsup.b2ba.igst ? formatNumber(response.data.itcsumm.itcavl.revsup.b2ba.igst.toFixed(2)) : "0.00");
									$('#itcrB2BACESS').html(response.data.itcsumm.itcavl.revsup.b2ba.cess ? formatNumber(response.data.itcsumm.itcavl.revsup.b2ba.cess.toFixed(2)) : "0.00");
								}
								if(response.data.itcsumm.itcavl.revsup.cdnra != "" && response.data.itcsumm.itcavl.revsup.cdnra != null){
									$('#itcrCDNACGST').html(response.data.itcsumm.itcavl.revsup.cdnra.cgst ? formatNumber(response.data.itcsumm.itcavl.revsup.cdnra.cgst.toFixed(2)) : "0.00");
									$('#itcrCDNASGST').html(response.data.itcsumm.itcavl.revsup.cdnra.sgst ? formatNumber(response.data.itcsumm.itcavl.revsup.cdnra.sgst.toFixed(2)) : "0.00");
									$('#itcrCDNAIGST').html(response.data.itcsumm.itcavl.revsup.cdnra.igst ? formatNumber(response.data.itcsumm.itcavl.revsup.cdnra.igst.toFixed(2)) : "0.00");
									$('#itcrCDNACESS').html(response.data.itcsumm.itcavl.revsup.cdnra.cess ?formatNumber(response.data.itcsumm.itcavl.revsup.cdnra.cess.toFixed(2)) : "0.00");
								}
							}
							if(response.data.itcsumm.itcavl.imports != "" && response.data.itcsumm.itcavl.imports != null){
								$('#itcIMPCGST').html(response.data.itcsumm.itcavl.imports.cgst ? formatNumber(response.data.itcsumm.itcavl.imports.cgst.toFixed(2)) : "0.00");
								$('#itcIMPSGST').html(response.data.itcsumm.itcavl.imports.sgst ? formatNumber(response.data.itcsumm.itcavl.imports.sgst.toFixed(2)) : "0.00");
								$('#itcIMPIGST').html(response.data.itcsumm.itcavl.imports.igst ? formatNumber(response.data.itcsumm.itcavl.imports.igst.toFixed(2)) : "0.00");
								$('#itcIMPCESS').html(response.data.itcsumm.itcavl.imports.cess ? formatNumber(response.data.itcsumm.itcavl.imports.cess.toFixed(2)) : "0.00");								
								if(response.data.itcsumm.itcavl.imports.impg != "" && response.data.itcsumm.itcavl.imports.impg != null){
									$('#itcIMPGCEST').html(response.data.itcsumm.itcavl.imports.impg.cgst ? formatNumber(response.data.itcsumm.itcavl.imports.impg.cgst.toFixed(2)) : "0.00");
									$('#itcIMPGSGST').html(response.data.itcsumm.itcavl.imports.impg.sgst ? formatNumber(response.data.itcsumm.itcavl.imports.impg.sgst.toFixed(2)) : "0.00");
									$('#itcIMPGIGST').html(response.data.itcsumm.itcavl.imports.impg.igst ? formatNumber(response.data.itcsumm.itcavl.imports.impg.igst.toFixed(2)) : "0.00");
									$('#itcIMPGCESS').html(response.data.itcsumm.itcavl.imports.impg.cess ? formatNumber(response.data.itcsumm.itcavl.imports.impg.cess.toFixed(2)) : "0.00");
								}
								if(response.data.itcsumm.itcavl.imports.impga!= "" && response.data.itcsumm.itcavl.imports.impga != null){
									$('#itcIMPGACGST').html(response.data.itcsumm.itcavl.imports.impga.cgst ? formatNumber(response.data.itcsumm.itcavl.imports.impga.cgst.toFixed(2)) : "0.00");
									$('#itcIMPGASGST').html(response.data.itcsumm.itcavl.imports.impga.sgst ? formatNumber(response.data.itcsumm.itcavl.imports.impga.sgst.toFixed(2)) : "0.00");
									$('#itcIMPGAIGST').html(response.data.itcsumm.itcavl.imports.impga.igst ? formatNumber(response.data.itcsumm.itcavl.imports.impga.igst.toFixed(2)) : "0.00");
									$('#itcIMPGACESS').html(response.data.itcsumm.itcavl.imports.impga.cess ? formatNumber(response.data.itcsumm.itcavl.imports.impga.cess.toFixed(2)) : "0.00");
								}
								if(response.data.itcsumm.itcavl.imports.impgsez!= "" && response.data.itcsumm.itcavl.imports.impgsez != null){
									$('#itcIMPGSEZCGST').html(response.data.itcsumm.itcavl.imports.impgsez.cgst ? formatNumber(response.data.itcsumm.itcavl.imports.impgsez.cgst.toFixed(2)) : "0.00");
									$('#itcIMPGSEZSGST').html(response.data.itcsumm.itcavl.imports.impgsez.sgst ? formatNumber(response.data.itcsumm.itcavl.imports.impgsez.sgst.toFixed(2)) : "0.00");
									$('#itcIMPGSEZIGST').html(response.data.itcsumm.itcavl.imports.impgsez.igst ? formatNumber(response.data.itcsumm.itcavl.imports.impgsez.igst.toFixed(2)) : "0.00");
									$('#itcIMPGSEZCESS').html(response.data.itcsumm.itcavl.imports.impgsez.cess ? formatNumber(response.data.itcsumm.itcavl.imports.impgsez.cess.toFixed(2)) : "0.00");
								}
								if(response.data.itcsumm.itcavl.imports.impgasez!= "" && response.data.itcsumm.itcavl.imports.impgasez != null){
									$('#itcIMPGSEZACGST').html(response.data.itcsumm.itcavl.imports.impgasez.cgst ? formatNumber(response.data.itcsumm.itcavl.imports.impgasez.cgst.toFixed(2)) : "0.00");
									$('#itcIMPGSEZASGST').html(response.data.itcsumm.itcavl.imports.impgasez.sgst ? formatNumber(response.data.itcsumm.itcavl.imports.impgasez.sgst.toFixed(2)) : "0.00");
									$('#itcIMPGSEZAIGST').html(response.data.itcsumm.itcavl.imports.impgasez.igst ? formatNumber(response.data.itcsumm.itcavl.imports.impgasez.igst.toFixed(2)) : "0.00");
									$('#itcIMPGSEZACESS').html(response.data.itcsumm.itcavl.imports.impgasez.cess ? formatNumber(response.data.itcsumm.itcavl.imports.impgasez.cess.toFixed(2)) : "0.00");									
								} 
							}
							
							if(response.data.itcsumm.itcavl.othersup != "" && response.data.itcsumm.itcavl.othersup != null){
								$('#itcOthCGST').html(response.data.itcsumm.itcavl.othersup.cgst ? formatNumber(response.data.itcsumm.itcavl.othersup.cgst.toFixed(2)) : "0.00");
								$('#itcOthSGST').html(response.data.itcsumm.itcavl.othersup.sgst ? formatNumber(response.data.itcsumm.itcavl.othersup.sgst.toFixed(2)) : "0.00");
								$('#itcOthIGST').html(response.data.itcsumm.itcavl.othersup.igst ? formatNumber(response.data.itcsumm.itcavl.othersup.igst.toFixed(2)) : "0.00");
								$('#itcOthCESS').html(response.data.itcsumm.itcavl.othersup.cess ? formatNumber(response.data.itcsumm.itcavl.othersup.cess.toFixed(2)) : "0.00");
								if(response.data.itcsumm.itcavl.othersup.cdnr != "" && response.data.itcsumm.itcavl.othersup.cdnr != null){
									$('#itcOthB2BCGST').html(response.data.itcsumm.itcavl.othersup.cdnr.cgst ? formatNumber(response.data.itcsumm.itcavl.othersup.cdnr.cgst.toFixed(2)) : "0.00");
									$('#itcOthB2BSGST').html(response.data.itcsumm.itcavl.othersup.cdnr.sgst ? formatNumber(response.data.itcsumm.itcavl.othersup.cdnr.sgst.toFixed(2)) : "0.00");
									$('#itcOthB2BIGST').html(response.data.itcsumm.itcavl.othersup.cdnr.igst ? formatNumber(response.data.itcsumm.itcavl.othersup.cdnr.igst.toFixed(2)) : "0.00");
									$('#itcOthB2BCESS').html(response.data.itcsumm.itcavl.othersup.cdnr.cess ? formatNumber(response.data.itcsumm.itcavl.othersup.cdnr.cess.toFixed(2)) : "0.00");
								}
								if(response.data.itcsumm.itcavl.othersup.cdnra != "" && response.data.itcsumm.itcavl.othersup.cdnra != null){
									$('#itcOthCDNACGST').html(response.data.itcsumm.itcavl.othersup.cdnra.cgst ? formatNumber(response.data.itcsumm.itcavl.othersup.cdnra.cgst.toFixed(2)) : "0.00");
									$('#itcOthCDNASGST').html(response.data.itcsumm.itcavl.othersup.cdnra.sgst ? formatNumber(response.data.itcsumm.itcavl.othersup.cdnra.sgst.toFixed(2)) : "0.00");
									$('#itcOthCDNAIGST').html(response.data.itcsumm.itcavl.othersup.cdnra.igst ? formatNumber(response.data.itcsumm.itcavl.othersup.cdnra.igst.toFixed(2)) : "0.00");
									$('#itcOthCDNACESS').html(response.data.itcsumm.itcavl.othersup.cdnra.cess ? formatNumber(response.data.itcsumm.itcavl.othersup.cdnra.cess.toFixed(2)) : "0.00");
								}
								if(response.data.itcsumm.itcavl.othersup.cdnrrev != "" && response.data.itcsumm.itcavl.othersup.cdnrrev != null){
									$('#itcOthCDNRCGST').html(response.data.itcsumm.itcavl.othersup.cdnrrev.cgst ? formatNumber(response.data.itcsumm.itcavl.othersup.cdnrrev.cgst.toFixed(2)) : "0.00");
									$('#itcOthCDNRSGST').html(response.data.itcsumm.itcavl.othersup.cdnrrev.sgst ? formatNumber(response.data.itcsumm.itcavl.othersup.cdnrrev.sgst.toFixed(2)): "0.00");
									$('#itcOthCDNRIGST').html(response.data.itcsumm.itcavl.othersup.cdnrrev.igst ? formatNumber(response.data.itcsumm.itcavl.othersup.cdnrrev.igst.toFixed(2)) : "0.00");
									$('#itcOthCDNRCESS').html(response.data.itcsumm.itcavl.othersup.cdnrrev.cess ? formatNumber(response.data.itcsumm.itcavl.othersup.cdnrrev.cess.toFixed(2)) : "0.00");									
								}
								if(response.data.itcsumm.itcavl.othersup.cdnrarev != "" && response.data.itcsumm.itcavl.othersup.cdnrarev != null){
									$('#itcOthCDNRACGST').html(response.data.itcsumm.itcavl.othersup.cdnrarev.cgst ? formatNumber(response.data.itcsumm.itcavl.othersup.cdnrarev.cgst.toFixed(2)) : "0.00");
									$('#itcOthCDNRASGST').html(response.data.itcsumm.itcavl.othersup.cdnrarev.sgst ? formatNumber(response.data.itcsumm.itcavl.othersup.cdnrarev.sgst.toFixed(2)) : "0.00");
									$('#itcOthCDNRAIGST').html(response.data.itcsumm.itcavl.othersup.cdnrarev.igst ? formatNumber(response.data.itcsumm.itcavl.othersup.cdnrarev.igst.toFixed(2)) : "0.00");
									$('#itcOthCDNRACESS').html(response.data.itcsumm.itcavl.othersup.cdnrarev.cess ? formatNumber(response.data.itcsumm.itcavl.othersup.cdnrarev.cess.toFixed(2)) : "0.00");									
								}
								if(response.data.itcsumm.itcavl.othersup.isd != "" && response.data.itcsumm.itcavl.othersup.isd != null){
									$('#itcOthISDCDNCGST').html(response.data.itcsumm.itcavl.othersup.isd.cgst ? formatNumber(response.data.itcsumm.itcavl.othersup.isd.cgst.toFixed(2)) : "0.00");
									$('#itcOthISDCDNSGST').html(response.data.itcsumm.itcavl.othersup.isd.sgst ? formatNumber(response.data.itcsumm.itcavl.othersup.isd.sgst.toFixed(2)): "0.00");
									$('#itcOthISDCDNIGST').html(response.data.itcsumm.itcavl.othersup.isd.igst ? formatNumber(response.data.itcsumm.itcavl.othersup.isd.igst.toFixed(2)) : "0.00");
									$('#itcOthISDCDNCESS').html(response.data.itcsumm.itcavl.othersup.isd.cess ? formatNumber(response.data.itcsumm.itcavl.othersup.isd.cess.toFixed(2)) : "0.00");									
								}
								if(response.data.itcsumm.itcavl.othersup.isda != "" && response.data.itcsumm.itcavl.othersup.isda != null){
									$('#itcOthISDCDNACGST').html(response.data.itcsumm.itcavl.othersup.isda.cgst ? formatNumber(response.data.itcsumm.itcavl.othersup.isda.cgst.toFixed(2)) : "0.00");
									$('#itcOthISDCDNASGST').html(response.data.itcsumm.itcavl.othersup.isda.sgst ? formatNumber(response.data.itcsumm.itcavl.othersup.isda.sgst.toFixed(2)) : "0.00");
									$('#itcOthISDCDNAIGST').html(response.data.itcsumm.itcavl.othersup.isda.igst ? formatNumber(response.data.itcsumm.itcavl.othersup.isda.igst.toFixed(2)) : "0.00");
									$('#itcOthISDCDNACESS').html(response.data.itcsumm.itcavl.othersup.isda.cess ? formatNumber(response.data.itcsumm.itcavl.othersup.isda.cess.toFixed(2)) : "0.00");
								}
							}
						}
						
						if(response.data.itcsumm.itcunavl != "" && response.data.itcsumm.itcunavl != null){
							if(response.data.itcsumm.itcunavl.nonrevsup != "" && response.data.itcsumm.itcunavl.nonrevsup != null){
								$('#itcunorCGST').html(response.data.itcsumm.itcunavl.nonrevsup.cgst ? formatNumber(response.data.itcsumm.itcunavl.nonrevsup.cgst.toFixed(2)) : "0.00");
								$('#itcunorSGST').html(response.data.itcsumm.itcunavl.nonrevsup.sgst ? formatNumber(response.data.itcsumm.itcunavl.nonrevsup.sgst.toFixed(2)) : "0.00");
								$('#itcunorIGST').html(response.data.itcsumm.itcunavl.nonrevsup.igst ? formatNumber(response.data.itcsumm.itcunavl.nonrevsup.igst.toFixed(2)) : "0.00");
								$('#itcunorCESS').html(response.data.itcsumm.itcunavl.nonrevsup.cess ? formatNumber(response.data.itcsumm.itcunavl.nonrevsup.cess.toFixed(2)) : "0.00");
								if(response.data.itcsumm.itcunavl.nonrevsup.b2b != "" && response.data.itcsumm.itcunavl.nonrevsup.b2b != null){
									$('#itcunorB2BCGST').html(response.data.itcsumm.itcunavl.nonrevsup.b2b.cgst ? formatNumber(response.data.itcsumm.itcunavl.nonrevsup.b2b.cgst.toFixed(2)) : "0.00");
									$('#itcunorB2BSGST').html(response.data.itcsumm.itcunavl.nonrevsup.b2b.sgst ? formatNumber(response.data.itcsumm.itcunavl.nonrevsup.b2b.sgst.toFixed(2)) : "0.00");
									$('#itcunorB2BIGST').html(response.data.itcsumm.itcunavl.nonrevsup.b2b.igst ? formatNumber(response.data.itcsumm.itcunavl.nonrevsup.b2b.igst.toFixed(2)) : "0.00");
									$('#itcunorB2BCESS').html(response.data.itcsumm.itcunavl.nonrevsup.b2b.cess ? formatNumber(response.data.itcsumm.itcunavl.nonrevsup.b2b.cess.toFixed(2)) : "0.00");
								}
								if(response.data.itcsumm.itcunavl.nonrevsup.cdnr != "" && response.data.itcsumm.itcunavl.nonrevsup.cdnr != null){
									$('#itcunorCDNCGST').html(response.data.itcsumm.itcunavl.nonrevsup.cdnr.cgst ? formatNumber(response.data.itcsumm.itcunavl.nonrevsup.cdnr.cgst.toFixed(2)) : "0.00");
									$('#itcunorCDNSGST').html(response.data.itcsumm.itcunavl.nonrevsup.cdnr.sgst ? formatNumber(response.data.itcsumm.itcunavl.nonrevsup.cdnr.sgst.toFixed(2)) : "0.00");
									$('#itcunorCDNIGST').html(response.data.itcsumm.itcunavl.nonrevsup.cdnr.igst ? formatNumber(response.data.itcsumm.itcunavl.nonrevsup.cdnr.igst.toFixed(2)) : "0.00");
									$('#itcunorCDNCESS').html(response.data.itcsumm.itcunavl.nonrevsup.cdnr.cess ? formatNumber(response.data.itcsumm.itcunavl.nonrevsup.cdnr.cess.toFixed(2)) : "0.00");
								}
								if(response.data.itcsumm.itcunavl.nonrevsup.b2ba != "" && response.data.itcsumm.itcunavl.nonrevsup.b2ba != null){
									$('#itcunorB2BACGST').html(response.data.itcsumm.itcunavl.nonrevsup.b2ba.cgst ? formatNumber(response.data.itcsumm.itcunavl.nonrevsup.b2ba.cgst.toFixed(2)) : "0.00");
									$('#itcunorB2BASGST').html(response.data.itcsumm.itcunavl.nonrevsup.b2ba.sgst ? formatNumber(response.data.itcsumm.itcunavl.nonrevsup.b2ba.sgst.toFixed(2)) : "0.00");
									$('#itcunorB2BAIGST').html(response.data.itcsumm.itcunavl.nonrevsup.b2ba.igst ? formatNumber(response.data.itcsumm.itcunavl.nonrevsup.b2ba.igst.toFixed(2)) : "0.00");
									$('#itcunorB2BACESS').html(response.data.itcsumm.itcunavl.nonrevsup.b2ba.cess ? formatNumber(response.data.itcsumm.itcunavl.nonrevsup.b2ba.cess.toFixed(2)) : "0.00");
								}
								if(response.data.itcsumm.itcunavl.nonrevsup.cdnra != "" && response.data.itcsumm.itcunavl.nonrevsup.cdnra != null){
									$('#itcunorCDNACGST').html(response.data.itcsumm.itcunavl.nonrevsup.cdnra.cgst ? formatNumber(response.data.itcsumm.itcunavl.nonrevsup.cdnra.cgst.toFixed(2)) : "0.00");
									$('#itcunorCDNASGST').html(response.data.itcsumm.itcunavl.nonrevsup.cdnra.sgst ? formatNumber(response.data.itcsumm.itcunavl.nonrevsup.cdnra.sgst.toFixed(2)) : "0.00");
									$('#itcunorCDNAIGST').html(response.data.itcsumm.itcunavl.nonrevsup.cdnra.igst ? formatNumber(response.data.itcsumm.itcunavl.nonrevsup.cdnra.igst.toFixed(2)) : "0.00");
									$('#itcunorCDNACESS').html(response.data.itcsumm.itcunavl.nonrevsup.cdnra.cess ? formatNumber(response.data.itcsumm.itcunavl.nonrevsup.cdnra.cess.toFixed(2)) : "0.00");
								}
							}
							if(response.data.itcsumm.itcunavl.nonrevsup.isdsup != "" && response.data.itcsumm.itcunavl.nonrevsup.isdsup != null){
								$('#itcunISDTotCGST').html(response.data.itcsumm.itcunavl.isdsup.cgst ? formatNumber(response.data.itcsumm.itcavl.isdsup.cgst.toFixed(2)) : "0.00");
								$('#itcunISDTotSGST').html(response.data.itcsumm.itcunavl.isdsup.sgst ? formatNumber(response.data.itcsumm.itcavl.isdsup.sgst.toFixed(2)): "0.00");
								$('#itcunISDTotIGST').html(response.data.itcsumm.itcunavl.isdsup.igst ? formatNumber(response.data.itcsumm.itcavl.isdsup.igst.toFixed(2)) : "0.00");
								$('#itcunISDTotCESS').html(response.data.itcsumm.itcunavl.isdsup.cess ? formatNumber(response.data.itcsumm.itcavl.isdsup.cess.toFixed(2)) : "0.00");
								if(response.data.itcsumm.itcunavl.nonrevsup.isdsup.isd != "" && response.data.itcsumm.itcunavl.nonrevsup.isdsup != null){
									$('#itcunISDCGST').html(response.data.itcsumm.itcunavl.isdsup.isd.cgst ? formatNumber(response.data.itcsumm.itcunavl.isdsup.isd.cgst.toFixed(2)): "0.00");
									$('#itcunISDSGST').html(response.data.itcsumm.itcunavl.isdsup.isd.sgst ? formatNumber(response.data.itcsumm.itcunavl.isdsup.isd.sgst.toFixed(2)) : "0.00");
									$('#itcunISDIGST').html(response.data.itcsumm.itcunavl.isdsup.isd.igst ? formatNumber(response.data.itcsumm.itcunavl.isdsup.isd.igst.toFixed(2)) : "0.00");
									$('#itcunISDCESS').html(response.data.itcsumm.itcunavl.isdsup.isd.cess ? formatNumber(response.data.itcsumm.itcunavl.isdsup.isd.cess.toFixed(2)) : "0.00");
								}
								if(response.data.itcsumm.itcunavl.nonrevsup.isdsup.isda != "" && response.data.itcsumm.itcunavl.nonrevsup.isdsup.isda != null){
									$('#itcunSDACGST').html(response.data.itcsumm.itcunavl.isdsup.isda.cgst ? formatNumber(response.data.itcsumm.itcunavl.isdsup.isda.cgst.toFixed(2)) : "0.00");
									$('#itcunISDASGST').html(response.data.itcsumm.itcunavl.isdsup.isda.sgst ? formatNumber(response.data.itcsumm.itcunavl.isdsup.isda.sgst.toFixed(2)) : "0.00");
									$('#itcunISDAIGST').html(response.data.itcsumm.itcunavl.isdsup.isda.igst ? formatNumber(response.data.itcsumm.itcunavl.isdsup.isda.igst.toFixed(2)) : "0.00");
									$('#itcunISDACESS').html(response.data.itcsumm.itcunavl.isdsup.isda.cess ? formatNumber(response.data.itcsumm.itcunavl.isdsup.isda.cess.toFixed(2)) : "0.00");
								}
							}
							if(response.data.itcsumm.itcunavl.revsup != "" && response.data.itcsumm.itcunavl.revsup != null){
								$('#itcunrTotCGST').html(response.data.itcsumm.itcunavl.revsup.cgst ? formatNumber(response.data.itcsumm.itcunavl.revsup.cgst.toFixed(2)) : "0.00");
								$('#itcunrTotSGST').html(response.data.itcsumm.itcunavl.revsup.sgst ? formatNumber(response.data.itcsumm.itcunavl.revsup.sgst.toFixed(2)) : "0.00");
								$('#itcunrTotIGST').html(response.data.itcsumm.itcunavl.revsup.igst ? formatNumber(response.data.itcsumm.itcunavl.revsup.igst.toFixed(2)) : "0.00");
								$('#itcunrTotCESS').html(response.data.itcsumm.itcunavl.revsup.cess ? formatNumber(response.data.itcsumm.itcunavl.revsup.cess.toFixed(2)): "0.00");
								
								if(response.data.itcsumm.itcunavl.revsup.b2b != "" && response.data.itcsumm.itcunavl.revsup.b2b != null){
									$('#itcunrB2BCGST').html(response.data.itcsumm.itcunavl.revsup.b2b.cgst ? formatNumber(response.data.itcsumm.itcunavl.revsup.b2b.cgst.toFixed(2)) : "0.00");
									$('#itcunrB2BSGST').html(response.data.itcsumm.itcunavl.revsup.b2b.sgst ? formatNumber(response.data.itcsumm.itcunavl.revsup.b2b.sgst.toFixed(2)) : "0.00");
									$('#itcunrB2BIGST').html(response.data.itcsumm.itcunavl.revsup.b2b.igst ? formatNumber(response.data.itcsumm.itcunavl.revsup.b2b.igst.toFixed(2)) : "0.00");
									$('#itcunrB2BCESS').html(response.data.itcsumm.itcunavl.revsup.b2b.cess ? formatNumber(response.data.itcsumm.itcunavl.revsup.b2b.cess.toFixed(2)) : "0.00");
								}
								if(response.data.itcsumm.itcunavl.revsup.cdnr != "" && response.data.itcsumm.itcunavl.revsup.cdnr != null){
									$('#itcunrCDNCGST').html(response.data.itcsumm.itcunavl.revsup.cdnr.cgst ? formatNumber(response.data.itcsumm.itcunavl.revsup.cdnr.cgst.toFixed(2)) : "0.00");
									$('#itcunrCDNSGST').html(response.data.itcsumm.itcunavl.revsup.cdnr.sgst ?formatNumber(response.data.itcsumm.itcunavl.revsup.cdnr.sgst.toFixed(2)): "0.00");
									$('#itcunrCDNIGST').html(response.data.itcsumm.itcunavl.revsup.cdnr.igst ? formatNumber(response.data.itcsumm.itcunavl.revsup.cdnr.igst.toFixed(2)) : "0.00");
									$('#itcunrCDNCESS').html(response.data.itcsumm.itcunavl.revsup.cdnr.cess ? formatNumber(response.data.itcsumm.itcunavl.revsup.cdnr.cess.toFixed(2)) : "0.00");
								}
								if(response.data.itcsumm.itcunavl.revsup.b2ba != "" && response.data.itcsumm.itcunavl.revsup.b2ba != null){
									$('#itcunrB2BACGST').html(response.data.itcsumm.itcunavl.revsup.b2ba.cgst ? formatNumber(response.data.itcsumm.itcunavl.revsup.b2ba.cgst.toFixed(2)) : "0.00");
									$('#itcunrB2BASGST').html(response.data.itcsumm.itcunavl.revsup.b2ba.sgst ? formatNumber(response.data.itcsumm.itcunavl.revsup.b2ba.sgst.toFixed(2)) : "0.00");
									$('#itcunrB2BAIGST').html(response.data.itcsumm.itcunavl.revsup.b2ba.igst ? formatNumber(response.data.itcsumm.itcunavl.revsup.b2ba.igst.toFixed(2)) : "0.00");
									$('#itcunrB2BACESS').html(response.data.itcsumm.itcunavl.revsup.b2ba.cess ? formatNumber(response.data.itcsumm.itcunavl.revsup.b2ba.cess.toFixed(2)) : "0.00");
								}
								if(response.data.itcsumm.itcunavl.revsup.cdnra != "" && response.data.itcsumm.itcunavl.revsup.cdnra != null){
									$('#itcunrCDNACGST').html(response.data.itcsumm.itcunavl.revsup.cdnra.cgst ? formatNumber(response.data.itcsumm.itcunavl.revsup.cdnra.cgst.toFixed(2)) : "0.00");
									$('#itcunrCDNASGST').html(response.data.itcsumm.itcunavl.revsup.cdnra.sgst ? formatNumber(response.data.itcsumm.itcunavl.revsup.cdnra.sgst.toFixed(2)) : "0.00");
									$('#itcunrCDNAIGST').html(response.data.itcsumm.itcunavl.revsup.cdnra.igst ? formatNumber(response.data.itcsumm.itcunavl.revsup.cdnra.igst.toFixed(2)) : "0.00");
									$('#itcunrCDNACESS').html(response.data.itcsumm.itcunavl.revsup.cdnra.cess ?formatNumber(response.data.itcsumm.itcunavl.revsup.cdnra.cess.toFixed(2)) : "0.00");		  
								}
							}
							if(response.data.itcsumm.itcunavl.othersup != "" && response.data.itcsumm.itcunavl.othersup != null){
								$('#itcunOthTotCGST').html(response.data.itcsumm.itcunavl.othersup.cgst ? formatNumber(response.data.itcsumm.itcunavl.othersup.cgst.toFixed(2)) : "0.00");
								$('#itcunOthTotSGST').html(response.data.itcsumm.itcunavl.othersup.sgst ? formatNumber(response.data.itcsumm.itcunavl.othersup.sgst.toFixed(2)) : "0.00");
								$('#itcunOthTotIGST').html(response.data.itcsumm.itcunavl.othersup.igst ? formatNumber(response.data.itcsumm.itcunavl.othersup.igst.toFixed(2)) : "0.00");
								$('#itcunOthTotCESS').html(response.data.itcsumm.itcunavl.othersup.cess ? formatNumber(response.data.itcsumm.itcunavl.othersup.cess.toFixed(2)) : "0.00");			  								
								if(response.data.itcsumm.itcunavl.othersup.cdnr != "" && response.data.itcsumm.itcunavl.othersup.cdnr != null){
									$('#itcunOthB2BCGST').html(response.data.itcsumm.itcunavl.othersup.cdnr.cgst ? formatNumber(response.data.itcsumm.itcunavl.othersup.cdnr.cgst.toFixed(2)) : "0.00");
									$('#itcunOthB2BSGST').html(response.data.itcsumm.itcunavl.othersup.cdnr.sgst ? formatNumber(response.data.itcsumm.itcunavl.othersup.cdnr.sgst.toFixed(2)) : "0.00");
									$('#itcunOthB2BIGST').html(response.data.itcsumm.itcunavl.othersup.cdnr.igst ? formatNumber(response.data.itcsumm.itcunavl.othersup.cdnr.igst.toFixed(2)) : "0.00");
									$('#itcunOthB2BCESS').html(response.data.itcsumm.itcunavl.othersup.cdnr.cess ? formatNumber(response.data.itcsumm.itcunavl.othersup.cdnr.cess.toFixed(2)) : "0.00");
								}
								if(response.data.itcsumm.itcunavl.othersup.cdnra != "" && response.data.itcsumm.itcunavl.othersup.cdnra != null){
									$('#itcunOthCDNACGST').html(response.data.itcsumm.itcunavl.othersup.cdnra.cgst ? formatNumber(response.data.itcsumm.itcunavl.othersup.cdnra.cgst.toFixed(2)) : "0.00");
									$('#itcunOthCDNASGST').html(response.data.itcsumm.itcunavl.othersup.cdnra.sgst ? formatNumber(response.data.itcsumm.itcunavl.othersup.cdnra.sgst.toFixed(2)) : "0.00");
									$('#itcunOthCDNAIGST').html(response.data.itcsumm.itcunavl.othersup.cdnra.igst ? formatNumber(response.data.itcsumm.itcunavl.othersup.cdnra.igst.toFixed(2)) : "0.00");
									$('#itcunOthCDNACESS').html(response.data.itcsumm.itcunavl.othersup.cdnra.cess ? formatNumber(response.data.itcsumm.itcunavl.othersup.cdnra.cess.toFixed(2)) : "0.00");
								}
								if(response.data.itcsumm.itcunavl.othersup.cdnrrev != "" && response.data.itcsumm.itcunavl.othersup.cdnrrev != null){
									$('#itcunOthCDNCGST').html(response.data.itcsumm.itcunavl.othersup.cdnrrev.cgst ? formatNumber(response.data.itcsumm.itcunavl.othersup.cdnrrev.cgst.toFixed(2)) : "0.00");
									$('#itcunOthCDNSGST').html(response.data.itcsumm.itcunavl.othersup.cdnrrev.sgst ? formatNumber(response.data.itcsumm.itcunavl.othersup.cdnrrev.sgst.toFixed(2)): "0.00");
									$('#itcunOthCDNIGST').html(response.data.itcsumm.itcunavl.othersup.cdnrrev.igst ? formatNumber(response.data.itcsumm.itcunavl.othersup.cdnrrev.igst.toFixed(2)) : "0.00");
									$('#itcunOthCDNCESS').html(response.data.itcsumm.itcunavl.othersup.cdnrrev.cess ? formatNumber(response.data.itcsumm.itcunavl.othersup.cdnrrev.cess.toFixed(2)) : "0.00");
								}
								if(response.data.itcsumm.itcunavl.othersup.cdnrarev != "" && response.data.itcsumm.itcunavl.othersup.cdnrarev != null){
									$('#itcunOthCDNRACGST').html(response.data.itcsumm.itcunavl.othersup.cdnrarev.cgst ? formatNumber(response.data.itcsumm.itcunavl.othersup.cdnrarev.cgst.toFixed(2)) : "0.00");
									$('#itcunOthCDNRASGST').html(response.data.itcsumm.itcunavl.othersup.cdnrarev.sgst ? formatNumber(response.data.itcsumm.itcunavl.othersup.cdnrarev.sgst.toFixed(2)) : "0.00");
									$('#itcunOthCDNRAIGST').html(response.data.itcsumm.itcunavl.othersup.cdnrarev.igst ? formatNumber(response.data.itcsumm.itcunavl.othersup.cdnrarev.igst.toFixed(2)) : "0.00");
									$('#itcunOthCDNRACESS').html(response.data.itcsumm.itcunavl.othersup.cdnrarev.cess ? formatNumber(response.data.itcsumm.itcunavl.othersup.cdnrarev.cess.toFixed(2)) : "0.00");
								}
								if(response.data.itcsumm.itcunavl.othersup.isd != "" && response.data.itcsumm.itcunavl.othersup.isd != null){
									$('#itcunOthISDCGST').html(response.data.itcsumm.itcunavl.othersup.isd.cgst ? formatNumber(response.data.itcsumm.itcunavl.othersup.isd.cgst.toFixed(2)) : "0.00");
									$('#itcunOthISDSGST').html(response.data.itcsumm.itcunavl.othersup.isd.sgst ? formatNumber(response.data.itcsumm.itcunavl.othersup.isd.sgst.toFixed(2)): "0.00");
									$('#itcunOthISDIGST').html(response.data.itcsumm.itcunavl.othersup.isd.igst ? formatNumber(response.data.itcsumm.itcunavl.othersup.isd.igst.toFixed(2)) : "0.00");
									$('#itcunOthISDCESS').html(response.data.itcsumm.itcunavl.othersup.isd.cess ? formatNumber(response.data.itcsumm.itcunavl.othersup.isd.cess.toFixed(2)) : "0.00");
								}
								if(response.data.itcsumm.itcunavl.othersup.isda != "" && response.data.itcsumm.itcunavl.othersup.isda != null){
									$('#itcunOthISDACGST').html(response.data.itcsumm.itcunavl.othersup.isda.cgst ? formatNumber(response.data.itcsumm.itcunavl.othersup.isda.cgst.toFixed(2)) : "0.00");
									$('#itcunOthISDASGST').html(response.data.itcsumm.itcunavl.othersup.isda.sgst ? formatNumber(response.data.itcsumm.itcunavl.othersup.isda.sgst.toFixed(2)) : "0.00");
									$('#itcunOthISDAIGST').html(response.data.itcsumm.itcunavl.othersup.isda.igst ? formatNumber(response.data.itcsumm.itcunavl.othersup.isda.igst.toFixed(2)) : "0.00");
									$('#itcunOthISDACESS').html(response.data.itcsumm.itcunavl.othersup.isda.cess ? formatNumber(response.data.itcsumm.itcunavl.othersup.isda.cess.toFixed(2)) : "0.00");
								}
							}
						}
					}
				}
				allTablesIgnoreAllDetails(response.data.docdata);
			}else{
				$('#datatableb2b,#datatableb2ba,#datatablecdnr,#datatablecdnra,#datatableisd,#datatableisda,#datatableimpg,#datatableimpgsez').DataTable({
					"dom": '<"toolbar"f>lrtip<"clear">',
					"paging": true,
					"orderClasses": false,
					"searching": true,
					"lengthMenu": [ [10, 25, 50,100, 500], [10, 25, 50,100, 500] ],
					"responsive": true
				});
			}
		},
		error : function(errData) {
		}
	});
	$.ajax({
		url: _getContextPath()+"/cp_users/"+id+"/"+clientId,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			if (response.length > 0) {
				$("#multiselectUsers").append($("<option></option>").attr("value",globaluser).text(globaluser)); 
				response.forEach(function(cp_user) {
					$("#multiselectUsers").append($("<option></option>").attr("value",cp_user.name).text(cp_user.name));
				});
			}else{
				$("#multiselectUsers").append($("<option></option>").attr("value",globaluser).text(globaluser)); 
			}
			$('#multiselectUsers').multiselect({
				nonSelectedText: '- Users -',
				includeSelectAllOption: true,
				onChange: function(element, checked) {
					applyGSTR2BFilters();
				},
				onSelectAll: function() {
					applyGSTR2BFilters();
				},
				onDeselectAll: function() {
					applyGSTR2BFilters();
				}
		    });
		},error:function(err){}
	});
	var prlStr = _getContextPath()+'/getGstr2bData/'+id+'/'+clientId+'/'+month+'/'+year;
	$.ajax({
		url: prlStr,
		async: true,
		cache: false,
		//dataType:"json",
		contentType: 'application/json',
		success :  function(gstr2bDataList) {
			var content='';
			transCount = 0; totalValue = 0; totalTax = 0; totalTaxableValue = 0; totalIGST = 0; totalCGST = 0; totalSGST = 0; totalCESS = 0;
			if(gstr2bDataList instanceof Array) {
				if(gstr2bDataList.length > 0) {
					var counts = 0;
					var custnames = [];
					gstr2bDataList.forEach(function(invoice){
						invoice = updateGstr2bInvoiceDetails(invoice);
						if(invoice.billedtoname) {
							if(counts == 0){
								custnames.push(invoice.billedtoname);
								$("#multiselectSuppliers").append($("<option></option>").attr("value",invoice.billedtoname).text(invoice.billedtoname));
							}
							if(jQuery.inArray( invoice.billedtoname, custnames ) == -1){
								custnames.push(invoice.billedtoname);
								$("#multiselectSuppliers").append($("<option></option>").attr("value",invoice.billedtoname).text(invoice.billedtoname));
							}
						}
						content += '<tr>';
						content += '<td class="text-left"><span>'+invoice.invtype+'</span></td><td class="text-left"><span>'+invoice.invoiceno+'</span></td><td class="text-left"><span>'+invoice.billedtoname+'</span></td><td class="text-left"><span>'+invoice.gstin+'</span></td><td class="text-left"><span>'+invoice.dateofinvoice+'</span></td><td class="text-right"><span class="ind_formats">'+formatNumber(invoice.totaltaxableamount.toFixed(2))+'</span></td><td class="text-right"><span class="ind_formats" id="tot_tax">'+formatNumber(invoice.totaltax.toFixed(2))+'<div id="tax_tot_drop1" style="display:none"><div id="drop-tottax"></div><h6 style="text-align: center;" class="mb-2">TAX AMOUNT</h6><div class="row pl-3" style="height:25px"><p class="mr-3">IGST <span style="margin-left:5px">:<span></p><span><label class="dropdown taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(invoice.igstamount.toFixed(2))+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">CGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(invoice.cgstamount.toFixed(2))+'</label></span></div><div class="row pl-3" style="height:25px"><p class="mr-3">SGST :</p><span><label class="taxindformat" id="" name="" style="border:none;padding-top: 2px;background: none;">'+formatNumber(invoice.sgstamount.toFixed(2))+'</label></span></div></div></span></td><td class="text-right"><span class="ind_formats">'+formatNumber(invoice.totalamount.toFixed(2))+'</span></td>';
						content += '</tr>';
						counts++;
						transCount++;
					});
				}
				gstrInvoiceArray = gstr2bDataList;
				$('#dbTablegstr2bBody').html(content);	
			}else{
				gstrInvoiceArray = new Object();
			}
			$("#multiselectSuppliers").multiselect('rebuild');
			gstr2bDbTable = $('#dbTablegstr2b').DataTable({
				"dom": '<"toolbar"f>lrtip<"clear">',
				"paging": true,
				"orderClasses": false,
				"searching": true,
				"lengthMenu": [ [10, 25, 50,100, 500], [10, 25, 50,100, 500] ],
				"responsive": true
			});
			$('#idCountGSTR2B').html(transCount);
			$('#idIGSTGSTR2B').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(totalIGST).toFixed(2)));
			$('#idCGSTGSTR2B').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(totalCGST).toFixed(2)));
			$('#idSGSTGSTR2B').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(totalSGST).toFixed(2)));
			$('#idCESSGSTR2B').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(totalCESS).toFixed(2)));
			$('#idTaxableValGSTR2B').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(totalTaxableValue).toFixed(2)));
			$('#idTaxValGSTR2B').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(totalTax).toFixed(2)));
			$('#idTotAmtValGSTR2B').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(totalValue).toFixed(2)));
		},
		error : function(errData) {
			gstrInvoiceArray = new Object();
			console.log(errData);
			var content='';
			transCount = 0; totalValue = 0; totalTax = 0; totalTaxableValue = 0; totalIGST = 0; totalCGST = 0; totalSGST = 0; totalCESS = 0;
			gstr2bDbTable = $('#dbTablegstr2b').DataTable({
				"dom": '<"toolbar"f>lrtip<"clear">',
				"paging": true,
				"orderClasses": false,
				"searching": true,
				"lengthMenu": [ [10, 25, 50,100, 500], [10, 25, 50,100, 500] ],
				"responsive": true
			});
			$('#idCountGSTR2B').html(transCount);
			$('#idIGSTGSTR2B').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(totalIGST).toFixed(2)));
			$('#idCGSTGSTR2B').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(totalCGST).toFixed(2)));
			$('#idSGSTGSTR2B').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(totalSGST).toFixed(2)));
			$('#idCESSGSTR2B').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(totalCESS).toFixed(2)));
			$('#idTaxableValGSTR2B').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(totalTaxableValue).toFixed(2)));
			$('#idTaxValGSTR2B').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(totalTax).toFixed(2)));
			$('#idTotAmtValGSTR2B').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(totalValue).toFixed(2)));
		}
	});
	$('#multiselectInvType').multiselect({
		nonSelectedText: '- Invoice Type -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyGSTR2BFilters();
		},
		onSelectAll: function() {
			applyGSTR2BFilters();
		},
		onDeselectAll: function() {
			applyGSTR2BFilters();
		}
    });
	$('#multiselectSuppliers').multiselect({
		nonSelectedText: '- Suppliers -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyGSTR2BFilters();
		},
		onSelectAll: function() {
			applyGSTR2BFilters();
		},
		onDeselectAll: function() {
			applyGSTR2BFilters();
		}
    });
	$('#multiselectReverseCharge').multiselect({
		nonSelectedText: '- Reverse Charge -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyGSTR2BFilters();
		},
		onSelectAll: function() {
			applyGSTR2BFilters();
		},
		onDeselectAll: function() {
			applyGSTR2BFilters();
		}
    });
	$('#multiselectITCAvailablity').multiselect({
		nonSelectedText: '- ITC Type -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyGSTR2BFilters();
		},
		onSelectAll: function() {
			applyGSTR2BFilters();
		},
		onDeselectAll: function() {
			applyGSTR2BFilters();
		}
    });	
}

function applyGSTR2BFilters(){
	var typeOptions = $('#multiselectInvType option:selected');
	var userOptions = $('#multiselectUsers option:selected');
	var vendorOptions = $('#multiselectSuppliers option:selected');
	var reverseChargeOptions = $('#multiselectReverseCharge option:selected');
	var itcAvailablityOptions = $('#multiselectITCAvailablity option:selected');
	gstr2bDbTable.clear();
	if(typeOptions.length > 0 || userOptions.length > 0 || vendorOptions.length > 0 || reverseChargeOptions.length > 0 || itcAvailablityOptions.length > 0){
		$('.normaltable .filter').css("display","block");
	}else{
		$('.normaltable .filter').css("display","none");
	}
	var typeArr=new Array();
	var userArr=new Array();
	var vendorArr=new Array();
	var reverseChargeArr=new Array();
	var itcAvailablityArr=new Array();
	var filterContent='';
	if(typeOptions.length > 0) {
		for(var i=0;i<typeOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+typeOptions[i].text+'<span data-val="'+typeOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			typeArr.push(typeOptions[i].value);
		}
	} else {
		typeArr.push('All');
	}
	if(userOptions.length > 0) {
		for(var i=0;i<userOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+userOptions[i].value+'<span data-val="'+userOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			userArr.push(userOptions[i].value);
		}
	} else {
		userArr.push('All');
	}
	if(vendorOptions.length > 0) {
		for(var i=0;i<vendorOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+vendorOptions[i].value+'<span data-val="'+vendorOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			vendorArr.push(vendorOptions[i].value);
		}
	} else {
		vendorArr.push('All');
	}
	if(reverseChargeOptions.length > 0) {
		for(var i=0;i<reverseChargeOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+reverseChargeOptions[i].text+'<span data-val="'+reverseChargeOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			//filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+reverseChargeOptions[i].text+'<span data-val="'+reverseChargeOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			reverseChargeArr.push(reverseChargeOptions[i].value);
		}
	} else {
		reverseChargeArr.push('All');
	}
	if(itcAvailablityOptions.length > 0) {
		for(var i=0;i<itcAvailablityOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+itcAvailablityOptions[i].text+'<span data-val="'+itcAvailablityOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			//filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+itcAvailablityOptions[i].text+'<span data-val="'+itcAvailablityOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			itcAvailablityArr.push(itcAvailablityOptions[i].value);
		}
	} else {
		itcAvailablityArr.push('All');
	}
	$('#divFiltersGstr2b').html(filterContent);
	commonGstr2bInvoiceFilter(typeArr, userArr, vendorArr, reverseChargeArr, itcAvailablityArr);
	gstr2bDbTable.draw();
}
function clearGstr2bFilters() {
	$('.gstr2bfilter .multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
	$('.normaltable .filter').css("display","none");
	gstr2bDbTable.clear();
	commonGstr2bInvoiceFilter(new Array(), new Array(), new Array(), new Array(), new Array());	
	gstr2bDbTable.draw();
}

function commonGstr2bInvoiceFilter(arrInvType, arrUser, arrVendor, arrReverseCharge, arrItcAvailablity) {
	if(gstrInvoiceArray.length > 0) {
		var rows = new Array();
		var taxArray = new Array();
		var rowNode;
		gstrInvoiceArray.forEach(function(invoice) {
			if(invoice.billedtoname == "" || invoice.billedtoname == undefined){
				invoice.billedtoname = "";
			}
			if(invoice.gstin == "" || invoice.gstin == undefined){
				invoice.gstin = "";
			}
			if(invoice.fullname == "" || invoice.fullname == undefined){
				invoice.fullname = "";
			}
			var rowData = [invoice.invtype, invoice.invoiceno, invoice.billedtoname, invoice.gstin, invoice.dateofinvoice, formatNumber(invoice.totaltaxableamount.toFixed(2)), formatNumber(invoice.totaltax.toFixed(2)), formatNumber(invoice.totalamount.toFixed(2)), invoice.revchargetype, invoice.itcavl, invoice.fullname];
			rows.push(rowData);
			taxArray.push([invoice.igstamount,invoice.cgstamount,invoice.sgstamount,invoice.cessamount,invoice.totaltaxableamount,invoice.totaltax,invoice.totalamount]);
		});
		var index = 0, transCount=0, tIGST=0, tCGST=0, tSGST=0, tCESS=0, tTaxableAmount=0, tTotalTax=0, tTotalAmount=0;
		rows.forEach(function(row) {
		  if((arrInvType.length == 0 || $.inArray('All', arrInvType) >= 0 || $.inArray(row[0], arrInvType) >= 0)
				&& (arrUser.length == 0 || $.inArray('All', arrUser) >= 0 || $.inArray(row[10], arrUser) >= 0)
				&& (arrVendor.length == 0 || $.inArray('All', arrVendor) >= 0 || $.inArray(row[3], arrVendor) >= 0)
				&& (arrReverseCharge.length == 0 || $.inArray('All', arrReverseCharge) >= 0 || $.inArray(row[8], arrReverseCharge) >= 0)
				&& (arrItcAvailablity.length == 0 || $.inArray('All', arrItcAvailablity) >= 0 || $.inArray(row[9], arrItcAvailablity) >= 0)) {
			  	row[5] = "<span class='ind_formats text-right'>"+row[5]+"</span>";
				row[6] = "<span class='ind_formats text-right'>"+row[6]+"</span>";
				row[7] = "<span class='ind_formats text-right'>"+row[7]+"</span>";
				rowNode = gstr2bDbTable.row.add(row);
				gstr2bDbTable.row(rowNode).column(5).nodes().to$().addClass('text-right');
				gstr2bDbTable.row(rowNode).column(6).nodes().to$().addClass('text-right');
				gstr2bDbTable.row(rowNode).column(7).nodes().to$().addClass('text-right');
				transCount++;
				if(row[0] == 'Credit Note' || row[0] == 'Credit Note(CDNA)'){
					tIGST-=parseFloat(taxArray[index][0]);
					tCGST-=parseFloat(taxArray[index][1]);
					tSGST-=parseFloat(taxArray[index][2]);
					tCESS-=parseFloat(taxArray[index][3]);
					tTaxableAmount-=parseFloat(taxArray[index][4]);
					tTotalTax-=parseFloat(taxArray[index][5]);
					tTotalAmount-=parseFloat(taxArray[index][6]);
				}else{
					tIGST+=parseFloat(taxArray[index][0]);
					tCGST+=parseFloat(taxArray[index][1]);
					tSGST+=parseFloat(taxArray[index][2]);
					tCESS+=parseFloat(taxArray[index][3]);
					tTaxableAmount+=parseFloat(taxArray[index][4]);
					tTotalTax+=parseFloat(taxArray[index][5]);
					tTotalAmount+=parseFloat(taxArray[index][6]);
				}
		  }
		  index++;
		});
		$('#idCountGSTR2B').html(transCount);
		$('#idIGSTGSTR2B').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tIGST).toFixed(2)));
		$('#idCGSTGSTR2B').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tCGST).toFixed(2)));
		$('#idSGSTGSTR2B').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tSGST).toFixed(2)));
		$('#idCESSGSTR2B').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tCESS).toFixed(2)));
		$('#idTaxableValGSTR2B').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tTaxableAmount).toFixed(2)));
		$('#idTaxValGSTR2B').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tTotalTax).toFixed(2)));
		$('#idTotAmtValGSTR2B').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tTotalAmount).toFixed(2)));
	}
}

function updateGstr2bInvoiceDetails(invoice) {
	var totalIGST1 = 0, totalCGST1 = 0, totalSGST1 = 0, totalCESS1 = 0;
		invoice.id = invoice.userid;
		invoice.invoicetype = invoice.invtype;
		if(invoice.invoiceno == null){
			invoice.invoiceno = '';
		}
		invoice.serialnoofinvoice = invoice.invoiceno;
		invoice.billedtogstin = invoice.gstin;
		
		var invDate = new Date(invoice.dateofinvoice);
		var day = invDate.getDate() + "";
		var inv_month = (invDate.getMonth() + 1) + "";
		var year = invDate.getFullYear() + "";
		day = checkZero(day);
		inv_month = checkZero(inv_month);
		year = checkZero(year);
		invoice.dateofinvoice = day + "/" + inv_month + "/" + year;
		
		if(invoice.gstin == null) {
			invoice.gstin = '';
		}
		
		if(invoice.revchargetype == null) {
			invoice.revchargetype = 'N';
		}
		if(invoice.itcval == null) {
			invoice.itcavl = 'N';
		}
		
		if(invoice.billedtoname == null) {
			invoice.billedtoname = '';
		}
		
		if(invoice.totaltaxableamount) {
			if(invoice.invtype == 'Credit Note' || invoice.invtype == 'Credit Note(CDNA)') {
				totalTaxableValue-=invoice.totaltaxableamount;
			}else{
				totalTaxableValue+=invoice.totaltaxableamount;
			}
		} else {
			invoice.totaltaxableamount = 0.00;
		}
		if(invoice.totalitc) {
			if(invoice.invtype == 'Credit Note' || invoice.invtype == 'Credit Note(CDNA)') {
				totalITC-=invoice.totalitc;	
			}else{
				totalITC+=invoice.totalitc;				
			}
		} else {
			invoice.totalitc = 0.00;
		}
		if(invoice.totaltax) {
			if(invoice.invtype == 'Credit Note' || invoice.invtype == 'Credit Note(CDNA)') {
				totalTax-=invoice.totaltax;
			}else{
				totalTax+=invoice.totaltax;
			}
		} else {
			invoice.totaltax = 0.00;
		}
		if(invoice.totalamount) {
			if(invoice.invtype == 'Credit Note' || invoice.invtype == 'Credit Note(CDNA)') {
				totalValue-=invoice.totalamount;
			}else{
				totalValue+=invoice.totalamount;
			}
		} else {
			invoice.totalamount = 0.00;
		}
				
		if(invoice.items) {
			invoice.items.forEach(function(item) {
				if(item.rate == null) {
					if(item.igstrate) {
						item.rate = item.igstrate;
					} else if(item.cgstrate) {
						item.rate = 2*item.cgstrate;
					}
				}
				if(item.hsn) {
					item.code = item.hsn;
					if(item.hsn.indexOf(':') > 0) {
						item.hsn=item.hsn.substring(0,item.hsn.indexOf(':'));
					}
				}
				if(item.igstamount) {
					if(invoice.invtype == 'Credit Note' || invoice.invtype == 'Credit Note(CDNA)') {
						totalIGST1 +=item.igstamount;
						totalIGST -=item.igstamount;
					}else{
						totalIGST1 +=item.igstamount;
						totalIGST +=item.igstamount;
					}
				} else {
				item.igstamount = 0.00;
			}
			if(item.cgstamount) {
				if(invoice.invtype == 'Credit Note' || invoice.invtype == 'Credit Note(CDNA)') {
					totalCGST1 +=item.cgstamount;
					totalCGST -=item.cgstamount;
				}else{
					totalCGST1 +=item.cgstamount;
					totalCGST +=item.cgstamount;
				}
			} else {
				item.cgstamount = 0.00;
			}
			if(item.sgstamount) {
				if(invoice.invtype == 'Credit Note' || invoice.invtype == 'Credit Note(CDNA)') {
					totalSGST1 +=item.sgstamount;
					totalSGST -=item.sgstamount;
				}else{
					totalSGST1 +=item.sgstamount;
					totalSGST +=item.sgstamount;
				}
			} else {
				item.sgstamount = 0.00;
			}
			if(item.cessamount) {
				if(invoice.invtype == 'Credit Note' || invoice.invtype == 'Credit Note(CDNA)') {
					totalCESS1 +=item.cessamount;
					totalCESS -=item.cessamount;
				}else{
					totalCESS1 +=item.cessamount;
					totalCESS +=item.cessamount;					
				}
			} else {
				item.cessamount = 0.00;
			}
			if(item.discount == null) {
				item.discount = 0.00;
			}
			if(item.advreceived == null) {
				item.advreceived = 0.00;
			}
			if(item.type) {
				
			} else {
				item.type = '';
			}
		});
	}
	invoice.igstamount = totalIGST1;
	invoice.cgstamount = totalCGST1;
	invoice.sgstamount = totalSGST1;
	invoice.cessamount = totalCESS1;
	return invoice;
}

function allTablesIgnoreAllDetails(docData){
	var b2bContent = '', b2baContent = '', cdnrContent = '', cdnraContent = '', isdContent = '', isdaContent = '', impgContent = '', impgsezContent = '';
	var emptyVal ="";
	if(docData != "" && docData != null && docData != undefined){
	
		if(docData.b2b != "" && docData.b2b != null && docData.b2b != undefined){
			if(docData.b2b.length > 0){
				
				for(var i=0; i < docData.b2b.length; i++){
					if(docData.b2b[i].inv.length > 0){
						for(var j=0; j < docData.b2b[i].inv.length; j++){
							b2bContent += '<tr>';
							b2bContent += '<td class="text-left"><span>B2B</span>';
							var ctin = '', trdnm ='', gstinFiledPrd = '', gstinFiledDate ='';
							if(docData.b2b[i].ctin != null){
								ctin = docData.b2b[i].ctin;
							}
							if(docData.b2b[i].trdnm != null){
								trdnm = docData.b2b[i].trdnm;
							}
							var ctinTrdnmContent =  '</td><td class="text-left"><span>'+ctin+'</span></td>';
							ctinTrdnmContent += '</td><td class="text-left"><span>'+trdnm+'</span></td>';
							
							if(docData.b2b[i].supprd != null){
								gstinFiledPrd = docData.b2b[i].supprd;
							}
							if(docData.b2b[i].supfildt != null){
								gstinFiledDate = docData.b2b[i].supfildt;
							}
							var gstinFiledInfoContent =  '</td><td class="text-left"><span>'+gstinFiledPrd+'</span></td>';
							gstinFiledInfoContent += '</td><td class="text-left"><span>'+gstinFiledDate+'</span></td>';
							if(docData.b2b[i].inv[j].inum != null){
								b2bContent += '</td><td class="text-left"><span>'+docData.b2b[i].inv[j].inum+'</span></td>';
							}else{
								b2bContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							
							b2bContent += ctinTrdnmContent;
							
							if(docData.b2b[i].inv[j].dt != null){
								b2bContent += '</td><td class="text-left"><span>'+docData.b2b[i].inv[j].dt+'</span></td>';
							}else{
								b2bContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							if(docData.b2b[i].inv[j].pos != null){
								b2bContent += '</td><td class="text-left"><span>'+docData.b2b[i].inv[j].pos+'</span></td>';
							}else{
								b2bContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							if(docData.b2b[i].inv[j].rev != null){
								b2bContent += '</td><td class="text-left"><span>'+docData.b2b[i].inv[j].rev+'</span></td>';
							}else{
								b2bContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							if(docData.b2b[i].inv[j].val != null){
								b2bContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(docData.b2b[i].inv[j].val).toFixed(2))+'</span></td>';
							}else{
								b2bContent += '</td><td class="text-right"><span>'+0.0+'</span></td>';
							}
							
							var igst = 0.0, cgst = 0.0, sgst = 0.0 ,rt = 0.0, cess=0.0, txval=0.0;
							if(docData.b2b[i].inv[j].items != "" && docData.b2b[i].inv[j].items != null){
								if(docData.b2b[i].inv[j].items.length> 0){
									for(var k=0 ; k<docData.b2b[i].inv[j].items.length ; k++){
										if(docData.b2b[i].inv[j].items[k].igst != null){
											igst += docData.b2b[i].inv[j].items[k].igst;
										}
										if(docData.b2b[i].inv[j].items[k].cgst != null){
											cgst += docData.b2b[i].inv[j].items[k].cgst;
										}
										if(docData.b2b[i].inv[j].items[k].sgst != null){
											sgst += docData.b2b[i].inv[j].items[k].sgst;
										}
										if(docData.b2b[i].inv[j].items[k].cess != null){
											cess += docData.b2b[i].inv[j].items[k].cess;
										}
										if(docData.b2b[i].inv[j].items[k].txval != null){
											txval += docData.b2b[i].inv[j].items[k].txval;
										}
									}
								}
							}
							
							b2bContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(txval).toFixed(2))+'</span></td>';
							b2bContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(igst).toFixed(2))+'</span></td>';
							b2bContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(cgst).toFixed(2))+'</span></td>';
							b2bContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(sgst).toFixed(2))+'</span></td>';
							b2bContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(cess).toFixed(2))+'</span></td>';
							
							b2bContent += gstinFiledInfoContent;
							
							if(docData.b2b[i].inv[j].itcavl != null){
								b2bContent += '</td><td class="text-left"><span>'+docData.b2b[i].inv[j].itcavl+'</span></td>';
							}else{
								b2bContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							if(docData.b2b[i].inv[j].rsn != null){
								b2bContent += '</td><td class="text-left"><span>'+docData.b2b[i].inv[j].rsn+'</span></td>';
							}else{
								b2bContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							if(docData.b2b[i].inv[j].diffprcnt != null){
								b2bContent += '</td><td class="text-left"><span>'+docData.b2b[i].inv[j].diffprcnt+'</span></td>';
							}else{
								b2bContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
						b2bContent += '</tr>';
						}
					}
					
				}	
			}
		}
		
		if(docData.b2ba != "" && docData.b2ba != null && docData.b2ba != undefined){
			if(docData.b2ba.length > 0){
				
				for(var i=0; i < docData.b2ba.length; i++){
					if(docData.b2ba[i].inv.length > 0){
						for(var j=0; j < docData.b2ba[i].inv.length; j++){
							b2baContent += '<tr>';
							b2baContent += '<td class="text-left"><span>B2BA</span>';
							var ctin = '', trdnm ='', gstinFiledPrd = '', gstinFiledDate ='';
							if(docData.b2ba[i].ctin != null){
								ctin = docData.b2ba[i].ctin;
							}
							if(docData.b2ba[i].trdnm != null){
								trdnm = docData.b2ba[i].trdnm;
							}
							var ctinTrdnmContent =  '</td><td class="text-left"><span>'+ctin+'</span></td>';
							ctinTrdnmContent += '</td><td class="text-left"><span>'+trdnm+'</span></td>';
							
							if(docData.b2ba[i].supprd != null){
								gstinFiledPrd = docData.b2ba[i].supprd;
							}
							if(docData.b2ba[i].supfildt != null){
								gstinFiledDate += docData.b2ba[i].supfildt;
							}
							var gstinFiledInfoContent =  '</td><td class="text-left"><span>'+gstinFiledPrd+'</span></td>';
							gstinFiledInfoContent += '</td><td class="text-left"><span>'+gstinFiledDate+'</span></td>';
							if(docData.b2ba[i].inv[j].inum != null){
								b2baContent += '</td><td class="text-left"><span>'+docData.b2ba[i].inv[j].inum+'</span></td>';
							}else{
								b2baContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							
							b2baContent += ctinTrdnmContent;
							
							if(docData.b2ba[i].inv[j].dt != null){
								b2baContent += '</td><td class="text-left"><span>'+docData.b2ba[i].inv[j].dt+'</span></td>';
							}else{
								b2baContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							if(docData.b2ba[i].inv[j].pos != null){
								b2baContent += '</td><td class="text-left"><span>'+docData.b2ba[i].inv[j].pos+'</span></td>';
							}else{
								b2baContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							if(docData.b2ba[i].inv[j].rev != null){
								b2baContent += '</td><td class="text-left"><span>'+docData.b2ba[i].inv[j].rev+'</span></td>';
							}else{
								b2baContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							
							if(docData.b2ba[i].inv[j].val != null){
								b2baContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(docData.b2ba[i].inv[j].val).toFixed(2))+'</span></td>';
							}else{
								b2baContent += '</td><td class="text-right"><span>'+0.0+'</span></td>';
							}
							
							var igst = 0.0, cgst = 0.0, sgst = 0.0 ,rt = 0.0, cess=0.0, txval=0.0;
							if(docData.b2ba[i].inv[j].items != "" && docData.b2ba[i].inv[j].items != null){
								if(docData.b2ba[i].inv[j].items.length> 0){
									for(var k=0 ; k<docData.b2ba[i].inv[j].items.length ; k++){
										if(docData.b2ba[i].inv[j].items[k].igst != null){
											igst += docData.b2ba[i].inv[j].items[k].igst;
										}
										if(docData.b2ba[i].inv[j].items[k].cgst != null){
											cgst += docData.b2ba[i].inv[j].items[k].cgst;
										}
										if(docData.b2ba[i].inv[j].items[k].sgst != null){
											sgst += docData.b2ba[i].inv[j].items[k].sgst;
										}
										if(docData.b2ba[i].inv[j].items[k].cess != null){
											cess += docData.b2ba[i].inv[j].items[k].cess;
										}
										if(docData.b2ba[i].inv[j].items[k].txval != null){
											txval += docData.b2ba[i].inv[j].items[k].txval;
										}
									}
								}
							}
							
							b2baContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(txval).toFixed(2))+'</span></td>';
							b2baContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(igst).toFixed(2))+'</span></td>';
							b2baContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(cgst).toFixed(2))+'</span></td>';
							b2baContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(sgst).toFixed(2))+'</span></td>';
							b2baContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(cess).toFixed(2))+'</span></td>';
							
							b2baContent += gstinFiledInfoContent;
							
							if(docData.b2ba[i].inv[j].itcavl != null){
								b2baContent += '</td><td class="text-left"><span>'+docData.b2ba[i].inv[j].itcavl+'</span></td>';
							}else{
								b2baContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							if(docData.b2ba[i].inv[j].rsn != null){
								b2baContent += '</td><td class="text-left"><span>'+docData.b2ba[i].inv[j].rsn+'</span></td>';
							}else{
								b2baContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							if(docData.b2ba[i].inv[j].diffprcnt != null){
								b2baContent += '</td><td class="text-left"><span>'+docData.b2ba[i].inv[j].diffprcnt+'</span></td>';
							}else{
								b2baContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							b2baContent += '</tr>';
						}
					}
									
				}	
			}
		}
		
		if(docData.cdnr != "" && docData.cdnr != null && docData.cdnr != undefined){
			if(docData.cdnr.length > 0){
				
				for(var i=0; i < docData.cdnr.length; i++){
					if(docData.cdnr[i].nt.length > 0){
						for(var j=0; j < docData.cdnr[i].nt.length; j++){
							cdnrContent += '<tr>';
					
							var ctin = '', trdnm ='', gstinFiledPrd = '', gstinFiledDate ='';
							if(docData.cdnr[i].ctin != null){
								ctin = docData.cdnr[i].ctin;
							}
							if(docData.cdnr[i].trdnm != null){
								trdnm = docData.cdnr[i].trdnm;
							}
							var ctinTrdnmContent =  '</td><td class="text-left"><span>'+ctin+'</span></td>';
							ctinTrdnmContent += '</td><td class="text-left"><span>'+trdnm+'</span></td>';
							
							if(docData.cdnr[i].supprd != null){
								gstinFiledPrd = docData.cdnr[i].supprd;
							}
							if(docData.cdnr[i].supfildt != null){
								gstinFiledDate += docData.cdnr[i].supfildt;
							}
							var gstinFiledInfoContent =  '</td><td class="text-left"><span>'+gstinFiledPrd+'</span></td>';
							gstinFiledInfoContent += '</td><td class="text-left"><span>'+gstinFiledDate+'</span></td>';
					
							var type = 'Credit Note';
							if(docData.cdnr[i].nt[j].typ != null){
								type = docData.cdnr[i].nt[j].typ == 'D'? 'Debit Note' : 'Credit Note';
							}
							cdnrContent += '<td class="text-left"><span>'+type+'</span>';
							if(docData.cdnr[i].nt[j].ntnum != null){
								cdnrContent += '</td><td class="text-left"><span>'+docData.cdnr[i].nt[j].ntnum+'</span></td>';
							}else{
								cdnrContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							
							cdnrContent += ctinTrdnmContent;
							
							if(docData.cdnr[i].nt[j].dt != null){
								cdnrContent += '</td><td class="text-left"><span>'+docData.cdnr[i].nt[j].dt+'</span></td>';
							}else{
								cdnrContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							if(docData.cdnr[i].nt[j].pos != null){
								cdnrContent += '</td><td class="text-left"><span>'+docData.cdnr[i].nt[j].pos+'</span></td>';
							}else{
								cdnrContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							if(docData.cdnr[i].nt[j].rev != null){
								cdnrContent += '</td><td class="text-left"><span>'+docData.cdnr[i].nt[j].rev+'</span></td>';
							}else{
								cdnrContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							
							if(docData.cdnr[i].nt[j].val != null){
								cdnrContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(docData.cdnr[i].nt[j].val).toFixed(2))+'</span></td>';
							}else{
								cdnrContent += '</td><td class="text-right"><span>'+0.0+'</span></td>';
							}
							
							var igst = 0.0, cgst = 0.0, sgst = 0.0 ,rt = 0.0, cess=0.0, txval=0.0;
							if(docData.cdnr[i].nt[j].items != "" && docData.cdnr[i].nt[j].items != null){
								if(docData.cdnr[i].nt[j].items.length> 0){
									for(var k=0 ; k<docData.cdnr[i].nt[j].items.length ; k++){
										if(docData.cdnr[i].nt[j].items[k].igst != null){
											igst += docData.cdnr[i].nt[j].items[k].igst;
										}
										if(docData.cdnr[i].nt[j].items[k].cgst != null){
											cgst += docData.cdnr[i].nt[j].items[k].cgst;
										}
										if(docData.cdnr[i].nt[j].items[k].sgst != null){
											sgst += docData.cdnr[i].nt[j].items[k].sgst;
										}
										if(docData.cdnr[i].nt[j].items[k].cess != null){
											cess += docData.cdnr[i].nt[j].items[k].cess;
										}
										if(docData.cdnr[i].nt[j].items[k].txval != null){
											txval += docData.cdnr[i].nt[j].items[k].txval;
										}
									}
									
								}
							}
							
							cdnrContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(txval).toFixed(2))+'</span></td>';
							cdnrContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(igst).toFixed(2))+'</span></td>';
							cdnrContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(cgst).toFixed(2))+'</span></td>';
							cdnrContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(sgst).toFixed(2))+'</span></td>';
							cdnrContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(cess).toFixed(2))+'</span></td>';
							
							cdnrContent += gstinFiledInfoContent;
							
							if(docData.cdnr[i].nt[j].itcavl != null){
								cdnrContent += '</td><td class="text-left"><span>'+docData.cdnr[i].nt[j].itcavl+'</span></td>';
							}else{
								cdnrContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							if(docData.cdnr[i].nt[j].rsn != null){
								cdnrContent += '</td><td class="text-left"><span>'+docData.cdnr[i].nt[j].rsn+'</span></td>';
							}else{
								cdnrContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							if(docData.cdnr[i].nt[j].diffprcnt != null){
								cdnrContent += '</td><td class="text-left"><span>'+docData.cdnr[i].nt[j].diffprcnt+'</span></td>';
							}else{
								cdnrContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							cdnrContent += '</tr>';				
						}
					}
					
				}	
			}
		}
		
		if(docData.cdnra != "" && docData.cdnra != null && docData.cdnra != undefined){
			if(docData.cdnra.length > 0){
				
				for(var i=0; i < docData.cdnra.length; i++){
					if(docData.cdnra[i].nt.length > 0){
						for(var j=0; j < docData.cdnra[i].nt.length; j++){
							cdnraContent += '<tr>';					
							var ctin = '', trdnm ='', gstinFiledPrd = '', gstinFiledDate ='';
							if(docData.cdnra[i].ctin != null){
								ctin = docData.cdnra[i].ctin;
							}
							if(docData.cdnra[i].trdnm != null){
								trdnm = docData.cdnra[i].trdnm;
							}
							var ctinTrdnmContent =  '</td><td class="text-left"><span>'+ctin+'</span></td>';
							ctinTrdnmContent += '</td><td class="text-left"><span>'+trdnm+'</span></td>';
							
							if(docData.cdnra[i].supprd != null){
								gstinFiledPrd = docData.cdnra[i].supprd;
							}
							if(docData.cdnra[i].supfildt != null){
								gstinFiledDate += docData.cdnra[i].supfildt;
							}
							var gstinFiledInfoContent =  '</td><td class="text-left"><span>'+gstinFiledPrd+'</span></td>';
							gstinFiledInfoContent += '</td><td class="text-left"><span>'+gstinFiledDate+'</span></td>';
							var type = 'CDNA Credit Note';
							if(docData.cdnr[i].nt[j].typ != null){
								type = docData.cdnr[i].nt[j].typ == 'D'? 'CDNA Debit Note' : 'CDNA Credit Note';
							}
							cdnraContent += '<td class="text-left"><span>'+type+'</span>';
							
							if(docData.cdnra[i].nt[j].ntnum != null){
								cdnraContent += '</td><td class="text-left"><span>'+docData.cdnra[i].nt[j].ntnum+'</span></td>';
							}else{
								cdnraContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							
							cdnraContent += ctinTrdnmContent;
							
							if(docData.cdnra[i].nt[j].dt != null){
								cdnraContent += '</td><td class="text-left"><span>'+docData.cdnra[i].nt[j].dt+'</span></td>';
							}else{
								cdnraContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							if(docData.cdnra[i].nt[j].pos != null){
								cdnraContent += '</td><td class="text-left"><span>'+docData.cdnra[i].nt[j].pos+'</span></td>';
							}else{
								cdnraContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							if(docData.cdnra[i].nt[j].rev != null){
								cdnraContent += '</td><td class="text-left"><span>'+docData.cdnra[i].nt[j].rev+'</span></td>';
							}else{
								cdnraContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							
							if(docData.cdnra[i].nt[j].val != null){
								cdnraContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(docData.cdnra[i].nt[j].val).toFixed(2))+'</span></td>';
							}else{
								cdnraContent += '</td><td class="text-right"><span>'+0.0+'</span></td>';
							}
							
							var igst = 0.0, cgst = 0.0, sgst = 0.0 ,rt = 0.0, cess=0.0, txval=0.0;
							if(docData.cdnra[i].nt[j].items != "" && docData.cdnra[i].nt[j].items != null){
								if(docData.cdnra[i].nt[j].items.length> 0){
									for(var k=0 ; k<docData.cdnra[i].nt[j].items.length ; k++){
										if(docData.cdnra[i].nt[j].items[k].igst != null){
											igst += docData.cdnra[i].nt[j].items[k].igst;
										}
										if(docData.cdnra[i].nt[j].items[k].cgst != null){
											cgst += docData.cdnra[i].nt[j].items[k].cgst;
										}
										if(docData.cdnra[i].nt[j].items[k].sgst != null){
											sgst += docData.cdnra[i].nt[j].items[k].sgst;
										}
										if(docData.cdnra[i].nt[j].items[k].cess != null){
											cess += docData.cdnra[i].nt[j].items[k].cess;
										}
										if(docData.cdnra[i].nt[j].items[k].txval != null){
											txval += docData.cdnra[i].nt[j].items[k].txval;
										}
									}
								}
							}
							
							cdnraContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(txval).toFixed(2))+'</span></td>';
							cdnraContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(igst).toFixed(2))+'</span></td>';
							cdnraContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(cgst).toFixed(2))+'</span></td>';
							cdnraContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(sgst).toFixed(2))+'</span></td>';
							cdnraContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(cess).toFixed(2))+'</span></td>';
							
							cdnraContent += gstinFiledInfoContent;
							
							if(docData.cdnra[i].nt[j].itcavl != null){
								cdnraContent += '</td><td class="text-left"><span>'+docData.cdnra[i].nt[j].itcavl+'</span></td>';
							}else{
								cdnraContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							if(docData.cdnra[i].nt[j].rsn != null){
								cdnraContent += '</td><td class="text-left"><span>'+docData.cdnra[i].nt[j].rsn+'</span></td>';
							}else{
								cdnraContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							if(docData.cdnra[i].nt[j].diffprcnt != null){
								cdnraContent += '</td><td class="text-left"><span>'+docData.cdnra[i].nt[j].diffprcnt+'</span></td>';
							}else{
								cdnraContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							cdnraContent += '</tr>';
						}
						
					}
									
				}	
			}
		}
		
		if(docData.isd != "" && docData.isd != null && docData.isd != undefined){
			if(docData.isd.length > 0){
				
				for(var i=0; i < docData.isd.length; i++){
					if(docData.isd[i].doclist.length > 0){
						for(var j=0; j < docData.isd[i].doclist.length; j++){	
							isdContent += '<tr>';							
							var ctin = '', trdnm ='', gstinFiledPrd = '', gstinFiledDate ='';
							if(docData.isd[i].ctin != null){
								ctin = docData.isd[i].ctin;
							}
							if(docData.isd[i].trdnm != null){
								trdnm = docData.isd[i].trdnm;
							}
							var ctinTrdnmContent =  '</td><td class="text-left"><span>'+ctin+'</span></td>';
							ctinTrdnmContent += '</td><td class="text-left"><span>'+trdnm+'</span></td>';
								
							if(docData.isd[i].supprd != null){
								gstinFiledPrd = docData.isd[i].supprd;
							}
							if(docData.isd[i].supfildt != null){
								gstinFiledDate += docData.isd[i].supfildt;
							}
							var gstinFiledInfoContent =  '</td><td class="text-left"><span>'+gstinFiledPrd+'</span></td>';
							gstinFiledInfoContent += '</td><td class="text-left"><span>'+gstinFiledDate+'</span></td>';
							var type = '';
							if(docData.isd[i].doclist[j].doctyp != null){
								type = docData.isd[i].doclist[j].doctyp;
							}
							isdContent += '<td class="text-left"><span>'+type+'</span>';
							if(docData.isd[i].doclist[j].docnum != null){
								isdContent += '</td><td class="text-left"><span>'+docData.isd[i].doclist[j].docnum+'</span></td>';
							}else{
								isdContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
									
							isdContent += ctinTrdnmContent;
									
							if(docData.isd[i].doclist[j].docdt != null){
								isdContent += '</td><td class="text-left"><span>'+docData.isd[i].doclist[j].docdt+'</span></td>';
							}else{
								isdContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							if(docData.isd[i].doclist[j].oinvnum != null){
								isdContent += '</td><td class="text-left"><span>'+docData.isd[i].doclist[j].oinvnum+'</span></td>';
							}else{
								isdContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							if(docData.isd[i].doclist[j].oinvdt != null){
								isdContent += '</td><td class="text-left"><span>'+docData.isd[i].doclist[j].oinvdt+'</span></td>';
							}else{
								isdContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}

							var igst = 0.0, cgst = 0.0, sgst = 0.0 ,rt = 0.0, cess=0.0, txval=0.0;
							if(docData.isd[i].doclist[j].igst){
								igst += docData.isd[i].doclist[j].igst;
							}
							if(docData.isd[i].doclist[j].cgst){
								cgst += docData.isd[i].doclist[j].cgst;
							}
							if(docData.isd[i].doclist[j].sgst){
								sgst += docData.isd[i].doclist[j].sgst;
							}
							if(docData.isd[i].doclist[j].cess){
								cess += docData.isd[i].doclist[j].cess;
							}
							isdContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(igst).toFixed(2))+'</span></td>';
							isdContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(cgst).toFixed(2))+'</span></td>';
							isdContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(sgst).toFixed(2))+'</span></td>';
							isdContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(cess).toFixed(2))+'</span></td>';
								
							isdContent += gstinFiledInfoContent;
							
							if(docData.isd[i].doclist[j].itcelg != null){
								isdContent += '</td><td class="text-left"><span>'+docData.isd[i].doclist[j].itcelg+'</span></td>';
							}else{
								isdContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							isdContent += '</tr>';
						}
					}
									
				}
			}
		}
		
		if(docData.isda != "" && docData.isda != null && docData.isda != undefined){
			if(docData.isda.length > 0){
				
				for(var i=0; i < docData.isda.length; i++){
					
					if(docData.isda[i].doclist.length > 0){	
						for(var j=0; j < docData.isda[i].doclist.length; j++){
							isdaContent += '<tr>';							
							var ctin = '', trdnm ='', gstinFiledPrd = '', gstinFiledDate ='';
							if(docData.isda[i].ctin != null){
								ctin = docData.isda[i].ctin;
							}
							if(docData.isda[i].trdnm != null){
								trdnm = docData.isda[i].trdnm;
							}
							var ctinTrdnmContent =  '</td><td class="text-left"><span>'+ctin+'</span></td>';
							ctinTrdnmContent += '</td><td class="text-left"><span>'+trdnm+'</span></td>';
								
							if(docData.isda[i].supprd != null){
								gstinFiledPrd = docData.isda[i].supprd;
							}
							if(docData.isda[i].supfildt != null){
								gstinFiledDate += docData.isda[i].supfildt;
							}
							var gstinFiledInfoContent =  '</td><td class="text-left"><span>'+gstinFiledPrd+'</span></td>';
							gstinFiledInfoContent += '</td><td class="text-left"><span>'+gstinFiledDate+'</span></td>';
							var type = '';
							if(docData.isda[i].doclist[j].doctyp != null){
								type = docData.isda[i].doclist[j].doctyp;
							}
							isdaContent += '<td class="text-left"><span>'+type+'</span>';
							if(docData.isda[i].doclist[j].docnum != null){
								isdaContent += '</td><td class="text-left"><span>'+docData.isda[i].doclist[j].docnum+'</span></td>';
							}else{
								isdaContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
									
							isdaContent += ctinTrdnmContent;
									
							if(docData.isda[i].doclist[j].docdt != null){
								isdaContent += '</td><td class="text-left"><span>'+docData.isda[i].doclist[j].docdt+'</span></td>';
							}else{
								isdaContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							if(docData.isda[i].doclist[j].oinvnum != null){
								isdaContent += '</td><td class="text-left"><span>'+docData.isda[i].doclist[j].oinvnum+'</span></td>';
							}else{
								isdaContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							if(docData.isda[i].doclist[j].oinvdt != null){
								isdaContent += '</td><td class="text-left"><span>'+docData.isda[i].doclist[j].oinvdt+'</span></td>';
							}else{
								isdaContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
								
							var igst = 0.0, cgst = 0.0, sgst = 0.0 ,rt = 0.0, cess=0.0, txval=0.0;
							if(docData.isda[i].doclist[j].igst != null){
								igst += docData.isda[i].doclist[j].igst;
							}
							if(docData.isda[i].doclist[j].cgst != null){
								cgst += docData.isda[i].doclist[j].cgst;
							}
							if(docData.isda[i].doclist[j].sgst != null){
								sgst += docData.isda[i].doclist[j].sgst;
							}
							if(docData.isda[i].doclist[j].cess != null){
								cess += docData.isda[i].doclist[j].cess;
							}
							
							isdaContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(igst).toFixed(2))+'</span></td>';
							isdaContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(cgst).toFixed(2))+'</span></td>';
							isdaContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(sgst).toFixed(2))+'</span></td>';
							isdaContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(cess).toFixed(2))+'</span></td>';
								
							isdaContent += gstinFiledInfoContent;
							
							if(docData.isda[i].doclist[j].itcelg != null){
								isdaContent += '</td><td class="text-left"><span>'+docData.isda[i].doclist[j].itcelg+'</span></td>';
							}else{
								isdaContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							isdaContent += '</tr>';				
						}
					}
					
				}
			}
		}
		
		if(docData.impg != "" && docData.impg != null && docData.impg != undefined){
			if(docData.impg.length > 0){
				var emptyVal = '';
				for(var i=0; i < docData.impg.length; i++){
					impgContent += '<tr>';
					
					if(docData.impg[i].refdt != null){
						impgContent += '</td><td class="text-left"><span>'+docData.impg[i].refdt+'</span></td>';
					}else{
						impgContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
					}
							
					if(docData.impg[i].portcode != null){
						impgContent += '</td><td class="text-left"><span>'+docData.impg[i].portcode+'</span></td>';
					}else{
						impgContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
					}
					if(docData.impg[i].boenum != null){
						impgContent += '</td><td class="text-left"><span>'+docData.impg[i].boenum+'</span></td>';
					}else{
						impgContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
					}
					if(docData.impg[i].boedt != null){
						impgContent += '</td><td class="text-left"><span>'+docData.impg[i].boedt+'</span></td>';
					}else{
						impgContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
					}
					var igst = 0.0, cgst = 0.0, sgst = 0.0 ,rt = 0.0, cess=0.0, txval=0.0;
					if(docData.impg[i].igst != null){
						igst += docData.impg[i].igst;
					}
					if(docData.impg[i].txval != null){
						txval += docData.impg[i].txval;
					}
					if(docData.impg[i].cess != null){
						cess += docData.impg[i].cess;
					}
					impgContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(txval).toFixed(2))+'</span></td>';
					impgContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(igst).toFixed(2))+'</span></td>';
					impgContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(cgst).toFixed(2))+'</span></td>';
					impgContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(sgst).toFixed(2))+'</span></td>';
					impgContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(cess).toFixed(2))+'</span></td>';
					
					if(docData.impg[i].isamd != null){
						impgContent += '</td><td class="text-left"><span>'+docData.impg[i].isamd+'</span></td>';
					}else{
						impgContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
					}
				}
				impgContent += '</tr>';				
			}
		}
		
		if(docData.impgsez != "" && docData.impgsez != null && docData.impgsez != undefined){
			if(docData.impgsez.length > 0){
				
				for(var i=0; i < docData.impgsez.length; i++){
					if(docData.impgsez[i].boe.length > 0){
						for(var j=0; j < docData.impgsez[i].boe.length; j++){
							impgsezContent += '<tr>';
							var ctin = '', trdnm ='', gstinFiledPrd = '', gstinFiledDate ='';
							if(docData.impgsez[i].ctin != null){
								ctin = docData.impgsez[i].ctin;
							}
							if(docData.impgsez[i].trdnm != null){
								trdnm = docData.impgsez[i].trdnm;
							}
							impgsezContent += '</td><td class="text-left"><span>'+ctin+'</span></td>';
							impgsezContent += '</td><td class="text-left"><span>'+trdnm+'</span></td>';
							if(docData.impgsez[i].boe[j].refdt != null){
								impgsezContent += '</td><td class="text-left"><span>'+docData.impgsez[i].boe[j].refdt+'</span></td>';
							}else{
								impgsezContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}		
							if(docData.impgsez[i].boe[j].portcode != null){
								impgsezContent += '</td><td class="text-left"><span>'+docData.impgsez[i].boe[j].portcode+'</span></td>';
							}else{
								impgsezContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							if(docData.impgsez[i].boe[j].boenum != null){
								impgsezContent += '</td><td class="text-left"><span>'+docData.impgsez[i].boe[j].boenum+'</span></td>';
							}else{
								impgsezContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							if(docData.impgsez[i].boe[j].boedt != null){
								impgsezContent += '</td><td class="text-left"><span>'+docData.impgsez[i].boe[j].boedt+'</span></td>';
							}else{
								impgsezContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
								
							var igst = 0.0, cgst = 0.0, sgst = 0.0 ,rt = 0.0, cess=0.0, txval=0.0;
							if(docData.impgsez[i].boe[j].igst != null){
								igst += docData.impgsez[i].boe[j].igst;
							}
							if(docData.impgsez[i].boe[j].cess != null){
								cess += docData.impgsez[i].boe[j].cess;
							}
							if(docData.impgsez[i].boe[j].txval != null){
								txval += docData.impgsez[i].boe[j].txval;
							}
							impgsezContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(txval).toFixed(2))+'</span></td>';
							impgsezContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(igst).toFixed(2))+'</span></td>';
							impgsezContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(cgst).toFixed(2))+'</span></td>';
							impgsezContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(sgst).toFixed(2))+'</span></td>';
							impgsezContent += '</td><td class="text-right"><span>'+formatNumber(parseFloat(cess).toFixed(2))+'</span></td>';
							
							if(docData.impgsez[i].boe[j].isamd != null){
								impgsezContent += '</td><td class="text-left"><span>'+docData.impgsez[i].boe[j].isamd+'</span></td>';
							}else{
								impgsezContent += '</td><td class="text-left"><span>'+emptyVal+'</span></td>';
							}
							impgsezContent += '</tr>';				
						}
					}
					
				}
			}
		}
	}
	$('#datatableb2bBody').html(b2bContent);
	$('#datatableb2baBody').html(b2baContent);
	$('#datatablecdnrBody').html(cdnrContent);
	$('#datatablecdnraBody').html(cdnraContent);
	$('#datatableisdBody').html(isdContent);
	$('#datatableisdaBody').html(isdaContent);
	$('#datatableimpgBody').html(impgContent);
	$('#datatableimpgsezBody').html(impgsezContent);
	$('#datatableb2b,#datatableb2ba,#datatablecdnr,#datatablecdnra,#datatableisd,#datatableisda,#datatableimpg,#datatableimpgsez').DataTable({
		"dom": '<"toolbar"f>lrtip<"clear">',
		"paging": true,
		"orderClasses": false,
		"searching": true,
		"lengthMenu": [ [10, 25, 50,100, 500], [10, 25, 50,100, 500] ],
		"responsive": true
	});
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
