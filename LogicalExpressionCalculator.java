public class main {
	public static void main(String args[]) {
		
		// TODO: set testStr to be the user inputed argument, complete the finalTruth method, evaluate various string inputs for accuracy
		
		String testStr = "TRUE OR (FALSE OR TRUE AND NOT FALSE) AND (FALSE OR NOT TRUE) AND TRUE OR FALSE.";

		// determines the details and validity of the string
		int numWords = countWords(testStr);
		int numParens = countParens(testStr);
		int numPeriods = countPeriods(testStr);
		if (!checkValid(numWords, numParens)) {
			System.out.println("Sorry, invalid syntax.");
		} else {

			// creates an array to hold each word, parens, and the period/s,
			String[] wordArr = createArr(testStr, numWords, numParens, numPeriods);

			// checks that only keywords are used in the string
			if (!checkWords(wordArr)) System.out.println("Sorry, invalid syntax.");
			else {
				int nullCount = findNulls(wordArr);

				// creates array with no nulls and determines how many sections have been created by parentheses
				String[] noNullArr = nullVoider(wordArr, nullCount);
				int numSections = countSections(noNullArr);

				// creates a boolean tracker for each section and creates
				// sections to be evaluated
				int[] sections = createSections(noNullArr, numSections);

				// creates a key for individual snippets
				int[] keys = evaluateSection(wordArr, sections);

				// converts the key to the final truth assignment
				boolean endTruth = finalTruth(keys);
				System.out.println("The string evaluates to: " + endTruth + ".");
			}
		}
	}

	// finalTruth() - converts the truth keys into a final truth determination
	public static boolean finalTruth(int[] keys) {
		// KEY:
		// 1 = NOT - can't actually happen
		// 2 = TRUE AND NOT
		// 3 = FALSE AND NOT *
		// 4 = TRUE OR NOT
		// 5 = FALSE OR NOT *
		// 6 = AND
		// 7 = TRUE AND
		// 8 = FALSE AND *
		// 9 = OR
		// 10 = TRUE OR
		// 11 = FALSE OR *
		// 12 = TRUE
		// 13 = FALSE
		boolean tempBool = false;
		for (int i = 0; i < keys.length; i++) {
			// initial determination of true or false
			if (i == 0 && (keys[i] == 2 || keys[i] == 4 || keys[i] == 7 || keys[i] == 10 || keys[i] == 12)) tempBool = true;
			else if (i == 0 && (keys[i] == 3 || keys[i] == 5 || keys[i] == 8 || keys[i] == 11 || keys[i] == 13)) tempBool = false;
			// evaluates the overall true or false after considering additional segments
			else if (tempBool == true && keys[i] == 2) {

				if (keys[i - 1] == 4 || keys[i - 1] == 7 || keys[i - 1] == 10 || keys[i - 1] == 11) tempBool = true;
			}

			// next is 11
			else if (tempBool == true && keys[i] == 11) {
				if (keys[i - 1] == 2 || keys[i - 1] == 4 || keys[i - 1] == 10) tempBool = true;
				else if (keys[i - 1] == 7) tempBool = false;
			} else if (tempBool == false && keys[i] == 11) {
				if (keys[i - 1] == 5) tempBool = true;
				else if (keys[i - 1] == 3 || keys[i - 1] == 8 || keys[i - 1] == 11) tempBool = false;
			}
			// next is 12
			else if (tempBool == true && keys[i] == 12) {
				if (keys[i - 1] == 4 || keys[i - 1] == 7 || keys[i - 1] == 10) tempBool = true;
				else if (keys[i - 1] == 2) tempBool = false;
			} else if (tempBool == false && keys[i] == 12) {
				if (keys[i - 1] == 5) tempBool = true;
				else if (keys[i - 1] == 3 || keys[i - 1] == 8 || keys[i - 1] == 11) tempBool = false;

				// next is 13
			} else if (tempBool == true && keys[i] == 13) {
				if (keys[i - 1] == 2 || keys[i - 1] == 4 || keys[i - 1] == 10) tempBool = true;
				else if (keys[i - 1] == 7) tempBool = false;
			} else if (tempBool == false && keys[i] == 13) {
				if (keys[i - 1] == 5) tempBool = true;
				else if (keys[i - 1] == 3 || keys[i - 1] == 8 || keys[i - 1] == 11) tempBool = false;
			}
		}
		return tempBool;
	}

	// countWords() - counts the number of words in a string and also ensures it is not null or simply whitespace
	public static int countWords(String str) {
		if (str == null || str.isEmpty() || !str.matches(".*\\w.*")) return 0;
		int numWords = 1;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == ' ') numWords++;
		}
		return numWords;
	}

	// countParens() - counts the number of parens in a string and also ensures there are equivalent open and close parens
	public static int countParens(String str) {
		int totParens = 0;
		int openParens = 0;
		int closeParens = 0;

		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '(') {
				totParens++;
				openParens++;
			}
			if (str.charAt(i) == ')') {
				totParens++;
				closeParens++;
			}
		}
		if (openParens == closeParens) return totParens;
		else return -1;
	}

	// countPeriods() - counts the number of periods in a string
	public static int countPeriods(String str) {
		int numPer = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '.') numPer++;
		}
		return numPer;
	}

	// checkValid() - determines if the string is valid
	public static boolean checkValid(int numWords, int numParens) {
		if (numWords == 0 || numParens == -1) {
			System.out.println("Invalid syntax");
			return false;
		} else return true;
	}

	// createArr() - creates an array from the string that was inputed by user
	public static String[] createArr(String str, int numWords, int numParens, int numPeriods) {

		// creates an array to hold each word, parens, and the period/s
		String[] wordArr = new String[numWords + numParens + numPeriods];
		int wordArrIndex = 0;
		int strIndex = 0;

		// adds each important keyword to an array
		for (int i = 0; i < str.length(); i++) {
			// checks for open parens and adds to the array
			if (str.charAt(i) == '(' && i == strIndex) {
				wordArr[wordArrIndex] = "(";
				wordArrIndex++;
				strIndex = i + 1;
			}
			// checks for spaces to determine the end of a keyword
			else if (str.charAt(i) == ' ' || str.charAt(i) == '(' || str.charAt(i) == ')' || str.charAt(i) == '.') {
				String tempStr = "";
				for (int j = strIndex; j < i; j++) {
					tempStr += str.charAt(j);
				}
				wordArr[wordArrIndex] = tempStr;
				wordArrIndex++;
				if (str.charAt(i) == ')' || str.charAt(i) == '.') strIndex = i;
				else strIndex = i + 1;
			}
			// checks for close parens and adds to the array
			else if (str.charAt(i) == ')' && i == strIndex - 1) {
				System.out.println("the word index is " + wordArrIndex + " and the str Index is " + strIndex
						+ " and i is " + i);
				wordArr[wordArrIndex] = ")";
				wordArrIndex++;
				strIndex = i + 1;
			}
			// checks for a period to end the logical expression
			else if (str.charAt(i) == '.' && i == strIndex) {
				wordArr[wordArrIndex] = ".";
				wordArrIndex++;
				strIndex = i + 1;
			}
		}
		return wordArr;
	}

	// checkWords() - checks that there are only key words in the array
	public static boolean checkWords(String[] str) {
		boolean check = true;
		for (int i = 0; i < str.length - 1; i++) {
			if (str[i].equals(".") || str[i].equals("(") || str[i].equals(")") || str[i].equals("AND")
					|| str[i].equals("OR") || str[i].equals("NOT") || str[i].equals("TRUE") || str[i].equals("FALSE")) continue;
			else check = false;
		}
		return check;
	}

	// findNulls() - determines the number of nulls in the array
	public static int findNulls(String[] str) {
		int nullCount = 0;

		for (int i = 0; i < str.length; i++) {
			if (str[i] == null) nullCount++;
		}
		return nullCount;
	}

	// nullVoider() - creates an array copy without nulls
	public static String[] nullVoider(String[] str, int nullCount) {
		String[] newArr = new String[str.length - nullCount];
		for (int i = 0; i < str.length - nullCount; i++) {
			newArr[i] = str[i];
		}
		return newArr;
	}

	// countSections() - counts the number of sections in the array to be evaluated
	public static int countSections(String[] arr) {
		int numSections = 0;
		boolean firstSection = true;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equals("(")) {
				if (firstSection && i > 0) {
					numSections += 2;
					firstSection = false;
				} else numSections++;
			} else if (arr[i].equals(")") && i < arr.length - 1 && !arr[i + 1].equals(".")) {
				numSections++;
			}
		}
		return numSections;
	}

	// createSections() - creates an array of start and stop points for each section
	public static int[] createSections(String[] arr, int numSections) {
		// if no parens
		if (numSections == 0) {
			int[] sections = { 0, arr.length };
			return sections;
			// if parens
		} else {
			int[] sections = new int[numSections * 2];
			sections[0] = 0;
			int counter = 0;
			boolean nonParenSection = true;
			if (arr[0].equals("(")) nonParenSection = false;

			for (int i = 0; i < arr.length; i++) {
				// stops at a period
				if (arr[i].equals(".")) {
					sections[counter] = i - 1;
					break;
					// checks at each open paren
				} else if (arr[i].equals("(")) {
					// if the preceding section was not in parens
					if (nonParenSection && i > 0) {
						counter++;
						sections[counter] = i - 1;
						counter++;
						sections[counter] = i + 1;
						counter++;
						nonParenSection = false;
					} else {
						sections[counter] = i;
						counter++;
						nonParenSection = false;
					}
					// checks at each close paren
				} else if (arr[i].equals(")")) {
					// if there is a following section
					if (i < arr.length - 1 && !arr[i + 1].equals(".") && !arr[i + 1].equals("(")) {
						sections[counter] = i - 1;
						counter++;
						sections[counter] = i + 1;
						nonParenSection = true;
					} else {
						sections[counter] = i - 1;
						counter++;
					}
					// stops at the end of the array
				} else if (i == arr.length - 1) {
					counter++;
					sections[counter] = i;
				}
			}
			return sections;
		}
	}

	// evaluateSection() - evaluates the true/false or and/or behavior of a section
	public static int[] evaluateSection(String[] arr, int[] sections) {
		// KEY:
		// 1 = NOT
		// 2 = TRUE AND NOT
		// 3 = FALSE AND NOT
		// 4 = TRUE OR NOT
		// 5 = FALSE OR NOT
		// 6 = AND
		// 7 = TRUE AND
		// 8 = FALSE AND
		// 9 = OR
		// 10 = TRUE OR
		// 11 = FALSE OR
		// 12 = TRUE
		// 13 = FALSE

		int[] intKeys = new int[sections.length / 2];
		boolean tempBool = false;
		int start = 0;
		int end = 1;

		for (int i = 0; i < sections.length / 2; i++) {
			start = sections[2 * i];
			end = sections[(2 * i) + 1];

			// checks if a section is only AND or OR
			if (end - start == 0) {
				if (arr[start].equals("AND")) {
					intKeys[i] = 6;
				} else if (arr[start].equals("OR")) {
					intKeys[i] = 9;
				}
				// checks the rest of the possible section types
			} else {
				String[] tempArr = new String[end - start + 1];
				int count = 0;
				for (int j = start; j <= end; j++) {
					// if (i == sections.length/2 -1 && j == end -1) continue;
					tempArr[count] = arr[j];
					count++;
				}
				tempBool = findTruth(tempArr);
				// assigns values based on outcome
				if (tempBool == true && arr[end].equals("NOT") && arr[end - 1].equals("AND")) intKeys[i] = 2;
				else if (tempBool == false && arr[end].equals("NOT") && arr[end - 1].equals("AND")) intKeys[i] = 3;
				else if (tempBool == true && arr[end].equals("NOT") && arr[end - 1].equals("OR")) intKeys[i] = 4;
				else if (tempBool == false && arr[end].equals("NOT") && arr[end - 1].equals("OR")) intKeys[i] = 5;
				else if (tempBool == true && arr[end].equals("AND")) intKeys[i] = 7;
				else if (tempBool == false && arr[end].equals("AND")) intKeys[i] = 8;
				else if (tempBool == true && arr[end].equals("OR")) intKeys[i] = 10;
				else if (tempBool == false && arr[end].equals("OR")) intKeys[i] = 11;
				// used for the last section
				else if (tempBool == true && i == sections.length / 2 - 1 && arr[start].equals("AND")) intKeys[i] = 7;
				else if (tempBool == false && i == sections.length / 2 - 1 && arr[start].equals("AND")) intKeys[i] = 8;
				else if (tempBool == true && i == sections.length / 2 - 1 && arr[start].equals("OR")) intKeys[i] = 10;
				else if (tempBool == false && i == sections.length / 2 - 1 && arr[start].equals("OR")) intKeys[i] = 11;
				// used for items in parens
				else if (tempBool == true) intKeys[i] = 12;
				else if (tempBool == false) intKeys[i] = 13;
			}
		}
		return intKeys;
	}

	// findTruth() - determines if the section is true or false
	public static boolean findTruth(String[] str) {
		boolean bool = false;
		int before = 0;
		int after = 0;
		int count = 0;

		// checks AND statements
		for (int i = 0; i < str.length; i++) {
			if (str[i].equals("AND") && i != 0) {
				// checks before the AND
				if (str[i - 1].equals("TRUE")) {
					if (i - 2 >= 0 && str[i - 2].equals("NOT")) before--;
					else before++;
				} else if (str[i - 1].equals("FALSE")) {
					if (i - 2 >= 0 && str[i - 2].equals("NOT")) before++;
					else before--;
				}
				// checks after the AND
				if (str[i + 1].equals("NOT")) {
					if (i + 2 < str.length && str[i + 2].equals("TRUE")) after--;
					else after++;
				} else if (str[i + 1].equals("FALSE")) after--;
				else if (str[i + 1].equals("TRUE")) after++;
			}
			if (before > 0 && after > 0) count++;
		}
		before = 0;
		after = 0;

		// checks OR statements
		for (int i = 0; i < str.length; i++) {
			if (str[i].equals("OR") && i != 0) {
				// checks before the OR
				if (str[i - 1].equals("TRUE")) {
					;
					if (i - 2 >= 0 && str[i - 2].equals("NOT")) before--;
					else before++;
				} else if (str[i - 1].equals("FALSE")) {
					if (i - 2 >= 0 && str[i - 2].equals("NOT")) before++;
					else before--;
				}
				// checks after the OR
				if (i + 1 < str.length) {
					if (str[i + 1].equals("NOT")) {
						if (i + 2 < str.length && str[i + 2].equals("TRUE")) after--;
						else after++;
					} else if (str[i + 1].equals("FALSE")) after--;
					else if (str[i + 1].equals("TRUE")) after++;
				}
			}
			if (before > 0 || after > 0) count++;
		}

		if (count > 0) bool = true;
		return bool;
	}
}
