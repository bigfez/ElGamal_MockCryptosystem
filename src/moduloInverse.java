import java.util.*;

public class moduloInverse{
	
  	public static void main(String[] args){
    	Scanner sc = new Scanner(System.in);	
  		int [] arr = new int[3];
      	int a, p, inv;
		System.out.print("Multiplicative Inverse Calculator (a^-1 (mod p))\n\nPlease enter the value for Modulus 'p': ");
  		p = sc.nextInt();
  		System.out.print("Please enter the value for the base 'a': ");
  		a = sc.nextInt();
      	arr = extEuclid(a, p);
      	inv = arr[1];
      	System.out.println("\n\nThe inverse is: " + inv);
    }
      
	public static int[] extEuclid(int r, int m) {
		if (m == 0)
			return new int[] {r, 1, 0}; //Flag that signals to move back up the recursion chain
		int[] nums = extEuclid(m, r % m); //Recursive call
		int a = nums[0];
		int b = nums[2];
		int c = nums[1] - (r / m) * nums[2];
		return new int[] {a, b%m, c}; //Returns results of algorithm, second value being r^-1 mod m
	}
  
}