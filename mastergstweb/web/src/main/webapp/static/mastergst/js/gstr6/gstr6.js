function autocalculategstr6(){
		var gstr6Url  = _getContextPath()+'/populateGSTR6'+urlSuffix+'/'+month+'/'+year;
		var itcUrl  = _getContextPath()+'/populateITC'+urlSuffix+'/'+month+'/'+year;
		$.ajax({
			 url : gstr6Url,
			 type: 'GET',
	         contentType: 'application/json; charset=utf-8',
	         dataType: "json",
	        success : function(resp) {
	        	 var response = resp.invoices.content;
	        	 var itcresponse = resp.itcinvoices.content;
	        	 var itcDetails = resp.itcDetails;
	        	 var lateFee = resp.latefee;
	        	 var b2binv = JSON.stringify(response.b2b);
				invRowCount =1;
				if(invRowCount < response.length){
					 for (var i = 1; i < response.length; i++) {
	                        if (response[i]) {
	                        	if(response[i].invtype != "B2BA" && response[i].invtype !="CDNA"){
	                        		addGSTR6_row(response[i].invtype);
	                        	}
	                        }
				     }
				}
				for(var i=0;i<response.length;i++){
					var rowno = i+1;
					var gstnnum ='';var ogstnnum ='';var oldinum='';var oldidt='';
					if(response[i].b2b && response[i].b2b.length > 0){
						if(response[i].b2b[0].ctin != undefined){
							gstnnum = response[i].b2b[0].ctin;
						}
					}else if(response[i].cdn && response[i].cdn.length > 0){
						if(response[i].cdn[0].ctin != undefined){
							gstnnum = response[i].cdn[0].ctin;
						}
					}else if(response[i].b2ba && response[i].b2ba.length > 0){
						if(response[i].b2ba[0].ctin != undefined){
							gstnnum = response[i].b2ba[0].ctin;
						}
					}
					
					 $('#gstr6_gstin'+rowno).val(gstnnum);
					 $('#gstr6_invoiceno'+rowno).val(response[i].invoiceno);
					 $('#gstr6_invdate'+rowno).val((new Date(response[i].dateofinvoice)).toLocaleDateString('en-GB'));
					 $('#gstr6_Totalval'+rowno).val(formatNumber(response[i].totalamount.toFixed(2)));
					 //$('#gstr6_rate'+rowno).val(response[i].totalamount);
					 $('#gstr6_taxval'+rowno).val(formatNumber(response[i].totaltaxableamount.toFixed(2)));
					 $('#gstr6_igst'+rowno).val(formatNumber(response[i].totalIgstAmount.toFixed(2)));
					 $('#gstr6_cgst'+rowno).val(formatNumber(response[i].totalCgstAmount.toFixed(2)));
					 $('#gstr6_sgst'+rowno).val(formatNumber(response[i].totalSgstAmount.toFixed(2)));
					 $('#gstr6_cess'+rowno).val(formatNumber(response[i].totalCessAmount.toFixed(2)));
					 
				}
				for(var i=0;i<response.length;i++){
					var rowno = i+1;
					var gstnnum ='';var ogstnnum ='';var oldinum='';var oldidt='';
					 if(response[i].b2ba && response[i].b2ba.length > 0){
						 if(response[i].b2ba[0].ctin != undefined){
								ogstnnum = response[i].b2ba[0].ctin;
							}
							if(response[i].b2ba[0].inv[0] != undefined){
								oldinum = response[i].b2ba[0].inv[0].oinum;
							}
							if(response[i].b2ba[0].inv[0] != undefined){
								oldidt = response[i].b2ba[0].inv[0].oidt;
							}
							 $('#table6_b2ba_ogstin1').val(ogstnnum);
							$('#table6_b2ba_oinum1').val(oldinum);
							$('#table6_b2ba_oidt1').val(oldidt);
					}else if(response[i].cdna && response[i].cdna.length > 0){
						 if(response[i].cdna[0].ctin != undefined){
								ogstnnum = response[i].cdna[0].ctin;
							}
							if(response[i].cdna[0].nt[0] != undefined){
								oldinum = response[i].cdna[0].nt[0].ont_num;
							}
							if(response[i].cdna[0].nt[0] != undefined){
								oldidt = response[i].cdna[0].nt[0].ont_dt;
							}
							 $('#table6_cdna_ogstin1,#table6_cdnaa_ogstin1').val(ogstnnum);
							$('#table6_cdna_oinum1,#table6_cdnaa_oinum1').val(oldinum);
							$('#table6_cdna_oidt1,#table6_cdnaa_oidt1').val(oldidt);
					}
					 $('#table6_b2ba_gstin1,#table6_cdna_gstin1,#table6_cdnaa_gstin1').val(ogstnnum);
					 $('#table6_b2ba_inum1,#table6_cdna_inum1,#table6_cdnaa_inum1').val(response[i].invoiceno);
					 $('#table6_b2ba_idt1,#table6_cdna_idt1,#table6_cdnaa_idt1').val((new Date(response[i].dateofinvoice)).toLocaleDateString('en-GB'));
					 $('#table6_b2ba_val1,#table6_cdna_val1,#table6_cdnaa_val1').val(formatNumber(response[i].totalamount.toFixed(2)));
					 $('#table6_b2ba_taxval1,#table6_cdna_taxval1,#table6_cdnaa_taxval1').val(formatNumber(response[i].totaltaxableamount.toFixed(2)));
					 $('#table6_b2ba_cgst1,#table6_cdna_cgst1,#table6_cdnaa_cgst1').val(formatNumber(response[i].totalCgstAmount.toFixed(2)));
					 $('#table6_b2ba_sgst1,#table6_cdna_sgst1,#table6_cdnaa_sgst1').val(formatNumber(response[i].totalSgstAmount.toFixed(2)));
					 $('#table6_b2ba_igst1,#table6_cdna_igst1,#table6_cdnaa_igst1').val(formatNumber(response[i].totalIgstAmount.toFixed(2)));
					 $('#table6_b2ba_cess1,#table6_cdna_cess1,#table6_cdnaa_cess1').val(formatNumber(response[i].totalCessAmount.toFixed(2)));
				}
				var itcIGST=0.00,itcCGST=0.00,itcSGST=0.00,itcCESS=0.00;
				var elgitcIGST=0.00,elgitcCGST=0.00,elgitcSGST=0.00,elgitcCESS=0.00
				var inelgitcIGST=0.00,inelgitcCGST=0.00,inelgitcSGST=0.00,inelgitcCESS=0.00
				for(var i=0;i<itcresponse.length;i++){
					var isdrowno = i+1;
					var gstnnum ='';
					itcRowCount =1;
					if(itcresponse[i] != undefined){
					if(itcresponse[i].isd && itcresponse[i].isd.length > 0){
						if(itcresponse[i].isd[0].elglst.cpty != undefined){
							gstnnum = itcresponse[i].isd[0].elglst.cpty;
						}
					}
					if(itcresponse[i].isd && itcresponse[i].isd[0].elglst != undefined){
						elgitcIGST = itcresponse[i].isd[0].elglst.doclst[0].iamti + itcresponse[i].isd[0].elglst.doclst[0].samti + itcresponse[i].isd[0].elglst.doclst[0].camti;
						elgitcCGST = itcresponse[i].isd[0].elglst.doclst[0].iamtc + itcresponse[i].isd[0].elglst.doclst[0].camtc;
						elgitcSGST = itcresponse[i].isd[0].elglst.doclst[0].iamts + itcresponse[i].isd[0].elglst.doclst[0].samts;
						elgitcCESS += itcresponse[i].isd[0].elglst.doclst[0].csamt;
					}
					if(itcresponse[i].isd && itcresponse[i].isd[0].inelglst != undefined){
						inelgitcIGST = itcresponse[i].isd[0].inelglst.doclst[0].iamti + itcresponse[i].isd[0].inelglst.doclst[0].samti + itcresponse[i].isd[0].inelglst.doclst[0].camti;
						inelgitcCGST = itcresponse[i].isd[0].inelglst.doclst[0].iamtc + itcresponse[i].isd[0].inelglst.doclst[0].camtc;
						inelgitcSGST = itcresponse[i].isd[0].inelglst.doclst[0].iamts + itcresponse[i].isd[0].inelglst.doclst[0].samts;
						inelgitcCESS += itcresponse[i].isd[0].inelglst.doclst[0].csamt;
					}
					var oldgstin='',rgstin='',oldDocno='',oldDate='',rDocno='',rDate='',cDocNo='',cDate='';
					var isdaigst=0.00,isdacgst=0.00,isdasgst=0.00,isdacess=0.00;
					var inelgoldgstin='',inelgrgstin='',inelgoldDocno='',inelgoldDate='',inelgrDocno='',inelgrDate='',inelgcDocNo='',inelgcDate='';
					var inelgisdaigst=0.00,inelgisdacgst=0.00,inelgisdasgst=0.00,inelgisdacess=0.00;
					if(itcresponse[i].isda && itcresponse[i].isda.length > 0){
						if(itcresponse[i].isda[0].elglst.cpty != undefined){
							oldgstin = itcresponse[i].isda[0].elglst.cpty;
						}
						if(itcresponse[i].isda[0].elglst.rcpty != undefined){
							rgstin = itcresponse[i].isda[0].elglst.rcpty;
						}
						if(itcresponse[i].isda[0].elglst != undefined && itcresponse[i].isda[0].elglst.doclst[0] != undefined){
							oldDocno = itcresponse[i].isda[0].elglst.doclst[0].odocnum;
							oldDate = itcresponse[i].isda[0].elglst.doclst[0].odocdt;
							rDocno = itcresponse[i].isda[0].elglst.doclst[0].rdocnum;
							rDate = itcresponse[i].isda[0].elglst.doclst[0].rdocdt;
							cDocNo = itcresponse[i].isda[0].elglst.doclst[0].ocrdnum;
							cDate = itcresponse[i].isda[0].elglst.doclst[0].ocrddt;
							isdaigst = itcresponse[i].isda[0].elglst.doclst[0].iamti + itcresponse[i].isda[0].elglst.doclst[0].samti + itcresponse[i].isda[0].elglst.doclst[0].camti;
							isdacgst = itcresponse[i].isda[0].elglst.doclst[0].iamtc + itcresponse[i].isda[0].elglst.doclst[0].camtc;
							isdasgst = itcresponse[i].isda[0].elglst.doclst[0].iamts + itcresponse[i].isda[0].elglst.doclst[0].samts;
							isdacess += itcresponse[i].isda[0].elglst.doclst[0].csamt;
						}
						
						if(itcresponse[i].isda[0].inelglst.cpty != undefined){
							inelgoldgstin = itcresponse[i].isda[0].inelglst.cpty;
						}
						if(itcresponse[i].isda[0].inelglst.rcpty != undefined){
							inelgrgstin = itcresponse[i].isda[0].inelglst.rcpty;
						}
						if(itcresponse[i].isda[0].inelglst != undefined && itcresponse[i].isda[0].inelglst.doclst[0] != undefined){
							inelgoldDocno = itcresponse[i].isda[0].inelglst.doclst[0].odocnum;
							inelgoldDate = itcresponse[i].isda[0].inelglst.doclst[0].odocdt;
							inelgrDocno = itcresponse[i].isda[0].inelglst.doclst[0].rdocnum;
							inelgrDate = itcresponse[i].isda[0].inelglst.doclst[0].rdocdt;
							inelgcDocNo = itcresponse[i].isda[0].inelglst.doclst[0].ocrdnum;
							inelgcDate = itcresponse[i].isda[0].inelglst.doclst[0].ocrddt;
							inelgisdaigst = itcresponse[i].isda[0].inelglst.doclst[0].iamti + itcresponse[i].isda[0].inelglst.doclst[0].samti + itcresponse[i].isda[0].inelglst.doclst[0].camti;
							inelgisdacgst = itcresponse[i].isda[0].inelglst.doclst[0].iamtc + itcresponse[i].isda[0].inelglst.doclst[0].camtc;
							inelgisdasgst = itcresponse[i].isda[0].inelglst.doclst[0].iamts + itcresponse[i].isda[0].inelglst.doclst[0].samts;
							inelgisdacess += itcresponse[i].isda[0].inelglst.doclst[0].csamt;
						}
						$('#table9_isda_gstin').val(oldgstin);
						$('#table9_isda_rgstin').val(rgstin);
						$('#table9_isda_oldinum').val(oldDocno);
						$('#table9_isda_oldidt').val(oldDate);
						$('#table9_isda_crinum').val(cDocNo);
						$('#table9_isda_cridt').val(cDate);
						$('#table9_isda_inum').val(rDocno);
						$('#table9_isda_idt').val(rDate);
						$('#table9_isda_rgstin').val(rgstin);
						$('#table9_isda_cgst').val(formatNumber(isdacgst.toFixed(2)));
						$('#table9_isda_sgst').val(formatNumber(isdasgst.toFixed(2)));
						$('#table9_isda_igst').val(formatNumber(isdaigst.toFixed(2)));
						$('#table9_isda_cess').val(formatNumber(isdacess.toFixed(2)));
						
						$('#table9b_isda_gstin').val(inelgoldgstin);
						$('#table9b_isda_rgstin').val(inelgrgstin);
						$('#table9b_isda_oldinum').val(inelgoldDocno);
						$('#table9b_isda_oldidt').val(inelgoldDate);
						$('#table9b_isda_crinum').val(inelgcDocNo);
						$('#table9b_isda_cridt').val(inelgcDate);
						$('#table9b_isda_inum').val(inelgrDocno);
						$('#table9b_isda_idt').val(inelgrDate);
						$('#table9b_isda_rgstin').val(inelgrgstin);
						$('#table9b_isda_cgst').val(formatNumber(inelgisdacgst.toFixed(2)));
						$('#table9b_isda_sgst').val(formatNumber(inelgisdasgst.toFixed(2)));
						$('#table9b_isda_igst').val(formatNumber(inelgisdaigst.toFixed(2)));
						$('#table9b_isda_cess').val(formatNumber(inelgisdacess.toFixed(2)));
					}
					$('#elg5A_gstin'+isdrowno).val(gstnnum);
					$('#elg5A_invoiceno'+isdrowno).val(itcresponse[i].invoiceno);
					$('#elg5A_invoicedate'+isdrowno).val((new Date(itcresponse[i].dateofinvoice)).toLocaleDateString('en-GB'));
					$('#elg5A_cgst'+isdrowno).val(formatNumber(elgitcCGST.toFixed(2)));
					$('#elg5A_sgst'+isdrowno).val(formatNumber(elgitcSGST.toFixed(2)));
					$('#elg5A_igst'+isdrowno).val(formatNumber(elgitcIGST.toFixed(2)));
					$('#elg5A_cess'+isdrowno).val(formatNumber(elgitcCESS.toFixed(2)));
					
					$('#elg5B_gstin'+isdrowno).val(gstnnum);
					$('#elg5B_invoiceno'+isdrowno).val(itcresponse[i].invoiceno);
					$('#elg5B_invoicedate'+isdrowno).val((new Date(itcresponse[i].dateofinvoice)).toLocaleDateString('en-GB'));
					$('#elg5B_cgst'+isdrowno).val(formatNumber(inelgitcCGST.toFixed(2)));
					$('#elg5B_sgst'+isdrowno).val(formatNumber(inelgitcSGST.toFixed(2)));
					$('#elg5B_igst'+isdrowno).val(formatNumber(inelgitcIGST.toFixed(2)));
					$('#elg5B_cess'+isdrowno).val(formatNumber(inelgitcCESS.toFixed(2)));
				}
				}
				if(resp.totalITC != undefined){
					$('#totalITC_igst').val(formatNumber(resp.totalITC.iamt.toFixed(2)));
					$('#totalITC_cgst').val(formatNumber(resp.totalITC.camt.toFixed(2)));
					$('#totalITC_sgst').val(formatNumber(resp.totalITC.samt.toFixed(2)));
					$('#totalITC_cess').val(formatNumber(resp.totalITC.csamt.toFixed(2)));
				}	
				if(resp.elgITC != undefined){
					$('#elgtotalITC_igst').val(formatNumber(resp.elgITC.iamt.toFixed(2)));
					$('#elgtotalITC_cgst').val(formatNumber(resp.elgITC.camt.toFixed(2)));
					$('#elgtotalITC_sgst').val(formatNumber(resp.elgITC.samt.toFixed(2)));
					$('#elgtotalITC_cess').val(formatNumber(resp.elgITC.csamt.toFixed(2)));
				}
				if(resp.inelgITC != undefined){
					$('#inelgtotalITC_igst').val(formatNumber(resp.inelgITC.iamt.toFixed(2)));
					$('#inelgtotalITC_cgst').val(formatNumber(resp.inelgITC.camt.toFixed(2)));
					$('#inelgtotalITC_sgst').val(formatNumber(resp.inelgITC.samt.toFixed(2)));
					$('#inelgtotalITC_cess').val(formatNumber(resp.inelgITC.csamt.toFixed(2)));
				}
				if(lateFee != undefined){
					$('#latefee_cgst').val(formatNumber(lateFee.cLamt.toFixed(2)));
					$('#latefee_sgst').val(formatNumber(lateFee.sLamt.toFixed(2)));
					$('#latefee_debitno').val(lateFee.debitId);
				}
			}
		});
		
}
function addGSTR6_row(invtype){
	invRowCount++;
	var table = null;
	var rowPrefix = null;var row = null;
	var table_len=invRowCount;
	table = document.getElementById("dbTable1st");
	//rowPrefix = "<tr id='"+table_len+"' class=''><td class='form-group text-right'><input type='text' class='form-control' id='gstr6_gstin"+table_len+"'/></td><td align='left' class='form-group'><input type='text' class='form-control' id='gstr6_invoiceno"+table_len+"'/></td><td align='left' class='form-group'><input type='text' class='form-control' id='gstr6_invdate"+table_len+"'/></td><td align='right' class='form-group'><input type='text' class='form-control text-right' id='gstr6_Totalval"+table_len+"'/></td><td align='right' class='form-group'><input type='text' class='form-control text-right' id='gstr6_rate"+table_len+"'/></td><td align='right' class='form-group'><input type='text' class='form-control text-right' id='gstr6_taxval"+table_len+"'/></td><td align='right' class='form-group'><input type='text' class='form-control text-right' id='gstr6_cgst"+table_len+"'/></td><td align='right' class='form-group'><input type='text' class='form-control text-right' id='gstr6_sgst"+table_len+"'/></td><td align='right' class='form-group'><input type='text' class='form-control text-right' id='gstr6_igst"+table_len+"'/></td><td align='right' class='form-group'><input type='text' class='form-control text-right' id='gstr6_cess"+table_len+"'/></td></tr>";
	rowPrefix = "<tr id='"+table_len+"' class=''><td class='form-group text-right'><input type='text' class='form-control' id='gstr6_gstin"+table_len+"'/></td><td align='left' class='form-group'><input type='text' class='form-control' id='gstr6_invoiceno"+table_len+"'/></td><td align='left' class='form-group'><input type='text' class='form-control' id='gstr6_invdate"+table_len+"'/></td><td align='right' class='form-group'><input type='text' class='form-control text-right' id='gstr6_Totalval"+table_len+"'/></td><td align='right' class='form-group'><input type='text' class='form-control text-right' id='gstr6_taxval"+table_len+"'/></td><td align='right' class='form-group'><input type='text' class='form-control text-right' id='gstr6_cgst"+table_len+"'/></td><td align='right' class='form-group'><input type='text' class='form-control text-right' id='gstr6_sgst"+table_len+"'/></td><td align='right' class='form-group'><input type='text' class='form-control text-right' id='gstr6_igst"+table_len+"'/></td><td align='right' class='form-group'><input type='text' class='form-control text-right' id='gstr6_cess"+table_len+"'/></td></tr>";
	row = table.insertRow(table_len+1).outerHTML=rowPrefix;
	$('#dbTable1st').tableDnDUpdate();
}
function addAmdRow(){
	b2bainvRowCount++;
	var table = null;
	var rowPrefix = null;var row = null;
	var table_len=b2bainvRowCount;
	table = document.getElementById("Amd_dbTable1");
	rowPrefix = "<tr id='"+table_len+"' class=''><td class='form-group text-right'><input type='text' class='form-control' id='table6_b2ba_ogstin"+table_len+"'/></td><td class='form-group text-right'><input type='text' class='form-control' id='table6_b2ba_ogstin"+table_len+"'/></td><td class='form-group text-right'><input type='text' class='form-control' id='table6_b2ba_ogstin"+table_len+"'/></td><td class='form-group text-right'><input type='text' class='form-control' id='table6_b2ba_ogstin"+table_len+"'/></td><td class='form-group text-right'><input type='text' class='form-control' id='table6_b2ba_ogstin"+table_len+"'/></td><td class='form-group text-right'><input type='text' class='form-control' id='table6_b2ba_ogstin"+table_len+"'/></td><td class='form-group text-right'><input type='text' class='form-control' id='table6_b2ba_ogstin"+table_len+"'/></td><td class='form-group text-right'><input type='text' class='form-control' id='table6_b2ba_ogstin"+table_len+"'/></td><td class='form-group text-right'><input type='text' class='form-control' id='table6_b2ba_ogstin"+table_len+"'/></td><td class='form-group text-right'><input type='text' class='form-control' id='table6_b2ba_ogstin"+table_len+"'/></td><td class='form-group text-right'><input type='text' class='form-control' id='table6_b2ba_ogstin"+table_len+"'/></td><td class='form-group text-right'><input type='text' class='form-control' id='table6_b2ba_ogstin"+table_len+"'/></td></tr>";
	row = table.insertRow(table_len+1).outerHTML=rowPrefix;
	$('#Amd_dbTable1').tableDnDUpdate();
}
function addTable5_row(){
	itcRowCount++;
	var table = null;
	var rowPrefix = null;var row = null;
	var table_len=itcRowCount;
	table = document.getElementById("dbTable2");
	//rowPrefix = "<tr id='"+table_len+"' class=''><td class='form-group text-right'><input type='text' class='form-control' id='gstr6_gstin"+table_len+"'/></td><td align='left' class='form-group'><input type='text' class='form-control' id='gstr6_invoiceno"+table_len+"'/></td><td align='left' class='form-group'><input type='text' class='form-control' id='gstr6_invdate"+table_len+"'/></td><td align='right' class='form-group'><input type='text' class='form-control text-right' id='gstr6_Totalval"+table_len+"'/></td><td align='right' class='form-group'><input type='text' class='form-control text-right' id='gstr6_rate"+table_len+"'/></td><td align='right' class='form-group'><input type='text' class='form-control text-right' id='gstr6_taxval"+table_len+"'/></td><td align='right' class='form-group'><input type='text' class='form-control text-right' id='gstr6_cgst"+table_len+"'/></td><td align='right' class='form-group'><input type='text' class='form-control text-right' id='gstr6_sgst"+table_len+"'/></td><td align='right' class='form-group'><input type='text' class='form-control text-right' id='gstr6_igst"+table_len+"'/></td><td align='right' class='form-group'><input type='text' class='form-control text-right' id='gstr6_cess"+table_len+"'/></td></tr>";
	rowPrefix = "<tr id='"+table_len+"' class=''><td class='form-group text-right'><input type='text' class='form-control' id='elg5A_gstin"+table_len+"'/></td><td align='left' class='form-group'><input type='text' class='form-control' id='elg5A_invoiceno"+table_len+"'/></td><td align='left' class='form-group'><input type='text' class='form-control' id='elg5A_invoicedate"+table_len+"'/></td><td align='right' class='form-group'><input type='text' class='form-control text-right' id='elg5A_cgst"+table_len+"'/></td><td align='right' class='form-group'><input type='text' class='form-control text-right' id='elg5A_sgst"+table_len+"'/></td><td align='right' class='form-group'><input type='text' class='form-control text-right' id='elg5A_igst"+table_len+"'/></td><td align='right' class='form-group'><input type='text' class='form-control text-right' id='elg5A_cess"+table_len+"'/></td></tr>";
	row = table.insertRow(table_len+1).outerHTML=rowPrefix;
	$('#dbTable2').tableDnDUpdate();
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