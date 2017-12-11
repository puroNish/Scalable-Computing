package repoClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CcCalculator {
	
	public int calculateCC(File file) throws IOException {
		FileReader fileReader = new FileReader(file);
		@SuppressWarnings("resource")
		BufferedReader bufferedReader = new BufferedReader(fileReader);
//		List<String> ccLines = (fileName);
		List<String> ifs = Arrays.asList("if", "?");
		List<String> loops = Arrays.asList("while", "case", "for", "switch", "do");
		List<String> returns = Arrays.asList("return");
		List<String> conditions = Arrays.asList("&&", "||", "or", "and", "xor");
		int complexity = 2;
		String line;
		while((line = bufferedReader.readLine())!=null) {
			// System.out.println(i);
			String[] wordsInLine = line.split(" |;|\t");
			boolean ifFlag = false;
			// System.out.println(Arrays.toString(words));
			for (String perWord : wordsInLine) {
				if (ifs.contains(perWord)) {
					ifFlag = true;
//					System.out.println(perWord);
					complexity++;
				}
				if (loops.contains(perWord)) {
//					System.out.println(perWord);
					complexity++;
				}
				if (conditions.contains(perWord)) {
//					System.out.println(perWord);
					if(ifFlag) {
						complexity+=2;
					}else {
						complexity++;
					}
				}
				if (returns.contains(perWord)) {
//					System.out.println(perWord);
					complexity--;
				}
			}
		}
//		System.out.println(complexity);
		return complexity;
	}

}
