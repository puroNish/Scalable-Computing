package ccRepoFiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class CycloResolution {

	public File getFile(String path) {
		File file = new File(path);
		return file;
	}

	public List<String> getLines(String fileName) throws IOException {
		/*
		 * String content = new String(Files.readAllBytes(Paths.get(fileName)));
		 * System.out.println("Content ::"); System.out.println(content);
		 */
		List<String> lines = Files.readAllLines(Paths.get(fileName));
		// System.out.println("Lines ::");
		// System.out.println(lines);

		return lines;
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String fileName = "C:\\Users\\nishp\\Music\\demo.java";
		CycloResolution cc = new CycloResolution();
		int temp = cc.calculateCC(fileName);
		
		System.out.println("CC :: " + temp);
	}
	
	public int calculateCC(String fileName) throws IOException {
		CycloResolution cc = new CycloResolution();
		List<String> ccLines = cc.getLines(fileName);
		List<String> ifs = Arrays.asList("if", "?");
		List<String> loops = Arrays.asList("while", "case", "for", "switch", "do");
		List<String> returns = Arrays.asList("return");
		List<String> conditions = Arrays.asList("&&", "||", "or", "and", "xor");
		int complexity = 2;
		for (String line : ccLines) {
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
		System.out.println(complexity);
		return complexity;
	}
}
