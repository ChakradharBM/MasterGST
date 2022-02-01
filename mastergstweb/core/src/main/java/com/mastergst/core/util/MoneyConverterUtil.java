package com.mastergst.core.util;

public class MoneyConverterUtil {
	
	public static final String[] units = { "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
			"Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen",
			"Nineteen" };

	public static final String[] tens = { "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty",
			"Ninety" };
	
	
	private static final String[] inunits = {
		    "",
		    " One",
		    " Two",
		    " Three",
		    " Four",
		    " Five",
		    " Six",
		    " Seven",
		    " Eight",
		    " Nine"
		  }; 
		  private static final String[] twoDigits = {
		    " Ten",
		    " Eleven",
		    " Twelve",
		    " Thirteen",
		    " Fourteen",
		    " Fifteen",
		    " Sixteen",
		    " Seventeen",
		    " Eighteen",
		    " Nineteen"
		  };
		  private static final String[] tenMultiples = {
		    "",
		    "",
		    " Twenty",
		    " Thirty",
		    " Forty",
		    " Fifty",
		    " Sixty",
		    " Seventy",
		    " Eighty",
		    " Ninety"
		  };
		  private static final String[] placeValues = {
		    " ",
		    " Thousand",
		    " Million",
		    " Billion",
		    " Trillion"
		  };
		        
		  public static String convertNumber(final double number,final String mainunit, final String fractionunit) {    
			  long rupees = (long) number;
				long paisa = Math.round((number - rupees) * 100);
				if (number == 0D) {
					return "";
				}
			  String word = "";    
		    int index = 0;
		    do {
		      // take 3 digits in each iteration
		      int num = (int)(rupees % 1000);
		      if (num != 0){
		        String str = ConversionForUptoThreeDigits(num);
		        word = str + placeValues[index] + word;
		      }
		      index++;
		      // next 3 digits
		      rupees = rupees/1000;
		    } while (rupees > 0);
		    String paisaPart = "";
			if (paisa > 0) {
				if (word.length() > 0) {
					paisaPart = " "+mainunit+" and ";
				}
				paisaPart += convert(paisa) +" "+ fractionunit + (paisa == 1 ? "" : " Only");
			}
			
		    return "*** "+word + (paisaPart == "" ? " Only" : paisaPart);
		  }
		    
		  public static String ConversionForUptoThreeDigits(int number) {
		    String word = "";    
		    int num = number % 100;
		    if(num < 10){
		      word = word + inunits[num];
		    }
		    else if(num < 20){
		      word = word + twoDigits[num%10];
		    }else{
		      word = tenMultiples[num/10] + inunits[num%10];
		    }
		    
		    word = (number/100 > 0)? inunits[number/100] + " hundred" + word : word;
		    return word;
		  }
	

	public static String getMoneyIntoWords(final double money) {
		long rupees = (long) money;
		long paisa = Math.round((money - rupees) * 100);
		if (money == 0D) {
			return "";
		}
		String ruppesPart = "";
		if (rupees > 0) {
			ruppesPart = convert(rupees) + " Rupees" + (rupees == 1 ? "" : "");
		}
		String paisaPart = "";
		if (paisa > 0) {
			if (ruppesPart.length() > 0) {
				paisaPart = " and ";
			}
			paisaPart += convert(paisa) + " Paisa" + (paisa == 1 ? "" : " Only");
		}
		return "*** "+ruppesPart + (paisaPart == "" ? " Only" : paisaPart);
	}

	public static String convert(final long n) {
		if (n < 0) {
			return "Minus " + convert(-n);
		}
		if (n < 20) {
			return units[(int) n];
		}
		if (n < 100) {
			return tens[(int) (n / 10)] + ((n % 10 != 0) ? " " : "") + units[(int) (n % 10)];
		}
		if (n < 1000) {
			return units[(int) (n / 100)] + " Hundred" + ((n % 100 != 0) ? " " : "") + convert(n % 100);
		}
		if (n < 100000) {
			return convert(n / 1000) + " Thousand" + ((n % 10000 != 0) ? " " : "") + convert(n % 1000);
		}
		if (n < 10000000) {
			return convert(n / 100000) + " Lakh" + ((n % 100000 != 0) ? " " : "") + convert(n % 100000);
		}
		return convert(n / 10000000) + " Crore" + ((n % 10000000 != 0) ? " " : "") + convert(n % 10000000);
	}
}
