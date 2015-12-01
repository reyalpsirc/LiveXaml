import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import static com.sun.nio.file.SensitivityWatchEventModifier.HIGH;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;


public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		//current directory
		if (args.length>0 && args[0].toString().equals("start")){
			Path dir = Paths.get("",new String[]{});
			System.out.println("my dir: "+dir.toAbsolutePath().toString());
			WatchService watcher = FileSystems.getDefault().newWatchService();
			dir.register(watcher, new WatchEvent.Kind[]{ENTRY_MODIFY,ENTRY_MODIFY,ENTRY_DELETE}, HIGH);
			// Start the infinite polling loop
	        WatchKey key = null;
	        while (true) {
	            key = watcher.take();
	
	            // Dequeueing events
	            Kind<?> kind = null;
	            for (WatchEvent<?> watchEvent : key.pollEvents()) {
	                // Get the type of the event
	                kind = watchEvent.kind();
	                if (OVERFLOW == kind) {
	                    continue; // loop
	                } else if (ENTRY_CREATE == kind || ENTRY_MODIFY == kind) {
	                    Path newPath = ((WatchEvent<Path>) watchEvent).context();
	                    String filename=newPath.getFileName().toString();
	                    CreateOrUpdateFileIfXAML(filename);
	                    
	                } else if (ENTRY_DELETE == kind) {
	                    Path newPath = ((WatchEvent<Path>) watchEvent).context();
	                    String filename=newPath.getFileName().toString();
	                    DeleteFileIfXAML(filename);
	                }
	            }
	
	            if (!key.reset()) {
	                break; // loop
	            }
	        }
		}
	}

	private static void CreateOrUpdateFileIfXAML(String filename) {
		System.out.println(CouchdbHelper.Fetch(""));
		System.out.println("New file created or updated: " + filename);
	}
	
	private static void DeleteFileIfXAML(String filename) {
		System.out.println("New path deleted: " + filename);
	}

}

