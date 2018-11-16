import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TriviaContest {

	private boolean newSong = false;
	private String songName = "";
	private String oldname = "";
	private String songArtist = "";
	
	public final static String URL = "http://s4.bb-stream.com:10319/7.html";
	
	public class ArtistInfo
	{
		String artistName;
		List<String> songs;
		List<String> trivia;
		String genre;
	}
	
	public class MultipleChoice
	{
		String[] wrongAnswers;
		String theAnswer;
	}
	
	static final String WIKI = "c:\\Users\\rtheas\\Documents\\cheerwiki.txt"; //File containing artist info
	static final String PLAYLIST = "c:\\Users\\rtheas\\Documents\\playlist.txt"; //playlist of songs to be played
	
	Map<String,ArtistInfo> artists = new HashMap<String,ArtistInfo>();
	Map<String,String> playList = new HashMap<String,String>();
	
	// Trim and remove "The " and capitalize
	public static String normalizeLesser(String aInS) {
		String u = aInS.toUpperCase();
		u = u.trim();
		if (u.startsWith("THE ")) {
			u = u.substring(4);
		}

		return u;
	}
	
	public static boolean caseContains(String n, List<String> l)
	{
		if ((n==null) || (l==null)) return false;
		
		for (String s:l)
		{
			if (n.equalsIgnoreCase(s))
			{
				return true;
			}
		}
		return false;
	}
	
	static String getArtist(String line) {
		if (!line.startsWith("ARTIST;")) {
			return null;
		}

		String p[] = line.split(";");
		if (p.length >= 2) {
			return p[1];
		}
		return null;
	}
	
	static String getPLArtist(String line)
	{
		String items[] = line.split(" - ");
		if (items.length>=2)
		{
			return items[1];
		}
		return null;
	}
	
	static String getPLTitle(String line)
	{
		String items[] = line.split(" - ");
		if (items.length>=2)
		{
			return items[0];
		}
		return null;
	}
	
	
	static String[] getSongs(String line) {
		if (!line.startsWith("TRUE;") && !line.startsWith("BOUGHT;")) {
			return null;
		}

		String p[] = line.split(";");
		if (p.length >= 2) {
			String songs[] =  p[1].split("\\|");
		
			for (int i=0; i<songs.length;i++)
			{
				songs[i] = songs[i].trim();
			}
			return songs;
		}
		return null;
	}
	
	static String getTrivia(String line) {
		if (!line.startsWith("TRIVIA;")) {
			return null;
		}

		String p[] = line.split(";");
		if (p.length >= 2) {
			String trivia =  p[1].trim();
		
			return trivia;
		}
		return null;
	}

	static String getGenre(String line) {
		if (!line.startsWith("ARTIST;")) {
			return null;
		}

		String p[] = line.split(";");
		if (p.length >= 4) {
			return p[3].trim();
		}
		return null;
	}
	
	static boolean  getQuestion(String line, boolean question) {
		if (!line.startsWith("QUESTION;")) {
			return question;
		}

		String p[] = line.split(";");
		if (p.length >= 2) {
			return "TRUE".equals(p[1].trim());
		}
		return false;
	}
	
	void loadPlaylist()
	{
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(PLAYLIST));
			String line = null;
			while ((line = br.readLine()) != null) 
			{
				String artist = getPLArtist(line);
				String title = getPLTitle(line);
				if ((artist==null) || (title==null)) continue;
				if (artists.get(normalizeLesser(artist))==null)
				{
					System.out.println("Artist not found: "+line);
					continue;
				}
				playList.put(title, normalizeLesser(artist));
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	void loadArtists()
	{
		String currentArtist=null;
		boolean question=false; 
		List<String> songList=null;
		List<String> triviaList=null;
		ArtistInfo artistInfo=null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(WIKI));
			String line = null;
			while ((line = br.readLine()) != null) 
			{
				String artist = getArtist(line);
				if (artist!=null) 
				{
					if (currentArtist!=null)
					{
						
					   artistInfo.songs = songList;
					   artistInfo.trivia = triviaList;
				       artists.put(currentArtist,artistInfo);
					}
					question = false;
					currentArtist = normalizeLesser(artist);
					songList = new ArrayList<String>(1);
					triviaList = new ArrayList<String>(1);
					artistInfo = new ArtistInfo();
					artistInfo.genre = getGenre(line);
					artistInfo.artistName = artist.trim();
					continue;
				}
				if (currentArtist==null)  continue;
				
				question = getQuestion(line,question);
				
				String triv = getTrivia(line);
				if (triv!=null)
				{
					triviaList.add(triv);
				}
				
				if (!question) continue;
                String[] songs = getSongs(line);
                if (songs!=null)
                {
                	songList.addAll(Arrays.asList(songs));
                }
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public MultipleChoice songQuestion(String artist)
	{
		ArtistInfo info = artists.get(normalizeLesser(artist));
		if (info==null) return null;
		String genre = info.genre;
		
		List<String> bands = new ArrayList<String>(artists.keySet());
		
		Iterator<String> it = bands.iterator();
		while (it.hasNext())
		{
			String s = it.next();
			ArtistInfo ay = artists.get(s);
			if (ay==null)
			{
				it.remove();
				continue;
			}
			if (!genre.equals(ay.genre))
			{
				it.remove();
				continue;
			}
		}
		
		int l = bands.size();
		if (l<3)
		{
			System.out.println("ERROR- not enough artists to choose from to get false answers.");
			return null;
		}
		List<String> answers = new ArrayList<String>();
		while (answers.size()<3)
		{
			Random rand = new Random();
			int  index = rand.nextInt(l);
			String newAnswer = bands.get(index);
			ArtistInfo ay = artists.get(newAnswer);
			newAnswer = ay.artistName;
			if (!answers.contains(newAnswer) && !artist.equals(newAnswer))
			{
				answers.add(newAnswer);
			}
			
		}
		MultipleChoice ret = new MultipleChoice();
		ret.theAnswer = artist;
		ret.wrongAnswers = (String[]) answers.toArray(new String[0]);
		return ret;
	}
	
	public MultipleChoice bonusQuestion(String artist, String song)
	{
		ArtistInfo info = artists.get(normalizeLesser(artist));
		if (info==null) return null;
		String genre = info.genre;
		
		List<String> bands = new ArrayList<String>(artists.keySet());
		
		Iterator<String> it = bands.iterator();
		while (it.hasNext())
		{
			String s = it.next();
			ArtistInfo ay = artists.get(s);
			if (ay==null)
			{
				it.remove();
				continue;
			}
			if (!genre.equals(ay.genre))
			{
				it.remove();
				continue;
			}
		}
		
		int l = bands.size();
		if (l<3)
		{
			System.out.println("ERROR- not enough artists to choose from to get false answers.");
			return null;
		}
		List<String> bandanswers = new ArrayList<String>();
		List<String> songanswers = new ArrayList<String>();
		
		List<String> artistSongs = info.songs;
		while (bandanswers.size()<3)
		{
			Random rand = new Random();
			int  index = rand.nextInt(l);
			String newAnswer = bands.get(index);
			ArtistInfo ay = artists.get(newAnswer);
			newAnswer = ay.artistName;
			if (!bandanswers.contains(newAnswer) && !artist.equals(newAnswer))
			{
				List<String> songs = ay.songs;
				if (songs!=null && songs.size()>0)
				{
				   index = rand.nextInt(songs.size());
				   String newSong = songs.get(index);
				   if (!caseContains(newSong,songanswers) && !song.equalsIgnoreCase(newSong) && !caseContains(newSong,artistSongs))
				   {
					   bandanswers.add(newAnswer);
					   songanswers.add(newSong);
				   }
				}
			}
			
		}
		String theAnswer=null;
		if (artistSongs.size()==1) theAnswer = artistSongs.get(0);
		while (theAnswer==null)
		{
			   Random rand = new Random();
			   if (artistSongs.size()==0) return null;
			   int index = rand.nextInt(artistSongs.size());
			   String newSong = artistSongs.get(index);
			   if (!newSong.equalsIgnoreCase(song))
			   {
			      theAnswer = newSong;
			   }
		}
		MultipleChoice ret = new MultipleChoice();
		ret.theAnswer = theAnswer;
		ret.wrongAnswers = (String[]) songanswers.toArray(new String[0]);
		return ret;
	}
	
	public void triviaSongQuestion(String artist, String title)
	{
		MultipleChoice mc = songQuestion(artist);
		if (mc==null)
		{
			System.out.println("Could not come up with question. "+artist);
			return;
		}
		Random rand = new Random();
		int ans = rand.nextInt(4)+1;
		
		System.out.println("Who sang the song \""+title+"\"?\n");
		int n=0;
		for (int i=1; i<5; i++)
		{
			if (i!=ans)
			{
			    System.out.println(i+". "+mc.wrongAnswers[n]);
			    n++;
			} else
			{
				System.out.println(i+". "+mc.theAnswer);
			}
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		try {
		while ((line = br.readLine()) != null) 
		{
			if ( (1==ans && ("1".equals(line)))
					|| (2==ans && ("2".equals(line)))
					|| (3==ans && ("3".equals(line)))
					|| (4==ans && ("4".equals(line))))
			{
				System.out.println("That is correct! Congratulations");
			} else
			{
				System.out.println("Sorry, the answer is "+ans+". \""+mc.theAnswer+"\"");
			}
		    break;
		}
		} catch (Exception e) {e.printStackTrace();};
	}
	
	public String retrieveShoutCast()
	{
		BufferedReader in=null;
		try {
			URL url = new URL(URL);
		    URLConnection con = url.openConnection();
		    con.setRequestProperty("User-Agent", "Mozilla/5.0");
		    in = new BufferedReader(new InputStreamReader(con.getInputStream()));

		    String inputLine;

		    while ((inputLine = in.readLine())!= null)
		    {
		    	int index=0, lastIndex=0;
		    	for (int i=0; i<6;i++)
		    	{
		            index=inputLine.indexOf(",", lastIndex);
		            lastIndex = index+1;
		            if (index==-1)
		            {
		            	return null;
		            }
		    	}
		    	lastIndex=inputLine.indexOf("</body>");
		    	String name = inputLine.substring(index+1, lastIndex);
		    	return name;
		    }
		} catch (Exception e){
		    e.printStackTrace();
		} finally
		{
			if (in!=null)
			{
			   try {
				in.close();
			   } catch (Exception e){};
			}
		}
		return null;
	}
	
	public void shoutCastWait()
	{
		while (true)
		{
			try {
			Thread.sleep(5000);
			String title = retrieveShoutCast();
			//System.out.println(title);
			if (title!=null && !title.equals(oldname))
			{
				oldname = title;
				//System.out.println("A");
				String artist = playList.get(title);
				//System.out.println("B");
				if (artist!=null)
				{
					//System.out.println("C");
					ArtistInfo info = artists.get(normalizeLesser(artist));
					if (info!=null)
					{
						songName = title;
						songArtist = info.artistName;
						return;
					} else
					{
						System.out.println(title + " artist not found.");
					}
					
				} else
				{
					System.out.println("'"+title+"' artist not found.");
				}
			}
			} catch (Exception e){e.printStackTrace();};
		}
	}
	
	private Iterator<String> lIt;
	private void playlistGet()
	{
		if (lIt == null)
		{
			lIt = playList.keySet().iterator();
		}
		while (lIt.hasNext())
		{
			String name = lIt.next();
			String artist = playList.get(name);
			if (artist!=null)
			{
				ArtistInfo info = artists.get(normalizeLesser(artist));
				if (info!=null)
				{
					songName = name;
					songArtist = info.artistName;
					return;
				} else
				{
					System.out.println("'"+songName+"' artist not found.");
				}
			}
		}
	}
	
	public void triviaShoutcast()
	{
		while (true)
		{
			shoutCastWait();
			String name = songName;
			String artist = songArtist;
			
			triviaSongQuestion(artist,name);
			triviaBonusQuestion(artist,name);
		}
		
	}
	
	public void triviaAlone()
	{
		while (true)
		{
			playlistGet();
			String name = songName;
			String artist = songArtist;
			
			triviaSongQuestion(artist,name);
			triviaBonusQuestion(artist,name);
		}
		
	}
	
	public void triviaBonusQuestion(String artist, String title)
	{
		MultipleChoice mc = bonusQuestion(artist,title);
		if (mc==null)
			{
			System.out.println("Could not come up with bonus question. "+artist);
			return;
			}
		Random rand = new Random();
		int ans = rand.nextInt(4)+1;
		
		System.out.println("Can you name another song sung by  \""+artist+"\"?\n");
		int n=0;
		for (int i=1; i<5; i++)
		{
			if (i!=ans)
			{
			    System.out.println(i+". "+mc.wrongAnswers[n]);
			    n++;
			} else
			{
				System.out.println(i+". "+mc.theAnswer);
			}
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		try {
		while ((line = br.readLine()) != null) 
		{
			if ( (1==ans && ("1".equals(line)))
					|| (2==ans && ("2".equals(line)))
					|| (3==ans && ("3".equals(line)))
					|| (4==ans && ("4".equals(line))))
			{
				System.out.println("That is correct! Congratulations");
			} else
			{
				System.out.println("Sorry, the answer is "+ans+". \""+mc.theAnswer+"\"");
			}
		    break;
		}
		} catch (Exception e) {e.printStackTrace();};
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		final TriviaContest object = new TriviaContest();
		object.loadArtists();
		object.loadPlaylist();
		
		/*Runnable r = new Runnable()
		{
	         public void run() 
	         {
	        	 object.shoutCastThread();
	         };
		};
		Thread t = new Thread(r);
		t.start();
		*/		
		//System.out.println("DONE.");
		
		//ArtistInfo a = object.artists.get("TERRY JACKS");
		//ArtistInfo b = object.artists.get("JOHN DENVER");
		//MultipleChoice x = object.4("John Denver");
		//MultipleChoice y = object.bonusQuestion("ABBA", "Dancing Queen");
		//System.out.println(x.wrongAnswers);
		//object.triviaSongQuestion("The Go-Go's","Our Lips Are Sealed");
		//object.triviaBonusQuestion("The Go-Go's","Our Lips Are Sealed");
		
		//object.triviaAlone();
		object.triviaShoutcast();
	}

}
