--------------------------------------------------------------------------------------------------
--		----	  |			  |			|		 |				|    --    |      ----			--
--		----	  |			  |			|		 |				|    --	   |      ----			--
--		----	  |		-------	   -----|	 ---------		-----          -      ----	   -------
--		----	  |			---			|		 -----		------        --      ----			--
--		----	  |			---			|		 -----		-------	 	 ---      ----			--
--		----	  |		-------	   ----------	 -----		-------		 ---      ----	   -------
--			|	  |		-------			|		 -----		-------		 ---		  |			--
--			|	  |		-------			|	 	 -----		-------		 ---		  |			--
--------------------------------------------------------------------------------------------------

--------------------------------------------------------------------------------------------------
--ONLY UNCOMMENT (deleting the "--" before a line) FROM THE INSTRUMENT YOU WANT TO ADD SONGS TO---
--------------------------------------------------------------------------------------------------
----INSTRUCTIONS----------------------------------------------------------------------------------
------------"local InstrumentTracks = require" is what we use to load the music table - do not rename anything here
------------"table.insert" is what we use to insert your music - change the values, but do not delete any symbols;
------------Find the instrument you want to add songs to and remove the "--" from the section;
------------The first table.insert should be replaced by your first music;
------------To add more songs simply copy the line you just edited, paste it one line below it and change it (don't forget the ; at the end)
------------Remember to save your changes----------------------------------------------------------
---------------------------------------------------------------------------------------------------
----VALUES-----------------------------------------------------------------------------------------
------------level is level required to play the song, value goes from 0 to 10
------------sound is the name of your sound file (without the .ogg), must be inside " "
------------length is how many real seconds the song takes to play from start to end
------------name is the name you want to appear when selecting the song in-game (here it can have spaces between words)
---------------------------------------------------------------------------------------------------



----------------------TRUMPET SOUNDTRACKS---------------------------------
---------DELETE THE "--" FROM EVERYTHING UNDER THIS LINE IF YOU WANT TO ADD TRACKS FOR THE TRUMPET--------

--local TrumpetTracks = require "TimedActions/PlayTrumpetTracks"
		
--table.insert(TrumpetTracks, {level=0, sound="replaceme_mymusic", length=3, name="Replace Me By MyMusic"});





---------------------------------------------------------------------------------------------------------------------------
----------------------ACOUSTIC GUITAR SOUNDTRACKS---------------------------------
---------DELETE THE "--" FROM EVERYTHING UNDER THIS LINE IF YOU WANT TO ADD TRACKS FOR THE ACOUSTIC GUITAR--------

--local GuitarAcousticTracks = require "TimedActions/PlayGuitarAcousticTracks"
		
--table.insert(GuitarAcousticTracks, {level=0, sound="replaceme_mymusic", length=3, name="Replace Me By MyMusic"});





---------------------------------------------------------------------------------------------------------------------------
----------------------BANJO SOUNDTRACKS---------------------------------
---------DELETE THE "--" FROM EVERYTHING UNDER THIS LINE IF YOU WANT TO ADD TRACKS FOR THE BANJO--------

--local BanjoTracks = require "TimedActions/PlayBanjoTracks"
		
--table.insert(BanjoTracks, {level=0, sound="replaceme_mymusic", length=3, name="Replace Me By MyMusic"});



---------------------------------------------------------------------------------------------------------------------------
----------------------FLUTE SOUNDTRACKS---------------------------------
---------DELETE THE "--" FROM EVERYTHING UNDER THIS LINE IF YOU WANT TO ADD TRACKS FOR THE FLUTE--------

--local FluteTracks = require "TimedActions/PlayFluteTracks"
		
--table.insert(FluteTracks, {level=0, sound="replaceme_mymusic", length=3, name="Replace Me By MyMusic"});



---------------------------------------------------------------------------------------------------------------------------
----------------------ELECTRIC GUITAR BASS SOUNDTRACKS---------------------------------
---------DELETE THE "--" FROM EVERYTHING UNDER THIS LINE IF YOU WANT TO ADD TRACKS FOR THE BASS--------

--local GuitarElectricBassTracks = require "TimedActions/PlayGuitarElectricBassTracks"
		
--table.insert(GuitarElectricBassTracks, {level=0, sound="replaceme_mymusic", length=3, name="Replace Me By MyMusic"});



---------------------------------------------------------------------------------------------------------------------------
----------------------ELECTRIC GUITAR SOUNDTRACKS---------------------------------
---------DELETE THE "--" FROM EVERYTHING UNDER THIS LINE IF YOU WANT TO ADD TRACKS FOR THE GUITAR--------

--local GuitarElectricTracks = require "TimedActions/PlayGuitarElectricTracks"
		
--table.insert(GuitarElectricTracks, {level=0, sound="replaceme_mymusic", length=3, name="Replace Me By MyMusic"});



---------------------------------------------------------------------------------------------------------------------------
----------------------KEYTAR SOUNDTRACKS---------------------------------
---------DELETE THE "--" FROM EVERYTHING UNDER THIS LINE IF YOU WANT TO ADD TRACKS FOR THE KEYTAR--------

--local KeytarTracks = require "TimedActions/PlayKeytarTracks"
		
--table.insert(KeytarTracks, {level=0, sound="replaceme_mymusic", length=3, name="Replace Me By MyMusic"});




---------------------------------------------------------------------------------------------------------------------------
----------------------SAXOPHONE SOUNDTRACKS---------------------------------
---------DELETE THE "--" FROM EVERYTHING UNDER THIS LINE IF YOU WANT TO ADD TRACKS FOR THE SAXOPHONE--------

--local SaxophoneTracks = require "TimedActions/PlaySaxophoneTracks"
		
--table.insert(SaxophoneTracks, {level=0, sound="replaceme_mymusic", length=3, name="Replace Me By MyMusic"});




---------------------------------------------------------------------------------------------------------------------------
----------------------HARMONICA SOUNDTRACKS---------------------------------
----------------------!!REQUIRES THE HARMONICA ADDON!!----------------------
---------DELETE THE "--" FROM EVERYTHING UNDER THIS LINE IF YOU WANT TO ADD TRACKS FOR THE SAXOPHONE--------

--local HarmonicaTracks = require "TimedActions/PlayHarmonicaTracks"
		
--table.insert(HarmonicaTracks, {level=0, sound="replaceme_mymusic", length=3, name="Replace Me By MyMusic"});