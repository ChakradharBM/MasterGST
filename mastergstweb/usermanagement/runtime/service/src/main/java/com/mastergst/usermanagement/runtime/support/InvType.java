package com.mastergst.usermanagement.runtime.support;

import java.util.HashMap;
import java.util.Map;

public class InvType {
	
	private static Map<String, Sales> salesMap = new HashMap<>();
	private static Map<String, Purchages> purchagesMap = new HashMap<>();
	private static Map<String, ANX1> anx1Map = new HashMap<>();
	static{
		salesMap.put("B2B", Sales.B2B);
		salesMap.put("B2C", Sales.B2C);
		salesMap.put("B2CL", Sales.B2CL);
		salesMap.put("Credit/Debit Notes", Sales.CREDIT_DEBIT_NOTES);
		salesMap.put("Credit/Debit Note for Unregistered Taxpayers", Sales.CREDIT_DEBIT_NOTE_FOR_UNREGISTERED_TAXPAYERS);
		salesMap.put("Exports", Sales.EXPORTS);
		salesMap.put("Nil Supplies", Sales.NIL_SUPPLIES);
		salesMap.put("Advances", Sales.ADVANCES);
		salesMap.put("Advance Adjusted Detail", Sales.ADVANCE_ADJUSTED_DETAIL);
		salesMap.put("B2BA", Sales.B2BA);
		salesMap.put("B2CSA", Sales.B2CSA);
		salesMap.put("B2CLA", Sales.B2CLA);
		salesMap.put("CDNURA", Sales.CDNURA);
		salesMap.put("CDNA", Sales.CDNA);
		salesMap.put("EXPA", Sales.EXPA);
		salesMap.put("TXPA", Sales.TXPA);
		salesMap.put("ATA", Sales.ATA);
		
		purchagesMap.put("B2B", Purchages.B2B);
		purchagesMap.put("B2C", Purchages.B2C);
		purchagesMap.put("B2CL", Purchages.B2CL);
		
		anx1Map.put("B2B", ANX1.B2B);
		anx1Map.put("B2C", ANX1.B2C);
		anx1Map.put("B2CL", ANX1.B2CL);
	}
	
	public enum Sales{
		B2B("B2B","4A, 4B, 4C, 6B, 6C - B2B Invoices",1),
		B2C("B2C","7 - B2C (Others)",6),
		B2CL("B2CL","5A, 5B - B2C (Large) Invoices",2),
		CREDIT_DEBIT_NOTES("Credit/Debit Notes","9B - Credit / Debit Notes (Registered)",3),
		CREDIT_DEBIT_NOTE_FOR_UNREGISTERED_TAXPAYERS("Credit/Debit Note for Unregistered Taxpayers","9B - Credit / Debit Notes (Unregistered)",4),
		EXPORTS("Exports","6A - Exports Invoices",5),
		NIL_SUPPLIES("Nil Supplies","8 - Nil rated, exempted and non GST outward supplies",7),
		ADVANCES("Advances","11A(1), 11A(2) - Tax Liability (Advances Received)",8),
		ADVANCE_ADJUSTED_DETAIL("Advance Adjusted Detail","11B(1), 11B(2) - Adjustment of Advances",9),
		B2BA("B2BA","9A - Amended B2B Invoices",10),
		B2CSA("B2CSA","10 - Amended B2C(Others)",15),
		B2CLA("B2CLA","9A - Amended B2C (Large) Invoices",11),
		CDNURA("CDNURA","9C - Amended Credit/Debit Notes (Unregistered)",13),
		CDNA("CDNA","9C - Amended Credit/Debit Notes (Registered)",12),
		EXPA("EXPA","9A - Amended Exports Invoices",14),
		TXPA("TXPA","11A - Amended Tax Liability (Advance Received)",16),
		ATA("ATA","11B - Amendment of Adjustment of Advances",17);
		
		private String name;
		private String value;
		private int order;
		
		private Sales(String name, String value, int order){
			this.name = name;
			this.value = value;
			this.order = order;
		}
	
		public String getName() {
			return name;
		}
	
		public String getValue() {
			return value;
		}
	
		public int getOrder() {
			return order;
		}
	}
	
	public enum Purchages{
		B2B("B2B","B2B (Business to Business)",1),
		B2C("B2C","B2C (Business to Consumers Small)",1),
		B2CL("B2CL","B2CL (Business to Consumers Large)",1);
		
		private String name;
		private String value;
		private int order;
		
		private Purchages(String name, String value, int order){
			this.name = name;
			this.value = value;
			this.order = order;
		}
	
		public String getName() {
			return name;
		}
	
		public String getValue() {
			return value;
		}
	
		public int getOrder() {
			return order;
		}
	} 
	
	public enum ANX1{
		B2B("B2B","B2B (Business to Business)",1),
		B2C("B2C","B2C (Business to Consumers Small)",1),
		B2CL("B2CL","B2CL (Business to Consumers Large)",1);
		
		private String name;
		private String value;
		private int order;
		
		private ANX1(String name, String value, int order){
			this.name = name;
			this.value = value;
			this.order = order;
		}
	
		public String getName() {
			return name;
		}
	
		public String getValue() {
			return value;
		}
	
		public int getOrder() {
			return order;
		}
	} 
	
	public static Sales getSalesInvType(String invType){
		return salesMap.get(invType);
	}
	public static Purchages getPurchagesInvType(String invType){
		return purchagesMap.get(invType);
	}
	
	public static ANX1 getAnx1InvType(String invType){
		return anx1Map.get(invType);
	}
	
}
