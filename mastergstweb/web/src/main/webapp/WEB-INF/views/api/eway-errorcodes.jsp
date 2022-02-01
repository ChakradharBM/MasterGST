
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

<div class="container " >
<table class="table table-bordered" style="margin-top:10px">
    <thead>
      <tr>
        <th>Error Code</th>
		<th>Error Description</th>
	  </tr>
	</thead>
	<tbody>
<tr><td>100</td>  <td>Invalid Json																														</td></tr>
<tr><td>101</td>  <td>Invalid Username																													</td></tr>	
<tr><td>102</td>  <td>Invalid Password					                                                                                                </td></tr>
<tr><td>104</td>  <td>Invalid Client -Id                                                                                                                </td></tr>
<tr><td>105</td>  <td>Invalid Token                                                                                                                     </td></tr>
<tr><td>106</td>  <td>Token Expired                                                                                                                     </td></tr>
<tr><td>107</td>  <td>Authentication failed. Pls. inform the helpdesk                                                                                   </td></tr>
<tr><td>108</td>  <td>Invalid login credentials.                                                                                                        </td></tr>
<tr><td>109</td>  <td>Decryption of data failed                                                                                                         </td></tr>
<tr><td>110</td>  <td>Invalid Client-ID/Client-Secret                                                                                                   </td></tr>
<tr><td>111</td>  <td>GSTIN is not registered to this GSP                                                                                               </td></tr>
<tr><td>112</td>  <td>IMEI does not belong to the user                                                                                               </td></tr>
<tr><td>113</td>  <td>os-type is mandatory in header                                                                                               </td></tr>
<tr><td>112</td>  <td>Invalid os-type parameter value                                                                                               </td></tr>
<tr><td>201</td>  <td>Invalid Supply Type                                                                                                               </td></tr>
<tr><td>202</td>  <td>Invalid Sub-supply Type                                                                                                           </td></tr>
<tr><td>203</td>  <td>Sub-transaction type does not belongs to transaction type                                                                         </td></tr>
<tr><td>204</td>  <td>Invalid Document type                                                                                                             </td></tr>
<tr><td>205</td>  <td>Document type does not match with transaction & Sub trans type                                                                    </td></tr>
<tr><td>206</td>  <td>Invalid Invoice Number                                                                                                            </td></tr>
<tr><td>207</td>  <td>Invalid Invoice Date                                                                                                              </td></tr>
<tr><td>208</td>  <td>Invalid Supplier GSTIN                                                                                                            </td></tr>
<tr><td>209</td>  <td>Blank Supplier Address                                                                                                            </td></tr>
<tr><td>210</td>  <td>Invalid or Blank Supplier PIN Code                                                                                                </td></tr>
<tr><td>211</td>  <td>Invalid or Blank Supplier state Code                                                                                              </td></tr>
<tr><td>212</td>  <td>Invalid Consignee GSTIN                                                                                                           </td></tr>
<tr><td>213</td>  <td>Invalid Consignee Address                                                                                                         </td></tr>
<tr><td>214</td>  <td>Invalid Consignee PIN Code                                                                                                        </td></tr>
<tr><td>215</td>  <td>Invalid Consignee State Code                                                                                                      </td></tr>
<tr><td>216</td>  <td>Invalid HSN Code                                                                                                                  </td></tr>
<tr><td>217</td>  <td>Invalid UQC Code                                                                                                                  </td></tr>
<tr><td>218</td>  <td>Invalid Tax Rate for Intra State Transaction                                                                                      </td></tr>
<tr><td>219</td>  <td>Invalid Tax Rate for Inter State Transaction                                                                                      </td></tr>
<tr><td>220</td>  <td>Invalid Trans mode                                                                                                                </td></tr>
<tr><td>221</td>  <td>Invalid Approximate Distance                                                                                                      </td></tr>
<tr><td>222</td>  <td>Invalid Transporter Id                                                                                                            </td></tr>
<tr><td>223</td>  <td>Invalid Transaction Document Number                                                                                               </td></tr>
<tr><td>224</td>  <td>Invalid Transaction Date                                                                                                          </td></tr>
<tr><td>225</td>  <td>Invalid Vehicle Number Format                                                                                                     </td></tr>
<tr><td>226</td>  <td>Both Transaction and Vehicle Number Blank                                                                                         </td></tr>
<tr><td>227</td>  <td>User Gstin cannot be blank                                                                                                        </td></tr>
<tr><td>228</td>  <td>User id cannot be blank                                                                                                           </td></tr>
<tr><td>229</td>  <td>Supplier name is required                                                                                                         </td></tr>
<tr><td>230</td>  <td>Supplier place is required                                                                                                        </td></tr>
<tr><td>231</td>  <td>Consignee name is required                                                                                                        </td></tr>
<tr><td>232</td>  <td>Consignee place is required                                                                                                       </td></tr>
<tr><td>233</td>  <td>Eway bill does not contains any items                                                                                             </td></tr>
<tr><td>234</td>  <td>Total amount/Taxable amount is mandatory                                                                                          </td></tr>
<tr><td>235</td>  <td>Tax rates for Intra state transaction is blank                                                                                    </td></tr>
<tr><td>236</td>  <td>Tax rates for Inter-state transaction is blank                                                                                    </td></tr>
<tr><td>237</td>  <td>Invalid client -Id/client-secret                                                                                                  </td></tr>
<tr><td>238</td>  <td>Invalid auth token                                                                                                                </td></tr>
<tr><td>239</td>  <td>Invalid action                                                                                                                    </td></tr>
<tr><td>240</td>  <td>Could not generate eway bill, pls contact helpdesk                                                                                </td></tr>
<tr><td>242</td>  <td>Invalid or Blank Officer StateCode                                                                                                </td></tr>
<tr><td>243</td>  <td>Invalid or Blank IR Number                                                                                         				</td></tr>
<tr><td>244</td>  <td>Invalid or Blank Actual Vehicle Number Format                                 		                                            </td></tr>
<tr><td>245</td>  <td>Invalid Verification Date Format					                                                                                </td></tr>
<tr><td>246</td>  <td>Invalid Vehicle Release Date Format                                                                               				</td></tr>
<tr><td>247</td>  <td>Invalid Verification Time Format                                                                                					</td></tr>
<tr><td>248</td>  <td>Invalid Vehicle Release Date Format                                                                                				</td></tr>
<tr><td>249</td>  <td>Actual Value cannot be less than or equal to zero                                                                                 </td></tr>
<tr><td>250</td>  <td>Invalid Vehicle Release Date Format                                                                                				</td></tr>
<tr><td>251</td>  <td>CGST nad SGST TaxRate should be same                                                                                				</td></tr>
<tr><td>252</td>  <td>Invalid CGST Tax Rate                                                                                								</td></tr>
<tr><td>253</td>  <td>Invalid SGST Tax Rate                                                                                								</td></tr>
<tr><td>254</td>  <td>Invalid IGST Tax Rate                                                                                								</td></tr>
<tr><td>255</td>  <td>Invalid CESS Rate                                                                                									</td></tr>
<tr><td>256</td>  <td>Invalid Cess Non Advol value                                                                                						</td></tr>
<tr><td>278</td>  <td>User Gstin does not match with Transporter Id                                                                                		</td></tr>
<tr><td>280</td>  <td>Status is not ACTIVE                                                                                								</td></tr>
<tr><td>281</td>  <td>Eway Bill is already expired hence update transporter is not allowed                                                              </td></tr>
<tr><td>301</td>  <td>Invalid eway bill number                                                                                                          </td></tr>
<tr><td>302</td>  <td>Invalid transporter mode                                                                                                          </td></tr>
<tr><td>303</td>  <td>Vehicle number is required                                                                                                        </td></tr>
<tr><td>304</td>  <td>Invalid vehicle format                                                                                                            </td></tr>
<tr><td>305</td>  <td>Place from is required                                                                                                            </td></tr>
<tr><td>306</td>  <td>Invalid from state                                                                                                                </td></tr>
<tr><td>307</td>  <td>Invalid reason                                                                                                                    </td></tr>
<tr><td>308</td>  <td>Invalid remarks                                                                                                                   </td></tr>
<tr><td>309</td>  <td>Could not update vehicle details, pl contact helpdesk                                                                             </td></tr>
<tr><td>311</td>  <td>Validity period lapsed, you cannot update vehicle details                                                                         </td></tr>
<tr><td>312</td>  <td>This eway bill is either not generated by you or cancelled                                                                        </td></tr>
<tr><td>313</td>  <td>Error in validating ewaybill for vehicle updation			                                                                        </td></tr>
<tr><td>315</td>  <td>Validity period lapsed, you cannot cancel this eway bill                                                                          </td></tr>
<tr><td>316</td>  <td>Eway bill is already verified, you cannot cancel it                                                                               </td></tr>
<tr><td>317</td>  <td>Could not cancel eway bill, please contact helpdesk                                                                               </td></tr>
<tr><td>320</td>  <td>Invalid state to                                                                                                                  </td></tr>
<tr><td>321</td>  <td>Invalid place to                                                                                                                  </td></tr>
<tr><td>322</td>  <td>Could not generate consolidated eway bill                                                                                         </td></tr>
<tr><td>325</td>  <td>Could not retrieve data                                                                                                           </td></tr>
<tr><td>326</td>  <td>Could not retrieve GSTIN details for the given GSTIN number                                                                       </td></tr>
<tr><td>327</td>  <td>Could not retrieve data from hsn                                                                                                  </td></tr>
<tr><td>328</td>  <td>Could not retrieve transporter details from gstin                                                                                 </td></tr>
<tr><td>329</td>  <td>Could not retrieve States List                                                                                                    </td></tr>
<tr><td>330</td>  <td>Could not retrieve UQC list                                                                                                       </td></tr>
<tr><td>331</td>  <td>Could not retrieve Error code                                                                                                     </td></tr>
<tr><td>334</td>  <td>Could not retrieve user details by userid                                                                                         </td></tr>
<tr><td>336</td>  <td>Could not retrieve transporter data by gstin                                                                                      </td></tr>
<tr><td>337</td>  <td>Could not retrieve HSN details for the given HSN number                                                                           </td></tr>
<tr><td>338</td>  <td>You cannot update transporter details, as the current tranporter is already entered Part B details of the eway bil                </td></tr>
<tr><td>339</td>  <td>You are not assigned to update the tranporter details of this eway bill                                                           </td></tr>
<tr><td>341</td>  <td>This e-way bill is generated by you and hence you cannot reject it                                                                </td></tr>
<tr><td>342</td>  <td>You cannot reject this e-way bill as you are not the other party to do so                                                         </td></tr>
<tr><td>343</td>  <td>This e-way bill is cancelled                                                                          							</td></tr>
<tr><td>344</td>  <td>Invalid eway bill number                                                                           								</td></tr>
<tr><td>345</td>  <td>Validity period lapsed, you cannot reject the e-way bill                                                                          </td></tr>
<tr><td>346</td>  <td>You can reject the e-way bill only within 72 hours from generated timel                                                           </td></tr>
<tr><td>347</td>  <td>Validation of eway bill number failed, while rejecting ewaybill                                                                   </td></tr>
<tr><td>348</td>  <td>Part-B is not generated for this e-way bill, hence rejection is not allowed                                                       </td></tr>
<tr><td>350</td>  <td>Could not generate consolidated eway bill                                                                                         </td></tr>
<tr><td>351</td>  <td>Invalid state code                                                                                                                </td></tr>
<tr><td>352</td>  <td>Invalid rfid date                                                                                                                 </td></tr>
<tr><td>353</td>  <td>Invalid location code                                                                                                             </td></tr>
<tr><td>354</td>  <td>Invalid rfid number                                                                                                               </td></tr>
<tr><td>355</td>  <td>Invalid Vehicle Number Format                                                                                                     </td></tr>
<tr><td>356</td>  <td>Invalid wt on bridge                                                                                                              </td></tr>
<tr><td>357</td>  <td>Could not retrieve eway bill details, pl. contact helpdesk                                                                        </td></tr>
<tr><td>358</td>  <td>GSTIN passed in request header is not matching with the user gstin mentioned in payload JSON                                      </td></tr>
<tr><td>359</td>  <td>User GSTIN should match to GSTIN(from) for outward transactions                                                                   </td></tr>
<tr><td>360</td>  <td>User GSTIN should match to GSTIN(to) for inward transactions                                                                      </td></tr>
<tr><td>361</td>  <td>Invalid Vehicle Type                                                                                                              </td></tr>
<tr><td>362</td>  <td>Transporter document date cannot be earlier than the invoice date                                                                 </td></tr>
<tr><td>363</td>  <td>E-way bill is not enabled for intra state movement for you state                                                                  </td></tr>
<tr><td>364</td>  <td>Error in verifying eway bill                                                                                                      </td></tr>
<tr><td>365</td>  <td>Error in verifying consolidated eway bill                                                                                         </td></tr>
<tr><td>366</td>  <td>You will not get the ewaybills generated today, howerver you cann access the ewaybills of yester days                             </td></tr>
<tr><td>367</td>  <td>Could not retrieve data for officer login                                                                                         </td></tr>
<tr><td>368</td>  <td>Could not update transporter                                                                                                      </td></tr>
<tr><td>369</td>  <td>GSTIN/Transin passed in request header should match with the transported Id mentioned in payload JSON                             </td></tr>
<tr><td>370</td>  <td>GSTIN/Transin passed in request header should not be the same as supplier(fromGSTIN) or recepient(toGSTIN)                        </td></tr>
<tr><td>371</td>  <td>Invalid or Blank Supplier Ship-to State Code                                                                                      </td></tr>
<tr><td>372</td>  <td>Invalid or Blank Consignee Ship-to State Code                                                                                     </td></tr>
<tr><td>373</td>  <td>The Supplier Bill-to state code should be Other Country for Sub Supply Type- Export                                               </td></tr>
<tr><td>374</td>  <td>The Consignee pin code should be 999999 for Sub Supply Type- Export                                                               </td></tr>
<tr><td>375</td>  <td>The Supplier Bill-from state code should be Other Country for Sub Supply Type- Import                                             </td></tr>
<tr><td>376</td>  <td>The Supplier pin code should be 999999 for Sub Supply Type- Import                                                                </td></tr>
<tr><td>377</td>  <td>Sub Supply Type is mentioned as Others, the description for that is mandatory                                                     </td></tr>
<tr><td>378</td>  <td>The supplier or consignee belong to SEZ, Inter state tax rates are applicable here                                                </td></tr>
<tr><td>379</td>  <td>Eway Bill cannot be extended.. Already Cancelled                                                                                  </td></tr>
<tr><td>380</td>  <td>Eway Bill Cannot be Extended. Not in Active State                                                                                 </td></tr>
<tr><td>381</td>  <td>There is No PART-B/Vehicle Entry.. So Please Update Vehicle Information..                                                         </td></tr>
<tr><td>382</td>  <td>You Cannot Extend as EWB can be Extended only 8 hour before or after w.r.t Validity of EWB..!!                                    </td></tr>
<tr><td>383</td>  <td>Error While Extending..Please Contact Helpdesk.                                                                                   </td></tr>
<tr><td>384</td>  <td>You are not current transporter or Generator of the ewayBill, with no transporter details.                                        </td></tr>
<tr><td>385</td>  <td>For Rail/Ship/Air transDocNo and transDocDate is mandatory                                                                        </td></tr>
<tr><td>386</td>  <td>Reason Code, Remarks is mandatory.                                                                                                </td></tr>
<tr><td>387</td>  <td>No Record Found for Entered consolidated eWay bill.                                                                               </td></tr>
<tr><td>388</td>  <td>Exception in regeneration of consolidated eWayBill!!Please Contact helpdesk                                                       </td></tr>
<tr><td>389</td>  <td>Remaining Distance Required                                                                                                       </td></tr>
<tr><td>390</td>  <td>Remaining Distance cannot be greater than Actual Distance.                                                                        </td></tr>
<tr><td>391</td>  <td>No eway bill of specified tripsheet, neither ACTIVE nor not Valid.                                                                </td></tr>
<tr><td>392</td>  <td>Tripsheet is already cancelled, Hence Regeration is not possible                                                                  </td></tr>
<tr><td>393</td>  <td>Invalid GSTIN                                                                                                                     </td></tr>
<tr><td>394</td>  <td>For other than Road Transport, TransDoc number is required                                                                        </td></tr>
<tr><td>395</td>  <td>Eway Bill Number should be numeric only                                                                                           </td></tr>
<tr><td>396</td>  <td>Either Eway Bill Number Or Consolidated Eway Bill Number is required for Verification                                             </td></tr>
<tr><td>397</td>  <td>Error in Multi Vehicle Movement Initiation                                                                                        </td></tr>
<tr><td>398</td>  <td>Eway Bill Item List is Empty                                                                                                      </td></tr>
<tr><td>399</td>  <td>Unit Code is not matching with any of the Unit Code from ItemList                                                                 </td></tr>
<tr><td>400</td>  <td>total quantity is exceeding from multi vehicle movement initiation quantity                                                       </td></tr>
<tr><td>401</td>  <td>Error in inserting multi vehicle details                                                                                          </td></tr>
<tr><td>402</td>  <td>total quantity can not be less than or equal to zero                                                                              </td></tr>
<tr><td>403</td>  <td>Error in multi vehicle details                                                                                                    </td></tr>
<tr><td>405</td>  <td>No record found for multi vehicle update with specified ewbNo groupNo and old vehicleNo/transDocNo with status as ACT             </td></tr>
<tr><td>406</td>  <td>Group number cannot be empty or zero                                                                                              </td></tr>
<tr><td>407</td>  <td>Invalid old vehicle number format                                                                                                 </td></tr>
<tr><td>408</td>  <td>Invalid new vehicle number format                                                                                                 </td></tr>
<tr><td>409</td>  <td>Invalid old transDoc number                                                                                                       </td></tr>
<tr><td>410</td>  <td>Invalid new transDoc number                                                                                                       </td></tr>
<tr><td>411</td>  <td>Multi Vehicle Initiation data is not there for specified ewayBill and group No                                                    </td></tr>
<tr><td>412</td>  <td>Multi Vehicle movement is already Initiated,hence PART B updation not allowed                                                     </td></tr>
<tr><td>413</td>  <td>Unit Code is not matching with unit code of first initiaton                                                                       </td></tr>
<tr><td>415</td>  <td>Error in fetching in verification data for officer                                                                                </td></tr>
<tr><td>416</td>  <td>Date range is exceeding allowed date range                                                                                        </td></tr>
<tr><td>417</td>  <td>No verification data found for officer                                                                                            </td></tr>
<tr><td>418</td>  <td>No record found                                                                                                                   </td></tr>
<tr><td>419</td>  <td>Error in fetching search result for taxpayer/transporter                                                                          </td></tr>
<tr><td>420</td>  <td>Minimum six character required for Tradename/legalname search                                                                     </td></tr>
<tr><td>421</td>  <td>Invalid pincode                                                                                                                   </td></tr>
<tr><td>422</td>  <td>Invalid mobile number                                                                                                             </td></tr>
<tr><td>423</td>  <td>Error in fetching ewaybill list by vehicle number                                                                                 </td></tr>
<tr><td>424</td>  <td>Invalid PAN number                                                                                                                </td></tr>
<tr><td>425</td>  <td>Error in fetching Part A data by IR Number                                                                                        </td></tr>
<tr><td>426</td>  <td>For Vehicle Released vehicle release date and time is mandatory                                                                   </td></tr>
<tr><td>427</td>  <td>Error in saving Part-A verification Report                                                                                        </td></tr>
<tr><td>428</td>  <td>For Goods Detained,Vehicle Released feild is mandatory                                                                            </td></tr>
<tr><td>429</td>  <td>Error in saving Part-B verification Report                                                                                        </td></tr>
<tr><td>430</td>  <td>Goods Detained Field required                                                                                                     </td></tr>
<tr><td>431</td>  <td>Part-A for this ewaybill is already generated by you                                                                              </td></tr>
<tr><td>432</td>  <td>invalid vehicle released value                                                                                                    </td></tr>
<tr><td>433</td>  <td>invalid goods detained parameter value                                                                                            </td></tr>
<tr><td>434</td>  <td>invalid ewbNoAvailable parameter value                                                                                            </td></tr>
<tr><td>435</td>  <td>Part B is already updated,hence updation is not allowed                                                                           </td></tr>
<tr><td>436</td>  <td>Invalid Consignee ship to State Code for the given pincode                                                                        </td></tr>
<tr><td>437</td>  <td>Invalid Supplier ship from State Code for the given pincode                                                                       </td></tr>
<tr><td>438</td>  <td>Invalid Latitude                                                                          										</td></tr>
<tr><td>439</td>  <td>Invalid Longitude                                                                           										</td></tr>
<tr><td>440</td>  <td>Error in inserting in verification data                                                                           				</td></tr>
<tr><td>441</td>  <td>Invalid verification type                                                                          								</td></tr>
<tr><td>442</td>  <td>Error in inserting verification details                                                                                           </td></tr>
<tr><td>443</td>  <td>invalid invoice available value                                                                                                   </td></tr>
<tr><td>600</td>  <td>Invalid category                                                                                                                  </td></tr>
<tr><td>601</td>  <td>Invalid date format                                                                                                               </td></tr>
<tr><td>602</td>  <td>Invalid File Number                                                                                                               </td></tr>
<tr><td>603</td>  <td>For file details file number is required                                                                                          </td></tr>
<tr><td>604</td>  <td>E-way bill(s) are already generated for the same document number, you cannot generate again on same document number               </td></tr>
<tr><td>605</td>  <td>If the goods are moving towards transporter location, the value of toTransporterLoc should be Y                                   </td></tr>
<tr><td>606</td>  <td>Vehicle type is mandatory, if the goods are moving to transporter place                                                           </td></tr>
<tr><td>607</td>  <td>dispatch from gstin is mandatary                                                                                                  </td></tr>
<tr><td>608</td>  <td>ship to from gstin is mandatary                                                                                                   </td></tr>
<tr><td>609</td>  <td>invalid ship to from gstin                                                                                                        </td></tr>
<tr><td>610</td>  <td>invalid dispatch from gstin                                                                                                       </td></tr>
<tr><td>611</td>  <td>invalid document type for the given supply type                                                                                   </td></tr>
<tr><td>612</td>  <td>Invalid transaction type                                                                                                          </td></tr>
<tr><td>613</td>  <td>Exception in getting Officer Role                                                                                                 </td></tr>
<tr><td>614</td>  <td>Transaction type is mandatory                                                                                                     </td></tr>
<tr><td>615</td>  <td>Dispatch From GSTIN cannot be sent as the transaction type selected is Regular                                                    </td></tr>
<tr><td>616</td>  <td>Ship to GSTIN cannot be sent as the transaction type selected is Regular                                                          </td></tr>
<tr><td>617</td>  <td>Bill-from and dispatch-from gstin should not be same for this transaction type                                                    </td></tr>
<tr><td>618</td>  <td>Bill-to and ship-to gstin should not be same for this transaction type                                                            </td></tr>
<tr><td>619</td>  <td>Transporter Id is mandatory for generation of Part A slip                                                                         </td></tr>
<tr><td>620</td>  <td>Total invoice value cannot be less than the sum of total assessible value and tax values                                          </td></tr>
<tr><td>621</td>  <td>trans mode is mandatory since vehicle number is present                                                                           </td></tr>
<tr><td>622</td>  <td>trans mode is mandatory since trans doc number is present                                                                         </td></tr>
<tr><td>623</td>  <td>IGST value is not applicable for Intra State Transaction                                                                          </td></tr>
<tr><td>624</td>  <td>SGST/CGST value is not applicable for Inter State Transaction                                                                     </td></tr>
<tr><td>627</td>  <td>Total value should not be negative                                                                                                </td></tr>
<tr><td>628</td>  <td>Total invoice value should not be negative                                                                                        </td></tr>
<tr><td>629</td>  <td>IGST value should not be negative                                                                                                 </td></tr>
<tr><td>630</td>  <td>CGST value should not be negative                                                                                                 </td></tr>
<tr><td>631</td>  <td>SGST value should not be negative                                                                                                 </td></tr>
<tr><td>632</td>  <td>Cess value should not be negative                                                                                                 </td></tr>
<tr><td>633</td>  <td>Cess non advol should not be negative                                                                                             </td></tr>
<tr><td>634</td>  <td>Vehicle type should not be ODC when transmode is other than road                                                                  </td></tr>
<tr><td>635</td>  <td>You cannot update part B, as the current tranporter is already entered Part B details of the eway bill                            </td></tr>
<tr><td>636</td>  <td>You are not assigned to update part B                                                                                             </td></tr>
<tr><td>637</td>  <td>You cannot extend ewaybill, as the current tranporter is already entered Part B details of the ewaybill                           </td></tr>
<tr><td>638</td>  <td>Transport mode is mandatory as Vehicle Number/Transport Document Number is given                                                  </td></tr>
<tr><td>640</td>  <td>Tolal Invoice value is mandatory                                                                                                  </td></tr>
<tr><td>641</td>  <td>For outward CKD/SKD/Lots supply type, Bill To state should be as Other Country, since the Bill To GSTIN given is of SEZ unit      </td></tr>
<tr><td>642</td>  <td>For inward CKD/SKD/Lots supply type, Bill From state should be as Other Country, since the Bill From GSTIN given is of SEZ unit   </td></tr>
<tr><td>643</td>  <td>For regular transaction, Bill from state code and Dispatch from state code should be same                                         </td></tr>
<tr><td>644</td>  <td>For regular transaction, Bill to state code and Ship to state code should be same                                                 </td></tr>
<tr><td>645</td>  <td>You cannot do Multi Vehicle movement, as current transporeter already entered part B                                              </td></tr>
<tr><td>646</td>  <td>You are not assigned to do multi vehicle movement                                                                                 </td></tr>
<tr><td>647</td>  <td>Could not insert RFID data, please contact to helpdesk                                                                            </td></tr>
<tr><td>648</td>  <td>Multi Vehicle movement is already Initiated,hence generation of consolidated eway bill is not allowed                             </td></tr>
<tr><td>649</td>  <td>You cannot generate consolidated eway bill , as the current tranporter is already entered Part B details of the eway bill         </td></tr>
<tr><td>650</td>  <td>You are not assigned to generate consolidated ewaybill                                                                            </td></tr>
<tr><td>651</td>  <td>For Category PartA or PartB ewbDt is mandatory                                                                                    </td></tr>
<tr><td>652</td>  <td>For Category EWB03 procDt is mandatory                                                                                            </td></tr>
<tr><td>653</td>  <td>The Ewaybill is cancelled                                                                                                         </td></tr>
<tr><td>654</td>  <td>This GSTIN has generated a common Enrolment Number. Hence you are not allowed to generate Eway bill                               </td></tr>
<tr><td>655</td>  <td>This GSTIN has generated a common Enrolment Number. Hence you cannot mention it as a transporter                                  </td></tr>
<tr><td>656</td>  <td>This Eway Bill does not belongs to your state                                                                                     </td></tr>
<tr><td>657</td>  <td>Eway Bill Category wise details will be available after 4 days only                                                               </td></tr>
<tr><td>658</td>  <td>You are blocked for accesing this API as the allowed number of requests has been exceeded                                         </td></tr>
<tr><td>659</td>  <td>Remarks is mandatory                                                                                                              </td></tr>
<tr><td>670</td>  <td>Invalid Month Parameter                                                                                                           </td></tr>
<tr><td>671</td>  <td>Invalid Year Parameter                                                                                                            </td></tr>
<tr><td>672</td>  <td>User Id is mandatory                                                                                                              </td></tr>
<tr><td>673</td>  <td>Error in getting officer dashboard                                                                                                </td></tr>
<tr><td>675</td>  <td>Error in getting EWB03 details by acknowledgement date range                                                                      </td></tr>
<tr><td>676</td>  <td>Error in getting EWB Not Available List by entered date range                                                                     </td></tr>
<tr><td>677</td>  <td>Error in getting EWB Not Available List by closed date range                                                                      </td></tr>
<tr><td>678</td>  <td>Invalid Uniq No                                                                                                                   </td></tr>
<tr><td>679</td>  <td>Invalid EWB03 Ack No                                                                                                              </td></tr>
<tr><td>680</td>  <td>Invalid Close Reason                                                                                                              </td></tr>
<tr><td>681</td>  <td>Error in Closing EWB Verification Data                                                                                            </td></tr>
<tr><td>682</td>  <td>No Record available to Close                                                                                                      </td></tr>
<tr><td>683</td>  <td>Error in fetching WatchList Data                                                                                                  </td></tr>
<tr><td>685</td>  <td>Exception in fetching dashboard data                                                                                              </td></tr>
<tr><td>700</td>  <td>You are not assigned to extend e-waybill                                                                                          </td></tr>
<tr><td>701</td>  <td>Invalid Vehicle Direction                                                                                          				</td></tr>
<tr><td>702</td>  <td>The distance between the pincodes given is too high                                                                               </td></tr>
<tr><td>703</td>  <td>Since the consignor is Composite Taxpayer, inter state transactions are not allowed                                               </td></tr>
<tr><td>704</td>  <td>Since the consignor is Composite Taxpayer, Tax rates should be zero                                                               </td></tr>
<tr><td>705</td>  <td>Invalid transit type                                                                                          					</td></tr>
<tr><td>706</td>  <td>Address Line1 is mandatory                                                                                          				</td></tr>
<tr><td>707</td>  <td>Address Line2 is mandatory                                                                                          				</td></tr>
<tr><td>708</td>  <td>Address Line3 is mandatory                                                                                          				</td></tr>
<tr><td>709</td>  <td>Pin to pin distance is not available for the given pin codes                                                                      </td></tr>
<tr><td>710</td>  <td>Invalid state code for the given pincode                                                                                          </td></tr>
<tr><td>711</td>  <td>Invalid value for isInTransit field                                                                                          		</td></tr>
<tr><td>712</td>  <td>Transit Type is not required as the good are not in movement                                                                      </td></tr>
<tr><td>713</td>  <td>Transit Address is not required as the good are not in movement                                                                   </td></tr>
<tr><td>714</td>  <td>Document type - Tax Invoice is not allowed for composite tax payer                                                                </td></tr>
<tr><td>715</td>  <td>The Consignor GSTIN is blocked from e-waybill generation as Return is not filed for past 2 months                                 </td></tr>
<tr><td>716</td>  <td>The Consignee GSTIN is blocked from e-waybill generation as Return is not filed for past 2 months                                 </td></tr>
<tr><td>717</td>  <td>The Transporter GSTIN is blocked from e-waybill generation as Return is not filed for past 2 months                               </td></tr>
<tr><td>718</td>  <td>The User GSTIN is blocked from Transporter Updation as Return is not filed for past 2 months                                      </td></tr>
<tr><td>719</td>  <td>The Transporter GSTIN is blocked from Transporter Updation as Return is not filed for past 2 months                               </td></tr>
<tr><td>720</td>  <td>E Way Bill should be generated as part of IRN generation or with reference to IRN in E Invoice System, Since Supplier is enabled for E Invoice</td></tr>
<tr><td>721</td>  <td>The distance between the given pincodes are not available in the system. Please provide distance                                  </td></tr>
<tr><td>722</td>  <td>Consignee GSTIN is cancelled and document date is later than the De-Registration date                                             </td></tr>
<tr><td>724</td>  <td>HSN code of at least one item should be of goods to generate e-Way Bill                                                           </td></tr>
<tr><td>726</td>  <td>Vehicle type can not be regular when transportation mode is ship		                                                            </td></tr>                                                                                  
	</tbody>
	    
</table>
        
</div>  
        
</body> 
        
</html> 
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        