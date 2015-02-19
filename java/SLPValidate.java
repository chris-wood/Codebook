import java.util.*;
import java.io.*;

public class SLPValidate 
{
	static void error(String s) { System.err.println(s); }
	static void disp(String s) { System.out.println(s); }
	static void displ(String s) { System.out.print(s); }
	static void disp(int n) { System.out.println(n); }
	static void disp(int[] v) {
		System.out.print("[");
		for (int i = 0; i < v.length - 1; i++) System.out.print(v[i] + " ");
		System.out.println(v[v.length - 1] + "]");
	}
	static void disp(ArrayList<int[]> vs) {
		for (int i = 0; i < vs.size(); i++) disp(vs.get(i));
	}

	public static int[] XOR(int[] x, int[] y) throws Exception
	{
		if (x.length != y.length) throw new Exception("Invalid x/y dimensions.");
		int[] and = new int[x.length];
		for (int i = 0; i < x.length; i++) 
		{
			and[i] = x[i] ^ y[i];
		}
		return and;
	}

	public static int[] AND(int[] x, int[] y) throws Exception
	{
		if (x.length != y.length) throw new Exception("Invalid x/y dimensions.");
		int[] and = new int[x.length];
		for (int i = 0; i < x.length; i++) 
		{
			and[i] = x[i] & y[i];
		}
		return and;
	}

	public static int[] NEGATE(int[] x) 
	{
		int[] nx = new int[x.length];
		for (int i = 0; i < x.length; i++) 
		{
			nx[i] = x[i] == 0 ? 1 : 0; // enforce only two possibilities
		}
		return nx;
	}

	public static int bitsToInt(int[] bits)
	{
		String s = "";
		for (int i = 0; i < bits.length; i++) s += bits[i];
		return Integer.parseInt(s, 2);
	}

	public static ArrayList<Integer> validateCircuit(String fname) throws Exception {
		ArrayList<Integer> failures = new ArrayList<Integer>();
		Scanner s = new Scanner(new BufferedReader(new FileReader(fname)));

		// Initialize known signals
		int[] a4 = {0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1};
		int[] a3 = {0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1};
		int[] a2 = {0,0,0,0,1,1,1,1,0,0,0,0,1,1,1,1};
		int[] a1 = {0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1};

		while(s.hasNextLine())
		{
			String line = s.nextLine();
			if (line.startsWith("f"))
			{
				// Extract target spectrum
				String[] header = line.split(" ");
				int target = Integer.parseInt(header[0].substring(1, header[0].length()));
				disp("\n*************\nParsing: " + target + "\n");

				// Map for temporary variables
				HashMap<String, int[]> varMap = new HashMap<String, int[]>();
				ArrayList<Integer> specList = new ArrayList<Integer>();
				int[] targetSpectrum = null;

				// Parse away...
				line = s.nextLine();
//				disp(line);
//				disp(line.indexOf("*"));
				while (line.length() > 0 && line.indexOf("*") < 0)
				{
					// String[] split1 = line.split(" : ");
					String split1 = line;
					// specList.add(Integer.parseInt(split1[0]));

					// parse the RHS
					// String[] split2 = split1[1].split(" = ");
					String[] split2 = split1.split(" = ");
					String var = split2[0];
//					disp(var);

					// determine if AND or XOR
					if (split2[1].indexOf(" x ") == -1) // XOR
					{
						String[] vsplit = split2[1].split("\\+");
						String v1 = vsplit[0].trim();
						String v2 = vsplit[1].trim();

						int[] vs1;
						int[] vs2;

						// disp(v1 + "," + v2);

						if (varMap.containsKey(v1))
						{
							vs1 = varMap.get(v1);
						}
						else if (v1.equals("ONE"))
						{
							vs1 = null;
						}
						else // must be an input variables (i.e. a1, a2, a3, or a4)
						{
							int id = Integer.parseInt(v1.substring(1, v1.length()).trim());
							switch (id)
							{
								case 1:
									vs1 = a1;
									break;
								case 2:
									vs1 = a2;
									break;
								case 3:
									vs1 = a3;
									break;
								case 4:
									vs1 = a4;
									break;
								default:
									System.err.println("Invalid variable ID");
									throw new Exception("Invalid variable ID: " + target);
							}
						}

						if (varMap.containsKey(v2))
						{
							vs2 = varMap.get(v2);
						}
						else if (v2.equals("ONE"))
						{
							vs2 = null;
						}
						else // must be an input variables (i.e. a1, a2, a3, or a4)
						{
							int id = Integer.parseInt(v2.substring(1, v2.length()).trim());
							switch (id)
							{
								case 1:
									vs2 = a1;
									break;
								case 2:
									vs2 = a2;
									break;
								case 3:
									vs2 = a3;
									break;
								case 4:
									vs2 = a4;
									break;
								default:
									System.err.println("Invalid variable ID");
									throw new Exception("Invalid variable ID: " + target);
							}
						}

						if (vs1 == null && vs2 == null)
						{
							System.err.println("Both can't be null.");
							throw new Exception("Both can't be null: " + target);
						}

						int[] newSpectrum;

						if (vs1 == null)	
						{
							newSpectrum = NEGATE(vs2);
						}
						else if (vs2 == null)
						{
							newSpectrum = NEGATE(vs1);
						}
						else
						{
							newSpectrum = XOR(vs1, vs2);
						}

						varMap.put(var, newSpectrum);
						targetSpectrum = newSpectrum;
					}
					else // AND
					{
//						disp(split2[1]);
						String[] vsplit = split2[1].split(" x ");
						String v1 = vsplit[0].trim();
						String v2 = vsplit[1].trim();
//						disp(v1);
//						disp(v2);

						int[] vs1;
						int[] vs2;

						// disp(v1 + "," + v2);

						if (varMap.containsKey(v1))
						{
							vs1 = varMap.get(v1);
						}
						else if (v1.equals("ONE"))
						{
							vs1 = null;
						}
						else // must be an input variables (i.e. a1, a2, a3, or a4)
						{
							int id = Integer.parseInt(v1.substring(1, v1.length()).trim());
							switch (id)
							{
								case 1:
									vs1 = a1;
									break;
								case 2:
									vs1 = a2;
									break;
								case 3:
									vs1 = a3;
									break;
								case 4:
									vs1 = a4;
									break;
								default:
									System.err.println("Invalid variable ID");
									throw new Exception("Invalid variable ID: " + target);
							}
						}

						if (varMap.containsKey(v2))
						{
							vs2 = varMap.get(v2);
						}
						else if (v2.equals("ONE"))
						{
							vs2 = null;
						}
						else // must be an input variables (i.e. a1, a2, a3, or a4)
						{
							int id = Integer.parseInt(v2.substring(1, v2.length()).trim());
							switch (id)
							{
								case 1:
									vs2 = a1;
									break;
								case 2:
									vs2 = a2;
									break;
								case 3:
									vs2 = a3;
									break;
								case 4:
									vs2 = a4;
									break;
								default:
									System.err.println("Invalid variable ID");
									throw new Exception("Invalid variable ID: " + target);
							}
						}

						if (vs1 == null && vs2 == null)
						{
							System.err.println("Both can't be null.");
							throw new Exception("Both can't be null: " + target);
						}

						int[] newSpectrum;

						if (vs1 == null)	
						{
							newSpectrum = NEGATE(vs2);
						}
						else if (vs2 == null)
						{
							newSpectrum = NEGATE(vs1);
						}
						else
						{
							newSpectrum = AND(vs1, vs2);
						}

						varMap.put(var, newSpectrum);
						targetSpectrum = newSpectrum;
					}

					// displ(line + ": ");
					disp(targetSpectrum);

					// Advance...
					if (s.hasNextLine())
					{
						line = s.nextLine();
						if (target == 31135) error(line);
					}
					else
					{
						if (target == 31135) error("breaking?");
						break;
					}
				}
				
				if (targetSpectrum != null)
				{
					int computedTarget = bitsToInt(targetSpectrum);
					if (computedTarget != target)
					{
						error("Output spectrum did not match target spectrum: " + target);
						failures.add(target);
						error("\nComputed and target:");
						error("   " + computedTarget);
						error("   " + target);
					}
					disp("\nComputed and target:");
					disp("   " + computedTarget);
					disp("   " + target);
				}
			}
		}

		return failures;
	}

	public static void main(String[] args) throws Exception {
		ArrayList<Integer> failures = validateCircuit(args[0]);
		if (failures.size() == 0) 
		{
			System.out.println("\n\n********************\n\nAll circuits passed verification");
		}
		else
		{
			System.out.println(failures.size() + " circuits failed verification:");
			for (Integer target : failures)
			{
				System.out.println("   -> " + target);
			}
		}
	}
}