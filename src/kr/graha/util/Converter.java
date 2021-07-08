package kr.graha.util;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;

/**
 * Graha(그라하) 소스코드를 Apache Tomcat 10 에 맞춰서 변경한다.
 * Apache Tomcat 10 이후의 버전에서는 변경사항은 다음과 같다.
 * jakarta.servlet으로 변경되었다.
 * 오래된 Apache Common FileUpload 라이브러리가 동작하지 않는 대신, Apache Tomcat 10 에 내장된 것을 사용한다.
 * 주의사항 : import 구문만을 변경한다.
 * import javax.servlet 을 import jakarta.servlet 으로 변경한다.
 * import org.apache.commons.fileupload 를 import org.apache.tomcat.util.http.fileupload 로 변경한다.
 * Graha(그라하) 소스코드가 아닌 경우 오동작 할 가능성이 매우 높다.

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.5.0.2
 */

public class Converter {
	public static void main(String[] args) throws Exception {
		Converter c = new Converter();
		File f = null;
		if(args.length > 0) {
			f = new File(args[0]);
		}
		if(f == null || !f.exists()) {
			f = new File(System.getProperty("user.dir"));
		}
		c.execute(f);
	}
	private void execute(File f) throws IOException {
		File origin = new File(f.getPath() + java.io.File.separator + "src");
		
		File target = new File(f.getPath() + java.io.File.separator + "tomcat10");
		execute(origin, origin.getPath(), target.getPath());
	}
	private void execute(File origin, String prefix, String targetPrefix) throws IOException {
		File[] files = origin.listFiles();
		if(files != null) {
			for(int i = 0; i < files.length; i++) {
				if(files[i].getPath().endsWith("~")) {
					continue;
				}
				if(files[i].isDirectory()) {
					execute(files[i], prefix, targetPrefix);
				} else if(files[i].isFile()) {
					copy(files[i], prefix, targetPrefix);
				}
			}
		}
	}
	private void copy(File file, String prefix, String targetPrefix) throws IOException {
		
		File target = new File(targetPrefix + file.getPath().substring(prefix.length()));
		if(!target.getParentFile().exists()) {
			target.getParentFile().mkdirs();
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(target));
		String importServlet = "import javax.servlet.";
		String replaceServlet = "import jakarta.servlet.";
		String importFileUpload = "import org.apache.commons.fileupload.";
		String replaceFileUpload = "import org.apache.tomcat.util.http.fileupload.";
/*
tomcat-coyote.jar
*/
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line;
		while ((line = br.readLine()) != null) {
			if(file.getPath().endsWith(".java") && line.startsWith(importServlet)) {
				bw.write(replaceServlet + line.substring(importServlet.length()));
				bw.newLine();
			} else if(file.getPath().endsWith(".java") && line.startsWith(importFileUpload)) {
				bw.write(replaceFileUpload + line.substring(importFileUpload.length()));
				bw.newLine();
			} else {
				bw.write(line);
				bw.newLine();
			}
		}
		br.close();
		bw.flush();
		bw.close();
	}
}