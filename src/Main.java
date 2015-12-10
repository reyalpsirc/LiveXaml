import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import static com.sun.nio.file.SensitivityWatchEventModifier.HIGH;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collection;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;


public class Main extends WebSocketServer{
	
	private static Main sv;
	private static Path filepath;
	
	private Main( int port ) throws UnknownHostException {
		super( new InetSocketAddress( port ) );
	}

	private Main( InetSocketAddress address ) {
		super( address );
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		//current directory
		if (args.length>0){
			if (!args[0].toString().toLowerCase().endsWith(".xaml")){
				System.out.println("\""+args[0].toString()+"\" is not a xaml file.");
				return;
			}
			filepath = Paths.get(args[0].toString(),new String[]{});
			File f = new File(filepath.toAbsolutePath().toString());
			if(!f.exists() || f.isDirectory()) { 
				System.out.println("\""+args[0].toString()+"\" is a directory or does not exist.");
				return;
			}
			//create the websocket
			CreateWebsocketServer();
			//watch for file changes
			WatchFileChanges();
		}
	}

	private static void CreateWebsocketServer() throws UnknownHostException {
		WebSocketImpl.DEBUG = false;
		int port = 8887; // 843 flash policy port
		sv = new Main( port );
		sv.start();
		System.out.println( "LiveXAML server started on port: " + sv.getPort() );
	}



	private static void WatchFileChanges() throws IOException, InterruptedException {
		String fileLocation=filepath.toAbsolutePath().toString();
		System.out.println("Location: "+fileLocation);
		File f=new File(fileLocation);
		Path dir=Paths.get(f.getAbsoluteFile().getParent());
		System.out.println("Dir: "+dir);
		WatchService watcher = FileSystems.getDefault().newWatchService();
		dir.register(watcher, new WatchEvent.Kind[]{ENTRY_CREATE,ENTRY_MODIFY}, HIGH);
		// Start the infinite polling loop
        WatchKey key = null;
        while (true) {
            key = watcher.take();

            Kind<?> kind = null;
            for (WatchEvent<?> watchEvent : key.pollEvents()) {
                // Get the type of the event
                kind = watchEvent.kind();
                if (OVERFLOW == kind) {
                    continue; // loop
                } else if (ENTRY_CREATE == kind || ENTRY_MODIFY == kind) {
                	final Path changed = (Path) watchEvent.context();
                    if (changed.toString().equals(filepath.getFileName().toString())) {
                    	System.out.println("File updated!");
                    	SeverSocketUpdate();
                    }
                }
            }

            if (!key.reset()) {
                break; // loop
            }
        }
		
	}

	private static void SeverSocketUpdate() throws IOException {
		//read the file
		/*byte[] encoded = Files.readAllBytes(filepath);
		String data=new String(encoded,StandardCharsets.US_ASCII);*/
		String defaultEncoding = "UTF-8";
		InputStream inputStream = new FileInputStream(filepath.toAbsolutePath().toString());
		try {
		    BOMInputStream bOMInputStream = new BOMInputStream(inputStream);
		    ByteOrderMark bom = bOMInputStream.getBOM();
		    String charsetName = bom == null ? defaultEncoding : bom.getCharsetName();
		    BufferedReader in = new BufferedReader(new InputStreamReader(new BufferedInputStream(bOMInputStream), charsetName));
			String data="";
			String line;
			while( (line = in.readLine()) != null) { 
				data+=line;
			}
			//send file data
			sv.sendToAll(data);			
		} finally {
		    inputStream.close();
		}
	}

	@Override
	public void onOpen( WebSocket conn, ClientHandshake handshake ) {
		System.out.println( conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!" );
		try {
			SeverSocketUpdate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
		/*this.sendToAll( conn + " has left the room!" );
		System.out.println( conn + " has left the room!" );*/
	}

	@Override
	public void onMessage( WebSocket conn, String message ) {
		System.out.println("Exception: " + message );
	}

	@Override
	public void onFragment( WebSocket conn, Framedata fragment ) {
		//System.out.println( "received fragment: " + fragment );
	}
	
	@Override
	public void onError( WebSocket conn, Exception ex ) {
		ex.printStackTrace();
		if( conn != null ) {
			// some errors like port binding failed may not be assignable to a specific websocket
		}
	}

	/**
	 * Sends <var>text</var> to all currently connected WebSocket clients.
	 * 
	 * @param text
	 *            The String to send across the network.
	 * @throws InterruptedException
	 *             When socket related I/O errors occur.
	 */
	public void sendToAll( String text ) {
		Collection<WebSocket> con = connections();
		synchronized ( con ) {
			for( WebSocket c : con ) {
				c.send( text );
			}
		}
	}

}

