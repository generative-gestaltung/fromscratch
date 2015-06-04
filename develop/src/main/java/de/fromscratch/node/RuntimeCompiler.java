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



public class RuntimeCompiler {

    // the node name, to become the name of the temporal compiled class
    String nodeName = "";
        
    // original name of the underlying class
	String lastSource = null;
	boolean update = false;

	Path originalSourceFile;
	Path tmpSourceFile;
        
	String classPathRoot;
	String originalClassName;
        
	public static final String SRC_ROOT = "src/main/java/";
	public static String TMP_ROOT = "tmp/";
	
	public RuntimeCompiler (String theNodeName, String theOriginalClassPath, String baseDir) {
            
		nodeName = theNodeName;
		TMP_ROOT = baseDir+"/";
            
		originalClassName = theOriginalClassPath.substring(theOriginalClassPath.lastIndexOf("/")+1);
		classPathRoot     = theOriginalClassPath.substring(0, theOriginalClassPath.lastIndexOf("/"));
		originalSourceFile = Paths.get(SRC_ROOT+theOriginalClassPath+".java");   
		tmpSourceFile      = Paths.get(TMP_ROOT+theOriginalClassPath.replace(originalClassName, nodeName)+".java");
            
		// check 1st if tmp path + file already exists, if yes load this file
		if (Files.exists(tmpSourceFile)) {
			
        }
            
		else {
			// if not create tmp path and file
			try {
				Files.createDirectories(tmpSourceFile.getParent());
				Files.copy(originalSourceFile, tmpSourceFile, StandardCopyOption.REPLACE_EXISTING);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
        
        
	public void updateTmpPath(String theTmpPath) {
		tmpSourceFile = Paths.get(tmpSourceFile.toString().replace(TMP_ROOT, theTmpPath));
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
            String newClassPath = classPathRoot+"/"+nodeName;
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
				source = source.replaceAll(originalClassName, nodeName);
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
}
