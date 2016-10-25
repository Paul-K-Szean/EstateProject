


Project:
	- download github
	- learn how to use github.
	- inform inside the chat group on the things you going to implement just in case other people are doing the same function.
	
Coding rules:
	- please follow the style of the original codes (IMPORTANT)
	
Github Desktop:
	- before commiting any changes, you have to write a summary of what you have done in the description box.

*******************************************************************************************************************************************************************
****************************************************			INSTRUCTION			*******************************************************************************
*******************************************************************************************************************************************************************
WAMP server setup
1.	Install WAMP Server 3 from their website.
	- current project WAMP server version is 3.0.6
	- learn and understand 
		a.	how WAMP Server works
		b.	our project structure
	
2.	Setting up WAMP Server root directory (IMPORTANT) !all our server side files are stored here!
	a.	Open up httpd-vhosts.conf by clicking the WAMP Server icon (should be in green color)
	b.	select Apache
	c.	Select httpd-vhosts.conf
	d. 	Edit this section of the conf 
		(Notepad++ is highly recommended for editing and adding of new php scripts)
	https://www.youtube.com/watch?v=t-2kuO8CS7Q - extremely soft audio
	
	DocumentRoot c:/wamp64/www		<--- Look for this section, you may comment this line by "#"
	<Directory  "c:/wamp64/www/">	<--- Look for this section, you may comment this line by "#"
	
	DocumentRoot "YOUR OWN DIRECTORY PATH THAT MAPS TO estatephpfiles IN OUR DROPBOX FOLDER"	<-- change to this. 
	<Directory "YOUR OWN DIRECTORY PATH THAT MAPS TO estatephpfiles IN OUR DROPBOX FOLDER">	<-- change to this. 
	
	NOTE: Please follow the format of the sentence. Also, make sure there are no whitespaces in the directories.
		EG: 
		DocumentRoot c:/wamp64/www		to
		DocumentRoot c:/DROPBOX/CZ2006Project-NoName/estatephpfiles
		<Directory  "c:/wamp64/www/">	to
		<Directory  "c:/DROPBOX/CZ2006Project-NoName/estatephpfiles/">

3.	Save the file, restart WAMP Server by clicking the WAMP Server icon and restart all services.
	- ensure icon is in green color.
	- test the connection by typing: localhost/estatelogin
	
4.	Create a database name "estate"
	a.	type: localhost/phpmyadmin in your internet browser
	b.	username root and click go 
	c.	click new on the left panel
	d.	enter "estate" for database name and create
	e.	select "estate"
	f.	click import on the top bar 
	h.	browse for estate.sql
		- ignore the error if there is data inside the "estate" db you created.
		
**************************************************************************************************************************************************************************************************************************************************************************************************************************
Android Studios setup
1. Install Android Studios.
2. Go to SDK manager and install Android 4.4 API level 19,	Android SDK build tools, cmake, sdk platform tools, sdk tools, and NDK.
3. Go to ADK manager, choose any phone of your choice, and look for a API level 19 version. DO NOT CHOOSE level 24/25. That is for Android 7.0 and above. You can't use it unless you have the latest Google Pixel phone, which if you do, please let me try it.
4. Start a new Android project. 
Project name: Estate
Company domain: estateco	
Note: make sure project name and company domain are as above.
5. Click next, next, select no activity, and click finish.
6. Now, go to your project directory.
7. git clone the repo on github, and move the contents of the estate folder into your project directory. select replace for any same filenames.
8. Close your android studio, and reopen it. 
9. Rebuild project, debug, and select the emulator you created. or load the apk into your android phone. 