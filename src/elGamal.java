import java.util.*;

public class elGamal {

	//Client code, prompts user for public key and starts looping for CT pairs
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		char choice = 'Y';
		int p, al, b, a;		
		int [] cText = new int[2];

		System.out.println("Welcome to the ElGamal Cryptosystem simulation!\n");
		System.out.print("Please enter a value for p (up to 100,000 max): ");
		p = in.nextInt();
		while(p > 100000 || p < 1){
			System.out.print("Incorrect value, please stay in range (1 - 100,000): ");
			p = in.nextInt();
		}
		System.out.print("Please enter a value for alpha: ");
		al = in.nextInt();
		while(al > p || al < 1){
			System.out.print("Incorrect value, please stay in range (1 - " + p + "): ");
			al = in.nextInt();
		}
		System.out.print("Please enter a value for beta: ");
		b = in.nextInt();
		while(b > p || b < 1){
			System.out.print("Incorrect value, please stay in range (1 - " + p + "): ");
			b = in.nextInt();
		}
		System.out.println("Your public key is now: (" + p + ", " + al + ", " + b + ")");
		a = findA(p, al, b);
		if(a == 0){ //Only runs if 'a' does not exist for the given public key pair
			choice = 'N';
			System.out.println("Secret key doesn't exist with this public key. Run this program again with new key values.");
		}	
		System.out.println("a is " + a);
		//Loops decrypting a CT pair for the key inputted, checks for user choice as condition
		while(choice == 'Y' || choice == 'y'){
			System.out.print("\nPlease enter first value for CipherText pair (r, t): ");
			cText[0] = in.nextInt();
			while(cText[0] > p || cText[0] < 1){
				System.out.print("Incorrect value, please stay in range (1 - " + p + "): ");
				cText[0] = in.nextInt();
			}
			System.out.print("Please enter second value for CipherText pair (r, t): ");
			cText[1] = in.nextInt();
			while(cText[1] > p || cText[1] < 1){
				System.out.print("Incorrect value, please stay in range (1 - " + p + "): ");
				cText[1] = in.nextInt();
			}
			System.out.println("The CT pair is now ("+ cText[0] + ", " + cText[1] + ")\n");
			String message = decryptCT(p, a, cText);
			System.out.println("THE DECRYPTED MESSAGE == " + message); //Shows final PT from that CT pair
			System.out.print("\nDecrypt another CT pair with this key? (Y/N): ");
			choice = in.next().charAt(0); //Prompts user for whether they want to try a new CT or not (exit if not)
		}
		in.close();
	}
	
	//Finds secret key 'a'
	public static int findA(int p, int al, int b) {
		int temp = 1, count = 0;
		for(int a = 1; a < p; a++){
			temp = temp * al; //Multiples by itself until this value == beta
			temp = temp % p;
			count++;
			if(temp == b){
				return count; //Returns count which is the correct exponent 'a'
			}	
		}
		return 0; //When 'a' cannot be found
	}
	
	//Decryption function that calls many other functions to find the PT
	public static String decryptCT(int p, int a, int[] CT){
		int[] inverse = extEuclid(CT[0], p); //Finds r^-1
		int rInv = inverse[1];
		if(rInv < 0)
			rInv = rInv + p; //In case r^-1 is a negative value
		int rInvA = modArithmetic(p, rInv, a); //Calculates r^-a
		int rawPT = computeM(CT[1], rInvA, p); //Calculates raw PT data to be converted
		int [] letterVals = convertCT(rawPT); //Converts the data to 3 letter values
		System.out.println("Letter values extracted from CT pair ("+ CT[0] + ", " + CT[1] + ") are: " + letterVals[0] + ", " + letterVals[1] + ", " + letterVals[2]);
		char m1 = (char) (letterVals[0]+64);
		char m2 = (char) (letterVals[1]+64);
		char m3 = (char) (letterVals[2]+64); //Turns these 3 values to its respective letters by typecasting
		return new String(new char[] {m1, m2, m3}); //Returns these letters as a string
	}
	
	//An extended Euclidean algorithm function 
	public static int[] extEuclid(int r, int m) {
		if (m == 0)
			return new int[] {r, 1, 0}; //Flag that signals to move back up the recursion chain
		int[] nums = extEuclid(m, r % m); //Recursive call
		int a = nums[0];
		int b = nums[2];
		int c = nums[1] - (r / m) * nums[2];
		return new int[] {a, b%m, c}; //Returns results of algorithm, second value being r^-1 mod m
	}
	
	//Modular arithmetic algorithm to determine final value of r^-a
	public static int modArithmetic(int p, int base, int pwr){
		int temp = 1;
		for(int a = 0; a < pwr; a++){ //Loops until the exponent is reached
			temp = temp * base;
			temp = temp % p;	
		}
		return temp; //Returns r^-a
	}
	
	//Computes the raw PT data from m = t * r^-a
	public static int computeM(int t, int rInvA, int mod){
		int m = t * rInvA;
		m = m % mod;
		return m; //Returns the message data
		
	}
	
	//Converts the raw PT to 3 separate letter values
	public static int [] convertCT(int m){
		int a = 0, b = 0, c = 0, temp, count = 0;
		boolean found = false;
		while(!found && count < 26){ //Finds first letter since 676 = 26^2
			temp = 676 * count; 
			if(temp > m){
				a = count - 1;
				m = m - (676*a); //Sets remainder m for second letter
				found = true;
			}
			else
				count++; //Increments until temp is greater than m
		}
		found = false;
		count = 0;
		while(!found && count < 26){ //Finds second letter since 26 = 26^1 
			temp = 26 * count; 
			if(temp > m){
				b = count - 1;
				m = m - (26*b); //Sets remainder m for third letter
				found = true;
			}
			else
				count++;
		}
		c = m; //Finds 3rd letter since remaining m can only be 0-25
		return new int[]{a+1, b+1, c+1}; //Adds 1 to each upon return since 1-26 is easier to comprehend than 0-25
	}
	
}
