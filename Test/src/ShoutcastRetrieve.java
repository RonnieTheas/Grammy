
	
	import java.io.BufferedReader;
import java.io.IOException;
	import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
	import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	import java.util.regex.Matcher;
	import java.util.regex.Pattern;

	public class ShoutcastRetrieve {

	protected URL streamUrl;
	private Map<String, String> metadata;
	private boolean isError;

	public ShoutcastRetrieve(URL streamUrl) {
	    setStreamUrl(streamUrl);

	    isError = false;
	}

	/**
	 * Get artist using stream's title
	 *
	 * @return String
	 * @throws IOException
	 */
	public String getArtist() throws IOException {
	    Map<String, String> data = getMetadata();

	    if (!data.containsKey("StreamTitle"))
	        return "";

	    String streamTitle = data.get("StreamTitle");
	    String title = streamTitle.substring(0, streamTitle.indexOf("-"));
	    return title.trim();
	}

	/**
	 * Get title using stream's title
	 *
	 * @return String
	 * @throws IOException
	 */
	public String getTitle() throws IOException {
	    Map<String, String> data = getMetadata();

	    if (!data.containsKey("StreamTitle"))
	        return "";

	    String streamTitle = data.get("StreamTitle");
	    String artist = streamTitle.substring(streamTitle.indexOf("-")+1);
	    return artist.trim();
	}

	public Map<String, String> getMetadata() throws IOException {
	    if (metadata == null) {
	        refreshMeta();
	    }

	    return metadata;
	}

	public void refreshMeta() throws IOException {
	    retreiveMetadata();
	}

	private void retreiveMetadata() throws IOException {
	    URLConnection con = streamUrl.openConnection();
	    con.setRequestProperty("User-Agent", "Mozilla/5.0");
	    con.setRequestProperty("Icy-MetaData", "1");
	    con.setRequestProperty("Connection", "close");
	    con.setRequestProperty("Accept", null);
	    con.connect();

	    int metaDataOffset = 0;
	    Map<String, List<String>> headers = con.getHeaderFields();
	    InputStream stream = con.getInputStream();

	    if (headers.containsKey("icy-metaint")) {
	        // Headers are sent via HTTP
	        metaDataOffset = Integer.parseInt(headers.get("icy-metaint").get(0));
	    } else {
	        // Headers are sent within a stream
	        StringBuilder strHeaders = new StringBuilder();
	        char c;
	        while ((c = (char)stream.read()) != -1) {
	            strHeaders.append(c);
	            if (strHeaders.length() > 5 && (strHeaders.substring((strHeaders.length() - 4), strHeaders.length()).equals("\r\n\r\n"))) {
	                // end of headers
	                break;
	            }
	        }
System.out.println(strHeaders);
	        // Match headers to get metadata offset within a stream
	        Pattern p = Pattern.compile("\\r\\n(icy-metaint):\\s*(.*)\\r\\n");
	        Matcher m = p.matcher(strHeaders.toString());
	        if (m.find()) {
	            metaDataOffset = Integer.parseInt(m.group(2));
	        }
	    }

	    // In case no data was sent
	    if (metaDataOffset == 0) {
	        isError = true;
	        return;
	    }

	    // Read metadata
	    int b;
	    int count = 0;
	    int metaDataLength = 4080; // 4080 is the max length
	    boolean inData = false;
	    StringBuilder metaData = new StringBuilder();
	    // Stream position should be either at the beginning or right after headers
	    while ((b = stream.read()) != -1) {
	        count++;

	        // Length of the metadata
	        if (count == metaDataOffset + 1) {
	            metaDataLength = b * 16;
	        }

	        if (count > metaDataOffset + 1 && count < (metaDataOffset + metaDataLength)) {
	            inData = true;
	        } else {
	            inData = false;
	        }
	        if (inData) {
	            if (b != 0) {
	                metaData.append((char)b);
	            }
	        }
	        if (count > (metaDataOffset + metaDataLength)) {
	            break;
	        }

	    }

	    // Set the data
	    metadata = ShoutcastRetrieve.parseMetadata(metaData.toString());

	    // Close
	    stream.close();
	}

	public boolean isError() {
	    return isError;
	}

	public URL getStreamUrl() {
	    return streamUrl;
	}

	public void setStreamUrl(URL streamUrl) {
	    this.metadata = null;
	    this.streamUrl = streamUrl;
	    this.isError = false;
	}

	public static Map<String, String> parseMetadata(String metaString) {
	    Map<String, String> metadata = new HashMap();
	    String[] metaParts = metaString.split(";");
	    Pattern p = Pattern.compile("^([a-zA-Z]+)=\\'([^\\']*)\\'$");
	    Matcher m;
	    for (int i = 0; i < metaParts.length; i++) {
	        m = p.matcher(metaParts[i]);
	        if (m.find()) {
	            metadata.put(m.group(1), m.group(2));
	        }
	    }

	    return metadata;
	}
	
	public static void main(String[] args) {
		/*try {
		URL url = new URL("http://s4.bb-stream.com:10319");
		ShoutcastRetrieve a = new ShoutcastRetrieve(url);
		a.getMetadata();
		} catch (Exception e){e.printStackTrace();} 
		*/
		String htmlCode = "";

		try {
			URL url = new URL("http://s4.bb-stream.com:10319/7.html");
		    //URL url = new URL("http://cassini.shoutca.st:8266/status-json.xsl"); 
			//URL url = new URL("http://us.unitystreams.net:8003/status-json.xsl"); 
			// {"icestats":{"admin":"icemaster@localhost","host":"us.unitystreams.net","location":"Earth","server_id":"Icecast 2.4.2","server_start":"Thu, 29 Jun 2017 10:59:46 -0700","server_start_iso8601":"2017-06-29T10:59:46-0700","source":{"bitrate":64,"genre":"Misc","listener_peak":2,"listeners":0,"listenurl":"http://us.unitystreams.net:8003/live","server_description":"Unspecified description","server_name":"Unspecified name","server_type":"audio/mpeg","server_url":"http://","stream_start":"Thu, 29 Jun 2017 11:02:20 -0700","stream_start_iso8601":"2017-06-29T11:02:20-0700","title":"The Things We Do For Love","dummy":null}}}
		    // {"icestats":{"admin":"icemaster@localhost","host":"cassini.shoutca.st" ,"location":"Earth","server_id":"Icecast 2.4.2","server_start":"Thu, 29 Jun 2017 00:21:06 +0000","server_start_iso8601":"2017-06-29T00:21:06+0000","source":[{"audio_info":"channels=2;samplerate=44100;bitrate=96","bitrate":96,"channels":2,"genre":"Unspecified","listener_peak":1,"listeners":1,"listenurl":"http://cassini.shoutca.st:8266/autodj","samplerate":44100,"server_description":"Unspecified description","server_name":"Ronnie Stream","server_type":"audio/mpeg","server_url":"http://localhost/","stream_start":"Thu, 29 Jun 2017 00:21:07 +0000","stream_start_iso8601":"2017-06-29T00:21:07+0000","title":"Buggles - Video Killed the Radio Star","dummy":null},{"listeners":0,"listenurl":"http://cassini.shoutca.st:8266/live","dummy":null},{"listeners":0,"listenurl":"http://cassini.shoutca.st:8266/stream","dummy":null}]}}
		    
		    //URL url = new URL("http://50.22.218.101:12028/7.html");
		    //URL url = new URL("http://us.unitystreams.net:8025/7.html");
			//URL url = new URL("http://www.msn.com"); 
		    URLConnection con = url.openConnection();
		    con.setRequestProperty("User-Agent", "Mozilla/5.0");
		    //con.setRequestProperty("Icy-MetaData", "1");
		    //con.setRequestProperty("Connection", "close");
		    //con.setRequestProperty("Accept", null);
		    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

		    String inputLine;

		    while ((inputLine = in.readLine())!= null)
		        htmlCode += inputLine;
		    System.out.println(htmlCode);
		    System.out.println(htmlCode.length());
		   
		    in.close();     
		} catch (Exception e){
		    e.printStackTrace();
		}
		
		/*try{
		URL yahoo = new URL("http://www.yahoo.com/");
        URLConnection yc = yahoo.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                yc.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null) 
            System.out.println(inputLine);
        in.close();
		} catch (Exception e){e.printStackTrace();} */
	}
}


