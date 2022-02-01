
<!DOCTYPE html>
<html lang="en">
<title>MasterGST - Error Codes</title>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script type="text/javascript">
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-98474020-1', 'auto');
  ga('send', 'pageview');

</script>
</head>

<body lang=EN-US link=blue vlink="#954F72" style='tab-interval:.5in'>

<div class="container">
<h4 class="text-center">API Error Codes</h4>
<table class="table table-bordered" style="margin-top:10px">
    <thead>
      <tr>
        <th>Error Code</th>
		<th>Error Description</th>
	  </tr>
	</thead>
	<tbody>
	<tr><td>RET11400 	</td> <td>Header Value Missing</td>                                                               </tr>
<tr><td>RET11403 	</td> <td>Invalid API Request </td>                                                                   </tr>
<tr><td>RET11402 	</td> <td>Unauthorized User   </td>                                                                   </tr>
<tr><td>RET11404 	</td> <td>Stae Code is not valid   </td>                                                              </tr>
<tr><td>RET11409 	</td> <td>Username is not Valid    </td>                                                              </tr>
<tr><td>RET11407 	</td> <td>AuthToken is invalid     </td>                                                              </tr>
<tr><td>RET11420 	</td> <td>Invalid API Header value </td>                                                              </tr>
<tr><td>RET11410 	</td>  <td>Invalid GSTIN           </td>                                                              </tr>
<tr><td>AUTH113	 	</td>  <td>Invalid Return Period   </td>                                                              </tr>
<tr><td>AUTH117	 	</td>  <td>GSTR1 is already filed for current period                </td>                             </tr>
<tr><td>AUTH141	 	</td>  <td>Mandatory Parameters - Gstin or Return Period missing    </td>                             </tr>
<tr><td>AUTH119	 	</td>  <td>GSTR3 is not filed for previous period                   </td>                             </tr>
<tr><td>AUTH150	 	</td>  <td>GST Practioner is not a valid user                       </td>                             </tr>
<tr><td>AUTH143	 	</td>  <td>Invalid request parameters                               </td>                             </tr>
<tr><td>RET12505 	</td>  <td>Corrupted API Payload Data                               </td>                             </tr>
<tr><td>RET13506 	</td>  <td>Invalid DSC Signature                                    </td>                             </tr>
<tr><td>RET13507    </td>	<td>Mismatch of signed data and payload                     </td>                             </tr>
<tr><td>RT_FIL_02	</td>	<td>GSTR1 is already filed                                  </td>                             </tr>
<tr><td>RET13501	</td>	<td>Checksum mismatch while filing and submit               </td>                             </tr>
<tr><td>RT_FIL_24	</td>	<td>Some Error Occured while saving DSC data.Please try again. </td>                          </tr>
<tr><td>RET13504	</td>	<td>Unable to process your request. Please try after sometime  </td>                          </tr>
<tr><td>RET12521	</td>	<td>GSTR1 is already submitted for current period              </td>                          </tr>
<tr><td>RET12590	</td>  <td>GSTR-1 Save/Action on Counterparty Invoices / Submit are not allowed for current date,Please visit https: //www.gst.gov.in for details on GSTR-1 timelines</td></tr>
<tr><td>RTDSC04		</td>  <td>Pan Number or Sign is invalid                                  </td>                         </tr>
<tr><td>RTDSC05		</td>  <td>DSC verification failed.Please Try after sometime              </td>                         </tr>
<tr><td>AUTH151		</td>  <td>You are not authoried to access GSTR1 for this return period   </td>                         </tr>
<tr><td>RET13505	</td>  <td>System Failure                                                 </td>                         </tr>
<tr><td>RET13509	</td>  <td>OTP is either expired or incorrect                             </td>                         </tr>
<tr><td>RT_FIL_09	</td>  <td>Signed summary is not the latest one                           </td>                         </tr>
<tr><td>RT_FIL_10	</td>  <td>Please submit the invoices before filing                       </td>                         </tr>
<tr><td>RTN_11		</td>  <td>GSTR1 submit is under process                                  </td>                         </tr>
<tr><td>RT_FIL_017	</td>  <td>User Is not Registered to any DSC                              </td>                         </tr>
<tr><td>RT_FIL_018	</td>  <td>Pan is not registered for the given gstin                      </td>                         </tr>
<tr><td>RT_FIL_31	</td>  <td>User is not authorized to File with EVC Option                 </td>                         </tr>
<tr><td>RTN_02		</td>  <td>Invalid Return Date                                            </td>                         </tr>
<tr><td>RET13508	</td>  <td>No Details Found for the Provided Inputs                       </td>                         </tr>
<tr><td>RET13511	</td>  <td>Date format entered is Invalid                                 </td>                         </tr>
<tr><td>RET11408	</td>  <td>Invalid Transaction ID                                         </td>                         </tr>
<tr><td>RTN_15		</td>  <td>Returns still under processing                                 </td>                         </tr>
<tr><td>RTN_17		</td>  <td>Invoices already Submitted                                     </td>                         </tr>
<tr><td>RET12523	</td>  <td>GSTR1 submit is already in progress for current period         </td>                         </tr>
<tr><td>RTN_24		</td>  <td>File Generation is in progress, please try after sometime.     </td>                         </tr>
<tr><td>RTN_25		</td>  <td>Error In File Generation                                       </td>                         </tr>
<tr><td>RTN_27		</td>  <td>The User is not a Registered Normal Taxpayer                   </td></tr>
<tr><td>RTN_31		</td>  <td>File is Generated, please click the download button to download the file.                                               </td></tr>
<tr><td>RTN_32		</td>  <td>Download request already in progress, please wait for the file to be generated.                                         </td></tr>
<tr><td>RTN_FIL_28	</td>  <td>Certificate has Expired                                                                                                 </td></tr>
<tr><td>RTN_FIL_29	</td>  <td>Certificate is not valid                                                                                                </td></tr>
<tr><td>RTN_FIL_30	</td>  <td>Signed summary data does not matches with encoded hash summary                                                          </td></tr>
<tr><td>RT_FIL_25	</td>  <td>Summary Checksum generation is in progress.Please Try after sometime                                                    </td></tr>
<tr><td>RET191101	</td>  <td>Decrypt request Failed. Values entered are null                                                                         </td></tr>
<tr><td>RET191103	</td>  <td>Corrupted Data or File                                                                                                  </td></tr>
<tr><td>RET191104	</td>  <td>GstRequest is null                                                                                                      </td></tr>
<tr><td>RET191106	</td>  <td>Error in Json structure validation                                                                                      </td></tr>
<tr><td>RET191107	</td>  <td>Atleast one line item should be present .                                                                               </td></tr>
<tr><td>RET191110	</td>  <td>Approval date for the given GSTIN is not present.                                                                       </td></tr>
<tr><td>RET191111	</td>  <td>Invoice No. <invoice number> already exist in GSTR 1 of <retPrd>. Do enter the correct invoice number.                  </td></tr>
<tr><td>RET191112	</td>  <td>Do enter the correct State code in POS.                                                                                 </td></tr>
<tr><td>RET191113	</td>  <td>GSTIN you entered is same as counter Party GSTIN. Do enter the correct GSTIN.                                           </td></tr>
<tr><td>RET191113	</td>  <td>You are not authoried to access GSTR1 for this return period                                                            </td></tr>
<tr><td>RET191113	</td>  <td>The GSTIN is invalid. Please enter a valid GSTIN.                                                                       </td></tr>
<tr><td>RET191114	</td>  <td>Invoice date cannot be older than the registration date of the supplier TIN.                                            </td></tr>
<tr><td>RET191114	</td>  <td>Invoice date cannot be later than the return period or older than 18 months.                                            </td></tr>
<tr><td>RET191115	</td>  <td>Following invoice has linked credit/debit note(s). Do delete the linked credit/debit note(s) before deleting the invoice.                  </td></tr>
<tr><td>RET191117	</td>  <td>Do not repeat the same rate for the invoice                                                                                                </td></tr>
<tr><td>RET191117	</td>  <td>Combination of HSN , Description and Uqc can not be repeatable. Please aggregate                                                           </td></tr>
<tr><td>RET191119	</td>  <td>GSTIN of Receiver does not match the original invoice records.                                                                             </td></tr>
<tr><td>RET191120	</td>  <td>Date is Invalid. Revised invoice date cannot be later than the last day of the tax period when original invoice was uploaded.              </td></tr>
<tr><td>RET191121	</td>  <td>Revised Invoice No.(s) <invoice number> already exist in GSTR 1 of <retPrd>                                                                </td></tr>
<tr><td>RET191122	</td>  <td>Do enter the correct recipient State Code.                                                                                                 </td></tr>
<tr><td>RET191123	</td>  <td>Note No.(s) <note number> already exist in for the return period <retPrd>                                                                  </td></tr>
<tr><td>RET191124	</td>  <td>Invalid Original Invoice Details. Original invoice cannot be tracked. Please enter correct invoice number and date                         </td></tr>
<tr><td>RET191125	</td>  <td>Counter party of Invoice and Credit/Debit Note are different. Please enter a valid GSTIN or State code.                                    </td></tr>
<tr><td>RET191126	</td>  <td>Note Date is Invalid. Date of note cannot exceed the current tax period and be older than 18 months                                        </td></tr>
<tr><td>RET191126	</td>  <td>Note Date is Invalid. Date of note cannot be before registration date.                                                                     </td></tr>
<tr><td>RET191131	</td>  <td>Either GSTIN or return period is missing                                                                                                   </td></tr>
<tr><td>RET191133	</td>  <td>Invoice already exists with different CTIN or same CTIN. Please delete the existing invoice and upload again.                              </td></tr>
<tr><td>RET191134	</td>  <td>Invoice may already exist in another section. Do enter the correct invoice number or delete existing invoice and upload again              </td></tr>
<tr><td>RET191134	</td>  <td>Credit/Debit Note number may already exist in another section. Do enter it correctly or delete existing Credit/Debit note number and upload again</td></tr>
<tr><td>RET191135	</td>  <td>Invoice already exist with different state code. Please delete the existing invoice and upload again                                        </td></tr>
<tr><td>RET191136	</td>  <td>Either Return period or GSTIN from the header and payload are different                                                                     </td></tr>
<tr><td>RET191138	</td>  <td>Error in Processing the request                                                                                                             </td></tr>
<tr><td>RET191139	</td>  <td>Decoded payload is null                                                                                                                     </td></tr>
<tr><td>RET191140	</td>  <td>Error while reading a file                                                                                                                  </td></tr>
<tr><td>RET191141	</td>  <td>Exception while getting the data from dist cache or sql table or hbase table                                                                </td></tr>
<tr><td>RET191141	</td>  <td>Exception while getting the data from Cache                                                                                                 </td></tr>
<tr><td>RET191143	</td>  <td>Mismatch noted in the checksum. Please enter the correct checksum to take action                                                            </td></tr>
<tr><td>RET191148	</td>  <td>No section data or Gross Turnover is available to process the request                                                                       </td></tr>
<tr><td>RET191150	</td>  <td>IGST is required for interstate supply. Do remove CGST and SGST as they are not applicable.                                                 </td></tr>
<tr><td>RET191150	</td>  <td>CGST and SGST is required for intrastate supply. Do remove IGST as they are not applicable.                                                 </td></tr>
<tr><td>RET191150	</td>  <td>CGST and SGST should not be present for INTER state supply: for the Supplier TIN : <stin> and the Place of suply is : <pos>                 </td></tr>
<tr><td>RET191150	</td>  <td>CGST and SGST values are mondatory for INTRA state supply: for the Supplier TIN : <stin> and the Place of suply is : <pos>                  </td></tr>
<tr><td>RET191150	</td>  <td>IGST should not be present for INTRA state supply: for the Supplier TIN : <stin> and the Place of Supply is : <pos>                         </td></tr>
<tr><td>RET191150	</td>  <td>IGST is mandatory for INTER state supply: for the Supplier TIN : <stin> and the Place of Supply is : <pos>                                  </td></tr>
<tr><td>RET191150	</td>  <td>Error while trying to determine invoice is of intrastate or interstate supply                                                               </td></tr>
<tr><td>RET191150	</td>  <td>IGST must be present for With Pay transaction but none of amounts are required for Without Pay transaction                                  </td></tr>
<tr><td>RET191150	</td>  <td>IGST is required for With Pay Exports transactions. Do remove CGST and SGST as it is not applicable.                                        </td></tr>
<tr><td>RET191150	</td>  <td>IGST , CGST & SGST amounts are not required for Without Pay transaction                                                                     </td></tr>
<tr><td>RET191150	</td>  <td>IGST is required for Deemed Exports transactions. Do remove CGST and SGST as it is not applicable.                                          </td></tr>
<tr><td>RET191151	</td>  <td>Do enter the correct invoice date for credit/debit transaction that matches with the original invoice date.                                 </td></tr>
<tr><td>RET191152	</td>  <td>The ETIN is invalid. Please enter a valid Ecommerce GSTIN.                                                                                  </td></tr>
<tr><td>RET191152	</td>  <td>The ETIN is invalid. The Ecommerce GSTIN you entered is same as Supplier GSTIN.                                                             </td></tr>
<tr><td>RET191152	</td>  <td>Do enter the correct Ecommerce GSTIN for B2CS supplies made through e-commerce operator                                                     </td></tr>
<tr><td>RET191152	</td>  <td>Ecommerce GSTIN cannot be present for B2CS supplies made through non e-commerce operator.                                                   </td></tr>
<tr><td>RET191153	</td>  <td>Original Invoice is of Type Intra State. Please enter a valid invoice of same supply type                                                   </td></tr>
<tr><td>RET191154	</td>  <td>Original Invoice is of Type Inter State. Please enter a valid invoice of same supply type                                                   </td></tr>
<tr><td>RET191157	</td>  <td>Orignal data for B2CS doesn't exsist.                                                                                                       </td></tr>
<tr><td>RET191158	</td>  <td>Original Invoice date entered is invalid. Please enter a valid invoice date.                                                                </td></tr>
<tr><td>RET191159	</td>  <td>Original note cannot be tracked. Please enter correct note number and date.                                                                 </td></tr>
<tr><td>RET191159	</td>  <td>Original note is not amendable. Please enter different note number and date.                                                                </td></tr>
<tr><td>RET191159	</td>  <td>Revised note is duplicate. Please enter different note number and date.                                                                     </td></tr>
<tr><td>RET191160	</td>  <td>State code of Supplier and POS/Receiver state code cannot be same for B2CL. Do enter the correct State Codes.                               </td></tr>
<tr><td>RET191164	</td>  <td>System Component failure                                                                                                                    </td></tr>
<tr><td>RET191165	</td>  <td>Request Payload mapping failure                                                                                                             </td></tr>
<tr><td>RET191166	</td>  <td>Decryption/Decoding failed                                                                                                                  </td></tr>
<tr><td>RET191168	</td>  <td>Orignal data for AT can't be tracked.Kindly enter valid AT details.                                                                         </td></tr>
<tr><td>RET191170	</td>  <td>Do file the invoice before accepting/rejecting.                                                                                             </td></tr>
<tr><td>RET191171	</td>  <td>Do file the note before accepting/rejecting.                                                                                                </td></tr>
<tr><td>RET191172	</td>  <td>Do enter an item number that does not exist in the payload.                                                                                 </td></tr>
<tr><td>RET191175	</td>  <td>Do enter the correct rate as per the rate list.                                                                                             </td></tr>
<tr><td>RET191176	</td>  <td>Do enter the correct shipping bill date that is on or after Invoice Date and on or before today's date.                                     </td></tr>
<tr><td>RET191178	</td>  <td>Supplier state code and place of supply (POS) cannot be different for intrastate supply.                                                    </td></tr>
<tr><td>RET191179	</td>  <td>Supplier state code and place of supply (POS) cannot be same for interstate supply.                                                         </td></tr>
<tr><td>RET191180	</td>  <td>B2CL invoice value cannot be less than 2.5 lakhs.                                                                                           </td></tr>
<tr><td>RET191181	</td>  <td>Do select the correct invoice type that matches with the original invoice type.                                                             </td></tr>
<tr><td>RET191182	</td>  <td>Submit Request is in progress, Uploaded request can not be processed                                                                        </td></tr>
<tr><td>RET191183	</td>  <td>GSTR1 has either been filed or submitted, Hence,uploaded request can not be processed                                                       </td></tr>
<tr><td>RET191184	</td>  <td>Invoice date must be prior to July 1st 2017 for Pre GSTIN</td></tr>
<tr><td>RET191185	</td>  <td>Return Period is future date. Kindly provide valid return period                                                                            </td></tr>
<tr><td>RET191188	</td>  <td>Invalid Note reason                                                                                                                         </td></tr>
<tr><td>RET191189	</td>  <td>B2CS for SEZ gstin should be interstate.                                                                                                    </td></tr>
<tr><td>RET191189	</td>  <td>SEZ gstin should always be an interstate.                                                                                                   </td></tr>
<tr><td>RET191190	</td>  <td>Duplicate Invoice number '<inv-number>' found in the payload. Please correct.                                                               </td></tr>
<tr><td>RET191191	</td>  <td>Processing Retry timeout                                                                                                                    </td></tr>
<tr><td>RET191192	</td>  <td>Processing Retry timeout, Failed to process the section(s)                                                                                  </td></tr>
<tr><td>RET191193	</td>  <td>Refund note type is not allowed now                                                                                                         </td></tr>
<tr><td>RET191194	</td>  <td>Either HSN or DESC is mandatory.                                                                                                            </td></tr>
<tr><td>RET191195	</td>  <td>The UQC entered is not valid                                                                                                                </td></tr>
<tr><td>RET191196	</td>  <td>Revised Invoice Number is not Valid                                                                                                         </td></tr>
<tr><td>RET191197	</td>  <td>Orignal data for TXPD can't be tracked.Kindly enter valid TXPD details.                                                                     </td></tr>
<tr><td>RET191198	</td>  <td>Data can not be amended for the same return period.                                                                                         </td></tr>
<tr><td>RET191199	</td>  <td>Invoice type can not be changed in CDN Amendments.Kindly enter same invoice type as orignal CDN .                                           </td></tr>
<tr><td>RET191200	</td>  <td>Supply Type can not be changed for saved CDN .Kindly enter same supply type as orignal CDN .                                                </td></tr>
<tr><td>RET191202	</td>  <td>Invoice date should be before note date and return period.                                                                                  </td></tr>
<tr><td>RET191203	</td>  <td>The invoice number entered is invalid. Please enter a valid invoice number                                                                  </td></tr>
<tr><td>RET191203	</td>  <td>The invoice is of type <typ>. Please enter the valid invoice type                                                                           </td></tr>
<tr><td>RET191204	</td>  <td>Please select the preference first and then proceed                                                                                         </td></tr>
	</tbody>
	
</table>

</div>

</body>

</html>
