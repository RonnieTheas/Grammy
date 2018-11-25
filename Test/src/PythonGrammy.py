
import http.client
import urllib.request
import time
import random

WIKI = "c:\\Users\\rtheas\\Documents\\cheerwiki.txt"
PLAYLISTFILE = "c:\\Users\\rtheas\\Documents\\playlist.txt"
NEWARTISTFILE="c:\\Users\\rtheas\\Documents\\masterartistlist.txt"

oldName = ""
artists = []
playlist = {}

class Artist:
    def __init__(self, name, genre, listOfSongs=[], listOfTrivia=[]):
        self.name = name
        self.active = True
        self.genre = genre
        self.songs = listOfSongs
        self.trivia = listOfTrivia


def getArtist(line):
    if not line.startswith("ARTIST;"):
        return None

    list = line.split(";")
    if len(list)>=2:
        ret = list[1].strip()
        if (ret==""):
            return None
        return ret

    return None

def getGenre(line):
	if not line.startswith("ARTIST;"):
		return None

	list = line.split(";")
	if len(list)>=4:
		return list[3].strip()

	return None

def getActive(line):
	if not line.startswith("ARTIST;"):
		return False

	list = line.split(";")
	if len(list)>=3:
		return list[2].strip()=="**"

	return False

def getTrivia(line):
	if not line.startswith("TRIVIA;"):
		return None

	list = line.split(";")
	if len(list)>=2:
		return list[1].strip()

	return None

def getSongs(line):
	if not line.startswith("TRUE;") and not line.startswith("BOUGHT;"):
		return None

	list = line.split(";")
	if len(list)>=2:
		songlist = list[1].split("|")

		newsonglist = []
		for song in songlist:
			song = song.strip()
			if song!=None and song!="":
				newsonglist.append(song)

		return newsonglist

	return None


def normalize(string):
	return string

def getQuestion(line, oldQuestion):
	if not line.startswith("QUESTION;"):
		return oldQuestion

	list = line.split(";")
	if len(list)>=2:
		return list[1].strip()=="TRUE"

	return oldQuestion


def loadArtists():
    artistList = [];
    currentArtist = None
    file = open(WIKI,"r") 

    for line in file:
        artist = getArtist(line)
        if artist!=None:
            if currentArtist!=None:
                artistList.append(currentArtist)

            question = False
            artistName = normalize(artist)
            artistName = artistName.strip()
            active = getActive(line)
            genre = getGenre(line);
            currentArtist = Artist(artistName,genre)
            currentArtist.active = active
            currentArtist.songs = []
            currentArtist.trivia = []
            continue;

        if currentArtist==None:
            continue

        question = getQuestion(line,question)

        trivia = getTrivia(line)
        if trivia!=None:
            currentArtist.trivia.append(trivia)

        if question==False:
            continue

        songs = getSongs(line)
        if songs!=None:
            currentArtist.songs.extend(songs)

    return artistList

def genreArtists(genre):
	list = []
	for art in artists:
		if art.genre==genre:
			list.append(art.name)

	return list

def genreSongsAndArtists(genre):
    list = []
    for art in artists:
        if art.genre==genre:
            for song in art.songs:
                list.append((song,art.name))

    return list

def getArtistPL(line):
    list = line.split(" - ")
    if (len(list)==2):
        return list[1].strip()
    return None

def getSongPL(line):
    list = line.split(" - ")
    if (len(list)==2):
        return list[0].strip()
    return None

def loadPlaylist():
    playlist = {}
    currentArtist = None
    file = open(PLAYLISTFILE,"r") 

    for line in file:
        artist = getArtistPL(line)
        name = getSongPL(line)
        if artist!=None and name!=None:
            playlist.update({name : artist})
        else:
            print ("artist not found:"+line)

    return playlist

def checkPlaylist():
    global artists
    artists = loadArtists()
    playlist = {}
    currentArtist = None
    file = open(PLAYLISTFILE,"r") 

    flag = True
    for line in file:
        artist = getArtistPL(line)
        name = getSongPL(line)
        if artist!=None and name!=None:
            if (findArtist(artist)==None):
                print("Artist not found: "+artist)
                flag = False;
    if flag:
        print("No missing artists.")
    else:
        print("Unknown artists found.")

    

def cleanPlaylist(inputFile):
    inFile = open(inputFile,"r")
    outFile = open(PLAYLISTFILE,"w")
    for line in inFile:
        if (line.startswith("#EXTINF:")):
            line = line[line.index(",")+1:-1]
            list = line.split(" - ")
            if (len(list)==2):
                artist = list[0].strip()
                name = list[1].strip()
                outFile.write(name + " - "+artist+"\n")
    inFile.close()
    outFile.close()

URL = "http://s4.bb-stream.com:10319/7.html"

def retrieveShoutcast():
    values = {"User-Agent": "Mozilla/5.0"}
    f = urllib.request.Request(URL, headers=values)
    response = urllib.request.urlopen(f)
   
    inputLine = response.read().decode("utf-8") 
    index=0
    lastIndex=0
    for i in range(0,6):
        index=inputLine.index(",", lastIndex)
        lastIndex = index+1
        if (index==-1):
            return None

    lastIndex=inputLine.index("</body>");
    name = inputLine[index+1:lastIndex]
    return name

def findArtist(artist):
	for art in artists:
		if art.name ==artist:
			return art

	return None

def shoutcastWait():
    global oldName
    while True:
        title = retrieveShoutcast();

        if (title!=None and title!=oldName):
            oldName = title
            artist = playlist.get(title)
            if (artist!=None):
                info = findArtist(normalize(artist))
                if (info!=None):
                    return title, info.name
                else:
                    print(title + " artist not found.")
            else:
                print("'"+title+"' artist not found.")

        time.sleep(5)
    

def getQuestion(song, artist):
    info = findArtist(artist)
    if (info==None):
        return None
    
    question = {}
    
    list = genreArtists(info.genre)
    if (len(list)<5):
        return None

    answerNum = random.randint(1,4)
    question.update({answerNum:info.name})
    question.update({"answer":answerNum})
    for i in range(1,5):
        if i==answerNum:
            continue
        while True:
            art = list[random.randint(0,len(list)-1)]
            if not art in question.values():
                break
        question.update({i:art})

    return question

def getBonusQuestion(song, artist):
    info = findArtist(artist)
    if (info==None):
        return None
    
    if (info.songs==None or len(info.songs))==0:
        return None

    question = {}
    
    list = genreSongsAndArtists(info.genre)
    if (len(list)<5):
        return None

    artistSongs = info.songs
    songTitle=song
    while songTitle==song:
        answerNum = random.randint(0,len(artistSongs)-1)
        songTitle = artistSongs[answerNum]
    answerNum = random.randint(1,4)
    question.update({answerNum:songTitle})
    question.update({(str)(answerNum)+" artist":artist})
    for i in range(1,5):
        if i==answerNum:
            continue
        while True:
            index = random.randint(0,len(list)-1)
            art = list[index][1]
            sng = list[index][0]
            if not art in question.values() and not sng in question.values() and not sng in artistSongs:
                break
        question.update({i:sng})
        question.update({(str)(i)+" artist":art})

    return question

def triviaSongQuestion(song, artist):
    question = getQuestion(song, artist)
    if (question==None or len(question)==0):
        print("Could not come up with a question for %s",song)
        return

    print("Who sang the song '"+song+"'?")
    for i in range(1,5):
        print ((str)(i)+". "+question[i])

    answer = (int) (input("Your answer?"))
    if answer==question["answer"]:
        print("That is correct!")
    else:
        a = question["answer"]
        print("Sorry, the answer is "+question[a])

    print("\n")

def triviaSongBonus(song, artist):
    question = getBonusQuestion(song, artist)
    if (question==None or len(question)==0):
        print("Could not come up with a bonus question for ",song)
        return

    print("Name another song that '"+artist+"' sings:")
    for i in range(1,5):
        print ((str)(i)+". "+question[i])

    answer = (int) (input("Your answer?"))
    if artist==question[str(answer)+" artist"]:
        print("That is correct!")
    else:
        a = 4
        for j in range(1,4):
            if artist==question[str(j)+" artist"]:
                a = j
                break
        
        print("Sorry, the answer is "+question[a])

    for i in range(1,5):
        print("'"+question[i]+"' is sung by '"+question[str(i)+ " artist"]+"'")
    print("\n")


def triviaShoutcast():
    global artists, playlist
    artists = loadArtists()
    playlist = loadPlaylist()

    while(True):
        name, artist = shoutcastWait()
        triviaSongQuestion(name, artist) 
        triviaSongBonus(name,artist)

#------------------------------------------------------------------------------------------------------------------------------------------

def printArtist(artist):
	print(artist.name)
	print(artist.genre)
	for s in artist.songs:
		print(s)

	for t in artist.trivia:
		print(t)


def printArtists():
	for a in artists:
		printArtist(a)


def songsForArtist(theArtist):
    for art in artists:
        if art.name==theArtist:
            return art.songs
    return None

def ignoreArtist(theArtist):
    if theArtist.genre=="METAL":
        return True
    if not theArtist.active:
        return True
    return False

def randomArtists():
    artists = loadArtists()
    max = len(artists)
    alist = []
    for i in range(1,16):
        index = random.randint(0,max-1)
        artist = artists[index]
        while ignoreArtist(artist) or artist.name in alist:
            index = random.randint(0,max-1)
            artist = artists[index]
  
        alist.append(artist.name)
        mm = len(artist.songs)
        iin = random.randint(0,mm-1)
        print(str(i)+". "+artist.name+" - "+artist.songs[iin])  

def checkNewArtists():
    global artists
    artists = loadArtists()
    newartistlist = {}
    currentArtist = None
    file = open(NEWARTISTFILE,"r") 

    flag = True
    for line in file:
        artist = getSongPL(line) #get part before " - "
        if artist!=None:
            if (findArtist(artist)==None):
                print("New Artist found: "+artist)
                flag = False;
    if flag:
        print("No new artists.")
    else:
        print("New artists found.")

    
#randomArtists()

#triviaShoutcast()

#cleanPlaylist("c:\\Users\\rtheas\\Documents\\ronnie76.m3u")
#print("Playlist created.")
#checkPlaylist()
checkNewArtists()

#artists = loadArtists()
#for art in artists:
#    if (not art.active):
#        print('"'+art.name+'"')
