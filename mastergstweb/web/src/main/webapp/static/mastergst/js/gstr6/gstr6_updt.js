$(function(){
});
function supplierWiseAmts(type){
	$('#gstwiseData').html('');
	var tableContent='';
	$.ajax({
		url : contextPath+'/populateInvoiceData'+urlSuffixs+'/'+month+'/'+year+'?type='+type,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			var tableContent = "";
			var index = 1;
			if(response !="" && response !=null){
				if(type == 'b2b'){
					if(response.b2b !="" && response.b2b !=null){
						var txval =0, iamt = 0.0, camt =0.0, samt =0.0, csamt =0.0;
						for(var i = 0; i< response.b2b.length; i++){
							for(var j = 0; j< response.b2b[i].inv[i].itms.length; j++){
								txval += response.b2b[i].inv[i].itms[j].itm_det.txval;
								iamt +=response.b2b[i].inv[i].itms[j].itm_det.iamt;
								camt += response.b2b[i].inv[i].itms[j].itm_det.camt;
								samt += response.b2b[i].inv[i].itms[j].itm_det.samt;
								csamt += response.b2b[i].inv[i].itms[j].itm_det.csamt;
							}
							
							tableContent += '<tr><td>'+index+'</td><td>'+response.b2b[i].ctin+'</td><td>'+response.b2b[i].inv[i].inum+'</td><td>'+response.b2b[i].inv[i].idt+'</td><td>'+formatNumber(txval.toFixed(2))+'</td><td>'+formatNumber(iamt.toFixed(2))+'</td><td>'+formatNumber(camt.toFixed(2))+'</td><td>'+formatNumber(samt.toFixed(2))+'</td><td>'+formatNumber(csamt.toFixed(2))+'</td></tr>';
							index++;
						}
					}
				}else if(type == 'b2ba'){
					if(response.b2ba !="" && response.b2ba !=null){
						var txval =0, iamt = 0.0, camt =0.0, samt =0.0, csamt =0.0;
						for(var i = 0; i< response.b2ba.length; i++){
							for(var j = 0; j< response.b2ba[i].inv[i].itms.length; j++){
								txval += response.b2ba[i].inv[i].itms[j].itm_det.txval;
								iamt +=response.b2ba[i].inv[i].itms[j].itm_det.iamt;
								camt += response.b2ba[i].inv[i].itms[j].itm_det.camt;
								samt += response.b2ba[i].inv[i].itms[j].itm_det.samt;
								csamt += response.b2ba[i].inv[i].itms[j].itm_det.csamt;
							}
							
							tableContent += '<tr><td>'+index+'</td><td>'+response.b2ba[i].ctin+'</td><td>'+response.b2ba[i].inv[i].inum+'</td><td>'+response.b2ba[i].inv[i].idt+'</td><td>'+formatNumber(txval.toFixed(2))+'</td><td>'+formatNumber(iamt.toFixed(2))+'</td><td>'+formatNumber(camt.toFixed(2))+'</td><td>'+formatNumber(samt.toFixed(2))+'</td><td>'+formatNumber(csamt.toFixed(2))+'</td></tr>';
							index++;
						}
					}
				}else if(type == 'cdn'){
					if(response.cdn !="" && response.cdn !=null){
						var txval =0, iamt = 0.0, camt =0.0, samt =0.0, csamt =0.0;
						for(var i = 0; i< response.cdn.length; i++){
							for(var j = 0; j< response.cdn[i].nt[i].itms.length; j++){
								txval += response.cdn[i].nt[i].itms[j].itm_det.txval;
								iamt +=response.cdn[i].nt[i].itms[j].itm_det.iamt;
								camt += response.cdn[i].nt[i].itms[j].itm_det.camt;
								samt += response.cdn[i].nt[i].itms[j].itm_det.samt;
								csamt += response.cdn[i].nt[i].itms[j].itm_det.csamt;
							}
							tableContent += '<tr><td>'+index+'</td><td>'+response.cdn[i].ctin+'</td><td>'+response.cdn[i].nt[i].nt_num+'</td><td>'+response.cdn[i].nt[i].nt_dt+'</td><td>'+formatNumber(txval.toFixed(2))+'</td><td>'+formatNumber(iamt.toFixed(2))+'</td><td>'+formatNumber(camt.toFixed(2))+'</td><td>'+formatNumber(samt.toFixed(2))+'</td><td>'+formatNumber(csamt.toFixed(2))+'</td></tr>';
							index++;
						}
					}
				}else if(type == 'cdna'){
					if(response.cdna !="" && response.cdna !=null){
						var txval =0, iamt = 0.0, camt =0.0, samt =0.0, csamt =0.0;
						for(var i = 0; i< response.cdna.length; i++){
							for(var j = 0; j< response.cdna[i].nt[i].itms.length; j++){
								txval += response.cdna[i].nt[i].itms[j].itm_det.txval;
								iamt +=response.cdna[i].nt[i].itms[j].itm_det.iamt;
								camt += response.cdna[i].nt[i].itms[j].itm_det.camt;
								samt += response.cdna[i].nt[i].itms[j].itm_det.samt;
								csamt += response.cdna[i].nt[i].itms[j].itm_det.csamt;
							}
							
							tableContent += '<tr><td>'+index+'</td><td>'+response.cdna[i].ctin+'</td><td>'+response.cdna[i].nt[i].nt_num+'</td><td>'+response.cdna[i].nt[i].nt_dt+'</td><td>'+formatNumber(txval.toFixed(2))+'</td><td>'+formatNumber(iamt.toFixed(2))+'</td><td>'+formatNumber(camt.toFixed(2))+'</td><td>'+formatNumber(samt.toFixed(2))+'</td><td>'+formatNumber(csamt.toFixed(2))+'</td></tr>';
							index++;
						}
					}
				}else if(type == 'ISDElg'){
					if(response.isd !="" && response.isd !=null){
						var txval =0, iamt = 0.0, camt =0.0, samt =0.0, csamt =0.0;
						var inum='',idt='';
						for(var i = 0; i< response.isd.length; i++){
							for(var j = 0; j< response.isd[i].elglst.doclst.length; j++){
								inum = response.isd[i].elglst.doclst[j].docnum;
								idt = response.isd[i].elglst.doclst[j].docdt;
								if(response.isd[i].elglst.doclst[j].iamti != null && response.isd[i].elglst.doclst[j].iamti !=""){
									iamt +=response.isd[i].elglst.doclst[j].iamti;
								}
								if(response.isd[i].elglst.doclst[j].samti != null && response.isd[i].elglst.doclst[j].samti !=""){
									iamt +=response.isd[i].elglst.doclst[j].samti;
								}
								if(response.isd[i].elglst.doclst[j].camti != null && response.isd[i].elglst.doclst[j].camti !=""){
									iamt +=response.isd[i].elglst.doclst[j].camti;
								}
								if(response.isd[i].elglst.doclst[j].iamtc != null && response.isd[i].elglst.doclst[j].iamtc !=""){
									camt +=response.isd[i].elglst.doclst[j].iamtc;
								}
								if(response.isd[i].elglst.doclst[j].camtc != null && response.isd[i].elglst.doclst[j].camtc !=""){
									camt +=response.isd[i].elglst.doclst[j].camtc;
								}
								if(response.isd[i].elglst.doclst[j].iamts != null && response.isd[i].elglst.doclst[j].iamts !=""){
									samt +=response.isd[i].elglst.doclst[j].iamts;
								}
								if(response.isd[i].elglst.doclst[j].samts != null && response.isd[i].elglst.doclst[j].samts !=""){
									samt +=response.isd[i].elglst.doclst[j].samts;
								}
								if(response.isd[i].elglst.doclst[j].csamt != null && response.isd[i].elglst.doclst[j].csamt !=""){
									csamt +=response.isd[i].elglst.doclst[j].csamt;
								}
								tableContent += '<tr><td>'+index+'</td><td>'+response.isd[i].elglst.cpty+'</td><td>'+inum+'</td><td>'+idt+'</td><td>'+formatNumber(txval.toFixed(2))+'</td><td>'+formatNumber(iamt.toFixed(2))+'</td><td>'+formatNumber(camt.toFixed(2))+'</td><td>'+formatNumber(samt.toFixed(2))+'</td><td>'+formatNumber(csamt.toFixed(2))+'</td></tr>';
								index++;
							}
						}
					}
				}else if(type == 'ISDInElg'){
					if(response.isd !="" && response.isd !=null){
						var txval =0, iamt = 0.0, camt =0.0, samt =0.0, csamt =0.0;
						var inum='',idt='';
						for(var i = 0; i< response.isd.length; i++){
							for(var j = 0; j< response.isd[i].inelglst.doclst.length; j++){
								inum = response.isd[i].inelglst.doclst[j].docnum;
								idt = response.isd[i].inelglst.doclst[j].docdt;
								if(response.isd[i].inelglst.doclst[j].iamti != null && response.isd[i].inelglst.doclst[j].iamti !=""){
									iamt +=response.isd[i].inelglst.doclst[j].iamti;
								}
								if(response.isd[i].inelglst.doclst[j].samti != null && response.isd[i].inelglst.doclst[j].samti !=""){
									iamt +=response.isd[i].inelglst.doclst[j].samti;
								}
								if(response.isd[i].inelglst.doclst[j].camti != null && response.isd[i].inelglst.doclst[j].camti !=""){
									iamt +=response.isd[i].inelglst.doclst[j].camti;
								}
								if(response.isd[i].inelglst.doclst[j].iamtc != null && response.isd[i].inelglst.doclst[j].iamtc !=""){
									camt +=response.isd[i].inelglst.doclst[j].iamtc;
								}
								if(response.isd[i].inelglst.doclst[j].camtc != null && response.isd[i].inelglst.doclst[j].camtc !=""){
									camt +=response.isd[i].inelglst.doclst[j].camtc;
								}
								if(response.isd[i].inelglst.doclst[j].iamts != null && response.isd[i].inelglst.doclst[j].iamts !=""){
									samt +=response.isd[i].inelglst.doclst[j].iamts;
								}
								if(response.isd[i].inelglst.doclst[j].samts != null && response.isd[i].inelglst.doclst[j].samts !=""){
									samt +=response.isd[i].inelglst.doclst[j].samts;
								}
								if(response.isd[i].inelglst.doclst[j].csamt != null && response.isd[i].inelglst.doclst[j].csamt !=""){
									csamt +=response.isd[i].inelglst.doclst[j].csamt;
								}
								tableContent += '<tr><td>'+index+'</td><td>'+response.isd[i].inelglst.cpty+'</td><td>'+inum+'</td><td>'+idt+'</td><td>'+formatNumber(txval.toFixed(2))+'</td><td>'+formatNumber(iamt.toFixed(2))+'</td><td>'+formatNumber(camt.toFixed(2))+'</td><td>'+formatNumber(samt.toFixed(2))+'</td><td>'+formatNumber(csamt.toFixed(2))+'</td></tr>';
								index++;
							}
						}
					}
				}else if(type == 'ISDAElg'){
					if(response.isda !="" && response.isda !=null){
						var txval =0, iamt = 0.0, camt =0.0, samt =0.0, csamt =0.0;
						var inum='',idt='';
						for(var i = 0; i< response.isda.length; i++){
							for(var j = 0; j< response.isda[i].elglst.doclst.length; j++){
								inum = response.isda[i].elglst.doclst[j].rdocnum;
								idt = response.isda[i].elglst.doclst[j].rdocdt;
								if(response.isda[i].elglst.doclst[j].iamti != null && response.isda[i].elglst.doclst[j].iamti !=""){
									iamt +=response.isda[i].elglst.doclst[j].iamti;
								}
								if(response.isda[i].elglst.doclst[j].samti != null && response.isda[i].elglst.doclst[j].samti !=""){
									iamt +=response.isda[i].elglst.doclst[j].samti;
								}
								if(response.isda[i].elglst.doclst[j].camti != null && response.isda[i].elglst.doclst[j].camti !=""){
									iamt +=response.isda[i].elglst.doclst[j].camti;
								}
								if(response.isda[i].elglst.doclst[j].iamtc != null && response.isda[i].elglst.doclst[j].iamtc !=""){
									camt +=response.isda[i].elglst.doclst[j].iamtc;
								}
								if(response.isda[i].elglst.doclst[j].camtc != null && response.isda[i].elglst.doclst[j].camtc !=""){
									camt +=response.isda[i].elglst.doclst[j].camtc;
								}
								if(response.isda[i].elglst.doclst[j].iamts != null && response.isda[i].elglst.doclst[j].iamts !=""){
									samt +=response.isda[i].elglst.doclst[j].iamts;
								}
								if(response.isda[i].elglst.doclst[j].samts != null && response.isda[i].elglst.doclst[j].samts !=""){
									samt +=response.isda[i].elglst.doclst[j].samts;
								}
								if(response.isda[i].elglst.doclst[j].csamt != null && response.isda[i].elglst.doclst[j].csamt !=""){
									csamt +=response.isda[i].elglst.doclst[j].csamt;
								}
								tableContent += '<tr><td>'+index+'</td><td>'+response.isda[i].elglst.rcpty+'</td><td>'+inum+'</td><td>'+idt+'</td><td>'+formatNumber(txval.toFixed(2))+'</td><td>'+formatNumber(iamt.toFixed(2))+'</td><td>'+formatNumber(camt.toFixed(2))+'</td><td>'+formatNumber(samt.toFixed(2))+'</td><td>'+formatNumber(csamt.toFixed(2))+'</td></tr>';
								index++;
							}
						}
					}
				}else if(type == 'ISDAInElg'){
					if(response.isda !="" && response.isda !=null){
						var txval =0, iamt = 0.0, camt =0.0, samt =0.0, csamt =0.0;
						var inum='',idt='';
						for(var i = 0; i< response.isda.length; i++){
							for(var j = 0; j< response.isda[i].inelglst.doclst.length; j++){
								inum = response.isda[i].inelglst.doclst[j].rdocnum;
								idt = response.isda[i].inelglst.doclst[j].rdocdt;
								if(response.isda[i].inelglst.doclst[j].iamti != null && response.isda[i].inelglst.doclst[j].iamti !=""){
									iamt +=response.isda[i].inelglst.doclst[j].iamti;
								}
								if(response.isda[i].inelglst.doclst[j].samti != null && response.isda[i].inelglst.doclst[j].samti !=""){
									iamt +=response.isda[i].inelglst.doclst[j].samti;
								}
								if(response.isda[i].inelglst.doclst[j].camti != null && response.isda[i].inelglst.doclst[j].camti !=""){
									iamt +=response.isda[i].inelglst.doclst[j].camti;
								}
								if(response.isda[i].inelglst.doclst[j].iamtc != null && response.isda[i].inelglst.doclst[j].iamtc !=""){
									camt +=response.isda[i].inelglst.doclst[j].iamtc;
								}
								if(response.isda[i].inelglst.doclst[j].camtc != null && response.isda[i].inelglst.doclst[j].camtc !=""){
									camt +=response.isda[i].inelglst.doclst[j].camtc;
								}
								if(response.isda[i].inelglst.doclst[j].iamts != null && response.isda[i].inelglst.doclst[j].iamts !=""){
									samt +=response.isda[i].inelglst.doclst[j].iamts;
								}
								if(response.isda[i].inelglst.doclst[j].samts != null && response.isda[i].inelglst.doclst[j].samts !=""){
									samt +=response.isda[i].inelglst.doclst[j].samts;
								}
								if(response.isda[i].inelglst.doclst[j].csamt != null && response.isda[i].inelglst.doclst[j].csamt !=""){
									csamt +=response.isda[i].inelglst.doclst[j].csamt;
								}
								tableContent += '<tr><td>'+index+'</td><td>'+response.isda[i].inelglst.rcpty+'</td><td>'+inum+'</td><td>'+idt+'</td><td>'+formatNumber(txval.toFixed(2))+'</td><td>'+formatNumber(iamt.toFixed(2))+'</td><td>'+formatNumber(camt.toFixed(2))+'</td><td>'+formatNumber(samt.toFixed(2))+'</td><td>'+formatNumber(csamt.toFixed(2))+'</td></tr>';
								index++;
							}
						}
					}
				}
			}
			$('#gstwiseData').append(tableContent);
		}
	});
}
function loadTables(b2bAmts){
	if(b2bAmts != undefined){
		$('#b2b_records').val(b2bAmts.totalTransactions ? b2bAmts.totalTransactions : '0');
		$('#b2b_taxableValue').val(b2bAmts.totalTaxableAmount ? b2bAmts.totalTaxableAmount : '0.00');
		$('#b2b_cgst').val(b2bAmts.totalCGSTAmount ? b2bAmts.totalCGSTAmount : '0.00');
		$('#b2b_sgst').val(b2bAmts.totalSGSTAmount ? b2bAmts.totalSGSTAmount : '0.00');
		$('#b2b_igst').val(b2bAmts.totalIGSTAmount ? b2bAmts.totalIGSTAmount : '0.00');
		$('#b2b_cess').val(b2bAmts.totalCessAmount ? b2bAmts.totalCessAmount : '0.00');
	}
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