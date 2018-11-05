import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.concurrent.ThreadLocalRandom;

public class ArtistCheck {

	static final String FNAME = "c:\\Users\\rtheas\\Documents\\cheerwiki.txt";
	static final String PLAYLIST = "c:\\Users\\rtheas\\Documents\\ronnie73.m3u";
	static final String PLAYLIST_OUT = "c:\\Users\\rtheas\\Documents\\playlist.txt";

	List<String> original = new LinkedList<String>();
	List<String> normalized = new LinkedList<String>();

	public String normalize(String aInS) 
	{
		return normalizeLesser(aInS);
	}
	
	// Trim and remove "The " and capitalize and remove all non letter/number characters
	public String normalizeGreater(String aInS) {
		String u = aInS.toUpperCase();
		u = u.trim();
		if (u.startsWith("THE ")) {
			u = u.substring(4);
		}

		StringBuilder s = new StringBuilder();
		for (char c : u.toCharArray()) {
			if ((c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
				s.append(c);
			}
		}
		String ret = s.toString();
		/*if (ret.endsWith("ES")) {
			ret = ret.substring(0, ret.length() - 2);
		} else if (ret.endsWith("E")) {
			ret = ret.substring(0, ret.length() - 1);
		} else if (ret.endsWith("S")) {
			ret = ret.substring(0, ret.length() - 1);
		}*/
		return ret;
	}

	// Trim and remove "The " and capitalize
	public String normalizeLesser(String aInS) {
		String u = aInS.toUpperCase();
		u = u.trim();
		if (u.startsWith("THE ")) {
			u = u.substring(4);
		}

		return u;
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

	static String getGenre(String line) {
		if (!line.startsWith("ARTIST;")) {
			return null;
		}

		String p[] = line.split(";");
		if (p.length > 4) {
			String genre = p[3].trim();
			if (!genreCheck(genre))
			{
				System.out.println("WARNING--UNKNOWN GENRE: "+genre);
			}
			return genre;
		}
		return null;
	}

	public static boolean genreCheck(String genre)
	{
		String[] genres = {"BAND", "BAND_BRIT", "DUO_MF", "DUO_M", "FV", "F_BAND","FV_CTRY","MV",
	            "MV_BRIT","MV_CTRY","METAL","MUSICAL"};
		
		for (String s:genres)
		{
			if (s.equals(genre))
			{
				return true;
			}
		}
		return false;
	}
	
	public void checkForDuplicates() {
		BufferedReader br = null;
		int numA = 0;
		try {
			br = new BufferedReader(new FileReader(FNAME));
			String line = br.readLine();
			int lineNum = 1;
			while (line != null) {
				String artist = getArtist(line);
				if (artist!=null) artist = artist.trim();
				if (artist != null && artist.length() > 0) {
					//System.out.println("Found artist:" + artist); 
					numA++;
					original.add(artist);
				}

				line = br.readLine();
				lineNum++;
			}

			for (String s : original) {
				String mod = normalize(s);
				if (normalized.contains(mod)) {
					System.out.println("Duplicate found: " + s + " , " + original.get(normalized.indexOf(mod)));
				}
				normalized.add(mod);
			}
			System.out.println("Number of artists: "+numA);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (Exception ex) {
				}
		}
	}
	
	public void checkForSemicolon() {
		BufferedReader br = null;
		String thisArtist = "";
		try {
			br = new BufferedReader(new FileReader(FNAME));
			String line = br.readLine();
			while (line != null) {
				String artist = getArtist(line);
				if (artist!=null) artist = artist.trim();
				if (artist != null && artist.length() > 0) {
					//System.out.println("Found artist:" + artist); 
					thisArtist = artist;
				}

				line.trim();
				if ("QUESTION; TRUE".equalsIgnoreCase(line))
				{
					System.out.println("Found missing semicolon for artist: "+thisArtist);
				}
				if ("QUESTION;TRUE".equalsIgnoreCase(line))
				{
					System.out.println("Found missing semicolon for artist: "+thisArtist);
				}
				if ("QUESTION TRUE".equalsIgnoreCase(line))
				{
					System.out.println("Found missing semicolon for artist: "+thisArtist);
				}
				if ("QUESTION TRUE;".equalsIgnoreCase(line))
				{
					System.out.println("Found missing semicolon for artist: "+thisArtist);
				}
				line = br.readLine();
			}


		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (Exception ex) {
				}
		}
	}

	public String getArtistPL(String line)
	{
		int index = line.indexOf(" - ");
		if (index==-1) return null;
		return line.substring(index+3);
	}
	
	public void playlistCheck(String aInPlaylist)
	{
		BufferedReader br = null, br2 = null;
		try {
			br = new BufferedReader(new FileReader(FNAME));
			String line = br.readLine();
			while (line != null) {
				String artist = getArtist(line);
				if (artist!=null) artist = artist.trim();
				if (artist != null && artist.length() > 0) {
					//System.out.println("Found artist:" + artist); 
					original.add(artist);
				}

				line = br.readLine();
			}
			
			br2 = new BufferedReader(new FileReader(aInPlaylist));
			line = br2.readLine();
			while (line != null) {
				String artist = getArtistPL(line);
				if (artist!=null) artist = artist.trim();
				if (artist==null)
				{
					System.out.println("No artist parsed: "+line);
				}
				
				if (!original.contains(artist))
				{
  			         System.out.println("Artist not found: "+artist+"   line:"+line);
				}

				line = br2.readLine();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (Exception ex) {
				}
			
			if (br2 != null)
				try {
					br2.close();
				} catch (Exception ex) {
				}
		}
	}
	
	void checkLine(String aInLine)
	{
		for (int i=0;i<aInLine.length(); i++)
		{
			char c = aInLine.charAt(i);
			if ((c<32) || (c>'z'))
			{
				System.out.println("Suspect character found: "+aInLine + " '"+c+"'");
				return;
			}
		}
	}
	
	public void playlistCheckChars(String aInPlaylist)
	{
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(aInPlaylist));
			String line = br.readLine();
			while (line != null) {
				checkLine(line);
				line = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (Exception ex) {
				}
		}
	}
	
	public void playlistCheckNormalized(String aInPlaylist)
	{
		BufferedReader br = null, br2 = null;
		try {
			br = new BufferedReader(new FileReader(FNAME));
			String line = br.readLine();
			while (line != null) {
				String artist = getArtist(line);
				if (artist!=null) artist = artist.trim();
				if (artist != null && artist.length() > 0) {
					//System.out.println("Found artist:" + artist); 
					original.add(artist);
					normalized.add(normalize(artist));
				}

				line = br.readLine();
			}
			
			br2 = new BufferedReader(new FileReader(aInPlaylist));
			line = br2.readLine();
			while (line != null) {
				String artist = getArtistPL(line);
				if (artist!=null) artist = artist.trim();
				if (artist==null)
				{
					System.out.println("No artist parsed: "+line);
				}
				
				if (!normalized.contains(normalize(artist)))
				{
					System.out.println("Artist not found: "+artist+"   line:"+line);
				}

				line = br2.readLine();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (Exception ex) {
				}
			
			if (br2 != null)
				try {
					br2.close();
				} catch (Exception ex) {
				}
		}
	}
	
	public void genreList(String genre) {
		original.clear();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(FNAME));
			String line = br.readLine();
			int lineNum = 1;
			while (line != null) {
				String artist = getArtist(line);
				String g = getGenre(line);
				if (artist != null && artist.length() > 0 && (genre==null ||  g != null && g.equals(genre))) {

					original.add(artist.trim());
				}

				line = br.readLine();
				lineNum++;
			}

			for (String s : original) {
				System.out.println(s);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (Exception ex) {
				}
		}

	}
	
	public void randomArtistList(int num) {
		original.clear();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(FNAME));
			String line = br.readLine();
			int lineNum = 1;
			while (line != null) {
				String artist = getArtist(line);
				String g = getGenre(line);
				if (artist != null && artist.length() > 0 && (g != null && !g.equals("METAL"))) {

					original.add(artist.trim());
				}

				line = br.readLine();
				lineNum++;
			}

			for (int i=0; i<num; i++)
			{
			   int t = original.size();
			   int index = ThreadLocalRandom.current().nextInt(0, t);
			   System.out.println(original.get(index));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (Exception ex) {
				}
		}

	}
	
	public void genreList(String genre, String outFile) {
		BufferedReader br = null;
		BufferedWriter writer = null;
		original.clear();
		try {
			br = new BufferedReader(new FileReader(FNAME));
			String line = br.readLine();
			int lineNum = 1;
			while (line != null) {
				String artist = getArtist(line);
				String g = getGenre(line);
				if (artist != null && artist.length() > 0 && (genre==null ||  g != null && g.equals(genre))) {

					original.add(artist.trim());
				}

				line = br.readLine();
				lineNum++;
			}

			File playlist=new File(outFile);

			writer = new BufferedWriter(new FileWriter(playlist));
		    for (String s: original)
		    {
		       writer.write(s);
		       writer.newLine();
		       if (s.length()==0)
				{
					System.out.println("WARNING:  BLANK Artist!");
				}
		    }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (Exception ex) {
				}
			if (writer != null)
				try {
					writer.close();
				} catch (Exception ex) {
				}
		}

	}
	
	//artist;title
	public void songGenreList(String genre) {
		BufferedReader br = null;
		boolean question = false;
		try {
			
			br = new BufferedReader(new FileReader(FNAME));
			String line = br.readLine();
			String artist = null;
			String thisgenre = null;
			while (line != null) {
				String thisartist = getArtist(line);
				if (thisartist!=null)
				{
					question = false;
					thisgenre = getGenre(line);
					artist = thisartist.trim();
				} else
				{
					question = getQuestion(line,question);
				}
				if ((genre==null || genre.equals(thisgenre)) && (question))
				{
					String[] songs = getSongs(line);
					if (songs!=null)
					{
						for (int i=0; i<songs.length;i++)
						{
							System.out.println(artist+";"+songs[i]);
							if (songs[i].length()==0)
							{
								System.out.println("WARNING:  BLANK SONG!  Artist: "+artist);
							}
						}
					}
				}

				line = br.readLine();
			}

			for (String s : original) {
				System.out.println(s);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (Exception ex) {
				}
		}

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
	
	public void songGenreList(String genre, String outfile) {
		boolean question = false;
		BufferedReader br = null;
		BufferedWriter writer = null;
		try {
			br = new BufferedReader(new FileReader(FNAME));
			File playlist=new File(outfile);
            writer = new BufferedWriter(new FileWriter(playlist));
			
			String line = br.readLine();
			int lineNum = 1;
			String artist = null;
			String thisgenre = null;
			while (line != null) {
				String thisartist = getArtist(line);
				if (thisartist!=null)
				{
					question = false;
					thisgenre = getGenre(line);
					artist = thisartist.trim();
				} else
				{
					question = getQuestion(line,question);
				}
				if ((genre==null || genre.equals(thisgenre))&& (question))
				{
					String[] songs = getSongs(line);
					if (songs!=null)
					{
						for (int i=0; i<songs.length;i++)
						{
							if (songs[i].length()>0)
							{
								writer.write(artist+";"+songs[i]);
								writer.newLine();
							}
							else
							{
								System.out.println("WARNING:  BLANK SONG! Artist: "+artist);
							}
						}
					}
				}

				line = br.readLine();
				lineNum++;
			}
     	} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (Exception ex) {
				}
			if (writer != null)
				try {
					writer.close();
				} catch (Exception ex) {
				}
		}

	}

	//artist;title
		public void dupsongGenreList(String genre) {
			BufferedReader br = null;
			original.clear();
			try {
				br = new BufferedReader(new FileReader(FNAME));
				String line = br.readLine();
				int lineNum = 1;
				String artist = null;
				String thisgenre = null;
				while (line != null) {
					String thisartist = getArtist(line);
					if (thisartist!=null)
					{
						thisgenre = getGenre(line);
						artist = thisartist.trim();
					} 
					if ((genre==null || genre.equals(thisgenre)))
					{
						String[] songs = getSongs(line);
						if (songs!=null)
						{
							for (int i=0; i<songs.length;i++)
							{
								if (original.contains(songs[i]))
								{
									System.out.println("Duplicate found: "+songs[i]);
								}
								//System.out.println(artist+";"+songs[i]);
							}
							original.addAll(Arrays.asList(songs));
						}
					}

					line = br.readLine();
					lineNum++;
				}

				System.out.println("Total number of songs: "+original.size());

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (br != null)
					try {
						br.close();
					} catch (Exception ex) {
					}
			}

		}
		
	public void numSongs() {
		int n = 0;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(FNAME));
			String line = br.readLine();
			int lineNum = 1;
			String artist = null;
			while (line != null) {
				String thisartist = getArtist(line);
				if (thisartist!=null)
				{
					artist = thisartist.trim();
				}
				{
					String[] songs = getSongs(line);
					if (songs!=null)
					{
						//for (int i=0; i<songs.length;i++)
						//{
							//System.out.println(artist+";"+songs[i]);
						//}
						n+=songs.length;
					}
				}

				line = br.readLine();
				lineNum++;
			}

			System.out.println("Number of songs: "+n);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (Exception ex) {
				}
		}

	}
	
	public void cleanPlaylist()
	{
		List<String> songs = new ArrayList<String>(20);
		BufferedReader br = null;
		BufferedWriter writer = null;
		int numA = 0;
		try {
			br = new BufferedReader(new FileReader(PLAYLIST));
			String line = br.readLine();
			while (line != null) 
			{
				if (line.startsWith("#EXTINF:"))
				{
					int index = line.indexOf(",");
					if (index!=-1)
					{
						String q = line.substring(index+1);
						String[] r = q.split(" - ");
						if (r.length>=2)
						{
							songs.add(r[1].trim()+" - "+r[0].trim());
						}
					}
				}
			
				line = br.readLine();
				
			}
			
			if (songs.size()>0)
			{
				File playlist=new File(PLAYLIST_OUT);

			    writer = new BufferedWriter(new FileWriter(playlist));
			    for (String s: songs)
			    {
			       writer.write(s);
			       writer.newLine();
			    }
            }
			System.out.println("Created Playlist.");
			
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			if (br != null)
				try {
					br.close();
				} catch (Exception ex) {
				}
			
			if (writer != null)
				try {
					writer.close();
				} catch (Exception ex) {
				}
		}
	}
	
	public static void main(String[] args) {
		ArtistCheck ac = new ArtistCheck();
		//ac.checkForDuplicates();
		//ac.checkForSemicolon();
		//ac.genreList("BAND_BRIT");
		//ac.songGenreList("FV");
		//ac.numSongs();
		//ac.dupsongGenreList(null);
	
		//ac.randomArtistList(10);
		
		//ac.playlistCheck("C:\\Users\\rtheas\\Documents\\playlist.txt");
		ac.playlistCheckChars("C:\\Users\\rtheas\\Documents\\playlist.txt");
		//ac.cleanPlaylist();
	
		//ac.playlistCheckNormalized("C:\\Users\\rtheas\\Documents\\playlist.txt");
		
		// BAND    | BAND_BRIT = Band British | DUO_MF = Duo Male & Female | DUO_M    = Duo Male
		// FV = Female Vocalist  | F_BAND = Female Band  | FV_CTRY = Female Vocalis Country
		
		// MV = Male Vocalist| MV_BRIT = Male Vocalist British  | MV_CTRY = Male Vocalist Country
		// METAL 
		
		/*String[] genres = {"BAND", "BAND_BRIT", "DUO_MF", "DUO_M", "FV", "F_BAND","FV_CTRY","MV",
				            "MV_BRIT","MV_CTRY","METAL"};
		
		final String pre = "c:\\Users\\rtheas\\Documents\\";
		for (String g: genres)
		{
			String fnameG = pre+"GENRE "+g;
			String fnameS = pre+"FALSE_HITS_"+g;
			
			ac.genreList(g,fnameG);
			ac.songGenreList(g, fnameS);
		}*/
		
	}

}
