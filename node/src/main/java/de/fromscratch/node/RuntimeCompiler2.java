package de.fromscratch.node;

/**
 *
 * @author maxg
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import java.util.Arrays;
import java.util.regex.Pattern;



public class RuntimeCompiler2 {

    // the node name, to become the name of the temporal compiled class
    String className = "";
        
    // original name of the underlying class
	String lastSource = null;
	boolean update = false;

	Path originalSourceFile;
	Path tmpSourceFile;
        
	String classPathRoot;
	String originalClassName;
    String classPathPrefix;    
	
	public static final String SRC_ROOT = "src/main/java/";
	public static String TMP_ROOT = "tmp/";
	
	
	
	public RuntimeCompiler2 (String theNodeName, String theOriginalClassPath, String baseDir, String theClassPathPrefix) {
            
		
		className = theNodeName;
		classPathPrefix = theClassPathPrefix;
		
		TMP_ROOT = baseDir+"/";
		if (!theOriginalClassPath.contains("/")) {
			theOriginalClassPath = theOriginalClassPath.replace(".", "/");
		}
		
		originalClassName = theOriginalClassPath.substring(theOriginalClassPath.lastIndexOf("/")+1);
		classPathRoot     = theOriginalClassPath.substring(0, theOriginalClassPath.lastIndexOf("/"));
		originalSourceFile = Paths.get(SRC_ROOT+theOriginalClassPath+".java");   
		tmpSourceFile      = Paths.get(TMP_ROOT+"/"+classPathPrefix+"/"+theOriginalClassPath.replace(originalClassName, className)+".java");
		
		// check 1st if tmp path + file already exists, if yes load this file
		if (Files.exists(tmpSourceFile)) {
			
        }
            
		else {
			
			// if not create tmp path and file and change content to new filename and classpath
			try {
				
				Files.createDirectories(tmpSourceFile.getParent());
				//Files.copy(originalSourceFile, tmpSourceFile, StandardCopyOption.REPLACE_EXISTING);
				List<String> lines = Files.readAllLines (originalSourceFile);
				String data = "";
				
				for (String line: lines) {
					data += line+"\n";
				}
				
				String packageName = theOriginalClassPath.replace ("/", ".");
				packageName = packageName.substring(0, packageName.lastIndexOf("."));
				
				data = data.replace (packageName, classPathPrefix+"."+packageName);
				data = data.replace (originalClassName, className);
				
				Files.write (tmpSourceFile, data.getBytes());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
        
	public String getCode() {
		return lastSource;
	}
        
	public void updateTmpPath(String theTmpPath) {
		tmpSourceFile = Paths.get(tmpSourceFile.toString().replace(TMP_ROOT, theTmpPath));
		TMP_ROOT = theTmpPath;
	}
	
	public void updatePatchPath(String theTmpPath) {
		tmpSourceFile = Paths.get(tmpSourceFile.toString().replace(classPathPrefix, theTmpPath));
		TMP_ROOT = theTmpPath;
	}
	
	
	
	// check file change in seperate thread, recompile in main thread
	public boolean checkCodeUpdate () {
	         
		
		boolean ret = false;
		try {
			List<String> lines  = Files.readAllLines(tmpSourceFile);
			String src = "";
			for (String line: lines) {
				src += line+"\n";
			}
			
			if (!src.equals(lastSource) || lastSource==null) {
				ret = true;
			}
			lastSource = src;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public Object recompile () {
		
		try {		

			Path compilationTargetPath = Paths.get(TMP_ROOT);

			// init toolchain
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager (null, null, null);
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(compilationTargetPath.toFile()));
				
			// prepare compiler
			List<String> optionList = new ArrayList<>();
			List<JavaFileObject> compilationObjects = new ArrayList<>();
			
            // load file as custom file object 
            JavaFileObject fileObject = new JavaFileObject (tmpSourceFile.toString(), Kind.SOURCE);
			compilationObjects.add(fileObject);
			
			// compile todo: check if error
			JavaCompiler.CompilationTask task = compiler.getTask (null,fileManager,null,optionList,null,compilationObjects);
			task.call();
	
			// instantiate class from freshly compiled byte code location
            String newClassPath = classPathPrefix+"/"+classPathRoot+"/"+className;
            newClassPath = newClassPath.replaceAll("/", ".");
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {compilationTargetPath.toUri().toURL() });
            
            Class<?> cls = classLoader.loadClass(newClassPath);
            //Class<?> cls = Class.forName(newClassPath, false, classLoader); 
            Object instance = cls.newInstance(); 
		
            return instance;
        }
		
        catch (Exception e) {
        	e.printStackTrace();
        	return null;
        }
	}
	
	
	public class JavaFileObject extends SimpleJavaFileObject {
	
		OutputStream byteArrayOutputStream;
		String source = "";
            
		protected JavaFileObject (String newFilePath, Kind kind) {
                
	        // target
			super(Paths.get (newFilePath).toUri(), kind);
			
			// load content
			try {
				for (String line: Files.readAllLines(Paths.get (newFilePath), Charset.defaultCharset())) {
					source += line+"\n";	
	            }
				//source = source.replaceAll(originalClassName, nodeName);
			}
	           
			catch (Exception e) {
				e.printStackTrace();
			}   
			byteArrayOutputStream = new ByteArrayOutputStream();
		}
            
               
		@Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return source;
        }

		@Override
		public OutputStream openOutputStream() throws IOException {
            return byteArrayOutputStream;
        }
    }
	

	public void setCode(String theCode) {
		try {
			// check if classname changed
			String[] lines = theCode.split(Pattern.quote("\n"));
			String newClassName = originalClassName;
			Path sourceFile = tmpSourceFile; 
			
			for (String line: lines) {
				if (line.startsWith("public class ")) {
					newClassName = line.substring(13, line.indexOf(" ", 13));
					break;
				}
			}
			
			if (!newClassName.equals(originalClassName)) {
				String newFile = tmpSourceFile.toString().replace(originalClassName, newClassName);
				originalClassName = newClassName;
				className = newClassName;
				sourceFile = Paths.get(newFile);	
			}
			
			Files.write(sourceFile, theCode.getBytes());
			tmpSourceFile = sourceFile;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
