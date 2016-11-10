


Project:
	- download github
	- learn how to use github.
		
Github:
	- before commiting any changes, you have to write a summary of what you have done in the description box.
	
	

*******************************************************************************************************************************************************************
****************************************************			INSTRUCTION			*******************************************************************************
*******************************************************************************************************************************************************************
WAMP server setup
1.	Install WAMP Server 3.0.6.
	- current project WAMP server version is 3.0.6
	- learn and understand 
		a.	how WAMP Server works
	
2.	Setting up WAMP Server root directory (IMPORTANT)
	
	a.	Open up httpd-vhosts.conf by clicking the WAMP Server icon (should be in green color)
	b.	select Apache
	c.	Select httpd-vhosts.conf
	d. 	Edit this section of the conf (Notepad++ is highly recommended for editing and adding of new php scripts)
		https://www.youtube.com/watch?v=t-2kuO8CS7Q - extremely soft audio
	
	DocumentRoot c:/wamp64/www		<--- Look for this section, you may comment this line by "#"
	<Directory  "c:/wamp64/www/">	<--- Look for this section, you may comment this line by "#"
	
	DocumentRoot "YOUR OWN DIRECTORY PATH THAT MAPS TO estatephpfiles IN OUR DROPBOX FOLDER"	<-- change to this. 
	<Directory "YOUR OWN DIRECTORY PATH THAT MAPS TO estatephpfiles IN OUR DROPBOX FOLDER">	<-- change to this. 
	
	
	NOTE: 
	Please follow the format of the sentence. 
	Make sure there are no whitespaces in the directories.
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
		
*******************************************************************************************************************************************************************
*******************************************************************************************************************************************************************
*******************************************************************************************************************************************************************
